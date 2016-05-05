package uk.co.rc.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.lorob.utils.StringLoader;


/**
 * Component Factory is a store of different components
 * I use this top create my own components which read their
 * properties from property files rather than
 * have to repeat the code again and again
 * @author lorob
 */
public abstract class GUIComponentFactory
{   
    // the map of all factories
    private static Map mFactories = new HashMap();
    
    // map of the properties
    //private static Map mProperties = new HashMap();
    
    // abstract method implemented by calling factories
    protected abstract GUIComponent create();
    
    // properties of the GUI
    private static final String FONT=".font";
    private static final String FONTSTYLE=".fontstyle";
    private static final String FONTSIZE=".fontsize";
    private static final String FOREGROUND=".foreground";
    private static final String BACKGROUND=".background";
    private static final String TOOLTIPTEXT=".tooltiptext";
    private static final String LABEL=".label";
    private static final String TYPE=".type";
    private static final String HTML=".html";
    //private static String TITLE=".title";
    
    // components
    //private static String GUILOCATION="uk.co.rc.gui.";
    public static final String GUIJBUTTONID="GUIJButton";
    public static final String GUIJLABELID="GUIJLabel";
    public static final String GUIJPANELID="GUIJPanel";
    public static final String GUIJEDITORPANEID="GUIJEditorPane";
    public static final String GUIJTEXTFIELDID="GUIJTextField";
    public static final String GUIJTEXTAREAID="GUIJTextArea";
    public static final String GUIJCHECKBOXID="GUIJCheckBox";
    public static final String GUIJLISTID="GUIJList";
    public static final String GUIJCOMBOBOXID="GUIJComboBox";
    public static final String GUIJPASSWORDFIELDID="GUIJPasswordField";
    public static final String GUIJTOOLBAR="GUIJToolBar";
    
    /** nameForId
     * @param id
     * @return the class name for this component
     * @throws Exception
     */
    private static String nameForId(String id)
    	throws Exception
    {
    	if(GUIJBUTTONID.compareTo(id)==0)
    	{
    		return GUIJButton.class.getName(); 
    	}
    	else if(GUIJLABELID.compareTo(id)==0)
    	{
    		return GUIJLabel.class.getName(); 
    	}
    	else if(GUIJPANELID.compareTo(id)==0)
    	{
    		return GUIJPanel.class.getName(); 
    	}
    	else if(GUIJEDITORPANEID.compareTo(id)==0)
    	{
    		return GUIJEditorPane.class.getName(); 
    	}
    	else if(GUIJTEXTFIELDID.compareTo(id)==0)
    	{
    		return GUIJTextField.class.getName(); 
    	}
    	else if(GUIJTEXTAREAID.compareTo(id)==0)
    	{
    		return GUIJTextArea.class.getName(); 
    	}
    	else if(GUIJCHECKBOXID.compareTo(id)==0)
    	{
    		return GUIJCheckBox.class.getName(); 
    	}
    	else if(GUIJLISTID.compareTo(id)==0)
    	{
    		return GUIJList.class.getName(); 
    	}
    	else if(GUIJCOMBOBOXID.compareTo(id)==0)
    	{
    		return GUIJComboBox.class.getName(); 
    	}
    	else if(GUIJPASSWORDFIELDID.compareTo(id)==0)
    	{
    		return GUIJPasswordField.class.getName(); 
    	}
    	else if(GUIJTOOLBAR.compareTo(id)==0)
    	{
    		return GUIJToolBar.class.getName(); 
    	}
    	throw new Exception("Unsupported type = "+id);
    	
    }
    
  
    public static void addFactory(String id, GUIComponentFactory f) 
    {
        mFactories.put(id, f);
    }
  
    // A Template Method:
    public static final GUIComponent createComponent(String id,Class classLoader,String propertyFile,String instance) 
    {
        if(!mFactories.containsKey(id))
        {
            try 
            {
                // Load dynamically
                Class.forName(nameForId(id));
            }
            catch(ClassNotFoundException e) 
            {
                e.printStackTrace();
                throw new RuntimeException("Bad factory creation: " + id);
            }
            catch(Exception e2)
            {
                e2.printStackTrace();
                throw new RuntimeException("Bad component creation: " + id);
            }
            // See if it was put in:
            if(!mFactories.containsKey(id))
            {
                throw new RuntimeException("Bad component creation: " + id);
            }
        }
        // load up handed property file if not done so already
        StringLoader.load(classLoader,propertyFile);
        GUIComponent comp=((GUIComponentFactory)mFactories.get(id)).create();
        loadPropertiesForComponent(comp.getComponent(),instance,classLoader,propertyFile);
        return (comp);
  }
  
  
  
  
  // funciton to set the common component
  private static void loadPropertiesForComponent(Component comp,String instance,Class classLoader,String propertyFile)
  {
     // get the various properties  
     String font=StringLoader.get(instance+FONT,classLoader,propertyFile);
     String fontStyle=StringLoader.get(instance+FONTSTYLE,classLoader,propertyFile);
     String fontSize=StringLoader.get(instance+FONTSIZE,classLoader,propertyFile);
     String foreground=StringLoader.get(instance+FOREGROUND,classLoader,propertyFile);
     String background=StringLoader.get(instance+BACKGROUND,classLoader,propertyFile);
     String toolTipText=StringLoader.get(instance+TOOLTIPTEXT,classLoader,propertyFile);
     String label=StringLoader.get(instance+LABEL,classLoader,propertyFile);
     String type=StringLoader.get(instance+TYPE,classLoader,propertyFile);
     String html=StringLoader.get(instance+HTML,classLoader,propertyFile);
     //String title=StringLoader.get(instance+TITLE,classLoader,propertyFile);
     // set the values
     if(font!=null&&fontStyle!=null&&fontSize!=null)
     {
        try
        {
            comp.setFont(new Font(font,Integer.parseInt(fontStyle),Integer.parseInt(fontSize)));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
     }
     if(toolTipText!=null)
     {
        ((JComponent)comp).setToolTipText(toolTipText);
     }
     if(foreground!=null)
     {
        try
        {
            comp.setForeground(new Color(Integer.parseInt(foreground)));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
     }
     if(background!=null)
     {
        try
        {
            comp.setBackground(new Color(Integer.parseInt(background)));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
     }
     // set text dependant on type
     if(label!=null)
     {
        if( comp instanceof JLabel)
        {
            ((JLabel)comp).setText(label);
        }
        else if ( comp instanceof JButton )
        {
            ((JButton)comp).setText(label);
        }
        else if (  comp instanceof JTextField)
        {
            ((JTextField)comp).setText(label);
        }
        else if (  comp instanceof JTextArea)
        {
            ((JTextArea)comp).setText(label);
        }            
     }
     if(type!=null)
     {
        if ( comp instanceof GUIJButton)
        {
            ((GUIJButton)comp).setType(type);
        }
     }
     // do we have a html page to show
     if(html!=null)
     {
        if ( comp instanceof GUIJEditorPane)
        {
            try
            {
                // need to get the system location
                String systemDir=System.getProperty("user.dir");
                File file=new File(systemDir+"/uk/co/mybiz/mmm/html/"+html);
                StringBuffer buffer=((GUIJEditorPane)comp).loadHTMLPage(file);
                    
                ((GUIJEditorPane)comp).setText(buffer.toString());
                ((GUIJEditorPane)comp).setHtml(buffer.toString());
            }
            catch(Exception e1)
            {
                e1.printStackTrace();
            }

        }
     }
     
  }

}