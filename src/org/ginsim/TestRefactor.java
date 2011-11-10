package org.ginsim;

import org.ginsim.graph.AbstractGraphFrontend;
import org.ginsim.graph.Edge;
import org.ginsim.graph.Graph;
import org.ginsim.graph.backend.GraphBackend;
import org.ginsim.graph.backend.JgraphtBackendImpl;
import org.ginsim.graph.testGraph.TestEdge;
import org.ginsim.graph.testGraph.TestGraph;
import org.ginsim.graph.testGraph.TestGraphImpl;
import org.ginsim.graph.testGraph.TestVertex;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.backend.JgraphGUIImpl;
import org.ginsim.gui.graph.helper.GraphGUIHelper;
import org.ginsim.gui.graph.helper.GraphGUIHelperFactory;
import org.ginsim.gui.shell.MainFrame;

/**
 * Simple, stupid launcher to test the ongoing refactoring
 * 
 * @author Aurelien Naldi
 */
public class TestRefactor {

	/**
	 * @param args
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		TestGraph lrg = new TestGraphImpl();

		TestVertex v1 = lrg.addVertex();
		TestVertex v2 = lrg.addVertex();
		TestVertex v3 = lrg.addVertex();

		lrg.addEdge(v1, v2);
		lrg.addEdge(v1, v3);
		
		GraphGUI<TestGraph, TestVertex, TestEdge> graphGUI = GUIManager.getInstance().createGraphGUI( lrg);
		MainFrame frame = newFrame( graphGUI);
		
		GraphGUIHelper<TestGraph,TestVertex,TestEdge> helper = (GraphGUIHelper<TestGraph,TestVertex,TestEdge>) GraphGUIHelperFactory.getFactory().getGraphGUIHelper( lrg);
		GUIEditor<TestVertex> node_editor = helper.getNodeEditionPanel( lrg);
		System.out.println("TestRefactor.main() : Node editor = " + node_editor);
		
	}
	
	public static MainFrame newFrame(GraphGUI<?,?,?> gui) {
		MainFrame frame = new MainFrame("test", 800, 600, gui);
		frame.setVisible(true);
		return frame;
	}
	
}
