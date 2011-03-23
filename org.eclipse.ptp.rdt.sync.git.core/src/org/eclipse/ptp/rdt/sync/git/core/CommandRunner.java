/*******************************************************************************
 * Copyright (c) 2011 The University of Tennessee and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    John Eblen - initial implementation
 *******************************************************************************/
package org.eclipse.ptp.rdt.sync.git.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteFileManager;
import org.eclipse.ptp.remote.core.IRemoteProcess;
import org.eclipse.ptp.remote.core.IRemoteProcessBuilder;

// Static class for running system-level commands. This includes local and remote directory operations and also running arbitrary
// commands on remote machines.
public class CommandRunner {
	// Nested convenience class for storing the results of a command.
	public static class CommandResults {
		private String stdout;
		private String stderr;
		private int exitCode;

		/**
		 * @return the exitCode
		 */
		public int getExitCode() {
			return exitCode;
		}

		/**
		 * @return the stderr
		 */
		public String getStderr() {
			return stderr;
		}

		/**
		 * @return the stdout
		 */
		public String getStdout() {
			return stdout;
		}

		/**
		 * @param exitCode
		 */
		public void setExitCode(int exitCode) {
			this.exitCode = exitCode;
		}

		/**
		 * @param stderr
		 */
		public void setStderr(String stderr) {
			this.stderr = stderr;
		}

		/**
		 * @param stdout
		 */
		public void setStdout(String stdout) {
			this.stdout = stdout;
		}
	};

	enum DirectoryStatus {
		NOT_A_DIRECTORY, NOT_PRESENT, PRESENT
	}

	/**
	 * Simply check if a local directory is present but do not create
	 * 
	 * @return the directory status
	 */
	public static DirectoryStatus checkLocalDirectory(String localDirectory) {
		final File localDir = new File(localDirectory);
		if (localDir.exists() == false) {
			return DirectoryStatus.NOT_PRESENT;
		}

		else if (localDir.isDirectory()) {
			return DirectoryStatus.PRESENT;
		}

		return DirectoryStatus.NOT_A_DIRECTORY;
	}

	/**
	 * Simply check if a remote directory is present but do not create
	 * 
	 * @param conn
	 * @param remoteDir
	 * @return the directory status
	 */
	public static DirectoryStatus checkRemoteDirectory(IRemoteConnection conn, String remoteDir) {
		final IRemoteFileManager fileManager = conn.getRemoteServices().getFileManager(conn);
		final IFileStore fileStore = fileManager.getResource(remoteDir);
		final IFileInfo fileInfo = fileStore.fetchInfo();

		if (fileInfo.exists() == false) {
			return DirectoryStatus.NOT_PRESENT;
		} else if (fileInfo.isDirectory() == false) {
			return DirectoryStatus.NOT_A_DIRECTORY;
		} else {
			return DirectoryStatus.PRESENT;
		}
	}

	/**
	 * This function creates the local directory if it does not exist.
	 * 
	 * @return whether the directory was already PRESENT
	 * @TODO: Handle false return from mkdir
	 */
	public static DirectoryStatus createLocalDirectory(String localDirectory) {
		final DirectoryStatus directoryStatus = checkLocalDirectory(localDirectory);
		if (directoryStatus == DirectoryStatus.NOT_PRESENT) {
			final File localDir = new File(localDirectory);
			localDir.mkdir();
		}

		return directoryStatus;
	}

	/**
	 * This function creates the remote directory if it does not exist. Parent directories are also created if necessary. Note that
	 * this command does not overwrite if the requested remote directory exists but is not a directory.
	 * 
	 * @TODO: Check that this also holds true for creating parent directories. For example, if remoteDir =
	 *        "/home/user/project/project1/" and "/home/user/project" is a file will it be deleted?
	 * @param conn
	 * @param remoteDir
	 * @throws CoreException
	 *             on problem creating the remote directory.
	 * @return whether the directory was already PRESENT
	 */
	public static DirectoryStatus createRemoteDirectory(IRemoteConnection conn, String remoteDir) throws CoreException {
		final IRemoteFileManager fileManager = conn.getRemoteServices().getFileManager(conn);
		final IFileStore fileStore = fileManager.getResource(remoteDir);
		final IFileInfo fileInfo = fileStore.fetchInfo();

		if (fileInfo.exists() == true) {
			if (fileInfo.isDirectory() == true) {
				return DirectoryStatus.PRESENT;
			} else {
				return DirectoryStatus.NOT_A_DIRECTORY;
			}
		}

		try {
			fileStore.mkdir(EFS.NONE, null);
		} catch (final CoreException e) {
			throw e;
		}

		return DirectoryStatus.NOT_PRESENT;
	}

	/**
	 * Execute command on a remote host and wait for the command to complete.
	 * 
	 * @param conn
	 * @param command
	 * @return CommandResults (contains stdout, stderr, and exit code)
	 * @throws IOException
	 *             in several cases if there is a problem communicating with the remote host.
	 * @throws InterruptedException
	 *             if execution of remote command is interrupted.
	 * @TODO: Expand to work with multiple platforms (assumes UNIX \n line endings.)
	 */
	public static CommandResults executeRemoteCommand(IRemoteConnection conn, String command) throws IOException,
			InterruptedException {
		final CommandResults commandResults = new CommandResults();
		// Run the command and wait for it to complete.
		final List<String> commandStrings = Arrays.asList(command.split("\\s+")); //$NON-NLS-1$
		final IRemoteProcessBuilder rpb = conn.getRemoteServices().getProcessBuilder(conn, commandStrings);

		IRemoteProcess rp = null;
		try {
			rp = rpb.start();
		} catch (final IOException e) {
			throw e;
		}

		int exitCode;
		try {
			exitCode = rp.waitFor();
		} catch (final InterruptedException e) {
			throw e;
		}
		commandResults.setExitCode(exitCode);

		// Read and store stdout and stderr.
		BufferedReader commandOutputReader = null;
		BufferedReader commandErrorReader = null;
		commandOutputReader = new BufferedReader(new InputStreamReader(rp.getInputStream()));
		commandErrorReader = new BufferedReader(new InputStreamReader(rp.getErrorStream()));

		String output = ""; //$NON-NLS-1$
		try {
			String line;
			while ((line = commandOutputReader.readLine()) != null) {
				output += line;
				output += "\n"; //$NON-NLS-1$
			}
		} catch (final IOException e) {
			throw e;
		} finally {
			commandOutputReader.close();
		}

		String error = ""; //$NON-NLS-1$
		try {
			String line;
			while ((line = commandErrorReader.readLine()) != null) {
				error += line;
				error += "\n"; //$NON-NLS-1$
			}
		} catch (final IOException e) {
			throw e;
		} finally {
			commandErrorReader.close();
		}

		commandResults.setStdout(output);
		commandResults.setStderr(error);
		return commandResults;
	}

	// Enforce as static
	private CommandRunner() {
		throw new AssertionError();
	}
}