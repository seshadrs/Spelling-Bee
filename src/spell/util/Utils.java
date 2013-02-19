package spell.util;


public class Utils {

	
	public static int min(int a, int b, int c)
	{
		return Math.min(a, Math.min(b, c));
	}
	
	public static int min(int a, int b)
	{
		return Math.min(a, b);
	}
	
	public static void displayTrellis(int[][] trellis)
	{
		for (int i=0; i< trellis.length;i++)
			{
			for (int j=0; j< trellis[i].length;j++)
				System.out.print(trellis[i][j]+"\t");
			
			System.out.println();
			}
	}
	
	
	public static void displayTrellis(int[][] trellis, char[] x, char[] y)
	{
		for (int i=0; i< trellis.length;i++)
			{
			System.out.print("<"+y[y.length-1-i]+">\t");
			for (int j=0; j< trellis[i].length;j++)
				if (trellis[i][j]==Integer.MAX_VALUE)
					System.out.print("-\t");
				else
					System.out.print(trellis[i][j]+"\t");
			
			System.out.println("\n"); 
			}
		
		System.out.print("\t");		//offset for the dummy char
		
		for(int k=0; k<x.length; k++)
			System.out.print("<"+x[k]+">\t");
		System.out.println("\n");
	}
}
