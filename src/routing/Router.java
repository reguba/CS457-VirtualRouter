package routing;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.StringTokenizer;

public class Router {
	
	private static Trie trie;
	
	// Expects [route file] [ip file]
	public static void main(String[] args) {
		
		trie = new Trie();
		
		if(args.length < 2) {
			System.out.println("Usage: [routing file] [address file]");
		}
		
		String routeFilename = args[0];
		//String ipFilename = args[1];
		
		parseRoutes(routeFilename);
		
		trie.findNextHop(881256926L);
	}
	
	private static void parseRoutes(String filename) {
		// Read line, get prefix
		// count AS paths and hold first prefix entry as 'best'
		// read next line, if prefix is the same, compare AS paths
			// if new line has shorter AS path, save it as 'best'
		// repeat until a line with new prefix is encountered
		// store best entry for prefix
		// repeat above for next prefix
		
		
		
		try {
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line, lastPrefix, nextPrefix, nextHop;
			int minPathLength, nextPathLength, nextSigbits, lastSigbits, address;
			
			minPathLength = 0;
			lastPrefix = "";
			lastSigbits = 0;
			nextHop = "";
			
			
			while((line = reader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line, "/|");
				
				nextPrefix = tokenizer.nextToken();
				nextSigbits = Integer.parseInt(tokenizer.nextToken());
				
				// Look through duplicate prefixes for entry with shortest AS path
				if(!nextPrefix.equals(lastPrefix) || nextSigbits != lastSigbits) {
					// Save best entry for previous prefix before moving on
					if(!lastPrefix.equals("")) {
						
						trie.Insert(pack(InetAddress.getByName(lastPrefix).getAddress()), lastSigbits, nextHop);
					}
					
					lastPrefix = nextPrefix;
					lastSigbits = nextSigbits;
					minPathLength = tokenizer.nextToken().split(" ").length;
					nextHop = tokenizer.nextToken();
					
				} else {
					
					// Determine if entry is a shorter path
					if((nextPathLength = tokenizer.nextToken().split(" ").length) < minPathLength) {
						minPathLength = nextPathLength;
						nextHop = tokenizer.nextToken();
					}
					
				}
				
			}
			
			trie.Insert(pack(InetAddress.getByName(lastPrefix).getAddress()), lastSigbits, nextHop);
			
			
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static long pack(byte[] bytes) {
		  
		long val = 0;
		
		for (int i = 0; i < bytes.length; i++) {
			val <<= 8;
			val |= bytes[i] & 0xff;
		}
	  
		return val;
	}

}
