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
 *******************************************************************************/
package org.eclipse.ptp.debug.core.model;

import java.math.BigInteger;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.ptp.debug.core.pdi.model.IPDIStackFrame;

/**
 * @author Clement chu
 * 
 */
public interface IPStackFrame extends IRunToLine, IRunToAddress, IJumpToLine, IJumpToAddress, IPDebugElement, IStackFrame {
	/**
	 * @return
	 */
	public boolean canEvaluate();

	/**
	 * @param expression
	 * @return
	 * @throws DebugException
	 */
	public IValue evaluateExpression(String expression) throws DebugException;

	/**
	 * @param expression
	 * @return
	 * @throws DebugException
	 */
	public String evaluateExpressionToString(String expression) throws DebugException;

	/**
	 * @return
	 */
	public BigInteger getAddress();

	/**
	 * @return
	 */
	public String getFile();

	/**
	 * @return
	 */
	public int getFrameLineNumber();

	/**
	 * @return
	 */
	public String getFunction();

	/**
	 * @return
	 */
	public int getLevel();

	/**
	 * @return
	 */
	public IPDIStackFrame getPDIStackFrame();
}
