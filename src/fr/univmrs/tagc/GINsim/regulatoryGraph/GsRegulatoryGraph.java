package fr.univmrs.tagc.GINsim.regulatoryGraph;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.dynamicGraph.GsDynamicGraph;
import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.*;
import fr.univmrs.tagc.GINsim.gui.GsActions;
import fr.univmrs.tagc.GINsim.gui.GsEditModeDescriptor;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.gui.GsParameterPanel;
import fr.univmrs.tagc.GINsim.xml.GsGinmlHelper;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.datastore.ObjectEditor;
import fr.univmrs.tagc.common.manageressources.ImageLoader;
import fr.univmrs.tagc.common.manageressources.Translator;
import fr.univmrs.tagc.common.mdd.DecisionDiagramInfo;
import fr.univmrs.tagc.common.mdd.MDDNode;
import fr.univmrs.tagc.common.xml.XMLWriter;

/**
 * The regulatory graph
 */
public final class GsRegulatoryGraph extends GsGraph implements GsGenericRegulatoryGraph {

	private JPanel optionPanel = null;
	private ObjectEditor graphEditor;

	private int nextid=0;

    GsParameterPanel edgePanel = null;
    ObjectEditor vertexEditor = null;
	private ObjectEditor edgeEditor;

    private static GsGraph copiedGraph = null;
	public final static String zip_mainEntry = "regulatoryGraph.ginml";

    static {
        GsRegulatoryGraphDescriptor.registerObjectManager(new GsMutantListManager());
    }

    /**
     */
    public GsRegulatoryGraph() {
        this(null, false);
    }

    protected String getGraphZipName() {
    	return zip_mainEntry;
    }

    /**
     * @param savefilename
     */
    public GsRegulatoryGraph(String savefilename, boolean parsing) {
        super(GsRegulatoryGraphDescriptor.getInstance(), savefilename, parsing);
        setDefaults();
    }
    /**
     * @param map
     * @param file
     */
    public GsRegulatoryGraph(Map map, File file) {
        this(file.getAbsolutePath(), true);
        GsRegulatoryParser parser = new GsRegulatoryParser();
        parser.parse(file, map, this);
		graphManager.ready();
    }

    private void setDefaults() {
        tabLabel = "STR_modelAttribute";

        	vReader = graphManager.getVertexAttributesReader();
        	eReader = graphManager.getEdgeAttributesReader();

        vReader.setDefaultVertexSize(55, 25);
        eReader.setDefaultEdgeSize(2);
        canDelete = true;
    }

    protected Object doInteractiveAddVertex(int param) {

        while ( graphManager.getVertexByName("G" + nextid) != null) {
        		nextid++;
        }
        Object obj = new GsRegulatoryVertex(nextid++, (GsRegulatoryGraph)graphManager.getGsGraph());
        if (graphManager.addVertex(obj)) {
        		nodeOrder.add(obj);
        		return obj;
        }
        return null;
    }

    protected Object doInteractiveAddEdge(Object source, Object target, int param) {
    	Object obj = graphManager.getEdge(source, target);
    	if (obj != null) {
    		obj = ((GsDirectedEdge)obj).getUserObject();
    		GsGraphNotificationAction action = new GsGraphNotificationAction() {
    			final String[] t_action = {"go"};
				public boolean timeout(GsGraph graph, Object data) {
					return true;
				}
				public boolean perform(GsGraph graph, Object data, int index) {
					graph.getGraphManager().select(data);
					return true;
				}
				public String[] getActionName() {
					return t_action;
				}
			
			};
	    	this.addNotificationMessage(new GsGraphNotificationMessage(this,
	    			Translator.getString("STR_usePanelToAddMoreEdges"),
	    			action,
	    			obj,
	    			GsGraphNotificationMessage.NOTIFICATION_WARNING));
    		return obj;
    	}
    	obj = new GsRegulatoryMultiEdge((GsRegulatoryVertex)source, (GsRegulatoryVertex)target, param);
    	Object ret = graphManager.addEdge(source, target, obj);
    	((GsRegulatoryMultiEdge)obj).rescanSign(this);
    	((GsRegulatoryVertex)target).incomingEdgeAdded((GsRegulatoryMultiEdge)obj);
    	return ret;
    }

