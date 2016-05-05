package org.lorob.sql;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import org.lorob.excel.ExcelWorkbook;
import org.lorob.report.ReportPropertyFileReader;
import org.lorob.report.ReportVariable;
import org.lorob.utils.FileUtils;
import org.lorob.utils.OrderedHashMap;
import org.lorob.utils.Strings;

/**
 * SQLQuery class to run either sql or pl-sql or merge excel reports
 * A bit rough (like my pants) at the minute
 * @author grrob
 * @author lorob
 */
public class SQLQuery
{
	private Connection _connection = null;
	private SimpleDateFormat _sdf=new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
	private String _sql = null; // The SQL
	private boolean _plSQL = false; 
	private List _sqlParameters = null; // Parameters in the order in which they appear in the SQL
	private List _runParameters = null; // Parameters in the order in which they should be passed to the run method
	private HashMap _runParameterNamesToSubscripts = null; // Maps run parameter name (String, key) to subscript in the runParameters list (Integer)
	private String _expectedParameters = null; // String representation of the calculated expected runParameters.
	private int _totalOutputParameters = 0; // The total no of output parameters, for a pl/sql query.
	private int _totalInputParameters = 0; // The total no of input parameters.
	private ArrayList _headings = null;
	private String[][] _results = null; // The results of running a non-pl/sql query, as strings.
	private Object[] _contentsOfOutputVariables = null;
	private Properties _properties;
	private SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyymmdd"); 

	/**
	 * Creates a new query where the SQL is taken from the specified SQL file and the
	 * parameter details (both input and output variables) are specified in the properties file
	 * in the order they appear in this file.
	 * @param sqlFileName The name and location of the file containing the SQL.
	 * @param propertiesFileName The name and location of the properties file.
	 * @param plSQL If true then runs as PL/SQL (which can include output parameters etc).
	 * @param connectionProperties the property file containing the connection parameters 
	 */
	public SQLQuery(String sqlFileName, String propertiesFileName, boolean plSQL,Properties connectionProperties) throws Exception {
		_properties=connectionProperties;
		File variablesFile = new File(propertiesFileName);
		ReportPropertyFileReader reportPropertyFileReader=new ReportPropertyFileReader(variablesFile);	
		OrderedHashMap allVariables = reportPropertyFileReader.processFile();
		File sqlFile = new File(sqlFileName);
		_sql = FileUtils.readFile(sqlFile).toString();
		populateParameters(_sql, allVariables);
		_sql = stripOutComments(_sql);
		this._plSQL = plSQL;
	}
	
	/**
	 * Creates a new non PL/SQL query where the SQL is taken from the specified SQL file and the
	 * parameter details (both input and output variables) are specified in the properties file
	 * in the order they appear in this file.
	 * @param sqlFileName The name and location of the file containing the SQL.
	 * @param propertiesFileName The name and location of the properties file.
	 * @param connectionProperties the property file containing the connection parameters
	 */	
	public SQLQuery(String sqlFileName, String propertiesFileName,Properties connectionProperties) throws Exception {
		this(sqlFileName, propertiesFileName, false,connectionProperties);
	}
	
	// Replace preprocessing directives (starting #).
	// #YYYYMM-TO-MONTH-START - converts proc month to date of the start of the month for DB2
	// #YYYYMM-TO-MONTH-END - converts proc month to date of the end of the month
	// #YYYYMM-TO-NEXT-MONTH-START - converts proc month to date of the start of the following month for DB2
	
