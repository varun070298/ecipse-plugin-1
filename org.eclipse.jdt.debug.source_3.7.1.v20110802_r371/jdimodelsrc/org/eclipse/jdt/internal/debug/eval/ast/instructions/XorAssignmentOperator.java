/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.debug.eval.ast.instructions;

public class XorAssignmentOperator extends XorOperator {

	public XorAssignmentOperator(int variableTypeId, int valueTypeId, int start) {
		super(variableTypeId, variableTypeId, valueTypeId, true, start);
	}

	public String toString() {
		return InstructionsEvaluationMessages.XorAssignmentOperator_operator_1; 
	}

}
