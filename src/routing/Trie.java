package routing;

public class Trie {

	private Node root;
	
	public Trie() {
		
		root = new Node();
	}
	
	/**
	 * Adds an entry into the Trie addressed by the
	 * prefix and number of significant bits in the
	 * prefix and populates it with the next hop address.
	 * @param prefix The prefix corresponding to the desired hop address.
	 * @param sigbits The number of significant bits in the prefix.
	 * @param nextHop The next hop address.
	 */
	public void Insert(long prefix, int sigbits, String nextHop) {
		
		Node currentChild = root;
		long index;
		
		//If sigbits is odd, pad it with an extra bit.
		if((sigbits & 0x1) == 1) {
			
			sigbits++;
		}
		
		for(int i = 2; i <= sigbits; i += 2) {
			
			//Determine which child to go to
			index = (prefix >> (32 - i)) & 0x3;
			
			//Create branch if it doesn't exist
			if(currentChild.children[(int) index] == null) {
				
				currentChild.children[(int) index] = new Node();
				currentChild = currentChild.children[(int) index];
				
			} else {
				
				currentChild = currentChild.children[(int) index];
			}
		}
		
		//We've reached the node we want
		currentChild.nextHop = nextHop;
		
		return;
	}
	
	
	/**
	 * Find the IP address of the next hop
	 * for the given IP address.
	 * @param ip The destination IP address.
	 * @return The IP address of the next hop destination
	 * or null if a route for the address cannot be found.
	 */
	public String findNextHop(long ip) {
		
		Node currentChild = root;
		String destination = null;
		long index;
		
		for(int i = 2; i < 32; i += 2) {
			
			index = (ip >> (32 - i)) & 0x3;
			
			currentChild = currentChild.children[(int) index];
			
			//If our path ends, return the last hop address we saw
			if(currentChild == null) {
				
				return destination;
			}
			
			//If we encounter a node with a hop address,
			//keep the last one we saw
			if(currentChild.nextHop != null) {
				
				destination = currentChild.nextHop;
			}	
			
		}
		
		return destination;
	}
	
	private class Node {
		
		public Node[] children;
		public String nextHop;
		
		public Node() {
			
			children = new Node[4];
		}
	}
}
