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
 *  
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.ptp.rm.ibm.ll.core.rmsystem;

import org.eclipse.ptp.core.attributes.AttributeDefinitionManager;
import org.eclipse.ptp.rmsystem.AbstractResourceManagerConfiguration;
import org.eclipse.ptp.rtsystem.AbstractRuntimeResourceManager;
import org.eclipse.ptp.rtsystem.AbstractRuntimeResourceManagerControl;
import org.eclipse.ptp.rtsystem.AbstractRuntimeResourceManagerMonitor;

public class IBMLLResourceManager extends AbstractRuntimeResourceManager {

	/**
	 * @since 5.0
	 */
	public IBMLLResourceManager(AbstractResourceManagerConfiguration config, AbstractRuntimeResourceManagerControl control,
			AbstractRuntimeResourceManagerMonitor monitor) {
		super(config, control, monitor);
	}

	/**
	 * @since 5.0
	 */
	public AttributeDefinitionManager getAttributeDefinitionManager() {
		return getRuntimeSystem().getAttributeDefinitionManager();
	}
}