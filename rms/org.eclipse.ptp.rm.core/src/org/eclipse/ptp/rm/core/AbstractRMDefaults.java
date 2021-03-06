/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.ptp.rm.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.rm.core.messages.Messages;
import org.osgi.framework.Bundle;

/**
 * @since 3.0
 */
public abstract class AbstractRMDefaults {

	public static Properties read(Path defaultsPropertiesPath, Bundle bundle) throws CoreException {
		InputStream inStream;
		Properties properties = new Properties();
		try {
			inStream = FileLocator.openStream(bundle, defaultsPropertiesPath, false);
			properties.load(inStream);

		} catch (IOException e) {
			throw RMCorePlugin.coreErrorException(Messages.AbstractRMDefaults_Exception_FailedReadFile, e);
		}
		return properties;
	}

	public static String getString(Bundle bundle, Properties properties, String key) throws CoreException {
		String value = properties.getProperty(key);
		if (value == null) {
			throw new CoreException(new Status(IStatus.ERROR, bundle.getSymbolicName(), NLS.bind(
					Messages.AbstractRMDefaults_MissingValue, key)));
		}
		return value;
	}

	public static int getInteger(Bundle bundle, Properties properties, String key) throws CoreException {
		String value = getString(bundle, properties, key);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new CoreException(new Status(IStatus.ERROR, bundle.getSymbolicName(), NLS.bind(
					Messages.AbstractRMDefaults_FailedParseInteger, key)));
		}
	}

	public static boolean getBoolean(Bundle bundle, Properties properties, String key) throws CoreException {
		String value = getString(bundle, properties, key);
		return Boolean.parseBoolean(value);
	}
}
