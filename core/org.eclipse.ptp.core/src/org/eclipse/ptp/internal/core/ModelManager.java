/*******************************************************************************
 * Copyright (c) 2005 The Regents of the University of California. 
 * This material was produced under U.S. Government contract W-7405-ENG-36 
 * for Los Alamos National Laboratory, which is operated by the University 
 * of California for the U.S. Department of Energy. The U.S. Government has 
 * rights to use, reproduce, and distribute this software. NEITHER THE 
 * GOVERNMENT NOR THE UNIVERSITY MAKES ANY WARRANTY, EXPRESS OR IMPLIED, OR 
 * ASSUMES ANY LIABILITY FOR THE USE OF THIS SOFTWARE. If software is modified 
 * to produce derivative works, such modified software should be clearly marked, 
 * so as not to confuse it with the version available from LANL.
 * 
 * Additionally, this program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * LA-CC 04-115
 *******************************************************************************/
package org.eclipse.ptp.internal.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ptp.core.IModelManager;
import org.eclipse.ptp.core.IServiceConstants;
import org.eclipse.ptp.core.PTPCorePlugin;
import org.eclipse.ptp.core.PreferenceConstants;
import org.eclipse.ptp.core.Preferences;
import org.eclipse.ptp.core.elements.IPUniverse;
import org.eclipse.ptp.core.events.IResourceManagerAddedEvent;
import org.eclipse.ptp.core.events.IResourceManagerChangedEvent;
import org.eclipse.ptp.core.events.IResourceManagerErrorEvent;
import org.eclipse.ptp.core.events.IResourceManagerRemovedEvent;
import org.eclipse.ptp.core.listeners.IResourceManagerListener;
import org.eclipse.ptp.core.messages.Messages;
import org.eclipse.ptp.internal.core.elements.PUniverse;
import org.eclipse.ptp.internal.core.events.ResourceManagerAddedEvent;
import org.eclipse.ptp.internal.core.events.ResourceManagerRemovedEvent;
import org.eclipse.ptp.internal.core.events.ResourceManagerChangedEvent;
import org.eclipse.ptp.internal.core.events.ResourceManagerErrorEvent;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;
import org.eclipse.ptp.rmsystem.IResourceManagerControl;
import org.eclipse.ptp.services.core.IService;
import org.eclipse.ptp.services.core.IServiceConfiguration;
import org.eclipse.ptp.services.core.IServiceModelEvent;
import org.eclipse.ptp.services.core.IServiceModelEventListener;
import org.eclipse.ptp.services.core.IServiceModelManager;
import org.eclipse.ptp.services.core.IServiceProvider;
import org.eclipse.ptp.services.core.ServiceModelManager;

/**
 * @author greg
 * 
 */
public class ModelManager implements IModelManager {
	private class RMStartupJob extends Job {
		private final IResourceManagerControl resourceManager;

		public RMStartupJob(IResourceManagerControl rm) {
			super(Messages.ModelManager_0 + rm.getName());
			resourceManager = rm;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {
				try {
					monitor.beginTask(Messages.ModelManager_1, 100);
					resourceManager.start(new SubProgressMonitor(monitor, 100));
				} catch (CoreException e) {
					return e.getStatus();
				}
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
				return Status.OK_STATUS;
			} finally {
				monitor.done();
			}
		}

	}

	private final IServiceModelEventListener fServiceEventListener = new IServiceModelEventListener() {

		public void handleEvent(IServiceModelEvent event) {
			switch (event.getType()) {
			case IServiceModelEvent.SERVICE_CONFIGURATION_REMOVED: {
				IServiceProvider provider = ((IServiceConfiguration) event.getSource()).getServiceProvider(fLaunchService);
				if (provider != null && provider instanceof IResourceManagerConfiguration) {
					IResourceManagerControl rm = getResourceManagerFromUniqueName(((IResourceManagerConfiguration) provider)
							.getUniqueName());
					if (rm != null) {
						doRemoveResourceManager(rm);
					}
				}
				break;
			}

			case IServiceModelEvent.SERVICE_CONFIGURATION_CHANGED: {
				IServiceConfiguration config = (IServiceConfiguration) event.getSource();
				IServiceProvider oldProvider = event.getOldProvider();
				if (oldProvider != null) {
					if (oldProvider instanceof IResourceManagerConfiguration) {
						IServiceProvider newProvider = config.getServiceProvider(fLaunchService);
						if (newProvider != null && newProvider instanceof IResourceManagerConfiguration) {
							IResourceManagerControl rm = getResourceManagerFromUniqueName(((IResourceManagerConfiguration) oldProvider)
									.getUniqueName());
							if (rm != null) {
								rm.setConfiguration((IResourceManagerConfiguration) newProvider);
							}
						}
					}
				} else {
					IServiceProvider newProvider = config.getServiceProvider(fLaunchService);
					if (newProvider != null && newProvider instanceof IResourceManagerConfiguration) {
						IResourceManagerControl rm = getResourceManagerFromUniqueName(((IResourceManagerConfiguration) newProvider)
								.getUniqueName());
						if (rm == null) {
							rm = (IResourceManagerControl) ((IResourceManagerConfiguration) newProvider)
									.getAdapter(IResourceManagerControl.class);
							if (rm != null) {
								addResourceManager(rm);
							}
						}
					}
				}
				break;
			}

			case IServiceModelEvent.SERVICE_PROVIDER_CHANGED: {
				IServiceProvider provider = (IServiceProvider) event.getSource();
				if (provider != null && provider instanceof IResourceManagerConfiguration) {
					IResourceManagerControl rm = getResourceManagerFromUniqueName(((IResourceManagerConfiguration) provider)
							.getUniqueName());
					if (rm != null) {
						fireResourceManagerChanged(rm);
					}
				}
				break;
			}
			}
		}
	};

