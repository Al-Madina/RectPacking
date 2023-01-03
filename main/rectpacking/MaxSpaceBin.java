package rectpacking;

import rectpacking.RectPacking.PackingHeuristic;
import static rectpacking.RectPacking.PackingHeuristic.BestAreaFit;
import static rectpacking.RectPacking.PackingHeuristic.TopRightCornerDistance;
import static rectpacking.RectPacking.PackingHeuristic.TouchingPerimeter;
import static rectpacking.RectPacking.canRotate;

/**
 * A class implementing the maximal space data structure.
 * @author Ahmed Hassan (ahmedhasssan@aims.ac.za)
 */
public class MaxSpaceBin extends Bin{
    
    public MaxSpaceBin(int binWidth, int binHeight){
        super(binWidth, binHeight);
    }
    
    public MaxSpaceBin(Bin newBin){
        super(newBin);
    }
    
    @Override
    public Rect evaluatePacking(Rect rect, PackingHeuristic heur){
        Rect newRect; 
        switch(heur){
            case TouchingPerimeter: newRect = insertTouchingPerimeter(rect); break;
            case BestAreaFit: newRect = insertBestArea(rect); break;
            case TopRightCornerDistance: newRect = insertTopRightCornerDistance(rect); break;
            default: newRect = insertBestArea(rect); break; // Use best area fit as a default heuristic
        }
        return newRect;
    }
    
    @Override
    public boolean insert(Rect rect, PackingHeuristic heuristic){
        Rect newRect;
        //Check whether rect is ready for packing, i.e., method "evaluatePacking" has already been invoked
        if(!rect.isReadyForPacking()){
            switch(heuristic){
			case TouchingPerimeter:
				newRect = insertTouchingPerimeter(rect);
				break;
			case BestAreaFit:
				newRect = insertBestArea(rect);
				break;
			case TopRightCornerDistance:
				newRect = insertTopRightCornerDistance(rect);
				break;
			default:
				newRect = insertBestArea(rect);
				break; // Use best area fit as a default heuristic
			}
        }
        else{
            newRect = new Rect(rect);
        }
        //If packing rect is not possible
        if(newRect == null){
            return false;
        }
        
        //The rect can be packed in the bin.
        packRect(newRect);
        //Generate new free rects after packing the current rect.
        generateFreeSpaces(newRect);
        //Remove degenerate and non-maximal spaces.
        pruneMaxSpaces();        
        return true;
    }
   
    
    /**
     * Insert <code>rect</code> in the free rect such that the distance between
     * the top-right corner of the rect and that of the bin is maximized.
     * @param rect
     * @return a rect with the correct (x,y) coordinate inside the bin.
     */
    private Rect insertTopRightCornerDistance(Rect rect){
        double largestDist = -1;
        int bestMaxSpaceIndex = -1;
        boolean isRotated = false;
        for(int i=0; i<freeRects.size(); i++){
            Rect maxSpace = freeRects.get(i);
            //try to fit rect into maxSpace in upright position
            if(rect.width <= maxSpace.width && rect.height <= maxSpace.height){
                double dist = computeDistance(maxSpace.x + rect.width, maxSpace.y + rect.height, binWidth, binHeight);
                if(dist > largestDist){
                    largestDist = dist;
                    bestMaxSpaceIndex = i;
                    isRotated = false;
                }
            }
            //If rotation is possible, try to fit rect in.
            if(canRotate && rect.height <= maxSpace.width && rect.width <= maxSpace.height){
                double dist = computeDistance(maxSpace.x + rect.height, maxSpace.y + rect.width, binWidth, binHeight);
                if(dist > largestDist){
                    largestDist = dist;
                    bestMaxSpaceIndex = i;
                    isRotated = true;
                }
            }
            
        }
        //The current rect cannot be inserted into current bin.
        if(bestMaxSpaceIndex == -1){  
            //Instead of returning null, you could return a degenerate rect which
            //a rect with either side being 0 (i.e. width or height equals 0)
            return null;
        }
        //insert rect into the best maxSpace with the appropriate orientation
        Rect newRect = new Rect(rect.width, rect.height);
        if(isRotated) newRect.rotate();
        newRect.x = freeRects.get(bestMaxSpaceIndex).x;
        newRect.y = freeRects.get(bestMaxSpaceIndex).y;
        newRect.score = -largestDist; //smaller is better
        return newRect;
    }
    
