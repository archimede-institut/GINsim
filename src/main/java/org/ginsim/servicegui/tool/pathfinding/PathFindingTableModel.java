package org.ginsim.servicegui.tool.pathfinding;

import java.util.List;

import javax.swing.table.TableModel;

public interface PathFindingTableModel<N> extends TableModel {

	void add(int index);
	
	boolean del(int index);
	
	List<N> getNodes();
	
	void setNode(N node, int[] selectedRows);
}
