/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     John Eblen - Oak Ridge National Laboratory - change to use sync manager
 *                  and move to ui package
 *******************************************************************************/
package org.eclipse.ptp.rdt.sync.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.ptp.rdt.sync.core.RDTSyncCorePlugin;
import org.eclipse.ptp.rdt.sync.core.SyncExceptionHandler;
import org.eclipse.ptp.rdt.sync.core.SyncFlag;
import org.eclipse.ptp.rdt.sync.core.SyncManager;
import org.eclipse.ptp.rdt.sync.ui.messages.Messages;
import org.eclipse.ptp.rdt.sync.core.SyncManager.SYNC_MODE;
import org.eclipse.ptp.rdt.sync.core.resources.RemoteSyncNature;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ResourceChangeListener {
	private ResourceChangeListener() {
	}

	public static void startListening() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceListener, IResourceChangeEvent.POST_CHANGE);
	}

	public static void stopListening() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceListener);
	}

	// For sync errors, print information and provide a toggle box so that user can turn off error displays until the next
	// successful sync, which presumably means the problem has been resolved. Note that errors are re-enabled inside the
	// sync call.
	private static class SyncRCLExceptionHandler implements SyncExceptionHandler {
		private final IProject project;
		public SyncRCLExceptionHandler(IProject p) {
			project = p;
		}
		
		// Class for a message dialog that also allows the user to launch the merge resolution tool.
		private class MessageDialogWithToggleAndMergeButton extends MessageDialogWithToggle {
			public MessageDialogWithToggleAndMergeButton(Shell parentShell, String dialogTitle, Image image, String message,
					int dialogImageType, String[] dialogButtonLabels, int defaultIndex, String toggleMessage, boolean toggleState) {
				super(parentShell, dialogTitle, image, message, dialogImageType, dialogButtonLabels, defaultIndex, toggleMessage,
						toggleState);
			}
			
		    /**
		     * @see Dialog#createDialogArea(Composite)
		     */
			@Override
			protected Control createDialogArea(Composite parent) {
		        Composite dialogAreaComposite = (Composite) super.createDialogArea(parent);
		        setMergeButton(createMergeButton(dialogAreaComposite));
		        return dialogAreaComposite;
			}
			
		    /**
		     * Creates a merge button without any text or state.  The text and state
		     * will be created by <code>createDialogArea</code>. 
		     * 
		     * @param parent
		     *            The composite in which the merge button should be placed;
		     *            must not be <code>null</code>.
		     * @return The added merge button; never <code>null</code>.
		     */
		    protected Button createMergeButton(Composite parent) {
		        final Button button = new Button(parent, SWT.CHECK | SWT.LEFT);

		        GridData data = new GridData(SWT.NONE);
		        data.horizontalSpan = 2;
		        button.setLayoutData(data);
		        button.setFont(parent.getFont());

		        button.addSelectionListener(new SelectionAdapter() {
		            public void widgetSelected(SelectionEvent e) {
		                // Nothing yet
		            }

		        });

		        return button;
		    }
		    
		    /**
		     * A mutator for the button to launch merge resolution
		     * 
		     * @param button
		     *            The button to use; must not be <code>null</code>.
		     */
		    protected void setMergeButton(Button button) {
		        if (button == null) {
		            throw new NullPointerException(
		                    "A message dialog with merge may not have a null merge button.");} //$NON-NLS-1$

		        if (!button.isDisposed()) {
		            button.setText(Messages.ResourceChangeListener_1);
		        }
		    }
		}

		public void handle(CoreException e) {
			if (!SyncManager.getShowErrors(project)) {
				return;
			}

			String message;
			String endOfLineChar = System.getProperty("line.separator"); //$NON-NLS-1$
			Display errorDisplay = RDTSyncUIPlugin.getStandardDisplay();
			
			message = Messages.SyncMenuOperation_5 + project.getName() + ":" + endOfLineChar + endOfLineChar; //$NON-NLS-1$
			if ((e.getMessage() != null && e.getMessage().length() > 0) || e.getCause() == null) {
				message = message + e.getMessage();
			} else {
				message = message + e.getCause().getMessage();
			}
			
			final String finalMessage = message;
			errorDisplay.syncExec(new Runnable () {
				public void run() {
					if (false) {
						MessageDialogWithToggle dialog = MessageDialogWithToggle.openError(null, Messages.SyncMenuOperation_3,
								finalMessage, Messages.SyncMenuOperation_4, !SyncManager.getShowErrors(project), null, null);
						if (dialog.getToggleState()) {
							SyncManager.setShowErrors(project, false);
						} else {
							SyncManager.setShowErrors(project, true);
						}
					} else {
						MessageDialogWithToggle dialog = MessageDialogWithToggleAndMergeButton.openError(null,
								Messages.SyncMenuOperation_3, finalMessage, Messages.SyncMenuOperation_4,
								!SyncManager.getShowErrors(project), null, null);
						if (dialog.getToggleState()) {
							SyncManager.setShowErrors(project, false);
						} else {
							SyncManager.setShowErrors(project, true);
						}
					}
				}
			});
		}
	}

	private static IResourceChangeListener resourceListener = new IResourceChangeListener() {
		public void resourceChanged(IResourceChangeEvent event) {
			for (IResourceDelta delta : event.getDelta().getAffectedChildren()) {
				IProject project = delta.getResource().getProject();
				if (project == null) {
					return;
				}
				if (RemoteSyncNature.hasNature(project)) {
					SYNC_MODE syncMode = SyncManager.getSyncMode(project);
					// Note that sync'ing is necessary even if user has turned sync'ing off. The actual synchronization call does
					// more than just sync files to remote.
					try {
						if (!(SyncManager.getSyncAuto()) || syncMode == SYNC_MODE.NONE) {
							SyncManager.sync(delta, project, SyncFlag.NO_SYNC, null);
						} else if (syncMode == SYNC_MODE.ALL) {
							SyncManager.syncAll(delta, project, SyncFlag.NO_FORCE, new SyncRCLExceptionHandler(project));
						} else if (syncMode == SYNC_MODE.ACTIVE) {
							SyncManager.sync(delta, project, SyncFlag.NO_FORCE, new SyncRCLExceptionHandler(project));
						}
					} catch (CoreException e){
						// This should never happen because only a blocking sync can throw a core exception, and all syncs here are non-blocking.
						RDTSyncCorePlugin.log(Messages.ResourceChangeListener_0);
					}
				}
			}
		}
	};
}