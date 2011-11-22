package org.ginsim.gui.graph.backend;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.NodeRenderer;
import org.jgraph.graph.NodeView;

/**
 * custom NodeView: needed to be able to choose vertex shape...
 */
public class GsJgraphNodeView extends NodeView {

	private static final long serialVersionUID = 1578576767854674L;

	/**
	 * 
	 * @param cell
	 */
	public GsJgraphNodeView(Object cell) {
		super(cell);
	}

	public CellViewRenderer getRenderer() {
		Object ord = ((DefaultGraphCell)getCell()).getAttributes().get("RENDERER");
		if (ord == null || !(ord instanceof NodeRenderer)) {
			return renderer;
		}
		return (CellViewRenderer)ord;
	}
}
