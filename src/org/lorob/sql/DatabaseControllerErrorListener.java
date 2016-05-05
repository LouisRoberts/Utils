package org.lorob.sql;

public interface DatabaseControllerErrorListener
{
	/**
	 * An error has been thrown
	 * @param e
	 */
	public void parseError(Exception e);
}
