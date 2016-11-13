package ArrayVPN;



import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;

import java.awt.event.KeyEvent;

import java.awt.event.KeyListener;

import java.awt.event.WindowEvent;

import java.awt.event.WindowListener;

import javax.swing.*;

import java.awt.*;



public class MessageBox implements Runnable, ActionListener, WindowListener,

		KeyListener {



	// ---------- Private Fields ------------------------------

	private ActionListener listener;

	private JDialog dialog;

	private String closeWindowCommand = "CloseRequested";

	private String title;

	private JFrame frame;

	private boolean frameNotProvided;

	private JPanel buttonPanel = new JPanel();



	private ArrayVPNClient m_parent;



	// ---------- Initialization ------------------------------

	/**

	 * This convenience constructor is used to delare the listener that will be

	 * notified when a button is clicked. The listener must implement

	 * ActionListener.

	 */

	/*

	 * public MessageBox(ActionListener listener) { this(); this.listener =

	 * listener; }

	 */

	/**

	 * This constructor is used for no listener, such as for a simple okay

	 * dialog.

	 */

	public MessageBox() {

	}



	/*

	 * usage:sysarrayjavalog(this.getClass().getName() + " " +

	 * this.getClass().getMethods()[0].getName() + " " + errorinfo);

	 */

	private void sysarrayjavalog(String loginfo) {

		try {

			Runtime rt = Runtime.getRuntime();

			Process p1 = rt.exec(new String[] { "/usr/bin/syslog", "-s", "-l",

					"3", "arrayjavalog " + loginfo });

		} catch (Exception e) {

			System.out.println(e.getMessage());

		}

	}



	public MessageBox(ArrayVPNClient parent) {



		m_parent = parent;

	}



	// Unit test. Shows only simple features.

	public static void main(String args[]) {

		MessageBox box = new MessageBox();

		box.setTitle("Test MessageBox");

		box.askYesNo("Tell me now.\nDo you like Java?");

	}



	// ---------- Runnable Implementation ---------------------

	/**

	 * This prevents the caller from blocking on ask(), which if this class is

	 * used on an awt event thread would cause a deadlock.

	 */

	public void run() {

		dialog.setVisible(true);

	}



	// ---------- ActionListener Implementation ---------------

	public void actionPerformed(ActionEvent evt) {

		dialog.setVisible(false);

		dialog.dispose();



		if (evt.getActionCommand().equals("Yes")) {

			sysarrayjavalog(this.getClass().getName() + " "

					+ this.getClass().getMethods()[0].getName() + " "

					+ "click yes!");

			m_parent.terminate();

		}

		if (evt.getActionCommand().equalsIgnoreCase("No")) {

			sysarrayjavalog(this.getClass().getName() + " "

					+ this.getClass().getMethods()[0].getName() + " "

					+ "click no!");

		}

		if (frameNotProvided) {

			frame.dispose();

		}

		if (listener != null) {

			listener.actionPerformed(evt);

		}

	}



	// ---------- WindowListener Implementatons ---------------

	public void windowClosing(WindowEvent evt) {

		// User clicked on X or chose Close selection

		fireCloseRequested();

	}



	public void windowClosed(WindowEvent evt) {

	}



	public void windowDeiconified(WindowEvent evt) {

	}



	public void windowIconified(WindowEvent evt) {

	}



	public void windowOpened(WindowEvent evt) {

	}



	public void windowActivated(WindowEvent evt) {

	}



	public void windowDeactivated(WindowEvent evt) {

	}



	// ---------- KeyListener Implementation ------------------

	public void keyTyped(KeyEvent evt) {

	}



	public void keyPressed(KeyEvent evt) {

		if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {

			fireCloseRequested();

		}

	}



	public void keyReleased(KeyEvent evt) {

	}



	private void fireCloseRequested() {

		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,

				closeWindowCommand);

		actionPerformed(event);

	}



	// ---------- Public Methods ------------------------------

	/**

	 * This set the listener to be notified of button clicks and WindowClosing

	 * events.

	 */

	public void setActionListener(ActionListener listener) {

		this.listener = listener;

	}



	public void setTitle(String title) {

		this.title = title;

	}



	/**

	 * If a Frame is provided then it is used to instantiate the modal Dialog.

	 * Otherwise a temporary Frame is used. Providing a Frame will have the

	 * effect of putting the focus back on that Frame when the MessageBox is

	 * closed or a button is clicked.

	 */

	public void setFrame(JFrame frame) { // Optional

		this.frame = frame;

	}



	/**

	 * Sets the ActionCommand used in the ActionEvent when the user attempts to

	 * close the window. The window may be closed by clicking on "X", choosing

	 * Close from the window menu, or pressing the Escape key. The default

	 * command is "CloseRequested", which is just what a Close choice button

	 * would probably have as a command.

	 */

	public void setCloseWindowCommand(String command) {

		closeWindowCommand = command;

	}



	/**

	 * The

	 * 

	 * @param label

	 *            will be used for the button and the

	 * @param command

	 *            will be returned to the listener.

	 */

	public void addChoice(String label, String command) {

		JButton button = new JButton(label);

		button.setActionCommand(command);

		button.addActionListener(this);

		button.addKeyListener(this);



		buttonPanel.add(button);

	}



	/**

	 * A convenience method that assumes the command is the same as the label.

	 */

	public void addChoice(String label) {

		addChoice(label, label);

	}



	/**

	 * One of the "ask" methods must be the last call when using a MessageBox.

	 * This is the simplest "ask" method. It presents the provided

	 * 

	 * @param message.

	 */

	public void ask(String message) {

		if (frame == null) {

			frame = new JFrame();

			frameNotProvided = true;

		} else {

			frameNotProvided = false;

		}

		dialog = new JDialog(frame, true); // Modal

		dialog.addWindowListener(this);

		dialog.addKeyListener(this);

		dialog.setTitle(title);

		dialog.setLayout(new BorderLayout(5, 5));



		JPanel messagePanel = createMultiLinePanel(message);

		JPanel centerPanel = new JPanel();

		centerPanel.add(messagePanel);

		dialog.add("Center", centerPanel);

		dialog.add("South", buttonPanel);

		dialog.pack();

		enforceMinimumSize(dialog, 200, 100);

		centerWindow(dialog);

		Toolkit.getDefaultToolkit().beep();



		// Start a new thread to show the dialog

		Thread thread = new Thread(this);

		thread.start();

	}



	/**

	 * Same as ask(String message) except adds an "Okay" button.

	 */

	public void askOkay(String message) {

		addChoice("Okay");

		ask(message);

	}



	/**

	 * Same as ask(String message) except adds "Yes" and "No" buttons.

	 */

	public void askYesNo(String message) {

		addChoice("Yes");

		addChoice("No");

		ask(message);

	}



	// ---------- Private Methods -----------------------------

	private JPanel createMultiLinePanel(String message) {

		JPanel mainPanel = new JPanel();

		GridBagLayout gbLayout = new GridBagLayout();

		mainPanel.setLayout(gbLayout);

		addMultilineString(message, mainPanel);

		return mainPanel;

	}



	// There are a variaty of ways to do this....

	private void addMultilineString(String message, Container container) {

		GridBagConstraints constraints = getDefaultConstraints();

		constraints.gridwidth = GridBagConstraints.REMAINDER;

		// Insets() args are top, left, bottom, right

		constraints.insets = new Insets(0, 0, 0, 0);

		GridBagLayout gbLayout = (GridBagLayout) container.getLayout();



		while (message.length() > 0) {

			int newLineIndex = message.indexOf('\n');

			String line;

			if (newLineIndex >= 0) {

				line = message.substring(0, newLineIndex);

				message = message.substring(newLineIndex + 1);

			} else {

				line = message;

				message = "";

			}

			JLabel label = new JLabel(line);

			gbLayout.setConstraints(label, constraints);

			container.add(label);

		}

	}



	private GridBagConstraints getDefaultConstraints() {

		GridBagConstraints constraints = new GridBagConstraints();

		constraints.weightx = 1.0;

		constraints.weighty = 1.0;

		constraints.gridheight = 1; // One row high

		// Insets() args are top, left, bottom, right

		constraints.insets = new Insets(4, 4, 4, 4);

		// fill of NONE means do not change size

		constraints.fill = GridBagConstraints.NONE;

		// WEST means align left

		constraints.anchor = GridBagConstraints.WEST;



		return constraints;

	}



	private void centerWindow(JDialog win) {

		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();

		// If larger than screen, reduce window width or height

		if (screenDim.width < win.getSize().width) {

			win.setSize(screenDim.width, win.getSize().height);

		}

		if (screenDim.height < win.getSize().height) {

			win.setSize(win.getSize().width, screenDim.height);

		}

		// Center Frame, Dialogue or Window on screen

		int x = (screenDim.width - win.getSize().width) / 2;

		int y = (screenDim.height - win.getSize().height) / 2;

		win.setLocation(x, y);

	}



	private void enforceMinimumSize(JDialog comp, int minWidth, int minHeight) {

		if (comp.getSize().width < minWidth) {

			comp.setSize(minWidth, comp.getSize().height);

		}

		if (comp.getSize().height < minHeight) {

			comp.setSize(comp.getSize().width, minHeight);

		}

	}

}

