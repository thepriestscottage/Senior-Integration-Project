import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import javax.imageio.ImageIO;

public class SIPData {
	
	//<SIPData> is a simple code compiled to collect occurrences of
	//frequencies in Hz over time from selected musical pieces.
	//Data is collected reading a spectrogram of the selection, taking
	//in an image file (ie, .jpg, .png, etc) and reading pixel instances
	//from graph.  Frequencies are rounded and converted to ratios over
	//entered tonal-center, to simplify data and look for scalar sequences
	//and patterns.  Code can be applied to determine most common intervals,
	//scales, ranges, and pitches used.
	
	//Written for "The Creator's Harmony": How Native American Music and Philosophy 
	//	Point to the Pentatonic Scale, a Universally Pleasing Tonal Set
	//By Prescott Davis, in partial fulfillment of B.A. in Mathematics
	
	static double freq;
	static double conv;
	
	//<class Node> is a linked list that will hold the data from music 
	//selection. The frequency in Hz is held in <double data>, and the 
	//number of occurrences is held in <int count>.  Order within <Node> 
	//will be determined by <count>, so <next> will point to Hz with 
	//next most occurrences.
	private static class Node {
		double data; 
		int count=0;
		Node next;
		Node(double num, int c, Node nextNode){ //Create
			data = num;
			count = c;
			next = nextNode;
		}
	}
	
	static Node head;
	static Node current;
	static Node headR;
	static Node currentR;
	static Node headRR;
	static Node currentRR;
	//<scaleR> contains, in ascending order the simple frequency ratios
	//in a twelve-tone scale.  This is used to simplify my data-set.
	static double[] scaleR = new double[] {
		1.000,
		1.067,
		1.125,
		1.200,
		1.250,
		1.333,
		1.400,
		1.500,
		1.600,
		1.667,
		1.800,
		1.875,
		2.000};
	
	public static void run(int[] p){
		getMode(p);
		printMode();
	}
	
	//We will collected our data, all in frequencies, sort them from 
	//lowest to highest pitch, and add them to our linked-list with <add>.
	public static void getMode(int[] p){
		Arrays.sort(p);
		int pitch=0;
		for(int i=0; i<p.length; i++){
			pitch=p[i];
			add(pitch);
		}
	}
	
	public static void addRR(double num){
		if(headRR==null){
			headRR = new Node(num, 1, null);
			currentRR=headRR;
		}
		else if (currentRR==null) { //Check if list is empty
			currentRR = new Node(num, 1, null);
		}
		else{
			if(currentRR.data==num){
				currentRR.count++;
			}
			else{
				Node newCurr = new Node(num, 1, null);
				currentRR.next=newCurr;
				currentRR=newCurr;
			}
		}
	}
	
	public static void addR(double r, int c){
		if(headR==null){
			headR = new Node(r, c, null);
			currentR=headR;
		}
		else if(currentR==null){
			currentR=new Node(r,c,null);
		}
		else{
			if(c>=currentR.count){
				Node newCurr = new Node(r,c,null);
				currentR.next=newCurr;
				currentR=newCurr;
				//addR(r,c);
			}
			else {
				Node search = headR;
				while(search!=null){
					if((c>search.count) && (c<=search.next.count)){
						Node place = new Node(r,c,search.next);
						search.next=place;
						break;
					}
					search=search.next;
				}
			}
		}
	}
	
	public static void printR(){
		Node current=headR;
		while(current!=null){
			if(current.data==0){}
			else {
				System.out.println(current.data+","+current.count);
			}
			current=current.next;
		}
	}
	

	public static void printRR() throws IOException{
		Node current=headRR;
		while(current!=null){
			if(current.data==0){}
			else {
				System.out.println(current.data+", "+current.count);
			}
			current=current.next;
		}
	}
	
	//<void add> will add frequencies in Hz (double) to our linked list.
	//Repeated frequencies will not be re-added, but increase <count>
	//of previously created <node>.  List will automatically sort nodes
	//by count size.
	public static void add(double num){
		if(head==null){
			head = new Node(num, 1, null);
			current=head;
		}
		else if (current==null) { //Check if list is empty
			current = new Node(num, 1, null);
		}
		else{
			double pitchErrUp = current.data;
			if((num <= pitchErrUp)){
				current.count++;
			}
			else{
				Node newCurrent = new Node(num,1,null);
				current.next=newCurrent;
				current=current.next;
			}
		}
	}
	
