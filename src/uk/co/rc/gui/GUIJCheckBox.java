package uk.co.rc.gui;

import java.awt.Component;

import javax.swing.JCheckBox;

public class GUIJCheckBox extends JCheckBox implements GUIComponent 
{
    private static final long serialVersionUID = 1656520517480131046L;
	String mType;
    /** constructor
     * applies loaded properties to component
     */
    private GUIJCheckBox() 
    {
        super();
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
            return new GUIJCheckBox(); 
        }
    }
    
  static 
  {
    GUIComponentFactory.addFactory("GUIJCheckBox", new Factory());
  }
  
  /** set the colour of the button to darker
   * 
   */
   public void setDarker()
   {
    setForeground(getForeground().darker());
   }
}
