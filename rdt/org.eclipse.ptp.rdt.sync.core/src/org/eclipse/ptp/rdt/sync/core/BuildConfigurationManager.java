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

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.WriteAccessException;
import org.eclipse.cdt.core.settings.model.extension.CConfigurationData;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.Configuration;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.ptp.rdt.core.serviceproviders.IRemoteExecutionServiceProvider;
import org.eclipse.ptp.rdt.sync.core.messages.Messages;
import org.eclipse.ptp.rdt.sync.core.resources.RemoteSyncNature;
import org.eclipse.ptp.rdt.sync.core.serviceproviders.ISyncServiceProvider;
import org.eclipse.ptp.rdt.sync.core.services.IRemoteSyncServiceConstants;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteFileManager;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.services.core.IService;
import org.eclipse.ptp.services.core.IServiceConfiguration;
import org.eclipse.ptp.services.core.IServiceProvider;
import org.eclipse.ptp.services.core.ServiceModelManager;
import org.eclipse.ptp.services.core.ServiceProvider;
import org.eclipse.ui.XMLMemento;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Singleton that handles the storing of information about CDT build configurations. This includes the configuration's build
 * scenario (information on the sync point for the configuration), and its service configuration. New projects should call
 * "initProject" and specify a template service configuration and a build scenario, which is assigned to all existing
 * configurations. The template service configuration should specify all non-sync services. (Normally, the project's active
 * configuration should be used.) This template is copied as needed to create service configurations for build configurations.
 */
public class BuildConfigurationManager {
	private static final String projectScopeSyncNode = "org.eclipse.ptp.rdt.sync.core"; //$NON-NLS-1$
	private static final String CONFIG_NODE_NAME = "config"; //$NON-NLS-1$
	private static final String TEMPLATE_KEY = "template"; //$NON-NLS-1$
	private final Map<IConfiguration, IServiceConfiguration> fBConfigToSConfigMap = Collections
			.synchronizedMap(new HashMap<IConfiguration, IServiceConfiguration>());
	
	// Setup as a singleton
	private BuildConfigurationManager() {
	}

	private static BuildConfigurationManager fInstance = null;

	/**
	 * Get the single BuildConfigurationManager instance
	 * @return instance
	 */
	public static synchronized BuildConfigurationManager getInstance() {
		if (fInstance == null) {
			fInstance = new BuildConfigurationManager();
		}
		return fInstance;
	}

	/**
	 * Create a build scenario for configurations that build in the local Eclipse workspace.
	 * This function makes no changes to the internal data structures and is of little value for most clients.
	 * 
	 * @param project - cannot be null
	 * @return the build scenario - never null
	 * @throws CoreException
	 *             on problems getting local resources, either the local connection or local services
	 */
	public BuildScenario createLocalBuildScenario(IProject project) throws CoreException {
		IRemoteServices localService = PTPRemoteCorePlugin.getDefault().getRemoteServices(
				"org.eclipse.ptp.remote.LocalServices", null); //$NON-NLS-1$

		if (localService != null) {
			IRemoteConnection localConnection = localService.getConnectionManager().getConnection("Local"); //$NON-NLS-1$
			if (localConnection != null) {
				return new BuildScenario(null, localConnection, project.getLocation().toString());
			} else {
				throw new CoreException(new Status(IStatus.ERROR, "org.eclipse.ptp.rdt.sync.core", //$NON-NLS-1$
						Messages.BCM_LocalConnectionError));
			}
		} else {
			throw new CoreException(new Status(IStatus.ERROR, "org.eclipse.ptp.rdt.sync.core", Messages.BCM_LocalServiceError)); //$NON-NLS-1$
		}
	}

