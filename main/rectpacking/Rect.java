package rectpacking;

/**
 * A class implementing a rectangular item.
 * @author Ahmed Hassan (ahmedhasssan@aims.ac.za)
 */
public class Rect {
    /** the side of the rect that is parallel to x-axis (convention)*/
    public int width;
    /** the side of the rect that is parallel to y-axis (convention)*/
    public int height;
    /** the area of the rect item */
    public final int area;
    /** The x-coordinate of the bottom-left corner of this rect inside a bin */
    public int x;
    /** The y-coordinate of the bottom-left corner of this rect inside a bin */
    public int y;
    /** associate a score with packing to quantify the "goodness" of packing */
    public double score;

    /**
     * Create a degenerate rect.
     */
    public Rect(){
        this.width = 0;
        this.height = 0;
        this.area = width * height;
        this.score = Double.POSITIVE_INFINITY;
        this.x = -1; //not packed yet
        this.y = -1;
    }

    /**
     * Create rect of a specific width and height.
     * @param width
     * @param height
     */
    public Rect(int width, int height){
        this.width = width;
        this.height = height;
        this.area = width * height;
        this.score = Double.POSITIVE_INFINITY;
        x = -1; //not packed yet
        y = -1;
    }

    /**
     * Create a rect from another rect.
     * @param rect
     */
    public Rect(Rect rect){
        this.width = rect.width;
        this.height = rect.height;
        this.area = rect.area;
        this.x = rect.x;
        this.y = rect.y;
        this.score = rect.score;
    }

    /**
     * Create rect of a specific width and height (static constructor).
     * @param width
     * @param height
     * @return
     */
    public static Rect of(int width, int height) {
    	return new Rect(width, height);
    }

    public void removePackingInfo(){
        x = -1;
        y = -1;
        score = Double.POSITIVE_INFINITY;
    }

    public void rotate(){
        int tmp = width;
        width = height;
        height = tmp;
    }

    /**
     * Check if a rect reduces to a line or a point.
     * @return <code>true</code> if a rect is degenerate and return <code>false</code> otherwise.
     */
    public boolean isDegenerate(){
        return width == 0 || height == 0 || width == 0 && height == 0;
    }

    /**
     * A position in a bin is found for this rect.
     * @return
     */
    public boolean isReadyForPacking(){
        return x != -1 && y != -1;
    }

    /**
     * Compute the length of the shared horizontal edges between this rect and <code>rect</code>.
     * @param rect
     * @return
     */
    public int computeCommonHorizLength(Rect rect){
        int r1x1, r1x2, r2x1, r2x2;
        r1x1 = this.x; r1x2 = this.x + width;
        r2x1 = rect.x; r2x2 = rect.x + rect.width;
        return Integer.min(r1x2, r2x2) - Integer.max(r1x1, r2x1);
    }

    /**
     * Compute the length of the shared vertical edges between this rect and <code>rect</code>.
     * @param rect
     * @return
     */
    public int computeCommonVertLength(Rect rect){
        int r1y1, r1y2,r2y1, r2y2;
        r1y1 = this.y; r1y2 = this.y + this.height;
        r2y1 = rect.y; r2y2 = rect.y + rect.height;
        return Integer.min(r1y2, r2y2) - Integer.max(r1y1, r2y1);
    }

    /**
     * Check whether this rect is contained insdie <code>rect</code>.
     * @param rect
     * @return
     */
    public boolean isContainedIn(Rect rect){
        //note that isContained covers equals!
        return this.x >= rect.x && this.y >= rect.y
               && this.x + this.width <= rect.x + rect.width
               && this.y + this.height <= rect.y + rect.height;
    }

    int computeCommonLength(int start1, int end1, int start2, int end2){
        if(start2 >= end1 || end2 <= start1) {
			return 0;
		}
        return Integer.min(end1, end2) - Integer.max(start1, start2);
    }

    /**
     * Check if this rect overlaps with <code>rect</code>.
     * @param rect
     * @return
     */
    public boolean isOverlapping(Rect rect){
        //If overlaps horizontally
        if(!(this.x >= rect.x + rect.width || this.x + this.width <= rect.x)){
            if(computeCommonLength(this.y, this.y+this.height, rect.y, rect.y+rect.height) > 0) {
				return true;
			}
        }
        //If vertically overlaps
        else if(!(this.y >= rect.y + rect.height || this.y + this.height <= rect.y)){
            if(computeCommonLength(this.x, this.x+this.width, rect.x, rect.x+rect.width) > 0) {
				return true;
			}
        } else {
			return false;
		}
        return false;
    }


    boolean isTouching(int x1, int y1, int x2, int y2){
        boolean isTouching = false;
        //If the line is vertical
        if(x1 == x2){
            //check common vertical length
            if(this.x == x1 || this.x + this.width == x1) {
				isTouching = computeCommonLength(y1, y2, this.y, this.y + this.height) > 0;
			}
        }
        //If the line is horizontal
        else if(y1 == y2){
            if(this.y == y1 || this.y + this.height == y1) {
				isTouching = computeCommonLength(x1, x2, this.x, this.x + this.width) > 0;
			}
        }
        else{
            System.out.println("Neither horizontal nor vertical!");
        }
        return isTouching;
    }

    @Override
    public boolean equals(Object object){
        if(object == null) {
			return false;
		}
        if(object == this) {
			return true;
		}
        if(!(object instanceof Rect)) {
			return false;
		}
        Rect anotherRect = (Rect) object;
        return anotherRect.x == this.x && anotherRect.y == this.y && anotherRect.width == this.width
                && anotherRect.height == this.height;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.width;
        hash = 79 * hash + this.height;
        hash = 79 * hash + this.x;
        hash = 79 * hash + this.y;
        return hash;
    }

    @Override
    public String toString(){
    	return String.format("Rect(w=%s, h=%s, x=%s, y=%s)", width, height, x, y);
    }
}
