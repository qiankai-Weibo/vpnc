package ArrayVPN;


import java.util.Timer;
import java.util.TimerTask;

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
public class PeriodTask extends TimerTask {
	public PeriodTask() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private Object m_parent;
	private Timer m_timer;
//	private long m_interval = 0;
	private boolean m_started = false;

	public PeriodTask(Object obj) {
		m_parent = obj;
	}

	/**
	 * When an object implementing interface <code>Runnable</code> is used to
	 * create a thread, starting the thread causes the object's <code>run</code>
	 * method to be called in that separately executing thread.
	 * 
	 * @todo Implement this java.lang.Runnable method
	 */
	public void run() {
		if (m_parent instanceof MacVpnMainDlg) {
			((MacVpnMainDlg) m_parent).PeriodTask();
		} else if (m_parent instanceof ArrayVPNClient) {
			((ArrayVPNClient) m_parent).PeriodTask();
		}
	}

	public void StartTimer(long interval, long delay) {
		if (m_started)
			return;

		m_timer = new Timer();
	//	m_interval = interval;
		m_timer.schedule(this, delay, interval);
		m_started = true;
	}

	public void StopTimer() {
		if (m_started) {
			m_timer.cancel();
			m_timer = null;
		}
		m_started = false;
	}

	private void jbInit() throws Exception {
	}
}