    /**
     * Insert <code>rect</code> in the free rect such that the total touching 
     * perimeter is maximized.
     * @param rect
     * @return a rect with the correct (x,y) coordinate inside the bin.
     */
    private Rect insertTouchingPerimeter(Rect rect){
        double largestTouchingPerimeter = -1;
        int bestMaxSpaceIndex = -1;
        boolean isRotated = false;
        for(int i =0; i<freeRects.size(); i++){
            Rect maxSpace = freeRects.get(i);
            if(rect.width <= maxSpace.width && rect.height <= maxSpace.height){
                double perimeter = computeTouchingPerimeter(maxSpace.x, maxSpace.y, rect.width, rect.height);
                if(perimeter > largestTouchingPerimeter){
                    largestTouchingPerimeter = perimeter;
                    bestMaxSpaceIndex = i;
                    isRotated = false;
                }
            }
            if(canRotate && rect.height <= maxSpace.width && rect.width <= maxSpace.height){
                double perimeter = computeTouchingPerimeter(maxSpace.x, maxSpace.y, rect.height, rect.width);
                if(perimeter > largestTouchingPerimeter){
                    largestTouchingPerimeter = perimeter;
                    bestMaxSpaceIndex = i;
                    isRotated = true;
                }
            }
        }
        //The current rect cannot be inserted into current bin.
        if(bestMaxSpaceIndex == -1){
            //Instead of returning null, you could return a degenerate rect which
            //a rect with either side being 0 (i.e. width or height equals 0)
            return null;
        }
        Rect newRect = new Rect(rect.width, rect.height);
        if(isRotated) newRect.rotate();
        newRect.x = freeRects.get(bestMaxSpaceIndex).x;
        newRect.y = freeRects.get(bestMaxSpaceIndex).y;
        newRect.score = -largestTouchingPerimeter; //smaller is better
        return newRect;
    }
    
