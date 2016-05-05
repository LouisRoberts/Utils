package org.lorob.gui;

import java.awt.Frame;
import java.awt.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.lorob.utils.GUIUtils;

/**
 * Class that displays in alphabetical order
 * a selection dialog.
 * @author lorob
 *
 */
public class IterationChooser extends JDialog
{
	private static final long serialVersionUID = -4840943890179342502L;
	private Frame _frame;
	private List _fileList;
	private IterationChooserListener _me=null;
	private IterationChooser _this;
	private HashMap _dbKeys;
    
	/**
	 * Constructor
	 * @param owner
	 * @param dbKeys
	 * @param me
	 * @param defaultSelection
	 */
	public IterationChooser(Frame owner,String header,HashMap dbKeys,IterationChooserListener me,String defaultSelection)
	{
		this(owner,header, dbKeys, me, defaultSelection,null);
	}
	/**
	 * Constructor
	 * @param owner
	 * @param dbKeys
	 * @param me
	 * @param defaultSelection
	 * @param adapter
	 */
	public IterationChooser(Frame owner,String header,HashMap dbKeys,IterationChooserListener me,String defaultSelection,WindowAdapter adapter)
	{
		super(owner,header,true);
        _this=this;
        _frame=owner;
        _me=me;
        _dbKeys=dbKeys;
        setSize(400,400);
        // add list of files
        _fileList = new List(4, false);
        
        TreeSet treeSet=new TreeSet();
		SortedSet s = Collections.synchronizedSortedSet(treeSet);
		        
        Iterator keys=dbKeys.keySet().iterator();
        while(keys.hasNext())
        {
        	String thisKey=(String)keys.next();
            
            treeSet.add(thisKey);
        }
        int selectedIndex=0;
        int index=0;
        synchronized(s) 
		{
		      Iterator i = s.iterator(); // Must be in the synchronized block
		      while (i.hasNext())
		      {
		    	  String item=(String)i.next();
		    	  _fileList.add(item);
		    	  if(defaultSelection!=null)
		          {
		    		  if(item.compareTo(defaultSelection)==0)
		    		  {
		    			  selectedIndex=index;
		    		  }
		          }
		    	  index++;
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
                    if(index>=0&&index<_dbKeys.size())
                    {
                    	int ok=GUIUtils.showConfirmDialog(_frame,"Are You sure you want "+key+"?","Confirm Iteration");
                    	if(ok==JOptionPane.OK_OPTION)
                    	{
                    		_this.dispose();
                    		_me.fireSelected((String)_dbKeys.get(key),key);
                    	}
                    }
                    
                }
            }
        };
        _fileList.addMouseListener(mouseListener);
        // set the deault selection
        if(defaultSelection!=null)
        {
        	_fileList.select(selectedIndex);
        }
        // default window listener in case gets closed
        if(adapter==null)
        {
	        this.addWindowListener(new WindowAdapter()
			{
				public void windowClosing( WindowEvent e )
				{
					System.exit(0);
				}
				
			});
        }
        else
        {
        	this.addWindowListener(adapter);
        }
        
        setVisible(true);
	}
	
	public static final String ITERATION1="Release 4 Iteration 1";
	public static final String ITERATION2="Release 3 Iteration 2";
	public static final String ITERATION3="Release 3 Iteration 3";
	public static final String ITERATION4="Release 3 Iteration 4";
	
	public static final String UAT="Release 3 UAT";
	
	/**
	 * Static instance to be used for all components
	 * @param owner
	 * @param header
	 * @param me
	 */
	public static IterationChooser launchDefaultIterationChooser(Frame owner,String header,IterationChooserListener me)
	{
		
		IterationChooser chooser=IterationChooser.launchDefaultIterationChooser(owner, header, me,null);
		return chooser;
	}
	
	/**
	 * Static instance to be used for all components
	 * @param owner
	 * @param header
	 * @param me
	 */
	public static IterationChooser launchDefaultIterationChooser(Frame owner,String header,IterationChooserListener me,WindowAdapter adapter)
	{
		HashMap dbKeys=new HashMap();
		//dbKeys.put(ITERATION1, ITERATION1);
		//dbKeys.put(ITERATION2, ITERATION2);
		dbKeys.put(ITERATION3, ITERATION3);
		dbKeys.put(ITERATION4, ITERATION4);
		dbKeys.put(ITERATION1, ITERATION1);
		dbKeys.put(UAT,UAT);
		IterationChooser chooser=new IterationChooser(owner,header,dbKeys,me,ITERATION3,adapter);
		return chooser;
	}
	
	
}
