/*******************************************************************************
 * Copyright (c) 2011 University of Illinois All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html 
 * 	
 * Contributors: 
 * 	Albert L. Rossi - design and implementation
 ******************************************************************************/
package org.eclipse.ptp.rm.jaxb.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ptp.core.Preferences;
import org.eclipse.ptp.rm.jaxb.core.JAXBCorePlugin;
import org.eclipse.ptp.rm.jaxb.core.JAXBRMPreferenceConstants;
import org.eclipse.ptp.rm.jaxb.core.JAXBRMPreferenceManager;
import org.eclipse.ptp.rm.jaxb.ui.messages.Messages;
import org.eclipse.ptp.rm.ui.preferences.AbstractRemoteRMPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

public class JAXBRMPreferencesPage extends AbstractRemoteRMPreferencePage implements SelectionListener {

	private Button reloadOption;

	@Override
	public String getPreferenceQualifier() {
		return JAXBCorePlugin.getUniqueIdentifier();
	}

	@Override
	public void performApply() {
		savePreferences();
	}

	@Override
	public void performDefaults() {
		reloadOption.setSelection(Preferences.getDefaultBoolean(getPreferenceQualifier(),
				JAXBRMPreferenceConstants.FORCE_XML_RELOAD, false));
		updateApplyButton();
	}

	@Override
	public boolean performOk() {
		return true;
	}

	@Override
	public void savePreferences() {
		JAXBRMPreferenceManager.savePreferences();
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}

	public void widgetSelected(SelectionEvent e) {
		boolean b = reloadOption.getSelection();
		Preferences.setBoolean(getPreferenceQualifier(), JAXBRMPreferenceConstants.FORCE_XML_RELOAD, b);
	}

	@Override
	protected Control createContents(Composite parent) {
		Group optionsGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		optionsGroup.setText(Messages.Preferences_options);
		optionsGroup.setLayout(new GridLayout(1, true));
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		gd.horizontalSpan = 1;
		gd.horizontalIndent = 10;
		optionsGroup.setLayoutData(gd);
		reloadOption = new Button(optionsGroup, SWT.CHECK | SWT.LEFT);
		reloadOption.addSelectionListener(this);
		reloadOption.setText(JAXBRMPreferenceConstants.FORCE_XML_RELOAD);
		loadSaved();
		return optionsGroup;
	}

	/**
	 * Load values from preference store
	 */
	private void loadSaved() {
		boolean def = Preferences.getDefaultBoolean(getPreferenceQualifier(), JAXBRMPreferenceConstants.FORCE_XML_RELOAD, false);
		boolean b = Platform.getPreferencesService().getBoolean(getPreferenceQualifier(),
				JAXBRMPreferenceConstants.FORCE_XML_RELOAD, def, null);
		reloadOption.setSelection(b);
	}
}