package uk.co.rc.gui;

import java.awt.Component;

import javax.swing.JToolBar;

public class GUIJToolBar extends JToolBar implements GUIComponent 
{
	private static final long serialVersionUID = 3118497805178554246L;

	/** constructor
     * applies loaded properties to component
     */
    private GUIJToolBar() 
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
            return new GUIJToolBar(); 
        }
    }
    
  static 
  {
    GUIComponentFactory.addFactory("GUIJToolBar", new Factory());
  }
}
