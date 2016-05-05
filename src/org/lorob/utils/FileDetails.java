package org.lorob.utils;

import ftp.FtpListResult;

/** A container of file type details
 * @author lorob
 *
 */
public class FileDetails extends Object implements Comparable
{
	private String _fileName;
	private long _fileSize;
	private int _type; // file, directory etc
	public static final int FILE=FtpListResult.FILE;
	public static final int DIRECTORY=FtpListResult.DIRECTORY;
	
	/**
	 * Constructor
	 * @param name of file
	 * @param size of file
	 * @param type of file FILE | DIRECTORY
	 */
	public FileDetails(String name,long size,int type)
	{
		_fileName=name;
		_fileSize=size;
		_type=type;
	}
	
	/**
	 * Get the file name
	 * @return
	 */
	public String getFileName()
	{
		return _fileName;
	}
	
	/**
	 * Get the file size
	 * @return
	 */
	public long getFileSize()
	{
		return _fileSize;
	}
	
	/**
	 * Get the type of file
	 * @return
	 */
	public int getType()
	{
		return _type;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return getFileName();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		if(_fileName==null)
		{
			return  (int)_fileSize&7+_type&11;
		}
		else
		{
			return _fileName.hashCode()*3+(int)_fileSize*7+_type*11;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		if(obj==null)
		{
			return false;
		}
		if(!(obj instanceof FileDetails))
		{
			return false;
		}
		FileDetails toCompare=(FileDetails)obj; 
		if(_fileName==null&&toCompare._fileName!=null)
		{
			return false;
		}
		else if(_fileName!=null&&toCompare._fileName==null)
		{
			return false;
		}
		else if(_fileName==null&&toCompare._fileName==null)
		{
			return (_fileSize==toCompare._fileSize&&_type==toCompare._type);
		}
		return (_fileName.equals(toCompare._fileName)&&
				(_fileSize==toCompare._fileSize&&_type==toCompare._type));
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0)
	{
		if(arg0==null)
		{
			return -1;
		}
		FileDetails toCompare=(FileDetails)arg0;
		if(this.equals(toCompare)==true)
		{
			return 0;
		}
		// we are different
		return(this.hashCode()-toCompare.hashCode());
	}
}
