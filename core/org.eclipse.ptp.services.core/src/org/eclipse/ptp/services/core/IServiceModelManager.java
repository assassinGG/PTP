/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.ptp.services.core;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;

public interface IServiceModelManager extends IAdaptable {
	/**
	 * Associate the service configuration with a project. A project can have multiple
	 * service configurations. The service configuration will become the active
	 * configuration for the project.
	 * 
	 * @param project the project
	 * @param conf the configuration
	 * 
	 * @throws NullPointerException if project or conf is null
	 */
	public void addConfiguration(IProject project, IServiceConfiguration conf);
	
	/**
	 * Adds the given service configuration to the model without explicitly
	 * associating it with a particular project. It may be associated with a project
	 * later using the addConfiguration(IProject, IServiceConfiguration) method.
	 * 
	 * @param project the project
	 * @param conf the configuration
	 * 
	 * @throws NullPointerException if conf is null
	 */
	public void addConfiguration(IServiceConfiguration conf);
	
	/**
	 * Adds the given listener for service model events. Has no effect if an 
	 * identical listener is already registered. 
	 * <p>
	 * Listeners can listen for several types of event as defined in
	 * <code>IServiceModelEvent</code>. Clients are free to register for
	 * any number of event types. Clients are guaranteed to only receive
	 * event types for which they are registered.
	 * </p>
	 * 
	 * @param listener the listener
	 * @param eventMask the bit-wise OR of all event types of interest to the
	 * listener
	 * @see IServiceModelEventListener
	 * @see IServiceModelEvent
	 * @see #removeEventListener(IServiceModelEventListener)
	 */
	public void addEventListener(IServiceModelEventListener listener, int type);
	
	/**
	 * Export a set of service configurations to a file. 
	 * 
	 * @param file file name used to save the configuration
	 * @return service configurations to export
	 * @throws InvocationTargetException wraps any exceptions thrown during export
	 */
	public boolean exportConfigurations(String filename, IServiceConfiguration[] configs) throws InvocationTargetException;
	
	/**
	 * Get the global "active" service configuration. The "active" configuration is an
	 * arbitrary configuration can be used by tools that do not operate on a project basis.
	 * 
	 * @return active service configuration, or null if no active configuration has been set
	 */
	public IServiceConfiguration getActiveConfiguration();
	
	/**
	 * Get the configuration that is currently active for the project. Each project has
	 * exactly one active configuration, which describes the mapping from services to
	 * service providers. By default, the first configuration created for a project will 
	 * be the active configuration for that project.
	 * 
	 * @param project project for which the configuration will be obtained
	 * @return the service configuration for this project
	 * 
	 * @throws NullPointerException if project is null
	 * @throws ProjectNotConfiguredException if the project has not been configured
	 */
	public IServiceConfiguration getActiveConfiguration(IProject project);
	
	/**
	 * Returns all the service categories that have been registered with
	 * the system.
	 * @return
	 */
	public Set<IServiceCategory> getCategories();
	
	/**
	 * Get the named configuration for this project.
	 * 
	 * @param project project for which the configuration will be obtained
	 * @param name name of the configuration
	 * @return the service configuration or null if no configurations with the supplied name exist
	 * 
	 * @throws NullPointerException if project is null
	 * @throws ProjectNotConfiguredException if the project has not been configured
	 */
	public IServiceConfiguration getConfiguration(IProject project, String name);
	
	/**
	 * Get the configuration with the specified ID.
	 * 
	 * @param id ID of the configuration
	 * @return the service configuration or null if no configurations with the supplied ID exist
	 */
	public IServiceConfiguration getConfiguration(String id);
	
	/**
	 * Get all configurations available in the workspace.
	 * 
	 * @return all configurations that could be found, or an empty set
	 */
	public Set<IServiceConfiguration> getConfigurations();
	
	/**
	 * Get all the configurations that are known by the project
	 * 
	 * @param project project containing the configurations
	 * @return set of configurations known by the project
	 * 
	 * @throws NullPointerException if project is null
	 * @throws ProjectNotConfiguredException if the project has not been configured
	 */
	public Set<IServiceConfiguration> getConfigurations(IProject project);
	
	/**
	 * Get the set of projects which use the specified service configuration
	 * 
	 * @param serviceConfiguration Service configuration to query set of projects using it
	 * @return Set of projects using the service configuration
	 */
	public Set<IProject> getProjectsForConfiguration(IServiceConfiguration serviceConfiguration);
	
