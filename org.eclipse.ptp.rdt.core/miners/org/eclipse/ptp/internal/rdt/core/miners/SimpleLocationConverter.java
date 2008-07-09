/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ptp.internal.rdt.core.miners;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.index.IIndexLocationConverter;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class SimpleLocationConverter implements IIndexLocationConverter {

	String fScheme;
	String fHost;

	public SimpleLocationConverter(String scheme, String host) {
		fScheme = scheme;
		fHost = host;
	}
	
	public IIndexFileLocation fromInternalFormat(String raw) {
		try {
			IPath path = new Path(raw);
			path = path.setDevice(null);
			StringBuilder buffer = new StringBuilder();
			if (fScheme != null) {
				buffer.append(fScheme);
				buffer.append("://"); //$NON-NLS-1$
				if (fHost != null) {
					buffer.append(fHost);
				}
			}
			buffer.append(path.toPortableString());
			URI uri = new URI(buffer.toString());
			return new RemoteIndexFileLocation(null, uri);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		} 
	}

	public String toInternalFormat(IIndexFileLocation location) {
		// TODO Auto-generated method stub
		return null;
	}

}
