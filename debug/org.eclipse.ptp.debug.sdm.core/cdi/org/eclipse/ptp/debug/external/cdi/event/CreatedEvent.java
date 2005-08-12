/*******************************************************************************
 * Copyright (c) 2000, 2004 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *******************************************************************************/
package org.eclipse.ptp.debug.external.cdi.event;

import java.util.ArrayList;
import java.util.Hashtable;

import org.eclipse.cdt.debug.core.cdi.CDIException;
import org.eclipse.cdt.debug.core.cdi.event.ICDICreatedEvent;
import org.eclipse.cdt.debug.core.cdi.model.ICDIObject;
import org.eclipse.ptp.debug.core.cdi.event.IPCDIEvent;
import org.eclipse.ptp.debug.external.cdi.Session;
import org.eclipse.ptp.debug.external.cdi.model.Target;
import org.eclipse.ptp.debug.external.event.DebugEvent;
import org.eclipse.ptp.debug.external.event.EBreakpointCreated;
import org.eclipse.ptp.debug.external.event.ETargetRegistered;

/**
 */
public class CreatedEvent implements ICDICreatedEvent, IPCDIEvent {
	Session session;
	ICDIObject[] sources;
	DebugEvent event;
	int[] processes;

	public CreatedEvent(Session s, EBreakpointCreated ev) {
		session = s;
		event = ev;

		Hashtable table = ev.getSources();
		ArrayList sourceList = new ArrayList();
		processes = ev.getProcesses();
		
		int[] registeredTargets = session.getRegisteredTargetIds();
		
		for (int j = 0; j < registeredTargets.length; j++) {
			Integer targetId = new Integer(registeredTargets[j]);
			if (table.containsKey(targetId)) {
				/* Put Target as the source rather than the Threads */
	   		   ICDIObject src = session.getTarget(targetId.intValue());
			   sourceList.add(src);
			}
		}
		
	    sources = (ICDIObject[]) sourceList.toArray(new ICDIObject[0]);
	}

	public CreatedEvent(Session s, ETargetRegistered ev) {
		session = s;
		event = ev;
		
		Hashtable table = ev.getSources();
		ArrayList sourceList = new ArrayList();
		processes = ev.getProcesses();
		
		int[] registeredTargets = session.getRegisteredTargetIds();
		
		for (int j = 0; j < registeredTargets.length; j++) {
			Integer targetId = new Integer(registeredTargets[j]);
			if (table.containsKey(targetId)) {
		       int[] threads = (int[]) table.get(targetId);
		       if (threads.length == 0) {
		    		   ICDIObject src = session.getTarget(targetId.intValue());
		    		   sourceList.add(src);
		       }
		       for (int i = 0; i < threads.length; i++) {
		    	   try {
		    		   ICDIObject src = ((Target) session.getTarget(targetId.intValue())).getThread(threads[i]);
		    		   sourceList.add(src);
		    	   } catch (CDIException e) {
		    	   }
		       }
			}
		}
		
	    sources = (ICDIObject[]) sourceList.toArray(new ICDIObject[0]);
	}

	public ICDIObject getSource() {
		// Auto-generated method stub
		System.out.println("CreatedEvent.getSource()");
		return sources[0];
	}
	
	public ICDIObject[] getSources() {
		return sources;
	}
	
	public int[] getProcesses() {
		return processes;
	}
}