	//Print our data, and round to simplify output results.
	public static void printMode(){
		Node current=head;
		while(current!=null){
			double Hz = current.data*conv;
			if(current.data==0){}
			else {
				System.out.println("Pitch "+Hz+" appears "+current.count+" times");
				double ratio = Hz/(freq/2);
				double r = ratio;
				while(r>2){
					r=r/2.0;
				}
				double newR=r;
				r=Math.round(r*100.0)/100.0;
				double check=1000;
				for(int i=0; i<13; i++){
					double d = Math.abs(r-scaleR[i]);
					if(d < check){
						newR=scaleR[i];
						check = d;
					}
				}
				r=newR;
				addR(r,current.count);
			}
			current=current.next;
		}
		printR();
	}
	
    /**
     * @param args the command line arguments
     * @throws IOException  
     */
    public static void main(String args[]) throws IOException {
        try {
        	
        	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    		System.out.println("Enter file name.");
    		String fileName = reader.readLine();
    		
    		System.out.println("Enter tonal center frequency.");
    		freq = Double.parseDouble(reader.readLine());
    		//Approximate tonal-centers for current selections below:
    		//	DancingPeople ~ 540
    		//	RedStars ~ 468
    		//	Reubens ~ 213
    		//	Poetry ~ 420
    		//	GoodTimes ~ 293
    		//	EagleTails ~ 293
    		//	SpoMoKinNan ~ 483
    		//	IndianPeople ~ 560
    		//	TheLady ~ 500
    		//	WorldOfRainbows ~ 390
    		//	WakingSong ~ 363
    		//	TheSacredReed ~ 363
    		//	SpiralPassage ~ 363
    		//	KokopelliWind ~ 400
    		//	SongForTheMorningStar ~ 363
    		
    		System.out.println("Enter conversion factor from pixels to frequency.");
    		conv = Double.parseDouble(reader.readLine());
    		//Approximate conversion factors for current selections below:
    		//	DancingPeople ~ 5.4
    		//	RedStars ~ 3.77
    		//	Reubens ~ 3.76
    		//	Poetry ~ 9.77
    		//	GoodTimes ~ 2.2
    		//	EagleTails
    		//	SpoMoKinNan ~ 1.67
    		//	IndianPeople ~ 4.3
    		//	TheLady ~ 2.22
    		//	WorldOfRainbows ~ 2.4
    		//	WakingSong ~ 5.67
    		//	TheSacredReed ~ 4.0
    		//	SpiralPassage ~ 4.37
    		//	KokopelliWind ~ 5.33
    		//	SongForTheMorningStar ~ 5.58
    		
            File file1 = new File(fileName);
            BufferedImage image1 = ImageIO.read(file1);

            //WRITE FILE
            //FileWriter fstream = new FileWriter(output);
            //out = new BufferedWriter(fstream);
            
            //out.write("DATA FROM: "+fileName);
            //out.newLine();
            
            int[] pArray=new int[image1.getWidth()];
            
            int total=0;
            int count=0;
            for (int x = 0; x < image1.getWidth(); x++) {
                for (int y = 0; y < image1.getHeight(); y++) {

                  int c = image1.getRGB(x,y);
                  Color color = new Color(c);

                   if (color.getRed()==255 && color.getGreen()==255 && color.getBlue()==255) {
                       	int y1=image1.getHeight()-y;
                        if(y==image1.getHeight()){}
                        else x++;
                        total=total+y1;
                        count++;
                        pArray[x]=y1;
                    }
                }
            }
            double[] rArray = new double[pArray.length];
            for(int i=0; i<rArray.length; i++){
            	double Hz=pArray[i]*conv;
            	double ratio = Hz/(freq/2);
				//Round ratio to simple twelve-tone ratio
				double r = ratio;
				while(r>2){
					r=r/2.0;
				}
				while(r<1 && r>0){
					r=r*2.0;
				}
				if(r==0){}
				else{
					double newR=r;
					r=Math.round(r*1000.0)/1000.0;
					double check=1000;
					for(int j=0; j<13; j++){
						double d = Math.abs(r-scaleR[j]);
						if(d < check){
							newR=scaleR[j];
							check = d;
						}
					}
					r=newR;
	            	rArray[i]=r;
				}
            }
            Arrays.sort(rArray);
            for(int i=0; i<rArray.length; i++){
            	if(rArray[i]==0.0){ 
            	}
            	else{
            		addRR(rArray[i]);
            	}
            }
            printRR();
            int avg=total/count;
            System.out.println("Avg: "+avg*conv);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}