	/**
	 * Retrieves the service corresponding to a given id.
	 * 
	 * @param id The unique id of the service to retrieve.
	 * @return IService or null
	 */
	public IService getService(String id);
	
	/**
	 * Return a new instance of a service provider based on the descriptor.
	 * 
	 * @param desc extension description
	 * @return service provider
	 */
	public IServiceProvider getServiceProvider(IServiceProviderDescriptor desc);
	
	/**
	 * Get all the services that have been registered with the system.
	 * 
	 * @return a set of all the services
	 */
	public Set<IService> getServices();
	
	/**
	 * Get all the services that are used by a particular project.
	 * 
	 * @param project project using the services
	 * @return set of services
	 * 
	 * @throws NullPointerException if project is null
	 * @throws ProjectNotConfiguredException if the project has not been configured
	 */
	public Set<IService> getServices(IProject project);
	
	/**
	 * Get all the services that are associated with a project nature.
	 * 
	 * @param nature project nature
	 * @return set of services or null
	 */
	public Set<IService> getServices(String natureID);
	
	/**
	 * Import a set of service configurations from a file. The configurations
	 * must be added to the model using {@link #addConfiguration(IServiceConfiguration)}
	 * before they can be used. No model events will be generated while loading
	 * the configurations.
	 * 
	 * @param file file name containing the configuration
	 * @return imported service configurations
	 * @throws InvocationTargetException wraps any exceptions thrown during import
	 */
	public IServiceConfiguration[] importConfigurations(String filename) throws InvocationTargetException;

	/**
	 * Returns true if the given project has a configuration.
	 * 
	 */
	public boolean isConfigured(IProject project);
	
	/**
	 * Validate a set of service configurations in a file
	 * 
	 * @param file file name of the file containing the configurations
	 * @return true if valid, false otherwise
	 */
	public boolean isValidConfigurationFile(String filename);

	/**
	 * Obtain a new service configuration with name 'name'. The name
	 * does not need to be unique. This service configuration will not
	 * become part of the service model until it is passed to 
	 * one of the addConfiguration() methods.
	 * 
	 * @param name name of service configuration
	 * @return new service configuration
	 */
	public IServiceConfiguration newServiceConfiguration(String name);

	/**
	 * Remaps all the configurations and services associated to the removed project to the added project.
	 * 
	 * @param removedProject project removed from workspace
	 * @param addedProject project added to workspace
	 * @since 2.0
	 */
	public void remap(IProject removedProject, IProject addedProject);
	
	/**
	 * Removes all the configurations and services associated to the given project.
	 * If the project has not been configured then this method does nothing.
	 * 
	 * @throws NullPointerException if project is null
	 */
	public void remove(IProject project);
	
	/**
	 * Removes the service configuration.
	 * 
	 * @param conf the configuration
	 */
	public void remove(IServiceConfiguration conf);
	
	/**
	 * TODO What happens if you try to remove the active configuration?
	 * TODO What happens if there are no configurations left after removing the given configuration?
	 * 
	 * Remove the service configuration from a project.
	 * If the configuration was not set up on the project then this method
	 * does nothing.
	 * 
	 * @param project the project
	 * @param conf the configuration
	 * 
	 * @throws NullPointerException if project or conf is null
	 * @throws ProjectNotConfiguredException if the project has not been configured
	 */
	public void removeConfiguration(IProject project, IServiceConfiguration conf);
	
	/**
	 * Removes the given listener for service model events. Has no effect if the 
	 * listener is not registered. 
	 * <p>
	 * @param listener the listener
	 * @see IServiceModelEventListener
	 * @see IServiceModelEvent
	 * @see #addEventListener(IServiceModelEventListener)
	 */
	public void removeEventListener(IServiceModelEventListener listener);
	
	/**
	 * Set the active configuration for a project. By default, the first configuration created
	 * for a project will be the active configuration for that project.
	 * 
	 * @param project project for which the configuration will be obtained
	 * @param configuration configuration to set as active for this project
	 * 
	 * @throws NullPointerException if project or configuration is null
	 * @throws ProjectNotConfiguredException if the project has not been configured yet
	 * @throws IllegalArgumentException if the configuration was not part of the project
	 */
	public void setActiveConfiguration(IProject project, IServiceConfiguration configuration);
	
	/**
	 * Set the global "active" service configuration. The "active" configuration is an
	 * arbitrary configuration can be used by tools that do not operate on a project basis.
	 * 
	 * @param config the service configuration to select as active
	 */
	public void setActiveConfiguration(IServiceConfiguration config);
}
