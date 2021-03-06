/*******************************************************************************
 * Copyright (c) 2006 The Regents of the University of California. 
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
package org.eclipse.ptp.core.elements;

import java.util.Collection;

import org.eclipse.ptp.core.elements.attributes.QueueAttributes;
import org.eclipse.ptp.core.elements.listeners.IQueueListener;
import org.eclipse.ptp.rmsystem.IResourceManager;

public interface IPQueue extends IPElement {
	/**
	 * Add a listener for events related to this queue.
	 * 
	 * @param listener
	 */
	public void addElementListener(IQueueListener listener);

	/**
	 * Add the collection of jobs to the queue.
	 * 
	 * @param jobs
	 *            collection of IJobControls
	 * @since 5.0
	 */
	public void addJobs(Collection<IPJob> jobs);

	/**
	 * Get the jobs in this queue. Returns an empty array if there are no jobs
	 * 
	 * @return array of jobs in this queue
	 */
	public IPJob[] getJobs();

	/**
	 * Get the resource manager that controls this queue.
	 * 
	 * @return IResourceManagerControl
	 * @since 5.0
	 */
	public IResourceManager getResourceManager();

	/**
	 * Get the internal state of the process.
	 * 
	 * @return process state
	 */
	public QueueAttributes.State getState();

	/**
	 * Remove a listener for events related to this queue.
	 * 
	 * @param listener
	 */
	public void removeElementListener(IQueueListener listener);

	/**
	 * Remove job from the queue
	 * 
	 * @param jobs
	 *            to remove
	 * @since 5.0
	 */
	public void removeJobs(Collection<IPJob> jobs);
}
