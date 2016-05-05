package org.lorob.utils;

import java.io.IOException;
import java.util.HashMap;

import ftp.FtpBean;
import ftp.FtpException;
import ftp.FtpListResult;

/**
 * Class to wrap the ftp actions so is accessible from
 * anywhere in the code
 * @author lorob
 *
 */
public class FTPUtils
{	
	private FtpBean _ftp;
	private String _userName;
	private String _password;
	private String _server;
	private String _directory;
	private HashMap _fileList;
	
	/**
	 * Constructor
	 * @param userName
	 * @param password
	 * @param server
	 */
	public FTPUtils(String userName,String password,String server)
	{
		_server=server;
		_userName=userName;
		_password=password;
		_ftp = new FtpBean();
	}
	
	/**
	 * read the ftp directory and set the internal file list
	 * @param logSet
	 * @return
	 * @throws Exception
	 */
	public synchronized Boolean readFtpDirectory(String directory)
		throws Exception
	{
		try
		{
			_directory=directory;
			_ftp.ftpConnect(_server, _userName, _password); 
			FtpListResult ftplrs = null;
			_ftp.setDirectory(directory);
	        // Get its directory content.
	        _fileList=new HashMap();
            ftplrs = _ftp.getDirectoryContent();
            while(ftplrs.next())
            {
                int type = ftplrs.getType();
                if(type == FtpListResult.FILE)
                {
                    // only add files
                	_fileList.put(ftplrs.getName(),new FileDetails(ftplrs.getName(),ftplrs.getSize(),type));
                }
                else if (type ==  FtpListResult.DIRECTORY)
                {
                	// we have a directory
                	_fileList.put(ftplrs.getName(),new FileDetails(ftplrs.getName(),ftplrs.getSize(),type));
                }
            }
			
			return new Boolean(true);
		}
		catch(Exception e)
		{
			throw new Exception("Problem with FTP on "+_server+" using username "+_userName+"\n"+e.toString(),e);
		}
	}
	
	/**
	 * Get the directory we are at
	 * @return
	 */
	public String getDirectory()
	{
		return _directory;
	}
	
	/**
	 * Get the list of files and directories we have just read
	 * @return
	 */
	public HashMap getFileList()
	{
		return _fileList;
	}
	
	/**
	 * Get access to the ftp
	 * @return
	 */
	public FtpBean getFTPBean()
	{
		return _ftp;
	}
	
	/**
	 * Connect to the ftp source
	 * @throws FtpException
	 */
	public void connect()
		throws FtpException,IOException
	{
		_ftp.ftpConnect(_server, _userName, _password); 
	}
	
}
