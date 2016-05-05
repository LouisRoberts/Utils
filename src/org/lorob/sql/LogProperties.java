package org.lorob.sql;

import java.util.HashMap;
import java.util.Properties;

/**
 * Class for reading log property file
 * @author lorob
 *
 */
public class LogProperties
{
	private Properties _properties;
	private HashMap _logs;
	private Class _callingClass;
	
	
	public static String FTP_USER="FTP_USER";
	public static String FTP_PASSWORD="FTP_PASSWORD";
	public static String FTP_SERVER="FTP_SERVER";
	public static String DEFAULT="default";
	
	/**
	 * Constrcutor
	 * @param propertyFileName
	 * @throws Exception
	 */
	public LogProperties(String propertyFileName,Class callingClass)
		throws Exception
	{
		_logs=new HashMap();
		_callingClass=callingClass;
		readLogViewerProperties(propertyFileName);
	}
	
	/**
	 * Read the properties file and create 
	 * log sets
	 * @throws Exception
	 */
	private void readLogViewerProperties(String propertyFileName)
		throws Exception
	{
		_properties=new Properties();
		// read the property file
		_properties.load(_callingClass.getResourceAsStream(propertyFileName));
		
		int index=1;
		boolean logMoreSets=true; 
		
		while(logMoreSets==true)
		{
			String logName=_properties.getProperty("FTP_LOG_SERVER_"+index);
			if(logName==null)
			{
				logMoreSets=false;
				continue;
			}
			String logLocation=_properties.getProperty("FTP_LOG_LOCATION_"+index);
			LogSet thisLog=new LogSet(logName,logLocation);
			_logs.put(logName,thisLog);
			index++;
		}
		
	}
	
	/**
	 * Get the property file read
	 * @return
	 */
	public Properties getLogProperties()
	{
		return _properties;
	}
	
	/**
	 * Get the logs read
	 * @return
	 */ 
	public HashMap getLogs()
	{
		return _logs;
	}
}
