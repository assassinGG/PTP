/*******************************************************************************
 * Copyright (c) 2011 The University of Tennessee and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    John Eblen - initial implementation
 *******************************************************************************/
package org.eclipse.ptp.rdt.sync.git.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ptp.rdt.sync.core.serviceproviders.ISyncServiceProvider;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteConnectionChangeEvent;
import org.eclipse.ptp.remote.core.IRemoteConnectionChangeListener;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.remote.core.exception.RemoteConnectionException;
import org.eclipse.ptp.services.core.ServiceProvider;

public class GitServiceProvider extends ServiceProvider implements ISyncServiceProvider {
	public static final String ID = "org.eclipse.ptp.rdt.sync.git.core.GitServiceProvider"; //$NON-NLS-1$

	private static final String GIT_LOCATION = "location"; //$NON-NLS-1$

	private static final String GIT_CONNECTION_NAME = "connectionName"; //$NON-NLS-1$
	private static final String GIT_SERVICES_ID = "servicesId"; //$NON-NLS-1$
	private static final String GIT_PROJECT_NAME = "projectName"; //$NON-NLS-1$
	private IProject fProject = null;
	private String fLocation = null;
	private IRemoteConnection fConnection = null;
	private IRemoteSyncConnection fSyncConnection = null;

	/**
	 * Get the remote directory that will be used for synchronization
	 * 
	 * @return path
	 */
	public String getLocation() {
		if (fLocation == null) {
			fLocation = getString(GIT_LOCATION, null);
		}
		return fLocation;
	}

	/**
	 * Get the project to be synchronized
	 * 
	 * @return project
	 */
	public IProject getProject() {
		if (fProject == null) {
			final String name = getString(GIT_PROJECT_NAME, null);
			if (name != null) {
				fProject = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
			}
		}
		return fProject;
	}

	/**
	 * Get the remote connection used for synchronization
	 * 
	 * @return remote connection
	 */
	public IRemoteConnection getRemoteConnection() {
		if (fConnection == null) {
			final String name = getString(GIT_CONNECTION_NAME, null);
			if (name != null) {
				final IRemoteServices services = getRemoteServices();
				if (services != null) {
					fConnection = services.getConnectionManager().getConnection(name);
				}
			}
		}
		return fConnection;
	}

	/**
	 * Get the remote services used for the connection
	 * 
	 * @return remote services
	 */
	public IRemoteServices getRemoteServices() {
		final String id = getString(GIT_SERVICES_ID, null);
		if (id != null) {
			return PTPRemoteCorePlugin.getDefault().getRemoteServices(id);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.services.core.IServiceProvider#isConfigured()
	 */
	@Override
	public boolean isConfigured() {
		return getLocation() != null && getRemoteConnection() != null && getProject() != null;
	}

	/**
	 * Set the remote directory that will be used for synchronization
	 * 
	 * @param location
	 *            directory path
	 */
	public void setLocation(String location) {
		fLocation = location;
		putString(GIT_LOCATION, location);
	}

	/**
	 * Set the project that will be synchronized
	 * 
	 * @param project
	 *            project to synchronize
	 */
	public void setProject(IProject project) {
		fProject = project;
		putString(GIT_PROJECT_NAME, project.getName());
	}

	/**
	 * set the remote connection used for synchronization
	 * 
	 * @param conn
	 *            remote connection
	 */
	public void setRemoteConnection(IRemoteConnection conn) {
		fConnection = conn;
		putString(GIT_CONNECTION_NAME, conn.getName());
	}

	/**
	 * Set the remote services used for the connection
	 * 
	 * @param services
	 *            remote services
	 */
	public void setRemoteServices(IRemoteServices services) {
		putString(GIT_SERVICES_ID, services.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.rdt.sync.core.serviceproviders.ISyncServiceProvider#
	 * synchronize(org.eclipse.core.resources.IResourceDelta, org.eclipse.core.runtime.IProgressMonitor, boolean)
	 */
	@Override
	public synchronized void synchronize(IResourceDelta delta, IProgressMonitor monitor, boolean force) throws CoreException {

		// TODO: Use delta information
		// switch (delta.getKind()) {
		// case IResourceDelta.ADDED:
		// System.out.println("ensureSync kind=ADDED");
		// break;
		// case IResourceDelta.REMOVED:
		// System.out.println("ensureSync kind=REMOVED");
		// break;
		// case IResourceDelta.CHANGED:
		// System.out.println("ensureSync kind=CHANGED");
		// break;
		// default:
		// System.out.println("ensureSync kind=OTHER");
		// }
		// for (IResourceDelta child : delta.getAffectedChildren()) {
		// IResource resource = child.getResource();
		// if (resource instanceof IProject) {
		// System.out.println("ensureSync project=" + child.getResource().getName());
		// synchronize(child, monitor,
		// force);
		// } else if (resource instanceof IFolder) {
		// System.out.println("ensureSync folder=" +
		// child.getResource().getName());
		// synchronize(child, monitor, force);
		// } else if (resource instanceof IFile) {
		// System.out.println("ensureSync file=" + child.getResource().getName());
		// }
		// }

		// TODO: Review exception handling
		if (fSyncConnection == null) {

			// Open remote connection if necessary
			if (fConnection.isOpen() == false) {
				try {
					fConnection.open(null);
				} catch (final RemoteConnectionException e) {
					throw new RemoteSyncException(e);
				}
			}

			// Open a remote sync connection
			try {
				fSyncConnection = new GitRemoteSyncConnection(fConnection, fProject.getLocation().toString(), fLocation);
			} catch (final RemoteSyncException e) {
				throw e;
			}

			// Listen for the connection to close. In that case, remove the listener and set fSyncConnection to null.
			fConnection.addConnectionChangeListener(new IRemoteConnectionChangeListener() {
				@Override
				public void connectionChanged(IRemoteConnectionChangeEvent event) {
					if (event.getConnection().isOpen() == false) {
						fConnection.removeConnectionChangeListener(this);
						try {
							fSyncConnection.close();
						} catch (final RemoteSyncException e) {
							// Nothing to do
						}
						fSyncConnection = null;
					}
				}
			});
		}

		// Sync local and remote. For now, do both ways each time. This breaks encapsulation, though, because while Git handles this
		// correctly, other tools, such as rsync, might not.
		// TODO: Sync more efficiently and appropriately to the situation and in a way that works for a general sync tool.
		try {
			fSyncConnection.syncLocalToRemote();
		} catch (final RemoteSyncException e) {
			throw e;
		}

		try {
			fSyncConnection.syncRemoteToLocal();
		} catch (final RemoteSyncException e) {
			throw e;
		}
	}

}