    protected void doSave(OutputStreamWriter os, int mode, boolean selectedOnly) throws GsException {
    	try {
            XMLWriter out = new XMLWriter(os, dtdFile);
	  		out.write("<gxl xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n");
			out.write("\t<graph id=\"" + graphName + "\"");
			out.write(" class=\"regulatory\"");
			out.write(" nodeorder=\"" + stringNodeOrder() +"\"");
			out.write(">\n");
			saveNode(out, mode, selectedOnly);
			saveEdge(out, mode, selectedOnly);
            if (gsAnnotation != null) {
                gsAnnotation.toXML(out, null, 0);
            }
	  		out.write("\t</graph>\n");
	  		out.write("</gxl>\n");
        } catch (IOException e) {
            throw new GsException(GsException.GRAVITY_ERROR, Translator.getString("STR_unableToSave")+": "+ e.getMessage());
        }
    }

    /**
     * @param out
     * @param mode
     * @param selectedOnly
     * @throws IOException
     */
    private void saveEdge(XMLWriter out, int mode, boolean selectedOnly) throws IOException {
        Iterator it;
        if (selectedOnly) {
        		it = graphManager.getSelectedEdgeIterator();
        } else {
        		it = graphManager.getEdgeIterator();
        }
        switch (mode) {
	    	case 2:
	    	    GsEdgeAttributesReader ereader = graphManager.getEdgeAttributesReader();
		        while (it.hasNext()) {
		            GsDirectedEdge edge = (GsDirectedEdge) it.next();
		            ereader.setEdge(edge);
		            ((GsRegulatoryMultiEdge)edge.getUserObject()).toXML(out, GsGinmlHelper.getEdgeVS(ereader), mode);
		        }
		        break;
        	default:
		        while (it.hasNext()) {
		            GsDirectedEdge edge = (GsDirectedEdge) it.next();
		            ((GsRegulatoryMultiEdge)edge.getUserObject()).toXML(out, null, mode);
		        }
        }
    }

    /**
     * @param out
     * @param mode
     * @param selectedOnly
     * @throws IOException
     */
    private void saveNode(XMLWriter out, int mode, boolean selectedOnly) throws IOException {
    	Iterator it;
    	if (selectedOnly) {
    		it = graphManager.getSelectedVertexIterator();
    	} else {
    		it = graphManager.getVertexIterator();
    	}
    	if ( mode >=0) {
    	}

    	switch (mode) {
    		case 1:
    	        while (it.hasNext()) {
    	            Object vertex = it.next();
    	            String svs = "";
	                vReader.setVertex(vertex);
	                svs = GsGinmlHelper.getShortNodeVS(vReader);
    	            ((GsRegulatoryVertex)vertex).toXML(out, svs, mode);
    	        }
    			break;
    		case 2:
    	        while (it.hasNext()) {
    	            Object vertex = it.next();
    	            String svs = "";
	                vReader.setVertex(vertex);
	                svs = GsGinmlHelper.getFullNodeVS(vReader);
    	            ((GsRegulatoryVertex)vertex).toXML(out, svs, mode);
    	        }
    			break;
    		default:
    	        while (it.hasNext()) {
    	            Object vertex = it.next();
    	            ((GsRegulatoryVertex)vertex).toXML(out, "", mode);
    	        }
        }
    }

    /**
     * @param edge
     */
    public void addToExistingEdge(Object edge) {
        ((GsRegulatoryMultiEdge)((GsDirectedEdge)edge).getUserObject()).addEdge(this);
    }

    /**
     * @param edge
     * @param param
     */
    public void addToExistingEdge(Object edge, int param) {
        ((GsRegulatoryMultiEdge)((GsDirectedEdge)edge).getUserObject()).addEdge(this);
    }