	public String preProcess( String sql ) throws NumberFormatException, ParseException {
		String s = "";
			s = sql;
			final String[] directives = new String[] {"#YYYYMM-TO-MONTH-START", "#YYYYMM-TO-MONTH-END", "#YYYYMM-TO-NEXT-MONTH-START"};
			for(int i=0; i<directives.length; i++) {
				do {
					String d = directives[i];
					int p1 = s.indexOf(d);
					int p2 = s.indexOf(")", p1);
					if (p1 > -1) {
						String arg = "";
						// Strip off trailing or leading spaces and quotes.
						arg = s.substring(p1 + d.length() + 1, p2 ).trim().substring(1);
						arg = arg.substring( 0, arg.length()-1 );
						if (i==0) { // #YYYYMM-TO-MONTH-START - substitute the first day of the month - resultant date in DD/MM/YYYY format
							String year = arg.substring(0, 4);
							String month = arg.substring(4, 6);
							arg = "'" + year + "-" + month + "-01'";
						} else if (i==1) { // #YYYYMM-TO-MONTH-END - substitute the last date of the month - resultant date in DD/MM/YYYY format
							String year = arg.substring(0, 4);
							String month = arg.substring(4, 6);
							
							// Java date manipulation is horribly arcane so apologies in advance... all the below does is find out the last date of the month...
							int iMonth = Integer.valueOf(month).intValue();
							int iYear = Integer.valueOf(year).intValue();
							iMonth++;
							if (iMonth > 12) {
								iMonth = 1;
								iYear++;
							}
							String day;
							month = Integer.toString(iMonth);
							year = Integer.toString(iYear);
							arg = "01/" + month + "/" + year;
							DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
							Date dt = df.parse(arg);
							Calendar cal = Calendar.getInstance();
							cal.setTime(dt);
							cal.add(Calendar.DATE, -1);
							dt = cal.getTime();
							String textDate=sDateFormat.format(dt);
							int intMonth=Integer.parseInt(textDate.substring(4, 6));
							int intDay=Integer.parseInt(textDate.substring(6, 8));
							int intYear=Integer.parseInt(textDate.substring(0, 4));
							month = Integer.toString(intMonth + 1);
							if (month.length() < 2) {
								month = "0" + month;
							}
							day = Integer.toString(intDay);
							if (day.length() < 2) {
								day = "0" + day;
							}
							
							
							arg = "'" + (1900 + intYear) + "-" + 
							month + 
							"-" + 
							day + "'";
						} else if (i==2) { // #YYYYMM-TO-NEXT-MONTH-START
							String year = arg.substring(0, 4);
							String month = arg.substring(4, 6);
							int iMonth = Integer.valueOf(month).intValue();
							int iYear = Integer.valueOf(year).intValue();
							iMonth++;
							if (iMonth > 12) {
								iMonth = 1;
								iYear++;
							}
							month = "0" + iMonth;
							month = month.substring( month.length() - 2  );
							year = "" + iYear;
							arg = "'" + year + "-" + month + "-01'";
						}
						s = s.substring(0, p1) + arg + s.substring(p2+1);
					} else {
						break;
					}
				} while (true);				
			}
			System.out.println(s);
		return s;
	}
	
	/**
	 * Runs the SQL query substituting the specified input args.
	 * The args must be listed in order of first appearance in the SQL.
	 * @param args The arguments to the query, in the order in which they appear in the variable properties file.
	 */
	public void run( String[] args ) throws Exception {
		PreparedStatement stmt = null;
		try {
			connectToDb();
			String tempSQL = preProcess( mergeArguments(args)) ;
			
			System.out.println( tempSQL);
			if (_plSQL) {
				stmt = _connection.prepareCall(tempSQL);
			} else {
				stmt = _connection.prepareStatement(tempSQL);
			}
			// Set output parameters
			int i;
			if (_plSQL) {
				System.out.println( "plsql");
				i=0;
				for(Iterator it = _sqlParameters.iterator(); it.hasNext(); ) {
					ReportVariable variable = (ReportVariable) it.next();
					if (variable.isOutput()) {
						i++;
						if (variable.getType() == ReportVariable.INTEGER) {
							((CallableStatement)stmt).registerOutParameter( i, Types.NUMERIC );
						} else if (variable.getType() == ReportVariable.STRING) {
							((CallableStatement)stmt).registerOutParameter( i, Types.VARCHAR );
						} else if (variable.getType() == ReportVariable.DATE) {
							((CallableStatement)stmt).registerOutParameter( i, Types.DATE );
						} else if (variable.getType() == ReportVariable.FLOAT) {
							((CallableStatement)stmt).registerOutParameter( i, Types.DOUBLE );
						} else {
							((CallableStatement)stmt).registerOutParameter( i, Types.VARCHAR );
						}
					}
				}
			}
			stmt.execute();
			_contentsOfOutputVariables = null;
			// NEED TO PUT CODE HERE to store any output variables' values in results array:-
			if (_plSQL && getTotalOutputParameters() > 0) {
				_contentsOfOutputVariables = new Object[ getTotalOutputParameters() ];
				for(i=0; i<getTotalOutputParameters(); i++) {
					_contentsOfOutputVariables[i] = ((CallableStatement)stmt).getObject(i+1);
				}
			}
			_results = arrangeResults(stmt.getResultSet());
			_headings = getHeadings(stmt.getResultSet());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (_connection != null) {
				_connection.close();
			}
		}
	}

