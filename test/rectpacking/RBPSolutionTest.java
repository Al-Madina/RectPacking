/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rectpacking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Random;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Testing the Solution class.
 * @author Ahmed Hassan (ahmedhassan@aims.ac.za)
 */
public class RBPSolutionTest {
    //Instance of the rect packing problem
    private static RectPacking problem;    
    // A solution to the rect packing problem
    private RBPSolution solution;
    // The lower bound (the smallest theoretical number of bins that can be used)
    private static int lowerBound;
    //A random number generator
    private static Random rng;
    
    /**
     * This method sets up the test class.
     */
    @BeforeAll
    public static void setUpClass(){
        System.out.println("Setting up the tests for " + RBPSolutionTest.class);
        //You can choose any class. It does not really matter.
        int classID = 1; // class ID
        //Path to the location of dataset
        String directory = "D:\\Data\\RBP\\";
        //Problem instance file
        String className = classID < 10 ? "0"+classID : ""+classID;
        String filename = "Class_" + className + ".2bp";
        filename = directory + filename;        
        System.out.println("Reading: " + filename);
        //Create a problem
        problem = new RectPacking(12345);        
        try {
            //Read the problem instance file
            problem.read(filename);
        } catch (IOException ex) {
            Logger.getLogger(RBPSolutionTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Set up the instance to be solved. You could choose any other instance.
        int instanceID = 46;
        System.out.println("Instance to solve: " + instanceID);
        problem.setInstance(instanceID);
        lowerBound = 0;
        problem.getPackingQueue().forEach(rect -> {
            lowerBound += rect.area;
        });
        lowerBound = lowerBound/(problem.getInstance(instanceID).binWidth*problem.getInstance(instanceID).binHeight) + 1;        
        rng = new Random(12345);
    }
    
    @AfterAll
    public static void tearDownClass() {
        //No need to implement anything here. 
        //If you open a resource you can close it here.
    }
    
    /**
     * Sets up each test method by creating an empty solution that will be used
     * before each test.
     */
    @BeforeEach
    public void setUp() {
        //Create a solution before each test
        solution = problem.getEmptySolution();
    }
    
    @AfterEach
    public void tearDown() {
        //No need to clear solution created earlier in `setUp` sicne Junit 5 
        //will take care of that Implement this method if want to clean up 
        //resources after each test
    }
    
    /**
     * Test of pack method, of class RBPSolution.
     * @param in an element of type Input that encapsulate the choice of the 
     * packing heuristic and the packing sequence.
     */
    @ParameterizedTest //Accept parameters of type Input
    @MethodSource("generateArguments") //Create a list of the inputs
    public void testPack(Input in) {                
        //Pack the items using to the packing heuristic specified by in.heur and
        //the packing sequence specified by in.queue
        solution.pack(in.queue, in.heur);
        
        //Check if the solution is feasible.
        //A packing is feasible if the following conditions are satisfied:
        //(1) No rect in the bin overlaps with another rect.
        //(2) No rect is partially or fully packed outside the bin.
        assertTrue(solution.isFeasible());
        //You could make a case that we need to test `solution.isFeasible`,
        //however, I did not do that here since the correctness of the
        //implementation of isFeasible can be proved mathematically.
        
    }

    /**
     * Test of packFirst method, of class RBPSolution.
     * @param in an element of type Input that encapsulate the choice of the 
     * packing heuristic and the packing sequence.
     */
    @ParameterizedTest //Accept parameters of type Input
    @MethodSource("generateArguments") //Create a list of the inputs
    public void testPackFirst(Input in) {                
        //Pack the items using to the packing heuristic specified by in.heur and
        //the packing sequence specified by in.queue
        solution.packFirst(in.queue, in.heur);
        
        //Check if the solution is feasible.
        //A packing is feasible if the following conditions are satisfied:
        //(1) No rect in the bin overlaps with another rect.
        //(2) No rect is partially or fully packed outside the bin.
        assertTrue(solution.isFeasible());
        //Please note that `Solution#isFeasible` calls `MaxSpaceBin#isFeasible`
        //on every bin included in the solution. We tested `MaxSpaceBin#isFeasible`
        //in the `MaxSpaceBinTest` class
        
    }



    /**
     * Test of getNumberOfBin method, of class RBPSolution.It is important to 
     * note that we do not know the exact number of bins. 
     * However, the number of bins must not be less than the lower bound 
     * regardless of the packing sequence and the packing heuristic used.
     * @param in
     */
    @ParameterizedTest //Accept parameters of type Input
    @MethodSource("generateArguments") //Create a list of the inputs
    public void testGetNumberOfBin(Input in) {      
        solution.pack(in.queue, in.heur);
        int numBins = solution.getNumberOfBin();
        //The number of bins cannot be smaller than the lower bound
        assertTrue(numBins >= lowerBound);
    }
    
        /**
     * This method creates inputs for parameterized test methods.
     * The input are instance of the `Input` class defined below. Each instance
     * of the input class encapsulate a packing heuristic and a packing sequence
     * where the packing sequence defined a particular order of the items.
     * The reason for this is that the packing structure will be different 
     * depending on the choice of the packing heuristic and the packing sequence.
     * @return a list of the inputs for the parameterized test methods.
     */
    public static Collection<Input> generateArguments(){
        int numPackSeq = 10000;
        List<Input> inputList = new ArrayList<>(3*numPackSeq); //we have 3 packing heuristics
        for(RectPacking.PackingHeuristic heur : RectPacking.PackingHeuristic.values()){
            for(int i=0; i < numPackSeq; i++){
                List<Rect> queue = problem.getPackingQueue();
                Collections.shuffle(queue, rng);
                Input in = new Input(heur, queue);
                inputList.add(in);
            }
        }
        return inputList;
    }
    
    /**
     * A static nested class for defining an input for parameterized test methods.
     */
    public static class Input{
        RectPacking.PackingHeuristic heur;
        List<Rect> queue;

        public Input(RectPacking.PackingHeuristic heur, List<Rect> queue) {
            this.heur = heur;
            this.queue = queue;
        }
    }
}
