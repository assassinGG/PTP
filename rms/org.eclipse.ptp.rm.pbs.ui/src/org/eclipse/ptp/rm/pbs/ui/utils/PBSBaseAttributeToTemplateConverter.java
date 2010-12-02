/*******************************************************************************
 * Copyright (c) 2010 University of Illinois All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html 
 * 	
 * Contributors: 
 * 	Albert L. Rossi - design and implementation
 ******************************************************************************/
package org.eclipse.ptp.rm.pbs.ui.utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Properties;

import org.eclipse.ptp.core.attributes.IAttributeDefinition;
import org.eclipse.ptp.core.attributes.StringAttributeDefinition;
import org.eclipse.ptp.rm.pbs.ui.IPBSAttributeToTemplateConverter;
import org.eclipse.ptp.rm.pbs.ui.IPBSJobAttributeData;
import org.eclipse.ptp.rm.pbs.ui.IPBSNonNLSConstants;

/**
 * Provides the method for taking attribute definitions and qsub flags and
 * turning them into the full base template.
 * 
 * @author arossi
 * 
 */
public abstract class PBSBaseAttributeToTemplateConverter implements IPBSAttributeToTemplateConverter, IPBSNonNLSConstants {

	private static class AttributeNameSorter<T> implements Comparator<String> {
		public int compare(String s1, String s2) {
			if (TAG_QUEUE.equals(s1))
				return -1;
			if (TAG_QUEUE.equals(s2))
				return 1;
			return s1.compareTo(s2);
		}
	}

	private static final String[] HEADER = { "#!/bin/bash", //$NON-NLS-1$
			ZEROSTR, "#####################################################################", //$NON-NLS-1$
			"## Template for PBS Batch Script Generated by PBS Resource Manager",//$NON-NLS-1$
			"## ",//$NON-NLS-1$
			"## This template contains all the Job Attributes recognized as valid",//$NON-NLS-1$
			"## by a given PBS proxy instance.",//$NON-NLS-1$
			"## ",//$NON-NLS-1$
			"## Placeholders (@NAME@) are included for the PBS Job Attribute ",//$NON-NLS-1$
			"## names as specified by qsub, plus the following internal variables:",//$NON-NLS-1$
			"##",//$NON-NLS-1$
			"## - env                     : place for defining extra environment",//$NON-NLS-1$
			"##                             variables (NB: should not be removed)",//$NON-NLS-1$
			"## - prependedBash           : dynamically change arbitrary bash ",//$NON-NLS-1$
			"##                             commands which should precede the ",//$NON-NLS-1$
			"##                             execution of the main application code",//$NON-NLS-1$
			"## - mpiCommand mpiOptions   : run under MPI",//$NON-NLS-1$
			"## - executablePath progArgs : the actual application",//$NON-NLS-1$
			"## - postpendedBash          : dynamically change arbitrary bash ",//$NON-NLS-1$
			"##                             commands which should follow the ",//$NON-NLS-1$
			"##                             execution of the main application code",//$NON-NLS-1$
			"##",//$NON-NLS-1$
			"## A template can also directly contain arbitrary shell scripting (not ",//$NON-NLS-1$
			"## to be replaced via the 'prepended' and 'postpended' placeholders);",//$NON-NLS-1$
			"## these lines will remain fixed and will not be exposed through",//$NON-NLS-1$
			"## the Launch Tab for modification (they can however be altered by ",//$NON-NLS-1$
			"## using the Resource Manager Properties \"Edit\" Tab).",//$NON-NLS-1$
			"##",//$NON-NLS-1$
			"## NOTE: We advise removing either the ncpus or the nodes resource,",//$NON-NLS-1$
			"##       depending on the PBS configuration (nodes is more common);",//$NON-NLS-1$
			"##       otherwise, the correct value must be set on both redundantly",//$NON-NLS-1$
			"##       in order for the MPI computation to be correct (and ",//$NON-NLS-1$
			"##       some systems might reject a script with both set).",//$NON-NLS-1$
			"#####################################################################"//$NON-NLS-1$
	};

