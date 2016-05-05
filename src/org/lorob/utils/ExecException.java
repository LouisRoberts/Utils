package org.lorob.utils;

/**
 * Execute exception form OSExecUtils
 * @author lorob
 *
 */
public class ExecException  extends Exception
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5841323816527363580L;

	/**
	 * Constructor
	 * @param message
	 * @param e
	 */
	public ExecException(String message,Exception e)
	{
		super(message,e);
	}
}
