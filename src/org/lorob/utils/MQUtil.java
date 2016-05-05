package org.lorob.utils;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;

import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

/**
 * Utility to read messages from Queue.
 * Untested as security issues prevent access to queue.
 * @author lorob
 *
 */
public class MQUtil
{
	/**
	 * Function to return the number of messages in each queue of
	 * the handed queue manager
	 * @param myQmgr
	 * @param myQueue
	 * @param status
	 * @param xmlTag
	 * @param objectName
	 * @param host
	 * @param channel
	 * @param port
	 * @return
	 */
	public static int readObjectNumbersFromQueue(String myQmgr,String myQueue,CIPSystemStatus status,
			String host,String channel,String port)
	{
		int returnNo=0;;
		
		MQQueueManager qMgr=null;
		MQQueue        browseQueue = null;
		
		try
    	{
			if(!(host==null||host.length()==0))
	    	{
	    		MQEnvironment.hostname = host;
	     	}
	    	if(!(channel==null||channel.length()==0))
	    	{
	    		MQEnvironment.channel = channel;
	    	}
	    	if(!(port==null||port.length()==0))
	    	{
	    		MQEnvironment.port = Integer.parseInt(port);
	    	}
	    	qMgr=new MQQueueManager(myQmgr);
	    	int openOptions = MQC.MQOO_INQUIRE;
    		
            browseQueue = qMgr.accessQueue(myQueue, openOptions, null, null, null);
            if(browseQueue==null)
            {
            	System.out.println("no queue");
            	// something is wrong?
            	status.setStatus(CIPSystemStatus.RED);
            	status.setInfo("Failed to get queue "+myQmgr);
            }
            else
            {
            	returnNo=browseQueue.getCurrentDepth();
            }
    	}
		catch(Exception e)
		{
			System.out.println("Error reading on queue manager/queue "+myQmgr+"/"+myQueue);
			e.printStackTrace();
			return -1;
		}
		finally
		{
//			 close connection
	        try 
	        {
	        	if(browseQueue!=null)
	        	{
	        		browseQueue.close();
	        		qMgr.disconnect();
	        	}
	        }
	        catch (MQException ex) 
	        {
	           	status.setStatus(CIPSystemStatus.RED);
	        	status.setInfo("Error closing connection on queue manager/queue "+myQmgr+"/"+myQueue+".MQ error: Completion code " +
	                         ex.completionCode + " Reason code " + ex.reasonCode);
	    	}
		}
		
		return returnNo;
	}
	
	/** 
	 * Read ETP Specific messages
	 * @param myQmgr
	 * @param myQueue
	 * @param status
	 * @param objectName - the object to look for in the byte stream
	 * @return
	 */
	public static String readVOFromQueue(String myQmgr,String myQueue,CIPSystemStatus status,
			String xmlTag,String objectName,String host,String channel,String port)
	{
		StringBuffer message=new StringBuffer("<");
		message.append(xmlTag);
		message.append("s>");
		readVOFromQueue( myQmgr, myQueue,status,message,xmlTag,objectName,host,channel,port);
		message.append("</");
		message.append(xmlTag);
		message.append("s>\n");
		
		return message.toString();
	}
	
	/**
	 * Read the handed queue and append what is read
	 * to buffer. XML tag the individual messages
	 * @param myQmgr
	 * @param myQueue
	 * @param status
	 * @param message
	 * @param queueXML
	 * @param objectName - the object to look for in the byte stream
	 * @param host - host of queue
	 * @param channel - may be null
	 */
	public static void readVOFromQueue(String myQmgr,String myQueue,CIPSystemStatus status,
			StringBuffer message,String queueXML,String objectName,String host,String channel,String port)
	{
		MQQueueManager qMgr=null;
		MQQueue        browseQueue = null;
		
		try
    	{
			if(!(host==null||host.length()==0))
	    	{
	    		MQEnvironment.hostname = host;
	     	}
	    	if(!(channel==null||channel.length()==0))
	    	{
	    		MQEnvironment.channel = channel;
	    	}
	    	if(!(port==null||port.length()==0))
	    	{
	    		MQEnvironment.port = Integer.parseInt(port);
	    	}
	    	qMgr=new MQQueueManager(myQmgr);
    		int openOptions = MQC.MQOO_BROWSE ;//| MQC.MQOO_FAIL_IF_QUIESCING  ;
    		
            browseQueue = qMgr.accessQueue(myQueue, openOptions, null, null, null);
            if(browseQueue==null)
            {
            	System.out.println("no queue");
            	// something is wrong?
            	status.setStatus(CIPSystemStatus.RED);
            	status.setInfo("Failed to get queue "+myQmgr);
            }
            else
            {
            	readDataFromQueue(message,browseQueue,queueXML,objectName);
            }
    	}
    	catch(EOFException e)
        {
     	   // no worries
     	   //e.printStackTrace();
    		status.setStatus(CIPSystemStatus.GREEN);
        }
        catch (MQException ex) 
        {
        	System.out.println("Error in queue "+myQmgr+"/"+myQueue);
        	System.out.println("ex="+ex.toString());
        	if (ex.reasonCode == MQException.MQRC_NO_MSG_AVAILABLE) 
            {
        		status.setStatus(CIPSystemStatus.GREEN);
        		/*if(Integer.parseInt(messageCount.toString())<2)
        		{
        			status.setInfo("No Messages on queue manager/queue "+myQmgr+"/"+myQueue+" ");
        		}*/
            }
            else 
            {
            	status.setStatus(CIPSystemStatus.RED);
            	status.setInfo("Error getting messages on queue manager/queue "+myQmgr+"/"+myQueue+".MQ error: Completion code " +
                             ex.completionCode + " Reason code " + ex.reasonCode);
            }
        }
        catch (Exception ex) 
        {
        	System.out.println("Error in queue "+myQmgr+"/"+myQueue);        	
        	ex.printStackTrace();
        	status.setStatus(CIPSystemStatus.RED);
        	status.setInfo("Error getting messages on queue manager/queue "+myQmgr+"/"+myQueue+" "+ex.getMessage());
        }
        // close connection
        try 
        {
        	if(browseQueue!=null)
        	{
        		browseQueue.close();
        		qMgr.disconnect();
        	}
        }
        catch (MQException ex) 
        {
           	status.setStatus(CIPSystemStatus.RED);
        	status.setInfo("Error closing connection on queue manager/queue "+myQmgr+"/"+myQueue+".MQ error: Completion code " +
                         ex.completionCode + " Reason code " + ex.reasonCode);
    	}
        
        
	}
	
