package rectpacking;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * The rectangular two-dimensional bin packing problem.
 * @author Ahmed Hassan (ahmedhasssan@aims.ac.za)
 */
public class RectPacking {
    
    /**
     * The packing heuristics that determines the placement of the items inside
     * the bins.
     */
    public static enum PackingHeuristic {
        BestAreaFit, 
        TouchingPerimeter,
        TopRightCornerDistance
    }
    
    public static boolean canRotate = false;
    private final Random rng;
    /** Instance unique identifier */        
    private int instanceID;
    /** A list of instances. In each class (file), there are 50 instances*/
    List<Instance> instanceList;
    
    
    /**
     * Create an instance of the rectangular two-dimensional problem using a seed
     * for the random number generator.
     * @param seed 
     */
    public RectPacking(long seed){
        rng = new Random(seed);
        instanceList = new ArrayList<>(50);
    }   
    
    public RectPacking(int w, int h, List<Rect> rects){
    	rng = new Random();
    	Instance instance = new Instance();
    	instance.binWidth = w;
    	instance.binHeight = h;
    	instance.loadRectToPack(rects);
    	
    	instanceList = new ArrayList<>(1);
    	instanceList.add(instance);
    	instanceID = 0;
    }     
    
        
    /**
     * Read problem instance from a file.
     * @param pathToInstanceFile path to the instance file
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public final void read(String pathToInstanceFile) throws FileNotFoundException, IOException{
        int binWidth, binHeight;
        instanceList = new ArrayList<>(50);
        File f = new File(pathToInstanceFile);
        FileInputStream fis = new FileInputStream(f);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line = null;
        ArrayList<Rect> rectList = new ArrayList<>();
        int numItems;
        int headingLines = 0; //count the number of headings
        boolean isNewInstance = false; //beginning of reading a new instance
        Instance instance = new Instance();
        //start reading!
        while((line = br.readLine()) != null){
            //System.out.println(line);
            StringTokenizer tokens = new StringTokenizer(line);
            if(tokens.countTokens()==0){//file contains new lines to separate instance
                instance.loadRectToPack(rectList);
                instanceList.add(instance);
                //System.out.println("size = " + instance.size());
                continue; 
            } 
            if(line.contains("PROBLEM")){
                isNewInstance = true;
                headingLines++;
                //if(instance.isInitialized()) instanceList.add(instance);
                instance = new Instance();
                continue; //read the next 
            }
            if(isNewInstance){
                switch (headingLines) {
                    //first heading is number of items
                    case 1:
                        numItems = Integer.parseInt(tokens.nextToken());
                        rectList = new ArrayList<>(numItems);
                        headingLines++;
                        break;
                    //second heading is the numbering of the instances
                    case 2:
                        headingLines++;
                        break; //not interesting
                    //third heading is the bin dimensions
                    case 3:
                        binWidth = Integer.parseInt(tokens.nextToken());
                        binHeight = Integer.parseInt(tokens.nextToken());
                        instance.setBinDim(binWidth, binHeight);
                        headingLines = 0; //heading lines are done. reset the count
                        isNewInstance = false;
                        break;
                    default:
                        break;
                }
            }
            else{//not a new instance
                int width = Integer.parseInt(tokens.nextToken().trim());
                int height = Integer.parseInt(tokens.nextToken().trim());
                Rect rect = new Rect(width, height);
                //instance.loadRect(rect);
                rectList.add(rect);
            }
        }
    }

    /**
     * Initialize a solution by packing randomly shuffled items into bins using 
     * the best are fit heuristic.
     * @return an initial solution
     */
    public RBPSolution initializeSolution(){
        Instance instance = instanceList.get(instanceID);
        //Get the list of rects to be packed
        List<Rect> rectList = instance.queue;
        //Shuffle
        Collections.shuffle(rectList, rng);
        //Create an empty solution
        RBPSolution sol = new RBPSolution(instance.binWidth, instance.binHeight);
        //Pack rects. Use best area fit as an initial heuristic.
         sol.pack(rectList, PackingHeuristic.BestAreaFit);
        return sol;
    }
    
    /**
     * Get an empty solution.
     * @return an empty solution that does not contain any bin (not initialized)
     */
    public RBPSolution getEmptySolution(){
        Instance instance = instanceList.get(instanceID);
        //Get the list of rects to be packed
        List<Rect> rectList = instance.queue;
        //Shuffle
        Collections.shuffle(rectList, rng);
        //Create an empty solution
        return new RBPSolution(instance.binWidth, instance.binHeight);
    }
    
    public Instance getInstance(int instanceID){
        return instanceList.get(instanceID);
    }
    
    /**
     * Set the instance to be solved.
     * @param instanceID 
     */
    public void setInstance(int instanceID){
       this.instanceID = instanceID; 
    }
    
    public List<Rect> getPackingQueue(){
        Instance instance = instanceList.get(instanceID);
        return instance.queue;
    }
      
}    

