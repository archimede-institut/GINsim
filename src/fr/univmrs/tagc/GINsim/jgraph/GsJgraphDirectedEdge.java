package fr.univmrs.tagc.GINsim.jgraph;

import org._3pq.jgrapht.edge.DirectedEdge;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.data.ToolTipsable;

/**
 * DirectedEdge with an associated object
 */
public class GsJgraphDirectedEdge extends DirectedEdge implements GsDirectedEdge, ToolTipsable {

    private static final long serialVersionUID = 796467546398764L;
	private Object userObject;

	/**
	 * create a directedEdge.
	 *
	 * @param source the source vertex.
	 * @param target the target vertex.
	 * @param obj data to attach to th edge.
	 */
    public GsJgraphDirectedEdge(Object source, Object target, Object obj) {
       super(source, target);
       userObject = obj;
    }

    public Object getUserObject() {
        return userObject;
    }

    public Object getSourceVertex() {
        return getSource();
    }

    public Object getTargetVertex() {
        return getTarget();
    }

	public String toToolTip() {
		if (userObject instanceof ToolTipsable) {
			return ((ToolTipsable)userObject).toToolTip();
		}
		return null;
	}

    public void setUserObject(Object obj) {
        userObject = obj;
    }
		public int hashCode() {
			return toString().hashCode();
		}
}
