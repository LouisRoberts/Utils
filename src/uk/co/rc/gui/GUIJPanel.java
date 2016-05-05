package uk.co.rc.gui;

import java.awt.Component;

import javax.swing.JPanel;

public class GUIJPanel extends JPanel implements GUIComponent 
{
    private static final long serialVersionUID = -4735590074915518761L;
	String mType;
    /** constructor
     * applies loaded properties to component
     */
    private GUIJPanel() 
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
            return new GUIJPanel(); 
        }
    }
    
  static 
  {
    GUIComponentFactory.addFactory("GUIJPanel", new Factory());
  }
}
