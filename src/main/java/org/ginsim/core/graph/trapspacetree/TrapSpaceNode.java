package org.ginsim.core.graph.trapspacetree;

import org.colomoto.biolqm.tool.trapspaces.TrapSpace;

/**
 * Simple node containing a trap-space definition and further analysis results.
 * 
 * @author Aurelien Naldi
 */
public class TrapSpaceNode {

	public final TrapSpace trapspace;
	private final String s;
	
	public TrapSpaceNode(TrapSpace trapspace) {
		this.trapspace = trapspace;
		this.s = trapspace.shortString();
	}
	
	@Override
	public int hashCode() {
		return trapspace.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof TrapSpaceNode) {
			return trapspace.equals(((TrapSpaceNode)other).trapspace);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return s;
	}
	
	public long getSize(TrapSpaceInclusionDiagram tree) {
		long size = 1;
		return size;
	}
}
