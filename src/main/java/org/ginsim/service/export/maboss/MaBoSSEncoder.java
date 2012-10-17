package org.ginsim.service.export.maboss;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.PathSearcher;

/**
 * Export a Boolean LogicalModel into MaBoSS format
 * 
 * @author Aurelien Naldi
 */
public class MaBoSSEncoder {

	private final LogicalModel model;
	private final MDDManager ddmanager;
	private final PathSearcher searcher;
	private final List<NodeInfo> nodes;
	private final List<NodeInfo> extraNodes;
	
	public MaBoSSEncoder(LogicalModel model) {
		this.model = model;
		this.ddmanager = model.getMDDManager();
		this.nodes = model.getNodeOrder();
		this.extraNodes = model.getExtraComponents();
		this.searcher = new PathSearcher(ddmanager, 1);
	}
	
	public void write(Writer out) throws IOException {
		
		writeFunctions(nodes, model.getLogicalFunctions(), out);
		writeFunctions(model.getExtraComponents(), model.getExtraLogicalFunctions(), out);
	}

	public void writeConfig(Writer out) throws IOException {
		writeParams(nodes, out);
		writeParams(extraNodes, out);

		out.write("\n");
		writeInit(nodes, out);
		writeInit(extraNodes, out);
		
		// write generic config
		out.write("\n");
		out.write("discrete_time = 0;\n");
		out.write("use_physrandgen = FALSE;\n");
		out.write("seed_pseudorandom = 100;\n");
		out.write("sample_count = 500000;\n");
		out.write("\n");
		out.write("max_time = 5;\n");
		out.write("time_tick = 0.01;\n");
		out.write("\n");
		out.write("thread_count = 4;\n");
		out.write("\n");
		out.write("statdist_traj_count = 100;\n");
		out.write("statdist_cluster_threshold = 0.9;");
	}

	private void writeParams(List<NodeInfo> nodes, Writer out) throws IOException {
		for (NodeInfo ni: nodes) {
			String name = ni.getNodeID();
			out.write("$u_" + name + "=1;\n");
			out.write("$d_" + name + "=1;\n");
		}
	}

	private void writeInit(List<NodeInfo> nodes, Writer out) throws IOException {
		for (NodeInfo ni: nodes) {
			out.write(ni.getNodeID() + ".istate=0;\n");
		}
	}

	
	private void writeFunctions(List<NodeInfo> nodes, int[] functions, Writer out) throws IOException {
		
		for (int i=0 ; i<functions.length ; i++) {
			NodeInfo ni = nodes.get(i);
			int func = functions[i];
			String name = ni.getNodeID();
			
			if (ni.getMax() > 1) {
				throw new RuntimeException("Multivalued nodes not supported");
			}
			
			out.write("Node "+name+" {\n");
			
			if (ddmanager.isleaf(func)) {
				if (func == 0) {
					out.write("  rate_up = 0;\n");
					out.write("  rate_down = $u_" + name + ";\n");
				} else if (func == 1) {
					out.write("  rate_up = $u_" + name + ";\n");
					out.write("  rate_down = 0;\n");
				} else {
					throw new RuntimeException("Multivalued models not supported");
				}
			} else {
				out.write("  logic = "+getFunctionString(func) + ";\n");
				out.write("  rate_up = @logic ? $u_" + name + " : 0;\n");
				out.write("  rate_down = @logic ? 0 : $d_" + name + ";\n");
			}
			
			out.write("}\n\n");
		}
	}
	
	private String getFunctionString(int f) {
		
		StringBuffer sb = null;;
		int[] path = searcher.setNode(f);
		for (int leaf: searcher) {
			if (sb == null) {
				sb = new StringBuffer("(");
			} else {
				// add "or" term
				sb.append(" | (");
			}
			
			// add all terms
			boolean first = true;
			for (int i=0 ; i<path.length ; i++) {
				int val_i = path[i];
				if (val_i < 0) {
					continue;
				}
				
				if (first) {
					first = false;
				} else {
					sb.append(" & ");
				}
				
				String name_i = nodes.get(i).getNodeID();
				if (val_i == 0) {
					sb.append("!"+name_i);
				} else {
					sb.append(name_i);
				}
			}
			sb.append(")");
		}
		return sb.toString();
	}
}
