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
package org.eclipse.ptp.internal.core.elements;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.ptp.core.attributes.EnumeratedAttribute;
import org.eclipse.ptp.core.attributes.IAttribute;
import org.eclipse.ptp.core.attributes.IllegalValueException;
import org.eclipse.ptp.core.elementcontrols.IPElementControl;
import org.eclipse.ptp.core.elementcontrols.IPJobControl;
import org.eclipse.ptp.core.elementcontrols.IPProcessControl;
import org.eclipse.ptp.core.elementcontrols.IPQueueControl;
import org.eclipse.ptp.core.elements.IPProcess;
import org.eclipse.ptp.core.elements.IPQueue;
import org.eclipse.ptp.core.elements.attributes.JobAttributes;
import org.eclipse.ptp.core.elements.attributes.JobAttributes.State;
import org.eclipse.ptp.core.elements.events.IJobChangedEvent;
import org.eclipse.ptp.core.elements.events.IJobNewProcessEvent;
import org.eclipse.ptp.core.elements.events.IJobRemoveProcessEvent;
import org.eclipse.ptp.core.elements.listeners.IJobListener;
import org.eclipse.ptp.core.elements.listeners.IJobProcessListener;
import org.eclipse.ptp.internal.core.elements.events.JobChangedEvent;
import org.eclipse.ptp.internal.core.elements.events.JobNewProcessEvent;
import org.eclipse.ptp.internal.core.elements.events.JobRemoveProcessEvent;

public class PJob extends Parent implements IPJobControl {
	final public static int BASE_OFFSET = 10000;
	final public static int STATE_NEW = 5000;

	private final ListenerList elementListeners = new ListenerList();
	private final ListenerList childListeners = new ListenerList();
	private HashMap<String, IPProcessControl> numberMap = 
		new HashMap<String, IPProcessControl>();
	private boolean isDebugJob = false;

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.internal.core.elements.PElement#doAddAttributeHook(org.eclipse.ptp.core.attributes.IAttribute[])
	 */
	@Override
	protected void doAddAttributeHook(List<IAttribute> attribs) {
		fireChangedJob(attribs);
	}

	public PJob(String id, IPQueueControl queue, IAttribute[] attrs) {
		super(id, queue, P_JOB, attrs);
		/*
		 * Make sure we always have a state.
		 */
		EnumeratedAttribute jobState = (EnumeratedAttribute) getAttribute(JobAttributes.getStateAttributeDefinition());
		if (jobState == null) {
			try {
				jobState = JobAttributes.getStateAttributeDefinition().create();
				addAttribute(jobState);
			} catch (IllegalValueException e) {
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.elements.IPJob#addChildListener(org.eclipse.ptp.core.elements.listeners.IJobProcessListener)
	 */
	public void addChildListener(IJobProcessListener listener) {
		childListeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.elements.IPJob#addElementListener(org.eclipse.ptp.core.elements.listeners.IJobListener)
	 */
	public void addElementListener(IJobListener listener) {
		elementListeners.add(listener);
	}
	
	public void addProcess(IPProcessControl p) {
		addChild(p);
		String num = p.getProcessNumber();
		if (num != null) {
			numberMap.put(num, p);
		}
		fireNewProcess(p);
	}
	
	public synchronized IPProcess getProcessById(String id) {
		IPElementControl element = findChild(id);
		if (element != null)
			return (IPProcessControl) element;
		return null;
	}

	public synchronized IPProcess getProcessByNumber(String number) {
		return numberMap.get(number);
	}

	/*
	 * returns all the processes in this job, which are the children of the job
	 */
	public synchronized IPProcessControl[] getProcessControls() {
		return (IPProcessControl[]) getCollection().toArray(new IPProcessControl[size()]);
	}

	/*
	 * returns all the processes in this job, which are the children of the job
	 */
	public IPProcess[] getProcesses() {
		return getProcessControls();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.elements.IPJob#getQueue()
	 */
	public IPQueue getQueue() {
		return getQueueControl();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.elementcontrols.IPJobControl#getQueueControl()
	 */
	public IPQueueControl getQueueControl() {
		return (IPQueueControl) getParent();
	}
	
	public synchronized IPProcess[] getSortedProcesses() {
		IPProcessControl[] processes = getProcessControls();
		sort(processes);
		return processes;
	}

	public boolean isDebug() {
		return isDebugJob;
	}

	public boolean isTerminated() {
		EnumeratedAttribute jobState = (EnumeratedAttribute) getAttribute(JobAttributes.getStateAttributeDefinition());
		State state = (State) jobState.getEnumValue();
		if (state == State.TERMINATED || state == State.ERROR) {
			return true;
		}
		return false;
	}

	public void removeAllProcesses() {
		for (IPProcessControl process : getProcessControls()) {
			removeProcess(process);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.elements.IPJob#removeChildListener(org.eclipse.ptp.core.elements.listeners.IJobProcessListener)
	 */
	public void removeChildListener(IJobProcessListener listener) {
		childListeners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.elements.IPJob#removeElementListener(org.eclipse.ptp.core.elements.listeners.IJobListener)
	 */
	public void removeElementListener(IJobListener listener) {
		elementListeners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.elementcontrols.IPJobControl#removeProcess(org.eclipse.ptp.core.elementcontrols.IPProcessControl)
	 */
	public void removeProcess(IPProcessControl process) {
		removeChild(process);
		process.removeNode();
		process.clearOutput();
		String num = process.getProcessNumber();
		if (num != null) {
			numberMap.remove(num);
		}
		fireRemoveProcess(process);
	}

	public void setDebug() {
		isDebugJob = true;
	}

	private void fireChangedJob(Collection<IAttribute> attrs) {
		IJobChangedEvent e = 
			new JobChangedEvent(this, attrs);
		
		for (Object listener : childListeners.getListeners()) {
			((IJobListener)listener).handleEvent(e);
		}
	}

	private void fireNewProcess(IPProcess process) {
		IJobNewProcessEvent e = 
			new JobNewProcessEvent(this, process);
		
		for (Object listener : childListeners.getListeners()) {
			((IJobProcessListener)listener).handleEvent(e);
		}
	}

	private void fireRemoveProcess(IPProcess process) {
		IJobRemoveProcessEvent e = 
			new JobRemoveProcessEvent(this, process);
		
		for (Object listener : childListeners.getListeners()) {
			((IJobProcessListener)listener).handleEvent(e);
		}
	}
}
