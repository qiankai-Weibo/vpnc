package ArrayVPN;

import netscape.javascript.JSObject;
import java.applet.Applet;
import java.awt.Component;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.borland.jbcl.layout.XYConstraints;
import com.borland.jbcl.layout.XYLayout;

import java.math.BigInteger;

import java.util.Locale; 
import java.util.ResourceBundle;
//import crysec.SSL.SSLParams;

//import java.util.*;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */

public class ArrayVPNClient extends Applet {
//	public static void Main(String [] arg) {
//		Locale currentLocale;
//		ResourceBundle multi_lang_text;
//		currentLocale = new Locale("zh", "CN");
//		multi_lang_text = ResourceBundle.getBundle("ArrayVPN.multilingual", currentLocale);
//		System.out.println(multi_lang_text.getString("assigned_ip"));
//	}

	private static final long serialVersionUID = -1648917976993522224L;
	boolean isStandalone = false;
//	BorderLayout borderLayout1 = new BorderLayout();
//	String m_spname;
	int m_flags;
	boolean m_sp2 = true;
	boolean isMacOS = true;
	boolean isLinux = false;
	boolean bUbuntu = false;
    Socket vpnc = null;
    int exitnow = 0;
	private static final int VALID_CONFIG = 0xFEEDC0DE;
	private static final int TERMINATE = 0xDEEDC0DE;
	private static final int SEC_UNAME_LEN = 64;
	private static final int SEC_PASSWD_LEN = 32;
	private static final int MAX_CONFIG_STRLEN = 256;
	private static final int IPV6_ADDRESS_LEN = 40;
	private static final String vpnserver = "127.0.0.1";
	private static final int vpnport = 6666;

	// mark if the vpn_process has been killed (by clicking "disconnect"
	// button). If true,
	// set info "Disconnected", otherwise set to "due to sever disconnecting",
	// since maybe
	// the process has been destroied by 'kill' command other than clicking the
	// "Disconnect" button

	boolean has_killed = false;
	boolean monitor_now;
	int m_spip;
	int isipv6; //1 means ipv6 format address
	String m_spaddr;
	int m_spport;
	String m_spcompleteaddress;
	String m_sessid;
	String m_spver;
	String m_cputype;
	String m_navtype; // navigator type (Safari/Firefox)
	int m_auto_launch;
	int m_errcode;
	String m_errstr;
//	String m_StartTime;
	byte [] recvsock_buffer = new byte[100];
	XYLayout xYLayout1 = new XYLayout();
/*	JButton ConnectBtn = new JButton();
	JButton UninstallBtn = new JButton();
	JButton JavalogBtn = new JButton();
	MacVpnMainDlg m_maindlg;
*/
	JCheckBox LocalAccessChkBox = new JCheckBox();
/*	Border border1 = BorderFactory
			.createLineBorder(new Color(127, 157, 185), 2);
	Border border2 = BorderFactory
			.createLineBorder(new Color(127, 157, 185), 2);*/
//	JLabel InfoText = new JLabel();
	JTextArea InfoText = new JTextArea();
//	JLabel InfoTextErrMsg = new JLabel();
	JLabel clientipText = new JLabel();
	JLabel inputbytesText = new JLabel();
	JLabel outputbytesText = new JLabel();
	// communicate with execute file
//	BufferedInputStream m_stdin;
//	BufferedOutputStream m_stdout;
	DataInputStream m_stdin = null;
	DataOutputStream m_stdout = null;
	
	Process m_status;

	// When negotiating, closing portal would be considered as invalid quit.
	static byte m_invalidquit = 0;

//	PeriodTask m_ptask;

	volatile boolean m_needreconnect;

	String m_username, m_password, m_domain;
	String m_proxyhost;
	int m_proxyport;
	boolean m_doingbasic, m_doingntlm;

	// related to installation
	String m_instpath;

	Process m_instprocess = null;
	Thread m_monitor = null;
	int m_state = ST_START;

	// seconds
	int m_secs = 0;

	// schedule a task to run after some time
	Timer m_timer_run;
	Timer m_timer_uninst;
	TimerTask m_timetask_run;
	TimerTask m_monitortimertask_run;
	TimerTask m_timetask_uninst;
	
	String ipstr;
	String inputstr;
	String outputstr;
	private static final int ST_START = 0;
	private static final int ST_CONNECTED = 1;
	private static final int ST_FAILED = 2;
	private static final int ST_DISCONNECTED = 3;	

	private static final int ERR_NO_ERR = 0;
	private static final int ERR_IO_NOTREADY = 1;
	private static final int ERR_IO_EXCEPTION = 2;
	private static final int ERR_RESP_ERR = 3;
	private static final int ERR_RUN_VPNC = 4;
	private static final int ERR_MAINDLG_NULL = 5;
	private static final int ERR_MAINDLG_IO_NOTREADY = 6;
	private static final int ERR_GETSTATUS_FAIL = 7;
	private static final int ERR_NEGBASIC_RESP_ERR = 8;
	private static final int ERR_NEGBASIC_CREDENTIAL_FAIL = 9;
	private static final int ERR_NEGBASIC_EXCEPTION = 10;
	private static final int ERR_DETECTPROXY_NEEDAUTH = 11;
	private static final int ERR_DETECTPROXY_SERVERERR = 12;
	private static final int ERR_UNINSTALL_EXCEPTION = 13;
	private static final int ERR_AUTHPROXY_FAIL = 14;
	private static final int ERR_CONNECT_EXCEPTION = 15;
	private static final int ERR_CREATEDIR_FAIL = 16;
	private static final int ERR_DUPLICATE_NAME = 17;
	private static final int ERR_REMOVE_LOADER_FAIL = 18;
	private static final int ERR_DOWNLOAD_LOADER_FAIL = 19;
	private static final int ERR_REMOVE_VPNC_FAIL = 20;
	private static final int ERR_DOWNLOAD_VPNC_FAIL = 21;
	private static final int ERR_DOWNLOAD_TUNFILE_FAIL = 22;
	private static final int ERR_INSTALL_EXCEPTION = 23;
	private static final int ERR_NOPRIV_LOADER = 24;
	private static final int ERR_NOPRIV_VPNC = 25;
	private static final int ERR_NOPRIV_LOADER_ROOT = 26;
	private static final int ERR_NOPRIV_VPNC_ROOT = 27;
	private static final int ERR_INSTALL_CHMOD_EXCEPTION = 28;
	private static final int ERR_UPDATE_AUTHPROXY_FAIL = 29;
	private static final int ERR_UPDATE_EXCEPTION = 30;
	private static final int ERR_DOWNLOAD_UPDATEVPNC_FAIL = 31;
	private static final int ERR_DOWNLOAD_UPDATELOADER_FAIL = 32;
	private static final int ERR_UPDATE_DOWN_EXCEPTION = 33;
	private static final int ERR_CREATESOCK_EXCEPTION = 34;
	private static final int ERR_CONNECTSOCK_EXCEPTION = 35;
	private static final int ERR_PARSEHEADER_PREMATURE = 36;
	private static final int ERR_PARSEHEADER_REQUESTERR = 37;
	private static final int ERR_PARSEHEADER_INVALIDHDR = 38;
	private static final int ERR_INVALID_STATUS = 39;
	private static final int ERR_CHECKVERSION_FAIL = 40;
	private static final int ERR_INSTALLDRIVER_EXCEPTION = 41;
	private static final int ERR_DRIVER_NOTFOUND = 42;
	private static final int ERR_FINDDRIVER_EXCEPTION = 43;
	private static final int ERR_NEGNTLM_RESP_ERR = 44;
	private static final int ERR_NEGNTLM_EXCEPTION = 45;
	private static final int ERR_NEGNTLM_CREDENTIAL_FAIL = 46;

	private static final int ERR_DISCONNECTED = 65; // mac_vpnc down, should
													// reconnect
	private static final int ERR_INSTALL_CHOWN_ROOT_EXCEPTION = 66; // Mac OS
																	// system
																	// sleep
																	// signal
	// ADD NEW ERROR CODE FOR LINUX CHOWN ROOT
	private static final int ERR_SYS_SLEEP = 67;
	// applet to vpn client protocol codes
	private static final short CODE_NOOP = 0;
	private static final int CODE_CONFIG = 1;

	// applet to vpn client proxy type codes
	private static final byte PROXY_NONE = 0;
	private static final byte PROXY_NOAUTH = 1;
	private static final byte PROXY_BASIC = 2;
	private static final byte PROXY_NTLM = 3;

	private static final int PROXY_NONE_new = 0;
	private static final int PROXY_NOAUTH_new = 1;
	private static final int PROXY_BASIC_new = 2;
	private static final int PROXY_NTLM_new = 3;

	public static final int NTLM_PROXY = 1;
	public static final int BASIC_PROXY = 0;
	
	// temp for ssl param */
	//SSLParams m_param;

	// some const variant
	private static final String findit_cmd = "which";
	private static final String chmod_cmd = "chmod";
	private static final String chown_cmd = "chown";
	private static String default_path = "/usr/local/array_vpn/";
	private static final String tmp_install_path = "/tmp/";
	private static final String mac_array_loader_bin = "mac_loader";
	private static final String array_loader_bin = "array_loader";
	private static final String mac_array_vpnc_bin = "mac_vpnc";
	private static final String array_vpnc_bin = "array_vpnc";

	private static final String array_ncutil104_bin = "ncutil_104";
	private static final String array_ncutil105_bin = "ncutil_105";
	private static final String array_dir_marker = ".array";
	private static final String array_marker_txt = "ArrayNetworks autogenerated file. Do not modify or delete.";
	private static final String array_tun_install_tar = "an_tuninstaller_intel.tgz";

	String linux_array_loader_bin = array_loader_bin;
	String linux_array_vpnc_bin = array_vpnc_bin;

	Component sppliter = Box.createHorizontalStrut(8);
	JLabel LoggerLab = new JLabel();
	JScrollPane LoggerScroll = new JScrollPane();
	JTextArea LoggerTxtArea = new JTextArea();

	/* for debug showing */
	public boolean m_bIsDebugged = true;
	private int m_newHeight, m_origHeight, m_origWidth;
	
	//for multilingual
	private Locale currentLocale;
	private ResourceBundle multi_lang_text;
	private String lang;
	
	public void ShowDebugger(boolean debug) {
		m_bIsDebugged = debug;
		LoggerLab.setVisible(debug);
		LoggerScroll.setVisible(debug);
		LoggerTxtArea.setVisible(debug);
		if (debug) {
			setSize(m_origWidth, m_origHeight);
		} else {
			setSize(m_origWidth, m_newHeight);
		}
	}

	/*
	 * usage:sysarrayjavalog(this.getClass().getName() + " " +
	 * this.getClass().getMethods()[0].getName() + " " + errorinfo);
	 */
	private void sysarrayjavalog(String loginfo) {
		try {
			// Runtime rt = Runtime.getRuntime();
			// Process p1 = null;
			// if (isMacOS) {
			// p1 = rt.exec(new String[] { "/usr/bin/syslog", "-s", "-l", "3",
			// "arrayjavalog " + loginfo });
			// } else if (bUbuntu) {
			// p1 = rt.exec(new String[] { "/usr/bin/logger", "-p", "4",
			// "arrayjavalog " + loginfo });
			// } else {
			// p1 = rt.exec(new String[] { "logger", "-p", "3",
			// "arrayjavalog " + loginfo });
			// }
		} catch (Exception e) {
			Logger(e.getMessage());
		}
	}

	// Must use after Init()
	private void Logger(String log) {
		if (!m_bIsDebugged) {
			return;
		}
		System.out.println(log);
	}

	// Get a parameter value
	public String getParameter(String key, String def) {
		return isStandalone ? System.getProperty(key, def)
				: (getParameter(key) != null ? getParameter(key) : def);
	}

	// Construct the applet
	public ArrayVPNClient() {
		SimpleDateFormat bartDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");
		Date date = new Date();
	//	m_StartTime = bartDateFormat.format(date);
		// for log, do not delete	
	}

	// Initialize the applet
	public void init() {		
		/* Determine whether Mac or Linux OS */
		DeterminOStype();
		
		isipv6 = 0;
		try {
			m_spaddr = this.getParameter("sp_addr", "");
			sysarrayjavalog(" " + "init" + " "
					+ m_spaddr);
			Logger("Get IP address "+m_spaddr);
		} catch (Exception e) {
			Logger("get sp_addr exception: " + e.getMessage());
			e.printStackTrace();
		}
		try {		
			m_spport = Integer.parseInt(this.getParameter("sp_port", ""));			
			sysarrayjavalog(" " + "init" + " "
					+ m_spport);
		} catch (Exception e) {
			Logger("get sp_port exception: " + e.getMessage());
			e.printStackTrace();
		}
		try {
			m_sessid = this.getParameter("session_id", "");
		} catch (Exception e) {
			Logger("get session_id exception: " + e.getMessage());
			e.printStackTrace();
		}
		
		try {
			m_navtype = this.getParameter("navtype", "");
			sysarrayjavalog(" " + "init" + " "
					+ m_navtype);
		} catch (Exception e) {
			Logger("get navigator type exception: " + e.getMessage());
			e.printStackTrace();
		}
		
		try {
			m_spver = this.getParameter("version", "");
			sysarrayjavalog(" " + "init" + " "
					+ m_spver);
		} catch (Exception e) {
			Logger("get version exception: " + e.getMessage());
			e.printStackTrace();
		}
		try {
			m_auto_launch = Integer.parseInt(this.getParameter("auto_launch", ""));
		} catch (Exception e) {
			Logger("get autostart exception: " + e.getMessage());
			e.printStackTrace();
		}
		
		try {
			lang = this.getParameter("language", "").toLowerCase();
			Logger(" " + "Set Locale: " + " " + lang);
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
			multi_lang_text = ResourceBundle.getBundle("ArrayVPN.multilingual", currentLocale);
		} catch (Exception e) {
			Logger("get language exception: " + e.getMessage());
			e.printStackTrace();
		}

		/* sp1 or sp2 */
		
		m_sp2 = true;
		try {
			jbInit();
		} catch (Exception e) {
			Logger("jbInit exception: " + e.getMessage());
			e.printStackTrace();
		}	
		// try {
		// Runtime rt = Runtime.getRuntime();
		// Process p1 = null;
		// BufferedReader pin = null;
		// boolean done = false;
		// int ret = 0;
		//
		// p1 = rt.exec("uname -sr");
		// while (!done) {
		// try {
		// ret = p1.waitFor(); /* blocking call */
		// ret = p1.exitValue();
		// done = true;
		// } catch (Exception notdone) {
		// }
		// }
		//
		// pin = new BufferedReader(new InputStreamReader(p1.getInputStream()));
		// String os_typever = pin.readLine();
		// pin.close(); // only care about the first line of output
		// if (ret != 0 || os_typever == null) {
		// m_errcode = ERR_NOPRIV_VPNC;
		// m_errstr = "Get OS version fail.";
		// change_state(ST_FAILED);
		// throw new Exception(
		// "Insufficient priviledges, get OS version fail");
		// }
		// sysarrayjavalog(this.getClass().getName() + " " + "init"
		// + " Client os type and version: " + os_typever);
		// if (os_typever.indexOf("Darwin") == -1) {
		// isMacOS = false;
		// isLinux = true;
		// }
		//
		// if (isMacOS && os_typever.indexOf('9') == 7) {// Leopard
		// m_cputype = "LEOPARD";
		// sysarrayjavalog(this.getClass().getName() + " " + "init" + " "
		// + "cpu type is leopard");
		// }
		// if (isLinux) {
		// if (get_Architecture().toLowerCase().equals("x86_64")) {
		// m_array_loader_bin = linux_array_loader_bin + "64";
		// m_array_vpnc_bin = linux_array_vpnc_bin + "64";
		// }
		// if (IsUbuntu().equalsIgnoreCase("yes")) {
		// bUbuntu = true;
		// }
		//
		// }
		//
		// } catch (Exception e) {
		// Logger("get os version exception: " + e.getMessage());
		// sysarrayjavalog(this.getClass().getName() + " " + "init" + " "
		// + "get os version exception: " + e.getMessage());
		// e.printStackTrace();
		// }

		/*
		 * try { m_macusername = System.getProperty("user.name"); } catch
		 * (Exception e) { Logger("getProperty exception: " + e.getMessage());
		 * e.printStackTrace(); }
		 */
		// readLogFile();
		// other iniilization
		// initialize global variables to default values
		m_instpath = default_path;
		try {
			InetAddress address = InetAddress.getByName(m_spaddr);	
			int addrlen = address.getAddress().length;
			if((addrlen == 16) && (bUbuntu)&& (m_spaddr.indexOf(":") > 0)) {
				m_spcompleteaddress = new String("[" + m_spaddr + "]" + ":" + m_spport);
			} else {
				m_spcompleteaddress = new String(m_spaddr + ":" + m_spport);
			}
		} catch (Exception e) {
			Logger("init: Judge address type exception!");
		}
		
		m_username = null;
		m_password = null;
		m_domain = null;

		m_timer_run = null;
		m_timer_uninst = null;
		m_timetask_run = null;
		m_timetask_uninst = null;

		m_needreconnect = false;

		m_proxyhost = "";
		m_proxyport = 0;
		m_errcode = ERR_NO_ERR;

	/*	short[] ciphers = new short[1];
		ciphers[0] = SSLParams.SSL_RSA_WITH_RC4_128_MD5;
		SpinnerRandomBitsSource spinner = new SpinnerRandomBitsSource(10);
		m_param = new SSLParams();
		m_param.setRNG(spinner);
		m_param.setClientCipherSuites(ciphers);
		m_param.setCertVerifier(new SSLCertificateVerifier(false));
		m_param.setAllowSSL3(false);
		m_param.setAllowTLS1(true);
	 */
		m_stdin = null;
		m_stdout = null;

		m_invalidquit = 0; // initialize the variable to 0
	}
	
