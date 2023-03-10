/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.debug.core;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
 
/**
 * A runnable that represents one logical evaluation
 * to be run in a target thread. 
 * <p>
 * Clients are intended to implement this interface.
 * </p>
 * 
 * @see org.eclipse.jdt.debug.core.IJavaThread#runEvaluation(IEvaluationRunnable, IProgressMonitor, int, boolean)
 * @since 2.0
 */ 
public interface IEvaluationRunnable {
	
	/**
	 * Runs this evaluation in the specified thread, reporting
	 * progress to the given progress monitor.
	 * 
	 * @param thread the thread in which to run the evaluation
	 * @param monitor progress monitor (may be <code>null</code>)
	 * @exception DebugException if an exception occurs during
	 *  the evaluation
	 */
	public abstract void run(IJavaThread thread, IProgressMonitor monitor) throws DebugException;

}
