package spell.correct;


import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.*;

import spell.trellis.PrunedLevenshtein;
import spell.trellis.PrunedLevenshtein.PruningType;
import spell.util.Utils;
import java.util.Scanner;

public class WordMatch {
	
	
	
	public static void main(String args[])
	{
		Scanner s = new Scanner(System.in);
		String templates, input, pruning ;
		int pruningValue;
		
		System.out.print("ENTER TEMPLATES SEPARATED BY COMMAS: ");
		templates = s.next();
		System.out.println();
		
		
		System.out.print("ENTER INPUT: ");
		input = s.next();
		System.out.println();
		
		System.out.print("PRUNING max/bw/no ?: ");
		pruning = s.next();
		System.out.println();
		
		PrunedLevenshtein l = new PrunedLevenshtein();
		int lowestCost = Integer.MAX_VALUE;
		String bestMatch="";
		
		if (pruning.contains("max") || pruning.contains("bw"))
		{
			System.out.print("PRUNING Value(int): ");
			pruningValue = Integer.parseInt(s.next());
			System.out.println();
			
			for(String template : templates.split(","))
			{
				System.out.println();
				int cost =l.distance(input, template,(pruning.contains("max"))? PruningType.MAX_DISTANCE:PruningType.BEAM_WIDTH, pruningValue);
				System.out.println("Comparing "+template+"\tcost="+((cost==Integer.MAX_VALUE)?"Infinity":cost));
				if (cost < lowestCost)
					{
						bestMatch = template;
						lowestCost = cost;
					}
			}
			
			if (bestMatch!="")
			{
				System.out.println("\nBEST MATCH: "+bestMatch+"\n");
				l.showDistance(input, bestMatch, (pruning.contains("max"))? PruningType.MAX_DISTANCE:PruningType.BEAM_WIDTH, pruningValue);
			}
			else
				System.out.println("\nNO MATCH WAS FOUND\n");
			
		}
		else
		{
			for(String template : templates.split(","))
			{
				//no pruning
				System.out.println();
				int cost=l.distance(input, template, PruningType.MAX_DISTANCE, Integer.MAX_VALUE);
				System.out.println("Comparing "+template+"\tcost="+((cost==Integer.MAX_VALUE)?"Infinity":cost));
				if (cost < lowestCost)
					{
						bestMatch = template;
						lowestCost = cost;
					}
			}
			
			if(bestMatch!="")
			{	
				System.out.println("\nBEST MATCH: "+bestMatch+"\n");
				l.showDistance(input, bestMatch, PruningType.MAX_DISTANCE, Integer.MAX_VALUE);
			}
			else
				System.out.println("\nNO MATCH WAS FOUND\n");
		}
		
		
		
	}

}
