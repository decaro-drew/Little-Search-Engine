package lse;

import java.io.*;
import java.util.*;

public class Driver 
{
	static Scanner sc = new Scanner(System.in);
	
	static String getOption() 
	{
		System.out.print("getKeyWord(): ");
		String response = sc.next();
		return response;
	}
	
	public static void main(String args[])
	{
		LittleSearchEngine lse = new LittleSearchEngine();
		//System.out.println("getkey: " + lse.getKeyword("noth--ing!!"));

		try
		{
			lse.makeIndex("docs.txt", "noisewords.txt");
		} 
		catch (FileNotFoundException e)
		{
		}		
		
//		String input;
//
	//	for (String hi : lse.keywordsIndex.keySet())
		//	System.out.println(hi+" "+lse.keywordsIndex.get(hi));
//				
//		while (!(input = getOption()).equals("q"))
//		{
//				System.out.println(lse.getKeyWord(input));
//		}
		System.out.println("top 5: " + lse.top5search("deep", "world"));
	}
}