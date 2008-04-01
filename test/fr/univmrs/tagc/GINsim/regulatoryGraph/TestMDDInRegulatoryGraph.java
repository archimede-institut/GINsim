package fr.univmrs.tagc.GINsim.regulatoryGraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import junit.framework.TestCase;
import fr.univmrs.tagc.GINsim.graph.GsGinmlParser;
import fr.univmrs.tagc.common.TestTools;
import fr.univmrs.tagc.common.mdd.DecisionDiagramInfo;
import fr.univmrs.tagc.common.mdd.MDDNode;


public class TestMDDInRegulatoryGraph extends TestCase {
	File file = new File(TestTools.getTestDir(), "graph.ginml");

	GsRegulatoryGraph graph;
	DecisionDiagramInfo ddi;

	public TestMDDInRegulatoryGraph() throws FileNotFoundException {
		File file = new File(TestTools.getTestDir(), "graph.ginml");
		GsGinmlParser parser = new GsGinmlParser();
		this.graph = (GsRegulatoryGraph)parser.parse(new FileInputStream(file), null);
		// FIXME: Dealing with ddi is not yet nicely done
		this.ddi = DecisionDiagramInfo.getBalancedDDI(100);
	}

	public void testOMDD() {
		OmddNode[] t_omdd = graph.getAllTrees(true);
		System.out.println("as OMDD");
		for (int i=0 ; i<t_omdd.length ; i++) {
			System.out.println(i+": "+t_omdd[i]);
		}
		System.out.println();
	}
	public void testMDD() {
		System.out.println("as MDD");
		MDDNode[] t_omdd = graph.getNewTrees(ddi, true);
		for (int i=0 ; i<t_omdd.length ; i++) {
			System.out.println(i+": "+t_omdd[i]);
		}
		System.out.println();
	}
}
