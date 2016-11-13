package ArrayVPN;

import java.awt.*;

import javax.swing.*;

import com.borland.jbcl.layout.*;
import java.awt.event.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * <p>Title: Java Vesion Citrix TCS</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: ArrayNetworks, Inc.</p>
 *
 * @author Zhifeng Xia  Peng Huaping
 * @version 1.5
 */
public class CredentialsDlg extends JDialog implements ActionListener, KeyListener{
	private static final long serialVersionUID = -858964222166180378L;
	XYLayout MyXyLayout = new XYLayout();
	JLabel Username_Label = new JLabel();
	JLabel Password_Label = new JLabel();
	JLabel Host_Label = new JLabel();
	JLabel Port_Label = new JLabel();
	JLabel Domain_Label = new JLabel();
	JButton OK_Button = new JButton();
	JButton Cancel_Button = new JButton();
	JTextField Username_Txt = new JTextField();
	JTextField Domain_Txt = new JTextField();
	JPasswordField Pwd_Txt = new JPasswordField();
	String m_proxyhost = "";
	int m_proxyport = 0;

	public static final int Result_OK = 1;
	public static final int Result_Cancel = 0;
	int m_result = Result_Cancel;
	
	public static final int NTLM_PROXY = 1;
	public static final int BASIC_PROXY = 0;
	
	//multilingual support
	private Locale currentLocale;
	private ResourceBundle multi_lang_text;
	
	private String multilingText(String key) {
		String ret;
		try {
			ret = multi_lang_text.getString(key);
		} catch (java.util.MissingResourceException e){
			ret = "key <" + key + "> not found";
		}
		return ret;
	}
	//multilingual support
	
