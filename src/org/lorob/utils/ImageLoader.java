package org.lorob.utils;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.ImageIcon;

/**
 * An image load helper
 * @author lorob
 *
 */
public class ImageLoader
{
	public ImageLoader()
	{
		
	}
	
	/** loadImage
	 * @param thisComponent
	 * @param referenceClass
	 * @param fileName
	 * @return
	 */
	public static ImageIcon loadImage( Component thisComponent, Class referenceClass, String fileName )
    {
        Image img = null;
		URL loc;
		
        try
        {
			loc = referenceClass.getResource( fileName + ".gif" );
           
            if (loc != null )
            {
       	        img = Toolkit.getDefaultToolkit().getImage( loc );
		        MediaTracker tracker = new MediaTracker( thisComponent );
                tracker.addImage( img, 0 );
			    tracker.waitForAll();
            }
            else
            {
            	System.err.println( "No Image: " + loc );
            }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if(img!=null)
		{
			return new ImageIcon(img);	
		}
		return null;
    }
}
