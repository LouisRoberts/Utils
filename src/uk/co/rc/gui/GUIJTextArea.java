package uk.co.rc.gui;

import java.awt.Component;

import javax.swing.JTextArea;

public class GUIJTextArea extends JTextArea implements GUIComponent 
{
  	private static final long serialVersionUID = -1832102391367720287L;
	String mType;
    /** constructor
     * applies loaded properties to component
     */
    private GUIJTextArea() 
    {
        super("",4,15);
        setLineWrap(true);
    }
    
    /** 
     * set the type of the button
     * useful for deciding what to do when it is pressed
     * @param type the type of this button
     */
    public void setType(String type)
    {
        mType=type;
    }
    
    /**
     * return the type of button
     * @return String the type of button
     */
    public String getType()
    {
        return mType;
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
            return new GUIJTextArea(); 
        }
    }
    
  static 
  {
    GUIComponentFactory.addFactory("GUIJTextArea", new Factory());
  }
 
}
