package org.ginsim.gui.tbclient.nodetree;

import javax.swing.ImageIcon;

import org.ginsim.gui.tbclient.decotreetable.DTreeNodeBuilder;


public class NodeBuilder extends DTreeNodeBuilder {

	public NodeBuilder(boolean inTable) {
		super(inTable);
	}
	public void addVertexNote(ImageIcon ic_off, Object o, ImageIcon ic_on) {
		currentElement = new NodeNote(currentElement, o, ic_off, ic_on, inTable);
	}
}
