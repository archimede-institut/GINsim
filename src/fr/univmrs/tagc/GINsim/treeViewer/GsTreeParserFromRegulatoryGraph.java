package fr.univmrs.tagc.GINsim.treeViewer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.common.ColorPalette;

public class GsTreeParserFromRegulatoryGraph extends GsTreeParser {
	private final static int debug = 0;
	
	public static final String PARAM_REGGRAPH = "pfrg_regGraph";
	public static final String PARAM_INITIALVERTEXINDEX = "pfrg_initialVertex";
	
	/**
	 * Indicates the maximum count of terminal.
	 */
	private int max_terminal;
	/**
	 * Indicates for each depth, the sub-total (width) of children or 0 if the depth is not skipped (realDepth == -1).
	 */
	private int[] widthPerDepth;
	/**
	 * Indicates for each depth, the total (width) of children or 0 if the depth is not skipped (realDepth == -1).
	 */
	private int[] widthPerDepth_acc;
	/**
	 * As the omdd diagram could not contain all the levels (some could be skipped...), 
	 * we try to reduce the tree width, by assigning a depth to each __used__ level.
	 * 
	 * This can be achieved in two ways, 
	 * 		* by a first pass on the omdd (good for diagram)
	 *      * by computing the incoming vertices from the regulatoryGraph (good for tree)
	 *      
	 * A value of -2, indicates the depth correspond to a skipped level.
	 */
	private int[] realDetph;
	/**
	 * The total number of levels that are not skipped
	 */
	private int total_levels;
	/**
	 * The level of the last depth (corresponding to the terminal node in widthPerDepth)
	 */
	private int max_depth;
	

	protected GsRegulatoryGraph regGraph;


	public void init() {
		int initial_gene_id = ((Integer)getParameter(PARAM_INITIALVERTEXINDEX)).intValue();
		nodeOrder = (List)getParameter(PARAM_NODEORDER);
		regGraph = (GsRegulatoryGraph)getParameter(PARAM_REGGRAPH);

		GsRegulatoryVertex initialVertex = (GsRegulatoryVertex) nodeOrder.get(initial_gene_id);
		
		this.root = initialVertex.getTreeParameters(regGraph);
		widthPerDepth = widthPerDepth_acc = realDetph = null;
		total_levels = max_depth = 0;
		max_terminal = initialVertex.getMaxValue()+1;
		initRealDepth(initialVertex);
	}

	public void parseOmdd() {
		if (tree.getMode() == GsTree.MODE_TREE) {
			createTreeFromOmdd(root);
		} else {
			createDiagramFromOmdd(root);
			
		}	}

	public void updateLayout(GsVertexAttributesReader vreader, GsTreeNode vertex) {
		vreader.setVertex(vertex);
		int total_width = getTerminalWidth()*GsTreeNode.PADDING_HORIZONTAL;
		if (vertex.getType() == GsTreeNode.TYPE_LEAF) {
			vreader.setShape(GsVertexAttributesReader.SHAPE_ELLIPSE);
			vreader.setBackgroundColor(ColorPalette.defaultPalette[vertex.getValue()+1]);
			vreader.setBorder(0);
			if (vertex.getDepth() != -1) {
	    		vreader.setPos((int)((vertex.getWidth()-0.5)*total_width/getWidthPerDepth_acc(vertex))+100, getTotalLevels()*GsTreeNode.PADDING_VERTICAL+40);
			} else {
	    		vreader.setPos((int)((vertex.getWidth()+0.5)*total_width/getMaxTerminal())+100, getTotalLevels()*GsTreeNode.PADDING_VERTICAL+40);
			}
		} else {
			vreader.setShape(GsVertexAttributesReader.SHAPE_RECTANGLE);			
			if (vertex.getValue() == GsTreeNode.SKIPPED) {
				vreader.setBackgroundColor(Color.WHITE);
				vreader.setForegroundColor(Color.GRAY);
			}
			else vreader.setBackgroundColor(ColorPalette.defaultPalette[0]);
			vreader.setPos((int)((vertex.getWidth()-0.5)*total_width/getWidthPerDepth_acc(vertex))+100, (getRealDepth(vertex)+1)*GsTreeNode.PADDING_VERTICAL-40);
		}
		vreader.refresh();
	}