    public ObjectEditor getEdgeEditor() {
    	if (edgeEditor == null) {
    		edgeEditor = new RegulatoryEdgeEditor(this);
    	}
    	return edgeEditor;
    }
    public ObjectEditor getVertexEditor() {
    	if (vertexEditor == null) {
    		vertexEditor = new RegulatoryVertexEditor(this);
    	}
    	return vertexEditor;
    }
    public Vector getEditingModes() {
        Vector v_mode = new Vector();
        v_mode.add(new GsEditModeDescriptor("STR_addGene", "STR_addGene_descr", ImageLoader.getImageIcon("insertsquare.gif"), GsActions.MODE_ADD_VERTEX, 0));
        v_mode.add(new GsEditModeDescriptor("STR_addPositivInteraction", "STR_addPositivInteraction_descr", ImageLoader.getImageIcon("insertpositiveedge.gif"), GsActions.MODE_ADD_EDGE, 0));
        v_mode.add(new GsEditModeDescriptor("STR_addNegativInteraction", "STR_addNegativInteraction_descr", ImageLoader.getImageIcon("insertnegativeedge.gif"), GsActions.MODE_ADD_EDGE, 1));
        v_mode.add(new GsEditModeDescriptor("STR_addUnknownInteraction", "STR_addUnknownInteraction_descr", ImageLoader.getImageIcon("insertunknownedge.gif"), GsActions.MODE_ADD_EDGE, 2));
        v_mode.add(new GsEditModeDescriptor("STR_addEdgePoint", "STR_addEdgePoint_descr", ImageLoader.getImageIcon("custumizeedgerouting.gif"), GsActions.MODE_ADD_EDGE_POINT, 0));
        return v_mode;
    }
    public boolean idExists(String newId) {
        Iterator it = graphManager.getVertexIterator();
        while (it.hasNext()) {
            if (newId.equals(((GsRegulatoryVertex)it.next()).getId())) {
                return true;
            }
        }
        return false;
    }
    public void changeVertexId(Object vertex, String newId) throws GsException {
        GsRegulatoryVertex rvertex = (GsRegulatoryVertex)vertex;
        if (newId.equals(rvertex.getId())) {
            return;
        }
        if (idExists(newId)) {
        	throw  new GsException(GsException.GRAVITY_ERROR, "id already exists");
        }
        if (!rvertex.setId(newId)) {
        	throw  new GsException(GsException.GRAVITY_ERROR, "invalid id");
        }
        fireMetaChange();
    }

    /**
     * @param multiEdge
     * @param index
     * @throws GsException
     */
    public void removeEdgeFromMultiEdge(GsRegulatoryMultiEdge multiEdge, int index) throws GsException {
        if (index >= multiEdge.getEdgeCount()) {
            throw new GsException(GsException.GRAVITY_ERROR, "STR_noSuchSubedge");
        }
        if (multiEdge.getEdgeCount() == 1) {
            removeEdge(multiEdge);
        } else {
			multiEdge.removeEdge(index, this);
		}
    }
    /**
     *
     * @param obj
     */
    public void removeEdge(Object obj) {
       GsRegulatoryMultiEdge edge = (GsRegulatoryMultiEdge)((GsDirectedEdge)obj).getUserObject();
       edge.markRemoved();
       graphManager.removeEdge(edge.getSource(), edge.getTarget());
       edge.getTarget().removeEdgeFromInteraction(edge);
       fireGraphChange(CHANGE_EDGEREMOVED, obj);
    }

    public void removeVertex(Object obj) {
        List edge = graphManager.getOutgoingEdges(obj);
        for (int i=edge.size()-1 ; i>=0 ; i--) {
            removeEdge(edge.get(i));
        }
        graphManager.removeVertex(obj);
        nodeOrder.remove(obj);
        fireGraphChange(CHANGE_VERTEXREMOVED, obj);
    }

    /**
     * add a vertex from textual parameters (for the parser).
     *
     * @param id
     * @param name
     * @param max
     * @return the new vertex.
     */
    public GsRegulatoryVertex addNewVertex(String id, String name, short max) {
        GsRegulatoryVertex vertex = new GsRegulatoryVertex(id, (GsRegulatoryGraph)graphManager.getGsGraph());
        if (name != null) {
            vertex.setName(name);
        }
        vertex.setMaxValue(max, this);
        if (graphManager.addVertex(vertex)) {
        		nodeOrder.add(vertex);
        }
        return vertex;
    }

