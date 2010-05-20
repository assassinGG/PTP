/**********************************************************************
 * Copyright (c) 2010 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ptp.pldt.lapi;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.ptp.pldt.lapi.messages"; //$NON-NLS-1$
	public static String LapiArtifactView_construct_column_title;
	public static String LapiArtifactView_lapi_artifact_column_title;
	public static String LapiArtifactView_lapi_artifacts_plural;
	public static String LapiCASTVisitor_lapi_call;
	public static String LapiCASTVisitor_lapi_constant;
	public static String LapiIDs_lapi_includes_pref_page_title;
	public static String LAPIPreferencePage_includes_preference_browse_dialog_title;
	public static String LAPIPreferencePage_includes_preference_label;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
