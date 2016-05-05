package uk.co.rc.gui;

import java.awt.Component;

import javax.swing.JPasswordField;

public class GUIJPasswordField  extends JPasswordField implements GUIComponent
{
	private static final long serialVersionUID = 1087608335932014982L;

	private GUIJPasswordField() 
    {
        super(20);
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
            return new GUIJPasswordField(); 
        }
    }
    
  static 
  {
    GUIComponentFactory.addFactory("GUIJPasswordField", new Factory());
  }
}
