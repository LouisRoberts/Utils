package org.lorob.report;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * A container of a single variable type found in the properties file.
 * @author lorob
 *
 */
public class ReportVariable
{
	public static final int STRING=1;
	public static final int INTEGER=2;
	public static final int FLOAT=3;
	public static final int DATE=4;
	public static final int DRIVER=5;
	
	private int _type;
	private String _name;
	private String _value;
	private String _syntax;
	private boolean _validate;
	private String _message;
	private boolean _output;
	
	/**
	 * Constructor
	 * @param name
	 * @param type
	 * @param value
	 * @param validate
	 * @param syntax
	 * @param message
	 */
	public ReportVariable(String name,int type,String value,boolean validate,String syntax,String message)
	{
		this(name, type, value, validate, syntax, message, false);
	}
	
	/**
	 * Constructor
	 * @param name
	 * @param type
	 * @param value
	 * @param validate
	 * @param syntax
	 * @param message
	 */
	public ReportVariable(String name,int type,String value,boolean validate,String syntax,String message, boolean output)
	{
		_name=name;
		_type=type; // TODO validate range
		_value=value;
		_validate=validate;
		_syntax=syntax;
		_message=message;
		_output = output;
	}
	
	/**
	 * Get the associated message
	 * @return
	 */
	public String getMessage()
	{
		return _message;
	}
	
	/**
	 * Get the field name
	 * @return
	 */
	public String getName()
	{
		return _name;
	}
	
	/**
	 * Get the field value(as a string)
	 * @return
	 */
	public String getValue()
	{
		if(_value==null)
		{
			return "";
		}
		return _value;
	}
	
	/**
	 * Set the field value
	 * @param value
	 */
	public void setValue(String value)
	{
		_value=value;
	}
	
	/**
	 * Is this field to be validated
	 * @return
	 */
	public boolean isValidating()
	{
		return _validate;
	}
	
	/** get the type
	 * @return
	 */
	public int getType()
	{
		return _type;
	}
	
	/**
	 * get the syntax string
	 * @return
	 */
	public String getSyntax()
	{
		return _syntax;
	}
	
	public boolean isDataValid()
	{
		if(_type==INTEGER)
		{
			try
			{
				Integer.parseInt(_value);
				NumberFormat format=NumberFormat.getInstance();
				format.parse(_value);
			}
			catch(NumberFormatException pe)
			{
				return false;
			}
			catch(ParseException pe)
			{
				return false;
			}
			return true;
		}
		else if(_type==STRING||_type==DRIVER)
		{
			return true;
		}
		else if(_type==FLOAT)
		{
			try
			{
				Float.parseFloat(_value);
				DecimalFormat format=new DecimalFormat(); // TODO add syntax
				format.parse(_value);
			}
			catch(NumberFormatException pe)
			{
				return false;
			}
			catch(ParseException pe)
			{
				return false;
			}
			return true;
		}
		else if(_type==DATE)
		{
			if(_syntax==null)
			{
				return true;
			}
			SimpleDateFormat sdf=new SimpleDateFormat(_syntax);
			try
			{
				sdf.parse(_value);
			}
			catch(ParseException pe)
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @param output The output to set.
	 */
	public void setOutput(boolean output) 
	{
		this._output = output;
	}
	
	/**
	 * @return Returns the output.
	 */
	public boolean isOutput() 
	{
		return _output;
	}
}
