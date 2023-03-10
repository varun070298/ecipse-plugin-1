/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.debug.ui.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

/**
 * An adapter for an "add memory block" operation. The Memory View provides
 * a retargettable "add memory block" action that debuggers may plug into
 * by providing an adapter (see <code>IAdaptable</code>) of this type.
 * <p>
 * Clients may implement this interface.
 * </p>
 * @since 3.2
 */
public interface IAddMemoryBlocksTarget {
	
	/**
	 * Returns whether an add memory block operation can be performed from the specified
	 * part and the given selection.
	 * 
	 * @param part the part on which the action has been invoked
	 * @param selection the selection on which the action has been invoked
	 * @throws CoreException if unable to perform the action 
	 */
	public boolean canAddMemoryBlocks(IWorkbenchPart part, ISelection selection) throws CoreException;
	
	/**
	 * Returns whether this target will support adding memory block from the specified
	 * part.
	 * @param part
	 * @return true if the target wants to support adding memory block from the given
	 * part, false otherwise.
	 */
	public boolean supportsAddMemoryBlocks(IWorkbenchPart part);
	
	
	/**
	 * Perform an add memory block on the given element that is 
	 * currently selected in the Debug view. If a memory block can be successfully
	 * created, implementations must add the resulted memory block to <code>IMemoryBlockManager</code>
	 * In addition, implementations must query to see if default renderings should be created
	 * for the given memory block and add these renderings accordingly.
	 * 
	 * @param part the part on which the action has been invoked
	 * @param selection the selection on which the action has been invoked
	 * @throws CoreException if unable to perform the action 
	 * 
	 * @see org.eclipse.debug.core.model.IMemoryBlockRetrieval
	 * @see org.eclipse.debug.ui.memory.IMemoryRenderingManager
	 * @see org.eclipse.debug.core.IMemoryBlockManager
	 */
	public void addMemoryBlocks(IWorkbenchPart part, ISelection selection) throws CoreException;
}
