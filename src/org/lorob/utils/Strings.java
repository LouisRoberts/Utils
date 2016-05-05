package org.lorob.utils;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


/**
 * Utility class for string manipulations via static methods
 * @author lorob
  */
public final class Strings {

    //constants used by the escapeQuotes() method
    private static final String BACKSLASH = "\\" ;
    private static final String ESCAPED_BACKSLASH = "\\\\" ;
//    private static final String ESCAPED_BACKSLASH = "&#92;" ;
    private static final String DOUBLE_QUOTE = "\"";
    private static final String ESCAPED_DOUBLE_QUOTE = "\\\"";
//    private static final String ESCAPED_DOUBLE_QUOTE = "&#34;";
    private static final String SINGLE_QUOTE = "'";
    private static final String ESCAPED_SINGLE_QUOTE = "&#39;";

  /**
   * @return a String with all occurrences of <code>findValue</code>
   * in <code>replaceIn</code> replaced by <code>replaceWith</code>.
   */
  public static final String replace(String findIn, String findValue,
  final String replaceWith) {
    if (findIn==null) {
      return null;
    } else if (findValue==null || replaceWith==null || findValue.equals(replaceWith)) {
      return findIn;
    }
    final StringBuffer replaced = new StringBuffer(findIn) ;
    doReplace(findIn, findValue, replaceWith, new ReplaceAction() {
      public boolean performReplace(int replaceFrom, int replaceTo) {
        replaced.replace(replaceFrom, replaceTo, replaceWith);
        return true;
      }}
    );
    return replaced.toString();
  }

  /**
   * @return true if the String <code>findIn</code> is found within the String
   * <code>findValue</code>, false otherwise.
   */
  public static final boolean contains(String findIn, String findValue) {
    if (findIn==null && findValue==null) {
      return true;
    } else if (findIn==null) {
      return false;
    } else if (findValue==null) {
      return false;
    } else if (findValue.equals(findIn)) {
      return true;
    }
    return findIn.indexOf(findValue) > -1;
  }

  /**
   * Convert a String to title case and optionally condense it
   * @param baseOn the String to convert
   * @param wordSeparator the String that separates each word in baseOn
   * @param removeSeparator true to remove all occurrences of the wordSeparator,
   *  false otherwise
   * @return the converted value
   */
  public static final String toCondensedTitleCase(String baseOn, String wordSeparator,
  final boolean removeSeparator) {
    if (baseOn==null || baseOn.equals("")) {
      return baseOn;
    }
    final char[] tcChar = baseOn.toLowerCase().toCharArray();
    tcChar[0] = Character.toTitleCase(tcChar[0]);

    doReplace(baseOn, wordSeparator, wordSeparator, new ReplaceAction() {
      public boolean performReplace(int replaceFrom, int replaceTo) {
        if (replaceTo < tcChar.length && Character.isLetter(tcChar[replaceTo])) {
          tcChar[replaceTo] = Character.toTitleCase(tcChar[replaceTo]);
        }
        return true;
      }}
    );

    String tcVal = new String(tcChar);
    if (removeSeparator) {
      return replace(tcVal, wordSeparator, "");
    } else {
      return tcVal;
    }
  }

  /**
   * @return the value of the passed in String with all words capitalised
   */
  public static final String toTitleCase(String baseOn) {
    return toCondensedTitleCase(baseOn, " ", false);
  }

  /** @return the index of the 1st occurrence of a lowercase character
   *  within the specified String, or -1 if not found
   */
  public static final int posLowerCase(String findIn) {
    if (findIn==null || findIn.length()==0) {
      return -1;
    }
    int index = 0;
    char[] charArray = findIn.toCharArray();
    for (;index<charArray.length; ++index) {
      if (Character.isLowerCase(charArray[index])) {
        return index;
      }
    }
    return -1;
  }

  /** @return the number of occurrences of <code>findValue</code> within
   *   <code>replaceIn</code>, or -1 if not found
   */
  public static final int occurrencesOf(String findIn, String findValue) {
    if (findIn==null || findValue==null
    || findIn.length()==0 || findValue.length()==0) {
      return -1;
    } else if (findValue.equals(findIn)) {
      return 1;
    }
    final StringBuffer replaced = new StringBuffer() ;
    doReplace(findIn, findValue, findValue, new ReplaceAction() {
      public boolean performReplace(int replaceFrom, int replaceTo) {
        replaced.append('1');
        return true;
      }}
    );
    return replaced.length();
  }

  // The method that does the real work!
  private static final boolean doReplace(String findIn, String findValue,
  String replaceWith, ReplaceAction action) {
    char[] tempBuffer = findIn.toCharArray(), find = findValue.toCharArray();
    int currentOffset = 1, lengthDiff = replaceWith.length() - findValue.length();
    boolean findAgain = true, found = false;
    for (int i = 0, j = 0; findAgain && i < tempBuffer.length && j < find.length; ++i) {
      if (tempBuffer[i]==find[j]) {
        ++j;
      } else {
        j = 0;
      }
      if (j==find.length) {
        found = true;
        findAgain = action.performReplace(currentOffset+i-j, currentOffset+i);
        currentOffset += lengthDiff;
        j=0;
      }
    }
    return found;
  }

