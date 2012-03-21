package org.ginsim.service.export.sbml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdgeSign;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialState;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalParameter;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalParameterList;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDBrowserListener;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNodeBrowser;
import org.ginsim.core.graph.view.NodeAttributesReader;

public class SBMLQualEncoder implements OMDDBrowserListener{

	public static final String L3_QUALI_URL = "http://www.sbml.org/sbml/level3/version1/qual/version1";
	public static final String L3_LAYOUT_URL = "http://www.sbml.org/sbml/level3/version1/layout/version1";

	private static final boolean LAYOUT = true;
	
    List<RegulatoryNode> v_no;
    int len;
    OMDDNode[] t_tree;
    OMDDNodeBrowser browser;
    int curValue;
    XMLWriter out;
	
	/*
	 * This is where the real export is done.
	 * This method will be called by GsAbstractExport once the export configuration panel has been properly filled.
	 */
	protected void doExport( RegulatoryGraph graph, SBMLQualConfig config, String filename) throws IOException {

        v_no = graph.getNodeOrder();
        len = v_no.size();
        t_tree = ((RegulatoryGraph)graph).getAllTrees(true);
        browser = new OMDDNodeBrowser(this, t_tree.length);
        
        byte[][] t_markup = new byte[len][2];
		Iterator itinit = config.getInitialState().keySet().iterator();
		Map m_initstates = null;
		if (itinit.hasNext()) {
			m_initstates = ((InitialState) itinit.next()).getMap();
		}
		itinit = null;
		if (m_initstates == null) {
			m_initstates = new HashMap();
		}
        for (int i=0 ; i<len ; i++) {
            RegulatoryNode vertex = (RegulatoryNode)v_no.get(i);
            // default initial markup = 0
            t_markup[i][0] = 0;
            t_markup[i][1] = vertex.getMaxValue();
            List init = (List)m_initstates.get(vertex);
            if (init != null && init.size() > 0) {
                t_markup[i][0] = ((Integer)init.get(0)).byteValue();
            }
        }

        // FIXME: DTD for SBML ?
        OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8");
        out = new XMLWriter(os, null);
        String s_compartment = "c_" + graph.getGraphName();
        out.openTag("sbml");
        out.addAttr("xmlns", "http://www.sbml.org/sbml/level3/version1/core");
        out.addAttr("level", "3");
        out.addAttr("version", "1");
        out.addAttr("xmlns:qual", L3_QUALI_URL);
        out.addAttr("qual:required", "true");
        if (LAYOUT) {
        	out.addAttr("xmlns:layout", L3_LAYOUT_URL);
        	out.addAttr("layout:required", "false");
        }
        
        out.openTag("model");
        String graph_path = GraphManager.getInstance().getGraphPath( graph);
        if( graph_path != null){
        	out.addAttr("id", getPrevFilename( graph_path));
        }
        else{
        	out.addAttr("id", graph.getGraphName());
        }
        
        out.openTag("listOfCompartments");
        out.openTag("compartment");
        out.addAttr("id", s_compartment);
        out.addAttr("constant", "false");
        out.closeTag();
        out.closeTag();
        
        // layout information
        if (LAYOUT) {
            out.openTag("layout:listOfLayouts");
            out.addAttr("xmlns", L3_LAYOUT_URL);
            out.openTag("layout:layout");
            out.openTag("layout:listOfSpeciesGlyphs");

            NodeAttributesReader vreader = graph.getNodeAttributeReader();
            for (RegulatoryNode node: v_no) {
            	vreader.setNode(node);
            	out.openTag("layout:speciesGlyph");
            	out.addAttr("layout:species", node.getId());
            	out.openTag("layout:boundingBox");
            	
            	out.openTag("layout:position");
            	out.addAttr("layout:x", ""+vreader.getX());
            	out.addAttr("layout:y", ""+vreader.getY());
            	out.addAttr("layout:z", "0");
                out.closeTag();
            	out.openTag("layout:dimensions");
            	out.addAttr("layout:width", ""+vreader.getWidth());
            	out.addAttr("layout:height", ""+vreader.getHeight());
            	out.addAttr("layout:depth", "1");
                out.closeTag();
                
                out.closeTag();
                out.closeTag();
            }
            
            out.closeTag();
            out.closeTag();
            out.closeTag();
        }
        
        // List all components
        out.openTag("qual:listOfQualitativeSpecies");
        out.addAttr("xmlns", L3_QUALI_URL);

        for (int i=0 ; i<t_tree.length ; i++) {
            RegulatoryNode node = v_no.get(i);
            String s_id = node.getId();
            String s_name = node.getName();
            out.openTag("qual:qualitativeSpecies");
            out.addAttr("qual:id", s_id);
            if ((s_name != null) && (!s_name.equals("noName"))) {
            	out.addAttr("qual:name",s_name);
            } 
            out.addAttr("qual:compartment",s_compartment);
            out.addAttr("qual:maxLevel",""+node.getMaxValue());
            out.addAttr("qual:initialLevel",""+t_markup[i][0]);
            if (node.isInput()) {
                out.addAttr("qual:boundaryCondition", "true");
                out.addAttr("qual:constant", "true");
            }
            else {
            	out.addAttr("qual:boundaryCondition", "false");
                out.addAttr("qual:constant", "false");
            }
            out.closeTag();
        }
        out.closeTag(); // list of species
        
        // Dynamical rules: a transition for each component
        out.openTag("qual:listOfTransitions");
        out.addAttr("xmlns", L3_QUALI_URL);
        for (int i=0 ; i<t_tree.length ; i++) {
            RegulatoryNode regulatoryNode = (RegulatoryNode)v_no.get(i);
            if (regulatoryNode.isInput()) {
            	continue;
            }
            OMDDNode node = t_tree[i];
            String s_node = regulatoryNode.getId();
            out.openTag("qual:transition");
            out.addAttr("qual:id", "tr_"+s_node);
            
            out.openTag("qual:listOfInputs");               
            String edgeSign = null;
            for (RegulatoryMultiEdge me: graph.getIncomingEdges(v_no.get(i))) { 
            	RegulatoryEdgeSign sign = me.getSign(); 
                switch (sign) {
				case POSITIVE:
					edgeSign = "positive";
					break;
				case NEGATIVE:
					edgeSign = "negative";
					break;
				default:
					break;
				}                   
                              
                if(me.getEdgeCount() > 1) {
                	RegulatoryEdge myEdge;
                	for(int k = 0; k < me.getEdgeCount(); k++) {
                		myEdge = me.getEdge(k);
                		out.openTag("qual:input");
						out.addAttr("qual:id", "input_" + me.getSource().toString()+"_"+me.getMin(k));
						out.addAttr("qual:qualitativeSpecies", me.getSource().toString());
						out.addAttr("qual:tresholdLevel", ""+myEdge.getMin());
						out.addAttr("qual:transitionEffect","none");
						out.addAttr("qual:sign", ""+edgeSign);
						out.closeTag();
            	   } // for  
            	  
               } // if 
               else {
            	   out.openTag("qual:input");
            	   out.addAttr("qual:id", "input_" + me.getSource().toString() + "_"+me.getMin(0));
        		   out.addAttr("qual:qualitativeSpecies", me.getSource().toString());
        		   out.addAttr("qual:tresholdLevel", ""+me.getMin(0));
        		   out.addAttr("qual:transitionEffect","none");
        		   out.addAttr("qual:sign", ""+edgeSign);
        		   out.closeTag();
				}
            }
            out.closeTag();

            out.openTag("qual:listOfOutputs");
            out.openTag("qual:output");
            out.addAttr("qual:qualitativeSpecies", s_node);
            out.addAttr("qual:transitionEffect","assignmentLevel");
            out.closeTag();
            out.closeTag();

            out.openTag("qual:listOfFunctionTerms");
            out.openTag("qual:defaultTerm");
            
            boolean hasNoBasalValue = true;
            if (graph.getIncomingEdges(v_no.get(i)).size() == 0) {
                LogicalParameterList lpl = regulatoryNode.getV_logicalParameters();
                if (lpl.size() == 1) {
                	LogicalParameter lp = (LogicalParameter) lpl.get(0);
                	int value = lp.getValue();
                	if (lpl.isManual(lp)) {
       			    	out.addAttr("qual:resultLevel", ""+value);
                	    out.closeTag(); 
                	    hasNoBasalValue = false;
                	}
                }
            } 
            if (hasNoBasalValue) {
            out.addAttr("qual:resultLevel", ""+0);
            out.closeTag();
            for (curValue=1 ; curValue<=regulatoryNode.getMaxValue() ; curValue++) {
                out.openTag("qual:functionTerm");
                out.addAttr("qual:resultLevel", ""+curValue);
                out.openTag("math");
                out.addAttr("xmlns", "http://www.w3.org/1998/Math/MathML");
                
                out.openTag("apply");
                out.addTag("or");    // enforced for now, we should try to avoid it when not needed
                browser.browse(node); // will call leafReached()
                out.closeTag();  // apply

                out.closeTag(); // math
                out.closeTag(); // functionTerm
            }
            }
            out.closeTag(); // listOfFunctionTerms
            out.closeTag(); // transition
        }
            
        out.closeTag(); // list of transitions
        
		// Close the file
        out.closeTag(); // model
        out.closeTag(); // sbml
        os.flush();
        os.close();
    }
	
