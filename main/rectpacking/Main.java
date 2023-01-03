package rectpacking;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Ahmed Hassan (ahmedhasssan@aims.ac.za)
 */
public class Main {

    /**
     *
     * @param args
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException{
        //The dataset we consider has 10 classes
        int classID = 10; // class ID
        //Path to the location of dataset
        String directory = "D:\\Data\\RBP\\";
        //Problem instance file
        String className = classID < 10 ? "0"+classID : ""+classID;
        String filename = "Class_" + className + ".2bp";
        filename = directory + filename;
        System.out.println("Reading: " + filename);

        //Create an instance of the two-dimensional bin packing problem seeded
        //with 12345
        RectPacking problem = new RectPacking(12345);
        //Read the problem instance file
        problem.read(filename);
        //The problem file consists of 50 instances. We need to define which
        //instance we are solving
        int instanceID = 0; //Set the instance to be solved
        System.out.println("Instance to solve: " + instanceID);
        problem.setInstance(instanceID);
        //Create an initial solution
        RBPSolution solution = problem.initializeSolution();
        //Check if the solution is feasible (valid)
        if(!solution.isFeasible()){
            throw new RuntimeException("Infeasible solution");
        }
        System.out.println("Number of bins (using best area fit heuristic) = " + solution.getNumberOfBin());


        //The solution initialized above used the best area fit packing heuristic
        //by DEFAULT. To be able to use other packing heuristic, you can do:
        //Create an empty solution
        solution = problem.getEmptySolution();
        //Get the items to be packed
        List<Rect> queue = problem.getPackingQueue();
        //You can shuffle it or sort it
        Random rng = new Random(123456);
        Collections.shuffle(queue, rng);
        solution.pack(queue, RectPacking.PackingHeuristic.TouchingPerimeter);
        //Check if the solution is feasible (valid)
        if(!solution.isFeasible()){
            throw new RuntimeException("Infeasible solution");
        }
        System.out.println("Number of bins (using touching perimeter heuristic) = " + solution.getNumberOfBin());
    }
}
