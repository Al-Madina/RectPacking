/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rectpacking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Testing the MaxSpaceBin class.
 * @author Ahmed Hassan (ahmedhassan@aims.ac.za)
 */
public class MaxSpaceBinTest {
    
    /**
     * This method set up the test class.
     */
    @BeforeAll
    public static void setUpClass(){
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of evaluatePacking method, of class MaxSpaceBin.
     * Given an item to pack, it is placed in one of the free maximal spaces in
     * the bin. The choice of the maximal space to depends on a score. The score
     * depends on the packing heuristic used.
     * @param heur
     */
    @ParameterizedTest
    @EnumSource(RectPacking.PackingHeuristic.class)
    public void testEvaluatePacking(RectPacking.PackingHeuristic heur) {
        System.out.println("Testing evaluatePacking with " + heur + " ...");
        
        //Expected result depends on the packing heuristic
        //You could provide the argument as a stream of `Argument` and change the
        //method signature to receive the packing heuristic and the expected result
        double expectedResult = 0;
		switch (heur) {
		case BestAreaFit:
			expectedResult = 64;
			break;
		case TouchingPerimeter:
			expectedResult = -5;
			break;
		case TopRightCornerDistance:
			expectedResult = -9.433;
			break;
		}
        
        
        Bin bin = openBin();
        Rect r1 = new Rect(2, 3);
        r1.x = 0; r1.y = 0;
        //Insert the first rect at the bottom left corner of the bin
        //We will test the score computed by evaluatePacking when inserting the
        //second item below
        bin.insert(r1, heur);
        Rect r2 = new Rect(3, 2);
        r2 = bin.evaluatePacking(r2, heur);       
        assertEquals(expectedResult, r2.score, 0.01);
    }

    /**
     * Test of insert method, of class MaxSpaceBin.
     * We will test that <code>MaxSpaceBin#insert</code> returns true when packing
     * the item is possible. Otherwise, returns false. We will also test that the
     * bin size is correct after each insertion.
     * Please note that we do not have to test that the item is placed in the correct
     * free maximal space since <code>MaxSpaceBin#insert</code> calls 
     * <code>MaxSpaceBin#evaluatePacking</code> to determine the maximal space
     * that will contain the item. This means that if the packing score is correct
     * (as tested when testing <code>MaxSpaceBin#insert</code>) the correct maximal
     * space is identified.
     * If you feel you need to test that explicitly, feel free to do that!
     * @param heur
     */
    @ParameterizedTest
    @EnumSource(RectPacking.PackingHeuristic.class)
    public void testInsert(RectPacking.PackingHeuristic heur) {
        System.out.println("Testing insert with " + heur + " ....");
        Bin bin = openBin();
        Rect r1 = new Rect(5,3);
        boolean success = bin.insert(r1, heur);     
        assertTrue(success);
        assertEquals(1, bin.size());
        
        Rect r2 = new Rect(3,6);
        success = bin.insert(r2, heur);
        assertTrue(success);
        assertEquals(2, bin.size());
        
        Rect r3 = new Rect(8, 9);
        success = bin.insert(r3, heur);
        assertFalse(success);
        assertEquals(2, bin.size());
    }

    /**
     * Test of generateFreeSpaces method, of class MaxSpaceBin.
     * The packing does not have 100% test coverage!
     * You can test this your on your own.
     * Please note that if <code>MaxSpaceBin#generateFreeSpaces</code> is not 
     * working correctly, it will definitely yields to infeasible packing. 
     * Therefore, testing <code>MaxSpaceBin#isFeasible</code> implies that this 
     * method is implicitly tested.
     * If you want to explicitly test that, please feel free to do that!
     */
    @Test
    public void testGenerateFreeSpaces() {
        
    }

    /**
     * Test of isFeasible method, of class MaxSpaceBin.
     * A packing is feasible if the following conditions are satisfied:
     * <ul>
     *  <li> No rect in the bin overlaps with another rect. </li>
     *  <li> No rect is partially or fully packed outside the bin </li>.
     * </ul>
     * These means we need to test that all the following cases yields <code>false</code>
     * <ul>
     *  <li>Packing an item that is wider than the bin</li>
     * <li>Packing an item that is taller than the bin</li>
     * <li>Packing two items in identical position</li>
     * <li>Packing two items such that one item is placed on either sides of the 
     * other item</li>
     * <li>Packing two items such that one item is placed inside the other item. 
     * In this case, if the the second item is smaller (in terms of width and 
     * height), it will be totally contained in the first item. Otherwise, it 
     * will overlap with the first item</li>
     * </ul>
     */
    @Test
    public void testIsFeasible() {
        System.out.println("Testing isFeasible ...");
        //Asserts that inserting an item that is wider than the bin yields infeasible
        Bin bin = openBin();
        Rect wider = new Rect(11, 1);
        wider.x = 0; wider.y = 0;
        //The packing heuristic does not really matter since we hardcoded the 
        //placement of the item inside the bin to verify certain senarios
        bin.insert(wider, RectPacking.PackingHeuristic.BestAreaFit);
        assertFalse(bin.isFeasible());
        
        //Asserts that inserting an item taller than the bin yields infeasible
        bin = openBin();
        Rect taller = new Rect(1, 11);
        taller.x = 0; taller.y = 0;        
        bin.insert(taller, RectPacking.PackingHeuristic.BestAreaFit);
        assertFalse(bin.isFeasible());
        
        //Testing overlapping
        bin = openBin();
        Rect r1 = new Rect(2,2);
        Rect r2 = new Rect(3,3);
        //The two items share the exact location in the bin. Yielding infeasible
        r1.x = 0; r1.y = 0;
        r2.x = r1.x; r2.y =r1.y;
        bin.insert(r1, RectPacking.PackingHeuristic.BestAreaFit);
        bin.insert(r2, RectPacking.PackingHeuristic.BestAreaFit);
        assertFalse(bin.isFeasible());
        
        //Place an item along the horizontal side of the other item
        bin = openBin();
        r2.x = 1;
        bin.insert(r1, RectPacking.PackingHeuristic.BestAreaFit);
        bin.insert(r2, RectPacking.PackingHeuristic.BestAreaFit);
        assertFalse(bin.isFeasible());
        
        //Place an item along the vertical side of another item
        bin = openBin();
        r2.x = 0; r2.y = 1;
        bin.insert(r1, RectPacking.PackingHeuristic.BestAreaFit);
        bin.insert(r2, RectPacking.PackingHeuristic.BestAreaFit);
        assertEquals(2, bin.packedRects.size());
        assertFalse(bin.isFeasible());
        
        //Place an item in an area covered by the other item.
        bin = openBin();
        r2.x = 1; r2.y = 1;
        bin.insert(r1, RectPacking.PackingHeuristic.BestAreaFit);
        bin.insert(r2, RectPacking.PackingHeuristic.BestAreaFit);
        assertFalse(bin.isFeasible());
        
        //All these situation should yields infeasible packing structure. 
        //You could have one method to test all each situation above. However, 
        //if any assertion above does not hold the test will fail.
    }
    
    /**
     * Open a bin and initialize it.
     * @return 
     */
    private Bin openBin(){
        //Hard code width and height for the tests
        Bin bin = new MaxSpaceBin(10, 10);
        bin.init();
        return bin;
    }
    
}
