package uk.co.rc.gui;

import java.awt.Component;

import javax.swing.JTextField;

public class GUIJTextField extends JTextField implements GUIComponent 
{
    private static final long serialVersionUID = 3844417771168634377L;
	String mType;
    /** constructor
     * applies loaded properties to component
     */
    private GUIJTextField() 
    {
        super(20);
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
            return new GUIJTextField(); 
        }
    }
    
  static 
  {
    GUIComponentFactory.addFactory("GUIJTextField", new Factory());
  }
 
}
