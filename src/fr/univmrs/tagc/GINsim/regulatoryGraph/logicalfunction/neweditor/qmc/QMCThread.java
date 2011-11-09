package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.neweditor.qmc;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeExpression;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;

import java.util.Collection;
import java.util.List;
import javax.swing.tree.TreePath;
import javax.swing.JTree;
import javax.swing.JProgressBar;
import fr.univmrs.tagc.common.widgets.GsButton;

public class QMCThread extends Thread {
	private boolean cnf;
	private GsRegulatoryVertex vertex;
	private GsTreeExpression expression;
	private GsRegulatoryGraph graph;
	private QMCAlgo algo;
	private JTree tree;
	private JProgressBar progressBar;
	private GsButton cancelButton;

	public QMCThread(boolean cnf, GsRegulatoryGraph g, GsRegulatoryVertex v, GsTreeExpression e, JTree t, JProgressBar jpb, GsButton b) {
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
		Collection<GsRegulatoryMultiEdge> l = graph.getIncomingEdges(vertex);
		algo.init(l, expression.getChilds());
		algo.exec();
		TreePath sel_path = tree.getLeadSelectionPath();
		expression.getGraphicPanel().setText(algo.getFunction(), 0);
		tree.setSelectionPath(sel_path);
	}
}
