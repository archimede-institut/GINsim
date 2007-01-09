package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;
import fr.univmrs.ibdm.GINsim.dynamicGraph.GsDynamicGraph;
import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.ibdm.GINsim.gui.GsActions;
import fr.univmrs.ibdm.GINsim.gui.GsEditModeDescriptor;
import fr.univmrs.ibdm.GINsim.gui.GsFileFilter;
import fr.univmrs.ibdm.GINsim.gui.GsParameterPanel;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.xml.GsGinmlHelper;
import fr.univmrs.ibdm.GINsim.xml.GsXMLWriter;

/**
 * The regulatory graph
 */
public final class GsRegulatoryGraph extends GsGraph {

	private JPanel optionPanel = null;
	private GsRegulatoryGraphPropertiesPanel parameterPanel = null;
	
	private int nextid=0;
	
    GsParameterPanel edgePanel = null;
    GsParameterPanel vertexPanel = null;
    
    private static GsGraph copiedGraph = null;
	public final static String zip_mainEntry = "regulatoryGraph.ginml";
    
    static {
        GsRegulatoryGraphDescriptor.registerObjectManager(new GsMutantListManager());
    }

    /**
     */
    public GsRegulatoryGraph() {
        this(null);
    }

    protected String getGraphZipName() {
    	return zip_mainEntry;
    }
    