	public ArrayList getHeadings() {
		return _headings;
	}
	
	private ArrayList getHeadings(ResultSet rs) {
		ArrayList headings=new ArrayList();
		if(rs==null)
		{
			return headings;
		}
        try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int numCols = rsmd.getColumnCount();
			
			for (int i = 1; i <= numCols; i++) 
			{
				String columnLabel = rsmd.getColumnLabel(i);
				headings.add(columnLabel);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
        return headings;
	}
	
	/**
	 * Scans through the SQL constructing the SQL parameters and the run parameters from the
	 * variables read in from the properties file which are passed in. 
	 * @param sql The SQL to run.
	 * @param allVariables The master list of variables as read in from the appropriate properties file.
	 */	
	private void populateParameters(String sql, OrderedHashMap allVariables)
		throws Exception
	{
		_sqlParameters=new ArrayList();
	 	_runParameters = new ArrayList();
	 	OrderedHashMap hm = new OrderedHashMap();
	 	SortedSet hmSorted = new TreeSet();
	 	_totalOutputParameters = 0;
	 	_totalInputParameters = 0;
		for(int i=0;i<sql.length();i++)
		{
			char character=sql.charAt(i);
			// highlight the variables
			if(character=='?')
			{
				// look for a variable
				int comment=sql.toString().indexOf("--",i);
				int endOfLine=sql.toString().indexOf("\n",comment);
				String variable=sql.substring(comment+2, endOfLine).trim();
				if(comment>-1)
				{
					ReportVariable rv = (ReportVariable)allVariables.get(variable.toLowerCase());
					if (rv != null) {
						_sqlParameters.add(rv);
						if (!rv.isOutput()) { // If not output variable then add to hashmap so can construct run parameters later on in correct order
							// Find out which element in the list it is.
							int n = 0, v=-1;
							for(Enumeration en = allVariables.keys(); en.hasMoreElements(); ) {
								String o = (String) en.nextElement();
								if (o.equalsIgnoreCase(variable)) {
									v = n;
									break;
								}
								n++;
							}
							if (v>-1) {
								Integer t = new Integer(v);
								if (hm.get(t)==null) {
									hm.put(t, rv);
									hmSorted.add(t);
								}
							} else {
								throw new RuntimeException("Something strange has happened");
							}
						} else {
							_totalOutputParameters ++;
						}
					} else {
						throw new RuntimeException("Variable not found: " + variable);
					}
				}
				else
				{
					// TODO does not work
					throw new Exception("missing comment ");
				}
			}
		}
		_runParameterNamesToSubscripts = new HashMap();
		for(Iterator it = hmSorted.iterator(); it.hasNext(); ) {
			Integer t = (Integer) it.next();
			ReportVariable r = (ReportVariable) hm.get(t);
			_runParameterNamesToSubscripts.put(r.getName(), new Integer(_totalInputParameters++));
			_runParameters.add( r );
		}
		
		_expectedParameters = "";
		for(Iterator it = _runParameters.iterator(); it.hasNext();) {
			ReportVariable rv = (ReportVariable)it.next();
			_expectedParameters += rv;
			if (it.hasNext()) {
				_expectedParameters += ", ";
			}
		}
	}


	/**
	 * Format result set as a String array.
	 * @param rs The result set to be formatted
	 * @return String array containing results
	 * @throws Exception
	 */
	public String[][] arrangeResults(ResultSet rs)
		throws Exception
	{
		if (rs == null) {
			return null;
		}
		List headings = new ArrayList();
	
		try
		{
			ResultSetMetaData rsmd = rs.getMetaData();
	        int numCols = rsmd.getColumnCount();
	        for (int i = 1; i <= numCols; i++) 
	        {
	        	String columnLabel = rsmd.getColumnLabel(i);
	        	headings.add(columnLabel);
	        }
	        
	        ArrayList rows=new ArrayList();
	        while(rs.next())
	        {
	        	String[] results=new String[numCols];
	        	for(int i=0;i<numCols;i++)
	            {
	        		//System.out.println("i="+(i+1)+" col="+rsmd.getColumnName(i+1));
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
	        return allResults;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		} finally {
	        rs.close();
		}
	}	
	
	/**
	 * Return the expectedParameters
	 * @return
	 */
	public String getExpectedParameters() {
		return _expectedParameters;
	}
	
	/**
	 * Returns sql string
	 */
	public String toString() {
		return "Expected parameters: " + _expectedParameters + ". Query text: " + _sql;
	}
	
	/**
	 * Check if use pl sql
	 * @return
	 */
	public boolean isPlSQL() {
		return _plSQL;
	}
	
	/**
	 * Get the getTotalInputParameters
	 * @return
	 */
	public int getTotalInputParameters() {
		return _totalInputParameters;
	}
	
	/**
	 * Get the totalOutputParameters
	 * @return
	 */
	public int getTotalOutputParameters() {
		return _totalOutputParameters;
	}
	
	/**
	 * Get Results 
	 * @return
	 */
	public String[][] getResults() {
		return _results;
	}
	
	/**
	 * Get the  contentsOfOutputVariables
	 * @return
	 */
	public Object[] getContentsOfOutputVariables() {
		return _contentsOfOutputVariables;
	}	
	
	private String stripOutComments( String sql ) {
		StringBuffer newSQL = new StringBuffer(sql);
		boolean commentsFound=true;
		while (commentsFound==true)
		{
			int comment=newSQL.toString().indexOf("--");
			int endOfLine=newSQL.toString().indexOf("\n",comment);
			if(comment==-1)
			{
				commentsFound=false;
				continue;
			}
			if(endOfLine>0)
			{
				newSQL.replace(comment,endOfLine,"\n");
			}
			else
			{
				newSQL.replace(comment,newSQL.length()-1,"\n");
			}
		}
		newSQL=new StringBuffer(Strings.replace(newSQL.toString(),"\n"," "));
		return newSQL.toString();
	}
	
	/**
	 * Hard coded connection
	 * @throws Exception
	 */
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
        	e.printStackTrace();
        	throw new Exception("Error with parameters (dBUrl/dBDriver/dBUser/dBPassword)=("+
        			dBUrl+"/"+dBDriver+"/"+dBUser+"/"+dBPassword+")\n"
        			+e.getMessage());
        	
        }
    }
    
    /**
     * Disconnect from db
     * @throws Exception
     */
    private void disconnect()
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

	/**
	 * Merge the handed sql with the passed arguments 
	 * @param args The args to substitute into the SQL.
	 * @return The SQL with the args substituted.
	 */
	private String mergeArguments(String[] args)
	{
		StringBuffer newSQL=new StringBuffer(_sql);
		if(args==null || args.length == 0)
		{
			return _sql;
		} else if (args.length != _runParameters.size()) {
			throw new RuntimeException("Arguments/parameters mismatch: expected " + _runParameters.size() + " (" + _expectedParameters + ") but received " + args.length); 
		}
		
		int i=0, c=0;
		for(Iterator it=_sqlParameters.iterator(); it.hasNext(); ) {
			c++;
			ReportVariable variable = (ReportVariable) it.next();
			int index=newSQL.toString().indexOf("?", i);
			if (variable.isOutput()) { // If it's an output variable then we don't want to replace this question mark so ignore this one for the rest of this loop
				i = index + 1;
			} else {
				int subscript = ((Integer)_runParameterNamesToSubscripts.get(variable.getName())).intValue();
				if (subscript < 0 || subscript >= args.length) {
					throw new RuntimeException("Unable to find " + variable.getName() + " parameter");
				} else {
					if (variable.getType() == ReportVariable.INTEGER) {
						newSQL.replace(index,index+1,args[subscript]);
					} else {
						newSQL.replace(index,index+1,"'"+args[subscript]+"'");
					}
				}
			}
		}
		return newSQL.toString();
	}
	
	/**
	 * Output the results to screen
	 * Debug tool realy.
	 *
	 */
	public void outputResults() {
		if (_results == null) {
			System.out.println( "No results.");
		} else {
			System.out.println( "Results:-");
			for(int i=0; i<_results.length; i++) {
				String s = "";
				for(int j=0; j<_results[i].length; j++) {
					s += _results[i][j] + ",";
				}
				System.out.println(s);
			}
		}
		if (this._contentsOfOutputVariables == null) {
			System.out.println( "No output variables.");
		} else {
			System.out.println( "Output Variables:-");
			for(int i=0; i<_contentsOfOutputVariables.length; i++) {
				System.out.println( _contentsOfOutputVariables[i]);
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// *************** TESTING 
	
	
	
	// just for testing
	private static Properties getTestProperties1()
	{
		Properties returnProperties=new Properties();
		returnProperties.put("DBUrl","jdbc:oracle:thin:@jupiter:1521:cdrd");
		returnProperties.put("DBDriver","oracle.jdbc.OracleDriver");
		returnProperties.put("DBUser","lorob");
		returnProperties.put("DBPassword","lorob1");
		return returnProperties;
	}
	

	
//	 just for testing
	private static Properties getTestProperties2()
	{
		Properties returnProperties=new Properties();
		returnProperties.put("DBUrl","jdbc:odbc:Excel Files;DBQ=c:/w.xls");
		returnProperties.put("DBDriver","sun.jdbc.odbc.JdbcOdbcDriver");
		returnProperties.put("DBUser","");
		returnProperties.put("DBPassword","");
		return returnProperties;
	}

	private static Properties getTestProperties3()
	{
		Properties returnProperties=new Properties();
		returnProperties.put("DBUrl","jdbc:neon:DB2T;DSN=NONEED;CPFX=FA1TEST;DBD=FA1TEST;DBTY=DB2;Description=DAO/CIP;HOST=mvsd;LINK=TCPIP;PLAN=SDTCCIP;PORT=1326;SUBSYS=DB2T;");
		returnProperties.put("DBDriver","com.neon.jdbc.Driver");
		returnProperties.put("DBUser","$GROB");
		returnProperties.put("DBPassword","stoats1");
		return returnProperties;
	}	

	/**
	 * Test which runs some of Louis' ordinary SQL (not PL/SQL) with no output parameters.
	 */
	private static void testLouisNonPLSQL() {
		try {
			String currentSubDirectory = "F:\\CSD\\teams\\system_test\\systest\\CIP system test\\Utilities\\STEPdist\\sql\\Product Search";
			SQLQuery sqlQuery = new SQLQuery(currentSubDirectory + "\\Product Search.sql", currentSubDirectory + "\\variables.properties", false,getTestProperties1() );
			System.out.println( "\nExpected SQL query parameters: " + sqlQuery.getExpectedParameters() + ", Total input parameters = " + sqlQuery.getTotalInputParameters() + ", Total output parameters = " + sqlQuery.getTotalOutputParameters() );
			sqlQuery.run( new String[] {"01-06-2007", "Aspirin"} );
			sqlQuery.outputResults();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Test which runs PL/SQL including two output parameters.
	 */
	private static void testPLSQL() {
		try {
			String currentSubDirectory = "C:\\louis\\LouisStuff\\Stuff";
			SQLQuery sqlQuery = new SQLQuery(currentSubDirectory + "\\test.sql", currentSubDirectory + "\\variables.properties", true,getTestProperties1() );
			System.out.println( "\nExpected SQL query parameters: " + sqlQuery.getExpectedParameters() + ", Total input parameters = " + sqlQuery.getTotalInputParameters() + ", Total output parameters = " + sqlQuery.getTotalOutputParameters() );
			sqlQuery.run( new String[] {"1", "1"} );
			sqlQuery.outputResults();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
     * Test which collates 3 Excel spreadsheets into one then performs an SQL
     * select on the resulting spreadsheet writing the results out to another
     * spreadsheet.
     */
    private static void testExcelSQL() {
        try {
            // First copy the workbooks into one spreadsheet.
            ExcelWorkbook.copyWorkbook(new String[][] { new String[] { "c:\\ETPBatches\\Batch_4_Raw_Data.xls", "Data" },
                    new String[] { "c:\\ETPBatches\\Batch_5_Raw_Data.xls", "Data" },
                    new String[] { "c:\\ETPBatches\\FJE40_200705.xls", "PACT", "4" } }, "c:\\tempreport.xls", new String[][] {
                    new String[] { "0", "Batch4" }, new String[] { "1", "Batch5" }, new String[] { "2", "Actual" } });

            // ..because the PACT spreadsheet has extra lines at the top, need
            // to delete these, hence the extra 3 above

            // Now do SQL select from this spreadsheet and store the result in
            // another spreadsheet.
            String currentSubDirectory = "C:\\Louis\\LouisStuff\\Stuff";
            SQLQuery sqlQuery = new SQLQuery(currentSubDirectory + "\\test2.sql", currentSubDirectory
                    + "\\variables.properties", false,getTestProperties2() );
            System.out.println("\nExpected SQL query parameters: " + sqlQuery.getExpectedParameters()
                    + ", Total input parameters = " + sqlQuery.getTotalInputParameters()
                    + ", Total output parameters = " + sqlQuery.getTotalOutputParameters());
            sqlQuery.run(new String[] {});
            String filename="c:\\difference_report.xls";
            ExcelWorkbook.outputResultsToExcel(filename, "Difference Report",sqlQuery.getResults());
            ExcelWorkbook.fireExcel(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	/**
	 * Test which runs some of Louis' ordinary SQL (not PL/SQL) with no output parameters.
	 */
	private static void testRonSQL() {
		try {
			String currentSubDirectory=System.getProperty("user.dir");
			currentSubDirectory=currentSubDirectory.replace('\\','/');
			File parent=new File(currentSubDirectory);
			currentSubDirectory=parent.getParentFile().getAbsolutePath()+"/STEP/sql/MDR - PD Move/";
			System.out.println("Cur Dir="+currentSubDirectory);
			
			//String currentSubDirectory = "C:\\WAS6Workspace\\Rel2SysTestUtils\\STEP\\sql\\MDR Data";
			SQLQuery sqlQuery = new SQLQuery(currentSubDirectory + "\\GPmovp.sql", currentSubDirectory + "\\variables.properties", false,getTestProperties3() );
			//sqlQuery.preProcess("abc def #YYYYMM-TO-MONTH-END( '200704'   ) ghi jkl");
			
			//SQLQuery sqlQuery = new SQLQuery(currentSubDirectory + "\\GPnam2.sql", currentSubDirectory + "\\variables.properties", false,getTestProperties3() );
			System.out.println( "\nExpected SQL query parameters: " + sqlQuery.getExpectedParameters() + ", Total input parameters = " + sqlQuery.getTotalInputParameters() + ", Total output parameters = " + sqlQuery.getTotalOutputParameters() );
			sqlQuery.run( new String[] {"200702"} );
			sqlQuery.outputResults();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}    
    
    public static void main(String[] z) 
	{
		testLouisNonPLSQL();
		testPLSQL();
		testExcelSQL();
    	testRonSQL();
    	
	}
}
