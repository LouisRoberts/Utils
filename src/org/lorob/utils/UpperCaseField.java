package org.lorob.utils;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * Upper case field. Flinched from 
 * http://java.sun.com/j2se/1.3/docs/api/javax/swing/JTextField.html
 * @author lorob
 *
 */
public class UpperCaseField extends JTextField {
	 
    /**
	 * 
	 */
	private static final long serialVersionUID = -5535957391009477881L;

	/**
	 * Constructor
	 * @param cols
	 */
	public UpperCaseField(int cols) {
        super(cols);
    }
	
	/**
	 * Constructor
	 * @param string
	 */
	public UpperCaseField(String string)
	{
		super(string);
	}

    protected Document createDefaultModel() {
	      return new UpperCaseDocument();
    }

    static class UpperCaseDocument extends PlainDocument {

       private static final long serialVersionUID = 4564808427249293579L;

		public void insertString(int offs, String str, AttributeSet a) 
	          throws BadLocationException {

	          if (str == null) {
		      return;
	          }
	          char[] upper = str.toCharArray();
	          for (int i = 0; i < upper.length; i++) {
		      upper[i] = Character.toUpperCase(upper[i]);
	          }
	          super.insertString(offs, new String(upper), a);
	      }
    }
}

