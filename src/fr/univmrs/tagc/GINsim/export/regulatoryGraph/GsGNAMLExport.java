package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.univmrs.tagc.GINsim.export.GsAbstractExport;
import fr.univmrs.tagc.GINsim.export.GsExportConfig;
import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.xml.XMLWriter;

/**
 * Encode a graph to GNAML format.
 * 
 * 
 */
public class GsGNAMLExport extends GsAbstractExport {
	private GsExportConfig config = null;
	private FileWriter fout = null;
	private XMLWriter out = null;
	private GsRegulatoryGraph graph;

    public GsGNAMLExport() {
		id = "GNAML";
		extension = ".gnaml";
		filter = new String[] { "gnaml" };
		filterDescr = "GNAML files";
    }
    
	public GsPluggableActionDescriptor[] getT_action(int actionType, GsGraph graph) {
        if (graph instanceof GsRegulatoryGraph) {
        	return new GsPluggableActionDescriptor[] {
        			new GsPluggableActionDescriptor("STR_GNAML", "STR_GNAML_descr", null, this, ACTION_EXPORT, 0)
        	};
        }
        return null;
	}

	protected void doExport(GsExportConfig config) {
		this.config = config;
		try {
			long l = System.currentTimeMillis();
			run();
			System.out.println("gna export: done in "+(System.currentTimeMillis()-l)+"ms");
		} catch (IOException e) {
			e.printStackTrace();
			GsEnv.error(new GsException(GsException.GRAVITY_ERROR, e), null);
		}		
	}

	protected synchronized void run() throws IOException {
		this.graph = (GsRegulatoryGraph) config.getGraph();
		this.fout = new FileWriter(config.getFilename());
		this.out = new XMLWriter(fout, null);
  		
  		fout.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
  		out.openTag("gnaml");
  		out.addAttr("xmlns", "http://www-gna.inrialpes.fr/gnaml/version1");
  		out.addAttr("version", "1.0");
  		
  		out.openTag("model");
  		out.addAttr("id", graph.getGraphName());
  		writeStatesVariables(out, graph);
  		out.closeTag();//model
  		
  		out.openTag("initial-conditions");
  		out.addAttr("id", "??");	//TODO: set the id
  		
  		out.closeTag();//initial-conditions
  		
  		out.closeTag();//gnaml
  		
  		fout.close();
	}
  		
	protected void writeStatesVariables(XMLWriter out, GsRegulatoryGraph graph) throws IOException {
		List nodeOrder = graph.getNodeOrder();
		Iterator it = nodeOrder.iterator();
		while (it.hasNext()) {
			writeStateVariable((GsRegulatoryVertex) it.next());
		}
	}
	
