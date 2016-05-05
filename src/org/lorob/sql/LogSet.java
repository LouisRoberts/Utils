package org.lorob.sql;

/**
 * Container for log data
 * @author lorob
 *
 */
public class LogSet
{
	private String _logName;
	private String _logLocation;
	
	/**
	 * Constructor
	 * @param logName
	 * @param logLocation
	 */
	public LogSet(String logName,String logLocation)
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
