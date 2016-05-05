package org.lorob.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.lorob.utils.DatabaseMap;

/**
 * A class encapsulating the indirection of
 * property file values
 * @author lorob
 *
 */
public class DatabaseController
{
	// settings
	private Properties _properties; 
	private DatabaseMap _databaseSets;
	
	// database
	private Connection _connection;
	//private String _databaseSelected;
	private String _currentSet;
	
	private DatabaseControllerErrorListener _errorListener;
	
	/**
	 * Constructor
	 * @param properties
	 * @param databaseMap
	 */
	public DatabaseController(Properties properties,DatabaseMap databaseMap,DatabaseControllerErrorListener errorListener)
	{
		_properties=properties;
		_databaseSets=databaseMap;
		_errorListener=errorListener;
	}
	
	/**
	 * Get the current database set ebing used
	 * @param set
	 */
	public void setDatabaseSet(String set)
	{
		_currentSet=set;
	}
	
	/**
	 * Need to construct a property file for the SQL runner based
	 * on the stream
	 * @return properties or null if non for the alias
	 */
	public Properties constructSQLRunnerProperties(String dbAlias)
	{
		// first get the alias that is being used
    	// Now we need to get the correct set depending on the current
    	String databaseSet=_currentSet+"_"+dbAlias;
    	String alias=_databaseSets.getdatabaseMapping(_currentSet,databaseSet);
    	if(alias==null)
    	{
    		return null;
    	}
    	alias=alias+"_";
    	// now read database values
        String dBUrl=_properties.getProperty(alias+"DBUrl");
        String dBDriver=_properties.getProperty(alias+"DBDriver");
        String dBUser=_properties.getProperty(alias+"DBUser");
        String dBPassword=_properties.getProperty(alias+"DBPassword");
		
		Properties returnProps=new Properties();
		returnProps.put("DBUrl", dBUrl);
		returnProps.put("DBDriver", dBDriver);
		returnProps.put("DBUser", dBUser);
		returnProps.put("DBPassword", dBPassword);
		
		return returnProps;
	} 
	
	/**
	 * connect to database
	 * using property file values
	 * @param database - the database within the current set to connect to
	 * @throws Exception
	 */
	public  void connectToDb(String dbAlias)
    	throws Exception
    {
    	if(_currentSet==null)
    	{
    		throw new Exception("No database set selected");
    	}
    	//_databaseSelected=new String(database);
    	try
        {
        	// first get the alias that is being used
        	// Now we need to get the correct set depending on the current
        	String databaseSet=_currentSet+"_"+dbAlias;
        	String origAlias=_databaseSets.getdatabaseMapping(_currentSet,databaseSet);
        	String alias=origAlias+"_";
        	// now read database values
            String dBUrl=_properties.getProperty(alias+"DBUrl");
            String dBDriver=_properties.getProperty(alias+"DBDriver");
            String dBUser=_properties.getProperty(alias+"DBUser");
            String dBPassword=_properties.getProperty(alias+"DBPassword");
            String dBEndUrl=_properties.getProperty(alias+"DBEndUrl");
            if(dBUrl==null)
            {
            	// something wrong with the property file
            	throw new Exception("Missing entries in property file for "+alias);
            }
            if(dBEndUrl!=null&&dBEndUrl.length()>0)
            {
                // hypersonic driver ??
                dBUrl=dBUrl+System.getProperty("user.dir").replace('\\','/')+dBEndUrl;
            }
            Class.forName(dBDriver);
            
            disconnect();
            _connection = DriverManager.getConnection(dBUrl, dBUser, dBPassword);
            if(_connection==null)
            {
                System.out.println("failed to connect");
                throw new Exception("Failed to connect to database");
            }
            else
            {
                _connection.setAutoCommit(true);
            }
            System.out.println("Checking "+origAlias+"="+SQLRunner.checkDatabase(this.getConnection(),origAlias));
        }
        catch(Exception e)
        {
        	if(_errorListener!=null)
        	{
        		_errorListener.parseError(e);
        	}
        	throw e;
        }
    }
	
	/**
	 * get the connection
	 * @return
	 */
	public Connection getConnection()
	{
		return _connection;
	}
	
	/**
	 * Discconnect
	 * @throws Exception
	 */
    public void disconnect()
    throws Exception
    {
        try
        {
            if(_connection!=null)
            {
                _connection.close();
            }
        }
        catch(Exception e)
        {
        	if(_errorListener!=null)
        	{
        		_errorListener.parseError(e);
        	}
        	throw e;
        }
    }
   
}
