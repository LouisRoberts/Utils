package uk.co.rc.gui;

import java.awt.GridBagLayout;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.lorob.utils.StringLoader;

/**
 * The basic page class
 * @author  lorob
 */
public class ClientBasicPage extends JPanel
{
    private static final long serialVersionUID = 106587523432049874L;
	public GridBagLayout mGridbag;
    JPanel mMainPanel;
    ParentListener mParentListener;
    JFrame mParentFrame;
    String mHelpTag="default";
    
    /**
     * Constructor
     * set up basic look and feel and create main panel
     */
    public ClientBasicPage()
    {
        
    }
    
    public void setSettings(Class callingClass,String filename,String panelName)
    {
        // add border
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED ));
        mGridbag = new GridBagLayout();
        setLayout(mGridbag);
        mMainPanel=(GUIJPanel)GUIComponentFactory.createComponent(GUIComponentFactory.GUIJPANELID, callingClass, filename, panelName);
        mHelpTag=StringLoader.get("helpfile", callingClass, filename);
    }
    /**
     * @return the main panel
     */
    public JPanel getMainPanel()
    {
        return mMainPanel;
    }
    
    /** function that receives a notify that the page has just loaded
     * this is to allow updates i.e. rerun sql
     * should be overloaded by sub class
     */
    public void pageUpdated()
    {
        updateDynamicContent("",null);
    }
    
    /** function called after panel is added to screen
     * used in panel needs to start a process
     */
    public void start()
    {
    }
    
    /** function to talk to parent
     * @param ParentListener the listener to talk to 
     */
    public void addParentListener(ParentListener listener)
    {
        mParentListener=listener;
    }
    
    /** function for sub classes to talk 
     * to listener
     * @param event - the event for the parent to deal with
     */
   
    public void talkToParent(String event)
    {
        if(mParentListener!=null)
        {
            mParentListener.eventOccured(event);
        }
    }
    
    /** add the parent frame to this page for reference
     * @param frame the parent frame
     */
    public void addParentFrame(JFrame frame)
    {
        mParentFrame=frame;
    }
    
    /** return the parent frame
     * @return Frame the parent frame
     */
    public JFrame getParentFrame()
    {
        return mParentFrame;
    }
    
    /** look for any dynamic content in the mScreen and update it
    * should be overwritten if used
     */
    public void updateDynamicContent(String info,Hashtable variables)
    {
        
    }
    
    /** blank any data when page is unloaded
     */
    public void blankDataFields()
    {
    }
    
    /** get the help tag
    * @return get the help tag from the properties file
    */
    public String getHelpPage()
    {
        return mHelpTag;
    }
}