	/**
	 * Create a local build configuration. The corresponding build scenario has no sync provider and points to the project's working
	 * directory. It has a default name and description. This function is normally used as part of the setup when creating a new project
	 * and is of little value to most clients.
	 * 
	 * @param project
	 *            The project needing a local configuration - cannot be null
	 * @return the new configuration - can be null on problems during creation
	 */
	public IConfiguration createLocalConfiguration(IProject project) {
		checkProject(project);
		try {
			BuildScenario localBuildScenario = this.createLocalBuildScenario(project);
			if (localBuildScenario != null) {
				return this.createConfiguration(project, localBuildScenario, Messages.WorkspaceConfigName,
						Messages.BCM_WorkspaceConfigDes);
			}
		} catch (CoreException e) {
			RDTSyncCorePlugin.log(Messages.BCM_CreateConfigFailure + e.getMessage(), e);
		}

		return null;
	}

	/**
	 * Create a remote build configuration. This function is mainly used for internal sync purposes and is of little value to most
	 * clients.
	 * 
	 * @param project
	 *            The project needing a remote configuration - cannot be null
	 * @param remoteBuildScenario
	 *            Configuration's build scenario - cannot be null
	 * @param configName
	 *            Configuration's name
	 * @param configDesc
	 *            Configuration's description
	 * @return the new configuration - can be null on problems during creation
	 */
	public IConfiguration createRemoteConfiguration(IProject project, BuildScenario remoteBuildScenario, String configName,
			String configDesc) {
		checkProject(project);
		return this.createConfiguration(project, remoteBuildScenario, configName, configDesc);
	}

	/**
	 * Get the synchronize location URI of the resource associated with the active build configuration. Returns null if the project
	 * containing the resource is not a synchronized project.
	 * 
	 * @param resource
	 *            target resource - cannot be null
	 * @return URI or null if not a sync project
	 * @throws CoreException
	 */
	public URI getActiveSyncLocationURI(IResource resource) throws CoreException {
		if (resource.getProject().hasNature(RemoteSyncNature.NATURE_ID)) {
			IConfiguration configuration = ManagedBuildManager.getBuildInfo(resource.getProject()).getDefaultConfiguration();
			return getSyncLocationURI(configuration, resource);
		}
		return null;
	}

	/**
	 * Returns the service configuration set for the given build configuration, or null if it is unavailable.
	 * 
	 * @param bconf
	 *            The build configuration - cannot be null
	 * @return service configuration for the build configuration
	 */
	public IServiceConfiguration getConfigurationForBuildConfiguration(IConfiguration bconf) {
		IProject project = bconf.getOwner().getProject();
		checkProject(project);
		IServiceConfiguration sconf = fBConfigToSConfigMap.get(bconf);
		if (sconf == null) {
			BuildScenario bs = this.getBuildScenarioForBuildConfiguration(bconf);
			// Should never happen, but if it does do not continue. (Function call should have invoked error handling.)
			if (bs == null) {
				return null;
			}
            sconf = copyTemplateServiceConfiguration(project);
            modifyServiceConfigurationForBuildScenario(sconf, bs);
            fBConfigToSConfigMap.put(bconf, sconf);
		}
		
		return sconf;
	}

	/**
	 * Return the name of the sync provider for this project, as stored in the project's template service configuration.
	 * 
	 * @param project - cannot be null
	 * @return sync provider name or null if provider cannot be loaded (should not normally happen)
	 */
	public String getProjectSyncProvider(IProject project) {
		checkProject(project);
		String serviceConfigId = getTemplateServiceConfigurationId(project);
		IServiceConfiguration serviceConfig = ServiceModelManager.getInstance().getConfiguration(serviceConfigId);
		if (serviceConfig == null) {
			RDTSyncCorePlugin.log(Messages.BuildConfigurationManager_10 + serviceConfigId + Messages.BuildConfigurationManager_11 + project.getName());
			return null;
		}

		IService syncService = ServiceModelManager.getInstance().getService(IRemoteSyncServiceConstants.SERVICE_SYNC);
		if (syncService == null) {
			RDTSyncCorePlugin.log(Messages.BuildConfigurationManager_12);
			return null;
		}
		
		IServiceProvider provider = serviceConfig.getServiceProvider(syncService);
		if (provider == null) {
			RDTSyncCorePlugin.log(Messages.BuildConfigurationManager_13 + project.getName());
			return null;
		}

		return provider.getName();
	}

