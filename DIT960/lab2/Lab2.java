
import java.io.*;
import java.util.*;

public class Lab2 {
	public static String pureMain(String[] commands) {
                PriorityQueue<Bid> buy_pq = new PriorityQueue(new BidComparator(BidComparator.MAX));
                PriorityQueue<Bid> sell_pq = new PriorityQueue(new BidComparator(BidComparator.MIN));

		StringBuilder sb = new StringBuilder();

		for(int line_no=0;line_no<commands.length;line_no++){
			String line = commands[line_no];
			if( line.equals("") )continue;

			String[] parts = line.split("\\s+");
			if( parts.length != 3 && parts.length != 4)
				throw new RuntimeException("line " + line_no + ": " + parts.length + " words");
			String name = parts[0];
			if( name.charAt(0) == '\0' )
				throw new RuntimeException("line " + line_no + ": invalid name");
			String action = parts[1];
			int price;
			try {
				price = Integer.parseInt(parts[2]);
			} catch(NumberFormatException e){
				throw new RuntimeException(
						"line " + line_no + ": invalid price");
			}
                        
                        int newPrice = 0;
                        if(parts.length == 4){
                            try {
				newPrice = Integer.parseInt(parts[3]);
                            } catch(NumberFormatException e){
				throw new RuntimeException(
						"line " + line_no + ": invalid new price");
                            }
                        }
                        
			if( action.equals("K") ) {
                            buy_pq.add(new Bid(name, price));
			} else if( action.equals("S") ) {
                            sell_pq.add(new Bid(name, price));
			} else if( action.equals("NK") ){
                            // TODO: I think this is wrong implementation.
                            // OLD_TODO: update existing buy bid. use parts[3].
                            buy_pq.remove(new Bid(name, price));
                            buy_pq.add(new Bid(name, newPrice));
                                
			} else if( action.equals("NS") ){
                            // TODO: I think this is wrong implementation.
                            // OLD_TODO: update existing sell bid. use parts[3].
                            sell_pq.remove(new Bid(name, price));
                            sell_pq.add(new Bid(name, newPrice));
                                
			} else {
				throw new RuntimeException(
						"line " + line_no + ": invalid action");
			}

			if( sell_pq.size() == 0 || buy_pq.size() == 0 )continue;
			
			// TODO:
			// compare the bids of highest priority from each of
			// each priority queues.
			// if the lowest seller price is lower than or equal to
			// the highest buyer price, then remove one bid from
			// each priority queue and add a description of the
			// transaction to the output.
		}

		sb.append("Order book:\n");

		sb.append("Sellers: ");
                while(!sell_pq.isEmpty()){
                    sb.append(sell_pq.remove() + " ");
                }
                sb.append("\n");
		sb.append("Buyers: ");
                while(!buy_pq.isEmpty()){
                    sb.append(buy_pq.remove() + " ");
                }

		return sb.toString();
	}

	public static void main(String[] args) throws IOException {
		final BufferedReader actions;
		if( args.length != 1 ){
			actions = new BufferedReader(new InputStreamReader(System.in));
		} else {
			actions = new BufferedReader(new FileReader(args[0]));
		}

		List<String> lines = new LinkedList<String>();
		while(true){
			String line = actions.readLine();
			if( line == null)break;
			lines.add(line);
		}
		actions.close();

		System.out.println(pureMain(lines.toArray(new String[lines.size()])));
	}
}