	private final ListenerList resourceManagerListeners = new ListenerList();

	protected final IServiceModelManager fServiceManager = ServiceModelManager.getInstance();
	protected final Map<String, IResourceManagerControl> resourceManagers = new HashMap<String, IResourceManagerControl>();
	protected final IService fLaunchService = fServiceManager.getService(IServiceConstants.LAUNCH_SERVICE);
	protected final IPUniverse universe = new PUniverse();

	public ModelManager() {
		fServiceManager.addEventListener(fServiceEventListener, IServiceModelEvent.SERVICE_CONFIGURATION_ADDED
				| IServiceModelEvent.SERVICE_CONFIGURATION_REMOVED | IServiceModelEvent.SERVICE_CONFIGURATION_CHANGED
				| IServiceModelEvent.SERVICE_PROVIDER_CHANGED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.core.IModelManager#addListener(org.eclipse.ptp.core.listeners
	 * .IResourceManagerListener)
	 */
	public void addListener(IResourceManagerListener listener) {
		resourceManagerListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.core.IModelManager#addResourceManager(org.eclipse.ptp
	 * .core.elementcontrols.IResourceManagerControl)
	 */
	public void addResourceManager(IResourceManagerControl rm) {
		synchronized (resourceManagers) {
			resourceManagers.put(rm.getUniqueName(), rm);
		}
		fireNewResourceManager(rm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.core.IModelManager#addResourceManagers(org.eclipse.ptp
	 * .core.elementcontrols.IResourceManagerControl[])
	 */
	public void addResourceManagers(IResourceManagerControl[] rms) {
		for (IResourceManagerControl rm : rms) {
			addResourceManager(rm);
		}
	}

	/**
	 * Fire an event to notify that a resource manager has changed
	 * 
	 * @param rm
	 *            rm that has changed
	 * @since 5.0
	 */
	public void fireResourceManagerChanged(IResourceManagerControl rm) {
		IResourceManagerChangedEvent e = new ResourceManagerChangedEvent(rm);

		for (Object listener : resourceManagerListeners.getListeners()) {
			((IResourceManagerListener) listener).handleEvent(e);
		}
	}

	/**
	 * Fire an event to notify that a resource manager error has occurred
	 * 
	 * @param rm
	 *            rm that caused the error
	 * @param message
	 *            error message
	 */
	public void fireResourceManagerError(IResourceManagerControl rm, String message) {
		IResourceManagerErrorEvent e = new ResourceManagerErrorEvent(rm, message);

		for (Object listener : resourceManagerListeners.getListeners()) {
			((IResourceManagerListener) listener).handleEvent(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.core.IModelManager#getResourceManagerFromUniqueName(java
	 * .lang.String)
	 */
	public IResourceManagerControl getResourceManagerFromUniqueName(String rmUniqueName) {
		synchronized (resourceManagers) {
			return resourceManagers.get(rmUniqueName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.core.IModelManager#getResourceManagers()
	 */
	public IResourceManagerControl[] getResourceManagers() {
		synchronized (resourceManagers) {
			return resourceManagers.values().toArray(new IResourceManagerControl[0]);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.core.IModelPresentation#getUniverse()
	 */
	public IPUniverse getUniverse() {
		return universe;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.core.elements.listeners.IResourceManagerListener#handleEvent
	 * (org.eclipse.ptp.core.elements.events.IResourceManagerErrorEvent)
	 */
	public void handleEvent(IResourceManagerErrorEvent e) {
		// Ignore - handled by listener on RM
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.core.IModelManager#loadResourceManagers()
	 */
	public void loadResourceManagers() throws CoreException {
		Set<IResourceManagerControl> rmsNeedStarting = new HashSet<IResourceManagerControl>();

		/*
		 * Need to force service model to load so that the resource managers are
		 * created.
		 */
		fServiceManager.getActiveConfiguration();

		synchronized (resourceManagers) {
			for (IResourceManagerControl rm : resourceManagers.values()) {
				if (rm.getConfiguration().getAutoStart()) {
					rmsNeedStarting.add(rm);
				}
			}
		}

		if (Preferences.getBoolean(PTPCorePlugin.getUniqueIdentifier(), PreferenceConstants.PREFS_AUTO_START_RMS)) {
			startResourceManagers(rmsNeedStarting.toArray(new IResourceManagerControl[0]));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.core.IModelManager#removeListener(org.eclipse.ptp.core
	 * .listeners.IResourceManagerListener)
	 */
	public void removeListener(IResourceManagerListener listener) {
		resourceManagerListeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.core.IModelManager#removeResourceManager(org.eclipse.
	 * ptp.core.elementcontrols.IResourceManagerControl)
	 */
	public void removeResourceManager(IResourceManagerControl rm) {
		IResourceManagerConfiguration rmConf = rm.getConfiguration();
		if (rmConf instanceof IServiceProvider) {
			removeProviderFromConfiguration((IServiceProvider) rmConf);
		}
		doRemoveResourceManager(rm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.core.IModelManager#removeResourceManagers(org.eclipse
	 * .ptp.rmsystem.IResourceManagerConfiguration[])
	 */
	public void removeResourceManagers(IResourceManagerConfiguration[] rms) {
		for (IResourceManagerConfiguration rmConf : rms) {
			IResourceManagerControl rm = getResourceManagerFromUniqueName(rmConf.getUniqueName());
			if (rm != null) {
				removeResourceManager(rm);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.core.IModelManager#saveResourceManagers()
	 */
	public void saveResourceManagers() {
		// No longer needed
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.core.IModelManager#shutdown()
	 */
	public void shutdown() throws CoreException {
		saveResourceManagers();
		stopResourceManagers();
		shutdownResourceManagers();
		resourceManagerListeners.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.core.IModelManager#start()
	 */
	public void start() throws CoreException {
		loadResourceManagers();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.core.IModelManager#stopResourceManagers()
	 */
	public void stopResourceManagers() throws CoreException {
		for (IResourceManagerControl resourceManager : getResourceManagers()) {
			resourceManager.stop();
		}
	}

	private void doRemoveResourceManager(IResourceManagerControl rm) {
		synchronized (resourceManagers) {
			resourceManagers.remove(rm.getUniqueName());
		}
		fireRemoveResourceManager(rm);
		rm.dispose();
	}

	/**
	 * Fire a new resource manager event.
	 * 
	 * @param rm
	 */
	private void fireNewResourceManager(final IResourceManagerControl rm) {
		IResourceManagerAddedEvent event = new ResourceManagerAddedEvent(this, rm);
		for (Object listener : resourceManagerListeners.getListeners()) {
			((IResourceManagerListener) listener).handleEvent(event);
		}
	}

	/**
	 * Fire a remove resource manager event.
	 * 
	 * @param rm
	 */
	private void fireRemoveResourceManager(final IResourceManagerControl rm) {
		IResourceManagerRemovedEvent event = new ResourceManagerRemovedEvent(this, rm);
		for (Object listener : resourceManagerListeners.getListeners()) {
			((IResourceManagerListener) listener).handleEvent(event);
		}
	}

	/**
	 * Remove provider from a service configurations
	 * 
	 * @param provider
	 *            provider to remove
	 */
	private void removeProviderFromConfiguration(IServiceProvider provider) {
		Set<IServiceConfiguration> configs = fServiceManager.getConfigurations();

		for (IServiceConfiguration config : configs) {
			if (provider == config.getServiceProvider(fLaunchService)) {
				config.setServiceProvider(fLaunchService, null);
				break;
			}
		}
	}

	/**
	 * shuts down all of the resource managers.
	 */
	private void shutdownResourceManagers() {
		for (IResourceManagerControl resourceManager : getResourceManagers()) {
			resourceManager.dispose();
		}
	}

	/**
	 * Start all resource managers.
	 * 
	 * @param rmsNeedStarting
	 * @throws CoreException
	 */
	private void startResourceManagers(IResourceManagerControl[] rmsNeedStarting) throws CoreException {
		for (final IResourceManagerControl rm : rmsNeedStarting) {
			Job job = new RMStartupJob(rm);
			job.schedule();
		}
	}
}