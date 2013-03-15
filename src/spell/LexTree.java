package spell;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Set;

public class LexTree {
	
	public class Node{
		
		char val;
		ArrayList<Node> children;
		int[] costCol;	//cost column in trellis, for this char
		Node parent;
		
		
		Node(char x, Node parent)
		{
			this.val = x;
			this.parent = parent;
			this.children = null;
		}
		
		
		public boolean hasChild(char c)
		{
			if (this.children==null || this.children.size()==0)
				return false;
			
			for(Node n : this.children)
			{
				if (n.val==c)
					return true;
			}
			
			return false;
		}
		
		public Node addChild(char c, Node cur)
		{
			Node child = new Node(c, cur);
			
			if (this.children==null)
				this.children = new ArrayList<LexTree.Node>();
			
			this.children.add(child);
			return child;
		}
		
		public Node getChild(char c)
		{
			Node child;
			
			for(Node n : this.children)
				if (n.val==c)
					return n;
			
			return null;
		}
		
	}
	
	Node root;
	
	LexTree()
	{
		this.root = new Node('*', null);
	}
	
	public void populate(Set<String> dictionary)
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
				if (cur.hasChild(c))
				{
					cur = cur.getChild(c);
					System.out.print("-");
					continue;
					
				}
				else
				{
					System.out.print("+");
					cur = cur.addChild(c,cur);
				}
			}
			
			//add last character in word
			System.out.print("+");
			cur.addChild(w.charAt(len-1),cur);
			
			System.out.println("\t"+w);
			
		}
		
	}
	
	
	public static void main(String[] args) throws FileNotFoundException
	{
		
		Set<String> dict = IO.getWordDictionary("data/WordDictionary.txt");
		LexTree t = new LexTree();
		t.populate(dict);
		
	}
	

}
