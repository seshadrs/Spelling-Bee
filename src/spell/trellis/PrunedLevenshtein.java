
package spell.trellis;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.*;

import spell.util.Utils;

public class PrunedLevenshtein {
	
	public enum PruningType { MAX_DISTANCE, BEAM_WIDTH };
	public class Pruning
	{ 
		PruningType type;
		double value;
		
		Pruning( PruningType p, double x)
		{
			this.type = p;
			this.value = x;
		}
		
		
	}
	
	
	public int insertionCost=1;
	public int deletionCost=1;
	public int matchingCost=0;
	
	
	
	public int prunedCost(int[][] trellis, Boolean[][]validity, int y, int x, boolean match)
	{	//compute minimum only over nodes that are valid
		
		
		
		return Utils.min( 
				(validity[y][x-1])? trellis[y][x-1]+insertionCost: Integer.MAX_VALUE , 
				(validity[y+1][x-1] && match)?trellis[y+1][x-1]+((match)? matchingCost : 1): Integer.MAX_VALUE, 
				(validity[y+1][x])? trellis[y+1][x] + deletionCost: Integer.MAX_VALUE
				);
	}
	
	public int LevenshteinDistance(char[] x, char[] y, Pruning p)
	{
		int ylen = y.length;
		int xlen = x.length;
		int[][] trellis = new int[ylen][xlen];
		Boolean[][] validity = new Boolean[ylen][xlen];
		
		for(int i=0; i<validity.length; i++)
			for(int j=0; j<validity[i].length; j++)
				validity[i][j]=false;
		
		//INITIALIZING THE TRELLIS - FIRST COLUMN, FIRST ROW (COMPARISON WITH DUMMY CHAR)
		
		trellis[ylen-1][0]=0;	//empty char compared with empty char
		validity[ylen-1][0]= true;
		for(int i=1; i< ylen; i++)
			{
			trellis[ylen-1-i][0]= trellis[ylen-1-i+1][0] + Utils.min(deletionCost, insertionCost);
			validity[ylen-1-i][0] = true;
			}
		
		for(int i=1; i< xlen; i++)
			{
			trellis[ylen-1][i]= trellis[ylen-1][i-1] + Utils.min(deletionCost, insertionCost);
			validity[ylen-1][i] = true;
			}
		
		
		if (p.type==PruningType.BEAM_WIDTH)
		{
			int minCostInCurCol = Integer.MAX_VALUE;
			int tmpCost;
		
			//CALCULATING THE REST OF THE TRELLIS
			
			for(int i=1; i< xlen; i++)	//for 2nd column onwards
			{
				//get costs
				for(int j=1; j<ylen; j++)	//from 2nd row onwards
				{
					tmpCost = prunedCost(trellis, validity, ylen-1-j,i, x[i]==y[j]);
					
					if (tmpCost<minCostInCurCol)
						minCostInCurCol = tmpCost;
					
					trellis[ylen-1-j][i] = tmpCost; 
				}
				
				//set validity based on mincost
				for(int j=1; j<ylen; j++)	//from 2nd row onwards
				{
					if(trellis[ylen-1-j][i]<=minCostInCurCol+p.value)
					{
						validity[ylen-1-j][i]=true;
					}
					
				}
				
				minCostInCurCol = Integer.MAX_VALUE;
			}
		
		}
		else if (p.type==PruningType.MAX_DISTANCE)
		{
			//CALCULATING THE REST OF THE TRELLIS
			
			for(int i=1; i< xlen; i++)	//for 2nd column onwards
			{
				//get costs
				for(int j=1; j<ylen; j++)	//from 2nd row onwards
				{
					trellis[ylen-1-j][i] = prunedCost(trellis, validity, ylen-1-j,i, x[i]==y[j]); 
				}
				
				//set validity based on mincost
				for(int j=1; j<ylen; j++)	//from 2nd row onwards
				{
					if(trellis[ylen-1-j][i]<=p.value)
					{
						validity[ylen-1-j][i]=true;
					}
					
				}
			}
			
		}
		
		Utils.displayTrellis(trellis, x, y);
		
		return trellis[0][xlen-1];	//the farthest cell on the diagonal contains the total cost
		
	}
	
	public int distance(String A, String B)
	{
		//add dummy character in beginning to bootstrap distance trellis
		A = " " + A;
		B = " " + B;
		
		char[] a = A.toCharArray();
		char[] b = B.toCharArray();
		
		Pruning p = new Pruning(PruningType.MAX_DISTANCE, 4);
		return LevenshteinDistance(a,b,p);
		
	}
	
	
	public static void main(String args[])
	{
		String a = "testing";
		String b = "tastesaing";
		PrunedLevenshtein l = new PrunedLevenshtein();
		int distanceAB = l.distance(a,b);
//		System.out.println(distanceAB);
		
		a = "abcxyz";
		b = "abcdefg";
		distanceAB = l.distance(a,b);
//		System.out.println(distanceAB);
		
	}

}
