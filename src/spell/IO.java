package spell;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import com.sun.tools.javac.util.List;

public class IO {

	public static Set<String> getWordDictionary(String filePath) throws FileNotFoundException
	{
		//expects a word per line
		
		Set<String> dict = new HashSet<String>();
		File file = new File(filePath);
        Scanner scanner = new Scanner(file);
        String word;
        while (scanner.hasNextLine()) 
        {
        	word = scanner.nextLine().trim().toLowerCase();
        	if (word!="")
        		dict.add(word);
        	
        }
        scanner.close();
        
        System.out.println("Loaded "+dict.size()+" words into dictionary\n");
        return dict;
	}
	
	
	public static ArrayList<String> getTextToCorrect(String filePath) throws FileNotFoundException
	{
		ArrayList<String> doc = new ArrayList<String>();
		File file = new File(filePath);
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) 
        {
        	for(String word : scanner.nextLine().trim().toLowerCase().split(" "))
        	{
        		word = word.replace(".", "").replace(",", "").replace("!", "").replace("?", "").replace("\"", "");
        		if (word!="")
	        		doc.add(word);
        	}
        	
        }
        scanner.close();
        
        System.out.println("Loaded "+doc.size()+" words from document\n");
        return doc;
		
	}
	
}
