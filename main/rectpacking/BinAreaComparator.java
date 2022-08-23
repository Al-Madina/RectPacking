package rectpacking;

import java.util.Comparator;

/**
 *
 * @author ahmed
 */
public class BinAreaComparator implements Comparator{

    @Override
    public int compare(Object o1, Object o2) {
        /**
         * Bins with high occupancy are "less than" bin with low occupancy.
         * This way bins are ordered in a descending order of their occupancy.
         */
        Bin bin1 = (Bin) o1;
        Bin bin2 = (Bin) o2;
        //the compare is implemented this way so that bins are sorted in descending order of their occupancy
        //the reason for this is that when we work with bin of low occupancy and when removing them
        //from the list of bins, we do not need to do O(n) operations to update indices of the bins to the right
        return bin1.getOccupancy() == bin2.getOccupancy() ? 0 : (bin1.getOccupancy() > bin2.getOccupancy() ? -1 : 1);
    }
    
}
