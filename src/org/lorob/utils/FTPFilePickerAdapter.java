package org.lorob.utils;

import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import ftp.FtpListResult;
import ftp.FtpObserver;

/**
 * Class implementing the FTPFilePickerListener
 * to provide some default behaviour
 * 
 * To use
 * Create an adapter. Fill in the abstract functions
 * if you wish to monitor what is happening. 
 * parseError - should be used to display an error message if the ftp has gone wrong
 * fireFilePickedStart - is called before the file is ftped
 * fireFilePickedFinished - is called when the ftp has finished
 * triggeredRoundedPercentage - is called when the file percentage goes over an integer percentage i.e. 2 - 3
 * 
 * If you pass ftpObserver this will be called during a read or write. it can be null if you are not interested
 *
 * Frame parent=null; // or owner frame
 * FTPLogProperties properties=new FTPLogProperties("ftpProperties.properties",FTPFilePickerAdapter.class);  // contains username,password,server and aliases
 * FtpObserver ftpObserver=null; // if want to keep track of state
 * 
 * FTPFilePickerAdapter adapter=new FTPFilePickerAdapter(parent,properties,ftpObserver)
 * {
 * 	public void triggeredRoundedPercentage(int percentage){};
 *	public void parseError(Exception e){};
 *	public void fireFilePickedFinished(String filename){};
 *	public void fireFilePickedStart(){};
 * };
 *
 * adapter.fireLogPicked(ftpAlias);
 * 
 * @author lorob
 *
 */
public abstract class FTPFilePickerAdapter implements FTPFilePickerListener,FTPAccessDialogListener,FtpObserver
{
	private FTPUtils _ftpUtils;
	private HashMap _fileList;
	private FTPFilePickerAdapter _this;
	private FTPLogProperties _ftpLogProperties;
	private String _fileName;
	
	// status of download
	private long _fileSize;
	private long _numOfBytes = 0; 
	private long _oldFileSize=0;
	
	// listener for ftp events
	private FtpObserver _ftpObserver;
	
	// calling class
	private Frame _parent;
	
	private static String DEFAULT_DIRECTORY="c:/";
	private String _destinationDirectory=DEFAULT_DIRECTORY;
	
	/**
	 * Constrcutor
	 * @param parent
	 * @param properties
	 * @param ftpObserver can be null 
	 */
	public FTPFilePickerAdapter (Frame parent,FTPLogProperties properties,FtpObserver ftpObserver)
		throws Exception
	{
		_parent=parent;
		_this=this;
		_ftpLogProperties=properties;
		_ftpObserver=ftpObserver;
	}

	/* (non-Javadoc)
	 * @see org.lorob.FTPFilePickerListener#fireDirectoryPicked(java.lang.String)
	 */
	public void fireDirectoryPicked(String directory)
	{
		String directoryPath=_ftpUtils.getDirectory()+"/"+directory;
		readFtpDirectory(directoryPath);
		if(_fileList!=null)
		{
			new FTPFilePicker(_parent,_fileList,_this,directoryPath );
		}
		else
		{
			// failed to get ftp directory
			GUIUtils.showInformationDialog(_parent,"Error Reading FTP Directory. See screen for error information.","FTP directory Error");
		}
		
	}

	/* (non-Javadoc)
	 * @see org.lorob.FTPFilePickerListener#fireFilePicked(java.lang.String, boolean)
	 */
	public void fireFilePicked(String inFile, boolean downloadOnly)
	{
		fireFilePickedStart();
		final String file=inFile;
		final boolean downloadFlag = downloadOnly;
		SwingWorker worker = new SwingWorker()
		{
			public Object construct()
		    {
				FileDetails thisFile=(FileDetails)_fileList.get(file);
				_fileSize=thisFile.getFileSize();
				_fileName=null;
				int type=thisFile.getType();
				
				if(_fileSize==0&&type == FtpListResult.FILE)
				{
					// nothing to do
					return null;
				}
				else if (type == FtpListResult.DIRECTORY)
				{
					// This should not happen as intercepted
					// at action listener
					return null;
				}
				// copy across
				
				try
				{
					_fileName=getFile(thisFile.getFileName(),thisFile.getFileSize());
					GUIUtils.showInformationDialog(_parent,"File has been copied across to "+_fileName,"File Loaded Successfully");
				}
				catch(Exception e)
				{
					_this.parseError(e);
				} 
				return null;
		    }
			public void finished() 
			{
				fireFilePickedFinished(_fileName,downloadFlag);
			}
		};
		worker.start();
	}

