/**
 * Copyright (c) 2011 Forschungszentrum Juelich GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 		Claudia Knobloch, FZ Juelich
 */

package org.eclipse.ptp.rm.lml.core.model;

public interface ILguiHandler {
	/*
	 * Mandatory table fields
	 */
	public static String JOB_ID = "step"; //$NON-NLS-1$
	public static String JOB_OWNER = "owner"; //$NON-NLS-1$
	public static String JOB_STATUS = "status"; //$NON-NLS-1$
	public static String JOB_QUEUE_NAME = "queue"; //$NON-NLS-1$

	public static String ACTIVE_JOB_TABLE = "joblistrun"; //$NON-NLS-1$
	public static String INACTIVE_JOB_TABLE = "joblistwait"; //$NON-NLS-1$
}