package rectpacking;

import java.util.Comparator;

/**
 *
 * @author ahmed
 */
public class RectAreaComparator implements Comparator<Rect>{

    @Override
    public int compare(Rect o1, Rect o2) {
        return -Integer.compare(o1.area, o2.area);
    }

}
