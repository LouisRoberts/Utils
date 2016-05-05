package uk.co.rc.gui;

import java.awt.Component;

import javax.swing.JLabel;

public class GUIJLabel extends JLabel implements GUIComponent 
{
    private static final long serialVersionUID = 709988883996290921L;

	/** constructor
     * applies loaded properties to component
     */
    private GUIJLabel() 
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
            return new GUIJLabel(); 
        }
    }
    
  static 
  {
    GUIComponentFactory.addFactory("GUIJLabel", new Factory());
  }
}