    /**
     * Insert <code>rect</code> in the free rect such that the the wasted area is
     * minimized.
     * @param rect
     * @return a rect with the correct (x,y) coordinate inside the bin.
     */
    private Rect insertBestArea(Rect rect){
        int bestWastedArea = Integer.MAX_VALUE;
        int bestShortSide = Integer.MAX_VALUE;
        int bestMaxSpaceIndex = -1;
        boolean isRotated = false;
        for(int i=0; i<freeRects.size(); i++){
            Rect maxSpace = freeRects.get(i);
            int wastedArea = maxSpace.width * maxSpace.height - (rect.width * rect.height);
            if(maxSpace.width >= rect.width && maxSpace.height >= rect.height){
                int horizLeftOver = maxSpace.width - rect.width;
                int vertLeftOver = maxSpace.height - rect.height;
                int shortSide = Integer.min(horizLeftOver, vertLeftOver);
                if(wastedArea < bestWastedArea || (wastedArea == bestWastedArea && shortSide < bestShortSide)){
                    bestWastedArea = wastedArea;
                    bestShortSide = shortSide;
                    bestMaxSpaceIndex = i;
                    isRotated = false;
                }
            }
            if(canRotate && maxSpace.width >= rect.height && maxSpace.height >= rect.width){
                int horizLeftOver = maxSpace.width - rect.height;
                int vertLeftOver = maxSpace.height - rect.width;
                int shortSide = Integer.min(horizLeftOver, vertLeftOver);
                if(wastedArea < bestWastedArea || (wastedArea == bestWastedArea && shortSide < bestShortSide)){
                    bestWastedArea = wastedArea;
                    bestShortSide = shortSide;
                    bestMaxSpaceIndex = i;
                    isRotated = true;
                }
            }
        }
        if(bestMaxSpaceIndex == -1){
            //Instead of returning null, you could return a degenerate rect which
            //a rect with either side being 0 (i.e. width or height equals 0)
            return null;
        }
        Rect newRect = new Rect(rect.width, rect.height);
        if(isRotated) newRect.rotate();
        newRect.x = freeRects.get(bestMaxSpaceIndex).x;
        newRect.y = freeRects.get(bestMaxSpaceIndex).y;
        newRect.score = bestWastedArea;
        return newRect;
    }
    
    
    /**
     * Generate a new free space (maximal spaces) after packing <code>rect</code>.
     * @param rect the last rect inserted in the bin.
     */    
    @Override
    protected void generateFreeSpaces(Rect rect){
        int numFreeRects = freeRects.size();
        for(int i=0; i<numFreeRects; i++){
            Rect freeRect = freeRects.get(i);
            if(! isOverlapping(freeRect, rect)) continue;
            //Horizontal overlap
            if(rect.x < freeRect.x + freeRect.width && rect.x + rect.width > freeRect.x){
                //New free rect is on bottom of rect
                if(rect.y >  freeRect.y && rect.y < freeRect.y + freeRect.height){
                    Rect newFreeRect = new Rect(freeRect);
                    newFreeRect.height = rect.y - freeRect.y;
                    freeRects.add(newFreeRect);
                }
                //New free is on top of rect
                if(rect.y + rect.height > freeRect.y && rect.y + rect.height < freeRect.y + freeRect.height){
                    Rect newFreeRect = new Rect(freeRect);
                    newFreeRect.y = rect.y + rect.height;
                    newFreeRect.height = freeRect.y + freeRect.height - (rect.y + rect.height);
                    freeRects.add(newFreeRect);
                }
            }
            //Vertical overalp
            if(rect.y < freeRect.y + freeRect.height && rect.y + rect.height > freeRect.y){
                //New free rect is to the left of rect
                if(rect.x > freeRect.x && rect.x < freeRect.x + freeRect.width){
                    Rect newFreeRect = new Rect(freeRect);
                    newFreeRect.width = rect.x - freeRect.x;
                    freeRects.add(newFreeRect);
                }
                //New free rect is to the right of rect
                if(rect.x + rect.width > freeRect.x && rect.x + rect.width < freeRect.x + freeRect.width){
                    Rect newFreeRect = new Rect(freeRect);
                    newFreeRect.x = rect.x + rect.width;
                    newFreeRect.width = freeRect.x + freeRect.width - (rect.x + rect.width);
                    freeRects.add(newFreeRect);
                }
            }
            //freeRect should be removed as it is intersecting rect
            freeRects.remove(i);
            --i; //subsequent elements are shifted to left
            --numFreeRects;
        }
    }
    
    /**
     * Remove degenerate free maximal spaces.
     */
    private void pruneMaxSpaces(){
        for(int i=0; i<freeRects.size(); i++){
            Rect rectI = freeRects.get(i);
            for(int j=i+1; j<freeRects.size(); j++){
                Rect rectJ = freeRects.get(j);
                //if rect j is contained in rect i
                if(rectJ.isContainedIn(rectI)){
                    freeRects.remove(j);
                    --j;
                }
                //if rect i is contained in rect j
                else if(rectI.isContainedIn(rectJ)){
                    freeRects.remove(i);
                    --i;
                    break; //a non-maxSpace can be contained in at most one maxSpace
                }
            }
        }
    }
    
    
    /**
     * Check whether the packing in this bin is feasible.
     * @return <code>true</code> if the packing is valid and <code>false</code> otherwise.
     */
    @Override
    public boolean isFeasible(){
        //Check if all rects are packed inside the bins and do not overlap
        if(!super.isFeasible()) return false;
        //Moreover, check the free rects are feasible
        for(int i=0; i<freeRects.size(); i++){
            Rect maxRect1 = freeRects.get(i);
            for(int j=i+1; j<freeRects.size(); j++){
                Rect maxRect2 = freeRects.get(j);
                //No free rect is duplicated
                if(maxRect1.equals(maxRect2)){
                    return false;
                }
                //No free rect is inscribed into another one
                if(maxRect1.isContainedIn(maxRect2) || maxRect2.isContainedIn(maxRect1)){
                    return false;
                }
            }
        }
        return true;
    }    
}