	/**
	 * Initialize the <b>realDepth</b> array, and <b>max_terminal</b> from an initial vertex, assuming regGraph is defined
	 * @param initialVertex
	 */
	public void initRealDepth(GsRegulatoryVertex initialVertex) {
		realDetph = new int[regGraph.getNodeOrder().size()+1]; //+1 for the leafs
		List nodeOrder = regGraph.getNodeOrder();
		int i = 0;
		for (Iterator it = regGraph.getGraphManager().getIncomingEdges(initialVertex).iterator(); it.hasNext();) {
			GsDirectedEdge e = ((GsDirectedEdge) it.next());
			GsRegulatoryVertex source = (GsRegulatoryVertex) e.getSourceVertex();
			i = 0;
			for (Iterator it2 = nodeOrder.iterator(); it2.hasNext(); i++) {
				GsRegulatoryVertex v = (GsRegulatoryVertex) it2.next();
				if (v.equals(source)) {
					realDetph[i] = -1;
				}
			}
		}
		int next_realDepth = 0;
		for (i = 0; i < realDetph.length; i++) {
			if (realDetph[i] == -1) {
				total_levels++;
				realDetph[i] = next_realDepth++;
				log("realDetph["+i+"] = "+realDetph[i]+" for  "+nodeOrder.get(i));
			} else realDetph[i] = -2;
		}
	}
	
	
	public void computeWidthPerDepthFromRegGraph() {
		List nodeOrder = regGraph.getNodeOrder(); 
	
		widthPerDepth = new int[nodeOrder.size()+1];
		widthPerDepth_acc = new int[nodeOrder.size()+1];

		int last_real = -1;

		Iterator it = nodeOrder.iterator();
		for (int i = 0 ; it.hasNext() ; i++) {
			GsRegulatoryVertex v = (GsRegulatoryVertex) it.next();
			if (realDetph[i] != -2) {
				int max = v.getMaxValue()+1;
				widthPerDepth[i] = max;
				if (last_real != -1) widthPerDepth_acc[i] = widthPerDepth_acc[last_real] * widthPerDepth[last_real];
				else widthPerDepth_acc[i] = 1;
				last_real = i;
				log("widthPerDepth["+i+"] = "+widthPerDepth[i]+" , "+widthPerDepth_acc[i]+"  @  "+realDetph[i]+" "+v);
			}
		}
		max_depth = last_real+1;
		if (last_real != -1) widthPerDepth_acc[max_depth] = widthPerDepth_acc[last_real] * widthPerDepth[last_real];
		log("widthPerDepth_acc["+(max_depth)+"] = 0, "+widthPerDepth_acc[max_depth]+" @ "+last_real+" terminal");
	}	
	
	public void createDiagramFromOmdd(OmddNode root) {
		computeWidthPerDepthFromRegGraph();
		int[] currentWidthPerDepth = new int[widthPerDepth.length];
		tree.root = _createDiagramFromOmdd(root, 0, currentWidthPerDepth, graphManager.getEdgeAttributesReader());
	}
	private GsTreeNode _createDiagramFromOmdd(OmddNode o, int lastLevel, int[] currentWidthPerDepth, GsEdgeAttributesReader ereader) {
		GsTreeNode treeNode;
		if (o.next == null) {
			int mult = jump(lastLevel, max_depth, currentWidthPerDepth);
		
			if (tree.getMode() == GsTree.MODE_DIAGRAM_WITH_MULTIPLE_LEAFS) {
				log(tab(max_depth)+"leaf : value:"+o.value+", level:"+max_depth+" "+(currentWidthPerDepth[max_depth]+1)+"/"+widthPerDepth_acc[max_depth]);
				treeNode = new GsTreeNode(""+o.value, max_depth, ++currentWidthPerDepth[max_depth], GsTreeNode.TYPE_LEAF, o.value);
				if (mult > 1) currentWidthPerDepth[max_depth] += mult-1;
				tree.addVertex(treeNode);
			} else { // if (mode == MODE_DIAGRAM) {
				treeNode = GsTree.leafs[o.value];
				if (!tree.containsNode(treeNode)) {
					tree.addVertex(treeNode);
				}	
			}
			return treeNode;
		}
		
		log(tab(realDetph[o.level])+"branch : level:"+o.level+" wpd:"+(currentWidthPerDepth[o.level]+1)+"/"+widthPerDepth_acc[o.level]);
		treeNode = new GsTreeNode(((GsRegulatoryVertex)regGraph.getNodeOrder().get(o.level)).getId(), o.level, ++currentWidthPerDepth[o.level], GsTreeNode.TYPE_BRANCH); 
		tree.addVertex(treeNode);
		
		//jump(lastLevel, o.level, currentWidthPerDepth); //In fact this is implicit
		
		for (int i = 0 ; i < o.next.length ; i++) { //For all the children
	    	GsTreeNode child = _createDiagramFromOmdd(o.next[i], o.level, currentWidthPerDepth, ereader);
	    	linkNode(treeNode, child, i, ereader);
	    }
	    return treeNode;
	}
	
