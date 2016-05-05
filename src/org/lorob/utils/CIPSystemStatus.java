package org.lorob.utils;

/**
 * CIPSYstem status is a container for the system and current state of
 * testing
 * @author lorob
 *
 */
public class CIPSystemStatus
{
	private String _system;
	private String _info;
	private int _status;
	
	public static final int RED=0;
	public static final int AMBER=1;
	public static final int GREEN=2;
	
	/**
	 * Constructor
	 * @param system
	 * @param info
	 */
	public CIPSystemStatus(String system,String info)
	{
		_system=system;
		_info=info;
		_status=AMBER;
	}
	
	/**
	 * get the system
	 * @return
	 */
	public String getSystem()
	{
		return _system;
	}
	
	/**
	 * Get the info
	 * @return
	 */
	public String getInfo()
	{
		return _info;
	}
	
	/**
	 * Set the info
	 * @param info
	 */
	public void setInfo(String info)
	{
		_info=info;
	}
	
	/** Set the status 0-Red 1-Amber 2-Green
	 * @param status
	 */
	public void setStatus(int status)
	{
		if(status>=RED&&status<=GREEN)
		{
			_status=status;
		}
	}
	
	/** getStatus
	 * @return
	 */
	public int getStatus()
	{
		return _status;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer output=new StringBuffer("<CIPStatus><system>");
		output.append(getSystem());
		output.append("</system><info>");
		output.append(getInfo());
		output.append("</info><status>");
		output.append(getStatus());
		output.append("</status></CIPStatus>");
		return output.toString();
	}
	
	
}