	/**
	 * Get the synchronize location URI of the resource associated with the build configuration. Returns null if the configuration
	 * does not contain synchronization information or no connection has been configured.
	 * 
	 * @param configuration
	 *            build configuration with sync provider
	 * @param resource
	 *            target resource
	 * @return URI or null if not a sync configuration
	 * @throws CoreException
	 */
	public URI getSyncLocationURI(IConfiguration configuration, IResource resource) throws CoreException {
		// Project checked inside this function call
		BuildScenario scenario = getBuildScenarioForBuildConfiguration(configuration);
		if (scenario != null) {
			IPath path = new Path(scenario.location).append(resource.getProjectRelativePath());
			IRemoteConnection conn = scenario.getRemoteConnection();
			if (conn != null) {
				IRemoteFileManager fileMgr = scenario.getRemoteConnection().getRemoteServices().getFileManager(conn);
				return fileMgr.toURI(path);
			}
		}
		return null;
	}

	/**
	 * Initialize a project. Set the project's template service configuration to the passed configuration and set all current build
	 * configurations to use the passed build scenario. This function must be called before any calls to get or set methods.
	 * 
	 * The template service configuration is the one that is copied and modified to create a custom configuration for each build
	 * configuration.
	 * 
	 * @param project - cannot be null
	 * @param sc - the service configuration - cannot be null
	 * @param bs - the build scenario - cannot be null
	 */
	public void initProject(IProject project, IServiceConfiguration sc, BuildScenario bs) {
		if (project == null || sc == null || bs == null) {
			throw new NullPointerException();
		}
		
		// Cannot call "checkProject" because project not yet initialized
		try {
			if (!project.hasNature(RemoteSyncNature.NATURE_ID)) {
				throw new IllegalArgumentException(Messages.BuildConfigurationManager_6);
			}
		} catch (CoreException e) {
			throw new IllegalArgumentException(Messages.BuildConfigurationManager_8);
		}

		IScopeContext context = new ProjectScope(project);
		Preferences node = context.getNode(projectScopeSyncNode);
		if (node == null) {
			throw new RuntimeException(Messages.BuildConfigurationManager_0);
		}
		node.put(TEMPLATE_KEY, sc.getId());
		try {
			node.flush();
		} catch (BackingStoreException e) {
			RDTSyncCorePlugin.log(Messages.BuildConfigurationManager_1, e);
		}

		IManagedBuildInfo buildInfo = ManagedBuildManager.getBuildInfo(project);
		if (buildInfo == null) {
			throw new RuntimeException(Messages.BCM_BuildInfoError + project.getName());
		}
		
		IConfiguration[] allConfigs = buildInfo.getManagedProject().getConfigurations();
		for (IConfiguration config : allConfigs) {
			setBuildScenarioForBuildConfigurationInternal(bs, config);
		}
	}

	/**
	 * Indicate if the project has yet been initialized.
	 * 
	 * @param project - cannot be null
	 * @return whether or not the project has been initialized
	 */
	public boolean isInitialized(IProject project) {
		if (project == null) {
			throw new NullPointerException();
		}
		if (getTemplateServiceConfigurationId(project) == null) {
			return false;
		} else {
			return true;
		}
	}

