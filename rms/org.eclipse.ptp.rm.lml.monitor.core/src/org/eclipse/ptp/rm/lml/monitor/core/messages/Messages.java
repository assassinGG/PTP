package org.eclipse.ptp.rm.lml.monitor.core.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.ptp.rm.lml.monitor.core.messages.messages"; //$NON-NLS-1$
	public static String LMLResourceManagerMonitor_LMLMonitorJob;
	public static String LMLResourceManagerMonitor_RMSelectionJob;
	public static String LMLResourceManagerMonitor_unableToOpenConnection;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
