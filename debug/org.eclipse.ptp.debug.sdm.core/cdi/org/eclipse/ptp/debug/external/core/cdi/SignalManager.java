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
package org.eclipse.ptp.debug.external.core.cdi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.ptp.debug.core.cdi.PCDIException;
import org.eclipse.ptp.debug.core.cdi.event.IPCDIEvent;
import org.eclipse.ptp.debug.core.cdi.model.IPCDISignal;
import org.eclipse.ptp.debug.core.cdi.model.IPCDITarget;
import org.eclipse.ptp.debug.external.core.cdi.event.SignalChangedEvent;
import org.eclipse.ptp.debug.external.core.cdi.model.Signal;
import org.eclipse.ptp.debug.external.core.cdi.model.Target;
import org.eclipse.ptp.debug.external.core.commands.CLIHandleCommand;
import org.eclipse.ptp.debug.external.core.commands.CLIListSignalsCommand;

/**
 * @author Clement chu
 */
public class SignalManager extends Manager {
	IPCDISignal[] EMPTY_SIGNALS = {};
	Signal[] noSigs = new Signal[0];
	Map<IPCDITarget, List<IPCDISignal>> signalsMap;

	public SignalManager(Session session) {
		super(session, false);
		signalsMap = new Hashtable<IPCDITarget, List<IPCDISignal>>();
	}
	
	public void shutdown() {
		signalsMap.clear();
	}

	synchronized List<IPCDISignal> getSignalsList(Target target) {
		List<IPCDISignal> signalsList = signalsMap.get(target);
		if (signalsList == null) {
			signalsList = Collections.synchronizedList(new ArrayList<IPCDISignal>());
			signalsMap.put(target, signalsList);
		}
		return signalsList;
	}

	IPCDISignal[] createSignals(Target target) throws PCDIException {
		return createSignals(target, null);
	}

	IPCDISignal[] createSignals(Target target, String name) throws PCDIException {
		CLIListSignalsCommand command = new CLIListSignalsCommand(target.getTask(), name);
		target.getDebugger().postCommand(command);
		IPCDISignal[] signals = command.getInfoSignals();
		if (signals.length == 0) {
			throw new PCDIException("No signal found");
		}
		return signals;
	}

	/**
	 * Method hasSignalChanged.
	 * @param sig
	 * @param mISignal
	 * @return boolean
	 */
	private boolean hasSignalChanged(IPCDISignal sig, IPCDISignal signal) {
		return !sig.getName().equals(signal.getName()) ||
			sig.isStopSet() != signal.isStop() ||
			sig.isIgnore() != !signal.isPass();
	}

	protected IPCDISignal findSignal(Target target, String name) {
		IPCDISignal sig = null;
		List<IPCDISignal> signalsList = signalsMap.get(target);
		if (signalsList != null) {
			IPCDISignal[] sigs = (IPCDISignal[])signalsList.toArray(new IPCDISignal[0]);
			for (int i = 0; i < sigs.length; i++) {
				if (sigs[i].getName().equals(name)) {
					sig = sigs[i];
					break;
				}
			}
		}
		return sig;
	}

	public IPCDISignal getSignal(Target target, String name) {
		IPCDISignal sig = findSignal(target, name);
		if (sig == null) {
			// The session maybe terminated because of the signal.
			sig = new Signal(target, name, false, false, false, name);
		}
		return sig;
	}

	public void handle(Signal sig, boolean isIgnore, boolean isStop) throws PCDIException {
		StringBuffer buffer = new StringBuffer(sig.getName());
		buffer.append(" ");
		if (isIgnore) {
			buffer.append("ignore");
		} else {
			buffer.append("noignore");
		}
		buffer.append(" ");
		if (isStop) {
			buffer.append("stop");
		} else  {
			buffer.append("nostop");
		}

		Target target = (Target)sig.getTarget();
		CLIHandleCommand command = new CLIHandleCommand(target.getTask(), buffer.toString());
		target.getDebugger().postCommand(command);
		if (command.isWaitForReturn()) {
			sig.setHandle(isIgnore, isStop);
			target.getDebugger().fireEvents(new IPCDIEvent[] { new SignalChangedEvent(target.getSession(), target.getTask(), sig, sig.getName()) });
		}
	}

	public IPCDISignal[] getSignals(Target target) throws PCDIException {
		List<IPCDISignal> signalsList = signalsMap.get(target);
		if (signalsList == null) {
			update(target);
		}
		signalsList = signalsMap.get(target);
		if (signalsList != null) {
			return (IPCDISignal[])signalsList.toArray(new IPCDISignal[0]);
		}
		return EMPTY_SIGNALS;
	}

	public void update(Target target) throws PCDIException {
		IPCDISignal[] new_sigs = createSignals(target);
		List<IPCDIEvent> eventList = new ArrayList<IPCDIEvent>(new_sigs.length);
		List<IPCDISignal> signalsList = getSignalsList(target);
		for (int i = 0; i<new_sigs.length; i++) {
			IPCDISignal sig = findSignal(target, new_sigs[i].getName());
			if (sig != null) {
				if (hasSignalChanged(sig, new_sigs[i])) {
					// Fire ChangedEvent
					((Signal)sig).setSignal(new_sigs[i]);
					eventList.add(new SignalChangedEvent(target.getSession(), target.getTask(), new_sigs[i], new_sigs[i].getName())); 
				}
			} else {
				signalsList.add(new Signal(target, new_sigs[i]));
			}
		}
		IPCDIEvent[] events = (IPCDIEvent[])eventList.toArray(new IPCDIEvent[0]);
		target.getDebugger().fireEvents(events);
	}
} 