	private String multilingText(String key) {
		String ret;
		try {
			ret = multi_lang_text.getString(key);
		} catch (java.util.MissingResourceException e){
			ret = "key <" + key + "> not found";
		} 
		return ret;
	}

	// static{ ToolTipManager.sharedInstance().setDismissDelay(15000);}
	// Component initialization
	private void jbInit() throws Exception {
		this.setLayout(xYLayout1);

		xYLayout1.setWidth(800);
		xYLayout1.setHeight(100);
		this.setBackground(SystemColor.text);
		
	//	InfoText.setFont(new java.awt.Font("Arial", Font.PLAIN, 12));
	//	clientipText.setFont(new java.awt.Font("Arial", Font.PLAIN, 12));
	//	inputbytesText.setFont(new java.awt.Font("Arial", Font.PLAIN, 12));
	//	outputbytesText.setFont(new java.awt.Font("Arial", Font.PLAIN, 12));
			
		InfoText.setEditable(false);
		InfoText.setLineWrap(true);
		InfoText.setWrapStyleWord(true);
		InfoText.setText("");
		
		set_text(clientipText, multilingText("assigned_ip"));
		set_text(inputbytesText, multilingText("byte_recv"));
		set_text(outputbytesText, multilingText("byte_sent"));
		
		
		this.add(InfoText, new XYConstraints(30, 20, 770, 35));
		this.add(clientipText, new XYConstraints(30, 60, 300, 25));
		this.add(outputbytesText, new XYConstraints(300, 60, 250, 25));
		this.add(inputbytesText, new XYConstraints(550, 60, 250, 25));		

		if (!m_sp2) {
		//	LocalAccessChkBox.setFont(new java.awt.Font("Arial", Font.ITALIC, 11));
			LocalAccessChkBox.setText("Disable Local Access");
			LocalAccessChkBox.setSelected(true);
			LocalAccessChkBox.setOpaque(false);
			LocalAccessChkBox.addActionListener(new MacVPNClient_LocalAccessChkBox_actionAdapter(this));

			// local access
			boolean exists = (new File("/usr/local/array_vpn/localaccesscfg")).exists();
			if (exists) {
				try {
					FileReader fr = new FileReader(
							"/usr/local/array_vpn/localaccesscfg");
					BufferedReader br = new BufferedReader(fr);
					String record = new String();
					if ((record = br.readLine()) != null) {
						sysarrayjavalog(" "
								+ "jbInit" + " localaccess status is: "
								+ record);
						if (record.equals("1"))// userenable
						{
							LocalAccessChkBox.setEnabled(true);
							LocalAccessChkBox.setSelected(false);
						}
						if (record.equals("2"))// userdisable
						{
							LocalAccessChkBox.setEnabled(true);
							LocalAccessChkBox.setSelected(true);
						}
						if (record.equals("0"))// spdisable
						{
							LocalAccessChkBox.setEnabled(false);
						}
					}
					br.close();
					fr.close();
				} catch (IOException e) {
					sysarrayjavalog(" " + "jbInit"
							+ " localaccess status is: "
							+ "got an IOException error");
					e.printStackTrace();
				}
			} else {
				// File or directory does not exist
				sysarrayjavalog(" " + "jbInit"
						+ " localaccess file does not exist! ");
			}
		}	
		if (!m_sp2) {
			this.add(LocalAccessChkBox, new XYConstraints(224, 220, 150, 23));
		}	
	}

	// get session id
	private String GetSession(String cookie) {
		int ind = 0;
		int j = 0;
		String ret = "NULL";

		if (cookie.indexOf("ANsession") == -1) {
			return ret;
		}
		ind = cookie.indexOf("=");
		if (ind == -1) {
			return ret;
		}
		j = cookie.indexOf(";", ind);
		if (j == -1) {
			return ret;
		}
		ret = cookie.substring(ind + 1, j);
		return ret;
	}

	private String check_session_expired() {
		String status = "Expired";
		String cookiestr = "";
		String sessionstr = "";

		// check if the session has expired
		try {
			netscape.javascript.JSObject win;
			netscape.javascript.JSObject docu;
			win = (netscape.javascript.JSObject) netscape.javascript.JSObject
					.getWindow(this);
			// win.eval("window.location.reload(true);");
			docu = (netscape.javascript.JSObject) win.getMember("document");
			cookiestr = (String) docu.getMember("cookie");
			sessionstr = GetSession(cookiestr);
			if (sessionstr.equals(m_sessid)) {
				status = "Connected";
			}
		} catch (Exception e) {
			Logger("Unable to determine client status");
			status = "Expired";
			return status;
		}
		Logger("m_sessid=" + m_sessid);
		Logger("cursessid=" + sessionstr);
		return status;
	}
	
	private String famatshow(byte[] bytearray, int bytearraylen) {
		BigInteger b = BigInteger.valueOf(0);
		BigInteger kb =  BigInteger.valueOf(1024);
		BigInteger mb =  BigInteger.valueOf(1024*1024);
		BigInteger gb =  BigInteger.valueOf(1024*1024*1024);
		BigInteger tb =  gb.multiply(kb);
		BigInteger pb =  tb.multiply(kb);
		BigInteger eb =  pb.multiply(kb);
		BigInteger mul = BigInteger.valueOf(100);
		String fatstr;
		String integer;
		String decimal;

		BigInteger data = new BigInteger(bytes2str(bytearray, 0, bytearraylen));	
		if((data.compareTo(b) >= 0) && (data.compareTo(kb) < 0)) {
			integer = data.toString();
			fatstr = new String(integer + " B");
		} else if((data.compareTo(kb) >= 0) && (data.compareTo(mb) < 0)) {
			integer = data.divide(kb).toString();
			decimal = data.mod(kb).multiply(mul).divide(kb).toString();		
			if(1 == decimal.length()){
				decimal = new String("0" + decimal);				
			} else if(0 == decimal.length()){
				decimal = new String("00");				
			}
			fatstr = new String(integer + "." + decimal + " KB");
		} else if ((data.compareTo(mb) >= 0) 
				&& (data.compareTo(gb) < 0)) {
			integer = data.divide(mb).toString();
			decimal = data.mod(mb).multiply(mul).divide(mb).toString();			
			if(1 == decimal.length()){
				decimal = new String("0" + decimal);				
			} else if(0 == decimal.length()){
				decimal = new String("00");				
			}			
			fatstr = new String(integer + "." + decimal + " MB");
		} else if ((data.compareTo(gb) >= 0) 
				&& (data.compareTo(tb) < 0)) {
			integer = data.divide(gb).toString();
			decimal = data.mod(gb).multiply(mul).divide(gb).toString();
			if(1 == decimal.length()){
				decimal = new String("0" + decimal);				
			} else if(0 == decimal.length()){
				decimal = new String("00");				
			}		
			fatstr = new String(integer + "." + decimal + " GB");
		} else if ((data.compareTo(tb) >= 0) 
				&& (data.compareTo(pb) < 0)) {
			integer = data.divide(tb).toString();		
			decimal = data.mod(tb).multiply(mul).divide(tb).toString();
			if(1 == decimal.length()){
				decimal = new String("0" + decimal);				
			} else if(0 == decimal.length()){
				decimal = new String("00");				
			}		
			fatstr = new String(integer + "." + decimal + " TB");
		} else if ((data.compareTo(pb) >= 0) 
				&& (data.compareTo(eb) < 0)) {
			integer = data.divide(pb).toString();
			decimal = data.mod(pb).multiply(mul).divide(pb).toString();			
			if(1 == decimal.length()){
				decimal = new String("0" + decimal);				
			} else if(0 == decimal.length()){
				decimal = new String("00");				
			}		
			fatstr = new String(integer + "." + decimal + " PB");
		} else {
			integer = data.divide(eb).toString();
			decimal = data.mod(eb).multiply(mul).divide(eb).toString();	
			if(1 == decimal.length()){
				decimal = new String("0" + decimal);				
			} else if(0 == decimal.length()){
				decimal = new String("00");				
			}		
			fatstr = new String(integer + "." + decimal + " EB");		
		}
		return fatstr;
	}
	
