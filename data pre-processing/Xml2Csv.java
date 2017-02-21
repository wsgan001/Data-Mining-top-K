import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.String;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;

/**
 * @author Z. Su
 * Convert xml to csv
 */
public class Xml2Csv 
{
	// Delimiter used in CSV file
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";
	
	// CSV file header
	private static final String FILE_HEADER_POST = "Id,TypeId,AcceptedId,OwnerId";
	private static final String FILE_HEADER_USER = "Id,UserName";
	
	// The size of blocks: the buffer is written into the csv file for every sizeOfBlock logs.
	private static int sizeOfBlock = 500000;

	// Parse the line using pattern match
    String parseLineUser(String line, String key) 
    {

        // Find the start of the pattern
        String keyPattern = key + "=\"";
        int idx = line.indexOf(keyPattern);

        // No match
        if (idx == -1) return "-1";

        // Find the closing quote at the end of the pattern
        int start = idx + keyPattern.length();

        int end = start;
        while (line.charAt(end) != '"') 
        {
            end++;
        }

        // Extract [value] from the overall String and return it
        return line.substring(start, end);
    }
    
    // Parse the lines using string comparison, slightly faster.
    String [] parseLinePost(String line, String id, String type, String accepted, String owner) 
    {
    	// Initialize the four ID's in a list
    	String [] info = {"-1", "-1", "-1", "-1"};
    	
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
		    				start += 2; // jump over the ="
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

    // Read Posts.xml and covert to csv
    void convert2CsvPosts(String xmlName, String csvName) throws FileNotFoundException, IOException 
    {
		//Write the CSV file header and check possible Exceptions
    	FileWriter fileWriter = null;		
		try 
		{
			fileWriter = new FileWriter(csvName);

			//Write the CSV file header
			fileWriter.append(FILE_HEADER_POST.toString());
			
			//Add a new line separator after the header
			fileWriter.append(NEW_LINE_SEPARATOR);
		}
	    catch (Exception e) 
	    {
	    			System.out.println("Error in CsvFileWriter !!!");
	    			e.printStackTrace();
	    }
		finally 
		{			
			try 
			{
				fileWriter.flush();
				fileWriter.close();
			} 
			catch (IOException e) 
			{
				System.out.println("Error while flushing/closing.");
                e.printStackTrace();
			}		
		}

        BufferedReader b = new BufferedReader(
                new InputStreamReader(new FileInputStream(xmlName), Charset.forName("UTF-8")));
        String line;
        int counter = 1;
        while ((line = b.readLine()) != null)
        {		
		    try 
		    {
			fileWriter = new FileWriter(csvName, true);
	            while ((line = b.readLine()) != null) 
	            {
	            	String [] postInfo = parseLinePost(line, "Id", "PostTypeId", "AcceptedAnswerId","OwnerUserId");
	            	// if a non-informatic log, postInfo[0] is "-1", filter away;
	            	// if a question is not answered, postInfo[2] is "-1", filter away;
	            	// if incorrect log, postInfo[2] and postInfo[3] are both -1, filter away;
	            	if (postInfo[0].equals("-1") || (postInfo[2].equals("-1") && postInfo[3].equals("-1")))
	            	{
	            		continue;
	            	}
	            	for (int index = 0; index < postInfo.length-1; index++)
	            	{
						fileWriter.append(postInfo[index]);
						fileWriter.append(COMMA_DELIMITER);
	                }
	                fileWriter.append(postInfo[postInfo.length-1]);
	                fileWriter.append(NEW_LINE_SEPARATOR);
	                
	                // To save memory
	                // write the buffer into the csv in multiple times
	                counter ++;
	                if (counter % sizeOfBlock == 0)
	                {
	                	break;
	                }   
	            }
		    }
		    catch (Exception e) 
		    {
	    			System.out.println("Error in CsvWriter !!!");
	    			e.printStackTrace();
	        }
		    finally 
		    {
			    try 
			    {
			    	fileWriter.flush();
			    	fileWriter.close();
			    }
			    catch (IOException e) 
			    {
			    	System.out.println("Error while flushing/closing.");
                    e.printStackTrace();
			    }			
		    }
        }
        b.close();
        System.out.println("Converted to csv sucessfully.");
    }
    
 // Read Users.xml and covert to csv
    void convert2CsvUsers(String xmlName, String csvName) throws FileNotFoundException, IOException 
    {
		//Write the CSV file header and check possible Exceptions
    	FileWriter fileWriter = null;		
		try 
		{
			fileWriter = new FileWriter(csvName);

			//Write the CSV file header
			fileWriter.append(FILE_HEADER_USER.toString());
			
			//Add a new line separator after the header
			fileWriter.append(NEW_LINE_SEPARATOR);
		}
	    catch (Exception e) 
	    {
	    			System.out.println("Error in Csv writing.");
	    			e.printStackTrace();
	    }
		finally 
		{			
			try 
			{
				fileWriter.flush();
				fileWriter.close();
			} 
			catch (IOException e) 
			{
				System.out.println("Error while flushing/closing.");
                e.printStackTrace();
			}		
		}

        BufferedReader b = new BufferedReader(
                new InputStreamReader(new FileInputStream(xmlName), Charset.forName("UTF-8")));
        String line;
        int counter = 1;
        while ((line = b.readLine()) != null)
        {		
		    try 
		    {
			fileWriter = new FileWriter(csvName, true);
	            while ((line = b.readLine()) != null) 
	            {
	            	fileWriter.append(parseLineUser(line, "Id"));
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(parseLineUser(line, "DisplayName"));
					fileWriter.append(NEW_LINE_SEPARATOR);
					
					// To save memory
	                // write the buffer into the csv in multiple times
	                counter ++;
	                if (counter % sizeOfBlock == 0)
	                {
	                	break;
	                }   
	            }
		    }
		    catch (Exception e) 
		    {
	    			System.out.println("Error in Csv writing !!!");
	    			e.printStackTrace();
	        }
		    finally 
		    {
			    try 
			    {
			    	fileWriter.flush();
			    	fileWriter.close();
			    }
			    catch (IOException e) 
			    {
			    	System.out.println("Error while flushing/closing.");
                    e.printStackTrace();
			    }			
		    }
        }
        b.close();
        System.out.println("Converted to csv sucessfully.");
    }
    
    
    public static void main(String[] args) throws FileNotFoundException, IOException 
    {
    	// Convert the Users.xml to stackUser.csv
    	Xml2Csv s = new Xml2Csv();
        long start = System.currentTimeMillis();
    	s.convert2CsvUsers("users-short.xml", "stackUser.csv");
    	long elapsedTimeMillis = System.currentTimeMillis()-start;
    	float elapsedTimeMin = elapsedTimeMillis/(60*1000F);
    	System.out.println(elapsedTimeMin);
    	
    	
    	// Convert the Posts.xml to stackPost.csv
        long start2 = System.currentTimeMillis();
    	s.convert2CsvUsers("posts-short.xml", "stackPost.csv");
    	long elapsedTimeMillis2 = System.currentTimeMillis()-start2;
    	float elapsedTimeMin2 = elapsedTimeMillis2/(60*1000F);
    	System.out.println(elapsedTimeMin2);
    }
}