	// Does the low-level work of creating a copy of a service configuration
	// Returned configuration is never null
	private IServiceConfiguration copyTemplateServiceConfiguration(IProject project) {
		IServiceConfiguration newConfig = ServiceModelManager.getInstance().newServiceConfiguration(""); //$NON-NLS-1$
		if (newConfig == null) {
			throw new RuntimeException(Messages.BuildConfigurationManager_15);
		}
		String oldConfigId = getTemplateServiceConfigurationId(project);
		IServiceConfiguration oldConfig = ServiceModelManager.getInstance().getConfiguration(oldConfigId);
		if (oldConfig == null) {
			RDTSyncCorePlugin.log(Messages.BuildConfigurationManager_10 + oldConfigId + Messages.BuildConfigurationManager_11 + project.getName());
			return null;
		}

		for (IService service : oldConfig.getServices()) {
			ServiceProvider oldProvider = (ServiceProvider) oldConfig.getServiceProvider(service);
			try {
				// The memento creation methods seem the most robust way to copy state. It is more robust than getProperties() and
				// setProperties(), which saveState() and restoreState() use by default but which can be overriden by subclasses.
				ServiceProvider newProvider = oldProvider.getClass().newInstance();
				XMLMemento oldProviderState = XMLMemento.createWriteRoot("provider"); //$NON-NLS-1$
				oldProvider.saveState(oldProviderState);
				newProvider.restoreState(oldProviderState);
				newConfig.setServiceProvider(service, newProvider);
			} catch (InstantiationException e) {
				throw new RuntimeException(Messages.BCM_ProviderError + oldProvider.getClass());
			} catch (IllegalAccessException e) {
				throw new RuntimeException(Messages.BCM_ProviderError + oldProvider.getClass());
			}
		}

		return newConfig;
	}

	private IConfiguration createConfiguration(IProject project, BuildScenario buildScenario, String configName, String configDesc) {
		IManagedBuildInfo buildInfo = ManagedBuildManager.getBuildInfo(project);
		if (buildInfo == null) {
			throw new RuntimeException(Messages.BCM_BuildInfoError + project.getName());
		}

		// For recording of problems during attempt
		Throwable creationException = null;
		String creationError = null;
		boolean configAdded = false;

		ManagedProject managedProject = (ManagedProject) buildInfo.getManagedProject();
		Configuration configParent = (Configuration) buildInfo.getDefaultConfiguration();
		String configId = ManagedBuildManager.calculateChildId(configParent.getId(), null);
		Configuration config = new Configuration(managedProject, configParent, configId, true, false);
		CConfigurationData configData = config.getConfigurationData();
		ICProjectDescription projectDes = CoreModel.getDefault().getProjectDescription(project);
		ICConfigurationDescription configDes = null;
		try {
			configDes = projectDes.createConfiguration(ManagedBuildManager.CFG_DATA_PROVIDER_ID, configData);
		} catch (WriteAccessException e) {
			creationException = e;
		} catch (CoreException e) {
			creationException = e;
		}

		if (configDes != null) {
			config.setConfigurationDescription(configDes);
			configDes.setName(configName);
			configDes.setDescription(configDesc);
			config.getToolChain().getBuilder().setBuildPath(project.getLocation().toString());
			configAdded = true;
			try {
				CoreModel.getDefault().setProjectDescription(project, projectDes, true, null);
			} catch (CoreException e) {
				projectDes.removeConfiguration(configDes);
				configAdded = false;
				creationException = e;
				creationError = Messages.BCM_SetConfigDescriptionError;
			}
			if (configAdded) {
				this.setBuildScenarioForBuildConfigurationInternal(buildScenario, config);
			}
		} else {
			creationError = Messages.BCM_CreateConfigError;
		}

		if (!configAdded) {
			if (creationError == null && creationException != null) {
				creationError = creationException.getMessage();
			}
			RDTSyncCorePlugin.log(Messages.BCM_CreateConfigFailure + creationError, creationException);
			return null;
		}

		return config;
	}