	/* (non-Javadoc)
	 * @see org.lorob.FTPFilePickerListener#fireLogPicked(java.lang.String)
	 */
	public void fireLogPicked(String logFile)
	{
		// FTP The file Across
		final FTPLogSet theUsedLogSet=(FTPLogSet)_ftpLogProperties.getLogs().get(logFile);
		readFtpDirectory(theUsedLogSet.getLogLocation());
		if(_fileList!=null)
		{
			new FTPFilePicker(_parent,_fileList,_this,_ftpUtils.getDirectory());
		}
		else
		{
			// failed to get ftp directory
			GUIUtils.showInformationDialog(_parent,"Error Reading FTP Directory. See screen for error information.","FTP directory Error");
		}
	}

	/* (non-Javadoc)
	 * @see org.lorob.FTPFilePickerListener#fireUpDirectory(java.lang.String)
	 */
	public void fireUpDirectory(String currentDirectory)
	{
		int lastSlash=currentDirectory.lastIndexOf("/");
		if(lastSlash<=0)
		{
			GUIUtils.showInformationDialog(_parent,"You can not move further up the directory structure","FTPdirectory Error");
			new FTPFilePicker(_parent,_fileList,_this,currentDirectory );
			return;
		}
		String newPath=currentDirectory.substring(0,lastSlash);
		readFtpDirectory(newPath);
		if(_fileList!=null)
		{
			new FTPFilePicker(_parent,_fileList,_this,newPath );
		}
		else
		{
			// failed to get ftp directory
			GUIUtils.showInformationDialog(_parent,"Error Reading FTP Directory. See screen for error information.","FTP directory Error");
		}
	}
	
	/**
	 * Read the directory
	 * @param logSet
	 * @return
	 */
	public synchronized Boolean readFtpDirectory(String location)
	{
		try
		{
			
			String userName=_ftpLogProperties.getLogProperties().getProperty(FTPLogProperties.FTP_USER);
			String password=_ftpLogProperties.getLogProperties().getProperty(FTPLogProperties.FTP_PASSWORD);
			String server=_ftpLogProperties.getLogProperties().getProperty(FTPLogProperties.FTP_SERVER);
			if(userName.compareTo(FTPLogProperties.DEFAULT)==0||
           			password.compareTo(FTPLogProperties.DEFAULT)==0)
           	{
           		// we need to force user to set a user name
           		// and password
           		new FTPAccessDialog(_parent,userName,password,this,server);
           	}
			// try username and password again incase they have changed
			
			userName=_ftpLogProperties.getLogProperties().getProperty(FTPLogProperties.FTP_USER);
			password=_ftpLogProperties.getLogProperties().getProperty(FTPLogProperties.FTP_PASSWORD);
			if(userName.compareTo(FTPLogProperties.DEFAULT)==0||
           			password.compareTo(FTPLogProperties.DEFAULT)==0)
			{
				// ftp still wrong
				return new Boolean(false);
			}
			_ftpUtils=new FTPUtils(userName,password,server);
			_ftpUtils.readFtpDirectory(location);
           	_fileList=_ftpUtils.getFileList();
			
			return new Boolean(true);
		}
		catch(Exception e)
		{
			this.parseError(e);
			return new Boolean(false);
		}
	}

	/* (non-Javadoc)
	 * @see org.lorob.FTPAccessDialogListener#fireFTPLoggingDetailsChanged(java.lang.String, java.lang.String)
	 */
	public void fireFTPLoggingDetailsChanged(String userName, String password)
	{
		_ftpLogProperties.getLogProperties().setProperty(FTPLogProperties.FTP_USER,userName);
		_ftpLogProperties.getLogProperties().setProperty(FTPLogProperties.FTP_PASSWORD,password);
	}
	
