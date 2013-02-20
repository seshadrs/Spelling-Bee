package spell.correct;


import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.*;

import spell.IO;
import spell.trellis.PrunedLevenshtein;
import spell.trellis.PrunedLevenshtein.PruningType;
import spell.util.Utils;
import java.util.Scanner;

public class SpellChecker {
	
	
	
	public static void main(String args[]) throws FileNotFoundException
	{
		Set<String> dictionary = IO.getWordDictionary("data/WordDictionary.txt");
		ArrayList<String> document = IO.getTextToCorrect("data/ErroneousDocument.txt");
		
		PrunedLevenshtein l = new PrunedLevenshtein();
		int lowestCost;
		String bestMatch;
		
		Utils.lineBreak();
		System.out.println("\nSPELL CORRECTED STORY:\n");
		Utils.lineBreak();
		
		for(String incorrectWord : document)
		{
			String input = incorrectWord;
			lowestCost = Integer.MAX_VALUE;
			bestMatch="";
		
			for(String template : dictionary)
			{
				//no pruning
				int cost=l.distance(input, template, PruningType.MAX_DISTANCE, Integer.MAX_VALUE);
				if (cost < lowestCost)
					{
						bestMatch = template;
						lowestCost = cost;
					}
			}
			
			System.out.println(input+"\t->\t"+bestMatch);
		
		}
	}

}
