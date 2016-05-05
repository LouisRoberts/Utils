package org.lorob.utils;

public class FTPLogSet
{
	private String _logName;
	private String _logLocation;
	
	/**
	 * Constructor
	 * @param logName
	 * @param logLocation
	 */
	public FTPLogSet(String logName,String logLocation)
	{
		_logName=logName;
		_logLocation=logLocation;
	}
	
	/**
	 * Get The Log Name
	 * @return
	 */
	public String getLogName()
	{
		return _logName;
	}
	
	/**
	 * Get The Log Location
	 * @return
	 */
	public String getLogLocation()
	{
		return _logLocation;
	}
}
