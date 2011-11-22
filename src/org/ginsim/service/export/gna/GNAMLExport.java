package org.ginsim.service.export.gna;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryVertex;
import org.ginsim.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.gui.shell.GsFileFilter;

import fr.univmrs.tagc.common.xml.XMLWriter;

/**
 * Encode a graph to GNAML format.
 *
 *    TODO: service and serviceGUI
 */
public class GNAMLExport extends ExportAction<RegulatoryGraph> {

	private static final GsFileFilter ffilter = new GsFileFilter(new String[] { "gnaml" }, "GNAML files");
	
	private FileWriter fout = null;
	private XMLWriter out = null;

    public GNAMLExport(RegulatoryGraph graph) {
    	super( graph, "STR_GNAML", "STR_GNAML_descr");
    }
    
	protected void doExport( String filename) throws IOException {
		run(filename);
	}

	@Override
	protected GsFileFilter getFileFilter() {
		return ffilter;
	}

	
	protected synchronized void run(String filename) throws IOException {
		this.fout = new FileWriter(filename);
		this.out = new XMLWriter(fout, null);
  		
  		fout.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
  		out.openTag("gnaml");
  		out.addAttr("xmlns", "http://www-gna.inrialpes.fr/gnaml/version1");
  		out.addAttr("version", "1.0");
  		
  		out.openTag("model");
  		out.addAttr("id", graph.getGraphName());
  		writeStatesVariables(out, graph);
  		out.closeTag();//model
  		
//  		out.openTag("initial-conditions");
//  		out.addAttr("id", "??");	//TODO: set the id
//  		out.closeTag();//initial-conditions
  		
  		out.closeTag();//gnaml
  		
  		fout.close();
	}
  		
	protected void writeStatesVariables(XMLWriter out, RegulatoryGraph graph) throws IOException {
		List nodeOrder = graph.getNodeOrder();
		Iterator it = nodeOrder.iterator();
		while (it.hasNext()) {
			writeStateVariable((RegulatoryVertex) it.next());
		}
	}
	
	protected void writeStateVariable(RegulatoryVertex node) throws IOException {
		int thresholdLevels = node.getMaxValue();
		String id = node.getId();
		out.openTag("state-variable");
		out.addAttr("id", id);
		
		out.addTag("zero-parameter", new String[] {"id", "zero_"+id});
		out.addTag("box-parameter", new String[] {"id", "max_"+id});
		
		out.openTag("list-of-threshold-parameters");
		for (int i = 1; i <= thresholdLevels; i++) {
			out.addTag("threshold-parameter", new String[] {"id", "t_"+id+"_"+i});
		}
		out.closeTag();//list-of-threshold-parameters
		
		out.openTag("list-of-synthesis-parameters");
		for (int i = 0; i <= thresholdLevels; i++) {
			out.addTag("synthesis-parameter", new String[] {"id", "k_"+id+"_"+i});
		}
		out.closeTag();//list-of-synthesis-parameters
		
		out.openTag("list-of-degradation-parameters");
		out.addTag("degradation-parameter", new String[] {"id", "g_"+id});
		out.closeTag();//list-of-degradation-parameters
		
		out.openTag("state-equation");
		writeStateEquation(node, thresholdLevels, id);
		out.closeTag();//state-equation
		
		out.openTag("parameter-inequalities");
		writeParameterInequalities(node, thresholdLevels, id);
		out.closeTag();//parameter-inequalities
		
		out.closeTag();//state-variable
	}
	
