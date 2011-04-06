package org.eclipse.ptp.rdt.sync.ui.properties;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.settings.model.ICResourceDescription;
import org.eclipse.cdt.managedbuilder.ui.properties.AbstractCBuildPropertyTab;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.ptp.rdt.core.BuildConfigurationManager;
import org.eclipse.ptp.rdt.core.BuildScenario;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteFileManager;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.remotetools.core.RemoteToolsServices;
import org.eclipse.ptp.remote.ui.IRemoteUIConnectionManager;
import org.eclipse.ptp.remote.ui.IRemoteUIConstants;
import org.eclipse.ptp.remote.ui.IRemoteUIFileManager;
import org.eclipse.ptp.remote.ui.IRemoteUIServices;
import org.eclipse.ptp.remote.ui.PTPRemoteUIPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class BuildRemotePropertiesPage extends AbstractCBuildPropertyTab {
	public BuildRemotePropertiesPage() {
		super();
	}

	private IRemoteConnection fSelectedConnection;
	// TODO: Support other providers
	private IRemoteServices fSelectedProvider = RemoteToolsServices.getInstance();

	private final Map<Integer, IRemoteConnection> fComboIndexToRemoteConnectionMap = new HashMap<Integer, IRemoteConnection>();
	private final Map<IRemoteConnection, Integer> fComboRemoteConnectionToIndexMap = new HashMap<IRemoteConnection, Integer>();

	private Button fBrowseButton;
	private Button fNewConnectionButton;
	private Combo fConnectionCombo;
	private Text fLocationText;

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	@Override
	protected void createControls(Composite parent) {
		super.createControls(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		usercomp.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		usercomp.setLayoutData(gd);

		// connection combo
		// Label for "Connection:"
		Label connectionLabel = new Label(usercomp, SWT.LEFT);
		connectionLabel.setText("Connection:"); //$NON-NLS-1$

		// combo for providers
		fConnectionCombo = new Combo(usercomp, SWT.DROP_DOWN | SWT.READ_ONLY);
		// set layout to grab horizontal space
		fConnectionCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fConnectionCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleConnectionSelected();
			}
		});

		// populate the combo with a list of providers
		populateConnectionCombo(fConnectionCombo);

		// new connection button
		fNewConnectionButton = new Button(usercomp, SWT.PUSH);
		fNewConnectionButton.setText("Connection: "); //$NON-NLS-1$
		updateNewConnectionButtonEnabled(fNewConnectionButton);
		fNewConnectionButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IRemoteUIConnectionManager connectionManager = getUIConnectionManager();
				if (connectionManager != null) {
					connectionManager.newConnection(fNewConnectionButton.getShell());
				}
				// refresh list of connections
				populateConnectionCombo(fConnectionCombo);
			}
		});

		Label locationLabel = new Label(usercomp, SWT.LEFT);
		locationLabel.setText("Location:"); //$NON-NLS-1$

		fLocationText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		gd.grabExcessHorizontalSpace = true;
		gd.widthHint = 250;
		fLocationText.setLayoutData(gd);
		fLocationText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// MBSCustomPageManager.addPageProperty(REMOTE_SYNC_WIZARD_PAGE_ID,
				// PATH_PROPERTY, fLocationText.getText());
			}
		});

		// new connection button
		fBrowseButton = new Button(usercomp, SWT.PUSH);
		fBrowseButton.setText("Browse:"); //$NON-NLS-1$
		fBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (fSelectedConnection != null) {
					checkConnection();
					if (fSelectedConnection.isOpen()) {
						IRemoteUIServices remoteUIServices = PTPRemoteUIPlugin.getDefault().getRemoteUIServices(fSelectedProvider);
						if (remoteUIServices != null) {
							IRemoteUIFileManager fileMgr = remoteUIServices.getUIFileManager();
							if (fileMgr != null) {
								fileMgr.setConnection(fSelectedConnection);
								String correctPath = fLocationText.getText();
								String selectedPath = fileMgr.browseDirectory(
										fLocationText.getShell(),
										"Project Location (" + fSelectedConnection.getName() + ")", correctPath, IRemoteUIConstants.NONE); //$NON-NLS-1$ //$NON-NLS-2$
								if (selectedPath != null) {
									fLocationText.setText(selectedPath);
								}
							}
						}
					}
				}
			}
		});
	}
	
	/**
	 * Store remote location information both to the service model manager and to the selected build configuration (.cproject file)
	 * 
	 * @throws RuntimeException on problems retrieving the project or its build information.
	 */
	public void performOK() {
		// Register with build configuration manager
		BuildScenario buildScenario = new BuildScenario("Git", fSelectedConnection, fLocationText.getText()); //$NON-NLS-1$
		BuildConfigurationManager.setBuildScenarioForBuildConfiguration(buildScenario, getCfg());
	}

	private void checkConnection() {
		IRemoteUIConnectionManager mgr = getUIConnectionManager();
		if (mgr != null) {
			mgr.openConnectionWithProgress(fConnectionCombo.getShell(), null, fSelectedConnection);
		}
	}
	
	/**
	 * @return
	 */
	private IRemoteUIConnectionManager getUIConnectionManager() {
		IRemoteUIConnectionManager connectionManager = PTPRemoteUIPlugin.getDefault().getRemoteUIServices(fSelectedProvider)
				.getUIConnectionManager();
		return connectionManager;
	}

	/**
	 * Handle new connection selected
	 */
	private void handleConnectionSelected() {
		int selectionIndex = fConnectionCombo.getSelectionIndex();
		fSelectedConnection = fComboIndexToRemoteConnectionMap.get(selectionIndex);
		updateNewConnectionButtonEnabled(fNewConnectionButton);
	}

	/**
	 * @param connectionCombo
	 */
	private void populateConnectionCombo(final Combo connectionCombo) {
		connectionCombo.removeAll();

		IRemoteConnection[] connections = fSelectedProvider.getConnectionManager().getConnections();

		for (int k = 0; k < connections.length; k++) {
			connectionCombo.add(connections[k].getName(), k);
			fComboIndexToRemoteConnectionMap.put(k, connections[k]);
			fComboRemoteConnectionToIndexMap.put(connections[k], k);
		}

		connectionCombo.select(0);
		fSelectedConnection = fComboIndexToRemoteConnectionMap.get(0);
	}

	/**
	 * @param button
	 */
	private void updateNewConnectionButtonEnabled(Button button) {
		IRemoteUIConnectionManager connectionManager = getUIConnectionManager();
		button.setEnabled(connectionManager != null);
	}

	@Override
	public void performApply(ICResourceDescription src,
			ICResourceDescription dst) {
		performOK();
	}

	@Override
	protected void performDefaults() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateData(ICResourceDescription cfg) {
		populateConnectionCombo(fConnectionCombo);
		BuildScenario buildScenario = BuildConfigurationManager.getBuildScenarioForBuildConfiguration(getCfg());
		if (buildScenario != null) {
			fConnectionCombo.select(fComboRemoteConnectionToIndexMap.get(buildScenario.getRemoteConnection()));
			handleConnectionSelected();
			fLocationText.setText(buildScenario.getLocation());
		}
	}

	@Override
	protected void updateButtons() {
		// TODO Auto-generated method stub
		
	}
}