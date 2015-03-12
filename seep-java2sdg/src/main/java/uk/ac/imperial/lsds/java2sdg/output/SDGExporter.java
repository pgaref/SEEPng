/*******************************************************************************
 * Copyright (c) 2014 Imperial College London
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Raul Castro Fernandez - initial API and implementation
 ******************************************************************************/
package uk.ac.imperial.lsds.java2sdg.output;

import java.util.List;

import uk.ac.imperial.lsds.java2sdg.bricks2.SDG.OperatorBlock;

public interface SDGExporter {
	public void export(List<OperatorBlock> sdg, String filename);
}
