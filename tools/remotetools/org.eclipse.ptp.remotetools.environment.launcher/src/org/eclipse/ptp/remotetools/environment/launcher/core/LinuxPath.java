/******************************************************************************
 * Copyright (c) 2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial Implementation
 *
 *****************************************************************************/
package org.eclipse.ptp.remotetools.environment.launcher.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class LinuxPath {
	public static String toString(IPath path) {
		return path.toString();
	}

	public static IPath fromString(String location) {
		return new Path(location);
	}
}