	public void leafReached(int value, int depth, int[][] path) {
		if (value != curValue) {
			return;
		}
		
        try {
			out.openTag("apply");
	        out.addTag("and");    // enforced for now, we should try to avoid it when not needed

	        // TODO: list of conditions
	        for (int i=0 ; i<depth ; i++) {
	        	int level = path[i][2];
	        	if (path[i][0] > 0) {
	        		writeConstraint("geq", level, path[i][0]);
	        	}
	        	if (path[i][1] < path[i][3]) {
	        		writeConstraint("lt", level, path[i][1]);
	        	}
	        }
	        
            out.closeTag();  // apply
	        
		} catch (IOException e) {
			e.printStackTrace();
		}

		// this is where we write the mathml corresponding to the selected path
	}
	
	private void writeConstraint(String cst, int idx, int value) throws IOException {
		out.openTag("apply");
		out.addTag(cst);
		
		out.openTag("ci");
		out.addContent(""+v_no.get(idx)); 
        out.closeTag();  // ci
		
        out.openTag("ci");
		out.addContent("input_"+v_no.get(idx)+"_"+value); 
        out.closeTag();  // ci

        out.closeTag();  // apply
	}
	
	/**
	 * Return the name of the file in the given path without any extensions
	 * 
	 * @param full_path the path of the file
	 * @return the name of the file in the given path without any extensions
	 */
	public String getPrevFilename(String full_path) {
	
		// Retrieve the name of the file
		File file = new File( full_path);
		String real_name = file.getName();
		
		// Check if it exists an extension an remove it if so
		int index_dot = real_name.lastIndexOf( '.');
		if( index_dot >=0){
			real_name = real_name.substring(0, index_dot);
		}
		
		return real_name;	
	    }
}
