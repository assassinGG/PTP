/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    John Eblen - initial implementation
 *******************************************************************************/
package org.eclipse.ptp.rdt.sync.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ptp.rdt.sync.core.messages.Messages;
import org.eclipse.ptp.rdt.sync.core.serviceproviders.ISyncServiceProvider;
import org.eclipse.ptp.rdt.sync.core.services.IRemoteSyncServiceConstants;
import org.eclipse.ptp.services.core.IService;
import org.eclipse.ptp.services.core.IServiceConfiguration;
import org.eclipse.ptp.services.core.IServiceModelManager;
import org.eclipse.ptp.services.core.ServiceModelManager;
import org.eclipse.ptp.services.core.ServicesCorePlugin;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

public class SyncManager  {
	public static enum SYNC_MODE {
		ACTIVE, ALL, NONE
	};

	private static final IServiceModelManager serviceModel = ServiceModelManager.getInstance();
	private static final IService syncService = serviceModel.getService(IRemoteSyncServiceConstants.SERVICE_SYNC);
	private static final String DEFAULT_SAVE_FILE_NAME = "SyncManagerData.xml"; //$NON-NLS-1$
	private static final String SYNC_MANAGER_ELEMENT_NAME = "sync-manager-data"; //$NON-NLS-1$
	private static final String SYNC_MODE_ELEMENT_NAME = "project-to-sync-mode"; //$NON-NLS-1$
	private static final String SHOW_ERROR_ELEMENT_NAME = "project-to-show-error"; //$NON-NLS-1$
	private static final String ATTR_PROJECT_NAME = "project"; //$NON-NLS-1$
	private static final String ATTR_SYNC_MODE = "sync-mode"; //$NON-NLS-1$
	private static final String ATTR_SHOW_ERROR = "show-error"; //$NON-NLS-1$
	private static final String ATTR_AUTO_SYNC = "auto-sync"; //$NON-NLS-1$

	private static boolean fSyncAuto = true;
	private static final Map<IProject, SYNC_MODE> fProjectToSyncModeMap = Collections
			.synchronizedMap(new HashMap<IProject, SYNC_MODE>());
	private static final Map<IProject, Boolean> fProjectToShowErrorMap = Collections
			.synchronizedMap(new HashMap<IProject, Boolean>());

	static {
		try {
			loadConfigurationData();
		} catch (WorkbenchException e) {
			handleInitError(e);
		} catch (IOException e) {
			handleInitError(e);
		}
	}
	
	private static void handleInitError(Throwable e) {
		RDTSyncCorePlugin.log(Messages.SyncManager_1, e);
	}

	private static class SynchronizeJob extends Job {
		private final ISyncServiceProvider fSyncProvider;
		private final IResourceDelta fDelta;
		private final EnumSet<SyncFlag> fSyncFlags;
		private final SyncExceptionHandler fSyncExceptionHandler;

		public SynchronizeJob(IResourceDelta delta, ISyncServiceProvider provider, EnumSet<SyncFlag> syncFlags,
				SyncExceptionHandler seHandler) {
			super(Messages.SyncManager_4);
			fDelta = delta;
			fSyncProvider = provider;
			fSyncFlags = syncFlags;
			fSyncExceptionHandler = seHandler;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			SubMonitor progress = SubMonitor.convert(monitor, 100);
			try {
				fSyncProvider.synchronize(fDelta, progress.newChild(100), fSyncFlags);
			} catch (CoreException e) {
				if (fSyncExceptionHandler == null) {
					System.out.println(Messages.SyncManager_8 + e.getLocalizedMessage());
				} else {
					fSyncExceptionHandler.handle(e);
				}
			} finally {
				monitor.done();
			}
			return Status.OK_STATUS;
		}
	};

	/**
	 * Return project's current sync mode
	 * On first access, set sync mode to ACTIVE.
	 * 
	 * @param project
	 * @return sync mode. This is never null.
	 */
	public static SYNC_MODE getSyncMode(IProject project) {
		if (project == null) {
			throw new NullPointerException();
		}
		if (!(fProjectToSyncModeMap.containsKey(project))) {
			fProjectToSyncModeMap.put(project, SYNC_MODE.ACTIVE);
			try {
				saveConfigurationData();
			} catch (IOException e) {
				RDTSyncCorePlugin.log(Messages.SyncManager_2, e);
			}
		}
		return fProjectToSyncModeMap.get(project);
	}

	/**
	 * Should sync'ing be done automatically?
	 * 
	 * @return if sync'ing should be done automatically
	 */
	public static boolean getSyncAuto() {
		return fSyncAuto;
	}
	
