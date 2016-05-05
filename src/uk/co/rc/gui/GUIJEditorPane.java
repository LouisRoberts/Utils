package uk.co.rc.gui;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import org.lorob.utils.Strings;

/**
 * @author  lorob
 */
public class GUIJEditorPane extends JEditorPane implements GUIComponent 
{
   private static final long serialVersionUID = 9038305297942363276L;
	String mType;
    GUIJEditorPaneListener mListener;
    String mBasicHtml;
    
    /** constructor
     * applies loaded properties to component
     */
    private GUIJEditorPane() 
    {
        super();
        //setContentType("text/html");
        setEditable(false); 
        //addHyperlinkListener(createHyperLinkListener());
        
    }
    
     
     // hyperlink handler
     // hyperlinks work like the button links to the side
    public HyperlinkListener createHyperLinkListener() 
    { 
     	return new HyperlinkListener() 
     	{ 
 	        public void hyperlinkUpdate(HyperlinkEvent e) 
 	        { 
 		        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) 
 		        { 
 		            if (e instanceof HTMLFrameHyperlinkEvent) 
 		            { 
 			            ((HTMLDocument)getDocument()).processHTMLFrameHyperlinkEvent( (HTMLFrameHyperlinkEvent)e); 
 			        }
 			        else 
 			        { 
 			            try 
 			            { 
 			                // need to fire an event 
 			                if(mListener!=null)
 			                {
 			                    mListener.fireGUIJEditorPaneEvent(e.getDescription());
 			                }
 			            }
 			            catch (Exception ioe) 
 			            { 
 			                System.out.println("IOE: " + ioe); 
             			} 
         		    } 
         		} 
           } 
 	    }; 
 	}
 	
 	/** set the html text for this component
 	 * note this is for reference later
 	 * @param 
 	 */
    public void setHtml(String text)
    {
        mBasicHtml=text;
    }
    
    /** return the html text
     *
     * @return String the core text
     */
    public String getHtml()
    {
        return mBasicHtml;
    }
    
    /**
     * @return the component
     */
    public Component getComponent()
    {
        return (Component)this;
    }
    /**
     * @return the object
     */
    public Object getObject()
    {
        return (Object)this;
    }
    
    // return this component
    private static class Factory 
        extends GUIComponentFactory 
    {
        protected GUIComponent create() 
        { 
            // load up properties
            return new GUIJEditorPane(); 
        }
    }
    
  static 
  {
    GUIComponentFactory.addFactory("GUIJEditorPane", new Factory());
  }
  
  /** listener to fire hyperlink events at
   * @param listener a listener for hyperlink clicks
   */
   public void addGUIJEditorPaneListener(GUIJEditorPaneListener listener)
   {
        mListener=listener;
   }
   
 /** function to update the text
  */
  public void updateHTML(String html)
  {
    // look for images <!--IMG_SRC-->
    html=Strings.replace(html,"<!--IMG_SRC-->","file:"+System.getProperty("user.dir")+"\\uk\\co\\mybiz\\mmm\\html\\");
    HTMLEditorKit editor=new HTMLEditorKit();
    //create logical document
    DefaultStyledDocument doc=(DefaultStyledDocument)editor.createDefaultDocument();
    StringReader sr=new StringReader(html);
    try
    {
        editor.read((Reader)sr,(Document)doc,0);
    }
    catch(Exception e)
    {
        e.printStackTrace();
        return;
    }
    // set editor pane document
    setEditorKit(editor);
    setDocument(doc);
  }
  
  /** function to load the html page
  * and return the text
  * @param file - the file to read
  * @return StringBuffer - the file contents
  */
  public StringBuffer loadHTMLPage(File file)
  {
    try
    {
    	FileReader fr=new FileReader(file);
        BufferedReader reader=new BufferedReader(fr);
        StringBuffer buffer=new StringBuffer();
        String inStr="";
        while(inStr!=null)
        {
            buffer.append(inStr);
            inStr=reader.readLine();
        }
        reader.close();
        fr.close();
        return buffer;
    }
    catch(Exception e)
    {
        e.printStackTrace();
        return null;
    }
  }
}
