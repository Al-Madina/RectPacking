package rectpacking;

import java.util.ArrayList;
import java.util.List;

/**
 * An instance of the two-dimensional bin packing problem.
 * <p>
 * The instance is defined by
 * <ul>
 *  <li>The bin dimensions (width and height).</li>
 *  <li>The list of items to be packed.</li>
 * </ul>
 * @author Ahmed Hassan (ahmedhasssan@aims.ac.za)
 */
public class Instance {
    public int binWidth;
    public int binHeight;
    public List<Rect> queue;

    public Instance(){
        binWidth = 0;
        binHeight = 0;
        queue = new ArrayList<>();
    }

    public Instance(int binWidth, int binHeight){
        this.binWidth = binWidth;
        this.binHeight = binHeight;
        queue = new ArrayList<>();
    }

    public void loadRectToPack(List<Rect> queue){
        this.queue = new ArrayList<>(queue.size());
        for(int i=0; i < queue.size(); i++){
            Rect rect = queue.get(i);
            this.queue.add(rect);
        }
    }

    public void loadRect(Rect rect){
        this.queue.add(rect);
    }

    public void setBinDim(int binWidth, int binHeight){
        this.binWidth = binWidth;
        this.binHeight = binHeight;
    }

    public int size(){
        return this.queue.size();
    }
}