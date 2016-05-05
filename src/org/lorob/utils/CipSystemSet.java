package org.lorob.utils;

/**
 * Container for indexing of systems and aliases
 * @author lorob
 *
 */
public class CipSystemSet
{
	private String _systemName;
	private String _systemAlias;
	
	/**
	 * Constructor
	 * @param name
	 * @param alias
	 */
	public CipSystemSet(String name,String alias)
	{
		_systemName=name;
		_systemAlias=alias;
	}
	
	/**
	 * Get The System Name
	 * @return
	 */
	public String getName()
	{
		return _systemName;
	}
	
	/**
	 * Get The Alias
	 * @return
	 */
	public String getAlias()
	{
		return _systemAlias;
	}
}
