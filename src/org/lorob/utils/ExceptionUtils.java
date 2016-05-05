package org.lorob.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utility class for Exception handling
 * @author lorob
 *
 */
public class ExceptionUtils
{
	/** 
     * getFullStackTrace - for reporting to user when
     * things go tits up
     * @return String representation of the stack trace for this error
     */
    public static String getFullStackTrace(Exception exception)
    {
    	Throwable handedException=exception.fillInStackTrace().fillInStackTrace();
        StringWriter sw=new StringWriter();
        PrintWriter pw=new PrintWriter(sw);
        exception.printStackTrace(pw);
        if(handedException!=null)
        {
            pw.println("Caused by:");
            handedException.printStackTrace(pw);
        }
        sw.flush();
        return sw.toString();
    }
}