	/**
	 * Read data from the handed queue and wrap data in handed tags
	 * @param message
	 * @param browseQueue
	 * @param xmlTag
	 * @param objectName - the object to look for in the byte stream
	 * @return
	 * @throws EOFException
	 * @throws MQException
	 * @throws Exception
	 */
	public static void readDataFromQueue(StringBuffer message,
			MQQueue browseQueue,String xmlTag,String objectName)
		throws EOFException,MQException,Exception
	{
		
        	MQGetMessageOptions gmo = new MQGetMessageOptions(); 
        	int baseOptions=gmo.options;
            
            MQMessage myMessage = new MQMessage();
            int messageCount=1;
            while(true)
            {
           		if(messageCount==1)
            	{
            		gmo.options = baseOptions + MQC.MQGMO_BROWSE_FIRST;
            	}
            	else
            	{
            		gmo.options = baseOptions + MQC.MQGMO_BROWSE_NEXT;
            	}
            	
            	myMessage.clearMessage();
            	myMessage.correlationId = MQC.MQCI_NONE;
            	myMessage.messageId     = MQC.MQMI_NONE;
            	browseQueue.get(myMessage, gmo);
            	int msgLength = myMessage.getMessageLength() ;
            	byte[] data=new byte[msgLength];
            	myMessage.readFully(data);
            	String allData=new String(data);
            	//System.out.println("data="+allData);
            	Object thisObject=null;
               
				// we ignore the first object as the 'data' is
				// wrapped in a jms object that is buggered. Can't
				// figure how to read this wrapper object. The
				// data seems okay however!
            	
            	// "uk.org.nhs.bsa.ppd"
				int header=allData.indexOf(objectName);
				header=header-8;
				//System.out.println("header="+header);
				
				byte[] dataMinusHeader=getData(msgLength,data,header);
				
				//String allDataMinusHeader=new String(dataMinusHeader);
            	//System.out.println("allDataMinusHeader="+allDataMinusHeader);
				ByteArrayInputStream bis=new ByteArrayInputStream(dataMinusHeader);
				boolean isObject=false;
				ObjectInputStream ois=null;
				try
				{
					ois=new ObjectInputStream(bis);
					thisObject=ois.readObject();
					isObject=true;
					
				}
				catch(Exception e)
				{
					// if we fail try and get the object as  a pure string
					System.out.println("string data got");
				}
				
				   
				// USE reflection to read the details
				message.append("<");
				message.append(xmlTag);
				message.append(">");
				if(isObject)
				{
					prettyPrintGetMethods(thisObject,message);
				}
				else
				{
					message.append("<stringData><![CDATA[");
					header=allData.indexOf(objectName);
					
					byte[] stringData=getData(msgLength,data,header);
					// get rid of dodgy stuff
					
					String messageData=new String(stringData);
					StringBuffer finalData=null;
					if(objectName.startsWith("<"))
					{
						// assume xml
						String endTag="</"+objectName.substring(1);
						int endtagIndex=messageData.lastIndexOf(endTag);
						finalData=new StringBuffer(messageData.substring(0,endtagIndex));
						finalData.append(endTag);
					}
					for(int i=0;i<finalData.length();i++)
					{
						if(Character.isISOControl(finalData.charAt(i))==true||
								(int)finalData.charAt(i)>127)
						{
							// blast it
							finalData.replace(i, i+1, " ");
						}
					}
					message.append(finalData);
					message.append("]]></stringData>");
				}
				message.append("</");
				message.append(xmlTag);
				message.append(">");
				bis.close();
				if(ois!=null)
				{
					ois.close();
				}
				messageCount++;
            }
	}
	
