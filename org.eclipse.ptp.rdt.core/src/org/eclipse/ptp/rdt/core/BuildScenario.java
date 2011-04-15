package org.eclipse.ptp.rdt.core;

import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

/**
 * Class for build information that will be mapped to a specific service configuration. Utility methods for reading and writing
 * the information to an IConfiguration (a .cproject file) are also provided.
 * @since 3.1
 */
public class BuildScenario {
	private static final String ATTR_SYNC_PROVIDER = "sync-provider"; //$NON-NLS-1$
	private static final String ATTR_REMOTE_CONNECTION_ID = "remote-connection-id"; //$NON-NLS-1$
	private static final String ATTR_LOCATION = "location"; //$NON-NLS-1$
	private static final String ATTR_REMOTE_SERVICES_ID = "remote-services-id"; //$NON-NLS-1$
	final String syncProvider;
	final IRemoteConnection remoteConnection;
	final String location;
	
	/**
	 * Create a new build scenario
	 * 
	 * @param sp
	 *           Name of sync provider
	 * @param rcn
	 * 			 Name of remote connection
	 * @param l
	 * 			 Location (directory) on remote host
	 */
	public BuildScenario(String sp, IRemoteConnection rc, String l) {
		syncProvider = sp;
		remoteConnection = rc;
		location = l;
	}

	public String getSyncProvider() {
		return syncProvider;
	}

	public IRemoteConnection getRemoteConnection() {
		return remoteConnection;
	}

	public String getLocation() {
		return location;
	}
	
	/**
	 * Store scenario in a given memento 
	 *
	 * @param memento
	 */
	public void saveScenario(IMemento memento) {
		memento.putString(ATTR_SYNC_PROVIDER, syncProvider);
		memento.putString(ATTR_REMOTE_CONNECTION_ID, remoteConnection.getName());
		memento.putString(ATTR_LOCATION, location);
		memento.putString(ATTR_REMOTE_SERVICES_ID, remoteConnection.getRemoteServices().getId());
	}
	
	/**
	 * Load data from a memento into a new build scenario.
	 *
	 * @param memento
	 * @return a new build scenario or null if one of the values is not found or if something goes wrong while trying to find the
	 * appropriate IRemoteConnection using the remote services id and remote connection id stored in the memento.
	 */
	public static BuildScenario loadScenario(IMemento memento) {
		String sp = memento.getString(ATTR_SYNC_PROVIDER);
		String rc = memento.getString(ATTR_REMOTE_CONNECTION_ID);
		String l = memento.getString(ATTR_LOCATION);
		String rs = memento.getString(ATTR_REMOTE_SERVICES_ID);
		if ((sp == null) || (rc == null) || (l == null) || (rs == null)) {
			return null;
		}
		
		IRemoteServices remoteService = PTPRemoteCorePlugin.getDefault().getRemoteServices(rs);
		if (remoteService == null) {
			return null;
		}

		IRemoteConnection remoteConnection = remoteService.getConnectionManager().getConnection(rc);
		if (remoteConnection == null) {
			return null;
		}

		return new BuildScenario(sp, remoteConnection, l);
	}
}