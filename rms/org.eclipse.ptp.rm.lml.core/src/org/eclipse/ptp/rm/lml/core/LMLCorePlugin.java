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
 * 
 * Modified by:
 * 		Claudia Knobloch, Forschungszentrum Juelich GmbH
 *******************************************************************************/
package org.eclipse.ptp.rm.lml.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.ptp.rm.lml.core.messages.Messages;
import org.eclipse.ptp.rm.lml.core.util.DebugUtil;
import org.osgi.framework.BundleContext;
import org.xml.sax.SAXException;

public class LMLCorePlugin extends Plugin {
	public static final String PLUGIN_ID = "org.eclipse.ptp.rm.lml.core"; //$NON-NLS-1$

	// The shared instance.
	private static LMLCorePlugin fPlugin;

	/*
	 * Unmarshaller for JAXB-generator
	 */
	private static Unmarshaller unmarshaller;

	/*
	 * Marshaller for JAXB-model
	 */
	private static Marshaller marshaller;

	/**
	 * Returns the shared instance.
	 */
	public static LMLCorePlugin getDefault() {
		return fPlugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		final ResourceBundle bundle = LMLCorePlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (final MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Generate a unique identifier
	 * 
	 * @return unique identifier string
	 */
	public static String getUniqueIdentifier() {
		if (getDefault() == null) {
			// If the default instance is not yet initialized,
			// return a static identifier. This identifier must
			// match the plugin id defined in plugin.xml
			return PLUGIN_ID;
		}
		return getDefault().getBundle().getSymbolicName();
	}

	/**
	 * Create log entry from an IStatus
	 * 
	 * @param status
	 */
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	/**
	 * Create log entry from a string
	 * 
	 * @param msg
	 */
	public static void log(String msg) {
		if (DebugUtil.RM_TRACING) {
			System.err.println(msg);
		}
		log(new Status(IStatus.ERROR, getUniqueIdentifier(), IStatus.ERROR, msg, null));
	}

	/**
	 * Create log entry from a Throwable
	 * 
	 * @param e
	 */
	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, getUniqueIdentifier(), IStatus.ERROR, Messages.LMLCorePlugin_0, e));
	}

	/*
	 * Resource bundle
	 */
	private ResourceBundle resourceBundle;

	/**
	 * The constructor.
	 */
	public LMLCorePlugin() {
		super();

		fPlugin = this;

		try {
			resourceBundle = ResourceBundle.getBundle(PLUGIN_ID + ".ParallelPluginResources"); //$NON-NLS-1$
		} catch (final MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * Get the JAXB marshaller
	 * 
	 * @return the JAXB marshaller
	 */
	public Marshaller getMarshaller() {
		if (marshaller == null) {
			try {
				createMarshaller();
			} catch (final JAXBException e) {
				log(e);
			}
		}
		return marshaller;
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	/**
	 * Get the JAXB unmarshaller.
	 * 
	 * @return the JAXB unmarshaller
	 */
	public Unmarshaller getUnmarshaller() {
		if (unmarshaller == null) {
			try {
				createUnmarshaller();
			} catch (final JAXBException e) {
				log(e);
			} catch (final MalformedURLException e) {
				log(e);
			}
		}
		return unmarshaller;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		createUnmarshaller();
		DebugUtil.configurePluginDebugOptions();
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			Preferences.savePreferences(getUniqueIdentifier());
		} finally {
			super.stop(context);
			fPlugin = null;
		}
	}

	private void createMarshaller() throws JAXBException {
		ClassLoader loader = LMLCorePlugin.class.getClassLoader();
		final JAXBContext jc = JAXBContext.newInstance("org.eclipse.ptp.rm.lml.internal.core.elements", //$NON-NLS-1$
				loader);
		marshaller = jc.createMarshaller();
	}

	/**
	 * For the generation of instances from classes by JAXB a unmarshaller is needed. In the method the needed unmarshaller is
	 * created. It is said where the classes for the instantiation are.
	 * 
	 * @throws MalformedURLException
	 * @throws JAXBException
	 */
	private void createUnmarshaller() throws MalformedURLException, JAXBException {
		final URL xsd = getBundle().getEntry("/schema/lgui.xsd"); //$NON-NLS-1$

		ClassLoader loader = LMLCorePlugin.class.getClassLoader();
		final JAXBContext jc = JAXBContext.newInstance("org.eclipse.ptp.rm.lml.internal.core.elements", loader); //$NON-NLS-1$

		unmarshaller = jc.createUnmarshaller();

		// if xsd is null => do not check for validity
		if (xsd != null) {

			Schema mySchema;
			final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			try {

				mySchema = sf.newSchema(xsd);
			} catch (final SAXException saxe) {
				// ...(error handling)
				mySchema = null;

			}

			// Connect schema to unmarshaller
			unmarshaller.setSchema(mySchema);

		}
	}

}