	/**
	 * Should error messages be displayed for the given project?
	 * 
	 * @param project
	 * @return whether error messages should be displayed.
	 */
	public static boolean getShowErrors(IProject project) {
		if (project == null) {
			throw new NullPointerException();
		}
		if (!(fProjectToShowErrorMap.containsKey(project))) {
			fProjectToShowErrorMap.put(project, true);
			try {
				saveConfigurationData();
			} catch (IOException e) {
				RDTSyncCorePlugin.log(Messages.SyncManager_2, e);
			}
		}
		return fProjectToShowErrorMap.get(project);
	}

	/**
	 * Set sync mode for project
	 * 
	 * @param project
	 * @param mode
	 */
	public static void setSyncMode(IProject project, SYNC_MODE mode) {
		fProjectToSyncModeMap.put(project, mode);
		try {
			saveConfigurationData();
		} catch (IOException e) {
			RDTSyncCorePlugin.log(Messages.SyncManager_2, e);
		}
	}

	/**
	 * Turn automatic sync'ing on or off
	 * 
	 * @param isSyncAutomatic
	 */
	public static void setSyncAuto(boolean isSyncAutomatic) {
		fSyncAuto = isSyncAutomatic;
		try {
			saveConfigurationData();
		} catch (IOException e) {
			RDTSyncCorePlugin.log(Messages.SyncManager_2, e);
		}
	}
	
	/**
	 * Set whether error messages should be displayed
	 *
	 * @param project
	 * @param shouldBeDisplayed
	 */
	public static void setShowErrors(IProject project, boolean shouldBeDisplayed) {
		fProjectToShowErrorMap.put(project, shouldBeDisplayed);
		try {
			saveConfigurationData();
		} catch (IOException e) {
			RDTSyncCorePlugin.log(Messages.SyncManager_2, e);
		}
	}

	public void addHandlerListener(IHandlerListener handlerListener) {
		// Listeners not yet supported
	}