  // The interface required for doReplace() method
  private interface ReplaceAction {
    // return true to continue within the doReplace loop, false otherwise
    boolean performReplace(int replaceFrom, int replaceTo);
  }

  /**
   * @return the value of the passed in String with all double and single quotes plus backslash escaped
   */
  public static String escapeQuotes (String baseOn) {
    String result = replace(baseOn , BACKSLASH, ESCAPED_BACKSLASH );
    result = replace(result , DOUBLE_QUOTE, ESCAPED_DOUBLE_QUOTE );
    result = replace(result , SINGLE_QUOTE, ESCAPED_SINGLE_QUOTE );
    return result;
  }
  
  /**
   * Change the handed string to an XML compatible version 
   * @param inBuff
   * @return
   */
  public static StringBuffer makeXMLCompatible(String inBuff)
  {
      StringBuffer returnBuff=new StringBuffer("");
      if(inBuff==null)
      {
          return returnBuff;
      }
      int length=inBuff.length();
      for(int i=0;i<length;i++)
      {
          char queryChar=inBuff.charAt(i);
          if(queryChar=='&')
          {
              returnBuff.append("&amp;");
          }
          else if(queryChar=='<')
          {
              returnBuff.append("&lt;");
          }
          else if(queryChar=='>')
          {
              returnBuff.append("&gt;");
          }
          else if(queryChar=='"')
          {
              returnBuff.append("&quot;");
          }
          else if(queryChar=='\'')
          {
              returnBuff.append("&#39;");
          }
          else if(queryChar=='£')
          {
              returnBuff.append("&#163;");
          }
          else if(queryChar>(char)127)
          {
              int ichar=(int)queryChar;
              returnBuff.append("&#");
              returnBuff.append(Integer.toString(ichar));
              returnBuff.append(";");
          }
          else
          {
              returnBuff.append(queryChar);
          }
      }
      
      return returnBuff;
  }
  
  /**
   * convert the handed string to a HTML friendly version
   * @param inputStr
   * @return
   */
  public static String makeHTTPCompatible(String inputStr)
    {
        StringBuffer returnBuf=new StringBuffer();
        for(int i=0;i<inputStr.length();i++)
        {
            char thisChar=inputStr.charAt(i);
            if(thisChar==' ')
			{
				returnBuf.append("%20");
			}
			else if(thisChar=='\"')
			{
				returnBuf.append("%22");
			}
			else if(thisChar=='<')
			{
				returnBuf.append("%3C");
			}
			else if(thisChar=='>')
			{
				returnBuf.append("%3E");
			}
			else if(thisChar=='[')
			{
				returnBuf.append("%5B");
			}
			else if(thisChar==']')
			{
				returnBuf.append("%5D");
			}
			/*else if(thisChar=='\\')
			{
				returnBuf.append("%5C";
			}*/
			else if(thisChar=='^')
			{
				returnBuf.append("%5E");
			}
			else if(thisChar=='\'')
			{
				//returnBuf.append("%60");
				returnBuf.append(thisChar);
			}
			else if(thisChar=='{')
			{
				returnBuf.append("%7B");
			}
			else if(thisChar=='|')
			{
				returnBuf.append("%7C");
			}
			else if(thisChar=='}')
			{
				returnBuf.append("%7D");
			}
			else if(thisChar=='~')
			{
				returnBuf.append("%7E");
			}
			else if(thisChar=='&')
			{
				returnBuf.append("%26amp;");
			}
			else
			{
				returnBuf.append(thisChar);
			}
        }
        return returnBuf.toString();
    }
  
  /**
   * COnvert the handed URL to a HTTP friendly version
   * @param inBuff
   * @return
   */
  public static String replaceToUrlCompatibleString(String inBuff)
  {
      StringBuffer returnBuff=new StringBuffer("");
      if(inBuff==null)
      {
          return returnBuff.toString();
      }
      int length=inBuff.length();
      for(int i=0;i<length;i++)
      {
          char queryChar=inBuff.charAt(i);
          if(queryChar==' ')
          {
              returnBuff.append("%20");
          }
          else if(queryChar=='\\')
          {
              returnBuff.append("/");
          }
          else
          {
              returnBuff.append(queryChar);
          }
      }
      return returnBuff.toString();
  }
    
  /**
   * Use the common library
   * @param object
   * @return
   */
  public static String toString(Object object)
  {
	  String objectString=ReflectionToStringBuilder.toString(object,ToStringStyle.MULTI_LINE_STYLE);
	  // may want to XMLify this - TODO
	  return objectString;
  }

  // static methods -- private constructor
  private Strings () {
    super();
  }
}

