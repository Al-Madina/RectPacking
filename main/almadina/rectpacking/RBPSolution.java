package almadina.rectpacking;

import java.util.ArrayList;
import java.util.List;

import almadina.rectpacking.RectPacking.PackingHeuristic;


/**
 * A solution of the rectangular two-dimensional bin packing problem.
 * <p>
 * A solution is just a list of bins with items packed in them.
 * @author Ahmed Hassan (ahmedhasssan@aims.ac.za)
 */
public class RBPSolution {
    private final int binWidth;
    private final int binHeight;

    //This is not implemented properly in the packing methods
    private List<Bin> binList;
    //solution measures:
    private int numBins;


    public RBPSolution(int width, int height){
        //You might want to have a random number generator if you want to extend
        //the functionality of this class.
        this.binWidth = width;
        this.binHeight = height;
        numBins = 0;
    }

    public RBPSolution(RBPSolution newSol){
        this.binWidth = newSol.binWidth;
        this.binHeight = newSol.binHeight;
        binList = new ArrayList<>(newSol.binList.size());
        //binList.addAll(newSol.binList); //wrong
        for(Bin bin : newSol.binList){
            binList.add(new MaxSpaceBin(bin));
        }
        this.numBins = newSol.numBins;
    }


    /**
     * Pack the rects in <code>rectList</code> into bins.
     * @param rectList a list of rects to be packed
     * @param heur the packing heuristic
     */
    public void pack(List<Rect> rectList, PackingHeuristic heur){
        //Compute a lower bound on the number of bins
        numBins = computeLowerBound(rectList);
        binList = new ArrayList<>(numBins);
        //initialize bins
        for(int i=0; i<numBins; i++) {
			binList.add(openNewBin());
		}
        //Consider packing the rects according to their order
        for(Rect curRect : rectList){
            double bestValue = Double.POSITIVE_INFINITY;
            Bin bestBin = null; //Rect rectToPack = null;
            Rect bestRect = null;
            //determine the best bin for the current rect
            for(Bin bin : binList){
                //Remove the packing info if any exist
                curRect.removePackingInfo();
                //Evaluate whether it is possible to pack the current rect into
                //the current bin and if so, calcluate the cost of  that
                Rect newRect = bin.evaluatePacking(curRect, heur);
                if(newRect != null && newRect.score < bestValue){
                    bestValue = newRect.score;
                    bestBin = bin;
                    bestRect = newRect;
                }
            }
            //If a bin is found, pack rect
            if(bestBin != null){
                bestBin.insert(bestRect, heur);
            }
            //otherwise, open a new bin
            else{
                Bin newBin = openNewBin();
                newBin.insert(curRect, heur); //no need to evaluate packing
                binList.add(newBin);
            }
        }
        numBins = binList.size();
    }

    /**
     * Pack the rects in <code>rectList</code> into the <i>first available</i> bins.
     * @param rectList a list of rects to be packed
     * @param heur the packing heuristic
     */
    public void packFirst(List<Rect> rectList, PackingHeuristic heur){
        //Compute a lower bound on the number of bins
        numBins = computeLowerBound(rectList);
        binList = new ArrayList<>(numBins);
        binList.add(openNewBin());
        //Consider packing the rects according to their order
        for(Rect curRect : rectList){
            Bin bestBin = null; //Rect rectToPack = null;
            Rect bestRect = null;
            //determine the best bin for the current rect
            for(Bin bin : binList){
                //Remove the packing info if any exist
                curRect.removePackingInfo();
                //Evaluate whether it is possible to pack the current rect into
                //the current bin and if so, calcluate the cost of  that
                Rect newRect = bin.evaluatePacking(curRect, heur);
                if(newRect != null){
                    bestBin = bin;
                    bestRect = newRect;
                    break; // A bin is found, do not go further
                }
            }
            //If a bin is found, pack rect
            if(bestBin != null){
                bestBin.insert(bestRect, heur);
            }
            //otherwise, open a new bin
            else{
                Bin newBin = openNewBin();
                newBin.insert(curRect, heur); //no need to evaluate packing
                binList.add(newBin);
            }
        }
        numBins = binList.size();
    }

    /**
     * Check whether this solution is feasible.
     * @return
     */
    public boolean isFeasible(){
        for(Bin bin : binList) {
			if(!bin.isFeasible()) {
                return false;
            }
		}
        return true;
    }

    private Bin openNewBin(){
        Bin newBin = new MaxSpaceBin(binWidth, binHeight);
        newBin.init();
        return newBin;
    }

    public int computeLowerBound(List<Rect> rectList){
        int area = 0;
        for(Rect rect : rectList) {
			area += rect.width * rect.height;
		}
        return area/(binWidth*binHeight) + 1; //ceiling
    }

    public int getNumberOfBin(){
        return numBins;
    }

    public List<Bin> getBins() {
    	return binList;
    }

    @Override
    public String toString(){
        for(Bin bin : binList) {
			System.out.println(bin);
		}
        String st = "";
        for(int i=0; i<binList.size(); i++){
            Bin bin = binList.get(i);
            st += "Size of bin " + i + " = " + bin.size() + "\n";
        }
        return st;
    }

}