    /**
     * add an edge from textual parameters (for the parser).
     * @param from
     * @param to
     * @param minvalue
     * @param maxvalue
     * @param sign
     * @return the new edge
     */
    public GsRegulatoryEdge addNewEdge(String from, String to, short minvalue, String sign) {
    	short vsign = GsRegulatoryMultiEdge.SIGN_UNKNOWN;
    	for (short i=0 ; i<GsRegulatoryMultiEdge.SIGN.length ; i++) {
    		if (GsRegulatoryMultiEdge.SIGN[i].equals(sign)) {
    			vsign = i;
    			break;
    		}
    	}
    	return addNewEdge(from, to, minvalue, vsign);
    }
    /**
     * add an edge from textual parameters (for the parser).
     * @param from
     * @param to
     * @param minvalue
     * @param maxvalue
     * @param sign
     * @return the new edge.
     */
    public GsRegulatoryEdge addNewEdge(String from, String to, short minvalue, short sign) {
        GsRegulatoryVertex source = null;
        GsRegulatoryVertex target = null;

        source = (GsRegulatoryVertex) graphManager.getVertexByName(from);
        if (from.equals(to)) {
            target = source;
        } else {
            target = (GsRegulatoryVertex) graphManager.getVertexByName(to);
        }

        if (source == null || target == null) {
            GsEnv.error(new GsException(GsException.GRAVITY_ERROR, "STR_noSuchVertex"), null);
            return null;
        }
        Object oedge = graphManager.getEdge(source, target);
        GsRegulatoryMultiEdge me = null;
        int index = 0;
        if (oedge == null) {
            me = new GsRegulatoryMultiEdge(source, target, sign, minvalue);
            graphManager.addEdge(source, target, me);
            oedge = graphManager.getEdge(source, target);
        } else {
            me = (GsRegulatoryMultiEdge) ((GsDirectedEdge)oedge).getUserObject();
            index = me.addEdge(sign, minvalue, this);
        }
        return me.getEdge(index);
    }

	/**
	 * @param vertex
     * @return a warning string if necessary
	 */
	public void canApplyNewMaxValue(GsRegulatoryVertex vertex, short newMax, List l_fixable, List l_conflict) {
		Iterator it = graphManager.getOutgoingEdges(vertex).iterator();
		while (it.hasNext()) {
			Object next = it.next();
			GsRegulatoryMultiEdge me;
			me = (GsRegulatoryMultiEdge)((GsDirectedEdge)next).getUserObject();
			me.canApplyNewMaxValue(newMax, l_fixable, l_conflict);
		}
	}

	protected FileFilter doGetFileFilter() {
		GsFileFilter ffilter = new GsFileFilter();
		ffilter.setExtensionList(new String[] {"zginml", "ginml"}, "(z)ginml files");
		return ffilter;
	}

	protected JPanel doGetFileChooserPanel() {
		return getOptionPanel();
	}

	private JPanel getOptionPanel() {
		if (optionPanel == null) {

            Object[] t_mode = { Translator.getString("STR_saveNone"),
                    Translator.getString("STR_savePosition"),
                    Translator.getString("STR_saveComplet") };
			optionPanel = new GsRegulatoryGraphOptionPanel(t_mode, this.saveMode);
		}
		return optionPanel;
	}

	private String stringNodeOrder() {
		String s = "";
		for (int i=0 ; i<nodeOrder.size() ; i++) {
			s += nodeOrder.get(i)+" ";
		}
		if (s.length() > 0) {
			return s.substring(0, s.length()-1);
		}
		return s;
	}

	public ObjectEditor getGraphEditor() {
		if (graphEditor == null) {
			graphEditor = new RegulatoryGraphEditor();
			graphEditor.setEditedObject(this);
		}
		return graphEditor;
	}

