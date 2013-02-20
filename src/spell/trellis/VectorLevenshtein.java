package spell.trellis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.*;

import spell.trellis.PrunedLevenshtein.PruningType;
import spell.util.Utils;

public class VectorLevenshtein {
	
	
	public class AlignmentResult{
		int[][] trellis;
		int distance;
		Integer[][] path;
		int insertionCount=0;
		int deletionCount=0;
		int substitutionCount=0;
		
		public AlignmentResult(int[][] costMatrix) 
		{
			this.trellis = costMatrix;
			this.distance = costMatrix[0][costMatrix[0].length-1];
			if (distance!=Integer.MAX_VALUE)
				this.path = this.calcPath(trellis);
			
		}
		
		
		public Integer[][] calcPath(int[][] trellis)
		{
			Integer ylen = trellis.length;
			Integer xlen = trellis[0].length;
			
			Integer[][] path = new Integer[xlen+ylen][2];
			
			int xPos = xlen-1;
			int yPos =0;
			path[xPos][0]=yPos;
			path[xPos][1]=xPos;
			
			for(int i=path.length-1; i>=0; i--)
			{
				if(xPos==1 && yPos==ylen-2)
					break;
				
				if (trellis[yPos][xPos-1] == Utils.min(trellis[yPos][xPos-1],trellis[yPos+1][xPos-1],trellis[yPos+1][xPos]) && yPos!=ylen-1 && xPos-1!=0)
						{
							path[i][0]=yPos;
							path[i][1]=xPos-1;
							
							xPos -=1;
							
							this.insertionCount+=1;
						}
				else if (trellis[yPos+1][xPos-1] == Utils.min(trellis[yPos][xPos-1],trellis[yPos+1][xPos-1],trellis[yPos+1][xPos]) && yPos+1!=ylen-1 && xPos-1!=0)
						{
							path[i][0]=yPos+1;
							path[i][1]=xPos-1;
							
							yPos+=1;
							xPos-=1;
							
							if (trellis[yPos+1][xPos-1]!=trellis[yPos][xPos])	//is the don't match, it is a substitution
								this.substitutionCount+=1;
						}
				else if (trellis[yPos+1][xPos] == Utils.min(trellis[yPos][xPos-1],trellis[yPos+1][xPos-1],trellis[yPos+1][xPos]) && yPos+1!=ylen-1 && xPos!=0)
				{
						path[i][0]=yPos+1;
						path[i][1]=xPos;
						
						yPos+=1;
						
						this.deletionCount+=1;
				}
				
				
			}
			
			if(xPos==1)
				this.deletionCount += (ylen-1) - yPos -1;
			else if(yPos==ylen-2)
				this.insertionCount += xPos -1;
			
			return path;
		}
		
	}
	
	
	public int prunedCost(int[][] trellis, int y, int x, String X, String Y)
	{	//compute minimum only over nodes that are valid
		
		return Utils.min( 
				trellis[y][x-1] + Math.abs("".compareToIgnoreCase(X)), 
				trellis[y+1][x-1] + Math.abs(X.compareToIgnoreCase(Y)), 
				trellis[y+1][x] + Math.abs("".compareToIgnoreCase(Y))
				) ;
		
	}
	
	
	public int[][] LevenshteinDistance(String[] x, String[] y)
	{
		int ylen = y.length;
		int xlen = x.length;
		int[][] trellis = new int[ylen][xlen];
		
		//INITIALIZING THE TRELLIS - FIRST COLUMN, FIRST ROW (COMPARISON WITH DUMMY CHAR)
		
		trellis[ylen-1][0]=0;	//empty char compared with empty char
		for(int i=1; i< ylen; i++)
			trellis[ylen-1-i][0]= trellis[ylen-1-i+1][0] + Math.abs("".compareToIgnoreCase(y[i]));
		for(int i=1; i< xlen; i++)
			trellis[ylen-1][i]= trellis[ylen-1][i-1] + Math.abs("".compareToIgnoreCase(x[i]));
		
		
		//CALCULATING THE REST OF THE TRELLIS
		
		for(int i=1; i< xlen; i++)	//for 2nd column onwards
		{
			for(int j=1; j<ylen; j++)	//from 2nd row onwards
			{
				trellis[ylen-1-j][i] = prunedCost(trellis, ylen-1-j, i, x[i], y[j]);
			}
		}
		
		
		return trellis;	//the farthest cell on the diagonal contains the total cost
		
	}
	
	public int showDistance(ArrayList<String> A, ArrayList<String> B)
	{
		A.add(0,"");
		B.add(0,"");
		
		String[] a = A.toArray(new String[A.size()]);
		String[] b = B.toArray(new String[B.size()]);
		
		int[][] costMatrix = LevenshteinDistance(a,b);
		AlignmentResult res = new AlignmentResult(costMatrix);
		
		Utils.lineBreak();
		if (res.distance==Integer.MAX_VALUE)
			System.out.println("TRELLIS. No Alignment was found");
		else
			System.out.println("TRELLIS AND  ONE OF THE BEST-ALIGNMENT PATHS");
		Utils.lineBreak();
		
		Utils.displayTrellis(res.trellis, a, b, res.path);
		System.out.println("Total of "+res.insertionCount+" Insertions, "+res.deletionCount+" Deletions, "+res.substitutionCount+" Substitutions.");
		return res.distance;
		
	}
	
	
	public int distance(ArrayList<String> A, ArrayList<String> B)
	{
		A.add(0,"");
		B.add(0,"");
		
		String[] a = A.toArray(new String[A.size()]);
		String[] b = B.toArray(new String[B.size()]);
		
		int[][] costMatrix = LevenshteinDistance(a,b);
		AlignmentResult res = new AlignmentResult(costMatrix);
		
		System.out.println("Total of "+res.insertionCount+" Insertions, "+res.deletionCount+" Deletions, "+res.substitutionCount+" Substitutions.");
		return res.distance;
		
	}
	
	
	private static int minimum(int a, int b, int c) 
	{
	        return Math.min(Math.min(a, b), c);
	}
	
		
	public static void main(String args[])
	{
		Scanner s = new Scanner(System.in);
		ArrayList<String> template = new ArrayList<String>();
		ArrayList<String> input = new ArrayList<String>();
		
		System.out.print("ENTER TEMPLATE WORD SEQUENCE SEPARATED BY COMMAS: ");
		for (String w : s.next().split(","))
			template.add(w);
		System.out.println();
		
		System.out.print("ENTER INPUT WORD SEQUENCE SEPARATED BY COMMAS: ");
		for (String w : s.next().split(","))
			input.add(w);
		System.out.println();
		
		//no pruning
		System.out.println();
		VectorLevenshtein l = new VectorLevenshtein();
		l.showDistance(input, template);
		
		
		
	}

}
