package org.lorob.utils;

import java.awt.Frame;
import java.awt.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JDialog;

public class CIPSystemPicker extends JDialog
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7496162228211843316L;
	private Set _logs;
	private List _fileList;
	private CIPStatusListener _cipListener;
	
	/**
	 * Constructor - use Pick system Log as frame name
	 * @param owner
	 * @param dbKeys
	 * @param listener
	 */
	public CIPSystemPicker(Frame owner,Set dbKeys,CIPStatusListener listener)
	{
		this(owner,dbKeys,listener,"Pick System Log");
	}
	
	/**
	 * Constructor
	 * @param owner
	 * @param dbKeys
	 * @param listener
	 * @param name - name in frame
	 */
	public CIPSystemPicker(Frame owner,Set dbKeys,CIPStatusListener listener,String name)
	{
		super(owner,name,true);
		_cipListener=listener;
        _logs=dbKeys;
        setSize(400,400);
        // add list of files
        _fileList = new List(4, false);
        // make the list alphabetical
        TreeSet treeSet=new TreeSet();
		SortedSet sortedList = Collections.synchronizedSortedSet(treeSet);
		Iterator iter=_logs.iterator();
        
		while(iter.hasNext())
        {
        	treeSet.add((String)iter.next());
        }
//		 sort the list
		synchronized(sortedList) 
		{
		      Iterator i = sortedList.iterator(); // Must be in the synchronized block
		      while (i.hasNext())
		      {
		    	  _fileList.add((String)i.next());
		      }
		 }
        this.getContentPane().add(_fileList);
        // add mouse listener
        MouseListener mouseListener = new MouseAdapter() 
		{
		    
            public void mouseClicked(MouseEvent e) 
            {
                if (e.getClickCount() == 1) 
                {
                	String key=_fileList.getSelectedItem();
                	final int index = _fileList.getSelectedIndex();
                    if(index>=0&&index<_logs.size())
                    {
                    	dispose();
                    	_cipListener.CIPSystemPicked(key);
                    }
                    
                }
            }
        };
        _fileList.addMouseListener(mouseListener);
                
        setVisible(true);
	}
}
