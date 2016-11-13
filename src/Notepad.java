package ArrayVPN;


import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

public class Notepad extends JFrame {
	boolean m_Filter = true;
	boolean m_bExit = true;
	Process m_vpnprocess = null;
	BufferedInputStream m_stdin;
	BufferedOutputStream m_stdout;

	final JTextArea text;
	private ArrayVPNClient m_parent;
	private static final short CODE_CONFIG = 1;//1:receive configuration; 2:begin transfer log

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

	public void readLogFile() {
		StringBuffer strBF = new StringBuffer();
		text.setText("");
		m_bExit = true;

		while (m_bExit) {
			try {
				if (m_stdout != null && m_stdin != null) {
					byte[] code = { 1 };
					m_stdout.write(code);
					m_stdout.flush();
					byte[] rev = new byte[512];
					int len = m_stdin.read(rev);
					strBF.append(String.valueOf(bytes2str(rev, 0, len + 1)));
					rev = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			text.setText(strBF.toString());
			text.setCaretPosition(strBF.length());
		}
	}

	private static byte[] str2bytes(String str) {
		char[] chars = str.toCharArray();
		byte[] bytes = new byte[chars.length];

		int i;
		for (i = 0; i < chars.length; i++) {
			bytes[i] = (byte) (chars[i] & 0xFF);
		}

		return bytes;
	}

	//convert from int to bytes in network order (BE)
	private static byte[] int2bytes(int num, int len) {
		int i;
		byte[] bytes = new byte[len];

		for (i = (len - 1) * 8; i >= 0; i -= 8) {
			bytes[(len - 1) - i / 8] = (byte) ((num >> i) & 0xFF);
		}

		return bytes;
	}

	//construct a configuration packet from the available information
	private byte[] encode_config_packet() {
		byte[] config = new byte[1];
		return config;
		//create the CONFIG packet
	/*	int config_len = 2 + //code
				4 + //packet length
				2 + m_parent.m_StartTime.length() + 2;



		int i, offset;
		byte[] temp = null;

		offset = 0; //we bump this every write
		//it would be cool if Java supported macros

		temp = int2bytes(CODE_CONFIG, 2);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}

		temp = int2bytes(config_len, 4);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}

		temp = int2bytes(m_parent.m_StartTime.length(), 2);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}
		temp = str2bytes(m_parent.m_StartTime);
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}

		if (m_Filter) {
			int tmp = 1;
			temp = int2bytes(tmp, 2);
		} else {
			int tmp = 0;
			temp = int2bytes(tmp, 2);
		}
		for (i = 0; i < temp.length; i++) {
			config[offset++] = temp[i];
		}

		return config;*/
	}

	private void start_transferlog() {
		//start macvpn_loader to get log messages
		Runtime rt = Runtime.getRuntime();
		try {
			m_vpnprocess = rt.exec(new String[] {
					"/usr/local/array_vpn/macvpn_loader", "transferlog" });
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		m_stdin = new BufferedInputStream(m_vpnprocess.getInputStream(), 4096);
		m_stdout = new BufferedOutputStream(m_vpnprocess.getOutputStream());

		byte[] config_packet = encode_config_packet();
		try {
			m_stdout.write(config_packet, 0, config_packet.length);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			m_stdout.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		config_packet = null; //allow it to be garbage collected

		//clear all stdin characters
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (m_stdin != null) {
			byte[] ig = new byte[4096]; //4k is enough, maybe run route command
			try {
				int len = m_stdin.read(ig);
				System.out.println(String.valueOf(bytes2str(ig, 0, len + 1)));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ig = null;
		} else {
			System.out.println("stdin is null.");
		}

		Thread arraylog = new Thread() {
			public void run() {
				readLogFile();
			}
		};

		arraylog.start();
	}

	private void restart_transferlog() {
		//stop readlog
		m_bExit = false;

		//stop macvpn_loader
		m_vpnprocess.destroy();

		Runtime rt = Runtime.getRuntime();
		try {
			m_vpnprocess = rt.exec(new String[] { "killall", "-15",
					"macvpn_loader" });
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		m_stdin = null;
		m_stdout = null;
		m_vpnprocess = null;

		//start transferlog
		start_transferlog();
	}

	public Notepad(ArrayVPNClient parent) {
		super("Mac L3VPN Log");
		text = new JTextArea();
		text.setToolTipText("Get related information from system log.");
		m_parent = parent;
		//interface

		//Quit event
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//System.exit(0);
				m_bExit = false;

				dispose();
			//	m_parent.JavalogBtn.setEnabled(true);
			//	m_parent.JavalogBtn.setForeground(Color.black);

				m_vpnprocess.destroy();

				Runtime rt = Runtime.getRuntime();
				try {
					m_vpnprocess = rt.exec(new String[] { "killall", "-15",
							"macvpn_loader" });
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		//simple layout
		final JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 1));
		panel.add(new JScrollPane(text));
		this.getContentPane().add(panel);

		//menu item
		JMenuBar Mbar = new JMenuBar();
		this.setJMenuBar(Mbar);
		JMenu file = new JMenu("File");
		JMenu edit = new JMenu("Edit");
		JMenu help = new JMenu("Help");
		Mbar.add(file);
		Mbar.add(edit);
		Mbar.add(help);

		//layout is over

		//get log and display
		start_transferlog();

		//Quit
		JMenuItem exit = new JMenuItem("Quit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_bExit = false;

				dispose();
		//		m_parent.JavalogBtn.setEnabled(true);
		//		m_parent.JavalogBtn.setForeground(Color.black);
				//System.exit(0);

				m_vpnprocess.destroy();

				Runtime rt = Runtime.getRuntime();
				try {
					m_vpnprocess = rt.exec(new String[] { "killall", "-15",
							"macvpn_loader" });
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		exit.setMnemonic('Q');
		exit.setAccelerator(KeyStroke.getKeyStroke('Q',
				java.awt.Event.CTRL_MASK, true));

		//Saveas
		JMenuItem saveas = new JMenuItem("Save As");
		saveas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser savefile = new JFileChooser();
				savefile.setApproveButtonText("Saveas");
				savefile.setDialogTitle("Save File");
				savefile.showSaveDialog(panel);
				File filesa = savefile.getSelectedFile();
				FileOutputStream outputfile = null;

				//exception 
				try {
					outputfile = new FileOutputStream(filesa);
				} catch (FileNotFoundException fe) {
					System.out.println(fe.getMessage());
				}

				String filecontent = text.getText();
				try {
					outputfile.write(filecontent.getBytes());
				} catch (IOException ioEx) {
					System.out.println(ioEx.getMessage());
				}

				try {
					outputfile.close();
				} catch (IOException ioEx) {
					System.out.println(ioEx.getMessage());
				}
			}
		});
		saveas.setMnemonic('S');
		saveas.setAccelerator(KeyStroke.getKeyStroke('S',
				java.awt.Event.CTRL_MASK, true));

		//copy
		JMenuItem copy = new JMenuItem("CopyAll");
		copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				text.selectAll();
				text.copy();
			}
		});
		copy.setMnemonic('o');
		copy.setAccelerator(KeyStroke.getKeyStroke('O',
				java.awt.Event.CTRL_MASK, true));

		//switch filter
		// JMenuItem filter = new JMenuItem("ArrayLog");
		JCheckBoxMenuItem filter = new JCheckBoxMenuItem("OnlyArrayLog", true);
		filter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (m_Filter) {
					m_Filter = false;
					restart_transferlog();
				} else {
					m_Filter = true;
					restart_transferlog();
				}
				//				text.selectAll();
			}
		});
		filter.setMnemonic('F');
		filter.setAccelerator(KeyStroke.getKeyStroke('F',
				java.awt.Event.CTRL_MASK, true));

		JMenuItem about = new JMenuItem("About");
		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int type = JOptionPane.PLAIN_MESSAGE;
				String title = "About";
				String message = "logger Version "
						+ m_parent.m_spver
						+ "\n"
						+ "Copyright (c) 2006-2009, Array\nNetworks, Inc\nSupport Mac OS X 10.4, Mac OS X 10.5\non PowerPC and Intel";//"By zhuyj ArrayNetworks";
				JOptionPane.showMessageDialog(panel, message, title, type);
			}
		});

		// file.addSeparator();
		file.add(saveas);
		file.add(exit);
		edit.add(copy);
		edit.add(filter);
		help.add(about);
	}
	/*  
	public static void main(String[] args) {
	Notepad notepad = new Notepad();
	notepad.setSize(640, 480);
	notepad.setVisible(true);
	notepad.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
	 */
}