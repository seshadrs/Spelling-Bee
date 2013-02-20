
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
	
	public class AlignmentResult{
		int[][] trellis;
		int distance;
		Integer[][] path;
		
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
				
				if (trellis[yPos][xPos-1] == Utils.min(trellis[yPos][xPos-1],trellis[yPos+1][xPos-1],trellis[yPos+1][xPos]))
						{
							path[i][0]=yPos;
							path[i][1]=xPos-1;
							
							xPos -=1;
						}
				else if (trellis[yPos+1][xPos-1] == Utils.min(trellis[yPos][xPos-1],trellis[yPos+1][xPos-1],trellis[yPos+1][xPos]))
						{
							path[i][0]=yPos+1;
							path[i][1]=xPos-1;
							
							yPos+=1;
							xPos-=1;
						}
				else if (trellis[yPos+1][xPos] == Utils.min(trellis[yPos][xPos-1],trellis[yPos+1][xPos-1],trellis[yPos+1][xPos]))
				{
						path[i][0]=yPos+1;
						path[i][1]=xPos;
						
						yPos+=1;
				}
				
			}
			
			return path;
		}
		
	}
	
	
	public int insertionCost=1;
	public int deletionCost=1;
	public int matchingCost=0;
	public int substitutionCost=1;
	
	
	
	public int prunedCost(int[][] trellis, Boolean[][]validity, int y, int x, boolean match)
	{	//compute minimum only over nodes that are valid
		
			return Utils.min( 
				(validity[y][x-1])? trellis[y][x-1]+insertionCost: Integer.MAX_VALUE , 
				(validity[y+1][x-1])?trellis[y+1][x-1]+((match)?matchingCost: deletionCost): Integer.MAX_VALUE, 
				(validity[y+1][x])? trellis[y+1][x] + deletionCost: Integer.MAX_VALUE
				);
		
	}
	
	public int[][] LevenshteinDistance(char[] x, char[] y, Pruning p)
	{
		int ylen = y.length;
		int xlen = x.length;
		int[][] trellis = new int[ylen][xlen];
		Boolean[][] validity = new Boolean[ylen][xlen];
		
		for(int i=0; i<validity.length; i++)
			for(int j=0; j<validity[i].length; j++)
				validity[i][j]=false;
		
		//INITIALIZING THE TRELLIS - FIRST COLUMN, FIRST ROW (COMPARISON WITH DUMMY CHAR)
		
		trellis[ylen-1][0]=matchingCost;	//empty char compared with empty char
		validity[ylen-1][0]= true;
		for(int i=1; i< ylen; i++)
			{
			int cost = trellis[ylen-1-i+1][0] + Utils.min(deletionCost, insertionCost);
			if (cost<=p.value)
				{
					validity[ylen-1-i][0] = true;
				}
			trellis[ylen-1-i][0]= cost;
			}
		
		
		for(int i=1; i< xlen; i++)
			{
				int cost= trellis[ylen-1][i-1] + Utils.min(deletionCost, insertionCost);
			
				if (cost<=p.value)
				{
					validity[ylen-1][i] = true;
				}
				trellis[ylen-1][i]= cost;
			}
		
		
		if (p.type==PruningType.BEAM_WIDTH)
		{
			int minCostInCurCol = Integer.MAX_VALUE;
			int minCostInPrevCol = 0;
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
					
					if(tmpCost<=minCostInPrevCol+p.value)
					{
						trellis[ylen-1-j][i] = tmpCost;
						validity[ylen-1-j][i]=true;
					}
					else
						trellis[ylen-1-j][i] = Integer.MAX_VALUE; 
				}
				
				minCostInPrevCol = minCostInCurCol;
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
					
					if(trellis[ylen-1-j][i]<=p.value)
					{
						validity[ylen-1-j][i]=true;
					}
					else
						trellis[ylen-1-j][i]=Integer.MAX_VALUE;
				}
				
			}
			
		}
		
		
		//SET TO MAXINT VAL WHERE INITIALIZED CELLS ARE NOT VALID
		for(int i=1; i< ylen; i++)
			if (!validity[ylen-1-i][0])
				trellis[ylen-1-i][0]= Integer.MAX_VALUE;
		
		
		for(int i=1; i< xlen; i++)
			if (!validity[ylen-1][i])
				trellis[ylen-1][i]= Integer.MAX_VALUE;
		
		
		return trellis;	
		
	}
	
	public int distance(String A, String B, PruningType type, int prunigValue)
	{
		//add dummy character in beginning to bootstrap distance trellis
		A = " " + A;
		B = " " + B;
		
		char[] a = A.toCharArray();
		char[] b = B.toCharArray();
		
		Pruning p = new Pruning(type, prunigValue);
		int[][] costMatrix = LevenshteinDistance(a,b,p);
		
		AlignmentResult res = new AlignmentResult(costMatrix);
		
		Utils.displayTrellis(res.trellis, A.toCharArray(), B.toCharArray(), res.path);
		
//		for(int i=0; i< res.path.length; i++)
//			System.out.print(res.path[i][0]+" "+res.path[i][1]+"\t");
//		System.out.println();
		
		return res.distance;
		
	}
	
	
	public static void main(String args[])
	{
		int d=4;
		
		String a = "testing";
		String b = "tastesaing";
		PrunedLevenshtein l = new PrunedLevenshtein();
		int distanceAB = l.distance(a,b,PruningType.MAX_DISTANCE, d);
//		System.out.println(distanceAB);
		
		a = "abcxyz";
		b = "abcdefg";
		distanceAB = l.distance(a,b,PruningType.BEAM_WIDTH, d);
//		System.out.println(distanceAB);
		
	}

}
