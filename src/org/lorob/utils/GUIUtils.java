package org.lorob.utils;

import java.awt.Frame;

import javax.swing.JOptionPane;

/**
 * A few dialog helpers
 * @author lorob
 *
 */
public class GUIUtils
{
	/** showConfirmDialog
	 * @param message
	 * @param header
	 * @return
	 */
	public static int showConfirmDialog(Frame frame,String message,String header)
	{
		return JOptionPane.showConfirmDialog(frame, message, header, JOptionPane.YES_NO_OPTION);
	}
	
	/** showInformationDialog
	 * @param frame
	 * @param message
	 * @param header
	 * @return
	 */
	public static void showInformationDialog(Frame frame,String message,String header)
	{
		JOptionPane.showMessageDialog(frame, message, header, JOptionPane.INFORMATION_MESSAGE);
	}
}
