package org.ginsim.gui.graph.dynamicgraph;

import javax.swing.table.TableModel;

public interface StateTableModel extends TableModel {

	int getComponentCount();
	
	String getComponentName(int index);
	
	byte[] getState(int index);
}