	// Set all unknown configurations (those without a build scenario) to use the build scenario of their closest ancestor
	private void updateConfigurations(IProject project) {
		IManagedBuildInfo buildInfo = ManagedBuildManager.getBuildInfo(project);
		if (buildInfo == null) {
			throw new RuntimeException(Messages.BCM_BuildInfoError + project.getName());
		}

		IConfiguration[] allConfigs = buildInfo.getManagedProject().getConfigurations();
		for (IConfiguration config : allConfigs) {
			BuildScenarioAndConfiguration parentConfigInfo = getBuildScenarioForBuildConfigurationInternal(config);
			if (parentConfigInfo == null) {
				return; // Errors handled by prior function call
			}
			if (parentConfigInfo.configId == config.getId()) {
				continue;
			}
			IConfiguration parentConfig = buildInfo.getManagedProject().getConfiguration(parentConfigInfo.configId);
			if (parentConfig != null) {
				setBuildScenarioForBuildConfigurationInternal(parentConfigInfo.bs, config);
			} else {
				RDTSyncCorePlugin.log(Messages.BuildConfigurationManager_10 + parentConfigInfo.configId +
						Messages.BuildConfigurationManager_11 + project.getName());
			}
		}
	}

	// Does the low-level work of changing a service configuration for a new build scenario.
	private void modifyServiceConfigurationForBuildScenario(IServiceConfiguration sConfig, BuildScenario bs) {
		IService syncService = null; // Only set if sync service should be disabled
		for (IService service : sConfig.getServices()) {
			ServiceProvider provider = (ServiceProvider) sConfig.getServiceProvider(service);
			if (provider instanceof IRemoteExecutionServiceProvider) {
				// For local configuration, for example, that does not need to sync
				if (provider instanceof ISyncServiceProvider && bs.getSyncProvider() == null) {
					syncService = service;
				} else {
					((IRemoteExecutionServiceProvider) provider).setRemoteToolsConnection(bs.getRemoteConnection());
					((IRemoteExecutionServiceProvider) provider).setConfigLocation(bs.getLocation());

				}
			}
		}
		if (syncService != null) {
			sConfig.disable(syncService);
		}
	}
	
	// Return ID of the project's template service configuration, or null if not found (project not initialized)
	// Returned value is never null
	private static String getTemplateServiceConfigurationId(IProject project) {
		IScopeContext context = new ProjectScope(project);
		Preferences node = context.getNode(projectScopeSyncNode);
		if (node == null) {
			throw new RuntimeException(Messages.BuildConfigurationManager_0);
		}
		
		String configId = node.get(TEMPLATE_KEY, null);
		if (configId == null) {
			throw new RuntimeException(Messages.BuildConfigurationManager_9);
		}
		
		return configId;
	}
	
	/**
	 * Return the build scenario for the passed configuration. Any newly created configurations should be recorded by the call to
	 * "updateConfigurations." For configurations still unknown (perhaps newly created configurations not yet recorded in CDT),
	 *  return the build scenario for the closest known ancestor.
	 * 
	 * @param bconf - the build configuration - cannot be null
	 * @return build scenario or null if there are problems accessing configuration's information
	 */
	public BuildScenario getBuildScenarioForBuildConfiguration(IConfiguration bconf) {
		IProject project = bconf.getOwner().getProject();
		checkProject(project);
		updateConfigurations(project);
		return this.getBuildScenarioForBuildConfigurationInternal(bconf).bs;
	}
	
	// Simple class for bundling a build scenario with its configuration id
	private class BuildScenarioAndConfiguration {
		public final BuildScenario bs;
		public final String configId;
		
		BuildScenarioAndConfiguration(BuildScenario scenario, String configuration) {
			bs = scenario;
			configId = configuration;
		}
	}

