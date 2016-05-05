package org.lorob.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;

/** StringLoader
 * loads up common and gui specific strings 
 * @author Louis Roberts
 * @version $Revision: 1 $
 */
public class StringLoader
{
    static Hashtable sCommonStrings;
    
    public StringLoader()
    {
    }
    
    /** get the common string passed to it
     * @param id the id of the string to fetch 
     * @param callingClass the calling class
     * @param incomingProperties. Previously created via some other mechanism
     * @return the string of id or null if an error occurs.
      */
    public static String get(String id,Class callingClass,Properties incomingProperties)
    {
        try
        {
            if(sCommonStrings==null)
            {
                sCommonStrings=new Hashtable();
            }
            // are we already in the hashtable
            if(sCommonStrings.get(callingClass)==null)
            {
                // load
                Properties stringProperties=incomingProperties;
                Hashtable newHash=new Hashtable();
                // load up properties.
                for (Enumeration e=stringProperties.propertyNames();e.hasMoreElements() ;) 
                {
                    // insert into hash
                   String property=(String)e.nextElement();

                   newHash.put(property, stringProperties.get(property));
                }
                // put this hashtable in the hashtable of classes
                sCommonStrings.put(callingClass, newHash);
            }
            Hashtable strings=(Hashtable)sCommonStrings.get(callingClass);
            return (String)strings.get(id);
        }
        catch(Exception e)
        {
            // TODO - PROBABLY SWITCH THIS OFF
            System.out.println("Error retreiving string '"+id+"' from class '"+callingClass.getName()+"'");
            e.printStackTrace();
        }
        return null;
    }
    
    /** get the common string passed to it
     * @param id the id of the string to fetch 
     * @param callingClass the calling class
     * @param filename the filename of the property file that belongs to the calling class
     * @return the string of id or null if an error occurs.
      */
    public static String get(String id,Class callingClass,String filename)
    {
        try
        {
            if(sCommonStrings==null)
            {
                sCommonStrings=new Hashtable();
            }
            // are we already in the hashtable
            if(sCommonStrings.get(callingClass)==null)
            {
                // load
                Properties stringProperties=new Properties();
                stringProperties.load(callingClass.getResourceAsStream(filename));
                Hashtable newHash=new Hashtable();
                // load up properties.
                for (Enumeration e=stringProperties.propertyNames();e.hasMoreElements() ;) 
                {
                    // insert into hash
                   String property=(String)e.nextElement();

                   newHash.put(property, stringProperties.get(property));
                }
                // put this hashtable in the hashtable of classes
                sCommonStrings.put(callingClass, newHash);
            }
            Hashtable strings=(Hashtable)sCommonStrings.get(callingClass);
            return (String)strings.get(id);
        }
        catch(Exception e)
        {
            // TODO - PROBABLY SWITCH THIS OFF
            System.out.println("Error retreiving string '"+id+"' from class '"+callingClass.getName()+"'");
            e.printStackTrace();
        }
        return null;
    }
    
    /** load the property file
     * @param callingClass the calling class
     * @param filename the filename of the property file that belongs to the calling class
     */
     public static void load(Class callingClass,String filename)
     {
        try
        {
            if(sCommonStrings==null)
            {
                sCommonStrings=new Hashtable();
            }
            // are we already in the hashtable
            if(sCommonStrings.get(callingClass)==null)
            {
                // load
                Properties stringProperties=new Properties();
                stringProperties.load(callingClass.getResourceAsStream(filename));
                Hashtable newHash=new Hashtable();
                // load up properties.
                for (Enumeration e=stringProperties.propertyNames();e.hasMoreElements() ;) 
                {
                    // insert into hash
                   String property=(String)e.nextElement();

                   newHash.put(property, stringProperties.get(property));
                }
                // put this hashtable in the hashtable of classes
                sCommonStrings.put(callingClass, newHash);
            }
        }
        catch(Exception e)
        {
            System.out.println("Error loading class '"+callingClass.getName()+"'");
            e.printStackTrace();
        }
     }
     
     /** does the property file exist
      * @return boolean true if exists else false
      * @param filename -property name
      * @param callingClass -the class calling for the file
      */
     public static boolean propertyFileExists(String filename,Class callingClass)
     {
        try
        {
             Properties stringProperties=new Properties();
             stringProperties.load(callingClass.getResourceAsStream(filename));
             //stringProperties.load(new FileInputStream(filename));
             return true;
        }
        catch(Exception io)
        {
            //io.printStackTrace();
            return false;
        }
     }
     
     /**
      * load the property files for local and 
      * return the loaded file
      * @param callingClass
      * @param filename
      * @param requestedLocale
      * @param useLocalisation
      * @return
      */
     public static Properties loadPropertiesFile( Class callingClass, String filename, Locale requestedLocale ,boolean useLocalisation)
 	{
         Properties p;

         if (useLocalisation)
         {

             // Extract the language and country codes from the requested locale
             // These may return "" if the requested component is not set
             String language = requestedLocale.getLanguage();
             String country  = requestedLocale.getCountry();

             // Evaluate a couple of helper booleans
             boolean useCountry = (country.length() > 0);
             boolean useLanguage = (language.length() > 0);

             String languageFilename = filename + "_" + language;

             // ATTEMPT 1 - file_LL_CC.properties
             if (useCountry)
             {
                 p = loadPropertiesFromFile( callingClass, languageFilename + "_" + country );
                 if (p != null)
                     return p;
             }

             // ATTEMPT 2 - file_LL.properties
             if (useLanguage)
             {
                 p = loadPropertiesFromFile( callingClass, languageFilename );
                 if (p != null)
                     return p;
             }
         } // End useLocale == true

 		// FINAL ATTEMPT - file.properties
 		p = loadPropertiesFromFile( callingClass, filename );

 		return p;

 	}

     /**
      * Load the property file for class and return the property file
      * @param callingClass
      * @param filename
      * @return
      */
 	private static Properties loadPropertiesFromFile( Class callingClass, String filename )
 	{
 	    Properties p=null;
 	    
         InputStream is = getInputStream( callingClass, filename + ".properties");
         
         if (is != null)
         {
             try
             {
                 p = new Properties();
                 p.load(is);
                 //System.out.println("    Using file " + filename );
                 return p;
             }
             catch(Exception e1)
             {
                 System.out.println("*** WARNING: EXCEPTION LOADING PROPERTIES FROM FILE " + filename );
                 System.out.println("Exception = " + e1 );
             }
             finally
             {
             	try
             	{
             		is.close();
             	}
             	catch( IOException ioe )
             	{
             	}
             }
         }
         
         return null;
 	}
 	
 	/**
 	 * Get the input stream for the file
 	 * @param callingClass
 	 * @param filename
 	 * @return
 	 */
 	private static InputStream getInputStream( Class callingClass, String filename )
 	{

     	try
 		{
 			InputStream is = callingClass.getResourceAsStream(filename);
 			return is;
 		}
 		catch (Exception e)
 		{
 		    System.out.println("Failed to open filename " + filename);
             return null;
         }

     }
}