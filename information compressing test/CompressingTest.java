import java.lang.String;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author Z. Su
 * Comparing loading the whole Posts.xml into memory as
 * int data structure and Strings.
 */


public class CompressingTest {

    public CompressingTest() {}

// Parse posts and save as lists of 4 integers
    int[] parseLineInteger(String line, String id, String type, String accepted, String owner) 
    {
    	int[] info = {-1, -1, -1, -1};
    	
        // First see if a valid log via the ID
        String idPattern = id + "=\"";
        int idx = line.indexOf(idPattern);

        // No match
        if (idx == -1) return info;

        // Find the closing quote at the end of the pattern
        int start = idx + id.length() + 2; // 2 is for ="

        int end = start;
        while (line.charAt(end) != '"') 
        {
            end++;
        }

        // The Id as an integer
        int num = Integer.parseInt(line.substring(start, end));
		info[0] = num;

		// Find typeId
		while (start < line.length()-1)
		{
			start ++;
			if (line.charAt(start) == '=' && line.charAt(start+1) == '"') 
			{
    			if (line.substring(start-type.length(), start).equals(type))
    			{
    				start += 2; // jump over ="
    		        end = start;
    		        while (end < line.length() && line.charAt(end) != '"') 
    		        {
    		            end++;
    		        }
    		        num = Integer.parseInt(line.substring(start, end));
    				info[1] = num;
    				start = end;
    				break;
    			}
    		}
		}
		
		//If type-1, find the AcceptedPostId
		if (info[1] == 1)
		{
			while (start < line.length()-1)
			{
				start ++;
				if (line.charAt(start) == '=' && line.charAt(start+1) == '"') 
				{
	    			if (line.substring(start-accepted.length(), start).equals(accepted))
	    			{
	    				start += 2; // jump over ="
	    		        end = start;
	    		        while (end < line.length() && line.charAt(end) != '"') 
	    		        {
	    		            end++;
	    		        }
	    		        num = Integer.parseInt(line.substring(start, end));
	    				info[2] = num;
	    				start = end;
	    				return info;
	    			}
	    		}
			}
		}
		
		// If type-2, find the owerId
		else
		{
				while (start < line.length()-1)
				{
					start ++;
					if (line.charAt(start) == '=' && line.charAt(start+1) == '"') 
					{
		    			if (line.substring(start-owner.length(), start).equals(owner))
		    			{
		    				start += 2; // jump over ="
		    		        end = start;
		    		        while (end < line.length() && line.charAt(end) != '"') 
		    		        {
		    		            end++;
		    		        }
		    		        num = Integer.parseInt(line.substring(start, end));
		    				info[3] = num;
		    				start = end;
		    				return info;
		    			}
		    		}
				}
			}
        return info;
    }

    
 // Parse posts and save as lists of 4 strings
    String[] parseLineString(String line, String id, String type, String accepted, String owner) 
    {
    	String[] info = {"-1", "-1", "-1", "-1"};
    	
        // First see if a valid log via the ID
        String idPattern = id + "=\"";
        int idx = line.indexOf(idPattern);

        // No match
        if (idx == -1) return info;

        // Find the closing quote at the end of the pattern
        int start = idx + id.length() + 2; // 2 is for ="

        int end = start;
        while (line.charAt(end) != '"') 
        {
            end++;
        }

        // The Id as a string
        info[0] = line.substring(start, end);

		// Find typeId
		while (start < line.length()-1)
		{
			start ++;
			if (line.charAt(start) == '=' && line.charAt(start+1) == '"') 
			{
    			if (line.substring(start-type.length(), start).equals(type))
    			{
    				start += 2; // jump over ="
    		        end = start;
    		        while (end < line.length() && line.charAt(end) != '"') 
    		        {
    		            end++;
    		        }
    		        info[1] = line.substring(start, end);
    				start = end;
    				break;
    			}
    		}
		}
		
		//If type-1, find the AcceptedPostId
		if (info[1].equals("1"))
		{
			while (start < line.length()-1)
			{
				start ++;
				if (line.charAt(start) == '=' && line.charAt(start+1) == '"') 
				{
	    			if (line.substring(start-accepted.length(), start).equals(accepted))
	    			{
	    				start += 2; // jump over ="
	    		        end = start;
	    		        while (end < line.length() && line.charAt(end) != '"') 
	    		        {
	    		            end++;
	    		        }
	    		        info[2] = line.substring(start, end);
	    				start = end;
	    				return info;
	    			}
	    		}
			}
		}
		
		// If type-2, find the owerId
		else
		{
				while (start < line.length()-1)
				{
					start ++;
					if (line.charAt(start) == '=' && line.charAt(start+1) == '"') 
					{
		    			if (line.substring(start-owner.length(), start).equals(owner))
		    			{
		    				start += 2; // jump over ="
		    		        end = start;
		    		        while (end < line.length() && line.charAt(end) != '"') 
		    		        {
		    		            end++;
		    		        }
		    		        info[3] = line.substring(start, end);
		    				start = end;
		    				return info;
		    			}
		    		}
				}
			}
        return info;
    }

// Load posts as lists of integers, monitor the time meanwhile   
    public void loadPostsAsInteger(String filename) throws FileNotFoundException, IOException 
    {
    	// Keep track of all posts
        ArrayList<int []> posts = new ArrayList<>();
    	BufferedReader b = new BufferedReader(
            new InputStreamReader(new FileInputStream(filename), Charset.forName("UTF-8")));
        String line;
        int counter = 0;
        long start = System.currentTimeMillis();
        while ((line = b.readLine()) != null) 
        {
        	int[] postInfo = parseLineInteger(line, "Id", "PostTypeId", "AcceptedAnswerId","OwnerUserId"); 
            posts.add(postInfo);
            counter ++;
            if (counter % 100000==0)
            {
            	long elapsedTimeMillis = System.currentTimeMillis()-start;
            	float elapsedTimeSecond = elapsedTimeMillis/(1000F);
            	System.out.println(elapsedTimeSecond);
            }
        }
        b.close();
        System.out.println("Data is completely loaded.");
    }
    
    
 // Load posts as lists of strings, monitor the time meanwhile   
    public void loadPostsAsString(String filename) throws FileNotFoundException, IOException 
    {
    	// Keep track of all posts
        ArrayList<String []> posts = new ArrayList<>();
    	BufferedReader b = new BufferedReader(
            new InputStreamReader(new FileInputStream(filename), Charset.forName("UTF-8")));
        String line;
        int counter = 0;
        long start = System.currentTimeMillis();
        while ((line = b.readLine()) != null) 
        {
        	String[] postInfo = parseLineString(line, "Id", "PostTypeId", "AcceptedAnswerId","OwnerUserId"); 
            posts.add(postInfo);
            counter ++;
            if (counter % 100000==0)
            {
            	long elapsedTimeMillis = System.currentTimeMillis()-start;
            	float elapsedTimeSecond = elapsedTimeMillis/(1000F);
            	System.out.println(elapsedTimeSecond);
            }
        }
        b.close();
        System.out.println("Data is completely loaded.");
    }

    public static void main(String[] args) throws FileNotFoundException, IOException 
    {
    	CompressingTest test = new CompressingTest();
    	System.out.println("Load and save posts as integers:");
        test.loadPostsAsInteger("Posts.xml");
        
    	System.out.println("Load and save posts as strings:");
        test.loadPostsAsString("Posts.xml");
    }
}

