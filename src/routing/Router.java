package routing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

/**
 * The main Router class which takes
 * a routing file and list of IP addresses
 * to be routed. Outputs a file named
 * results.txt which contains all of the
 * determined routes for each IP address.
 * @author Eric Ostrowski, Alex Schuitema, Austin Anderson
 *
 */
public class Router {
	
	private static Trie trie;
	
	// Expects [route file] [ip file]
	public static void main(String[] args) {
		
		trie = new Trie();
		
		if(args.length < 2) {
			System.out.println("Usage: [routing file] [address file]");
			return;
		}
		
		String routeFilename = args[0];
		String ipFilename = args[1];
		
		try {
			parseRoutes(routeFilename);
		}
		catch(NumberFormatException e) {
			e.printStackTrace();
		}
		catch(UnknownHostException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		parseIPs(ipFilename);
	}
	
	/**
	 * Parses the given routing file and constructs
	 * a Trie representation of it in the trie variable.
	 * @param filename The name of the routing file.
	 * @throws NumberFormatException If a number conversion error occurs
	 * @throws UnknownHostException If an IP address cannot be converted to decimal form
	 * @throws IOException If an error occurs while reading the file
	 */
	private static void parseRoutes(String filename) throws NumberFormatException, UnknownHostException, IOException {

		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line, lastPrefix, nextPrefix, nextHop;
		int minPathLength, nextPathLength, nextSigbits, lastSigbits;
		
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
		reader.close();
	}
	
	/**
	 * Parses the file containing a list of IPs to be
	 * routed and places determined routs into a file
	 * named results.txt in the project directory.
	 * @param filename The name of the IP file.
	 */
	private static void parseIPs(String filename){
		try {
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String ip, nextIP;
			long time1, time2, totalTime = 0;
			int numLines=0;
			Writer writer = null;
			
			writer = new BufferedWriter(new OutputStreamWriter(
			          new FileOutputStream("results.txt"), "utf-8"));
			    
			while((ip = reader.readLine()) != null) {
				numLines++;
				long address = pack(InetAddress.getByName(ip).getAddress());
				time1=System.nanoTime();
				nextIP = trie.findNextHop(address);
				time2=System.nanoTime();
				totalTime+=(time2-time1);
					if (nextIP != null){
						 writer.write(ip+"\t"+nextIP+"\n");
					}else{
						 writer.write(ip+"\tNoMatch\n");
					}
					
			}
			System.out.println("Average time spent on lookup: "+totalTime/numLines+"ns.");
			
			writer.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Converts and array of bytes into a long value.
	 * @param bytes The array of bytes to convert.
	 * @return The long value represented by the array of bytes.
	 */
	private static long pack(byte[] bytes) {
		  
		long val = 0;
		
		for (int i = 0; i < bytes.length; i++) {
			val <<= 8;
			val |= bytes[i] & 0xff;
		}
	  
		return val;
	}

}