	// Return the build scenario stored for the passed id or the build scenario of its nearest ancestor.
	// Return null if not found.
	private BuildScenarioAndConfiguration getBuildScenarioForBuildConfigurationInternal(IConfiguration bconf) {
		IProject project = bconf.getOwner().getProject();
		IScopeContext context = new ProjectScope(project);
		Preferences prefRootNode = context.getNode(projectScopeSyncNode);
		if (prefRootNode == null) {
			RDTSyncCorePlugin.log(Messages.BuildConfigurationManager_0);
			return null;
		}

		try {
			if (!prefRootNode.nodeExists(CONFIG_NODE_NAME)) {
				throw new RuntimeException(Messages.BuildConfigurationManager_0);
			}
			
			String configId = bconf.getId();
			Preferences prefGeneralConfigNode = prefRootNode.node(CONFIG_NODE_NAME);
			while (configId != null && !prefGeneralConfigNode.nodeExists(configId)) {
				configId = getParentId(configId);
			}
			
			if (configId != null) {
				BuildScenario bs = BuildScenario.loadScenario(prefGeneralConfigNode.node(configId));
				if (bs == null) {
					RDTSyncCorePlugin.log(Messages.BuildConfigurationManager_14 + configId + Messages.BuildConfigurationManager_11
							+ project.getName());
					return null;
				} else {
					return new BuildScenarioAndConfiguration(bs, configId);
				}
			} else {
				return null;
			}
		} catch (BackingStoreException e) {
			RDTSyncCorePlugin.log(Messages.BuildConfigurationManager_2, e);
			return null;
		}
	}
	
	// Each new configuration id appends a number to the parent id. So we strip off the last id number to get the parent. We assume
	// the configuration does not have a parent and return null if the result does not end with a number.
	private static String getParentId(String configId) {
		String idRegEx = "\\.\\d+$"; //$NON-NLS-1$
		Pattern idPattern = Pattern.compile(idRegEx);
		String parentConfigId = configId.replaceFirst(idRegEx, ""); //$NON-NLS-1$

		if (idPattern.matcher(parentConfigId).find()) {
			return parentConfigId;
		}

		return null;
	}

	/**
	 * Associate the given configuration with the given build scenario.
	 * 
	 * @param buildScenario - cannot be null
	 * @param bconf - the build configuration - cannot be null
	 */
	public void setBuildScenarioForBuildConfiguration(BuildScenario bs, IConfiguration bconf) {
		if (bs == null) {
			throw new NullPointerException();
		}
		IProject project = bconf.getOwner().getProject();
		checkProject(project);
		// Update so that unknown children of the given configuration are set properly to use the previous build scenario
		updateConfigurations(project);
		this.setBuildScenarioForBuildConfigurationInternal(bs, bconf);
	}
	
	private void setBuildScenarioForBuildConfigurationInternal(BuildScenario bs, IConfiguration bconf) {
		IProject project = bconf.getOwner().getProject();

		IScopeContext context = new ProjectScope(project);
		Preferences prefRootNode = context.getNode(projectScopeSyncNode);
		if (prefRootNode == null) {
			throw new RuntimeException(Messages.BuildConfigurationManager_0);
		}

		Preferences prefConfigNode = prefRootNode.node(CONFIG_NODE_NAME + "/" + bconf.getId()); //$NON-NLS-1$
		bs.saveScenario(prefConfigNode);
		try {
			prefRootNode.flush();
		} catch (BackingStoreException e) {
			RDTSyncCorePlugin.log(Messages.BuildConfigurationManager_2, e);
		}
		
		IServiceConfiguration sconf = fBConfigToSConfigMap.get(bconf);
		if (sconf != null) {
			modifyServiceConfigurationForBuildScenario(sconf, bs);
		}
	}
	
	// Run standard checks on project and throw the appropriate exception if it is not valid
	// All public methods should call this for any passed project or any passed configuration's project.
	// Private methods assume projects have been checked.
	private void checkProject(IProject project) {
		try {
			if (!project.hasNature(RemoteSyncNature.NATURE_ID)) {
				throw new IllegalArgumentException(Messages.BuildConfigurationManager_6);
			}
		} catch (CoreException e) {
			throw new IllegalArgumentException(Messages.BuildConfigurationManager_8);
		}
		if (!isInitialized(project)) {
			throw new RuntimeException(Messages.BuildConfigurationManager_7);
		}
	}
}