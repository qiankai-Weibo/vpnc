package ArrayVPN;

//import java.awt.BorderLayout;
//import java.awt.Frame;

//import javax.swing.JDialog;

import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.*;


import com.borland.jbcl.layout.*;
//import javax.swing.JTextField;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.Border;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
//import java.applet.Applet;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MacVpnMainDlg extends JFrame {
	XYLayout xYLayout2 = new XYLayout();
	JButton ConnectBtn = new JButton();
	JLabel jLabel1 = new JLabel();
	JLabel jLabel2 = new JLabel();
	JLabel jLabel3 = new JLabel();
	JPanel jPanel1 = new JPanel();
	XYLayout xYLayout1 = new XYLayout();
	TitledBorder titledBorder1 = new TitledBorder("");
	Border border1 = BorderFactory.createEtchedBorder(Color.white, new Color(
			178, 178, 178));
	Border border2 = new TitledBorder(border1, "Status");
	JLabel InfoText = new JLabel();
	JLabel IpAddrText = new JLabel();
	JLabel ByteSentText = new JLabel();
	JLabel ByteRevText = new JLabel();

	static long m_secs = 0;
	PeriodTask m_task = new PeriodTask(this);

	private ArrayVPNClient m_parent;

	/* for only enter once close*/
	private boolean m_bBtnClose = false;

	public MacVpnMainDlg(String title) {
		super(title);
		try {
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			jbInit();
			pack();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public MacVpnMainDlg(ArrayVPNClient parent) {
		this("MacOS L3VPN Client");
		m_parent = parent;
	}

	private void jbInit() throws Exception {
		this.getContentPane().setLayout(xYLayout2);
		this.getContentPane().setBackground(SystemColor.text);
		this
				.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setEnabled(true);
		this.setJMenuBar(null);
		this.setResizable(true);
		this.addWindowListener(new MacVpnMainDlg_this_windowAdapter(this));
		ConnectBtn.setFont(new java.awt.Font("Arial", Font.PLAIN, 11));
		ConnectBtn.setText("Disconnect");
		ConnectBtn
				.addActionListener(new MacVpnMainDlg_ConnectBtn_actionAdapter(
						this));
		jLabel3.setFont(new java.awt.Font("Arial", Font.PLAIN, 11));
		jLabel3.setText("Byte Received:");
		jLabel2.setFont(new java.awt.Font("Arial", Font.PLAIN, 11));
		jLabel2.setText("Byte Sent:");
		jLabel1.setFont(new java.awt.Font("Arial", Font.PLAIN, 11));
		jLabel1.setText("Assigned IP Address:");
		InfoText.setFont(new java.awt.Font("Arial", Font.PLAIN, 11));
		InfoText.setText("Connected: 0 seconds");
		jPanel1.setBackground(SystemColor.text);
		jPanel1.setFont(new java.awt.Font("Arial", Font.PLAIN, 11));
		jPanel1.setBorder(border2);
		jPanel1.setLayout(xYLayout1);
		xYLayout2.setWidth(403);
		xYLayout2.setHeight(190);
		IpAddrText.setFont(new java.awt.Font("Arial", Font.PLAIN, 11));
		IpAddrText.setText("");
		ByteSentText.setFont(new java.awt.Font("Arial", Font.PLAIN, 11));
		ByteSentText.setText("0");
		ByteRevText.setFont(new java.awt.Font("Arial", Font.PLAIN, 11));
		ByteRevText.setText("0");
		//this.getContentPane().add(ConnectBtn, new XYConstraints(158, 150, 87, -1));
		this.getContentPane().add(ConnectBtn,
				new XYConstraints(158, 150, 100, -1));
		jPanel1.add(InfoText, new XYConstraints(11, 1, 307, 34));
		this.getContentPane().add(jPanel1, new XYConstraints(24, 73, 353, 71));
		this.getContentPane().add(jLabel2, new XYConstraints(77, 35, 59, -1));
		this.getContentPane().add(jLabel3, new XYConstraints(57, 53, 80, -1));
		this.getContentPane().add(IpAddrText,
				new XYConstraints(158, 15, 190, 18));
		this.getContentPane().add(jLabel1, new XYConstraints(21, 13, 124, 20));
		this.getContentPane().add(ByteRevText,
				new XYConstraints(158, 55, 171, 15));
		this.getContentPane().add(ByteSentText,
				new XYConstraints(158, 36, 171, 16));

	}

	// centers the dialog within the screen [1.1]
	//  (put that in the Frame/Dialog class)
	private void centerScreen() {
		Dimension dim = getToolkit().getScreenSize();
		Rectangle abounds = getBounds();
		setLocation((dim.width - abounds.width) / 2,
				(dim.height - abounds.height) / 2);
		requestFocus();
		setResizable(true);
	}

	// centers the dialog within the parent container [1.1]
	//  (put that in the Dialog class)
	private void centerParent() {
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

		setLocation(x, y);
		requestFocus();
		setResizable(true);
	}

	public void ShowDlg() {
		centerScreen();
		setVisible(true);

		//       m_task.StartTimer(1000, 1000);
	}

	public void UpdateStatus(String status, String assignip, String byte_in,
			String byte_out, boolean connect, boolean logout) {
		if (status != null) {
			InfoText.setText(status);
		}

		if (assignip != null) {
			IpAddrText.setText(assignip);
		}

		if (byte_out != null) {
			ByteSentText.setText(byte_out);
		}

		if (byte_in != null) {
			ByteRevText.setText(byte_in);
		}

		ConnectBtn.setEnabled(connect);
	}

	public void PeriodTask() {
		String status = "Connected: ";
		m_secs++;

		status += String.valueOf(m_secs) + " seconds";

		//       UpdateStatus(status, true, false);
	}

	public void setBtnClose(boolean isBtnClose) {
		m_bBtnClose = isBtnClose;
	}

	public void this_windowClosed(WindowEvent e) {
		if (m_bBtnClose)
			return;

		//        ConnectBtn_actionPerformed(null);
	}

	public void CloseDlg() {
		m_secs = 0; //no use
		dispose();
	}

	public void ConnectBtn_actionPerformed(ActionEvent e) {
		setBtnClose(true);
		m_parent.terminate();
	}
}

class MacVpnMainDlg_ConnectBtn_actionAdapter implements ActionListener {
	private MacVpnMainDlg adaptee;

	MacVpnMainDlg_ConnectBtn_actionAdapter(MacVpnMainDlg adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.ConnectBtn_actionPerformed(e);
	}
}

class MacVpnMainDlg_this_windowAdapter extends WindowAdapter {
	private MacVpnMainDlg adaptee;

	MacVpnMainDlg_this_windowAdapter(MacVpnMainDlg adaptee) {
		this.adaptee = adaptee;
	}

	public void windowClosed(WindowEvent e) {
		adaptee.this_windowClosed(e);
	}
}
