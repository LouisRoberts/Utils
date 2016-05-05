package uk.co.rc.gui;

import java.awt.Component;

import javax.swing.JComboBox;

public class GUIJComboBox extends JComboBox implements GUIComponent 
{
    private static final long serialVersionUID = 7932172724082271168L;

	/** constructor
     * applies loaded properties to component
     */
    private GUIJComboBox() 
    {
        super();
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
            return new GUIJComboBox(); 
        }
    }
    
  static 
  {
    GUIComponentFactory.addFactory("GUIJComboBox", new Factory());
  }
}
