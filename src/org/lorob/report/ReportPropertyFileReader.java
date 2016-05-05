package org.lorob.report;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.lorob.utils.OrderedHashMap;

/**
 * Class to read a property file defining variables
 * @author lorob
 *
 */
public class ReportPropertyFileReader
{
	private static String DATA_DRIVER="DATA_DRIVER";
	private static String VARIABLE="VARIABLE";
	private static String NAME="NAME";
	private static String VALIDATION="VALIDATION";
	private static String TYPE="TYPE";
	private static String FORMAT="FORMAT";
	private static String MESSAGE="MESSAGE";
	private static String FILE_NAME="FILE_NAME";
	private static String DB_NAME="DB_NAME";
	
	private Properties _reportProperties;
	private OrderedHashMap _processedVariables;
	private OrderedHashMap _fileToDBMapping;
	private OrderedHashMap _fileToDriverMapping;
	private File _fileName;
	
	/**
	 * Constructor
	 * @param fileName
	 * @throws Exception
	 */
	public ReportPropertyFileReader(File fileName)
		throws Exception
	{
		_reportProperties=new Properties();
		_fileName=fileName;
		FileInputStream fis=new FileInputStream(fileName);
		_reportProperties.load(fis);
		fis.close();
	}
	
	/** Read the proeprty file and return a hashmap of
	 * ReportVariable keyed by their name 
	 * @return a haspmap of ReportVariable
	 * @throws Exception
	 */
	public OrderedHashMap processFile()
		throws Exception
	{
		if(_processedVariables==null)
		{
			_processedVariables=new OrderedHashMap();
		}
		int index=1;
		boolean more=true;
		while(more==true)
		{
			StringBuffer variableBuffer=new StringBuffer(VARIABLE);
			variableBuffer.append("_");
			variableBuffer.append(index);
			variableBuffer.append("_");
			
			String name=_reportProperties.getProperty(variableBuffer.toString()+NAME);
			
			if(name==null)
			{
				// no more
				more=false;
				continue;
			}
			name=name.toLowerCase();
			String validation=_reportProperties.getProperty(variableBuffer.toString()+VALIDATION);
			String typeStr=_reportProperties.getProperty(variableBuffer.toString()+TYPE);
			String syntax=_reportProperties.getProperty(variableBuffer.toString()+FORMAT);
			String sOutput = _reportProperties.getProperty(variableBuffer.toString()+"OUTPUT","false");
			boolean output = sOutput != null && sOutput.equalsIgnoreCase("true");
			boolean validate=false;
			if(validation.compareTo("true")==0)
			{
				validate=true;
			}
			int type=switchOnStringType(typeStr);
			String message=_reportProperties.getProperty(variableBuffer.toString()+MESSAGE);
			ReportVariable thisVariable=new ReportVariable(name,type,null,validate,syntax,message,output);
			_processedVariables.put(name, thisVariable);
			index++;
		}
		// process db to file mapping
		_fileToDBMapping=new OrderedHashMap();
		_fileToDriverMapping=new OrderedHashMap();
		more=true;
		index=1;
		while(more==true)
		{
			StringBuffer fileNameBuffer=new StringBuffer(FILE_NAME);
			StringBuffer dbBuffer=new StringBuffer(DB_NAME);
			StringBuffer dataDriver=new StringBuffer(DATA_DRIVER);
			fileNameBuffer.append("_");
			fileNameBuffer.append(index);
			dbBuffer.append("_");
			dbBuffer.append(index);
			dataDriver.append("_");
			dataDriver.append(index);
						
			String fileName=_reportProperties.getProperty(fileNameBuffer.toString());
			String dbAlias=_reportProperties.getProperty(dbBuffer.toString());
			String driverFile=_reportProperties.getProperty(dataDriver.toString());
			if(fileName==null)
			{
//				 no more
				more=false;
				continue;
			}
			_fileToDBMapping.put(fileName, dbAlias);
			_fileToDriverMapping.put(fileName, driverFile);
			index++;
		}

		return _processedVariables;
	}
	
	public OrderedHashMap getDriverToFileMapping()
	{
		return _fileToDriverMapping;
	}
	
	public OrderedHashMap getDbToFileMapping()
	{
		return _fileToDBMapping;
	}
	
	/**
	 * Return statis type depending on string type
	 * @param type
	 * @return
	 * @throws Exception if passed string illegal
	 */
	private int switchOnStringType(String type)
		throws Exception
	{
		String typeLower=type.toLowerCase();
		if(typeLower.compareTo("integer")==0)
		{
			return ReportVariable.INTEGER;
		}
		else if(typeLower.compareTo("date")==0)
		{
			return ReportVariable.DATE;
		}
		else if(typeLower.compareTo("float")==0)
		{
			return ReportVariable.FLOAT;
		}
		else if(typeLower.compareTo("string")==0)
		{
			return ReportVariable.STRING;
		}
		else if(typeLower.compareTo("driver")==0)
		{
			return ReportVariable.DRIVER;
		}
		throw new Exception("Unsupported type of '"+type+"' in property file :"+_fileName.toString());
	}
	
}
