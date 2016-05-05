package uk.co.rc.gui;

import java.awt.Component;
import java.util.Vector;

import javax.swing.JList;

public class GUIJList extends JList implements GUIComponent 
{
    private static final long serialVersionUID = -4553740975721668393L;
	String mType;
    /** constructor
     * applies loaded properties to component
     */
    private GUIJList() 
    {
        super();
        setFixedCellWidth(30); // a default width
        setListData(new Vector());
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
            return new GUIJList(); 
        }
    }
    
  static 
  {
    GUIComponentFactory.addFactory("GUIJList", new Factory());
  }
  
  /** set the colour of the button to darker
   * 
   */
   public void setDarker()
   {
    setForeground(getForeground().darker());
   }
}