	protected void writeStateEquation(RegulatoryVertex node, int thresholdLevels, String id) throws IOException {
		out.openTag("math");
		out.addAttr("xmlns", "http://www.w3.org/1998/Math/MathML");
		out.openTag("apply");
		out.addTag("minus");

		OMDDNode mdd = node.getTreeParameters(graph).reduce();
		if (mdd.next != null) {
			List nodeOrder = graph.getNodeOrder();
			int [][] parcours = new int[nodeOrder.size()][4];
			boolean hasMoreThanOne = countNonZeroPath(mdd) > 1;
			if (hasMoreThanOne) {
				out.openTag("apply"); //K*s*s + K*s*s + ...
				out.addTag("plus");
				exploreNode(parcours, 0, id, mdd, nodeOrder);
				out.closeTag();//apply plus
			} else {
				exploreNode(parcours, 0, id, mdd, nodeOrder);
			}
		} else {
			out.openTag("ci");
			out.addContent("k_"+id+"_"+mdd.value);
			out.closeTag();
		}
		
		out.openTag("apply"); //- g_a * a
		out.addTag("times");
		out.addTagWithContent("ci", "g_"+id);
		out.addTagWithContent("ci", id);
		out.closeTag();//apply times
		
		out.closeTag();//apply minus
		out.closeTag();//math
	}
	private int countNonZeroPath(OMDDNode mdd) {
		if (mdd.next == null) {
			if (mdd.value > 0) {
				return 1;
			}
			return 0;
		}
		int ret = 0;
		OMDDNode refNode = null;
		for (int i=0 ; i<mdd.next.length ; i++) {
			OMDDNode nextNode = mdd.next[i];
			if (nextNode != refNode) {
				refNode = nextNode;
				ret += countNonZeroPath(mdd.next[i]);
			}
		}
		return ret;
	}
	protected void exploreNode(int[][] parcours, int deep, String topNodeId, OMDDNode node, List nodeOrder) throws IOException {
		if (node.next == null) {
			if (node.value > 0) {
				if (deep > 0) {
					out.openTag("apply");
					out.addTag("times");
				}
				out.addTagWithContent("ci", "k_"+topNodeId+"_"+node.value);
				String nodeName;
				for (int i = 0; i < deep; i++) {
					nodeName = getVertexNameForLevel(parcours[i][2], nodeOrder);//level
					if (parcours[i][0] > 0) {
						stepPlus(nodeName, parcours[i][0]);
					}
					if (parcours[i][1] < parcours[i][3]) {
						stepMinus(nodeName, parcours[i][1]);
					}
				}
				if (deep > 0) {
					out.closeTag();//apply times
				}
			}
			return ;
		}
		OMDDNode currentChild;
		for (int i = 0 ; i < node.next.length ; i++) {
			currentChild = node.next[i];
			int begin = i;
			int end;
			for (end=i+1 ; end < node.next.length && currentChild == node.next[end]; end++, i++) {
				// nothing to do
			}
			parcours[deep][0] = begin;
			parcours[deep][1] = end;
			parcours[deep][2] = node.level;
			parcours[deep][3] = node.next.length;
			exploreNode(parcours, deep+1, topNodeId, node.next[begin], nodeOrder);
		}
	}
	
	protected void stepPlus(String id, int i) throws IOException {
		out.openTag("apply");
		out.addTag("csymbol", new String[] {"encoding", "text", "definitionURL", "http://www-gna.inrialpes.fr/gnaml/symbols/step-plus"}, "s+");
		out.addTagWithContent("ci", id);
		out.addTagWithContent("ci", "t_"+id+"_"+i);
		out.closeTag();
	}
	protected void stepMinus(String id, int i) throws IOException {
		out.openTag("apply");
		out.addTag("csymbol", new String[] {"encoding", "text", "definitionURL", "http://www-gna.inrialpes.fr/gnaml/symbols/step-minus"}, "s-");
		out.addTagWithContent("ci", id);
		out.addTagWithContent("ci", "t_"+id+"_"+i);
		out.closeTag();
	}
	
	protected void writeParameterInequalities(RegulatoryVertex node, int thresholdLevels, String id) throws IOException {
		String g = "g_"+id;
		String K = "k_"+id+"_";
		String t = "t_"+id+"_";
		
		out.openTag("math");
		out.addAttr("xmlns", "http://www.w3.org/1998/Math/MathML");
		out.openTag("apply");
		out.addTag("lt");
		out.addTagWithContent("ci", "zero_"+id);
		for (int i = 0; i <= thresholdLevels; i++) {
			if (i>0) {
				out.addTagWithContent("ci", t+i);
			}
			out.openTag("apply");
			out.addTag("divide");
			out.addTagWithContent("ci", K+i);
			out.addTagWithContent("ci", g);
			out.closeTag();//apply
		}
		out.addTagWithContent("ci", "max_"+id);
		out.closeTag();//apply
		out.closeTag();//math
	}

	/**
	 * Return the ID of a node using his order and node order for the graph.
	 * @param order : The order of the node
	 * @param nodeOrder : The node order (in the graph)
	 * @return the ID as string
	 */
	protected String getVertexNameForLevel(int level, List nodeOrder) {
		return ((RegulatoryVertex) nodeOrder.get(level)).getId();
	}
}
