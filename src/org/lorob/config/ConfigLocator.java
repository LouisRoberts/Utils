package org.lorob.config;

import java.util.Properties;

import org.lorob.utils.Strings;

/**
 * Dummy Class to find property file
 * @author lorob
 *
 */
public class ConfigLocator
{
	private Properties _properties;
	private String _propertyFileName;
	
	/**
	 * Constructor
	 * @param propertyFileName
	 * @throws Exception
	 */
	public ConfigLocator(String propertyFileName)
		throws Exception
	{
		_properties = new Properties();
		String finalPropertyFileName=propertyFileName.toLowerCase()+".properties";
		_propertyFileName=Strings.replace(finalPropertyFileName," ","");
		System.out.println("_propertyFileName="+_propertyFileName);
		_properties.load(ConfigLocator.class.getResourceAsStream(_propertyFileName));
	}
	
	/**
	 * Get the property File Name
	 * @return
	 */
	public String getPropertyFileName()
	{
		return _propertyFileName;
	}
	
	/**
	 * Get the loaded property file
	 * @return
	 */
	public Properties getPropertyFile()
	{
		return _properties;
	}
}
