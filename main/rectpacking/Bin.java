package rectpacking;

import java.util.ArrayList;
import java.util.Objects;
import rectpacking.RectPacking.PackingHeuristic;

/**
 * A class for representing a two-dimensional bin. 
 * <p> 
 * This abstract class implements the basic functionalities of bins. It should be
 * extended by concrete classes that implements specific data structure for the 
 * bins such as the maximal space structure. 
 * @author Ahmed Hassan (ahmedhasssan@aims.ac.za)
 */
public abstract class Bin {
    /** Bin width */
    protected int binWidth;
    /** Bin height */
    protected int binHeight;
    /** Area in the bin occupied by rects */
    protected int occupiedArea; 
    /** A list of currently packed rects in this bin */
    protected ArrayList<Rect> packedRects;
    /** A list of free maximal spaces (free rects) that can potentially contain items */
    protected ArrayList<Rect> freeRects;
    
    
    /**
     * Create a bin of specific width and height.
     * @param binWidth width of the bin
     * @param binHeight height of the bin
     */
    public Bin(int binWidth, int binHeight){
        this.binWidth = binWidth;
        this.binHeight = binHeight;
        occupiedArea = 0;
        packedRects = new ArrayList<>();
        freeRects = new ArrayList<>();
    }
    
    /**
     * Create a bin from another bin using deep copying through the constructor.
     * @param newBin a bin
     */
    public Bin(Bin newBin){
        this.binWidth = newBin.binWidth;
        this.binHeight = newBin.binHeight;
        occupiedArea = newBin.occupiedArea;
        //Copy packed rects
        packedRects = new ArrayList<>(newBin.packedRects.size());
        for(Rect rect : newBin.packedRects)
            packedRects.add(new Rect(rect));
        // Copy free rects
        freeRects = new ArrayList<>(newBin.freeRects.size());
        for(Rect freeRect : newBin.freeRects)
            freeRects.add(new Rect(freeRect));
    }       
    
    /**
     * A method to set up the free spaces that are used to place items in.
     * <p>
     * Initially the free space is the whole bin and as more items are packed,
     * the free spaces needs to be calculated according to the chosen data
     * structure that is used (e.g. shelf data structure, maximal spaces data 
     * structure, skyline data structure).
     */
    protected void setupFreeRects(){
        freeRects = new ArrayList<>();
        Rect maxSpace = new Rect(binWidth, binHeight);
        maxSpace.x = 0;
        maxSpace.y = 0;
        freeRects.add(maxSpace);
    }
    
    /**
     * Generate free spaces in the bins after packing a new rectangular item.
     * <p>
     * In concrete bin classes, you need to implement this method to track the 
     * free spaces in the bin. The exact implementation depends on the choice of
     * the  data structure that is used (e.g. shelf data structure, maximal 
     * spaces data structure, skyline data structure).
     * @param rect the rectangular item that is currently packed in the bin.
     */
    protected abstract void generateFreeSpaces(Rect rect);
    
    /**
     * Initialize the bin.
     */
    public void init(){        
        packedRects = new ArrayList<>();
        setupFreeRects();
        occupiedArea = 0;
    }    
    
    /**
     * The number of items packed in the bin.
     * @return the number of items packed in the bin.
     */
    public int size(){
        return packedRects.size();
    }   
    
    /**
     * Evaluate the <i>cost</i> of packing <code>rect</code> in the bin using the
     * packing heuristic <code>heur</code>.
     * <p>
     * A subclass bin is required to provide a concrete implementation of this
     * method.
     * @param rect the item to be packed
     * @param heur the packing heuristic. For example, best area fit which packs
     * the item in the free space that results in the least wasted area in  the bin
     * @return the cost of packing <code>rect</code> in the bin using the packing
     * heuristic <code>heur</code>. If the packing is not possible, returns <code>null</code>
     */
    public abstract Rect evaluatePacking(Rect rect, PackingHeuristic heur);
    
    /**
     * Insert <code>rect</code> in the bin using the packing heuristic <code>heur</code>
     * to determine the position of <code>rect</code> in the bin.
     * <p>
     * 
     * @param rect
     * @param heur
     * @return <code>true</code> if <code>rect</code> is inserted in the bin. Otherwise, returns <code>false</code>.
     */
    public abstract boolean insert(Rect rect, PackingHeuristic heur);
    
    /**
     * Pack <code>rect</code> in this bin.
     * @param rect 
     */
    public final void packRect(Rect rect){
        packedRects.add(rect);
        occupiedArea += rect.width * rect.height;
    }                
    