	private static byte[] getData(int msgLength,byte[] data,int header)
	{
		int newMessageLength=msgLength-header;
		byte[] dataMinusHeader=new byte[newMessageLength];
		for(int i=0;i<newMessageLength;i++)
		{
			dataMinusHeader[i]=data[i+header];
		}
		return dataMinusHeader;
	}
	
	/**
	 * Reads the 'get' and 'is' methods from handed object and
	 * appends the details as xml to handed buffer
	 * @param thisObject
	 * @param buffer
	 * @throws Exception
	 */
	public static void prettyPrintGetMethods(Object thisObject,StringBuffer buffer)
		throws Exception
	{
		Class thisObjectClass=thisObject.getClass();
		
    
 	   // getDetails
    	// iterate through methods
    	Method[] methods=thisObjectClass.getMethods();
    	if(methods==null)
    	{
    		return;
    	}
    	for(int i=0;i<methods.length;i++)
    	{
    		Method thisMethod=methods[i];
    		String name=thisMethod.getName();
    		if((name.toLowerCase().startsWith("get")||
    				name.toLowerCase().startsWith("is"))&&thisMethod.getParameterTypes().length==0)
    		{
    			Object returnFromGet=thisMethod.invoke(thisObject, null);
    			
    			String methodName=null;
    			if(name.toLowerCase().startsWith("get"))
    			{
    				methodName=name.substring(3);
    			}
    			else
    			{
    				methodName=name.substring(2);
    			}
    			buffer.append("\n<");
    			buffer.append(methodName);
    			buffer.append(">");
    			buffer.append(returnFromGet.toString());
    			buffer.append("</");
    			buffer.append(methodName);
    			buffer.append(">");
    		}
    	}
	}
	
	
	
	/*************************************************************/
	   /* This method will dump the text of the message in both hex */
	   /* and character format.                                     */
	   /*************************************************************/
	   static void dumpHexMessage(MQMessage myMsg) throws java.io.IOException {
	       
	      int DataLength = myMsg.getMessageLength();
	      int ch      = 0;
	      int chars_this_line = 0;
	      int CHARS_PER_LINE  = 16;
	      StringBuffer line_text = new StringBuffer();
	      line_text.setLength(0);
	      do {
	         chars_this_line = 0;
	         String myPos = new String(Integer.toHexString(ch));
	         for (int i=0; i < 8 - myPos.length(); i++) {
	            System.out.print("0");
	         }
	         System.out.print((String)(Integer.toHexString(ch)).toUpperCase() + ": ");

	         while ( (chars_this_line < CHARS_PER_LINE) &&
	                 (ch < DataLength) ) {

	             if (chars_this_line % 2 == 0) {
	                System.out.print(" ");
	             }
	             char b = (char)(myMsg.readUnsignedByte() & 0xFF);
	             if (b < 0x10) {
	                System.out.print("0");
	             }

	             System.out.print((String)(Integer.toHexString(b)).toUpperCase());

	             /***********************************************/ 
	             /* If this is a printable character, print it. */
	             /* Otherwise, print a '.' as a placeholder.    */
	             /***********************************************/ 
	             if (Character.isLetterOrDigit(b)
	                 || Character.getType(b) == Character.CONNECTOR_PUNCTUATION
	                 || Character.getType(b) == Character.CURRENCY_SYMBOL
	                 || Character.getType(b) == Character.MATH_SYMBOL
	                 || Character.getType(b) == Character.MODIFIER_SYMBOL
	                 || Character.getType(b) == Character.UPPERCASE_LETTER
	                 || Character.getType(b) == Character.SPACE_SEPARATOR
	                 || Character.getType(b) == Character.DASH_PUNCTUATION
	                 || Character.getType(b) == Character.START_PUNCTUATION
	                 || Character.getType(b) == Character.END_PUNCTUATION
	                 || Character.getType(b) == Character.OTHER_PUNCTUATION ) {
	                line_text.append(b); 
	             } else {
	                line_text.append('.');
	             }
	             chars_this_line++;
	             ch++;
	         }

	         /*****************************************************/
	         /* pad with blanks to format the last line correctly */
	         /*****************************************************/
	         if (chars_this_line < CHARS_PER_LINE) {
	           for ( ;chars_this_line < CHARS_PER_LINE; chars_this_line++) {
	              if (chars_this_line % 2 == 0) System.out.print(" ");
	              System.out.print("  ");
	              line_text.append(' ');
	           }
	         }

	         System.out.println(" '" + line_text.toString() + "'");
	         line_text.setLength(0);
	       } while (ch < DataLength);

	   } /* end of dumpHexMessage */

	   /****************************************************/
	   /* Some of the MQ Ids are actually byte strings and */
	   /* need to be dumped in hex format.                 */
	   /****************************************************/
	   static void dumpHexId(byte[] myId) {
	      System.out.print("X'");
	      for (int i=0; i < myId.length; i++) {
	        char b = (char)(myId[i] & 0xFF);
	        if (b < 0x10) {
	           System.out.print("0");
	        }
	        System.out.print((String)(Integer.toHexString(b)).toUpperCase());
	      }
	      System.out.println("'");
	   }
	
}