	public boolean read_statistic() {
		boolean log_more = true;
		try {
			byte[] config = new byte[4];
			byte[] temp = null;
			int offset = 0, i = 0;
			temp = int2bytes(VALID_CONFIG, 4);
			for (i = 0; i < temp.length; i++) {
				config[offset++] = temp[i];
			}		
			m_stdout.write(config, 0, config.length);
			m_stdout.flush();	
			try {
				byte[] assign_ip = new byte[15];
				byte[] inputbytes = new byte[100];
				byte[] outputbytes = new byte[100];	
				int len_assign_ip = m_stdin.read(assign_ip);
				int len_inputbytes = m_stdin.read(inputbytes);
				int len_outputbytes = m_stdin.read(outputbytes);
				if((0 == len_assign_ip) || (-1 == len_assign_ip)
						|| (0 == len_inputbytes) || (-1 == len_inputbytes) 
						|| (0 == len_outputbytes) || (-1 == len_outputbytes)) {		
					ipstr = null;
					inputstr = null;
					outputstr = null;
				} else {
					ipstr = bytes2str(assign_ip, 0, len_assign_ip);
					inputstr = famatshow(inputbytes, len_inputbytes);
					outputstr = famatshow(outputbytes, len_outputbytes);						
				}				
			}catch (Exception e) {		
				ipstr = null;
				inputstr = null;
				outputstr = null;
				Logger("read ip error" + "  " + e.getLocalizedMessage() + "    " + e.getMessage());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block			
			ipstr = null;
			inputstr = null;
			outputstr = null;
			if (log_more) {
				Logger("failed to read data from c "+e.getMessage());
				log_more = false;
			}
		}
		return true;
	}
	/*public boolean isReady_old() {
		byte[] rev = { 2 };

		if (m_stdin == null || m_stdout == null) {
			Logger("failed from executable, stdin and stdout are null.");
			m_errcode = ERR_IO_NOTREADY;
			m_errstr = "input/output not ready.";
			return false;
		}
		try {
			m_stdout.write(rev);
			m_stdout.flush();
			m_stdin.read(rev);
		} catch (Exception ig) {
			ig.printStackTrace();
			Logger("exception: failed from executable: " + ig.getMessage());
			sysarrayjavalog(this.getClass().getName() + " " + "isReady"
					+ " exception: failed from executable: " + ig.getMessage());

			// check if vpn is launching
			try {
				Thread.sleep(1000);
			} catch (Exception serr) {
			}
			String status = GetConnStat();
			if (status.equals("Connected")) {
				Logger("Call vpn binary success.");
				return true;
			}
			m_errcode = ERR_IO_EXCEPTION;
			m_errstr = "Your session has expired. Please: login again.";
			return false;
		}
		if (rev[0] != 1) {
			Logger("array_vpnc not ready.");
			// check if vpn is launching
			try {
				Thread.sleep(1000);
			} catch (Exception serr) {
			}
			String status = GetConnStat();
			if (status.equals("Connected")) {
				Logger("Call vpn binary successfully.");
				return true;
			}

			m_errcode = ERR_RESP_ERR;
			m_errstr = "Cannot connecte to SP, please check the cable.";
			return false;
		}
		Logger("Success from executable: " + rev[0]);
		return true;
	}
*/

	public void monitor_thread() {	
		if(!monitor_now) {
			return;
		}	
		try {	
			read_statistic();
			if((ipstr == null || ipstr.isEmpty()) 
					|| (inputstr == null || inputstr.isEmpty()) 
					|| (outputstr == null || outputstr.isEmpty())) {
				change_state(ST_FAILED);
				InfoText.setText(multilingText("info_startVpnFailed"));		
				monitor_now = false;
				set_text(clientipText, multilingText("assigned_ip"));
				set_text(inputbytesText, multilingText("byte_recv"));
				set_text(outputbytesText, multilingText("byte_sent"));
				
			} else if(0 == ipstr.compareTo("0.0.0.0")) {
				StopVPN();
				return;
			} else {
				InfoText.setText(multilingText("info_connected"));
				change_state(ST_CONNECTED);
				set_text(clientipText, multilingText("assigned_ip") + ipstr);
				set_text(inputbytesText, multilingText("byte_recv") + inputstr);
				set_text(outputbytesText, multilingText("byte_sent") + outputstr);
			}
		} catch (Exception e) {
			m_errcode = ERR_NO_ERR; // when user click "disconnect", this
										// may occur, reset to 0
			Logger("Process exception due to waitfor or exitValue:"
					+ e.getMessage());
		}
	}

	private void creat_timer() {		
		m_timer_run = new Timer();
	}
	
	private void destroy_timer() {				
		if (m_timer_run != null) {
			m_timer_run.cancel();
			m_timer_run = null;
		}		
	}
	
	private void destroy_monitortimertask() {				
		if (m_monitortimertask_run != null) {
			m_monitortimertask_run.cancel();
			m_monitortimertask_run = null;
		}	
	}
	
	private void destroy_timertask() {				
		if (m_timetask_run != null) {
			m_timetask_run.cancel();
			m_timetask_run = null;
		}	
	}
	
	private void start_vpnEx() {
		m_timetask_run = new TimerTask() {
			public void run() {
				schedule_task();
			}
		};
		m_timer_run.schedule(m_timetask_run, 200);	
	}
	
	private void start_monitor() {
		m_monitortimertask_run = new TimerTask() {
			public void run() {
				try {
					monitor_thread();
				} catch (Exception e) {
					e.printStackTrace();
					//should catch, or timer will terminate when fresh page repeatly.	
				}
			}
		};		
		m_timer_run.schedule(m_monitortimertask_run, 1000, 1000);	
	}

	public int connect_sock_client() {
		Logger("begin to set socket");
		
        InetSocketAddress addr = new InetSocketAddress(vpnserver,
        		vpnport);
        
        int i = 0;
        while ((i < 3) && (null == vpnc)) {
            try {
            	vpnc = new Socket();
            	vpnc.setTcpNoDelay(true);
            	vpnc.setSendBufferSize(1);
            	vpnc.setSoLinger(true, 10);
            	try {
            		vpnc.connect(addr);
            	} catch (IOException ioe) {
            		Logger("connect error!");
            		throw ioe;
            	}
		break;
            } catch (Exception e) {
		    Logger("socket error! try again.");
		    try {
			    Thread.sleep(1000);
		    } catch (InterruptedException e1) {
			    // TODO Auto-generated catch block
			    e1.printStackTrace();
		    }                
	    } 
	    close_socketvpnc();
	    ++i;            
        }

		Logger("begin to set input stream");
		try {
			m_stdin = new DataInputStream(vpnc.getInputStream());
		} catch (Exception e) {
			Logger("stand in data stream get failed");
		}
		Logger("begin to set output stream");
		try {
			m_stdout = new DataOutputStream(vpnc.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (vpnc == null || m_stdin == null || m_stdout == null) {
			Logger("Connect sock to client failed");
			return -1;
		}
		return 0;
	}
	
	public void start_vpn() {
		Runtime rt = null;
		int ret = -1;
		boolean done = false;
		Process p = null;
		
		m_errcode = ERR_NO_ERR; // reset		
		
		if (!prepare_start()) {
			change_state(ST_FAILED);		
			return;
		}
		InfoText.setText(multilingText("info_connecting"));
			
		try {
			InetAddress ia = InetAddress.getByName(m_spaddr);	
			int addrlen = ia.getAddress().length;
			Logger("InetAddress.getByName address: " + ia.getHostAddress());
			Logger("InetAddress.getByName address length: " + addrlen);
			if (addrlen== 16) {
				isipv6 = 1;
			}
			else{
				m_spip = bytes2int(ia.getAddress(), 0, 4);
			}
			try {
				Thread.sleep(1000); // Sleep 1 second on the first install, then
									// run it to avoid exec exception
			} catch (Exception e) {
				Logger("sleep exception: " + e.getMessage());
			}

			do {
				Logger("start client process");
				// start the client process

				try {
					rt = Runtime.getRuntime();
					rt.exec(new String[] { default_path
							+ array_vpnc_bin });
					try {
						Thread.sleep(1000); // Sleep 1 second to wait for client's socket ready
					} catch (Exception e){
						e.printStackTrace();
					}
				} catch (Exception e) {
					Logger("run vpn client or sleep exception: " + e.getMessage());
					return;
				}
				Logger("vpn client start sucessfully");
				ret = connect_sock_client();
				if (ret != 0) {
					InfoText.setText(multilingText("info_failedLanuch"));
					change_state(ST_FAILED);
					return;
				}
				Logger("begin to encode packet");
				// generate the configuration data and send it to the client
				byte[] config_packet = encode_config_packet_new();
				Logger("write pkt, pktlen:" + config_packet.length);
				
				Logger("being to transfer configure");
				m_stdout.write(config_packet, 0, config_packet.length);
				m_stdout.flush();
				config_packet = null; // allow it to be garbage collected
				// clear all stdin characters
				Logger("call isready.");
				m_needreconnect = false;	
				rt = Runtime.getRuntime();
				p = rt.exec(new String[] {
						"/bin/bash",
						"-c",
						"rm -rf " + default_path + "inst.sh "
						        + default_path + "mac_chmod.sh"
								+ default_path + "linux_chmod.sh"
								+ default_path + "an*.tgz "
								+ default_path + "*.*pkg" });
				ret = -1;
				done = false;
				while (!done) {
					try {
						ret = p.waitFor();
						ret = p.exitValue();
						done = true;
					} catch (Exception notdone) {
						// keep waiting
					}
				}			

				m_errcode = ERR_NO_ERR; // reset the errcode to 0

				if (!m_sp2) {
					// local access
					try {
						FileReader fr = new FileReader(
								"/usr/local/array_vpn/localaccesscfg");
						BufferedReader br = new BufferedReader(fr);
						String record = new String();
						if ((record = br.readLine()) != null) {
							sysarrayjavalog(" "
									+ "start_vpn" + " localaccess status is: "
									+ record);
							if (record.equals("1"))// userenable
							{
								LocalAccessChkBox.setEnabled(true);
								LocalAccessChkBox.setSelected(false);
							}
							if (record.equals("2"))// userdisable
							{
								LocalAccessChkBox.setEnabled(true);
								LocalAccessChkBox.setSelected(true);
							}
							if (record.equals("0"))// spdisable
							{
								LocalAccessChkBox.setEnabled(false);
							}
						}
						br.close();
						fr.close();
					} catch (IOException e) {						
						e.printStackTrace();
					}
				}	
			} while (m_needreconnect); // reconnect
		} catch (Exception ig) {
			Logger("Runing main excutable encounters exception: "
					+ ig.getMessage());
			ig.printStackTrace();
			m_errcode = ERR_RUN_VPNC;
			m_errstr = "Runing main excutable exception:" + ig.getMessage();
			change_state(ST_FAILED);
		}
	}

	public void schedule_task() {
		start_vpn();

		if (m_state == ST_FAILED) {// success case is handled.
			InfoText.setText(multilingText("info_failedLanuch") + " (code: " + m_errcode + ").");
		}
	}

	private boolean IsVPNC2_client() {
		Process p1 = null;
		boolean done;
		int ret = 0;
		BufferedReader pin = null;
		
		try {
			p1 = Runtime.getRuntime().exec(
					new String[] {default_path + array_loader_bin,
					               "-v" });
			done = false;
			while (!done) {
				ret = p1.waitFor();
				ret = p1.exitValue();
				done = true;
			}
		
			pin = null;
			pin = new BufferedReader(new InputStreamReader(
				p1.getInputStream()));
		
			String inst_ver = pin.readLine();
			pin.close(); // only care about the first line of output
			if (ret != 0 || inst_ver == null) {
				Logger("Cannot check the version, assuming not installed.");
			}
			if (inst_ver.indexOf("VPNC2_") == -1) {
				return false;
			} 
		} catch (Exception e) {
			Logger("check the version exception:"+e.getMessage());
			return true;
		}
		return true;
	}
	private String GetConnStat() {
		String stat = "Disconnected";
		Process p1 = null;
		boolean done;
		int ret = 0;
		
		try {
			p1 = Runtime.getRuntime().exec(
					new String[] {default_path + array_loader_bin,
					               "status" });
			
			done = false;
			while (!done) {
				ret = p1.waitFor();
				ret = p1.exitValue();
				done = true;
			}
		} catch (Exception notdone) {
			Logger("lookup process exception:"
					+ notdone.getMessage());
			return stat;
		}
		if (ret == 2) {
			return stat;
		} else {
			stat = "Connected";
		}

		return stat;
	}

	public int GetVPNStatus() {	
		return m_state;	
	}
	
	public void logout_to_server() {
		java.net.Proxy proxy = null;
		String fileurl = "https://" + m_spcompleteaddress + "/prx/000/http/localhost/logout";
		HttpsURLConnection conn = null;
		try {
			if (m_proxyhost.length() > 0 && m_proxyport > 0) {
				java.net.InetAddress addr = InetAddress.getByName(m_proxyhost);
				InetSocketAddress sa = new InetSocketAddress(addr, m_proxyport);

				proxy = new java.net.Proxy(Proxy.Type.HTTP, sa);
			}

			URL url = new URL(fileurl);
			if (m_proxyhost.length() > 0 && m_proxyport > 0) {
				conn = (HttpsURLConnection) url.openConnection(proxy);
			} else {
				Logger("being to openconnection ");	
				conn = (HttpsURLConnection) url.openConnection();
			}
			java.io.InputStream fileIn = conn.getInputStream();
			byte[] value = new byte[1024];		
			while (fileIn.read(value) > 0){};
			fileIn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private void close_datainputstream() {
		try {
			if(null != m_stdin) {
				m_stdin.close();
				m_stdin = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	private void close_dataoutputstream() {
		try {
			if(null != m_stdout) {
				m_stdout.close();
				m_stdout = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	private void close_socketvpnc() {
		try {
			if(null != vpnc) {
				vpnc.close();
				vpnc = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	//this function will be called when close ie
	public void stop() {			
		destroy_monitortimertask();
		destroy_timertask();
		destroy_timer();
		close_datainputstream();
		close_dataoutputstream();
		close_socketvpnc();
		
		if(isMacOS){
			return;
		}
		/*
		 *Use JSObject to get document , In firefox when close window, we can get a exception
		 *and we use this exception to judge wether the browser is refresh or close
		 */
		try{
			JSObject myBrowser = (JSObject) JSObject.getWindow(this);
			JSObject myDocument =  (JSObject) myBrowser.getMember("document");
			String myCookie = (String)myDocument.getMember("cookie");

		}catch(Exception e){
			if (m_state != ST_CONNECTED) {
				String status = GetConnStat();
				if (!status.equals("Connected")) {
					return;
				}
			}
			terminate();
			/* tell server to kill user session */
			logout_to_server();	
		}
	}
	
	//Start the applet
	public void start() {
		String status="";

		if(IsVPNC2_client()) {
			status = GetConnStat();
		}

		if ((status.equals("Disconnected") && m_auto_launch == 0)) {
			change_state(ST_DISCONNECTED);				
			InfoText.setText(multilingText("info_clickStartBtn"));
			return;
		}		

		creat_timer();		
		monitor_now = true;	
		if (status.equals("Connected")) {
			int trynum = 0;
			int ret = -1;
			change_state(ST_CONNECTED);
			while(ret != 0 && trynum < 3) {
				ret = connect_sock_client();
				if(0 == ret) {
					continue;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				trynum++;
			}
			if (ret != 0) {
				Logger("connect failed");
				InfoText.setText(multilingText("info_failedLanuch"));
				change_state(ST_FAILED);				
				return;
			}	
		} else {		
		/* now download, launch and start vpn */
			start_vpnEx();			
		}
		start_monitor();
		
	}

	private synchronized void set_text(JLabel label, String status) {
		label.setText(status);
	}

	// Stop the applet
	public void StopVPN() {
		change_state(ST_START);	
		destroy_monitortimertask();
		destroy_timertask();
		destroy_timer();	
		Logger("terminate is called");
		terminate();	
		close_datainputstream();
		close_dataoutputstream();
		close_socketvpnc();
	}

	// Destroy the applet
	public void destroy() {
			
	}

	// Get Applet information
	public String getAppletInfo() {
		return "Applet Information";
	}

	// Get parameter info
	public String[][] getParameterInfo() {
		java.lang.String[][] pinfo = { { "sp_name", "String", "" },
				{ "sp_addr", "String", "" }, { "sp_port", "int", "" },
				{ "m_sessid", "String", "" }, };
		return pinfo;
	}

	public void ConnectBtn_actionPerformed(ActionEvent e) {
		/*
		if (ConnectBtn.getText().compareTo("Connect") == 0) {
			if (m_timer_uninst != null) {
				m_timer_uninst.cancel();
				m_timer_uninst = null;
			}
			if (m_timetask_uninst != null) {
				m_timetask_uninst = null;
			}

			start_vpnEx();
			if (m_state == ST_CONNECTED) {				
			}

			has_killed = false;
		} else {
			terminate();
			if (m_maindlg != null) {
				m_maindlg.setBtnClose(true);
				m_maindlg.CloseDlg();
				m_maindlg = null;
			}
		}
		*/
	}	

	public void PeriodTask() {
	/*
		m_secs++;
		
		if (m_maindlg == null) {
			Logger("PeriodTask, maindlg is null.");
			m_errcode = ERR_MAINDLG_NULL;
			m_errstr = "Maindlg is null";
			return;
		}

		String status = "Connected: ";
		status += String.valueOf(m_secs) + " seconds";
		m_maindlg.UpdateStatus(status, null, null, null, true, false);

		if (m_stdin == null || m_stdout == null) {
			Logger("PeriodTask, stdinout is null.");
			m_errcode = ERR_MAINDLG_IO_NOTREADY;
			m_errstr = "In periodTask, input/output is null.";
			return;
		}

		byte[] code = { 0 };

		try {
			byte[] rev = new byte[128];
			byte[] assign_ip = new byte[4];

			m_stdout.write(code);
			m_stdout.flush();
			m_stdin.read(rev);
			assign_ip[0] = rev[0];
			assign_ip[1] = rev[1];
			assign_ip[2] = rev[2];
			assign_ip[3] = rev[3];
			/*
			 * Update IP and in/out bytes. Here we can get the right IP address.
			 * When send config_packet to FILE_OUT, call stdin.read() once may
			 * not get the entire result, it depends on different MacOS machine.
			 * So we process the IO operation here since it will be called
			 * periodically, even if the result is wrong at the first several
			 * seconds, it will be right several seconds later.

			m_maindlg.UpdateStatus(null, InetAddress.getByAddress(assign_ip)
					.toString().substring(1),
					String.valueOf(bytes2int(rev, 4, 4)),
					String.valueOf(bytes2int(rev, 8, 4)), true, false);
			rev = null;
			assign_ip = null;
		} catch (Exception e) {
			Logger("Can't get status: " + e.getMessage());
			m_errcode = ERR_GETSTATUS_FAIL;
			m_errstr = "Get status exception:" + e.getMessage();
		}*/
	}

	// from linux java
	// avoid String.getBytes due to utf-8 bug in jdk 1.1
	private static byte[] str2bytes(String str) {
		char[] chars = str.toCharArray();
		byte[] bytes = new byte[chars.length];

		int i;
		for (i = 0; i < chars.length; i++) {
			bytes[i] = (byte) (chars[i] & 0xFF);
		}

		return bytes;
	}

	private static byte[] str2bytes(String str, int len) {
		char[] chars = str.toCharArray();
		byte[] bytes = new byte[len];

		int i;
		for (i = 0; i < chars.length; i++) {
			bytes[i] = (byte) (chars[i] & 0xFF);
		}
		for (i = chars.length; i < len; i++) {
			bytes[i] = 0;
		}

		return bytes;
	}

	// convert C-string (1 byte char) into Java-string (2 byte char)
	private static String bytes2str(byte[] bytes, int offset, int len) {
		int i;
		byte[] temp = new byte[1];
		String return_str = new String("");

		for (i = offset; i < offset + len; i++) {
			temp[0] = bytes[i];
			if (temp[0] == 0) {
				break;
			}
			return_str += new String(temp);
		}

		return return_str;
	}

	// convert from int to bytes in network order (BE)
	private static byte[] int2bytes(int num, int len) {
		int i;
		byte[] bytes = new byte[len];

		for (i = (len - 1) * 8; i >= 0; i -= 8) {
			bytes[(len - 1) - i / 8] = (byte) ((num >> i) & 0xFF);
		}

		return bytes;
	}

	// convert from bytes in network order (BE) to int
	private static int bytes2int(byte[] bytes, int offset, int len) {
		int i, num = 0;
		int[] mask = new int[4];

		mask[0] = 0xFF000000;
		mask[1] = 0x00FF0000;
		mask[2] = 0x0000FF00;
		mask[3] = 0x000000FF;

		for (i = (len - 1) * 8; i >= 0; i -= 8) {
			num |= ((int) bytes[offset + (len - 1) - i / 8] << i)
					& mask[3 - i / 8];
		}
		return num;
	}

	// construct a configuration packet from the available information
	private byte[] encode_config_packet() throws UnknownHostException {
		byte[] config = new byte[1];
		return config;
		// create the CONFIG packet
	/*	
		int config_len = 2 + // code
				4 + // packet length
				// 4 + // sp1 or sp2 ; 1 for sp1 ,2 for sp2
				2 + m_spname.length() + 4 + // server IP
				2 + // m_spport
				2 + m_sessid.length() + 1 + // proxy type
				2 + m_navtype.length() + 1 + // session start time
				2 + m_StartTime.length();
		if (m_proxyhost.length() > 0 && m_proxyport > 0) {
			config_len += 4 + 2; // proxy IP and port
		}
		if (m_username != null && m_password != null) {
			config_len += 2 + m_username.length() + 2 + m_password.length();
		}
		if (m_domain != null) {
			config_len += 2 + m_domain.length();
		}
		

		int i, offset, proxytype_offset;
		byte[] temp = null;

		offset = 0; // we bump this every write
		// it would be cool if Java supported macros

		temp = int2bytes(CODE_CONFIG, 2);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}

		temp = int2bytes(config_len, 4);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}

		// add new for sp version (sp1 or sp2)
		// int ver = 1; // sp1
		// if (m_sp2) {
		// ver = 2; // sp2
		// }
		// temp = int2bytes(ver, 4);
		// for (i = 0; i < temp.length; i++) {
		// config[offset++] = temp[i];
		// }
		// ######################
		temp = int2bytes(m_spname.length(), 2);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}
		temp = str2bytes(m_spname);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}

		temp = int2bytes(m_spip, 4);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}

		temp = int2bytes(m_spport, 2);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}
		temp = int2bytes(m_sessid.length(), 2);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}
		temp = str2bytes(m_sessid);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}

		if (m_proxyhost.length() > 0 && m_proxyport > 0) {
			// store this so we can backtrack if needed
			proxytype_offset = offset;
			// set the proxy type as proxy_noauth initially
			config[offset++] = PROXY_NOAUTH;

			temp = InetAddress.getByName(m_proxyhost).getAddress();
			for (i = 0; i < temp.length; i++) {
				config[offset++] = temp[i];
			}

			temp = int2bytes(m_proxyport, 2);
			for (i = 0; i < temp.length; i++) {
				config[offset++] = temp[i];
			}

			if (m_username != null && m_password != null) {
				// either basic or ntlm - assume basic
				config[proxytype_offset] = PROXY_BASIC;

				temp = int2bytes(m_username.length(), 2);
				for (i = 0; i < temp.length; i++) {
					config[offset++] = temp[i];
				}
				temp = str2bytes(m_username);
				for (i = 0; i < temp.length; i++) {
					config[offset++] = temp[i];
				}

				temp = int2bytes(m_password.length(), 2);
				for (i = 0; i < temp.length; i++) {
					config[offset++] = temp[i];
				}
				temp = str2bytes(m_password);
				for (i = 0; i < temp.length; i++) {
					config[offset++] = temp[i];
				}
				/* NTLM is not supported on Mac OS for now. 
				/*
				 * if (m_domain != null) { //it was ntlm after all
				 * config[proxytype_offset] = PROXY_NTLM;
				 * 
				 * temp = int2bytes(m_domain.length(), 2); for (i = 0; i <
				 * temp.length; i++) { config[offset++] = temp[i]; } temp =
				 * str2bytes(m_domain); for (i = 0; i < temp.length; i++) {
				 * config[offset++] = temp[i]; } }
				 
			}
		} else {
			config[offset++] = PROXY_NONE;
		}
		temp = int2bytes(m_navtype.length(), 2);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}
		temp = str2bytes(m_navtype);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}

		temp = int2bytes(m_StartTime.length(), 2);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}
		temp = str2bytes(m_StartTime);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}
		return config;*/
	}

	private byte[] encode_config_packet_new() throws UnknownHostException {
		int len = 4 * 7 + IPV6_ADDRESS_LEN + SEC_UNAME_LEN + SEC_PASSWD_LEN + MAX_CONFIG_STRLEN * 4;
		byte[] config = new byte[len];

		int i, offset;
		byte[] temp = null;
		offset = 0;
		
		temp = int2bytes(VALID_CONFIG, 4);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}
		// version
/*		temp = int2bytes(version, 4);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}

		temp = int2bytes(CODE_CONFIG, 4);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}
*/
		temp = int2bytes(isipv6, 4);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}		
		
		if (isipv6 > 0) {
			InetAddress address = InetAddress.getByName(m_spaddr);			
			temp = str2bytes(address.getHostAddress(), IPV6_ADDRESS_LEN);
			for (i = 0; i < temp.length; i++) {
				config[offset++] = temp[i];
			}			
		} else {
			for (i = 0; i < IPV6_ADDRESS_LEN; i++) {
				config[offset++] = 0;
			}	
		}		
		
		temp = int2bytes(m_spip, 4);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}

		temp = int2bytes(m_spport, 4);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}
		
		if (m_proxyhost.length() > 0 && m_proxyport > 0) {
			Logger("enter encode_config_packet_new 2");
			// store this so we can backtrack if needed
			//proxytype_offset = offset;
			// set the proxy type as proxy_noauth initially
			// config[offset++] = PROXY_NOAUTH;

			if (m_doingntlm) {
				temp = int2bytes(PROXY_NTLM_new, 4);
			} else {
				temp = int2bytes(PROXY_BASIC_new, 4);
			}
			for (i = 0; i < temp.length; i++) {
				config[offset++] = temp[i];
			}

			InetAddress ia = InetAddress.getByName(m_proxyhost);
			int proxyip = bytes2int(ia.getAddress(), 0, 4);

			temp = int2bytes(proxyip, 4);
			for (i = 0; i < temp.length; i++) {
				config[offset++] = temp[i];
			}

			temp = int2bytes(m_proxyport, 4);
			for (i = 0; i < temp.length; i++) {
				config[offset++] = temp[i];
			}

			if (m_username != null && m_password != null) {
				// either basic or ntlm - assume basic

				temp = str2bytes(m_username, SEC_UNAME_LEN);
				for (i = 0; i < temp.length; i++) {
					config[offset++] = temp[i];
				}

				temp = str2bytes(m_password, SEC_PASSWD_LEN);
				for (i = 0; i < temp.length; i++) {
					config[offset++] = temp[i];
				}
				/* NTLM is not supported on Mac OS for now. */
				/*
				 * if (m_domain != null) { //it was ntlm after all
				 * config[proxytype_offset] = PROXY_NTLM;
				 * 
				 * temp = int2bytes(m_domain.length(), 2); for (i = 0; i <
				 * temp.length; i++) { config[offset++] = temp[i]; } temp =
				 * str2bytes(m_domain); for (i = 0; i < temp.length; i++) {
				 * config[offset++] = temp[i]; } }
				 */
			} else {
				
				temp = new byte[SEC_PASSWD_LEN + SEC_UNAME_LEN];
				for (i = 0; i < temp.length; i++) {
					config[offset++] = temp[i];
				}
			}
		} else {
			Logger("enter encode_config_packet_new3 ");
			temp = int2bytes(PROXY_NONE_new, 4);
			for (i = 0; i < temp.length; i++) {
				config[offset++] = temp[i];
			}
			/* 4 proxy ip; 4 proxy port */
			temp = new byte[SEC_PASSWD_LEN + SEC_UNAME_LEN + 8];
			for (i = 0; i < temp.length; i++) {
				config[offset++] = temp[i];
			}
		}
		
		temp = str2bytes(m_sessid, MAX_CONFIG_STRLEN);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}

		// domain ntlm not suppurt
		if (m_doingntlm) {
			temp = str2bytes(m_domain, MAX_CONFIG_STRLEN);
		} else {
			temp = new byte[MAX_CONFIG_STRLEN];
		}
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}
		
		temp = str2bytes(m_navtype, MAX_CONFIG_STRLEN);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}
	/*	temp = str2bytes(m_StartTime, MAX_CONFIG_STRLEN);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}
*/
		temp = str2bytes(m_spaddr, MAX_CONFIG_STRLEN);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}
		Logger("return encode_config_packet_new");
		return config;
	}

	private String getLocalHostName() throws UnknownHostException{ 	
		String hostname;
		
		try{  
			InetAddress netAddress;
			netAddress = InetAddress.getLocalHost();  			
			hostname = netAddress.getHostName();
			Logger(hostname);
        }catch(UnknownHostException e){  
        	hostname = null;
        	Logger("unknown host!");  
        }		
		return hostname;		
    }  
	
	/*Please look this url: http://www.innovation.ch/personal/ronald/ntlm.html
	 * http://www.blogjava.net/security/archive/2008/11/18/38717.html*/
	private Socket negotiate_ntlm(String host, int port) {
		try {
			String auth_method = "<none>";			
			int status = 0;	
			int tries = 0;
			String host_name = new String("");
			Socket sock = null;			

			Logger(host + "   " + port);
			while (status != 200) {
				tries++;
				if (tries > 3) {					
					change_state(ST_FAILED);
					Logger("negotiate_ntlm, not 200.");
					m_errcode = ERR_NEGNTLM_RESP_ERR;
					m_errstr = "negotiate_ntlm failed.";
					return null;
				}
				
			// try to reuse existing credentials first
				if (m_username == null || m_password == null) {
					CredentialsDlg dlg = new CredentialsDlg(m_proxyhost,
							m_proxyport, NTLM_PROXY, lang);
					dlg.setTitle("NTLM PROXY");

					if (dlg.showDialog() == dlg.Result_OK) {
						m_username = dlg.getUserName();
						m_password = dlg.getPasswd();
						m_domain = dlg.getDomain();	
						host_name = getLocalHostName();
						Logger("proxy username  " + m_username + "   passwd: "
								+ m_password + "   domain: " + m_domain + "  hostname: " + host_name);
						if ((host_name == null) || m_username.isEmpty() || m_password.isEmpty() 
							|| m_domain.isEmpty()) {
							m_username = null;
							m_password = null;
							m_domain = null;
							host_name = null;
							continue;
						}
				} else {
					Logger("negotiate_ntlm, use credentials fail.");
					InfoText.setText(multilingText("info_no_uname_domain"));
					change_state(ST_FAILED);
					m_errcode = ERR_NEGNTLM_CREDENTIAL_FAIL;
					m_errstr = "negotiate_ntlm, use credentials fail..";					
					return null;
				}					
			}

			/* if authentication is required, do it now */
			NTLM ntlm = new NTLM();
			String strFirst = ntlm.getResponseFor("", "", "", host_name, m_domain);
			Logger("ret:" + strFirst);
			sock = new Socket(host, port);
			InputStream sp_in = sock.getInputStream();
			OutputStream sp_out = sock.getOutputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(sp_in));
			BufferedOutputStream buff_out = new BufferedOutputStream(sp_out, 1460);

			/* authenticate against a proxy host */
			String connect = "CONNECT " + m_spcompleteaddress
					+ " HTTP/1.0\r\nProxy-Authorization: NTLM " + strFirst
					+ "\r\n\r\n";

			/* send the CONNECT call */
			/* since we use 1-byte chars */
			buff_out.write(str2bytes(connect), 0, connect.length());

			buff_out.flush(); /* no more to write */

			/* read the response */
			String header = "";
			while ((header = reader.readLine()) != null) {
				if (header.length() == 0) {
					break; /* end of headers */
				}
				header.trim();
				Logger("Recv:" + header);
				/* authenticate against a proxy host */
				if (header.toLowerCase().startsWith("proxy-authenticate")) {
					auth_method = header.substring(header.indexOf(":") + 6).trim();
					Logger("auth_method: " + auth_method);
				}
			}

			String strSecond = ntlm.getResponseFor(auth_method, m_username,
					m_password, host_name, m_domain);
			/* authenticate against a proxy host */
			connect = "CONNECT " + m_spcompleteaddress + " HTTP/1.0\r\n";

			String keepalive = "Proxy-Connection: Keep-Alive\r\n\r\n";
			String AuthorizationStr = "Proxy-Authorization: NTLM " + strSecond
					+ "\r\n\r\n";

			/* send the CONNECT call */
			/* since we use 1-byte chars */
			buff_out.write(str2bytes(connect), 0, connect.length());
			buff_out.write(str2bytes(AuthorizationStr), 0,
					AuthorizationStr.length());
			/* since we use 1-byte chars */
			buff_out.write(str2bytes(keepalive), 0, keepalive.length());
			buff_out.flush(); /* no more to write */

			/* read the response */
			// String header;
			header = "";
			while ((header = reader.readLine()) != null) {
				if (header.length() == 0) {
					break; /* end of headers */
				}
				header.trim();
				Logger("OK! Recv:" + header);
				if (header.toUpperCase().startsWith("HTTP")) {
					if (header.indexOf("200") > -1) {
						// connection established
						status = 200;
					} else if (header.indexOf("407") > -1
							|| header.indexOf("401") > -1) {
						// we need to authenticate
						status = 407;
					} else {
						// bad HTTP status code
						status = 500;
					}
				}
			}
			
			Logger(String.valueOf(status));
			// don't close our socket if we succeeded in the inner loop!
			if (status != 200) {
				sock.close();
				sock = null;
				// well, these obviously didn't work
				m_username = null;
				m_password = null;
				m_domain = null;
			} 
		} // outter while !200 OK
			
			return sock;
		} catch (Exception e) {
			change_state(ST_FAILED);
			m_errcode = ERR_NEGNTLM_EXCEPTION;
			m_errstr = e.getMessage();
			Logger("Exception when negotiation:" + e.getMessage());
			return null;
		}
	}

	private Socket negotiate_basic(String host, int port) {
		try {
			String header_tokens, auth_method;
			int status = 0;
			int morebytes = 0;
			int tries = 0;

			Socket sock = null;
			auth_method = new String("");
			while (status != 200) {
				tries++;
				if (tries > 3) {					
					change_state(ST_FAILED);
					Logger("negotiate_basic, not 200.");
					m_errcode = ERR_NEGBASIC_RESP_ERR;
					m_errstr = "Negotiate_basic failed.";
					return null;
				}

				// try to reuse existing credentials first
				if (m_username == null || m_password == null) {
					CredentialsDlg dlg = new CredentialsDlg(m_proxyhost,
							m_proxyport, BASIC_PROXY, lang);
					dlg.setTitle("BASIC PROXY");				

					if (dlg.showDialog() == dlg.Result_OK) {
						m_username = dlg.getUserName();
						m_password = dlg.getPasswd();
					} else {
						Logger("negotiate_basic, use credentials fail.");
						change_state(ST_FAILED);
						m_errcode = ERR_NEGBASIC_CREDENTIAL_FAIL;
						m_errstr = "Negotiate_basic, use credentials fail..";
						return null;
					}
				}

				sock = new Socket(host, port);
				morebytes = 0;
				InputStream sp_in = sock.getInputStream();
				OutputStream sp_out = sock.getOutputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(sp_in));
				BufferedOutputStream buff_out = new BufferedOutputStream(
						sp_out, 1460);

				// Basic auth info is base64(concat(username, ':', password))
				byte[] auth_info = str2bytes(m_username + ":" + m_password);
				char[] auth_64 = Base64.encode(auth_info);

				// authenticate against a proxy host
				String connect = "CONNECT " + m_spcompleteaddress
						+ " HTTP/1.0\r\n";
				String keepalive = "Proxy-Connection: Keep-Alive\r\n";
				String basic_auth = "Proxy-Authorization: Basic ";

				// send the CONNECT call
				buff_out.write(str2bytes(connect), 0, connect.length());
				buff_out.write(str2bytes(keepalive), 0, keepalive.length());
				buff_out.write(str2bytes(basic_auth), 0, basic_auth.length());

				// kind of hackish, but we need to get the char[] into a byte[]
				auth_info = str2bytes(new String(auth_64));
				buff_out.write(auth_info, 0, auth_info.length);
				buff_out.write(str2bytes("\r\n\r\n"), 0, 4);
				buff_out.flush(); // no more to write

				// read and parse the response headers
				header_tokens = parse_response_headers(reader);
				status = get_response_code(header_tokens);
				morebytes = get_content_length(header_tokens);
				auth_method = get_auth_method(header_tokens);
				if (morebytes > 0) {
					// flush the socket buffer
					reader.skip((long) morebytes);
				}

				// don't close our socket if we succeeded in the inner loop!
				if (status != 200) {
					sock.close();
					sock = null;
					// well, these obviously didn't work
					m_username = null;
					m_password = null;
				} else {
					m_doingbasic = false; // authenticated
				}
			} // outter while !200 OK

			return sock; // connected
		} catch (Exception e) {
			change_state(ST_FAILED);
			m_errcode = ERR_NEGBASIC_EXCEPTION;
			m_errstr = e.getMessage();
			Logger("Exception when negotiation:" + e.getMessage());
			return null;
		}
	}

	private void get_pacproxy_url() throws Exception {	
		String strURL = "https://" + m_spcompleteaddress;
		String strRet = null;
		String response;
		String pac_url = null;	
		String enable = null;
		
		PacProxySelector pacproxy = null;		
		int ret = 0;

		Process p1 = null;
		try {
			p1 = Runtime.getRuntime().exec(new String[] 
					{"networksetup", "-getautoproxyurl", "Ethernet"});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		boolean done = false;
		while (!done) {
			try {
				ret = p1.waitFor();
				ret = p1.exitValue();
				done = true;
			} catch (Exception notdone) {
				// keep waiting
			}
		}
		
		BufferedReader pin = null;
		pin = new BufferedReader(new InputStreamReader(
				p1.getInputStream()));
		
		while ((response = pin.readLine()) != null) {
			int retval = -1;
			if (response.length() == 0) {
				break; // end of headers
			}		
			retval = response.indexOf("URL: ");
			if (retval != -1 && pac_url == null) {
				pac_url = response.substring(retval+5, response.length());
	
				URL url = null;
				url = new URL(pac_url);
				pacproxy = new PacProxySelector(new InputStreamReader(url.openStream()));

				strRet = pacproxy.select(new URI(strURL))
						.toString();	
			}
			retval = response.indexOf("Enabled: ");
			if (retval != -1) {
				enable = response.substring(retval+9, response.length());
			}			
		}
		pin.close(); // only care about the first line of output
	    
		if (strRet != null && enable != null && enable.compareTo("Yes") == 0) {
			String substr = null;
			ret = strRet.indexOf("HTTP @ ");
			if (ret != -1) {
				substr = strRet.substring(ret+7, strRet.length() - 1);
				if (substr.indexOf('/') != -1) {
					substr = substr.substring(substr.indexOf('/')+1, substr.length());
					if (substr != null && substr.length() != 0) {
						String a[] = substr.split(":");
						m_proxyhost = a[0];
						m_proxyport = Integer.parseInt(a[1]);
					} 
				}
			}		
		}
		return;
	}
	
	private void detect_proxy() {
		m_proxyhost = "";
		m_proxyport = 0;
		try {
			String strRet = null;		
			String strURL = "https://" + m_spcompleteaddress;
			
			Logger("begin to detect proxy, URL is: "+strRet);
			strRet = ProxySelector.getDefault().select(new URI(strURL))
						.toString();
			
			Logger("begin to detect proxy "+strRet);
			int retval = -1;
			String substr = null;
			if (strRet != null && strRet.length() != 0) {
				retval = strRet.indexOf("HTTP @ ");
				if (retval != -1) {
					substr = strRet.substring(retval+7, strRet.length() - 1);
					if (substr.indexOf('/') != -1) {
						substr = substr.substring(substr.indexOf('/')+1, substr.length());
					}
				}
			}
			
			if (retval != -1 && substr != null && substr.length() != 0) {
				String a[] = substr.split(":");
				m_proxyhost = a[0];
				m_proxyport = Integer.parseInt(a[1]);
				Logger("Detect proxy server: "+m_proxyhost+":"+m_proxyport);
			} else {
				if (isMacOS) {
					/* try again about PAC proxy url */
					get_pacproxy_url();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger("Exception in detect proxy:" + e.getMessage());	
			m_proxyhost = "";
			m_proxyport = 0;
		}
		/*
		 * try { com.sun.java.browser.net.ProxyInfo info[] =
		 * com.sun.java.browser.net.ProxyService.getProxyInfo(new URL("https://"
		 * + m_spname + ":" + m_spport + "/")); if (info != null && info.length
		 * > 0) { m_proxyhost = info[0].getHost(); m_proxyport =
		 * info[0].getPort();
		 * 
		 * m_proxyhost = InetAddress.getByName(m_proxyhost). getHostAddress();
		 * 
		 * if (m_proxyhost.equals(m_spaddr) && m_proxyport == m_spport) {
		 * m_proxyhost = ""; m_proxyport = 0; } } } catch (Exception e) { //no
		 * proxy, continue the next step Logger("Exception in detect proxy:" +
		 * e.getMessage()); m_proxyhost = ""; m_proxyport = 0; }
		 */
	}

	private void detect_proxy_auth() {
		m_doingbasic = false;
		m_doingntlm = false;
		String auth_method = "<none>";
		int morebytes = 0;
		int status = 0;

		// this function relies on detect_proxy() to have set m_proxyhost,
		// m_proxyport
		// and for run() to only call it if a proxy was detected
		try {
			Socket sock = new Socket(m_proxyhost, m_proxyport);
			InputStream sp_in = sock.getInputStream();
			OutputStream sp_out = sock.getOutputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					sp_in));
			BufferedOutputStream buff_out = new BufferedOutputStream(sp_out,
					1460);

			// authenticate against a proxy host
			String connect = "CONNECT " + m_spcompleteaddress
					+ " HTTP/1.0\r\n";
			String keepalive = "Proxy-Connection: Keep-Alive\r\n\r\n";			
			// send the CONNECT call
			buff_out.write(str2bytes(connect), 0, connect.length()); // since we use 1-byte chars
			buff_out.write(str2bytes(keepalive), 0, keepalive.length()); // since we use 1-byte chars
			buff_out.flush(); // no more to write

			// read the response
			String header;
			while ((header = reader.readLine()) != null) {
				if (header.length() == 0) {
					break; // end of headers
				}
				header.trim();				
				if (header.toUpperCase().startsWith("HTTP")) {
					if (header.indexOf("200") > -1) {
						// connection established
						status = 200;
					} else if (header.indexOf("407") > -1
							|| header.indexOf("401") > -1) {
						// we need to authenticate
						status = 407;
					} else {
						// bad HTTP status code
						status = 500;
					}
				}
				if (header.indexOf(":") < 1) {
					continue; // not a header of form name: value
				}
				if (header.toLowerCase().startsWith("content-length")) {
					// we should never get this from a proxy, but oh well
					morebytes = Integer.parseInt(header.substring(
							header.indexOf(":") + 1).trim());
				}
				// authenticate against a proxy host
				if (header.toLowerCase().startsWith("proxy-authenticate")) {
					auth_method = header.substring(header.indexOf(":") + 1)
							.trim();
					// find out our authentication option(s) - choose only Basic
					if (auth_method.toLowerCase().startsWith("basic")) {
						m_doingbasic = true;
					}
					if (auth_method.toLowerCase().startsWith("ntlm")) {
						m_doingntlm = true;
					}
				}
			}
			if (morebytes > 0) {
				// flush the socket buffer - not strictly needed
				reader.skip((long) morebytes);
			}

			// close the connection now since we don't need it
			sock.close();
			sock = null;

			// if authentication is required, do it now
			if (status == 407) {
				InfoText.setText(multilingText("info_input_proxy"));
				Logger("detect_proxy_auth 407, need auth.");
				if (!m_doingbasic) {
					if (!m_doingntlm) {
						// unsupported authentication scheme
						change_state(ST_FAILED);
						m_errcode = ERR_DETECTPROXY_NEEDAUTH;
						m_errstr = "unsupported authentication scheme.";
						return;
					} else {
						negotiate_ntlm(m_proxyhost, m_proxyport);
					}
				} else {
					negotiate_basic(m_proxyhost, m_proxyport);
				}
			}
			if (status >= 500) {
				m_errcode = ERR_DETECTPROXY_SERVERERR;
				Logger("detect_proxy_auth more than 500, server error");
				m_errstr = "Server error when detecting proxy.";
				change_state(ST_FAILED); // trigger our error handling case in
											// run()
			}
		} catch (Exception e) {
			// detect no proxy, continue the next step, don't fail
			Logger("detect_proxy_auth exception:" + e.getMessage());
		}
	}
	
	private void send_terminate_singal() {
		byte[] terminate_sig = new byte[4];
		byte[] temp = null;
		int offset = 0, i = 0;
		
		temp = int2bytes(TERMINATE, 4);
		for (i = 0; i < temp.length; i++) {
			terminate_sig[offset++] = temp[i];
		}		
		try {
			m_stdout.write(terminate_sig, 0, terminate_sig.length);
			m_stdout.flush();
		} catch (Exception e) {
			Logger("send error:" + e.getMessage());
		}
	}
	
	private int receive_terminate_singal_response() {
		int response = 0;
		
		try {
			int surplus = m_stdin.available();
			m_stdin.skip(surplus - 4);
			response = m_stdin.readInt();
		}catch (Exception e) {
			Logger("read terminate error" + e.getLocalizedMessage() + e.getMessage());
		}	
		return response;
	}
	
	public void terminate() {
		InfoText.setText(multilingText("info_disconnecting"));		
		if (m_state == ST_DISCONNECTED || m_state == ST_FAILED) {
			Logger("It has been disconnected already before this terminate operation");
			InfoText.setText(multilingText("info_disconnected"));	
			return;
		}
		// terminate the vpnc process
		int response = 0;
		send_terminate_singal();			
		response = receive_terminate_singal_response();				
		Logger("read terminate response   " + Integer.toHexString(response));			
		if ((response != TERMINATE) && (response != 0)) {
			change_state(ST_CONNECTED);
			InfoText.setText(multilingText("info_connected"));
			return;
		}			
		change_state(ST_DISCONNECTED);	
		InfoText.setText(multilingText("info_disconnected"));		
		set_text(clientipText, multilingText("assigned_ip"));
		set_text(inputbytesText, multilingText("byte_recv"));
		set_text(outputbytesText, multilingText("byte_sent"));		
	}
	
	public int terminate_by_loader() {

		InfoText.setText(multilingText("info_disconnecting"));		
		
		if (m_state == ST_DISCONNECTED || m_state != ST_CONNECTED) {
			Logger("It has been disconnected already before this terminate operation");
			InfoText.setText(multilingText("info_disconnected"));
			return 0;
		}
		// terminate the vpnc process
		try {
			int ret = -1;
			boolean done = false;			
			Process p1 = null;
			
			try {
				DataInputStream exeresult = null;
			/*	byte[] result = new byte[200];
				int ind = 0;				
				byte[] pid = new byte[200];*/
				int result = 0;

				try {
					 p1 = Runtime.getRuntime().exec
							   (new String[] {"/bin/bash",
										"-c",
										default_path+array_loader_bin +" stop"});
					
					Logger("terminate vpn by loader is called");
					while (!done) {
						try {
							ret = p1.waitFor();
							ret = p1.exitValue();
							done = true;
						} catch (Exception notdone) {
							// keep waiting
						}
					}
				} catch (Exception e) {
					Logger("failed execute"+e.getMessage());
				}
				
				 exeresult = new DataInputStream(p1.getInputStream());
				 try {
					 result = exeresult.readInt();
					 exeresult.close();
					 Logger("loader return value"+result);
				 } catch (Exception e) {
					 Logger("failed read"+e.getMessage());
				 }
				 
				 if (result == 1) {
					 Logger("vpc is stoped");
				 } else {
					 Logger("vpc is NOT stopped");
				 }
				 
		/*		 String resultptr = new String(result);
				 try {
					 ind = resultptr.indexOf(' ');
				 } catch (Exception e) {
					 Logger("failed index"+e.getMessage());
				 }
				 
				 Logger("get vpn process id "+resultptr+" "+resultptr.substring(0, ind));
				 */
				/* p1 = rt.exec
					       (new String[] { "/bin/bash", "-c", "kill -9 "+ resultptr.substring(0, ind)});
*/
			} catch (Exception e) {
				Logger("Failed to execute kill vpn process");
				InfoText.setText(multilingText("info_connected"));
				return 0;
			}
			
			String status = GetConnStat();
			if (status.equals("Connected")) {
				change_state(ST_CONNECTED);
				Logger("Failed to stop vpn");
				InfoText.setText(multilingText("info_connected"));

				return 0;
			}	
			
			change_state(ST_DISCONNECTED);
	/*		if (m_vpnprocess != null) {
				m_vpnprocess.destroy();
				m_vpnprocess = null;
			}
*/
			InfoText.setText(multilingText("info_disconnected"));
	
			// Refresh page, since the infotext may not be repainted on firefox
	/*		if (m_navtype.equals("Firefox")) {// Firefox
				Thread.sleep(500);
				getAppletContext().showDocument(getDocumentBase());
			}			
		*/	
			m_stdin = null;
			m_stdout = null;
			if (m_timer_run != null) {
				m_timer_run.cancel();
				m_timer_run = null;
			}

			if (m_timetask_run != null) {
				m_timetask_run = null;
			}

			if (m_timer_uninst != null) {
				m_timer_uninst.cancel();
				m_timer_uninst = null;
			}

			if (m_timetask_uninst != null) {
				m_timetask_uninst = null;
			}

	/*		if (m_ptask != null) {
				m_ptask.StopTimer();
				m_ptask = null;
			}

			if (m_maindlg != null) {
				m_maindlg.setBtnClose(true);
				m_maindlg.CloseDlg();
				m_maindlg = null;
			}
*/
		} catch (Exception ignored) {
			Logger("disconnect error:" + ignored.getMessage());
			return 0;
		}
		return 1;
	}

	private void uninstall_client() {
	//	change_state(ST_START);

		try {
			m_instpath = default_path;
			String array_loader_file = m_instpath + array_loader_bin;

			Logger("Uninstall excutable: " + array_loader_file);

			File array_loader = new File(array_loader_file);
			if (!array_loader.exists()) {
				Logger("Unable to find array_loader");
				// throw new Exception("Unable to find macvpn_loader");
			}

			Runtime rt = Runtime.getRuntime();
			Process p1 = rt.exec(new String[] { array_loader_file, "uninstall",
					m_instpath });

			int ret = -1;
			boolean done = false;
			while (!done) {
				try {
					ret = p1.waitFor();
					ret = p1.exitValue();
					done = true;
				} catch (Exception notdone) {
					// keep waiting
				}
			}

			if (ret != 0) {
				File array_vpnc = new File(m_instpath + array_vpnc_bin);
				if (array_loader.exists() || array_vpnc.exists()) {
					Logger("Uninstall failed");
					throw new Exception(
							"Uninstall failed, maybe due to no priviledges.");
				}
			}

			Logger("The SSL VPN Client was successfully uninstalled.");
			m_errcode = ERR_NO_ERR;
		} catch (Exception e) {
			Logger("Unable to remove existing installation:" + e.getMessage());
			m_errcode = ERR_UNINSTALL_EXCEPTION;
			m_errstr = "Uninstall exception: " + e.getMessage();
		}
	}

	private void downloadfile(String downloadfilename) {
		java.net.Proxy proxy = null;
		String fileurl = "https://" + m_spcompleteaddress + "/prx/000/http/localhost/"
				+ downloadfilename;		
		HttpsURLConnection conn = null;
		try {
			if (m_proxyhost.length() > 0 && m_proxyport > 0) {
				java.net.InetAddress addr = InetAddress.getByName(m_proxyhost);
				InetSocketAddress sa = new InetSocketAddress(addr, m_proxyport);

				proxy = new java.net.Proxy(Proxy.Type.HTTP, sa);
			/*	Authenticator.setDefault(new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						if (getRequestorType() == RequestorType.PROXY) {
							return new PasswordAuthentication(m_username,
									m_password.toCharArray());
						} else {
							return null;
						}
					}
				});*/
			}

			URL url = new URL(fileurl);
			if (m_proxyhost.length() > 0 && m_proxyport > 0) {
				conn = (HttpsURLConnection) url.openConnection(proxy);
			} else {
				conn = (HttpsURLConnection) url.openConnection();
			}
			java.io.InputStream fileIn = conn.getInputStream();
			String filename = tmp_install_path + downloadfilename;
			java.io.File file = new java.io.File(filename);
			java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
			byte[] value = new byte[1024];
			int len = 0;
			while ((len = fileIn.read(value)) > 0) {
				fos.write(value, 0, len);
			}
			fos.close();
			fileIn.close();
		} catch (Exception e) {
			Logger("download file " + downloadfilename + "failed");
			e.printStackTrace();
		}
	}

	private void downloadfile(String downloadfilename, String instfilename) {
		java.net.Proxy proxy = null;
		String fileurl = "https://" + m_spcompleteaddress + "/prx/000/http/localhost/"
				+ downloadfilename;
		Logger("being to down " + fileurl);
		HttpsURLConnection conn = null;
		try {
			if (m_proxyhost.length() > 0 && m_proxyport > 0) {
				java.net.InetAddress addr = InetAddress.getByName(m_proxyhost);
				InetSocketAddress sa = new InetSocketAddress(addr, m_proxyport);

				proxy = new java.net.Proxy(Proxy.Type.HTTP, sa);
		/*		Authenticator.setDefault(new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						if (getRequestorType() == RequestorType.PROXY) {
							return new PasswordAuthentication(m_username,
									m_password.toCharArray());
						} else {
							return null;
						}
					}
				});*/
			}

			URL url = new URL(fileurl);
			if (m_proxyhost.length() > 0 && m_proxyport > 0) {
				conn = (HttpsURLConnection) url.openConnection(proxy);
			} else {
				Logger("being to openconnection ");	
				conn = (HttpsURLConnection) url.openConnection();
			}
			java.io.InputStream fileIn = conn.getInputStream();
			String filename = tmp_install_path + instfilename;
			java.io.File file = new java.io.File(filename);
			java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
			byte[] value = new byte[1024];
			int len = 0;
			Logger("being to write file ");	
			while ((len = fileIn.read(value)) > 0) {
				fos.write(value, 0, len);
			}
			fos.close();
			fileIn.close();
		} catch (Exception e) {
			Logger("download file " + downloadfilename + "failed");
			e.printStackTrace();
		}
	}

	private void mv_chmod_downfiles_mac()
	{
		Runtime rt = null;
		Process p1 = null;
		boolean done = false;
		int ret = -1;
		//after download files, we should chmod binary's privileges
		rt = Runtime.getRuntime();

		try {
			p1 = rt.exec(new String[] {
					"/bin/bash",
					"-c",
					"cd "
							+ tmp_install_path
							+ "; tar xovzf an_tuninstaller_intel.tgz mac_chmod.sh" });
			Thread.sleep(1000);
			
			done = false;
			while (!done) {
				try {
					ret = p1.waitFor();
					ret = p1.exitValue();
					done = true;
				} catch (Exception notdone) {
					// keep waiting
					Logger("Exception on tar chmod script from tuninstall package");
				}
			}	
			
			Logger("Being to run chmod script");
			chmodfile(tmp_install_path+"mac_chmod.sh");
			
			p1 = rt.exec(new String[] {
					"/bin/bash",
					"-c",
					"cd " + tmp_install_path
							+ "; /usr/bin/osascript mac_chmod.sh" });

			done = false;
			while (!done) {
				try {
					ret = p1.waitFor();
					ret = p1.exitValue();
					done = true;
				} catch (Exception notdone) {
					// keep waiting
					Logger("Exception on chmod");
				}
			}			

			p1 = rt.exec(new String[] {
					"/bin/bash",
					"-c",
					"rm -rf " + tmp_install_path + "mac_chmod.sh" });
			done = false;
			while (!done) {
				try {
					ret = p1.waitFor();
					ret = p1.exitValue();
					done = true;
				} catch (Exception notdone) {
					// keep waiting
					Logger("Exception on remove chmod");
				}
			}	
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
	}
	
	private void mv_chmod_downfiles_linux()
	{
		Process p1 = null;
		boolean done = false;		
		
		try {
			
			Logger("run linux install script ...");
			/* 
			 * if you want to use gksu, you must make sure, linux_chmod.sh
			 * has right of execution
			 */
			chmodfile(tmp_install_path+"linux_chmod.sh");
			p1 = Runtime.getRuntime().exec(new String[] 
				{"gksu", tmp_install_path + "linux_chmod.sh" });
			
			while (!done) {
				try {
					p1.waitFor(); /* blocking call */
					p1.exitValue();
					done = true;
				} catch (Exception notdone) {
					// keep waiting
				}
			}
			
		/*	if (ret != 0) {
				throw new Exception("Insufficient priviledges"+ret);
			}
			*/
			p1 = Runtime.getRuntime().exec(new String[] {
					"/bin/bash",
					"-c",
					"rm -rf " + tmp_install_path + "linux_chmod.sh" });

			done = false;
			while (!done) {
				try {
					p1.waitFor();
					p1.exitValue();
					done = true;
				} catch (Exception notdone) {
					// keep waiting
					Logger("Exception on remove chmod");
				}
			}	
		} catch (Exception e) {
			Logger("install linux client failed" + e.getMessage());
		//	m_errcode = ERR_INSTALL_CHOWN_ROOT_EXCEPTION;
			InfoText.setText(multilingText("info_priviledge_failed") + e.getMessage());
			change_state(ST_FAILED);
			return;
		}		
	}
	
	private void install_client() {
		Socket sock = null;
		Runtime rt = null;
		Process p1 = null;
		boolean done = false;
		int ret = -1;
		// establish an SSL connection to the SP
		try {
			// open a connection to the SP on the control port
			if (m_proxyhost  != null && m_proxyhost.length() != 0 && m_proxyport != 0 && m_doingntlm) {
				/* we need this sock, maybe for following's dis ssl client certificate */
				sock = negotiate_ntlm(m_proxyhost, m_proxyport);
			} else {
				sock = init_control_connection();
			}
			
			if (sock == null) {
				Logger("sock null!");
				if (m_doingbasic || m_doingntlm) {
					Logger("Need credentials.");
					return; // need credentials; login will restart us
				}

				Logger("Unable to authenticate with the Proxy");
				if (m_errcode == ERR_NO_ERR) {
					m_errcode = ERR_AUTHPROXY_FAIL;
					m_errstr = "Unable to authenticate with the Proxy.";
					return;
				} // else, maybe be set in init_control_connection()
				change_state(ST_FAILED);
				throw new Exception(
						"Unable to authenticate with the Proxy.");
			}
		} catch (Exception e) {
			Logger("Can't connect to SP by SSL at this time:" + e.getMessage());
			e.printStackTrace();
			change_state(ST_FAILED);
			if (m_errcode == ERR_NO_ERR) { // maybe be set in
											// init_control_connection()
				m_errcode = ERR_CONNECT_EXCEPTION;
				m_errstr = "Connect to SP exception: " + e.getMessage();
			}
			return;
		}
		try {
			sock.setSoLinger(false, 0); // don't linger on close
		} catch (Exception ee) {
			Logger("Ignore exception:" + ee.getMessage());
			ee.printStackTrace();
		}

		ssl_disable_client_cert(sock);

		try {
			sock.close();
			sock = null;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		InfoText.setText(multilingText("info_download_client"));
		if (isMacOS) {
			downloadfile(mac_array_vpnc_bin, array_vpnc_bin);
			downloadfile(mac_array_loader_bin, array_loader_bin);
			downloadfile(array_ncutil104_bin);
			downloadfile(array_ncutil105_bin);
			downloadfile(array_tun_install_tar);
		/*	chmodfile(tmp_install_path + array_vpnc_bin);
			chmodfile(tmp_install_path + array_loader_bin);
			chmodfile(tmp_install_path + array_ncutil104_bin);
			chmodfile(tmp_install_path + array_ncutil105_bin);*/
			/* after download files move and chmod them */
			Logger("run macos install script ...");
			mv_chmod_downfiles_mac();
		} else {
			downloadfile(linux_array_loader_bin, array_loader_bin);
			downloadfile(linux_array_vpnc_bin, array_vpnc_bin);
			downloadfile("linux_chmod.sh");
			downloadfile("changeProxy.html");
		//	chmodfile(tmp_install_path + linux_install_script);
			/* after download files move and chmod them */
			mv_chmod_downfiles_linux();
		}
	
	}

	private void update_client() {
		Socket sock = null;

		// establish an SSL connection to the SP
		try {
			if (sock == null) {
				sock = init_control_connection();
				if (sock == null) {
					Logger("init connection fail.");
					if (m_doingbasic || m_doingntlm) {
						Logger("Update client, need credentials.");
						return; // need credentials; login will restart us
					}
					if (m_errcode == ERR_NO_ERR) {
						m_errcode = ERR_UPDATE_AUTHPROXY_FAIL;
						m_errstr = "Unable to authenticate with the Proxy.";
					}
					change_state(ST_FAILED);
					throw new Exception(
							"Unable to authenticate with the Proxy.");
				}
			}

			sock.setSoLinger(false, 0); // don't linger on close
			sock.setTcpNoDelay(true);// no nagle

			ssl_disable_client_cert(sock);
			sock.close();
			sock = null;
		} catch (Exception e) {
			if (m_errcode == ERR_NO_ERR) { // maybe be set in
											// init_control_connection()
				m_errcode = ERR_UPDATE_EXCEPTION;
				m_errstr = "Update exception: " + e.getMessage();
			}
			change_state(ST_FAILED);
			Logger("exception in update client: " + e.getMessage());
			return;
		}

		try {

			InfoText.setText(multilingText("info_update_client"));
			if (isMacOS) {
				downloadfile(array_ncutil104_bin);
				downloadfile(array_ncutil105_bin);
				downloadfile(mac_array_vpnc_bin, array_vpnc_bin);
				downloadfile(mac_array_loader_bin, array_loader_bin);
				chmodfile(tmp_install_path + array_ncutil104_bin);
				chmodfile(tmp_install_path + array_ncutil105_bin);
			} else {
				downloadfile(linux_array_loader_bin, array_loader_bin);
				downloadfile(linux_array_vpnc_bin, array_vpnc_bin);
			}

		} catch (Exception e) {
			if (m_errcode == ERR_NO_ERR) { // maybe download_file exception
				m_errcode = ERR_UPDATE_DOWN_EXCEPTION;
				m_errstr = "Update exception:" + e.getMessage();
			}
			change_state(ST_FAILED);
			Logger("Update exception : " + e.getMessage());
			return;
		}

		// call the loader to move the temp file into its final place and set
		// needed flags
		// mark for read later
		if (!move_file("vpnc", tmp_install_path + array_vpnc_bin)
				|| !move_file("loader", tmp_install_path + array_loader_bin)) {
			Logger("Upgrade exception");
			change_state(ST_FAILED);
			return;
		}
	}

	private boolean download_file(String filename,
			BufferedOutputStream file_out, BufferedInputStream buff_in,
			BufferedOutputStream buff_out) throws IOException {
		int filesize, len;
		byte[] buff = new byte[1024];
		// send the request to download the file
		Logger("download request, file:" + filename);
		String request = "GET /prx/000/http/localhost/" + filename
				+ " HTTP/1.0\r\n" + "Connection: Keep-Alive\r\n"
				+ "Cookie: ANsession=" + m_sessid + "\r\n\r\n";
		if (buff_out == null) {
			Logger("buffout is nulllll");
		}
		buff_out.write(str2bytes(request), 0, request.length());
		buff_out.flush();
		Logger("send download request finish.");
		// read and parse the response headers
		filesize = parse_byte_headers(buff_in);
		Logger("parse_byte_headers finish, filesize:" + filesize);
		// download and write the loader
		while (filesize > 0) {
			len = buff_in.read(buff, 0, buff.length);
			if (len == -1) {
				file_out.flush();
				file_out.close();
				// m_errcode = 41;
				return false;
			}
			file_out.write(buff, 0, len);
			filesize -= len;
		}
		return true;
	}

	private boolean move_file(String file_type, String temp_file) {
		try {
			Runtime rt = Runtime.getRuntime();
			Process p1 = null;
			boolean done = false;
			int ret = -1;
			p1 = rt.exec(new String[] { m_instpath + array_loader_bin,
					"upgrade", file_type, temp_file, m_instpath });
			while (!done) {
				try {
					ret = p1.waitFor();
					ret = p1.exitValue();
					done = true;
				} catch (Exception notdone) {
				}
			}

			if (ret != 0) {
				// m_errcode = 42; //not quit
				throw new Exception("Upgrade failed!");
			}
		} catch (Exception e) {
			// m_errcode = 43; //not quit
			Logger("exception(43): " + e.getMessage());
			return false;
		}
		return true;
	}

	private Socket ssl_disable_client_cert(Socket sock) {
		try {
			netscape.security.PrivilegeManager
					.enablePrivilege("TerminalEmulator");
		} catch (Exception ignored) {
			Logger("Error in ssl_disable_client_cert!");
		}

		try {
			long time = (new Date()).getTime();
			int unixtime = (int) (time / 1000); // convert miliseconds to
												// seconds
			byte[] gmt_unix_time = int2bytes(unixtime, 4);

			// construct the special header for disabling client cert auth
			byte[] tls_header = new byte[82];
			int i = 0, j;

			// TLSv1 Record Layer
			tls_header[i++] = 0x16; // Handshake
			tls_header[i++] = 0x03;
			tls_header[i++] = 0x01; // major, minor: TLSv1
			tls_header[i++] = 0x00;
			tls_header[i++] = 0x4D; // length: 77 bytes

			// Handshake Protocol
			tls_header[i++] = 0x01; // Client Hello
			tls_header[i++] = 0x00;
			tls_header[i++] = 0x00;
			tls_header[i++] = 0x49; // length: 73 bytes
			tls_header[i++] = 0x03;
			tls_header[i++] = 0x01; // major, minor: TLSv1

			for (j = 0; j < 4; j++) {
				tls_header[i++] = gmt_unix_time[j]; // GMT Unix Time
			}

			for (j = 0; j < 28; j++) {
				tls_header[i++] = (byte) (j + 1); // 28 bytes of random
			}

			tls_header[i++] = 0x20; // session id length: 32 bytes

			for (j = 0; j < 32; j++) {
				tls_header[i++] = (byte) 0xFF; // set all FF as special
			}
			tls_header[i++] = 0x00;
			tls_header[i++] = 0x02; // cipher suites length
			tls_header[i++] = 0x00;
			tls_header[i++] = 0x04; // TLS_RSA_WITH_RC4_128_MD5
			tls_header[i++] = 0x01; // compression methods length
			tls_header[i++] = 0x00; // compression method null
			OutputStream sp_out = sock.getOutputStream();
			BufferedOutputStream buff_out = new BufferedOutputStream(sp_out,
					1460);
			buff_out.write(tls_header, 0, tls_header.length);
			buff_out.flush(); // no more to write
		} catch (Exception e) {
			Logger("Error initializing SSL tunnel: "
					+ e.getMessage() + "\n");
			try {
				sock.close();
			} catch (Exception ignored) {
				Logger("Error in close socket!");
			}

			sock = null;
		}
		return sock;
	}

	private Socket init_control_connection() {
		String header_tokens;
		String auth_method = "<none>";
		int morebytes = 0;
		int status = 0;
		Socket sock = null;

		try {
			if (m_proxyhost.length() > 0 && m_proxyport > 0) {
				sock = new Socket(m_proxyhost, m_proxyport);
			} else {
				// sock = new Socket(InetAddress.getByName(m_spname), m_spport);
				// for JRE1.5
				sock = new Socket(InetAddress.getByName(m_spaddr),m_spport);
				return sock;
			}
		} catch (Exception e) {
			Logger("Create socket exception: " + e.getMessage());
			m_errcode = ERR_CREATESOCK_EXCEPTION;
			m_errstr = "Create sock exception:" + e.getMessage();
			return null;
		}

		try {
			InputStream sp_in = sock.getInputStream();
			OutputStream sp_out = sock.getOutputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					sp_in));
			BufferedOutputStream buff_out = new BufferedOutputStream(sp_out,
					1460);
			// authenticate against a proxy host
			String connect = "CONNECT " + m_spcompleteaddress
					+ " HTTP/1.0\r\n";
			String keepalive = "Proxy-Connection: Keep-Alive\r\n\r\n";
			// send the CONNECT call
			buff_out.write(str2bytes(connect), 0, connect.length()); // since we
																		// use
																		// 1-byte
																		// chars
			buff_out.write(str2bytes(keepalive), 0, keepalive.length()); // since
																			// we
																			// use
																			// 1-byte
																			// chars
			buff_out.flush(); // no more to write

			// read and parse the response headers
			header_tokens = parse_response_headers(reader);
			status = get_response_code(header_tokens);
			morebytes = get_content_length(header_tokens);
			auth_method = get_auth_method(header_tokens);

			if (morebytes > 0) {
				// flush the socket buffer
				reader.skip((long) morebytes);
			}

			if (status != 200) {
				sock.close();
				Logger("status not 200,sock null");
				sock = null;
			}
			sp_out.flush();
			sp_out.close();
			sp_in.close();

			if (status == 407) {
				// find out our authentication option(s) - choose Basic
				if (auth_method.toLowerCase().startsWith("basic")) {
					m_doingbasic = true;
					sock = negotiate_basic(m_proxyhost, m_proxyport);
				} else {
					// unsupported authentication scheme
				}
			}
		} catch (Exception e) {
			if (m_errcode == ERR_NO_ERR) { // maybe parse_response_headers
											// exception, errcode is set
				m_errcode = ERR_CONNECTSOCK_EXCEPTION;
				m_errstr = "Connect sock exception:" + e.getMessage();
			}
			Logger("Connect socket exception: " + e.getMessage());
			sock = null;
		}

		return sock;
	}

	// current token format: code,length,auth
	private int get_response_code(String header_tokens) {
		return Integer.parseInt(header_tokens.substring(0,
				header_tokens.indexOf(",")));
	}

	// current token format: code,length,auth
	private int get_content_length(String header_tokens) {
		int first_delimiter = header_tokens.indexOf(",");
		return Integer.parseInt(header_tokens.substring(first_delimiter + 1,
				header_tokens.indexOf(",", first_delimiter + 1)));
	}

	// current token format: code,length,auth
	private String get_auth_method(String header_tokens) {
		return header_tokens.substring(header_tokens.lastIndexOf(",") + 1);
	}

	// read using a BufferedInputStream and manually parse for binary content
	// return the number of bytes indicated by content-length or -1 for error
	private int parse_byte_headers(BufferedInputStream buff_in)
			throws IOException {
		int content_length;
		byte[] buff = new byte[64];
		int state; // 0 = nothing, 1 = \r, 2 = \r\n, 3 = \r\n\r, 4 = \r\n\r\n
		int b, offset;
		String header;

		content_length = -1;
		state = 0;
		while (state != 4) {
			offset = 0;
			do {
				b = buff_in.read();
				if (b == -1) {
					m_errcode = ERR_PARSEHEADER_PREMATURE;
					m_errstr = "Parse headers, premature end of stream";
					throw new IOException("Premature end of stream");
				}

				// store it if we have room
				if (offset < buff.length) {
					buff[offset] = (byte) b;
					offset++;
				} // else discard it since we don't care

				if ((state == 0 || state == 2) && b == 0x0d) {
					state++; // \r
				} else if ((state == 1 || state == 3) && b == 0x0a) {
					state++; // \n
				} else {
					state = 0; // start over
				}
			} while (state != 2 && state != 4);

			// reached end of a header - parse it
			header = bytes2str(buff, 0, offset);
			header.trim();
			if (header.toUpperCase().startsWith("HTTP")
					&& header.indexOf("200") == -1) {
				m_errcode = ERR_PARSEHEADER_REQUESTERR;
				m_errstr = "Parse header, request error.";
				throw new IOException("Parse header, request error no 200.");
			}

			if (header.indexOf(":") < 1) {
				continue; // not a header of form name: value
			}

			if (header.toLowerCase().startsWith("content-length")) {
				content_length = Integer.parseInt(header.substring(
						header.indexOf(":") + 1).trim());
			}
		}

		if (content_length < 1) {
			m_errcode = ERR_PARSEHEADER_INVALIDHDR;
			m_errstr = "Parse header,invalid Content-Length.";
			throw new IOException("Invalid Content-Length.");
		}

		return content_length;
	}

	// read using a BufferedReader to parse the http response headers easily
	// return String "status,content-length,authentication"
	private String parse_response_headers(BufferedReader reader)
			throws IOException {
		String header;
		int status, morebytes;
		String auth_method;
		boolean got_auth;

		status = 500;
		morebytes = 0;
		auth_method = "";
		got_auth = false;
		// read the response
		while ((header = reader.readLine()) != null) {
			if (header.length() == 0) {
				break; // end of headers
			}
			header.trim();
			if (header.toUpperCase().startsWith("HTTP")) {
				if (header.indexOf("200") > -1) {
					// connection established
					status = 200;
				} else if (header.indexOf("407") > -1
						|| header.indexOf("401") > -1) {
					// we need to authenticate
					status = 407;
				} else {
					// bad HTTP status code
					status = 500;
				}
			}
			if (header.indexOf(":") < 1) {
				continue; // not a header of form name: value
			}

			if (header.toLowerCase().startsWith("content-length")) {
				morebytes = Integer.parseInt(header.substring(
						header.indexOf(":") + 1).trim());
			}

			// authenticate against a proxy host
			if (!got_auth
					&& header.toLowerCase().startsWith("proxy-authenticate")) {
				auth_method = header.substring(header.indexOf(":") + 1).trim();
				if (!auth_method.toLowerCase().startsWith("basic")) {
					auth_method = ""; // only accept Basic
				} else {
					got_auth = true; // take Basic
				}
			}
		}
		// tokenize and return our results
		return new String("" + status + "," + morebytes + "," + auth_method);
	}

	public boolean needInstall(String cur, String loc) {
		      boolean bUpgrade = false;
		      int numSer=-1,numLoc=-1;
		      
		      if (cur == null || loc == null) {       
		         bUpgrade = true;
		         return bUpgrade;
		      } 
		      String[] remoteVersion=cur.trim().split("_");
		      String[] localVersion=loc.trim().split("_");
		      for (int i=0; i<remoteVersion.length;i++) {
		         if((remoteVersion[i]!=null) && (localVersion[i]!=null)) {
		            numSer=Integer.parseInt(remoteVersion[i]);
		            numLoc=Integer.parseInt(localVersion[i]);
		         }
		         System.out.println(numSer+":"+numLoc);
		         if (numSer > numLoc) {
		            bUpgrade = true;
		            break;
		         } else if (numSer < numLoc) {
		            bUpgrade = false;
		            break;
		         } else {
		            continue;
		         }
		      }         
		      return bUpgrade;
		    }

	/* include install or update */
	private boolean prepare_start() {
		Runtime rt = null;
		Process p1 = null;
		BufferedReader pin = null;
		boolean done, ok;
		int ret = 0;

		Logger("Now checking update...");

		int state = 10;
		/*
		 * 10 for detect proxy 0 for detect whether it has client 1 for install
		 * 2 for comparing version 3 for update -1 for failed 999 for tun/tap
		 * driver 1000 for success
		 */
		ok = false;
		while (!ok) {
			switch (state) {
			case 10:
				Logger("Detect proxy...");

				detect_proxy();
				if (m_proxyhost.length() > 0 && m_proxyport != 0) {
					detect_proxy_auth();
					Logger("Proxy is: " + m_proxyhost + ":" + m_proxyport);
				} else {
					Logger("No proxy detected.");
				}

				if (m_state == ST_FAILED) {
					Logger("Failed to detect the Proxy.");
					state = -1;
				} else {
					state = 0;
				}

				break;

			case 0: // start detecting whether it has client
				Logger("Detect if VPN client exists.");
				m_errcode = ERR_NO_ERR;

				InfoText.setText(multilingText("info_client_exist"));
				try {
					
						// not found - try the default path
						File array_vpnc = new File(default_path
								+ array_vpnc_bin);
						File array_loader = new File(default_path
								+ array_loader_bin);
						File ncutil04 = new File(default_path
								+ array_ncutil104_bin);
						File ncutil05 = new File(default_path
								+ array_ncutil105_bin);			
						File linux_setproxy = new File(default_path
								+ "changeProxy.html");
											
						state = 1;
						if (array_vpnc.exists() && array_loader.exists()) {
							if ((!isMacOS && linux_setproxy.exists()) || (ncutil04.exists() && ncutil05.exists())) {
								Logger("There is a client in default path");
								state = 2;
							}					
						}

				} catch (Exception e) {
					Logger("Exception in detecting client, assuming not installed:"
							+ e.getMessage());
					// m_errcode = 50; //continue the next step
					state = 1;
				}

				break;

			case 1: // install client
				InfoText.setText(multilingText("info_install_client"));
				m_errcode = ERR_NO_ERR; // reset

				try {
					install_client();
				} catch (Exception e) {
					Logger("install client exception." + e.getMessage());
					state = -1;
					break;
				}

				if (m_state == ST_FAILED) {
					Logger("install client failed.");
					state = -1;
					break;
				}

				state = 999;
				Logger("Installation succeeds");

				break;

			case 2: // version matching
				try {
					rt = Runtime.getRuntime();
					p1 = rt.exec(new String[] { m_instpath + array_vpnc_bin,
							"-v" });
					done = false;
					while (!done) {
						try {
							ret = p1.waitFor();
							ret = p1.exitValue();
							done = true;
						} catch (Exception notdone) {
							// keep waiting
						}
					}
					pin = null;
					pin = new BufferedReader(new InputStreamReader(
							p1.getInputStream()));
					String inst_ver = pin.readLine();
					pin.close(); // only care about the first line of output

					Logger("Version is '" + inst_ver
							+ "', Existed version is '" + m_spver + "'");

					if (ret != 0 || inst_ver == null) {
						state = -1;
						m_errcode = ERR_CHECKVERSION_FAIL;
						m_errstr = "Cannot check the version.";
						Logger("Cannot check the version, assuming not installed.");
						// state = 1;
						break;
					}
					if (inst_ver.indexOf("VPNC2_") == -1) {						
						state = 1;
						Logger("current vpnc is sp, need to update");
						InfoText.setText(multilingText("info_client_need_update"));
						break;
					}
					inst_ver = inst_ver.substring(inst_ver.indexOf('_') + 1);
					String spver = m_spver.substring(m_spver.indexOf('_') + 1);
					Logger("Version is '" + inst_ver
							+ "', Existed version is '" + spver + "'");
					//if (inst_ver.trim().compareTo(spver.trim()) != 0) {
					if (needInstall(spver, inst_ver)) {
						state = 1;
						Logger("state=" + state + " need to update");
						InfoText.setText(multilingText("info_client_need_update"));
						break;
					}

					// success, we have this client and run it directly
					state = 999;
				} catch (Exception e) {
					Logger("Exception in checking version, assuming not installed:"
							+ e.getMessage());
					// m_errcode = 51; //continue
					state = 1;
				}
				break;

	/*		case 3: // update
				Logger("update client");
				set_text("System updating...", false);
				m_errcode = ERR_NO_ERR;
				update_client();
				if (m_state == ST_FAILED) {
					state = -1;
					break;
				}

				state = 999;

				break;*/

			case -1: // error happens
				ok = true;				
				change_state(ST_FAILED);
				break;

			case 999: // detect tun driver
				if (isLinux) {// linux not install tap/tun driver
					state = 1000;
					break;
				}
				Logger("Detect if tun/tap driver exists.");
				m_errcode = ERR_NO_ERR;
				InfoText.setText(multilingText("info_check_tun"));
				try {
					String tmppath = "";
					rt = Runtime.getRuntime();
					p1 = rt.exec("find /Library/Extensions -name tun.kext");
					done = false;
					while (!done) {
						try {
							ret = p1.waitFor();
							ret = p1.exitValue();
							done = true;
						} catch (Exception notdone) {
							// keep waiting
						}
					}

		/*			set_text("Now checking if array_vpnc exists ...", false);
					rt = Runtime.getRuntime();
					p1 = rt.exec("find /usr/local/ -name array_vpnc");
					done = false;
					int ret2 = 0;
					while (!done) {
						try {
							ret2 = p1.waitFor();
							ret2 = p1.exitValue();
							done = true;
						} catch (Exception notdone) {
							// keep waiting
						}
					}

					set_text("Now checking if array_loader exists ...", false);
					rt = Runtime.getRuntime();
					p1 = rt.exec("find /usr/local/ -name array_loader");
					done = false;
					int ret3 = 0;
					while (!done) {
						try {
							ret3 = p1.waitFor();
							ret3 = p1.exitValue();
							done = true;
						} catch (Exception notdone) {
							// keep waiting
						}
					}
*/
					// ret = 1;//whether find /Library/Extensions/tun.kext or
					// not, install tun/tap driver.
					pin = new BufferedReader(new InputStreamReader(
							p1.getInputStream()));
					tmppath = pin.readLine();
					pin.close(); // only care about the first line of output

					if (tmppath == null || tmppath.length() == 0
					    || tmppath.indexOf("No such") != -1) 
					{
						Logger("No tun driver file, need install");

						InfoText.setText(multilingText("info_install_tun"));

						try {
							rt = Runtime.getRuntime();

							// unzip the tun tar file
							p1 = rt.exec(new String[] {
									"/bin/bash",
									"-c",
									"cd "
											+ default_path
											+ "; tar zxf an_tuninstaller_intel.tgz" });

							done = false;
							while (!done) {
								try {
									ret = p1.waitFor();
									ret = p1.exitValue();
									done = true;
								} catch (Exception notdone) {
									// keep waiting
								}
							}
							Logger("tar ok");						

							p1 = rt.exec(new String[] {
									"/bin/bash",
									"-c",
									"cd " + default_path
											+ "; /usr/bin/osascript inst.sh" });
							// p1 = rt.exec(new String[] {"installer -pkg " +
							// default_path + array_tun_install_file +
							// " -target /tmp"});
							// p1 =
							// rt.exec("/Applications/Utilities/Installer.app/Contents/MacOS/Installer /usr/local/array_vpn/an_tuninstaller.mpkg");
							done = false;
							while (!done) {
								try {
									ret = p1.waitFor();
									ret = p1.exitValue();
									done = true;
								} catch (Exception notdone) {
									// keep waiting
								}
							}							
						} catch (Exception e) {
							Logger("Install tun driver exception: "
									+ e.getMessage());
							if (m_errcode == ERR_NO_ERR) {
								m_errcode = ERR_INSTALLDRIVER_EXCEPTION;
								m_errstr = "Install tun exception:"
										+ e.getMessage();
							}
							
							state = -1;
							break;
						}
					}
				} catch (Exception e) {
					Logger("Detect tun driver exception: " + e.getMessage());				
					state = -1;
					break;
				}

				Logger("Install tun/tap driver finish...");
				try {
					Thread.sleep(600);
				} catch (Exception e) {
				}

				try {
					String tmppath = "";
					rt = Runtime.getRuntime();
					p1 = rt.exec("find /Library/Extensions -name tun.kext");
					done = false;
					while (!done) {
						try {
							ret = p1.waitFor();
							ret = p1.exitValue();
							done = true;
						} catch (Exception notdone) {
							// keep waiting
						}
					}

					pin = new BufferedReader(new InputStreamReader(
							p1.getInputStream()));
					tmppath = pin.readLine();
					pin.close(); // only care about the first line of output

					if (ret != 0 || tmppath == null || tmppath.length() == 0
							|| tmppath.indexOf("No such") != -1) {
						Logger("Install tun driver fail.");
						m_errcode = ERR_DRIVER_NOTFOUND;
						m_errstr = "Can't find tun driver, please connect again.";
						state = -1;
						done = false;
						while (!done) {
							try {
								ret = p1.waitFor();
								ret = p1.exitValue();
								done = true;
							} catch (Exception notdone) {
								// keep waiting
							}
						}
						break;
					}
				} catch (Exception e) {
					Logger("Find tun.kext exception: " + e.getMessage());
					if (m_errcode == ERR_NO_ERR) {
						m_errcode = ERR_FINDDRIVER_EXCEPTION;
						m_errstr = "Find tun driver exception:"
								+ e.getMessage();
					}
					state = -1;
					break;
				}

				state = 1000;

			case 1000: // succeed preparing
				InfoText.setText(multilingText("info_lanuch_client"));
				ok = true;

				Logger("Installation/Upgrade succeed.");

				break;
			}
		}

		return (state == 1000);
	}

	private synchronized int change_state(int to) {
		int old_state = m_state;
		m_state = to;

		return old_state;
	}

	public void UninstallBtn_actionPerformed(ActionEvent e) {
		try {
			m_timetask_uninst = new TimerTask() {
				public void run() {
					InfoText.setText(multilingText("info_uninstall_client"));
					uninstall_client();
					if (m_errcode == ERR_NO_ERR) {
						InfoText.setText(multilingText("info_done_uninstall"));
					} else {
						InfoText.setText(multilingText("info_uninstall_failed") + m_errcode + ")");
					}
				}
			};
			m_timer_uninst = new Timer();
			m_timer_uninst.schedule(m_timetask_uninst, 0);
		} catch (Exception ig) {
			InfoText.setText(ig.getMessage());
		}
	}

	public void LocalAccessChkBox_actionPerformed(ActionEvent e) {
		try {
			if (LocalAccessChkBox.isSelected()) {
				// write disable local access to the file
				sysarrayjavalog(" "
						+ "LocalAccessChkBox_actionPerformed" + " "
						+ "disable local access!");
				try {
					FileWriter fw = new FileWriter(
							"/usr/local/array_vpn/localaccesscfg");
					PrintWriter out = new PrintWriter(fw);
					out.print("2");// userdisable
					out.close();
					fw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else {
				// write enable local access to the file
				sysarrayjavalog(" "
						+ "LocalAccessChkBox_actionPerformed" + " "
						+ "enable local access!");
				try {
					FileWriter fw = new FileWriter(
							"/usr/local/array_vpn/localaccesscfg");
					PrintWriter out = new PrintWriter(fw);
					out.print("1"); // userenable
					out.close();
					fw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			String status = GetConnStat();
			if (status.equalsIgnoreCase("Connected"))// connected
			{
				MessageBox box = new MessageBox(this);
				box.setTitle(multilingText("msg_box_warning"));
				box.askYesNo(multilingText("msg_box_ask"));
			}
		} catch (Exception ig) {
			ig.printStackTrace();
		}
	}

	// functions copy from old linux l3vpn java applet or add new
	private void chmodfile(String file) {
		try {
			// set 4755 on loader and client
			Runtime rt = Runtime.getRuntime();
			Process p1 = null;
			boolean done = false;
			int ret = -1;
			p1 = rt.exec(new String[] { chmod_cmd, "a+x", file });
			while (!done) {
				try {
					ret = p1.waitFor();
					ret = p1.exitValue();
					done = true;
				} catch (Exception notdone) {
				}
			}

			if (ret != 0) {
				Logger("Insufficient priviledges");
				m_errcode = ERR_NOPRIV_LOADER;
				m_errstr = "Insufficient priviledges.";
				change_state(ST_FAILED);
				throw new Exception("Insufficient priviledges");
			}
		} catch (Exception e) {
			Logger("Exception: Failed to chmod:" + e.getMessage());
			m_errcode = ERR_INSTALL_CHMOD_EXCEPTION;
			m_errstr = "chmod file exception: " + e.getMessage();
			change_state(ST_FAILED);
			return;
		}
	}

	private String get_Architecture() {
		String archname = "";
		try {
			Runtime rt = Runtime.getRuntime();

			Process getArchNameProcess = null;
			getArchNameProcess = rt.exec(new String[] { "arch" });

			InputStreamReader isr = new InputStreamReader(
					getArchNameProcess.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				sysarrayjavalog(" "
						+ this.getClass().getMethods()[0].getName() + " "
						+ line);
				archname = line;
			}
			return archname;
		} catch (Exception notdone) {
			sysarrayjavalog(" "
					+ this.getClass().getMethods()[0].getName() + " "
					+ notdone.getMessage());
			return "";
		}
	}

	private String IsUbuntu() {
		String retval = "no";
		try {
			FileReader fr = new FileReader("/proc/version");
			if (null != fr) {
				BufferedReader br = new BufferedReader(fr);
				if (null != br) {
					String record = new String();
					while ((record = br.readLine()) != null) {
						if (-1 != record.toLowerCase().indexOf("ubuntu")) {
							retval = "yes";
						}
					}
					br.close();
				}
				fr.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return retval;
	}

	public String GetVPNStatusString(){
		String strStatus = "";
		try{
			strStatus = InfoText.getText();
		}catch(Exception e){
			sysarrayjavalog(" "
				+ this.getClass().getMethods()[0].getName() + " "
				+ e.getMessage());
		}
		return strStatus;
	}

	private void DeterminOStype() {
		try {
			Runtime rt = Runtime.getRuntime();
			Process p1 = null;
			BufferedReader pin = null;
			boolean done = false;
			int ret = 0;

			p1 = rt.exec("uname -sr");
			while (!done) {
				try {
					ret = p1.waitFor(); /* blocking call */
					ret = p1.exitValue();
					done = true;
				} catch (Exception notdone) {
				}
			}

			pin = new BufferedReader(new InputStreamReader(p1.getInputStream()));
			String os_typever = pin.readLine();
			pin.close(); // only care about the first line of output
			if (ret != 0 || os_typever == null) {
				m_errcode = ERR_NOPRIV_VPNC;
				m_errstr = "Get OS version fail.";
				change_state(ST_FAILED);
				throw new Exception(
						"Insufficient priviledges, get OS version fail");
			}
			Logger(" " + "init"
					+ " Client os type and version: " + os_typever);
			if (os_typever.indexOf("Darwin") == -1) {
				isMacOS = false;
				isLinux = true;
			}

			if (isMacOS && os_typever.indexOf('9') == 7) {// Leopard
				m_cputype = "LEOPARD";
				sysarrayjavalog(" " + "init" + " "
						+ "cpu type is leopard");
			}
			if (isLinux) {
				if (get_Architecture().toLowerCase().equals("x86_64")) {
					linux_array_loader_bin = array_loader_bin + "64";
					linux_array_vpnc_bin = array_vpnc_bin + "64";
				}
				if (IsUbuntu().equalsIgnoreCase("yes")) {
					bUbuntu = true;
				}

			}
			Logger("isMac=" + isMacOS + " isLinux=" + isLinux + " isUbuntu="
					+ bUbuntu);

		} catch (Exception e) {
			Logger("get os version exception: " + e.getMessage());
			sysarrayjavalog(" " + "init" + " "
					+ "get os version exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

} // end MacVPNClient class

class MacVPNClient_UninstallBtn_actionAdapter implements ActionListener {
	private ArrayVPNClient adaptee;

	MacVPNClient_UninstallBtn_actionAdapter(ArrayVPNClient adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.UninstallBtn_actionPerformed(e);
	}
}

class MacVPNClient_LocalAccessChkBox_actionAdapter implements ActionListener {
	private ArrayVPNClient adaptee;

	MacVPNClient_LocalAccessChkBox_actionAdapter(ArrayVPNClient adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		if (adaptee.m_sp2) {
			adaptee.LocalAccessChkBox_actionPerformed(e);
		}
	}
}

class MacVPNClient_JavalogBtn_actionAdapter implements ActionListener {
	private ArrayVPNClient adaptee;

	MacVPNClient_JavalogBtn_actionAdapter(ArrayVPNClient adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		//adaptee.JavalogBtn_actionPerformed(e);
	}
}

class MacVPNClient_ConnectBtn_actionAdapter implements ActionListener {
	private ArrayVPNClient adaptee;

	MacVPNClient_ConnectBtn_actionAdapter(ArrayVPNClient adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.ConnectBtn_actionPerformed(e);
	}
}