	private static final String[] FOOTER = { ENV_PLACEHOLDER, PRECMD_PLACEHOLDER, CHGDIR_CMD,
			MPICMD_PLACEHOLDER + SP + MPIOPT_PLACEHOLDER + SP + EXECMD_PLACEHOLDER + SP + PRARGS_PLACEHOLDER, PSTCMD_PLACEHOLDER };

	private static AttributeNameSorter<String> sorter = new AttributeNameSorter<String>();

	protected IPBSJobAttributeData data;
	protected IAttributeDefinition<?, ?, ?>[] defs;

	public String generateFullBatchScriptTemplate() throws Throwable {
		return generateBaseTemplate(false);
	}

	public String generateMinBatchScriptTemplate() throws Throwable {
		return generateBaseTemplate(true);
	}

	public IPBSJobAttributeData getData() {
		return data;
	}

	public void initialize() throws Throwable {
		initializeInternal();
		addInternalDefinitions();
	}

	public void setAttributeDefinitions(IAttributeDefinition<?, ?, ?>[] defs) {
		this.defs = defs;
	}

	protected abstract void initializeInternal() throws Throwable;

	/*
	 * Non-standard attribute definitions used for internal processing of the
	 * batch template.
	 */
	private void addInternalDefinitions() throws Throwable {
		Map<String, IAttributeDefinition<?, ?, ?>> definitions = data.getAttributeDefinitionMap();
		Properties ttips = data.getToolTips();
		if (definitions == null || ttips == null)
			return;
		definitions.put(TAG_MPIOPT, new StringAttributeDefinition(TAG_MPIOPT, TAG_MPIOPT, ZEROSTR, true, MPIOPT_DEFAULT));
		ttips.setProperty(TAG_MPIOPT, TAG_INTERNAL);
		definitions.put(TAG_SCRIPT, new StringAttributeDefinition(TAG_SCRIPT, TAG_SCRIPT, ZEROSTR, true, null));
		ttips.setProperty(TAG_SCRIPT, TAG_INTERNAL);
		definitions.put(TAG_MPICMD, new StringAttributeDefinition(TAG_MPICMD, TAG_MPICMD, ZEROSTR, true, MPICMD_DEFAULT));
		ttips.setProperty(TAG_MPICMD, TAG_INTERNAL);
		definitions.put(TAG_PRECMD, new StringAttributeDefinition(TAG_PRECMD, TAG_PRECMD, ZEROSTR, true, null));
		ttips.setProperty(TAG_PRECMD, TAG_INTERNAL);
		definitions.put(TAG_PSTCMD, new StringAttributeDefinition(TAG_PSTCMD, TAG_PSTCMD, ZEROSTR, true, null));
		ttips.setProperty(TAG_PSTCMD, TAG_INTERNAL);
	}

	/*
	 * Composes the flag translations into the space between the header and
	 * footer of the template file.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.rm.pbs.ui.IPBSAttributeToTemplateConverter#
	 * generateFullBatchScriptTemplate()
	 */
	private String generateBaseTemplate(boolean minimal) throws Throwable {
		Properties flags = data.getPBSQsubFlags();
		if (flags == null)
			return null;

		Map<String, String> minSet = data.getMinSet();

		String[] sorted = flags.keySet().toArray(new String[0]);
		Arrays.sort(sorted, sorter);

		StringBuffer template = new StringBuffer();

		for (int i = 0; i < HEADER.length; i++)
			template.append(HEADER[i]).append(REMOTE_LINE_SEP);

		for (int i = 0; i < sorted.length; i++)
			if (!minimal || minSet.containsKey(sorted[i])) {
				String flag = flags.getProperty(sorted[i]);
				template.append(PBSDIRECTIVE).append(flag).append(MARKER).append(sorted[i]).append(MARKER).append(REMOTE_LINE_SEP);
			}

		for (int i = 0; i < FOOTER.length; i++)
			template.append(FOOTER[i]).append(REMOTE_LINE_SEP);

		return template.toString();
	}

	public static Comparator<String> getSorter() {
		return sorter;
	}
}
