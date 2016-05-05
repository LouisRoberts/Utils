package org.lorob.utils;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Dialog class to ask users for FTP username and password
 * @author lorob
 *
 */
public class FTPAccessDialog  extends JDialog
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5739463427857635032L;
	private JTextField _userNameField;
	private JPasswordField _passwordField;
	private JButton _OKBut;
	private FTPAccessDialogListener _listener;
	private Frame _owner;
	private FTPUtils _fTPUtils;
	private String _server;
	
	/**
	 * Constructor
	 * @param owner
	 * @param userName
	 * @param password
	 * @param listener
	 */
	public FTPAccessDialog(Frame owner,String userName,String password,FTPAccessDialogListener listener,String server)
	{
		super(owner,"FTP Access Dialog",true);
		_listener=listener;
		_owner=owner;
		_server=server;
		// GUI
		setSize(300,180);
		
		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(2,2,2,2);
		gbc.gridx=0;
		gbc.gridy=0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx=0.0;
		gbc.weighty=0.0;
		gbc.anchor=GridBagConstraints.NORTHWEST;
		
		JLabel usernameLabel=new JLabel("User Name:");
		_userNameField=new JTextField(userName);
		JLabel passwordLabel=new JLabel("Password");
		_passwordField=new JPasswordField(password);
		_OKBut=new JButton("OK");
		
		JLabel instructions=new JLabel("Please enter FTP username and password for "+server);
		this.getContentPane().add(instructions,gbc);
		
		gbc.gridy++;
		gbc.gridwidth =1;
		this.getContentPane().add(usernameLabel,gbc);
		gbc.gridx++;
		this.getContentPane().add(_userNameField,gbc);
		
		gbc.gridy++;
		gbc.gridx=0;
		this.getContentPane().add(passwordLabel,gbc);
		gbc.gridx++;
		this.getContentPane().add(_passwordField,gbc);
		
		gbc.gridy++;
		gbc.gridx=0;
		this.getContentPane().add(_OKBut,gbc);
		
		// listeners
		addListeners();
		
		setVisible(true);
	}
	
	/**
	 * Add listeners to dialog
	 *
	 */
	private void addListeners()
	{
		_passwordField.addKeyListener(new KeyAdapter()
		{

			public void keyPressed(KeyEvent arg0)
			{
				if(arg0.getKeyCode()==KeyEvent.VK_ENTER)
				{
					 submitDetails();
				}
				
			}
			
		});
		_OKBut.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				 submitDetails();
			}
		});
	}
	
	private void submitDetails()
	{
		char[] password=_passwordField.getPassword();
		String userName=_userNameField.getText();
		if(userName==null)
		{
			GUIUtils.showInformationDialog(_owner,"Missing username","FTP Dialog Error");
			return;
		}
		if(password==null)
		{
			GUIUtils.showInformationDialog(_owner,"Missing password","FTP Dialog Error");
			return;
		}
		// try and log in
		_fTPUtils=new FTPUtils(userName,new String(password),_server);
		try
		{
			_fTPUtils.readFtpDirectory("/");
		}
		catch(Exception e)
		{
			GUIUtils.showInformationDialog(_owner,"UserName and Password combination incorrect","FTP Dialog Error");
			return;
		}
		
		_listener.fireFTPLoggingDetailsChanged(userName,new String(password));
		dispose();
	}
}
