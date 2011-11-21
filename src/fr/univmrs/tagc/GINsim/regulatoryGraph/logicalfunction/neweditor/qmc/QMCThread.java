package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.neweditor.qmc;

import java.util.Collection;

import javax.swing.JProgressBar;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.RegulatoryVertex;

import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeExpression;
import fr.univmrs.tagc.common.widgets.GsButton;

public class QMCThread extends Thread {
	private boolean cnf;
	private RegulatoryVertex vertex;
	private GsTreeExpression expression;
	private RegulatoryGraph graph;
	private QMCAlgo algo;
	private JTree tree;
	private JProgressBar progressBar;
	private GsButton cancelButton;

	public QMCThread(boolean cnf, RegulatoryGraph g, RegulatoryVertex v, GsTreeExpression e, JTree t, JProgressBar jpb, GsButton b) {
		this.cnf = cnf;
		vertex = v;
		expression = e;
		graph = g;
		tree = t;
		progressBar = jpb;
		cancelButton = b;
	}
	public void run() {
		algo = new QMCAlgo(cnf, progressBar, cancelButton);
		Collection<RegulatoryMultiEdge> l = graph.getIncomingEdges(vertex);
		algo.init(l, expression.getChilds());
		algo.exec();
		TreePath sel_path = tree.getLeadSelectionPath();
		expression.getGraphicPanel().setText(algo.getFunction(), 0);
		tree.setSelectionPath(sel_path);
	}
}