    public boolean isEmpty(){
        return packedRects.isEmpty();
    }
    
    /**
     * Check whether the packing in this bin is feasible.
     * <p>
     * A packing is feasible if the following conditions are satisfied:
     * <ul>
     *  <li> No rect in the bin overlaps with another rect. </li>
     *  <li> No rect is partially or fully packed outside the bin </li>.
     * </ul>
     * @return <code>true</code> if the packing is valid and <code>false</code> otherwise.
     */
    public boolean isFeasible(){
        for(int i=0; i<packedRects.size(); i++){
            Rect rect1 = packedRects.get(i);
            //If rect1 partially or fully lies outside the bin.
            if(rect1.x < 0 || rect1.x > binWidth) return false;
            if(rect1.y < 0 || rect1.y > binHeight) return false;
            if(rect1.x + rect1.width > binWidth || rect1.y + rect1.height > binHeight)
                return false;
            //Check for overlapping
            for(int j=i+1; j<packedRects.size(); j++){
                Rect rect2 = packedRects.get(j);
                //check wether the two rects overlap
                if(rect1.isOverlapping(rect2)) return false;
            }
        }
        return true;
    }
    
    protected final boolean isOverlapping(Rect rect1, Rect rect2){
        boolean horizSkip = rect1.x >= rect2.x + rect2.width || rect1.x + rect1.width <= rect2.x;
        boolean vertSkip = rect1.y >= rect2.y + rect2.height || rect1.y + rect1.height <= rect2.y;
        return !(horizSkip || vertSkip);
    }        
    
    public ArrayList<Rect> getPackedRect(){
        //make sure packed rects will not be modified; otherwise, returns a copy
        return packedRects; 
    }
    
    public int getPackedArea(){
        return occupiedArea;
    }
    
    public double getOccupancy(){
        return (double) occupiedArea/(binWidth*binHeight);
    }
    
    /**
     * Compute and returns the <i>touching perimeter</i>.
     * <p> The touching perimeter is the total perimeter shared between the item
     * that is currently packed and already-paced items plus the shared perimeter
     * between the item and the bin sides.
     * share
     * @return the touching perimeter
     */
    public double getTouchingPerimeter(){
        if(packedRects.isEmpty()) return 0;
        double touchingPerimeter = 0;
        double totalPer = 0;
        for(Rect rect : packedRects){
            touchingPerimeter += computeTouchingPerimeter(rect.x, rect.y, rect.width, rect.height);
            totalPer += 2*(rect.width + rect.height);
        }
        return touchingPerimeter/totalPer;        
    }
    
    /**
     * Compute the total touching perimeter of the rect that has its left-bottom
     * corner placed in (x,y).
     * <p>
     * The touching perimeter is the total shared "edges" between the rect and the
     * already-paced rects in the bin as well as between the bin and the rect.
     * @param x x-coordinate of the rect inside the bin
     * @param y y-coordinate of the rect inside the bin
     * @param width the rect's width
     * @param height the rect's height
     * @return the total touching perimeter
     */
    protected final double computeTouchingPerimeter(int x, int y, int width, int height){
        double perimeter = 0;
        if(x == 0 || x + width == binWidth)
            perimeter += height;
        if(y == 0 || y + height == binHeight)
            perimeter += width;
        for(Rect rect : packedRects){
            if(rect.x + rect.width == x || x + width == rect.x)
                perimeter += computeCommonLength(y, y+height, rect.y, rect.y+rect.height);
            if(rect.y == y + height || rect.y + rect.height == y)
                perimeter += computeCommonLength(x, x + width, rect.x, rect.x + rect.width);
        }
        return perimeter;
    }
    
    protected final int computeCommonLength(int start1, int end1, int start2, int end2){
        if(start2 >= end1 || end2 <= start1) return 0;
        return Integer.min(end1, end2) - Integer.max(start1, start2);
    }
    
    double computeDistance(int x1, int y1, int x2, int y2){
        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
        /**
         * You could also return the distance without the sqrt since it does not
         * really matter in this domain whether you return the distance or the distance squared.
         */
    }
    
    @Override
    public boolean equals(Object object){
        if(object == null) return false;
        else if (object == this) return true;
        else if (!(object instanceof Bin)) return false;
        Bin anotherBin = (Bin) object;
        return this.packedRects.equals(anotherBin.packedRects) && this.freeRects.equals(anotherBin.freeRects);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.packedRects);
        hash = 59 * hash + Objects.hashCode(this.freeRects);
        return hash;
    }
    
    @Override
    public String toString(){
        //Change this to whatever info you want to display from the bin
        return "I am a bin!";
    }

}