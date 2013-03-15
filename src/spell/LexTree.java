package spell;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Set;

import spell.correct.TreeSpellChecker;

public class LexTree {
	
	public class Node{
		
		char val;
		ArrayList<Node> children;
		Node parent;
		int depth;
		
		
		Node(char x, Node parent, int depth)
		{
			this.val = x;
			this.parent = parent;
			this.children = null;
			this.depth = depth;
		}
		
		
		public char getVal()
		{
			return this.val;
		}
		
		public Node getParent()
		{
			return this.parent;
		}
		
		public int getDepth()
		{
			return this.depth;
		}
		
		public ArrayList<Node> getChildren()
		{
			return this.children;
		}
		
		public boolean hasNonLeafChild(char c)
		{
			if (this.children==null || this.children.size()==0)
				return false;
			
			for(Node n : this.children)
			{
				if (n.val==c && (n.children!=null && n.children.size()!=0))
					return true;
			}
			
			return false;
		}
		
		public Node addChild(char c, Node cur)
		{
			Node child = new Node(c, cur, cur.depth+1);
			
			if (this.children==null)
				this.children = new ArrayList<LexTree.Node>();
			
			this.children.add(child);
			return child;
		}
		
		public Node getChild(char c)
		{
			for(Node n : this.children)
				if (n.val==c)
					return n;
			
			return null;
		}
		
	}
	
	public Node root;
	public static char rootChar = '*';
	public static boolean verbose = false;
	
	public LexTree(Set<String> dictionary)
	{
		this.root = new Node(rootChar, null,0);
		this.populate(dictionary);
	}
	
	public LexTree(Set<String> dictionary, boolean verbosity)
	{
		this.root = new Node(rootChar, null,0);
		this.verbose = verbosity;
		this.populate(dictionary);
	}
	
	private void populate(Set<String> dictionary)
	{
		int len,i;
		char c;
		Node cur;
		
		for(String w : dictionary)	//for every word in dict
		{
			
			
			//move cur pointer to root of tree
			cur = this.root;
			
			len = w.length();
			
			for(i=0; i<len-1; i++)	//from 0 to last but one char
			{
				c = w.charAt(i);
				if (cur.hasNonLeafChild(c))
				{
					cur = cur.getChild(c);
					if (this.verbose)
						System.out.print("-");
					continue;
					
				}
				else
				{	if (this.verbose)
						System.out.print("+");
					cur = cur.addChild(c,cur);
				}
			}
			
			//add last character in word
			if (this.verbose)
				System.out.print("+");
			cur = cur.addChild(w.charAt(len-1),cur);
			if (this.verbose)
				System.out.println("\t"+w);
			
			
		}
		
	}
	
	
	public static void main(String[] args) throws FileNotFoundException
	{
		
		Set<String> dict = IO.getWordDictionary("data/WordDictionary.txt");
		LexTree t = new LexTree(dict,true);
		
	}
	

}
