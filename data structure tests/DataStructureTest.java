import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.commons.collections4.bidimap.TreeBidiMap;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * @author Z. Su
 * Thanks to the following PatricialTrie manual
 * https://commons.apache.org/proper/commons-collections/apidocs/org/apache/commons/collections4/
 */
public class DataStructureTest 
{	
	//Delimiter used in CSV file
	private static final String COMMA_DELIMITER = ",";
	private static final int HASHSET_SIZE = 12300000;
	private static final int ARRAY_SIZE = 42000000;
	
	public static void read2Memory(String fileName) 
	{
		// Some of the tested data structures are the following:
		
		//PatriciaTrie<Integer> acceptedIds = new PatriciaTrie<Integer>();
		//TrieList acceptedIds = new TrieList();
		//TreeMap<Integer, Integer> acceptedIds = new TreeMap<Integer, Integer>();
		//TreeSet<Integer> acceptedIds = new TreeSet<>();
		//ArrayList<Trie> acceptedIds = new ArrayList<Trie>(10);
		//LinkedHashSet<Integer> acceptedIds = new LinkedHashSet<Integer>();
		HashSet<Integer> acceptedIds = new HashSet<Integer>();
		BufferedReader fileReader = null;
     
        try 
        {
            String line = "";
            fileReader = new BufferedReader(new FileReader(fileName));
            
            //Read the CSV file header to skip it
            fileReader.readLine();
            int counter = 0;
            Runtime rt = Runtime.getRuntime();
            long total = rt.totalMemory();
            while ((line = fileReader.readLine()) != null) 
            {
                String[] pp = line.split(COMMA_DELIMITER);
                if (pp[1].equals("1"))
                {
                	acceptedIds.add(Integer.parseInt(pp[2]));
                	counter ++;
                }
                if (counter % 500000 == 0){
                	System.out.print("Number of posts saved is:");
                	System.out.print('\t');
                    System.out.println(counter);
                    long free = rt.freeMemory();
                    long used = total - free;
                    System.out.print("Memory used is:");
                	System.out.print('\t');
                    System.out.println(used);
                    System.out.println();                    
                }
            }       
        } 
        catch (Exception e) {
        	System.out.println("Error in CsvReader !!!");
            e.printStackTrace();
        } 
        finally 
        {
            try 
            {
                fileReader.close();
            } 
            catch (IOException e) 
            {
            	System.out.println("Error while closing.");
                e.printStackTrace();
            }
        }
	}
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException 
	{
        long start = System.currentTimeMillis();
        DataStructureTest.read2Memory("stackPost.csv");
    	long elapsedTimeMillis = System.currentTimeMillis()-start;
    	float elapsedTimeSecond = elapsedTimeMillis/(1000F);
    	System.out.print("The time used in second is: ");
    	System.out.println(elapsedTimeSecond);
	}
}