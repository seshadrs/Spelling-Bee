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
import spell.trellis.VectorLevenshtein;
import spell.trellis.PrunedLevenshtein.PruningType;
import spell.util.Utils;
import java.util.Scanner;

public class VectorSpellAligner {
	
	
	
	public static void main(String args[]) throws FileNotFoundException
	{
		Set<String> dictionary = IO.getWordDictionary("data/WordDictionary.txt");
		ArrayList<String> document = IO.getTextToCorrect("data/ErroneousDocument.txt");
		ArrayList<String> cleanDocument = IO.getTextToCorrect("data/CleanDocument.txt");
		
		PrunedLevenshtein l = new PrunedLevenshtein();
		int lowestCost;
		String bestMatch;
		
		ArrayList<String> correctedDocument = new ArrayList<String>(); 
		
		System.out.println("GENERATING SPELLING CORRECTIONS ...\n");
		
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
			
			correctedDocument.add(bestMatch);
		
		}
		
		System.out.println("ALIGNING SPELLING CORRECTED TEXT WITH CLEAN TEXT\n");
		VectorLevenshtein vl = new VectorLevenshtein();
		vl.distance(cleanDocument, correctedDocument);
		
	}

}
