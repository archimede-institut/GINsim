package fr.univmrs.tagc.GINsim.jgraph;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

/**
 * custom VertexView: needed to be able to choose vertex shape...
 */
public class GsJgraphVertexView extends VertexView {

	private static final long serialVersionUID = 1578576767854674L;

	/**
	 * 
	 * @param cell
	 */
	public GsJgraphVertexView(Object cell) {
		super(cell);
	}

	public CellViewRenderer getRenderer() {
		Object ord = ((DefaultGraphCell)getCell()).getAttributes().get("RENDERER");
		if (ord == null || !(ord instanceof VertexRenderer)) {
			return renderer;
		}
		return (CellViewRenderer)ord;
	}
}
