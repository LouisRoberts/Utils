package org.lorob.utils;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

public class FilePicker extends JFileChooser
{
	private static final long serialVersionUID = 4263975525443854030L;

	/**
	 * Constructor
	 * @param mode - see JFileChooser
	 */
	public FilePicker(int mode)
	{
		super();
		setFileSelectionMode(mode);
	}
	
	/** getFile
	 * @param parent
	 * @return
	 */
	public File getFile(Component parent)
	{
		 int returnVal = showOpenDialog(parent);
		 if(returnVal == JFileChooser.APPROVE_OPTION) 
		 {
		       return getSelectedFile();
		 }
		 return null;
	}
}
