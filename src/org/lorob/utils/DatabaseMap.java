package org.lorob.utils;

import java.util.HashMap;

/**
 * Container Class containing sets of database mappings 
 * @author lorob
 *
 */
public class DatabaseMap
{
	private HashMap _databaseSet;
	
	public DatabaseMap()
	{
		_databaseSet=new HashMap();
	}

	/**
	 * @param setName
	 * @param databaseMapping
	 */
	public void addDatabaseToSet(String setName,String databaseMapping,String value)
	{
		HashMap thisSet=(HashMap)_databaseSet.get(setName);
		// check if we exist if not create 
		if(thisSet==null)
		{
			_databaseSet.put(setName,new HashMap());
			thisSet=(HashMap)_databaseSet.get(setName);
		}
		thisSet.put(databaseMapping,value);
	}
	
	/** Get the set fpr this name
	 * @param setName
	 * @return
	 */
	public HashMap getDatabaseSet(String setName)
	{
		return (HashMap)_databaseSet.get(setName);
	}
	
	/**
	 * Return the mapping matching criteria
	 * @param setName
	 * @param mapping
	 * @return
	 */
	public String getdatabaseMapping(String setName,String mapping)
	{
		HashMap thisSet=(HashMap)_databaseSet.get(setName);
		if(thisSet==null)
		{
			return null;
		}
		return (String)thisSet.get(mapping);
	}
	
}