	public void dispose() {
		// Nothing to do
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isHandled() {
		return true;
	}

	public void removeHandlerListener(IHandlerListener handlerListener) {
		// Listeners not yet supported
	}

	/**
	 * Invoke sync for active (default) configuration on a project
	 * 
	 * @param delta
	 *            project delta
	 * @param project
	 *            project to sync
	 * @param syncFlags
	 *            sync flags
	 * @param seHandler
	 * 			  logic to handle exceptions
	 * @return the scheduled sync job
	 * @throws CoreException 
	 */
	public static Job sync(IResourceDelta delta, IProject project, EnumSet<SyncFlag> syncFlags, SyncExceptionHandler seHandler)
			throws CoreException {
		return sync(delta, project, syncFlags, false, seHandler, null);
	}
	
	/**
	 * Invoke sync and block until sync finishes. This does not spawn another thread and no locking of resources is done.
	 * 
	 * @param delta
	 *            project delta
	 * @param project
	 *            project to sync
	 * @param syncFlags
	 *            sync flags
	 * @return the scheduled sync job
	 * @throws CoreException
	 * 			  on problems sync'ing
	 */
	public static Job syncBlocking(IResourceDelta delta, IProject project, EnumSet<SyncFlag> syncFlags, IProgressMonitor monitor)
			throws CoreException {
		return sync(delta, project, syncFlags, true, null, monitor);
	}
	
	private static Job sync(IResourceDelta delta, IProject project, EnumSet<SyncFlag> syncFlags, boolean isBlocking,
			SyncExceptionHandler seHandler, IProgressMonitor monitor) throws CoreException {
		BuildConfigurationManager bcm = BuildConfigurationManager.getInstance();
		if (!(bcm.isInitialized(project))) {
			return null;
		}

		IConfiguration[] buildConfigurations = new IConfiguration[1];
		buildConfigurations[0] = ManagedBuildManager.getBuildInfo(project).getDefaultConfiguration();
		Job[] syncJobs = scheduleSyncJobs(delta, project, syncFlags, buildConfigurations, isBlocking, seHandler, monitor);
		return syncJobs[0];
	}

	/**
	 * Invoke sync for all configurations on a project.
	 * Note that there is no syncAllBlocking, because it was not needed but would be easy to add.
	 * 
	 * @param delta
	 *            project delta
	 * @param project
	 *            project to sync
	 * @param syncFlags
	 *            sync flags
	 * @param seHandler
	 *			  logic to handle exceptions
	 * @return array of sync jobs scheduled
	 * @throws CoreException
	 * 			  on problems sync'ing
	 */
	public static Job[] syncAll(IResourceDelta delta, IProject project, EnumSet<SyncFlag> syncFlags, SyncExceptionHandler seHandler)
			throws CoreException {
		BuildConfigurationManager bcm = BuildConfigurationManager.getInstance();
		if (!(bcm.isInitialized(project))) {
			return new Job[0];
		}

		return scheduleSyncJobs(delta, project, syncFlags, ManagedBuildManager.getBuildInfo(project).getManagedProject()
				.getConfigurations(), false, seHandler, null);
	}

	// Note that the monitor is ignored for non-blocking jobs since SynchronizeJob creates its own monitor
	private static Job[] scheduleSyncJobs(IResourceDelta delta, IProject project, EnumSet<SyncFlag> syncFlags,
			IConfiguration[] buildConfigurations, boolean isBlocking, SyncExceptionHandler seHandler, IProgressMonitor monitor)
			throws CoreException {
		int jobNum = 0;
		Job[] syncJobs = new Job[buildConfigurations.length];
		BuildConfigurationManager bcm = BuildConfigurationManager.getInstance();
		for (IConfiguration buildConfig : buildConfigurations) {
			SynchronizeJob job = null;
			IServiceConfiguration serviceConfig = bcm.getConfigurationForBuildConfiguration(buildConfig);
			if (serviceConfig != null) {
				ISyncServiceProvider provider = (ISyncServiceProvider) serviceConfig.getServiceProvider(syncService);
				if (provider != null) {
					if (isBlocking) {
						provider.synchronize(delta, monitor, syncFlags);
					} else {
						job = new SynchronizeJob(delta, provider, syncFlags, seHandler);
						job.schedule();
					}
				}
			} else {
				RDTSyncCorePlugin.log(Messages.SyncManager_7 + buildConfig.getName());
			}

			// Each build configuration is matched with a job, which may be null if a job could not be created.
			syncJobs[jobNum] = job;
			jobNum++;
		}

		return syncJobs;
	}

	/**
	 * Save configuration data to plugin metadata area
	 * 
	 * @throws IOException
	 *             on problems writing configuration data to file
	 */
	public static synchronized void saveConfigurationData() throws IOException {
		XMLMemento rootMemento = XMLMemento.createWriteRoot(SYNC_MANAGER_ELEMENT_NAME);

		// Save project to sync mode map
		synchronized (fProjectToSyncModeMap) {
			for (IProject project : fProjectToSyncModeMap.keySet()) {
				IMemento modeMemento = rootMemento.createChild(SYNC_MODE_ELEMENT_NAME);
				modeMemento.putString(ATTR_PROJECT_NAME, project.getName());
				modeMemento.putString(ATTR_SYNC_MODE, fProjectToSyncModeMap.get(project).name());
			}
		}
		
		// Save project to "show error" map
		synchronized (fProjectToShowErrorMap) {
			for (IProject project : fProjectToShowErrorMap.keySet()) {
				IMemento showErrorMemento = rootMemento.createChild(SHOW_ERROR_ELEMENT_NAME);
				showErrorMemento.putString(ATTR_PROJECT_NAME, project.getName());
				showErrorMemento.putBoolean(ATTR_SHOW_ERROR, fProjectToShowErrorMap.get(project));
			}
		}
		
		// Save auto-sync setting
		rootMemento.putBoolean(ATTR_AUTO_SYNC, fSyncAuto);

		IPath savePath = ServicesCorePlugin.getDefault().getStateLocation().append(DEFAULT_SAVE_FILE_NAME);
		File saveFile = savePath.toFile();
		rootMemento.save(new FileWriter(saveFile));
	}

	/**
	 * Load configuration data. All previously stored data is erased.
	 * 
	 * @throws IOException
	 */
	private static void loadConfigurationData() throws IOException, WorkbenchException {
		// Setup root memento
		IPath loadPath = ServicesCorePlugin.getDefault().getStateLocation().append(DEFAULT_SAVE_FILE_NAME);
		File loadFile = loadPath.toFile();
		if (!(loadFile.exists())) {
			return;
		}

		BufferedReader reader = new BufferedReader(new FileReader(loadFile));
		XMLMemento rootMemento;
		try {
			rootMemento = XMLMemento.createReadRoot(reader);
		} catch (WorkbenchException e) {
			throw e;
		}

		// Load project sync modes
		fProjectToSyncModeMap.clear();
		for (IMemento modeMemento : rootMemento.getChildren(SYNC_MODE_ELEMENT_NAME)) {
			String projectName = modeMemento.getString(ATTR_PROJECT_NAME);
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			if (project == null) {
				throw new RuntimeException(Messages.SyncManager_0 + project);
			}
			String syncModeString = modeMemento.getString(ATTR_SYNC_MODE);
			SYNC_MODE syncMode = SYNC_MODE.valueOf(syncModeString);
			fProjectToSyncModeMap.put(project, syncMode);
		}
		
		// Load project "show error" settings
		fProjectToShowErrorMap.clear();
		for (IMemento showErrorMemento : rootMemento.getChildren(SHOW_ERROR_ELEMENT_NAME)) {
			String projectName = showErrorMemento.getString(ATTR_PROJECT_NAME);
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			if (project == null) {
				throw new RuntimeException(Messages.SyncManager_0 + project);
			}
			fProjectToShowErrorMap.put(project, showErrorMemento.getBoolean(ATTR_SHOW_ERROR));
		}
		
		// Load auto-sync setting
		fSyncAuto = rootMemento.getBoolean(ATTR_AUTO_SYNC);
	}
}
