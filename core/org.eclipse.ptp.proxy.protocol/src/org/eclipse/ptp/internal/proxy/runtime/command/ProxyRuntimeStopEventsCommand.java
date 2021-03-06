/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.ptp.internal.proxy.runtime.command;

import org.eclipse.ptp.proxy.command.AbstractProxyCommand;
import org.eclipse.ptp.proxy.runtime.command.IProxyRuntimeStopEventsCommand;

public class ProxyRuntimeStopEventsCommand extends AbstractProxyCommand
		implements IProxyRuntimeStopEventsCommand {

	public ProxyRuntimeStopEventsCommand() {
		super(STOP_EVENTS);
	}

	public ProxyRuntimeStopEventsCommand(int transID, String[] args) {
		super(STOP_EVENTS, transID, args);
	}
}
