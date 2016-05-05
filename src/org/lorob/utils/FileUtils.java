package org.lorob.utils;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


/**
 * Utility class for file handling
 * @author lorob
 *
 */
public class FileUtils
{
	private static int BUFFER=2038; // buffer to use during compression / decompression
	
	/**
	 * Read the handed file and return contents
	 * @param inFile
	 * @return
	 */
    public static StringBuffer readFile(File inFile)
    {
        BufferedReader br = null;
        try
        {
            StringBuffer buffer=new StringBuffer();
            br = new BufferedReader( new FileReader(inFile)); 
		    String line = br.readLine();
            while(line != null)
            {
         	    buffer.append(line);
         	    buffer.append("\n");
         	    line = br.readLine();
            }
            br.close();
            return buffer;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
	// GRROB 04/07/2008 Start of monthly doc totals changes.
    public static StringBuffer readFileNoCRs(File inFile)
    {
        BufferedReader br = null;
        try
        {
            StringBuffer buffer=new StringBuffer();
            br = new BufferedReader( new FileReader(inFile)); 
		    String line = br.readLine();
            while(line != null)
            {
         	    buffer.append(line);
         	    line = br.readLine();
            }
            br.close();
            return buffer;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }    
	// GRROB 04/07/2008 End of monthly doc totals changes.
    
    /**
     * 
	 * Read the handed file and return contents of the first line (up to \n)
	 * @param inFile
	 * @return
	 */
    public static StringBuffer readFileFirstLine(File inFile)
    {
        BufferedReader br = null;
        try
        {
            StringBuffer buffer=new StringBuffer();
            br = new BufferedReader( new FileReader(inFile)); 
		    String line = br.readLine();
            buffer.append(line);
         	br.close();
            return buffer;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    /** Pass in two files and zip together. Replace the encryptedFile
     * with the zip
     * @param encryptedPasswordFile
     * @param encryptedFile
     * @throws Exception
     */
    public static void zipAndReplaceFiles(File encryptedPasswordFile,File encryptedFile)
    	throws Exception
    {
    	File tempZipFile=File.createTempFile("tmp","zip",new File(encryptedFile.getParent()));
		File[] zippedFiles=new File[2];
		zippedFiles[0]=encryptedPasswordFile;
		zippedFiles[1]=encryptedFile;
		FileUtils.compressFiles(tempZipFile,zippedFiles);
		String encryptedFileName=encryptedFile.getAbsolutePath();
		boolean checkFile=encryptedFile.delete();
		if(!checkFile)
        {
        	System.out.println("Failed to delete "+encryptedFile);
        }
		checkFile=encryptedPasswordFile.delete();
		if(!checkFile)
        {
        	System.out.println("Failed to delete "+encryptedPasswordFile);
        }
		checkFile=tempZipFile.renameTo(new File(encryptedFileName));
		if(!checkFile)
        {
        	System.out.println("Failed to rename "+tempZipFile);
        }
    }
    
    /**
     * create a new file
     * @param fileName - filename
     * @param temp - true if file deletes when application closes
     * @return
     */
    public static File createNewFile(String fileName,boolean temp)
    {
        try
        {
            File file = new File(fileName);
            if(temp==false)
            {
                boolean checkFile=file.createNewFile();
                if(!checkFile)
                {
                	System.out.println("Failed to create "+file);
                }
            }
            else
            {
                String name=file.getName();
                String suffix=name.substring(file.getName().indexOf("."));
                boolean checkFile=file.delete();
                if(!checkFile)
                {
                	System.out.println("Failed to delete "+file);
                }
                file = File.createTempFile("tmp",suffix,new File(file.getParent()));
                file.deleteOnExit();
            }
            return file;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
   /**
    * Write to a file
    * @param fileName
    * @param contents
    * @param temp - true if file deletes when application closes
    * @return
    */
    public static File writeFile(String fileName,StringBuffer contents,boolean temp)
    {
        FileOutputStream fos = null;
        try
        {
            File file = createNewFile(fileName,temp);
            if(file==null)
            {
                return null;
            }
            fos=new FileOutputStream(file);
            BufferedOutputStream bos =  new BufferedOutputStream(fos);
            int length=contents.length();
            for(int i=0;i<length;i++)
            {
            	bos.write(contents.charAt(i));
            }
            bos.close();
            bos.flush();
            fos.close();
            return file;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    
 	
 	/**
 	 * Method to get a String from the byte array
 	 * @param toDecompress - file to decompress
 	 * @return the uncompressed data
 	 * @throws Exception
 	 */
 	public static byte[] upZipHandedByte(File toDecompress)
 		throws Exception
 	{
 		ZipEntry entry;
        ZipFile zipfile = new ZipFile(toDecompress);
        Enumeration e = zipfile.entries();
        while(e.hasMoreElements()) 
        {
        	// there should only be one        
           entry = (ZipEntry) e.nextElement();
           BufferedInputStream is = new BufferedInputStream(zipfile.getInputStream(entry));
           ByteArrayOutputStream byteStream=new ByteArrayOutputStream();
           BufferedOutputStream  outStream=new BufferedOutputStream(byteStream); 
           int count=0;
           int BUFFER=2048;
           byte data[] = new byte[BUFFER];
          
           while ((count = is.read(data, 0, BUFFER)) != -1)
           {
        	   outStream.write(data, 0, count);
           }
           outStream.flush();
           outStream.close();
           is.close();
           return byteStream.toByteArray();
        }
        return null;
        
 	}
 	
 	/**
 	 * Find the file with the prefix key in the zip file handed
 	 * @param file
 	 * @param prefix
 	 * @return
 	 * @throws Exception
 	 */
 	public static String findKeyFileInZip(File file,String prefix)
 		throws Exception
 	{
 		 try 
 		 {
 	        // Open the ZIP file
 	        ZipFile zf = new ZipFile(file);
 	    
 	        // Enumerate each entry
 	        for (Enumeration entries = zf.entries(); entries.hasMoreElements();) 
 	        {
 	            // Get the entry name
 	            String zipEntryName = ((ZipEntry)entries.nextElement()).getName();
 	            File testFile=new File(zipEntryName);
 	            if(testFile.getName().startsWith(prefix))
 	            {
 	            	zf.close();
 	            	return zipEntryName;
 	            }
 	        }
 	       zf.close();
 	    }
 		catch (IOException e) 
 		{
 			throw e;
 	    }
 		return null;
 	}
 	
 	/**
 	 * Decompress the file found in the zip file
 	 * null is returned if the zip file can not be found
 	 * @param fileToDecompress
 	 * @param zipFile
 	 * @param location
 	 * @return the decompressed file or null
 	 * @throws exception
 	 */
 	public static File decompressFile(String fileToDecompress,File zipFile,String tempFileLocation,boolean ignorePath)
 		throws Exception
 	{
 		File decompressedFile=null;
 		FileInputStream fis = new FileInputStream(zipFile.toString());
 		ZipInputStream zin = new ZipInputStream(new BufferedInputStream(fis));
 		//ZipFile zf = new ZipFile(zipFile);
 		ZipEntry thisEntry=(ZipEntry)zin.getNextEntry();
 		while (thisEntry!=null) 
 		{
            // Get the entry name
 			String zipEntryName = thisEntry.getName();
            if(ignorePath==true)
            {
            	if(zipEntryName.lastIndexOf("\\")>0)
            	{
            		// windows
            		zipEntryName=zipEntryName.substring(zipEntryName.lastIndexOf("\\")+1);
            	}
            	else if(zipEntryName.lastIndexOf("/")>0)
            	{
            		// other
            		zipEntryName=zipEntryName.substring(zipEntryName.lastIndexOf("/")+1);
            	}
            	//  no path !
            }
            if(zipEntryName.compareTo(fileToDecompress)==0)
            {
            	File tempFile=createNewFile(tempFileLocation,true);
            	BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(tempFile)); 
            	// we have the file
            	int len;
            	byte[] buf = new byte[1024];

            	while ((len = zin.read(buf)) > 0) 
            	{
                    bos.write(buf, 0, len);
                }
            	fis.close();
            	zin.close();
            	bos.flush();
            	bos.close();
            	return tempFile;
            }
            thisEntry=(ZipEntry)zin.getNextEntry();
        }
 		fis.close();
    	zin.close();
 		
 		return decompressedFile;
 	}
 	
 	
    /**
     * Compress the handed files
     * @param toCompress the returned base file
     * @param filesToCompress - the list of files to compress
     * @return the compressed file
     * @throws Exception
     */
    public static File compressFiles(File compressFile,File[] filesToCompress)
    	throws Exception
    {
    	String originalName=compressFile.getAbsolutePath();
    	try 
    	{
            BufferedInputStream origin = null;
            File tempFile=createNewFile(compressFile.getAbsolutePath()+".zip",false);
            FileOutputStream dest = new FileOutputStream(tempFile.getAbsoluteFile());
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            //out.setMethod(ZipOutputStream.DEFLATED);
            byte data[] = new byte[BUFFER];
            // get a list of files from current directory
            for(int i=0;i<filesToCompress.length;i++)
            {
	            FileInputStream fi = new FileInputStream(filesToCompress[i]);
	            origin = new BufferedInputStream(fi, BUFFER);
	            ZipEntry entry = new ZipEntry(filesToCompress[i].getAbsolutePath());
	            out.putNextEntry(entry);
	            int count;
	            while((count = origin.read(data, 0,BUFFER)) != -1) 
	            {
	            	out.write(data, 0, count);
	            }
	            origin.close();
	            out.closeEntry();
            }
            out.close();
            boolean checkFile=compressFile.delete();
            if(!checkFile)
            {
            	System.out.println("Failed to delete "+compressFile);
            }
            File origFile=new File(originalName);
            checkFile=tempFile.renameTo(origFile);
            if(!checkFile)
            {
            	System.out.println("Failed to rename "+tempFile);
            }
            return origFile;
    	}
    	catch(Exception e) 
    	{
    		e.printStackTrace();
    		throw e;
    	}
    }
    
    /**
     * Method to get a String from the byte array
     * @param input
     * @return string data of input
     * @throws Exception
     */
 	public static String upZipHandedByte(byte[] input)
 		throws Exception
 	{
 		//long time1=System.currentTimeMillis();
 		ByteArrayInputStream inStream=new ByteArrayInputStream(input);
 		ByteArrayOutputStream outStream=new ByteArrayOutputStream();
 		ZipInputStream in = new ZipInputStream(inStream);	
 		((ZipInputStream)in).getNextEntry();
 		write(in, outStream);
 		in.closeEntry();
 		//long time2=System.currentTimeMillis();
        //System.out.println("uncompression time ="+(time2-time1));
 		return outStream.toString();
 	}
 	
 	/**
 	 * Write available data to the outputstream
     * @param in
 	 * @param out
 	 * @throws IOException
 	 */
     private static final void write(InputStream in, OutputStream out) throws IOException{
         byte[] buffer = new byte[BUFFER];
         int count = 0;

         do {
             out.write(buffer, 0, count);
             count = in.read(buffer, 0, buffer.length);
         } while (count != -1);
     }
    
    /**
     * Decompress the handed file
     * @param toDecompress
     * @return The decompressed file
     * @throws Exception
     */
    public static File decompressFile(File toDecompress)
    	throws Exception
    {
    	try 
    	{
    		String originalName=toDecompress.getAbsolutePath();
    		File tempFile=new File(toDecompress.getAbsoluteFile()+".zip");
    		boolean checkFile=toDecompress.renameTo(tempFile);
    		if(!checkFile)
            {
            	System.out.println("Failed to create "+tempFile);
            }
            BufferedOutputStream dest = null;
            BufferedInputStream is = null;
            ZipEntry entry;
            ZipFile zipfile = new ZipFile(tempFile);
            Enumeration e = zipfile.entries();
            while(e.hasMoreElements()) 
            {
               entry = (ZipEntry) e.nextElement();
               is = new BufferedInputStream(zipfile.getInputStream(entry));
               int count;
               byte data[] = new byte[BUFFER];
               FileOutputStream fos = new FileOutputStream(entry.getName());
               dest = new BufferedOutputStream(fos, BUFFER);
               while ((count = is.read(data, 0, BUFFER))!= -1) 
               {
                  dest.write(data, 0, count);
               }
               zipfile.close();
               dest.flush();
               dest.close();
               is.close();
               checkFile=toDecompress.renameTo(new File(originalName));
               if(!checkFile)
               {
               	System.out.println("Failed to rename "+originalName);
               }
               checkFile=tempFile.delete();
               if(!checkFile)
               {
               	System.out.println("Failed to delete "+tempFile);
               }
               return toDecompress;
            }
            return null;
         } 
    	catch(Exception e) 
    	{
    		e.printStackTrace();
    		throw e;
        }

    }
    
    /**
     * Testing
     * @param args
     */
    public static void main(String[] args)
    {
    	try
    	{
    		System.out.println(FileUtils.findKeyFileInZip(new File("c:/cip.sql.safe"),"key"));
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
}
