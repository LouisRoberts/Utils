package org.lorob.utils;

import java.io.File;

public class HTMLCreator
{
	public static void main(String args[])
	{
		if(args.length!=3)
		{
			System.out.println("Incorrect usage. usage HTMLCreator xmlfile xslfile htmlfile");
			return;
		}
		try
		{
			XMLUtils.generateHTML(new File(args[0]), new File(args[1]), new File(args[2]));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