	public void createTreeFromOmdd(OmddNode root) {
		computeWidthPerDepthFromRegGraph();
		int[] currentWidthPerDepth = new int[widthPerDepth.length];
		tree.root = _createTreeFromOmdd(root, 0, null, 0, currentWidthPerDepth, graphManager.getEdgeAttributesReader());
	}
	private GsTreeNode _createTreeFromOmdd(OmddNode o, int lastLevel, GsTreeNode parent, int childIndex, int[] currentWidthPerDepth, GsEdgeAttributesReader ereader) {
		GsTreeNode treeNode = null;
		if (o.next == null) {
			int mult = 1;
			List parents = new ArrayList();
			parents.add(parent);
			for (int j = lastLevel+1 ; j < max_depth ; j++) { //For all the missing genes
				if (realDetph[j] != -2) {
					parents = addChildren(j, mult, parents, childIndex, currentWidthPerDepth, ereader);
					mult = widthPerDepth[j];
				}
			}
			for (Iterator it = parents.iterator(); it.hasNext();) {
				GsTreeNode p = (GsTreeNode) it.next();
				if (mult > 1) {
					for (int i = 0; i < max_terminal; i++) {
						log(tab(max_depth)+"S leaf : value:"+o.value+", level:"+max_depth+" "+(currentWidthPerDepth[max_depth]+1)+"/"+widthPerDepth_acc[max_depth]);
						treeNode = new GsTreeNode(""+o.value, max_depth, ++currentWidthPerDepth[max_depth], GsTreeNode.TYPE_LEAF, o.value);
						tree.addVertex(treeNode);
				    	linkNode(p, treeNode, i, ereader);
					}
				} else {
					log(tab(max_depth)+"leaf : value:"+o.value+", level:"+max_depth+" "+(currentWidthPerDepth[max_depth]+1)+"/"+widthPerDepth_acc[max_depth]);
					treeNode = new GsTreeNode(""+o.value, max_depth, ++currentWidthPerDepth[max_depth], GsTreeNode.TYPE_LEAF, o.value);
					tree.addVertex(treeNode);
			    	linkNode(p, treeNode, childIndex, ereader);	
				}
			}
			return null;
		}
		
		log(tab(realDetph[o.level])+"branch : level:"+o.level+" wpd:"+(currentWidthPerDepth[o.level]+1)+"/"+widthPerDepth_acc[o.level]);
		treeNode = new GsTreeNode(((GsRegulatoryVertex)regGraph.getNodeOrder().get(o.level)).getId(), o.level, ++currentWidthPerDepth[o.level], GsTreeNode.TYPE_BRANCH); 
		tree.addVertex(treeNode);
		
				
		for (int i = 0 ; i < o.next.length ; i++) { //For all the children
	    	GsTreeNode child = _createTreeFromOmdd(o.next[i], o.level, treeNode, i, currentWidthPerDepth, ereader);
			if (child != null) linkNode(treeNode, child, i, ereader);
	    }
	    return treeNode;
	}

		
 
	
	private List addChildren(int j, int mult, List parents, int childIndex, int[] currentWidthPerDepth, GsEdgeAttributesReader ereader) {
		List newParents = new ArrayList(mult);
		
		while (realDetph[j] == -2 && j < max_depth) j++; //Get the child level
		
		String parentId = ((GsRegulatoryVertex)regGraph.getNodeOrder().get(j)).getId();
		
		for (Iterator it = parents.iterator(); it.hasNext();) {
			GsTreeNode o  = (GsTreeNode) it.next();
			for (int i = 0 ; i < mult ; i++) {
				log(tab(realDetph[j])+" S branch : level:"+j+" wpd:"+(currentWidthPerDepth[j]+1)+"/"+widthPerDepth_acc[j]);
				GsTreeNode treeNode = new GsTreeNode(parentId, j, ++currentWidthPerDepth[j], GsTreeNode.TYPE_BRANCH, GsTreeNode.SKIPPED);
				newParents.add(treeNode);
				tree.addVertex(treeNode);
				linkNode(o, treeNode, childIndex, ereader);
			}
		}
		return newParents;
	}

	private int jump(int lastLevel, int maxLevel, int[] currentWidthPerDepth) {
		int mult = 1;
		for (int j = lastLevel+1 ; j < maxLevel ; j++) { //For all the missing genes
			if (realDetph[j] != -2) { //FIXME: and != 0 ??????
				currentWidthPerDepth[j] += mult;
				mult *= widthPerDepth[j];
			}
		}
		return mult;
	}
	
	private void linkNode(GsTreeNode parent, GsTreeNode child, int colorIndex, GsEdgeAttributesReader ereader) {
		Object e = tree.addEdge(parent, child);
		ereader.setEdge(e);
		ereader.setLineColor(ColorPalette.defaultPalette[colorIndex+1]);
    	if (child.isLeaf()) {
    		ereader.setDash(new float[] {10, 4, 3, 5});
    	}
		ereader.refresh();
	}

	public int[] getWidthPerDepth() { return widthPerDepth; }
	public int[] getWidthPerDepth_acc() { return widthPerDepth_acc; }
	public int getMaxTerminal() { return max_terminal; }
	public int getMaxDepth() { return max_depth; }
	public int[] getRealDetph() { return realDetph; }
	public int getTerminalWidth() { return widthPerDepth_acc[max_depth]; }
	public int getTotalLevels() { return total_levels; }
	
	private int getRealDepth(GsTreeNode node) {
		if (node.getDepth() == -1) return getMaxDepth();
		return getRealDetph()[node.getDepth()];
	}
	private int getWidthPerDepth_acc(GsTreeNode node) {
		if (node.getDepth() == -1) return getWidthPerDepth_acc()[getMaxDepth()];
		return getWidthPerDepth_acc()[node.getDepth()];
	}
	
	private void log(String s) {
		if (debug > 0) System.out.println(s);
	}
}