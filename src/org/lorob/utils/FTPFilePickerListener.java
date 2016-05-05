package org.lorob.utils;

/**
 * Events fired from a FTPFilePicker
 * @author lorob
 *
 */
public interface FTPFilePickerListener
{
	/**
	 * A Log File has been picked
	 * @param logFile
	 */
	public void fireLogPicked(String logFile);
	
	/**
	 * A File has been picked
	 * @param logFile
	 * @param downloadOnly - true if download only option selected
	 */
	public void fireFilePicked(String file,boolean downloadOnly);
	
	/**
	 * A directory has been picked
	 * @param directory
	 */
	public void fireDirectoryPicked(String directory);
	
	/**
	 * The directory has been request to move up
	 * @param currentDirectory
	 */
	public void fireUpDirectory(String currentDirectory);
	
	/**
	 * The window has been closed without a choice made
	 */
	public void fireClosePicked();
	
}