
import java.util.Comparator;

class BidComparator implements Comparator<Bid> {

    public static final boolean MAX = true;
    public static final boolean MIN = false;
    
    private int mod = 1;

    /**
     * 
     * @param isMax Used to denote if its a max r min heap.
     */
    public BidComparator(boolean isMax) {
        
        mod = isMax ? 1 : -1; 
    
    }
    
    
    
    @Override
    public int compare(Bid o1, Bid o2) {
        if (o1.equals(o2)) return 0;
        
        if (o1.bid >= o2.bid) {
            return -1*mod;
        } else {
            return 1*mod;
        }
    }
    
}