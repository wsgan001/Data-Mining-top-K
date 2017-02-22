import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;


/**
 * @author Z. Su
 * @param <User>
 * Find the top-k users who have most accepted posts.
 */
public class TopKAccepted 
{	
	
	private static final int TOP_K = 10;
	
	//Delimiter used in CSV file
	private static final String COMMA_DELIMITER = ",";
	private static final int ANSWER_SIZE = 42000000;
	private static final int USER_SIZE = 8000000;

    public TopKAccepted() 
    {
    }
	
    // Build User class of small size for most data processing
    // Two int fields are enough.
	public class User 
	{
        int Id;
        int acceptedCount;
        
        public User(int Id, int acceptedCount)
        {
            this.Id = Id;
            this.acceptedCount = acceptedCount;
        }
	}
	
	// Build a complete User class for final result display.
	// The memory expensive displayName the extra field.
    public class UserFull 
    {
           int Id;
           int acceptedCount;
           String displayName;
           
        public UserFull(int Id, int acceptedCount, String displayName)
        {
            this.Id = Id;
            this.acceptedCount = acceptedCount;
            this.displayName = displayName;
        }   
    }
	
    // Identify the accepted posts by looping the PostCsv file.
    // isAccepted[id] = true iif post of postId is id.
	boolean [] isAccepted = new boolean[ANSWER_SIZE];
	void findAcceptedPostIds(String fileName) throws FileNotFoundException, IOException 
	{
		BufferedReader fileReader = null;   
        try 
        {
            String line = "";          
            //Create the file reader
            fileReader = new BufferedReader(new FileReader(fileName));
            
            //Read the CSV file header to skip it
            fileReader.readLine();
            
            //Read the file line by line starting from the second line
            while ((line = fileReader.readLine()) != null) 
            {
                String[] pp = line.split(COMMA_DELIMITER);
                if (pp[1].equals("1"))
                {
                	isAccepted[Integer.parseInt(pp[2])] = true;
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
	

	// Count the accepted posts of each userId by looping the postCsv again.
	// acceptedFrequencyOfIds[userId] +=1 iif 
	// post[postId] is by userId && is accepted, denoted by isAccepted[postId] 
	int [] acceptedFrequencyOfIds = new int[USER_SIZE];
	void findAcceptedPostOwnerIds(String fileName) throws FileNotFoundException, IOException 
	{
		
		BufferedReader fileReader = null;
        try 
        {
            String line = "";          
            fileReader = new BufferedReader(new FileReader(fileName));
            
            //Read the CSV file header to skip it
            fileReader.readLine();
            while ((line = fileReader.readLine()) != null) 
            {
                String[] pp = line.split(COMMA_DELIMITER);

                if (pp[1].equals("2") && isAccepted[Integer.parseInt(pp[0])])
                {
                	acceptedFrequencyOfIds[Integer.parseInt(pp[3])] ++;
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
	
	
	// Build a comparator for the priority queue<User>
	// Decreasing according to user.acceptedCount.
    static Comparator<User> compFrequency = new Comparator<User>() 
    {
        public int compare(User p1, User p2)
        {
            return p2.acceptedCount - p1.acceptedCount;
        }
    };
    
	// Build a comparator for the priority queue<UserFull>
	// Decreasing according to user.acceptedCount.
    static Comparator<UserFull> compFrequencyFull = new Comparator<UserFull>() 
    {
        public int compare(UserFull p1, UserFull p2)
        {
            return p2.acceptedCount - p1.acceptedCount;
        }
    };
    
	// Build a comparator for the priority queue<User>
	// Decreasing according to user.Id.
    static Comparator<User> compId = new Comparator<User>() 
    {
        public int compare(User p1, User p2)
        {
            return p1.Id - p2.Id;
        }
    };
    
    
    // Push users in a max heap, according to user.accetedCount
    PriorityQueue<User> topKAcceptedHeap = new PriorityQueue<User>(1000, compFrequency);
    public void findTopKAcceptedIds(String fileName) throws FileNotFoundException, IOException 
	{
    	PriorityQueue<User> pqFinal = new PriorityQueue<User>(1000, compFrequency);
    	int counter = 0;
    	PriorityQueue<User> pq = new PriorityQueue<User>(50001, compFrequency);
		for (int i = 1; i<acceptedFrequencyOfIds.length;i++)
		{			
			if (acceptedFrequencyOfIds[i]>0) // efficient pruning
			{
				pq.add(new User(i, acceptedFrequencyOfIds[i]));
				counter ++;
			}
			
			// Divide the heap-build process
			// when priority queue pq is 50000 long, take the top-k heads to pqFinal
			// and discard the rest as they can not be in the final top-k queue.
	    	if (counter % 50000 == 0)
	    	{
	    		User u = null;
	    		int k = 0;
	    		while (k < TOP_K && (u=pq.poll()) != null)
	    		{
	    			pqFinal.add(u);
	    			k ++;
	    		}
	    		pq.clear(); // dump the rest of the queue
	    	}
	    	
	    	// keep the top-k element of pqFinal
    		User u = null;
    		while (topKAcceptedHeap.size()<TOP_K && (u=pqFinal.poll()) != null)
    		{
    			topKAcceptedHeap.add(u);
    		}
    		pqFinal.clear();
		}
	}	
    
	
    // topKAcceptedHeap keeps the Id and acceptedCount of the top-k users
    // Using the k Id's, we loop through the UserCsv to find the k associated displayNames.
    // Further, we build k UserFull objects to save these k users as topAcceptedName.
    PriorityQueue<UserFull> topKAcceptedNames = new PriorityQueue<UserFull>(TOP_K, compFrequencyFull);
    public void findTopKAcceptedNames(String fileName) throws FileNotFoundException, IOException 
	{	
    	// convert to another queue where user with smaller Id is the heap head
    	// such that we only need to loop through the UserCsv in one pass;
    	PriorityQueue<User> topKIds = new PriorityQueue<User>(TOP_K, compId);	
    	for (User u:topKAcceptedHeap)
    	{
    		topKIds.add(u);
    	}
    	
    	// Find the associated displayNames of Id's in order.
    	BufferedReader fileReader = null;   
        try 
        {
            String line = "";          
            fileReader = new BufferedReader(new FileReader(fileName));
        	User u = null;
        	fileReader.readLine();
        	while ((u=topKIds.poll()) != null)
        	{
        		String idString = String.valueOf(u.Id);
        		 while ((line = fileReader.readLine()) != null) 
                 {
                     String[] pp = line.split(COMMA_DELIMITER);
                     
                     if (pp[0].equals(idString))
                     {
                    	 // build a UserFull object for this user and save into a priority heap.
                     	UserFull userFinal = new UserFull(u.Id, u.acceptedCount, pp[1]);
                     	topKAcceptedNames.add(userFinal);
                     	break;
                     }
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
	
    // Run the files to find the top-k users in the heap, topKAcceptedNames
    // Print them out.
    public void runTopKAccepted(String postFileName, String userFileName) throws FileNotFoundException, IOException 
    {
    	findAcceptedPostIds(postFileName);
    	findAcceptedPostOwnerIds(postFileName);
    	findTopKAcceptedIds(postFileName);
    	findTopKAcceptedNames(userFileName);
    	
    	System.out.format("%8s\t%8s\t%8s%n", "Id", "DisplayName","Accepted Counts");

		UserFull u = null;
    	while ((u=topKAcceptedNames.poll()) != null)
    	{
    		System.out.format("%8d\t%8s\t%,8d%n", u.Id, u.displayName,u.acceptedCount);
    	}

    }
    
	public static void main(String[] args) throws FileNotFoundException, IOException 
	{
        long start = System.currentTimeMillis();
        TopKAccepted s = new TopKAccepted();
        s.runTopKAccepted("stackPost.csv", "stackUser.csv");
    	long elapsedTimeMillis = System.currentTimeMillis()-start;
    	float elapsedTimeSecond = elapsedTimeMillis/(1000F);
    	System.out.print("The time used to find top-10 users who have most accepted posts in second is: ");
    	System.out.println(elapsedTimeSecond); 
    }
}