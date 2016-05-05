package org.lorob.utils;

import java.util.HashMap;
import java.util.Properties;

/**
 * Class housing properties needed by FTPFilePicker
 * @author lorob
 *
 */
public class FTPLogProperties
{
	private Class _callingClass;
	private Properties _properties;
	private HashMap _logs;
	private int _maxId;
		
	public static final String FTP_USER="FTP_USER";
	public static final String FTP_PASSWORD="FTP_PASSWORD";
	public static final String FTP_SERVER="FTP_SERVER";
	public static final String DEFAULT="default";
	
	/**
	 * Constructor
	 * @throws Exception
	 */
	/*public FTPLogProperties(String propertyFileName,Class callingClass)
		throws Exception
	{
		_callingClass=callingClass;
		_logs=new HashMap();
		readLogViewerProperties(propertyFileName);
		_maxId=-1;
	}*/
	
	/**
	 * Constructor
	 * @throws Exception
	 */
	public FTPLogProperties(Properties property)
		throws Exception
	{
		_logs=new HashMap();
		_maxId=-1;
		_properties=property;
		readLogViewerProperties();
	}
	
	/**
	 * Constructor
	 * @throws Exception
	 */
	public FTPLogProperties(String propertyFileName,Class callingClass,int maxId)
		throws Exception
	{
		_callingClass=callingClass;
		_logs=new HashMap();
		_maxId=maxId;
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
		readLogViewerProperties();
	}
	
	/**
	 * Read the property file
	 * @throws Exception
	 */
	private void readLogViewerProperties()
		throws Exception
	{
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
			FTPLogSet thisLog=new FTPLogSet(logName,logLocation);
			_logs.put(logName,thisLog);
			index++;
			if(_maxId!=-1&&index>_maxId)
			{
				// escape
				logMoreSets=false;
				continue;
			}
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
