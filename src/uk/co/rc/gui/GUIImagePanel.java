package uk.co.rc.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;

import javax.swing.JPanel;

/** GUIImagePanel
 * loads and displays an image
 */
public class GUIImagePanel extends JPanel
{
   private static final long serialVersionUID = -4824678051179371284L;
	protected	Image             _Image;
    protected   Image             _OffScreenImage;
    private		Dimension	      _Size;
    private     String            _ErrorText;
    private     boolean           _Scalable;
    private     ComponentAdapter  _ResizeAdapter
        = new ComponentAdapter()
        {
	        // Do something sensible when we're asked to resize ourselves
 		    public void componentResized( ComponentEvent e )
 		    {
 		        _OffScreenImage = null;
 		    }
        };			
    private transient boolean mDisposed = false;
    
    /** Constructor
     * @param image - the image to display in the panel
     */
    public GUIImagePanel( Image image )
    {
		this( image, false );
    }

    /** Constructor
     * @param image - the image to display in the panel
     * @param scalable - Stretch the image on resize
     */
    public GUIImagePanel( Image image, boolean scalable )
    {
        super();

        _Image = image;
        _Scalable = scalable;
        int w = image.getWidth( this );
        int h = image.getHeight( this );
        
        _Size = null;
		
		if( h > 0 && w > 0 )
		    _Size = new Dimension( w, h );
		else
		    _Scalable = true;
        
        addComponentListener( _ResizeAdapter );
	}

    /** Constructor
     * @param name - load the named image
     * @param size - set the size of the image
     * @param scalable - Stretch the image on resize
     */
    public GUIImagePanel( String name, Dimension size, boolean scalable )
    {
        super();
        
        _Scalable = scalable;
		_Size = size;
        if (name == null || name.length() == 0)
        {
            _ErrorText = "A null name was passed to ImageCanvas";
        }
        else
        {
		    URL loc = getClass().getResource( name + ".gif" );
		    if (loc == null)
		    {
			    _Image = null;
                _ErrorText = name + " not found";
			    System.err.println( "Warning: image " + name + " not found" );
		    }
		    else
		    {
				try
				{
					_Image = Toolkit.getDefaultToolkit().getImage( loc );
					prepareImage( _Image, this );
				}
				catch (Exception e)
				{
					System.out.println( "Warning: unable to load image " + name );
					e.printStackTrace();
				}
		    }
        }
        // Do something sensible when we're asked to resize ourselves
        addComponentListener( _ResizeAdapter );
    }
    
    
   /**
     * Update the image
     */
    public boolean imageUpdate( Image image, int infoFlgs,
        int x, int y, int width, int height )
    {
        if( mDisposed )
            return false;
        _OffScreenImage = null;
        if( ( infoFlgs & ERROR ) != 0 )
            _ErrorText = "Error loading image";
        else if( ( infoFlgs & ABORT ) != 0 )
            _ErrorText = "Image was aborted before download completed";
        else if( ( infoFlgs & ( FRAMEBITS | ALLBITS ) ) != 0 )
        {
		    if( _Size == null )
		        _Size = new Dimension( image.getWidth( this ), image.getHeight( this ) );
        }
        else
            return true;

        update(getGraphics());
        
        // Indicate we're no longer interested if we're finished or it's a bust
        return ( infoFlgs & ( ABORT | ALLBITS ) ) == 0;
      
    }
    
    /**
     * remove the notify
      */
    public void removeNotify()
    {
        super.removeNotify();
        mDisposed = true;
    }

 /**
  * update the image
  * g - the graphics object
  */
    public void update ( Graphics g )
    {
        paint( g );
    }
    /**
  * update the image
  * g - the graphics object
  */
    public void paintAll( Graphics g )
	{
		paint( g );
	}
/**
  * update the image
  * onscreenGraphics - the graphics object
  */
    public void paint( Graphics onscreenGraphics )
    {
        if (onscreenGraphics == null)
        {
            return;
        }
        
        int width, height;
        
        if( _Scalable )
        {
            width = getSize().width;
            height = getSize().height;
        }
        else
        {
            width  = _Size.width;
            height = _Size.height;
        }
            
        // What can we do if we don't have a size?                
        if( width <= 0 || height <= 0 )
            return;
            
        if (_OffScreenImage == null)
        {
            _OffScreenImage = createImage(width, height);
        }
        
        Graphics g = _OffScreenImage.getGraphics();
  
        if (g == null) return;

        if (_ErrorText != null)
        {
    		g.drawString( _ErrorText, 2, getSize().height/2 );
        }
        else
        {
            if (_Image != null)
            {
			    Dimension viewSize;	
			    if( _Scalable )
				    viewSize = getSize();
			    else
				    viewSize = _Size;
                int imageWidth = _Image.getWidth( null );
                int imageHeight = _Image.getHeight( null );
                
                // draw and scale the image.

                g.drawImage(_Image, 0, 0, viewSize.width, viewSize.height,
                                    0, 0,     imageWidth,     imageHeight, this);
                               
            }
        }

		if(onscreenGraphics!=null&&_OffScreenImage!=null)
		{
			onscreenGraphics.drawImage( _OffScreenImage, 0, 0, this );
		}

        paintChildren(onscreenGraphics);
        
    }

/** get the size of the image
 * @return Dimension the dimension of the image
 */
    public Dimension getPreferredSize()
    {
		return ( _Size == null ? new Dimension( 0, 0 ) : _Size );
    }

    
}