	/**
	 * get the file via ftp
	 * @param name
	 * @param size
	 * @return Filename if successful
	 * @throws Exception
	 */
	private String getFile(String name,long size)
		throws Exception
    {
		_numOfBytes=0;
        // get the file and read it to the stream
        byte[] file=_ftpUtils.getFTPBean().getBinaryFile(name,0, this);
        
        //byte[] file=mFtp.getBinaryFile(name);
        File newCopyFile=new File(getDestinationDirectory()+name);
        
        FileOutputStream out=new FileOutputStream(newCopyFile);
        out.write(file);
        out.close();
        return newCopyFile.getAbsolutePath();
    }
	
	/* (non-Javadoc)
	 * @see ftp.FtpObserver#byteRead(int)
	 */
	public void byteRead(int bytes)
	{
//		 only update screen if there has been a change
		_numOfBytes += bytes;
		if(_fileSize>0)
		{
			int size=(int)(_numOfBytes*100/_fileSize);
			if(_ftpObserver!=null)
			{
				_ftpObserver.byteRead(bytes);
			}
			if(size!=_oldFileSize)
			{
				triggeredRoundedPercentage(size,_fileSize);
			}
			_oldFileSize=size;
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.lorob.FTPFilePickerListener#fireClosePicked()
	 */
	public void fireClosePicked()
	{
		// The window has been closed.
	}
	
	/**
	 * Get the current percentage downloaded
	 * This is triggered for every buffered ftp read
	 * @return the current percentage downloaded
	 */
	public long getPercentageDownload()
	{
		return (_numOfBytes*100/_fileSize);
	}

	/* (non-Javadoc)
	 * @see ftp.FtpObserver#byteWrite(int)
	 */
	public void byteWrite(int bytes)
	{
		if(_ftpObserver!=null)
		{
			_ftpObserver.byteWrite(bytes);
		}
	}
	
	
	
	/**
	 * This is called when the rounded percentage has changed
	 * i.e. from 2 to 3.
	 * @param percentage
	 * @param filesize
	 */
	public abstract void triggeredRoundedPercentage(int percentage,long fileSize);

	
	/**
	 * This is called when an error occurs during the ftping or reading of
	 * files
	 * @param e
	 * @throws exception if user wishes
	 */
	public abstract void parseError(Exception e);
	
	/**
	 * This is called when the ftp process has completed
	 * @param filename - the local file name the file has been copied to
	 * @param downloadOnly - true if download checked
	 */
	public abstract void fireFilePickedFinished(String filename,boolean downloadOnly);
	
	/**
	 * Called before the file is about to be downloaded
	 *
	 */
	public abstract void fireFilePickedStart();
	
	public static void main(String[] args)
	{
		System.out.println("Test");
		try
		{
			Frame parent=null; // or owner frame
			FTPLogProperties properties=new FTPLogProperties("test.properties",FTPFilePickerAdapter.class,-1);  // contains username,password,server and aliases
			FtpObserver ftpObserver=null; // if want to keep track of state
			
			FTPFilePickerAdapter adapter=new FTPFilePickerAdapter(parent,properties,ftpObserver)
			{
			 	public void triggeredRoundedPercentage(int percentage,long fileSize){}
				public void parseError(Exception e)
				{System.out.println("parse error="+e.toString());e.printStackTrace();}
				public void fireFilePickedFinished(String filename,boolean downloadOnly){System.exit(0);}
				public void fireFilePickedStart(){}
			};
		
			adapter.fireLogPicked("CIP_SYST1_CLUSTER1");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getDestinationDirectory()
	{
		return _destinationDirectory;
	}

	public void setDestinationDirectory(String destinationDirectory)
	{
		this._destinationDirectory = destinationDirectory;
	}
	
	public FTPUtils getFTPUtils()
	{
		return _ftpUtils;
	}
}
