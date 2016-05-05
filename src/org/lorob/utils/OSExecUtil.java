package org.lorob.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * Class to launch programs
 * Pass either a command line or pass a file and
 * the default program associated will launch
 * @author lorob
 *
 */
public class OSExecUtil
{
	private String _osCommand;
	private String _command;
	private File _file;
	private StringBuffer _standardOut;
	private StringBuffer _standardErrOut;
	private static Properties _osScripts; // for performance
	private static Logger _logger = MyLogger.getLogger(OSExecUtil.class.getName());
	
	// since the VM will only be on 1 operating system,
	// seems pointless to load up cmd for other operating systems
	/**
	 * Constructor
	 * @param command - command line to run
	 * @throws Exception
	 */
	public OSExecUtil(String command)
		throws Exception
	{
		_osCommand=initProperty();
		_command=command;
		_standardOut=new StringBuffer();
		_standardErrOut=new StringBuffer();
	}
	
	/**
	 * Query the launch parameters
	 * @return
	 */
	public String getOSLaunchParameters()
	{
		return _osCommand;
	}
	
	/**
	 * Constuctor
	 * @param file - file to launch using native OS program
	 * @throws Exception
	 */
	public OSExecUtil(File file)
		throws Exception
	{
		 _command= initProperty();
		_file=file;
		_standardOut=new StringBuffer();
		_standardErrOut=new StringBuffer();
		initFileParameters();
	}
	
	/**
	 * Checks if the Operating System is a supported Executable
	 * @return
	 */
	public boolean isOSSupported()
	{
		String scriptName = System.getProperty("os.name");
		if(_osScripts.getProperty(scriptName)==null)
		{
			return false;
		}
		return true;
	}
	
	/**
	 * Is the handed extension supported
	 * @param extension
	 * @return
	 */
	public boolean isExtensionSupported(String extension)
	{
		String scriptName = System.getProperty("os.name");
		scriptName = scriptName.replace(' ','.');
		scriptName=scriptName+".supported";
		String supported=_osScripts.getProperty(scriptName);
		if(supported==null)
		{
			return false;
		}
		// tokenise
		extension=extension.toLowerCase();
		StringTokenizer st = new StringTokenizer(supported,",");
		while (st.hasMoreTokens()) 
		{
			String thisToken=st.nextToken().toLowerCase();
			if(thisToken.compareTo(extension)==0)
			{
				return true;
			}
	    }
		return false;
	}
	
	/**
	 * Initialise one self from property file
	 * @return
	 * @throws Exception
	 */
	private String initProperty()
		throws Exception
	{
		String scriptName = System.getProperty("os.name");
        // do we have a value ?
        scriptName = scriptName.replace(' ','.');
        _logger.info("using os="+scriptName);
        
		if(_osScripts==null)
		{
			_osScripts = new Properties();
			_osScripts.load(OSExecUtil.class.getResourceAsStream("OSExec.properties"));
	        if(scriptName==null||scriptName.length()==0)
	        {
	        	_standardErrOut.append("No script found for "+scriptName);
	            scriptName= "Windows";
	            _logger.info("Assuming default Windows operating system");
	        }
		}
        
		return _osScripts.getProperty(scriptName);
	}
	
	/**
	 * init parameters depending on OS
	 *
	 */
	private void initFileParameters()
	{
		_command=_command+/*" /path \""+_file.getParent()+"\"+*/
		" /d \""+_file.getParent()+"\" "+
		_file.getName();
	}
	
	/**
	 * Execute
	 * @return
	 * @throws ExecException
	 */
	public boolean winExecute(boolean addLaunch)
		throws ExecException
	{
		if(addLaunch)
		{
			//_command=getOSLaunchParameters()+_command;
		}
	    Runtime r = Runtime.getRuntime();
		int result =0;
		try
		{
			_logger.info("OSEXEC COMMAND="+_command);
			//_logger.info("OSEXEC COMMAND="+_command);
			Process  proc = r.exec( _command );
			//_logger.info("finished"+_command);
			
			// loop here to display any info
			String s_out;
			String s_err;
			//_logger.info("1");
			BufferedReader br_out = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			//_logger.info("2");
			BufferedReader br_error = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			//_logger.info("3");
			while( br_out.ready() && (s_out = br_out.readLine()) != null &&
				br_error.ready() && (s_err = br_error.readLine()) != null)
			{
				_standardOut.append(s_out);
				_standardErrOut.append(s_err);
				//_logger.info( "s_out="+s_out );				
				//_logger.info( "s_err="+s_err );
			}
			br_out.close();
			br_error.close();
		
			result = proc.waitFor();
			//proc.destroy();
			
		
		}
		catch( Exception e )
		{
			e.printStackTrace();
			throw new ExecException("FAILED_TO_EXECUTE. Error=" +" "+e,e);
		}
		_logger.info("result="+result);
		if(result<=0)
		{
		    // error
			_logger.info("Result="+result);
		    _logger.info("SError="+_standardErrOut.toString());
		    _logger.info("SOut="+_standardOut.toString());
		    return false;
		}
		else
		{
            return true;
        }
	}
	
	/**
	 * Is OS windows
	 * @return
	 * @throws Exception
	 */
	public static boolean isWindowsOS()
		throws Exception
	{
		try
		{
			String scriptName = System.getProperty("os.name");
			if(scriptName.toLowerCase().lastIndexOf("windows")>=0)
			{
				return true;
			}
			return false;
		}
		catch(Exception e)
		{
			return false;
		}
		
	}
	
	/**
	 * Is OS linux
	 * @return
	 * @throws Exception
	 */
	public static boolean isLinuxOS()
		throws Exception
	{
		try
		{
			String scriptName = System.getProperty("os.name");
			if(scriptName.toLowerCase().lastIndexOf("linux")>=0)
			{
				return true;
			}
			return false;
		}
		catch(Exception e)
		{
			return false;
		}
		
	}
	
	/**
	 * Launch the handed file in Excel
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static boolean execExcel(String filename)
		throws Exception
	{
		OSExecUtil exec=new OSExecUtil("cmd /C start /B excel.exe \""+filename+"\"");
		boolean wait=exec.winExecute(true);
		return wait;
	}
	
	/**
	 * Launch The handed file in Internet Explorer
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static boolean execIE(String filename)
		throws Exception
	{
		OSExecUtil exec=new OSExecUtil("cmd /C start /B iexplore.exe \""+filename+"\"");
		boolean wait=exec.winExecute(true);
		return wait;
	}
	
	/**
	 * Test command line launching windows
	 *
	 */
	public static void test1()
	{
		try
		{
			OSExecUtil exec=new OSExecUtil("cmd /K start /B /WAIT C:/Temp/test.xls");
			boolean wait=exec.winExecute(true);
			_logger.info("wait="+wait);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Test file launching windows
	 *
	 */
	public static void test2()
	{
		try
		{
			OSExecUtil exec=new OSExecUtil(new File("C:/Temp/test.xls"));
			boolean wait=exec.winExecute(true);
			_logger.info("wait="+wait);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		test2();
	}
}


