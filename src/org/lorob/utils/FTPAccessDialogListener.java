package org.lorob.utils;

/**
 * Listener fired by FTPAccessDialog
 * Always one to update status of one self during long ftp ing
 * @author lorob
 *
 */
public interface FTPAccessDialogListener
{
	/**
	 * Fired when log in details changed
	 * @param userName
	 * @param password
	 */
	public void fireFTPLoggingDetailsChanged(String userName,String password);
	

}
