package spell.trellis;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.*;

import spell.util.Utils;

public class Levenshtein {
	
	public enum PruningType { MAX_DISTANCE, BEAM_WIDTH };
	public class PruningStrategy 
	{ 
		
		PruningType type;
		
		double value;
		
		PruningStrategy( PruningType p, double x)
		{
			this.type = p;
			this.value = x;
		}
		
	}
	
	
	public int insertionCost=1;
	public int deletionCost=1;
	public int matchingCost=0;
	
	
	
	public int LevenshteinDistance(char[] x, char[] y)
	{
		int ylen = y.length;
		int xlen = x.length;
		int[][] trellis = new int[ylen][xlen];
		
		//INITIALIZING THE TRELLIS - FIRST COLUMN, FIRST ROW (COMPARISON WITH DUMMY CHAR)
		
		trellis[ylen-1][0]=0;	//empty char compared with empty char
		for(int i=1; i< ylen; i++)
			trellis[ylen-1-i][0]= trellis[ylen-1-i+1][0] + Utils.min(deletionCost, insertionCost);;
		for(int i=1; i< xlen; i++)
			trellis[ylen-1][i]= trellis[ylen-1][i-1] + Utils.min(deletionCost, insertionCost);;
		
		
		//CALCULATING THE REST OF THE TRELLIS
		
		for(int i=1; i< xlen; i++)	//for 2nd column onwards
		{
			for(int j=1; j<ylen; j++)	//from 2nd row onwards
			{
				if (x[i]==y[j])	//matches
				{
					trellis[ylen-1-j][i] = Utils.min( trellis[ylen-1-j][i-1]+insertionCost, trellis[ylen-1-j+1][i-1]+matchingCost, trellis[ylen-1-j+1][i] + deletionCost);
				}
				else 
				{
					trellis[ylen-1-j][i] = Utils.min( trellis[ylen-1-j][i-1]+insertionCost, trellis[ylen-1-j+1][i] + deletionCost);
				}
			}
		}
		
		
		Utils.displayTrellis(trellis, x, y);
		return 0;
		
	}
	
	public int distance(String A, String B)
	{
		//add dummy character in beginning to bootstrap distance trellis
		A = " " + A;
		B = " " + B;
		
		char[] a = A.toCharArray();
		char[] b = B.toCharArray();
		
		return LevenshteinDistance(a,b);
		
	}
	
	
	public static void main(String args[])
	{
		String a = "testing";
		String b = "tastesaing";
		Levenshtein l = new Levenshtein();
		int distanceAB = l.distance(a,b);
		System.out.println(distanceAB);
		
		a = "abcxyz";
		b = "abcdefg";
		distanceAB = l.distance(a,b);
//		System.out.println(distanceAB);
		
	}

}
