package fr.univmrs.tagc.GINsim.gui.tbclient.nodetree;

import javax.swing.ImageIcon;

import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.DTreeNodeBuilder;

public class NodeBuilder extends DTreeNodeBuilder {

	public NodeBuilder(boolean inTable) {
		super(inTable);
	}
	public void addVertexNote(ImageIcon ic_off, Object o, ImageIcon ic_on) {
		currentElement = new VertexNote(currentElement, o, ic_off, ic_on, inTable);
	}
}