	public List getSpecificLayout() {
		return GsRegulatoryGraphDescriptor.getLayout();
	}
	public List getSpecificExport() {
		return GsRegulatoryGraphDescriptor.getExport();
	}
    public List getSpecificAction() {
        return GsRegulatoryGraphDescriptor.getAction();
    }
    public List getSpecificObjectManager() {
        return GsRegulatoryGraphDescriptor.getObjectManager();
    }

    protected GsGraph getCopiedGraph() {
        return copiedGraph;
    }

    protected List doMerge(GsGraph otherGraph) {
        if (!(otherGraph instanceof GsRegulatoryGraph)) {
            return null;
        }
        List ret = new ArrayList();
        HashMap copyMap = new HashMap();
        Iterator it = otherGraph.getGraphManager().getVertexIterator();
        GsVertexAttributesReader cvreader = otherGraph.getGraphManager().getVertexAttributesReader();
        while (it.hasNext()) {
            GsRegulatoryVertex vertexOri = (GsRegulatoryVertex)it.next();
            GsRegulatoryVertex vertex = (GsRegulatoryVertex)vertexOri.clone();
            addVertexWithNewId(vertex);
            cvreader.setVertex(vertexOri);
            vReader.setVertex(vertex);
            vReader.copyFrom(cvreader);
            vReader.refresh();
            copyMap.put(vertexOri, vertex);
            ret.add(vertex);
        }

        it = otherGraph.getGraphManager().getEdgeIterator();
        GsEdgeAttributesReader cereader = otherGraph.getGraphManager().getEdgeAttributesReader();
        while (it.hasNext()) {
            GsDirectedEdge deOri = (GsDirectedEdge)it.next();
            GsDirectedEdge de = (GsDirectedEdge) doInteractiveAddEdge(copyMap.get(deOri.getSourceVertex()), copyMap.get(deOri.getTargetVertex()), 0);
            GsRegulatoryMultiEdge edge = (GsRegulatoryMultiEdge)de.getUserObject();
            edge.copyFrom((GsRegulatoryMultiEdge)deOri.getUserObject());
            cereader.setEdge(deOri.getUserObject());
            eReader.setEdge(de);
            eReader.copyFrom(cereader);
            eReader.refresh();
            copyMap.put(deOri.getUserObject(), edge);
            ret.add(de);
        }

        it = otherGraph.getGraphManager().getVertexIterator();
        while (it.hasNext()) {
            ((GsRegulatoryVertex)it.next()).cleanupInteractionForNewGraph(copyMap);
        }
        return ret;
    }
    /**
     * @param vertex
     */
    private void addVertexWithNewId(GsRegulatoryVertex vertex) {
        String id = vertex.getId();
        if (graphManager.getVertexByName(id) == null) {
            graphManager.addVertex(vertex);
            nodeOrder.add(vertex);
            return;
        }
        int addon = 1;
        while ( graphManager.getVertexByName(id+"_"+addon) != null) {
            addon++;
        }
        vertex.setId(id+"_"+addon);
        graphManager.addVertex(vertex);
        nodeOrder.add(vertex);
    }