	public CredentialsDlg(Frame owner, String title, boolean modal,
			String proxyhost, int proxyport, int proxy_type, String lang) {
		super(owner, title, modal);
		
		if (lang.indexOf("chinese") != -1) {
			if(lang.indexOf("big5") != -1 || lang.indexOf("traditional") != -1){
				currentLocale = new Locale("zh", "TW");
			}else{
				currentLocale = new Locale("zh", "CN");
			}
		} else if (lang.indexOf("japanese") != -1) {
			currentLocale = new Locale("ja", "JP");
		} else {
			currentLocale = new Locale("en", "US");
		} 
		multi_lang_text = ResourceBundle.getBundle("ArrayVPN.Credentials", currentLocale);
		
		m_proxyhost = proxyhost;
		m_proxyport = proxyport;
		try {
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			jbInit(proxy_type);
			pack();
			centerScreen();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public CredentialsDlg(String proxyhost, int proxyport, int proxy_type, String lang) {
		this(new Frame(), "CredentialsDlg", false, proxyhost, proxyport, proxy_type, lang);
	}

	private void jbInit(int proxy_type) throws Exception {
		
		this.getContentPane().setLayout(MyXyLayout);
		Username_Label.setFont(new java.awt.Font("Arial", Font.PLAIN, 11));
		Username_Label.setText(multilingText("uname_lable"));
		Password_Label.setFont(new java.awt.Font("Arial", Font.PLAIN, 11));
		Password_Label.setText(multilingText("pass_lable"));
		Host_Label.setFont(new java.awt.Font("Arial", Font.PLAIN, 13));
		Host_Label.setText(multilingText("proxy_host") + m_proxyhost);
		Port_Label.setFont(new java.awt.Font("Arial", Font.PLAIN, 13));
		Port_Label.setText(multilingText("proxy_port") + m_proxyport);
		if(NTLM_PROXY == proxy_type) {
			Domain_Label.setFont(new java.awt.Font("Arial", Font.PLAIN, 11));
			Domain_Label.setText(multilingText("domain_lable"));
			Domain_Txt.setFont(new java.awt.Font("Arial", Font.PLAIN, 11));
			Domain_Txt.setText("");		
			Domain_Txt.addKeyListener(this);
		}
		OK_Button.setFont(new java.awt.Font("Arial", Font.PLAIN, 11));
		OK_Button.setSelected(true);
		OK_Button.setText(multilingText("ok_btn"));
		OK_Button.setDefaultCapable(true);
		OK_Button.addActionListener(this);
	
		Cancel_Button.setFont(new java.awt.Font("Arial", Font.PLAIN, 11));
		Cancel_Button.setText(multilingText("cancel_btn"));
		Cancel_Button.addActionListener(this);
		Username_Txt.setFont(new java.awt.Font("Arial", Font.PLAIN, 11));
		Username_Txt.setText("");
		Username_Txt.addKeyListener(this);
		
		this.setModal(true);
		this.setResizable(false);
		this.setTitle("");
		Pwd_Txt.setFont(new java.awt.Font("Arial", Font.PLAIN, 12));
		Pwd_Txt.setText("");		
		Pwd_Txt.addKeyListener(this);
		
		MyXyLayout.setHeight(260);
		
		this.getContentPane().add(Host_Label, 
				new XYConstraints(13, 20, -1, -1));
		this.getContentPane().add(Port_Label, 
				new XYConstraints(13, 60, -1, -1));
		
		this.getContentPane().add(Username_Label,
				new XYConstraints(13, 100, -1, -1));
		this.getContentPane().add(Password_Label,
				new XYConstraints(13, 140, -1, -1));		
		
		this.getContentPane().add(OK_Button,
				new XYConstraints(50, 139 + 84, 65, -1));
		this.getContentPane().add(Cancel_Button,
				new XYConstraints(164, 139 + 84, -1, -1));		
		this.getContentPane().add(Username_Txt,
				new XYConstraints(88, 100, 168, -1));
		this.getContentPane().add(Pwd_Txt,
				new XYConstraints(88, 140, 168, -1));
		
		if(NTLM_PROXY == proxy_type) {			
			this.getContentPane().add(Domain_Label,
					new XYConstraints(13, 180, -1, -1));
			this.getContentPane().add(Domain_Txt,
					new XYConstraints(88, 180, 168, -1));
		}
		
		MyXyLayout.setWidth(300);
	}	
	
	// centers the dialog within the screen [1.1]
	//  (put that in the Frame/Dialog class)
	public void centerScreen() {
		Dimension dim = getToolkit().getScreenSize();
		Rectangle abounds = getBounds();
		setLocation((dim.width - abounds.width) / 2,
				(dim.height - abounds.height) / 2);
		setResizable(false);
		requestFocus();
	}

	// centers the dialog within the parent container [1.1]
	//  (put that in the Dialog class)
	public void centerParent() {
		int x;
		int y;

		// Find out our parent
		Container myParent = getParent();
		Point topLeft = myParent.getLocationOnScreen();
		Dimension parentSize = myParent.getSize();

		Dimension mySize = getSize();

		if (parentSize.width > mySize.width) {
			x = ((parentSize.width - mySize.width) / 2) + topLeft.x;
		} else {
			x = topLeft.x;
		}

		if (parentSize.height > mySize.height) {
			y = ((parentSize.height - mySize.height) / 2) + topLeft.y;
		} else {
			y = topLeft.y;
		}

		setResizable(false);
		setLocation(x, y);
		requestFocus();
	}

	public int showDialog() {
		//show();
		setVisible(true);
		return m_result;
	}

	public String getUserName() {
		return Username_Txt.getText().trim();
	}

	public String getPasswd() {
		return String.valueOf(Pwd_Txt.getPassword()).trim();
	}

	public String getDomain() {		
		return Domain_Txt.getText().trim();
	}

	public void OKButton_EnterKey() {		
		m_result = Result_OK;
		dispose();
	}

	public void CancelButton_EscKey() {				
		m_result = Result_Cancel;
		dispose();
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(multilingText("ok_btn"))) {
			OKButton_EnterKey();
		} else if (e.getActionCommand().equals(multilingText("cancel_btn"))) {
			CancelButton_EscKey();
		}		
	}
	
	public void keyTyped(KeyEvent e) { }	
	public void keyReleased(KeyEvent e) { }
	public void keyPressed(KeyEvent e) { 		
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
    		OKButton_EnterKey();
    	} else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
    		CancelButton_EscKey();
    	}
	}
	
	public void setAuthMode(int mode) {
		switch (mode) {
		case 0: //basic
			Domain_Txt.setEnabled(false);
			Domain_Label.setEnabled(false);
			break;

		case 1: //ntml
			Domain_Txt.setEnabled(true);
			Domain_Label.setEnabled(true);
			break;

		default: //error, noting to do
			break;
		}
	}	
}


