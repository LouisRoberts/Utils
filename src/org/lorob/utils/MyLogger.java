package org.lorob.utils;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * Custom Logger
 * @author lorob
 *
 */
public class MyLogger extends Logger
{
	
	private static FileHandler _fileHandler;

	/**
	 * Constructor Create My Own Logger
	 * @param name
	 * @param resourceBundleName
	 */
	protected MyLogger(String name, String resourceBundleName)
	{
		super(name, resourceBundleName);
		initialise();
	}
	
	/**
	 * Constructor Create My Own Logger
	 * @param name
	 */
	protected MyLogger(String name)
	{
		super(name,"");
		initialise();
	}
	
	private void initialise()
	{
		// initialise fomratters
		setUseParentHandlers(false);
		MyCustomFormatter formatter = new MyCustomFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		addHandler(handler);
		
		try
		{
			if(_fileHandler==null)
			{
				_fileHandler= new FileHandler();
			}
			_fileHandler.setFormatter(formatter);
			addHandler(_fileHandler);
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
		
	/**
	 * @param name
	 * @return
	 */
	public static synchronized Logger getLogger(String name) {
		Logger logger=Logger.getLogger(name);
		logger.setUseParentHandlers(false);
		MyCustomFormatter formatter = new MyCustomFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
		try
		{
			if(_fileHandler==null)
			{
				_fileHandler= new FileHandler();
			}
			_fileHandler.setFormatter(formatter);
			logger.addHandler(_fileHandler);
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	    return logger;
	 }

}
