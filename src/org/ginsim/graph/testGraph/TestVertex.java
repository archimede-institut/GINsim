package org.ginsim.graph.testGraph;

/**
 * PlaceHolder...
 * 
 * @author Aurelien Naldi
 */
public class TestVertex {
	private static int IDX = 1;
	
	private final int idx;
	
	public TestVertex() {
		idx = IDX++;
	}
	
	public String toString() {
		return "G"+idx;
	}
}