    /**
     * @param savefilename
     */
    public GsRegulatoryGraph(String savefilename) {
        super(GsRegulatoryGraphDescriptor.getInstance(), savefilename);
        setDefaults();
    }
    /**
     * @param map
     * @param file
     */
    public GsRegulatoryGraph(Map map, File file) {
        this(file.getAbsolutePath());
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
        Object obj = new GsRegulatoryVertex(nextid++);
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
    			((GsRegulatoryMultiEdge)obj).addEdge(new GsRegulatoryEdge(param), this);
    			return obj;
    		}
        obj = new GsRegulatoryMultiEdge((GsRegulatoryVertex)source, (GsRegulatoryVertex)target, param);
        Object ret = graphManager.addEdge(source, target, obj);
        ((GsRegulatoryMultiEdge)obj).rescanSign(this);
        return ret;
    }

    protected void doSave(OutputStreamWriter os, int mode, boolean selectedOnly) throws GsException {
    	try {
            GsXMLWriter out = new GsXMLWriter(os, dtdFile);
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
    private void saveEdge(GsXMLWriter out, int mode, boolean selectedOnly) throws IOException {
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
    private void saveNode(GsXMLWriter out, int mode, boolean selectedOnly) throws IOException {
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
        ((GsRegulatoryMultiEdge)((GsDirectedEdge)edge).getUserObject()).addEdge(new GsRegulatoryEdge(), this);
    }

    /**
     * @param edge
     * @param param
     */
    public void addToExistingEdge(Object edge, int param) {
        ((GsRegulatoryMultiEdge)((GsDirectedEdge)edge).getUserObject()).addEdge(new GsRegulatoryEdge(param), this);
    }

    public GsParameterPanel getEdgeAttributePanel() {
        if (edgePanel == null) {
            edgePanel = new GsRegulatoryEdgeAttributePanel(mainFrame);
        }
        return edgePanel;
    }

    public GsParameterPanel getVertexAttributePanel() {
        if (vertexPanel == null) {
            vertexPanel = new GsRegulatoryVertexAttributePanel(mainFrame);
        }
        return vertexPanel;
    }

    public Vector getEditingModes() {
        Vector v_mode = new Vector();
        v_mode.add(new GsEditModeDescriptor("STR_addGene", "STR_addGene_descr", GsEnv.getIcon("insertsquare.gif"), GsActions.MODE_ADD_VERTEX, 0));
        v_mode.add(new GsEditModeDescriptor("STR_addPositivInteraction", "STR_addPositivInteraction_descr", GsEnv.getIcon("insertpositiveedge.gif"), GsActions.MODE_ADD_EDGE, 0));
        v_mode.add(new GsEditModeDescriptor("STR_addNegativInteraction", "STR_addNegativInteraction_descr", GsEnv.getIcon("insertnegativeedge.gif"), GsActions.MODE_ADD_EDGE, 1));
        v_mode.add(new GsEditModeDescriptor("STR_addUnknownInteraction", "STR_addUnknownInteraction_descr", GsEnv.getIcon("insertunknownedge.gif"), GsActions.MODE_ADD_EDGE, 2));
        v_mode.add(new GsEditModeDescriptor("STR_addEdgePoint", "STR_addEdgePoint_descr", GsEnv.getIcon("custumizeedgerouting.gif"), GsActions.MODE_ADD_EDGE_POINT, 0));
        return v_mode;
    }

    public void changeVertexId(Object vertex, String newId) throws GsException {
        GsRegulatoryVertex rvertex = (GsRegulatoryVertex)vertex;
        if (newId.equals(rvertex.getId())) {
            return;
        }
        
        Iterator it = graphManager.getVertexIterator();
        while (it.hasNext()) {
            if (newId.equals(((GsRegulatoryVertex)it.next()).getId())) {
                throw  new GsException(GsException.GRAVITY_ERROR, "id already exists");
            }
        }
        rvertex.setId(newId);
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
        }
        multiEdge.removeEdge(index, this);
    }
    /**
     * 
     * @param obj
     */
    public void removeEdge(Object obj) {
       GsRegulatoryMultiEdge edge = (GsRegulatoryMultiEdge)((GsDirectedEdge)obj).getUserObject();
       edge.getTarget().removeEdgeFromInteraction(edge);
       graphManager.removeEdge(edge.getSource(), edge.getTarget());
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
     * @param base
     * @param max
     * @return the new vertex.
     */
    public GsRegulatoryVertex addNewVertex(String id, String name, short base, short max) {
        GsRegulatoryVertex vertex = new GsRegulatoryVertex(id);
        if (name != null) {
            vertex.setName(name);
        }
        vertex.setMaxValue(max, this);
        vertex.setBaseValue(base, this);
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
    public GsRegulatoryEdgeInfo addNewEdge(String from, String to, short minvalue, short maxvalue, String sign) {
    	short vsign = GsRegulatoryEdge.SIGN_UNKNOWN;
    	for (short i=0 ; i<GsRegulatoryEdge.SIGN.length ; i++) {
    		if (GsRegulatoryEdge.SIGN[i].equals(sign)) {
    			vsign = i;
    			break;
    		}
    	}
    	return addNewEdge(from, to, minvalue, maxvalue, vsign);
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
    public GsRegulatoryEdgeInfo addNewEdge(String from, String to, short minvalue, short maxvalue, short sign) {
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
        
        GsRegulatoryEdge edge = new GsRegulatoryEdge(minvalue, maxvalue, sign);
        Object oedge = graphManager.getEdge(source, target);
        GsRegulatoryMultiEdge me;
        if (oedge == null) {
            me = new GsRegulatoryMultiEdge(source, target, edge, this);
            graphManager.addEdge(source, target, me);
            oedge = graphManager.getEdge(source, target);
        } else {
            me = (GsRegulatoryMultiEdge) ((GsDirectedEdge)oedge).getUserObject();
            me.addEdge(edge, this);
        }
        return new GsRegulatoryEdgeInfo((GsDirectedEdge)oedge, new GsEdgeIndex (me, me.getEdgeCount()-1));
    }

	/**
	 * @param vertex
     * @return a warning string if necessary
	 */
	public String applyNewMaxValue(GsRegulatoryVertex vertex) {
        String s = "";
		Iterator it = graphManager.getOutgoingEdges(vertex).iterator();
		while (it.hasNext()) {
			Object next = it.next();
			GsRegulatoryMultiEdge me;
			me = (GsRegulatoryMultiEdge)((GsDirectedEdge)next).getUserObject();
			me.applyNewMaxValue(vertex);
            // FIXME: add warning when updating
            // populate "s" or better way (tm) ?
            // a clean way would take undo into account
            // do it later...
		}
        return s;
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
			optionPanel = new GsRegulatoryGraphOptionPanel(t_mode, mainFrame != null ? 2 : 0);
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
	
	public JPanel getGraphParameterPanel() {
		if (parameterPanel == null) {
			parameterPanel = new GsRegulatoryGraphPropertiesPanel(this);
		}
		return parameterPanel;
	}

	public Vector getSpecificLayout() {
		return GsRegulatoryGraphDescriptor.getLayout();
	}
	public Vector getSpecificExport() {
		return GsRegulatoryGraphDescriptor.getExport();
	}
    public Vector getSpecificAction() {
        return GsRegulatoryGraphDescriptor.getAction();
    }
    public Vector getSpecificObjectManager() {
        return GsRegulatoryGraphDescriptor.getObjectManager();
    }

    protected GsGraph getCopiedGraph() {
        return copiedGraph;
    }
    
    protected Vector doMerge(GsGraph otherGraph) {
        if (!(otherGraph instanceof GsRegulatoryGraph)) {
            return null;
        }
        Vector ret = new Vector();
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
            cereader.setEdge(deOri);
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
                copiedGraph.getGraphManager().addVertex(vertex);
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
                cereader.setEdge(de);
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
        
        Vector regOrder = regGraph.nodeOrder;
        Vector dynOrder = dynGraph.getNodeOrder();
        if (regOrder == null || dynOrder == null || regOrder.size() != dynOrder.size()) {
            return false;
        }
        
        for (int i=0 ; i<regOrder.size() ; i++) {
            if (!( regOrder.get(i).toString().equals(dynOrder.get(i).toString()) )) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * @return a tree representation of logical parameters
     */
    public OmddNode[] getAllTrees() {
        OmddNode[] t_tree = new OmddNode[nodeOrder.size()];
        for (int i=0 ; i<nodeOrder.size() ; i++) {
            t_tree[i] = ((GsRegulatoryVertex)nodeOrder.get(i)).getTreeParameters(this).reduce();
        }
        return t_tree;
    }
}
