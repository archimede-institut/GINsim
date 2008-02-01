package fr.univmrs.ibdm.GINsim.connectivity;

import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.gui.GsParameterPanel;

/**
 * Temporary graph, not savable, not editable, nothing...
 */
public final class GsTmpGraph extends GsGraph {

    /**
     * create a temporary graph.
     */
	public GsTmpGraph() {
		super(null);
	}

	protected Object doInteractiveAddVertex(int param) {
		return null;
	}

	protected Object doInteractiveAddEdge(Object source, Object target,
			int param) {
		return null;
	}

	protected void doSave(OutputStreamWriter os, int mode, boolean selectedOnly)
			throws GsException {
	}

	protected FileFilter doGetFileFilter() {
		return null;
	}

	protected JPanel doGetFileChooserPanel() {
		return null;
	}

	public GsParameterPanel getEdgeAttributePanel() {
		return null;
	}

	public GsParameterPanel getVertexAttributePanel() {
		return null;
	}

	public void changeVertexId(Object vertex, String newId) throws GsException {
	}

	public void removeEdge(Object obj) {
	}
	/**
	 * add a vertex to this temporary graph.
	 * @param vertex
	 */
	public void addVertex(Object vertex) {
		graphManager.addVertex(vertex);
	}
	/**
	 * add an edge to this temporary graph.
	 * @param source
	 * @param target
	 */
	public void addEdge(Object source, Object target) {
		graphManager.addEdge(source, target, null);
	}
	public List getSpecificLayout() {
		return null;
	}
	public List getSpecificExport() {
		return null;
	}
	public List getSpecificAction() {
		return null;
	}
    public List getSpecificObjectManager() {
        return null;
    }

    protected GsGraph getCopiedGraph() {
        return null;
    }
    protected List doMerge(GsGraph otherGraph) {
        // this can't be usefull in anyway here
        return null;
    }
    protected GsGraph doCopySelection(Vector vertex, Vector edges) {
        // this can't be usefull in anyway here
        return null;
    }
    protected void setCopiedGraph(GsGraph graph) {
    }

}