    protected GsGraph doCopySelection(Vector v_vertex, Vector v_edges) {
        GsRegulatoryGraph copiedGraph = new GsRegulatoryGraph();
        GsVertexAttributesReader cvreader = copiedGraph.getGraphManager().getVertexAttributesReader();
        HashMap copyMap = new HashMap();
        if (v_vertex != null) {
            for (int i=0 ; i<v_vertex.size() ; i++) {
                GsRegulatoryVertex vertexOri = (GsRegulatoryVertex)v_vertex.get(i);
                GsRegulatoryVertex vertex = (GsRegulatoryVertex)vertexOri.clone();
                copiedGraph.addVertexWithNewId(vertex);
                vReader.setVertex(vertexOri);
                cvreader.setVertex(vertex);
                cvreader.copyFrom(vReader);
                copyMap.put(vertexOri, vertex);
            }
        }

        if (v_edges != null) {
            GsEdgeAttributesReader cereader = copiedGraph.getGraphManager().getEdgeAttributesReader();
	        for (int i=0 ; i<v_edges.size() ; i++) {
                GsDirectedEdge deOri = (GsDirectedEdge)v_edges.get(i);
                GsRegulatoryMultiEdge edgeOri = (GsRegulatoryMultiEdge)deOri.getUserObject();
                GsDirectedEdge de = (GsDirectedEdge)copiedGraph.doInteractiveAddEdge(copyMap.get(edgeOri.getSourceVertex()), copyMap.get(edgeOri.getTargetVertex()), 0);
	            GsRegulatoryMultiEdge edge = (GsRegulatoryMultiEdge)de.getUserObject();
	            edge.copyFrom(edgeOri);
	            copyMap.put(edgeOri, edge);
                eReader.setEdge(deOri);
                cereader.setEdge(edge);
                cereader.copyFrom(eReader);
	        }
        }

        if (v_vertex != null) {
            for (int i=0 ; i<v_vertex.size() ; i++) {
                ((GsRegulatoryVertex)v_vertex.get(i)).cleanupInteractionForNewGraph(copyMap);
            }
        }

        GsRegulatoryGraph.copiedGraph = copiedGraph;
        return copiedGraph;
    }

    protected void setCopiedGraph(GsGraph graph) {
        if (graph != null && graph instanceof GsRegulatoryGraph) {
            copiedGraph = graph;
        } else {
            copiedGraph = null;
        }
    }

    /**
     * Test if an association between a regulatory graph and a state transition graph is valid:
     * all what we can do is checking the node-order to see if they obviously differ (by size of node's name).
     *
     * if this function returns true, it's _NOT_ a garanty that both graphs are compatibles,
     * for exemple the state transition graph could have higher max value for one of the node.
     *
     * @param regGraph
     * @param dynGraph
     * @return true if the two graph can be associated
     */
    public static boolean associationValid(GsRegulatoryGraph regGraph, GsDynamicGraph dynGraph) {
        if (regGraph == null || dynGraph == null) {
            return false;
        }

        List regOrder = regGraph.nodeOrder;
        List dynOrder = dynGraph.getNodeOrder();
        if (regOrder == null || dynOrder == null || regOrder.size() != dynOrder.size()) {
            return false;
        }

        for (int i=0 ; i<regOrder.size() ; i++) {
            if (!regOrder.get(i).toString().equals(dynOrder.get(i).toString())) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param focal if true, leaves are focal points. Otherwise their are directions (-1, 0, +1)
     * @return a tree representation of logical parameters
     */
    public OmddNode[] getAllTrees(boolean focal) {
        OmddNode[] t_tree = new OmddNode[nodeOrder.size()];
        for (int i=0 ; i<nodeOrder.size() ; i++) {
        	GsRegulatoryVertex vertex = (GsRegulatoryVertex)nodeOrder.get(i);
            t_tree[i] = vertex.getTreeParameters(this);
            if (!focal) {
            	// FIXME: does non-focal tree works correctly ??????
            	t_tree[i] = t_tree[i].buildNonFocalTree(i, vertex.getMaxValue()+1).reduce();
            } else {
            	t_tree[i] = t_tree[i].reduce();
            }
        }
        return t_tree;
    }

    /**
     * @param focal if true, leaves are focal points. Otherwise their are directions (-1, 0, +1)
     * @return a tree representation of logical parameters
     */
    public MDDNode[] getNewTrees(DecisionDiagramInfo ddi, boolean focal) {
        MDDNode[] t_tree = new MDDNode[nodeOrder.size()];
        for (int i=0 ; i<nodeOrder.size() ; i++) {
        	GsRegulatoryVertex vertex = (GsRegulatoryVertex)nodeOrder.get(i);
            t_tree[i] = vertex.getMDD(this, ddi);
            // TODO: port "non-focal" to the new MDD
        }
        return t_tree;
    }

	public List getNodeOrderForSimulation() {
		return getNodeOrder();
	}
	public OmddNode[] getParametersForSimulation(boolean focal) {
		return getAllTrees(focal);
	}
}