	protected void writeStateVariable(GsRegulatoryVertex node) throws IOException {
		int thresholdLevels = node.getMaxValue();
		String id = node.getId();
		out.openTag("state-variable");
		
		out.addTag("zero-parameter", new String[] {"id", "zero_"+id});
		out.addTag("box-parameter", new String[] {"id", "max_"+id});
		
		out.openTag("list-of-threshold-parameters");
		for (int i = 1; i <= thresholdLevels; i++) {
			out.addTag("threshold-parameter", new String[] {"id", "t_"+id+"_"+i});
		}
		out.closeTag();//list-of-threshold-parameters
		
		out.openTag("list-of-synthesis-parameters");
		for (int i = 1; i <= thresholdLevels; i++) {
			out.addTag("synthesis-parameter", new String[] {"id", "K_"+id+"_"+i});
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
	
	private void writeStateEquation(GsRegulatoryVertex node, int thresholdLevels, String id) throws IOException {
		out.openTag("math");
		out.addAttr("xmlns", "http://www.w3.org/1998/Math/MathML");
		out.openTag("apply");
		out.addTag("minus");
		
		List pile = new ArrayList();
		exploreNode(pile, node.getId(), node.getTreeParameters(graph).reduce(), graph.getNodeOrder());
		Iterator it = pile.iterator();
		while (it.hasNext()) {
			String s = (String) it.next();
			if (s.charAt(0) == 'k') {
				fout.write("\n"+s+" * ");				
			} else if (s.charAt(0) == 's') {
				fout.write(s+"("+(String) it.next()+", "+(String) it.next()+") * ");				
			}
		}
		
		//- g_a * a
		out.openTag("apply");
		out.addTag("times");
		out.addTagWithContent("ci", "g_"+id);
		out.addTagWithContent("ci", id);
		out.closeTag();//apply
		
		out.closeTag();//apply
		out.closeTag();//math
	}
	
	private void exploreNode(List pile, String topNodeId, OmddNode node, List nodeOrder) throws IOException {
		if (node.next == null) {
			pile.add(0,"K_"+topNodeId+"_"+node.value);
			return ;
		}
		String nodeName = getVertexNameForLevel(node.level, nodeOrder);
		OmddNode currentChild;
		for (int i = node.next.length-1; i > 0; i--) {//ICI
			currentChild = node.next[i];
			int begin = i-1;
			int end = i;
			for (begin=i-1 ; begin > 0 && currentChild == node.next[begin]; begin--, i--);
			if (end < node.next.length) {
				pile.add(0,"t_"+nodeName+"_"+end);
				pile.add(0,nodeName);
				pile.add(0,"s-");
			}
			if (begin > 0) {
				pile.add(0,"t_"+nodeName+"_"+begin);
				pile.add(0,nodeName);
				pile.add(0,"s+");
			}
			exploreNode(pile, topNodeId, node.next[end], nodeOrder);
		}
	}
	
	private void stepPlus(String id, int i) throws IOException {
		out.openTag("apply");
		out.addTag("csymbol", new String[] {"encoding", "text", "definitionURL", "http://www-gna.inrialpes.fr/gnaml/symbols/step-plus"}, "s+");
		out.addTagWithContent("ci", id);
		out.addTagWithContent("ci", "t_"+id+"_"+i);
		out.closeTag();
	}
	private void stepMinus(String id, int i) throws IOException {
		out.openTag("apply");
		out.addTag("csymbol", new String[] {"encoding", "text", "definitionURL", "http://www-gna.inrialpes.fr/gnaml/symbols/step-minus"}, "s-");
		out.addTagWithContent("ci", id);
		out.addTagWithContent("ci", "t_"+id+"_"+i);
		out.closeTag();
	}
	
	private void writeParameterInequalities(GsRegulatoryVertex node, int thresholdLevels, String id) throws IOException {
		String g = "g_"+id;
		String K = "K_"+id+"_";
		String t = "t_"+id+"_";
		
		out.openTag("math");
		out.addAttr("xmlns", "http://www.w3.org/1998/Math/MathML");
		out.openTag("apply");
		out.addTag("lt");
		out.addTagWithContent("ci", "zero_"+id);
		for (int i = 1; i <= thresholdLevels; i++) {
			out.addTagWithContent("ci", t+i);
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


	


//			out.write("  degradation-parameters: g_" + id + "\n"
//					+ "  state-equation:\n    d/dt " + id + " = "
//					+ getGNAEq(node, nodeOrder)
//					+ " - g_"+id+" * "+id+"\n");
//			
//			//threshold-inequalities:
//			out.write("  threshold-inequalities: zero_"+id+" < ");
//			tmp = new StringBuffer();
//			for (int i = 1; i <= thresholdLevels; i++) {
//				tmp.append("t_"+id+i+" < ");
//				tmp.append("k_"+id+i+" / g_"+id+" < ");
//			}
//			out.write(tmp.toString());
//			out.write("max_"+id+"\n\n");
//		}
//		out.close();
//	}
//
//	private String getGNAEq(GsRegulatoryVertex node, List nodeOrder) {
//		String s = exploreNode(node.getId(), node.getTreeParameters(graph).reduce(), nodeOrder);
//		return s.substring(0, s.length()-3);
//	}
//
//	private String exploreNode(String topNodeId, OmddNode node, List nodeOrder) {
//		if (node.next == null) {
//			return "k_"+topNodeId+node.value+" + ";
//		} else {
//			String res = "";
//			String nodeName = getVertexNameForLevel(node.level, nodeOrder);
//			OmddNode currentChild;
//			for (int i = 0; i < node.next.length; i++) {
//				currentChild = node.next[i];
//				int begin = i;
//				int end = i+1;
//				for (end=i+1 ; end < node.next.length && currentChild == node.next[end]; end++, i++);
//				if (begin > 0) {
//					res += "s+("+nodeName+", t_"+nodeName+begin+")";
//				}
//				if (end < node.next.length) {
//					if (res.length() > 0) {
//						res += " * ";
//					}
//					res += "s-("+nodeName+", t_"+nodeName+end+")";
//				}
//				res += " * "+exploreNode(topNodeId, node.next[begin], nodeOrder);
//			}
//			return res;
//		}
//	}
	
	/**
	 * Return the ID of a node using his order and node order for the graph.
	 * @param order : The order of the node
	 * @param nodeOrder : The node order (in the graph)
	 * @return the ID as string
	 */
	private String getVertexNameForLevel(int order, List nodeOrder) {
		return ((GsRegulatoryVertex) nodeOrder.get(order)).getId();
	}
}
