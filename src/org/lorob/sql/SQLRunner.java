package org.lorob.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;

import org.lorob.utils.FileUtils;

/**
 * SQL Runner
 * Run a sql file
 * @author lorob
 *
 */
public class SQLRunner
{
	private String _systemDir;
	private StringBuffer _sql; 
	private ArrayList _headings;
	private SimpleDateFormat _sdf=new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
	
	 // database variables
	private Connection _connection;
	private Properties _properties;
	
	/**
	 * Constructor
	 * @param properties
	 */
	public SQLRunner(Properties properties)
	{
		_systemDir=System.getProperty("user.dir");
		_systemDir=_systemDir.replace('\\','/');
        _properties=properties;
	}
	
	public ArrayList getHeadings()
	{
		return _headings;
	}
	
	/**
	 * Run the hand sql file with arguments
	 * and return an array of results.
	 * @param sqlFile
	 * @param sqlArguments
	 * @return
	 * @throws Exception
	 */
	public String[][] runSQL(String sqlFile,String[] sqlArguments)
		throws Exception
	{
		String sqlCommand=null;
		try
		{
			sqlCommand=parseSql(new File(sqlFile),sqlArguments);
			_headings=new ArrayList();
			if(_connection==null)
			{
				connectToDb();
			}
			PreparedStatement stmt = _connection.prepareStatement(sqlCommand);
	        ResultSet rs = stmt.executeQuery();
	        ResultSetMetaData rsmd = rs.getMetaData();
	        int numCols = rsmd.getColumnCount();
	        
	        for (int i = 1; i <= numCols; i++) 
	        {
	        	String columnLabel = rsmd.getColumnLabel(i);
	        	_headings.add(columnLabel);
	        }
	        
	        ArrayList rows=new ArrayList();
	        while(rs.next())
	        {
	        	String[] results=new String[numCols];
	        	for(int i=0;i<numCols;i++)
	            {
	        		Object thisResult=rs.getObject(i+1);
	        		int type=rsmd.getColumnType(i+1);
	        		if(thisResult!=null)
	        		{
	        			if(type == Types.TIMESTAMP )
	        			{
	        				Timestamp thisTimeStamp=rs.getTimestamp(i+1);
	        				results[i]=_sdf.format(thisTimeStamp);
	        			}
	        			else
	        			{
	        				results[i]=thisResult.toString();
	        			}
	        		}
	        		else
	        		{
	        			results[i]="";
	        		}
	            }
	        	rows.add(results);
	        }
	        if(rows.size()==0)
	        {
	        	rs.close();
	 	        stmt.close();
	        	return null;
	        }
	        String[][] allResults=new String[rows.size()][numCols];
	        for(int i=0;i<rows.size();i++)
	        {
	        	String[] thisRow=(String[])rows.get(i);
	        	for(int j=0;j<numCols;j++)
	            {
	        		allResults[i][j]=thisRow[j];
	            }
	        }
	        
	        rs.close();
	        stmt.close();
	        return allResults;
		}
		catch(Exception e)
		{
			System.out.println("Failed to execute "+sqlCommand);
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * Run the passed sql statement
	 * @param sql
	 * @param sqlArguments
	 * @return
	 * @throws Exception
	 */
	public String[][] runSQLStatement(String sql,String[] sqlArguments)
		throws Exception
	{
		String sqlCommand=null;
		try
		{
			sqlCommand=parseSql(new StringBuffer(sql),sqlArguments);
			_headings=new ArrayList();
			if(_connection==null)
			{
				connectToDb();
			}
			PreparedStatement stmt = _connection.prepareStatement(sqlCommand);
			// are we an update
			if(sqlCommand.toLowerCase().startsWith("insert")||
				sqlCommand.toLowerCase().startsWith("update")||
				sqlCommand.toLowerCase().startsWith("delete"))
				{
					int noRows=stmt.executeUpdate();
					String[][] allResults=new String[1][1];
					allResults[0][0]=""+noRows;
					stmt.close();
					return allResults; 
				}
	        ResultSet rs = stmt.executeQuery();
	        ResultSetMetaData rsmd = rs.getMetaData();
	        int numCols = rsmd.getColumnCount();
	        
	        for (int i = 1; i <= numCols; i++) 
	        {
	        	String columnLabel = rsmd.getColumnLabel(i);
	        	_headings.add(columnLabel);
	        }
	        
	        ArrayList rows=new ArrayList();
	        while(rs.next())
	        {
	        	String[] results=new String[numCols];
	        	for(int i=0;i<numCols;i++)
	            {
	        		Object thisResult=rs.getObject(i+1);
	        		if(thisResult!=null)
	        		{
	        			results[i]=thisResult.toString();
	        		}
	        		else
	        		{
	        			results[i]="";
	        		}
	            }
	        	rows.add(results);
	        }
	        if(rows.size()==0)
	        {
	        	rs.close();
	 	        stmt.close();
	        	return null;
	        }
	        String[][] allResults=new String[rows.size()][numCols];
	        for(int i=0;i<rows.size();i++)
	        {
	        	String[] thisRow=(String[])rows.get(i);
	        	for(int j=0;j<numCols;j++)
	            {
	        		allResults[i][j]=thisRow[j];
	            }
	        }
	        
	        rs.close();
	        stmt.close();
	        return allResults;
		}
		catch(Exception e)
		{
			System.out.println("Failed to execute "+sqlCommand);
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * replace the ? with handed parameters
	 * @param sqlFile
	 * @param sqlArguments
	 * @return
	 * @throws Exception
	 */
	private String parseSql(StringBuffer sql,String[] sqlArguments)
		throws Exception
	{
		_sql=sql;
		// substitute any arguments
		if(sqlArguments!=null)
		{
			for(int i=0;i<sqlArguments.length;i++)
			{
				int sqlArg=_sql.toString().indexOf("?");
				if(sqlArg>=0)
				{
					_sql.replace(sqlArg,sqlArg+1,"'"+sqlArguments[i]+"'");
				}
			}
		}
		
		return _sql.toString();
	}
	
	private String parseSql(File sqlFile,String[] sqlArguments)
		throws Exception
	{
		_sql=FileUtils.readFile(sqlFile);
		return parseSql(_sql,sqlArguments);
	}
	
//	 disconnect
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
        	throw e;
        }
    }
	
    // connect to database
    // using property file values
    private void connectToDb()
    	throws Exception
    {
    	String dBUrl=null;
    	String dBDriver=null;
    	String dBUser=null;
    	String dBPassword=null;
    	try
        {
        	// one set of database values in property file
            dBUrl=_properties.getProperty("DBUrl");
            dBDriver=_properties.getProperty("DBDriver");
            dBUser=_properties.getProperty("DBUser");
            dBPassword=_properties.getProperty("DBPassword");
            
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
        }
        catch(Exception e)
        {
        	throw new Exception("Error with parameters (dBUrl/dBDriver/dBUser/dBPassword)=("+
        			dBUrl+"/"+dBDriver+"/"+dBUser+"/"+dBPassword+")\n"
        			+e.getMessage());
        	
        }
    }
    private static String QUIZ_DB_NAME="SELECT GLOBAL_NAME FROM GLOBAL_NAME";
    /** checkDatabase-check that the database conenction is the one you think it is!
     * @param connection
     * @param name
     * @return
     */
    public static boolean checkDatabase(Connection connection, String name)
    {
    	boolean isCorrect=false;
    	try
    	{
    		PreparedStatement stmt = connection.prepareStatement(QUIZ_DB_NAME);
	        ResultSet rs = stmt.executeQuery();
	        if(rs.next())
	        {
	        	String dbName=rs.getString(1);
	        	if(dbName==null||dbName.length()==0)
	        	{
	        		return false;
	        	}
	        	int dot=dbName.indexOf(".");
	        	if(dot>0)
	        	{
	        		dbName=dbName.substring(0,dot);
	        	}
	        	System.out.println("dbName="+dbName+" name="+name);
	        	if(dbName.toLowerCase().compareTo(name.toLowerCase())==0)
	        	{
	        		return true;
	        	}
	        }
	        
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return isCorrect;
    }
}
