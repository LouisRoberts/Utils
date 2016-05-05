package org.lorob.utils;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

public class FTPFilePicker extends JDialog
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7496162228211843316L;
	private FTPFilePickerListener _fTPFilePickerListener;
	private JList _fileList;
	private JCheckBox _downloadOnly;
	private JLabel _directoryLabel;
	private JButton _upDirectoryButton;
	private int _dataSize;
	private String _currentDirectory;
	
	/**
	 * Constructor
	 * @param owner
	 * @param dbMap
	 * @param listener
	 * @param directoryPath
	 */
	public FTPFilePicker(Frame owner,HashMap dbMap,FTPFilePickerListener listener,String directoryPath)
	{
		super(owner,"Pick File To Load",true);
		//Set dbKeys= dbMap.keySet();
		_fTPFilePickerListener=listener;
        _currentDirectory=directoryPath;
        setSize(400,400);
        // sort handed list alphabetically
        Vector sortedData=sortData(dbMap);
        
        // create GUI components
        _fileList = new JList();
		_fileList.setListData(sortedData);
		_fileList.setCellRenderer(new MyCellRenderer());
		JScrollPane scroll=new JScrollPane(_fileList);
		_directoryLabel=new JLabel("<html>Directory:  <u>"+directoryPath+"</u></html>");
		_upDirectoryButton=new JButton("Move Up Directory");
		_downloadOnly=new JCheckBox("Download File Only. Do Not Display.");
//		 add listeners
		addListeners();
		
		// add components to screen
		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(2,2,2,2);
		gbc.gridx=0;
		gbc.gridy=0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx=0.0;
		gbc.weighty=0.0;
		gbc.anchor=GridBagConstraints.NORTHWEST;
		
		// add directory label
		this.getContentPane().add(_directoryLabel,gbc);
		
		// add download checkbox
		gbc.gridy++;
		gbc.gridwidth = 1;
		this.getContentPane().add(_downloadOnly,gbc);
		
		// add up directory button
		gbc.gridx++;
		this.getContentPane().add(_upDirectoryButton,gbc);
		
		// add list
		gbc.gridx=0;
        gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx=1.0;
		gbc.weighty=1.0;
		gbc.gridy++;
		this.getContentPane().add(scroll,gbc);
        
		setVisible(true);
	}
	
	/**
	 * Sort the data alphabetically
	 * @param dbMap
	 * @return
	 */
	private Vector sortData(HashMap dbMap)
	{
		// There may be an issue here that
		// a directory and a file with the same name will be seen 
		// as the same thing and disappear
		if(dbMap!=null)
        {
        	System.out.println(dbMap.size()+" files found");
        }
//      make the list alphabetical
        TreeSet treeSet=new TreeSet();
		SortedSet sortedList = Collections.synchronizedSortedSet(treeSet);
		
		if(dbMap!=null)
		{
	        Iterator iter=dbMap.keySet().iterator();
	        while(iter.hasNext())
	        {
	        	FileDetails thisFileDetails=(FileDetails)dbMap.get((String)iter.next());
	        	treeSet.add(thisFileDetails.toString());
	        }
	        _dataSize=treeSet.size();
		}
//		 sort the list
		Vector data=new Vector();
		synchronized(sortedList) 
		{
		      Iterator i = sortedList.iterator(); // Must be in the synchronized block
		      while (i.hasNext())
		      {
		    	  String fileName=(String)i.next();
		    	  FileDetails thisFileDetails=(FileDetails)dbMap.get(fileName);
		    	  data.add(thisFileDetails);
		      }
		 }
		return data;
	}
	
	/**
	 * Add the listeners
	 *
	 */
	private void addListeners()
	{
		_upDirectoryButton.addActionListener(new ActionListener()
		{

			/* (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent arg0)
			{
				dispose();
				_fTPFilePickerListener.fireUpDirectory(_currentDirectory);
			}
			
		});
		
		MouseListener mouseListener = new MouseAdapter() 
		{
			    
			public void mouseClicked(MouseEvent e) 
	        {
                if (e.getClickCount() == 1) 
                {
                	FileDetails key=(FileDetails)_fileList.getSelectedValue();
                	final int index = _fileList.getSelectedIndex();
                    if(index>=0&&index<_dataSize)
                    {
                    	boolean downloadOnly=_downloadOnly.isSelected();
                    	if(key.getType()==FileDetails.FILE)
                    	{
                    		dispose();
                    		_fTPFilePickerListener.fireFilePicked(key.getFileName(),downloadOnly);
                    	}
                    	else
                    	{
                    		// directory
                    		// we need to reload ourselves and delve down
                    		dispose();
                    		_fTPFilePickerListener.fireDirectoryPicked(key.getFileName());
                    	}
                    }
                }
	            
	        }
			
		};
	    _fileList.addMouseListener(mouseListener);
	}
}

/**
 * Straight out of the Javadocs 
 * @author lorob
 *
 */
class MyCellRenderer extends JLabel implements ListCellRenderer {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8074369018778131961L;
	private ImageIcon _fileIcon;
    private ImageIcon _dirIcon;
    
    MyCellRenderer()
    {
    	_fileIcon=ImageLoader.loadImage(this,FilePicker.class,"file");
    	_dirIcon=ImageLoader.loadImage(this,FilePicker.class,"dir");
    }
    // This is the only method defined by ListCellRenderer.
    // We just reconfigure the JLabel each time we're called.

    public Component getListCellRendererComponent(
      JList list,
      Object value,            // value to display
      int index,               // cell index
      boolean isSelected,      // is the cell selected
      boolean cellHasFocus)    // the list and the cell have the focus
    {
    	FileDetails file=(FileDetails)value;
        String s = file.toString();
        setText(s);
        
        if(file.getType()==FileDetails.FILE)
        {
        	setIcon(_fileIcon);
        }
        else
        {
        	setIcon(_dirIcon);
        }
        	
        if (isSelected) 
  	   	{
  	   		setBackground(list.getSelectionBackground());
	        setForeground(list.getSelectionForeground());
  	   	}
        else 
        {
	       setBackground(list.getBackground());
	       setForeground(list.getForeground());
        }
  	   	setEnabled(list.isEnabled());
  	   	setFont(list.getFont());
        setOpaque(true);
        return this;
    }
}
