package spell.correct;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

import spell.IO;
import spell.LexTree;
import spell.util.Utils;

public class SegmentingSpellChecker {
	
	class PartialCost{
		
		LexTree.Node node;
		int[] prevCost;
		String prefix;
		int length;
		
		public PartialCost(LexTree.Node curCharNode, int[] prevCharNodeCost, String prefixStr)
		{
			this.node = curCharNode;
			this.prevCost = prevCharNodeCost;
			this.prefix = prefixStr;
			
			int len =0;
			for(int i=0; i< prefixStr.length(); i++)
				if (prefixStr.charAt(i)!='*')
					len++;
			this.length = len;
			//System.out.println("plen = "+len);
		}
	}
	
	class Result{
		String match;
		int cost;
		
		public Result(String match, int matchingCost)
		{
			this.cost = matchingCost;
			this.match= match;
		}
	}
	
	class ResultComparator implements Comparator<Result>{		
		@Override
		public int compare(Result a, Result b)
		{
			return a.cost-b.cost;
		}
	}
	
	
	LexTree lexTree;
	public int insertionCost=1;
	public int deletionCost=1;
	public int matchingCost=0;
	public int substitutionCost=1;
	
	
	public SegmentingSpellChecker (String dictFile) throws FileNotFoundException 
	{
		Set<String> dict = IO.getWordDictionary(dictFile);
		this.lexTree = new LexTree(dict);
	}
	
	public PriorityQueue<Result> spellCorrect(String input, int relPruneCost)
	{
		int[] costCol;
		
		int inputLen = input.length();
		
		Stack<PartialCost> work = new Stack<PartialCost>();
		PriorityQueue<Result> results = new PriorityQueue<Result>(10, new ResultComparator());
		
		int tmp;
		
		//Prepare first cost column
		costCol = new int[input.length()+1];
		costCol[0] = 0;
		for(int i=1; i<input.length()+1; i++)
		{
			if (costCol[i-1]==Integer.MAX_VALUE)
				tmp =Integer.MAX_VALUE;
			else
				tmp = costCol[i-1] + Math.min(insertionCost, deletionCost);
			costCol[i] = (tmp > relPruneCost)? Integer.MAX_VALUE: tmp;
		}
		
		//add first children to work stack, along with the first-cost column
		for(LexTree.Node n : this.lexTree.root.getChildren())
			work.add( new PartialCost(n, costCol, "") );
		
		PartialCost cur;
		int bestPrevCost =0;
		int insCost, delCost, diagCost;
		int bestResCost=Integer.MAX_VALUE;
		  
		while (work.size()>0)
		{
			cur = work.pop();
			
			//if popped the root node
			if(cur.node.getVal() == this.lexTree.rootChar)
			{
				cur.prefix = cur.prefix  +cur.node.getVal();
				//add root's children to work
				for(LexTree.Node n : this.lexTree.root.getChildren())
					work.add( new PartialCost(n, cur.prevCost, cur.prefix) ); 
				continue;
			}
			
			int[] curCost = new int[input.length()+1];
			int[] prevCost = cur.prevCost;
			
			//first cell of cost col
			tmp = (cur.length+1)*Math.min(insertionCost, deletionCost);
			if(tmp>bestPrevCost+relPruneCost)
				curCost[0] = Integer.MAX_VALUE;
			else
				curCost[0] = tmp;
			
					
			//all other cells 
			for(int i=1; i< input.length()+1; i++)
			{
				insCost = (curCost[i-1]==Integer.MAX_VALUE)? Integer.MAX_VALUE: curCost[i-1] + insertionCost;
				delCost = (prevCost[i]==Integer.MAX_VALUE)? Integer.MAX_VALUE: prevCost[i] + deletionCost;
				diagCost = (prevCost[i-1]==Integer.MAX_VALUE)? Integer.MAX_VALUE : prevCost[i-1] + ((input.charAt(i - 1) == cur.node.getVal()) ? matchingCost : substitutionCost);
						
				tmp = Utils.min(
                        insCost,
                        delCost,
                        diagCost);
				
				if(tmp>=bestPrevCost+relPruneCost)
					curCost[i] = Integer.MAX_VALUE;
				else
					curCost[i] = tmp;
			}
			
			bestPrevCost = Integer.MAX_VALUE;
			for(int i=0; i<input.length(); i++)
				if (curCost [i] < bestPrevCost)
					bestPrevCost = curCost[i];
			
			//prune if needed
			if(bestPrevCost == Integer.MAX_VALUE)
				continue;
			if(bestPrevCost > bestResCost)
				continue;
			if(cur.length+1>1.2*inputLen)
				continue;
			
			ArrayList<LexTree.Node> curChildren = cur.node.getChildren();
			//if leaf node
			if(curChildren==null || curChildren.size()==0 )	
			{
				//consider as result if length bounds satisfied
				if (cur.length+1 > 0.8*inputLen && cur.length+1<1.2*inputLen)
				{	int resultCost = curCost[input.length()];
					
					if(resultCost!=Integer.MAX_VALUE)
					{
						if(resultCost <= bestResCost)
						{
							System.out.println("Result "+ cur.prefix+cur.node.getVal() + "\t" + curCost[input.length()]);
							Result r = new Result(cur.prefix+cur.node.getVal(), curCost[input.length()]);
							results.add(r);
						}
						
						if(resultCost < bestResCost)
							bestResCost = resultCost;
					}
				}
				
				//jump to root node, add to work
				work.add( new PartialCost(this.lexTree.root, curCost, cur.prefix+cur.node.getVal()) );
				
			}
			else
			{
				//expand all child nodes
				for(LexTree.Node n : cur.node.getChildren())
					work.add( new PartialCost(n, curCost, cur.prefix+cur.node.getVal()) );
			}
		
			
		}
	
		return results;
		
	}
	

	
	public static void main(String[] args) throws FileNotFoundException
	{
		SegmentingSpellChecker  tsc = new SegmentingSpellChecker("data/WordDictionary.txt");
		
		int topSuggestionCount = 10;
		Scanner s = new Scanner(System.in);
		
		System.out.print("Enter string to segment and spell correct : ");
		String input = s.next().trim();
		
		while(!(input.compareTo("")==0))
		{
			PriorityQueue<Result> suggestions = tsc.spellCorrect(input,5);
			String bestMatches = "";
			for (int i=0; i< topSuggestionCount; i++)
			{
				if (suggestions.size()>0)
				{
					Result suggestion = suggestions.remove();
					bestMatches += suggestion.match + " ("+ suggestion.cost+")  ";
				}
			}
			System.out.println(input+"\t->\t"+bestMatches+"\n");
			
			System.out.print("\nEnter string to segment and spell correct : ");
			input = s.next().trim();
		}
		
	}

}
