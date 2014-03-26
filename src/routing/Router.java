package routing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class Router {
	
	// Expects [route file] [ip file]
	public static void main(String[] args) {
		
		if(args.length < 2) {
			System.out.println("Usage: [routing file] [address file]");
		}
		
		String routeFilename = args[0];
		//String ipFilename = args[1];
		
		parseRoutes(routeFilename);
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
			int minPathLength, nextPathLength;
			
			minPathLength = 0;
			lastPrefix = "";
			nextHop = "";
			
			
			while((line = reader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line, "|");
				
				// Look through duplicate prefixes for entry with shortest AS path
				if(!(nextPrefix = tokenizer.nextToken()).equals(lastPrefix)) {
					// Save best entry for previous prefix before moving on
					if(!lastPrefix.equals("")) {
						//TODO Insert it into trie
						System.out.println("Pefix: " + lastPrefix + "\n" + "Path Length: " + minPathLength + "\n" + "IP: " + nextHop);
					}
					
					lastPrefix = nextPrefix;
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
			
			//TODO Insert last prefix entry into trie
			System.out.println("Pefix: " + lastPrefix + "\n" + "Path Length: " + minPathLength + "\n" + "IP: " + nextHop);
			
			
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
