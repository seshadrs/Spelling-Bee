package spell.correct;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

import spell.IO;
import spell.LexTree;
import spell.util.Utils;

public class TreeSpellChecker {
	
	class PartialCost{
		
		LexTree.Node node;
		int[] prevCost;
		
		public PartialCost(LexTree.Node curCharNode, int[] prevCharNodeCost)
		{
			this.node = curCharNode;
			this.prevCost = prevCharNodeCost;
		}
	}
	
	class Result{
		String wordMatch;
		int cost;
		
		public Result(LexTree.Node leaf, int matchingCost)
		{
			this.cost = matchingCost;
			String match = "";
			LexTree.Node cur = leaf;
			while (cur.getDepth()>=1)
			{
				match = cur.getVal() + match;
				cur = cur.getParent();
			}
			this.wordMatch = match;
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
	
	
	public TreeSpellChecker(String dictFile) throws FileNotFoundException 
	{
		Set<String> dict = IO.getWordDictionary(dictFile);
		this.lexTree = new LexTree(dict);
	}
	
	public PriorityQueue<Result> spellCorrect(String input)
	{
		int[] costCol;
		
		Stack<PartialCost> work = new Stack<PartialCost>();
		PriorityQueue<Result> results = new PriorityQueue<Result>(10, new ResultComparator());
		
		//Prepare first cost column
		costCol = new int[input.length()+1];
		costCol[0] = 0;
		for(int i=1; i<input.length()+1; i++)
			costCol[i] = costCol[i-1] + Math.min(insertionCost, deletionCost);
		
		//add first children to work stack, along with the first-cost column
		for(LexTree.Node n : this.lexTree.root.getChildren())
			work.add( new PartialCost(n, costCol) );
		
		PartialCost cur;
		  
		while (work.size()>0)
		{
			cur = work.pop();
			//System.out.println(work.size()+" "+cur.node.getVal());
			int[] curCost = new int[input.length()+1];
			int[] prevCost = cur.prevCost;
			
			curCost[0] = cur.node.getDepth()*Math.min(insertionCost, deletionCost);
			for(int i=1; i< input.length()+1; i++)
				curCost[i] = Utils.min(
                        curCost[i-1] + insertionCost,
                        prevCost[i] + deletionCost,
                        prevCost[i-1] + ((input.charAt(i - 1) == cur.node.getVal()) ? matchingCost : substitutionCost));
			
			ArrayList<LexTree.Node> curChildren = cur.node.getChildren();
			if(curChildren==null || curChildren.size()==0 )	//is leaf node
			{
				//System.out.println("Result "+curCost[input.length()]);
				Result r = new Result(cur.node, curCost[input.length()]);
				results.add(r);
			}
			else	//expand all child nodes
			{
				for(LexTree.Node n : cur.node.getChildren())
					work.add( new PartialCost(n, curCost) );
			}
			
		}
	
		return results;
		
	}
	

	
	public static void main(String[] args) throws FileNotFoundException
	{
		TreeSpellChecker tsc = new TreeSpellChecker("data/WordDictionary.txt");
		ArrayList<String> document = IO.getTextToCorrect("data/ErroneousDocument.txt");
		
		Utils.lineBreak();
		System.out.println("\nSPELL CORRECTED STORY:\n");
		Utils.lineBreak();
		
		int topSuggestionCount = 10;
		
		for(String incorrectWord : document)
		{
			String input = incorrectWord;
			PriorityQueue<Result> suggestions = tsc.spellCorrect(input);
			String bestMatches = "";
			for (int i=0; i< topSuggestionCount; i++)
			{
				Result suggestion = suggestions.remove();
				bestMatches += suggestion.wordMatch + " ("+ suggestion.cost+")  ";
			}
			System.out.println(input+"\t->\t"+bestMatches+"\n");
			
		}
	}

}
