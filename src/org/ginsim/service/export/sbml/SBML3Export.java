package org.ginsim.service.export.sbml;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ginsim.graph.GraphManager;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.RegulatoryVertex;
import org.ginsim.gui.service.common.ExportAction;

import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsLogicalParameter;
import fr.univmrs.tagc.GINsim.regulatoryGraph.LogicalParameterList;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OMDDBrowserListener;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OMDDNodeBrowser;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialState;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStatePanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStateStore;
import fr.univmrs.tagc.common.Debugger;
import fr.univmrs.tagc.common.datastore.ObjectStore;
import fr.univmrs.tagc.common.gui.dialog.stackdialog.AbstractStackDialogHandler;
import fr.univmrs.tagc.common.gui.dialog.stackdialog.StackDialogHandler;
import fr.univmrs.tagc.common.xml.XMLWriter;

/**
 * Export a regulatory graph to the SBML format.
 * Each node is represented by a species and a (reversible) reaction.
 * Regulators of a given node are modifiers of this reaction.
 * Logical parameters (via OMDD) are translated in MathML
 */
public class SBML3Export extends ExportAction<RegulatoryGraph> implements OMDDBrowserListener {
	
	public static final String L3_QUALI_URL = "http://sbml.org/Community/Wiki/SBML_Level_3_Proposals/Qualitative_Models";
	private static final GsFileFilter ffilter = new GsFileFilter( new String[] { "sbml" }, "SBML files");

	private SBML3Config config = null;
    List<RegulatoryVertex> v_no;
    int len;
    OmddNode[] t_tree;
    OMDDNodeBrowser browser;
    int curValue;
    XMLWriter out;
    
	/**
	 * @param graph
	 * @param fileName
	 */
	public SBML3Export(RegulatoryGraph graph) {
		super( graph, "STR_SBML_L3", "STR_SBML_L3_descr");
	}

	@Override
	protected GsFileFilter getFileFilter() {
		return ffilter;
	}
	
	@Override
	public StackDialogHandler getConfigPanel( ) {
		config = new SBML3Config();
		return new SBML3ExportConfigPanel( graph, config);				
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
	        		System.out.println( "case GEQ");
	        		writeConstraint("geq", level, path[i][0]);
	        	}
	        	if (path[i][1] < path[i][3]) {
	        		System.out.println( "case LT");
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
		
        out.openTag("cn");
		out.addContent(""+value);
        out.closeTag();  // cn

        out.closeTag();  // apply
	}
	
	
	/*
	 * This is where the real export is done.
	 * This method will be called by GsAbstractExport once the export configuration panel has been properly filled.
	 */
	protected void doExport( String filename) throws IOException {

        v_no = graph.getNodeOrder();
        len = v_no.size();
        t_tree = ((RegulatoryGraph)graph).getAllTrees(true);
        browser = new OMDDNodeBrowser(this, t_tree.length);
        
        byte[][] t_markup = new byte[len][2];
		Iterator itinit = config.getInitialState().keySet().iterator();
		Map m_initstates = null;
		if (itinit.hasNext()) {
			m_initstates = ((GsInitialState) itinit.next()).getMap();
		}
		itinit = null;
		if (m_initstates == null) {
			m_initstates = new HashMap();
		}
        for (int i=0 ; i<len ; i++) {
            RegulatoryVertex vertex = (RegulatoryVertex)v_no.get(i);
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
        String s_compartment = "c_"+graph.getGraphName();
        out.openTag("sbml");
        out.addAttr("xmlns", "http://www.sbml.org/sbml/level3/version1/core");
        out.addAttr("level", "3");
        out.addAttr("version", "1");
        out.addAttr("xmlns:qual", L3_QUALI_URL);
        out.addAttr("qual:required", "true");
        
        out.openTag("model");
        out.addAttr("id", ""+ getPrevFilename( GraphManager.getInstance().getGraphPath( graph)));
        
        out.openTag("listOfCompartments");
        out.openTag("compartment");
        out.addAttr("id", s_compartment);
        out.closeTag();
        out.closeTag();
        
        // List all components
        out.openTag("listOfQualitativeSpecies");
        out.addAttr("xmlns", L3_QUALI_URL);

        for (int i=0 ; i<t_tree.length ; i++) {
            RegulatoryVertex node = (RegulatoryVertex)v_no.get(i);
            String s_id = node.getId();
            String s_name = node.getName();
            out.openTag("qualitativeSpecies");
            out.addAttr("id", s_id);
            if ((s_name != null) && (!s_name.equals("noName"))) {
            	out.addAttr("name",s_name);
            } 
            out.addAttr("compartment",s_compartment);
            out.addAttr("maxLevel",""+node.getMaxValue());
            out.addAttr("initialLevel",""+t_markup[i][0]);
            if (node.isInput()) {
                out.addAttr("boundaryCondition", "true");
                out.addAttr("constant", "true");
            }
            else {
            	out.addAttr("boundaryCondition", "false");
                out.addAttr("constant", "false");
            }
            out.closeTag();
        }
        out.closeTag(); // list of species
        
        // Dynamical rules: a transition for each component
        out.openTag("listOfTransitions");
        out.addAttr("xmlns", L3_QUALI_URL);
        for (int i=0 ; i<t_tree.length ; i++) {
            RegulatoryVertex regulatoryVertex = (RegulatoryVertex)v_no.get(i);
            if (regulatoryVertex.isInput()) {
            	continue;
            }
            OmddNode node = t_tree[i];
            String s_node = regulatoryVertex.getId();
            out.openTag("transition");
            out.addAttr("id", "tr_"+s_node);
            
            out.openTag("listOfInputs");               
            String edgeSign = null;
            for (RegulatoryMultiEdge me: graph.getIncomingEdges(v_no.get(i))) {
                int sign = me.getSign(); 
                switch (sign) {
				case 0:
					edgeSign = "SBO:0000459";
					break;
				case 1:
					edgeSign = "SBO:0000020";
					break;
				default:
					break;
				}                   
                out.openTag("input");
                out.addAttr("qualitativeSpecies", me.getSource().toString());
                out.addAttr("transitionEffect","none");
                out.addAttr("sign", ""+edgeSign);
                out.closeTag();
            }
            out.closeTag();

            out.openTag("listOfOutputs");
            out.openTag("output");
            out.addAttr("qualitativeSpecies", s_node);
            out.addAttr("transitionEffect","assignmentLevel");
            out.closeTag();
            out.closeTag();

            out.openTag("listOfFunctionTerms");
            out.openTag("defaultTerm");
            
            boolean hasNoBasalValue = true;
            if (graph.getIncomingEdges(v_no.get(i)).size() == 0) {
                LogicalParameterList lpl = regulatoryVertex.getV_logicalParameters();
                if (lpl.size() == 1) {
                	GsLogicalParameter lp = (GsLogicalParameter) lpl.get(0);
                	int value = lp.getValue();
                	if (lpl.isManual(lp)) {
       			    	out.addAttr("resultLevel", ""+value);
                	    out.closeTag(); 
                	    hasNoBasalValue = false;
                	}
                }
            } 
            if (hasNoBasalValue) {
            out.addAttr("resultLevel", ""+0);
            out.closeTag();
            for (curValue=1 ; curValue<=regulatoryVertex.getMaxValue() ; curValue++) {
                out.openTag("functionTerm");
                out.addAttr("resultLevel", ""+curValue);
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
	
	/**
	 * gets filename without extension
	 **/
	// gets filename without extension
		public String getPrevFilename(String fullPath) {  
			char pathSeparator = '/';
			char extensionSeparator = '.';
		    int dot = fullPath.lastIndexOf(extensionSeparator);
			int sep = fullPath.lastIndexOf(pathSeparator);
			String realName= fullPath.substring(sep + 1, dot);
			return realName;	
		    }
}


class SBML3ExportConfigPanel extends AbstractStackDialogHandler {
	private static final long serialVersionUID = 9043565812912568136L;
	
	private final SBML3Config config;
	private final RegulatoryGraph graph;
	
	protected SBML3ExportConfigPanel( RegulatoryGraph graph, SBML3Config config) {
		super();
		setLayout(new GridBagLayout());
		this.config = config;
		this.graph = graph;
	}
	
	@Override
	protected void init() {
		
		GsInitialStatePanel initPanel = new GsInitialStatePanel( stack, graph, false);
		initPanel.setParam(config);
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(initPanel, c);
	}

	@Override
	public void run() {
		// TODO run export
		Debugger.log("Run export");
	}	
}

 
class SBML3Config implements GsInitialStateStore {
	Map m_init = new HashMap();
	Map m_input = new HashMap();
	ObjectStore store = new ObjectStore(2);

	public Map getInitialState() {
		return m_init;
	}

	public Map getInputState() {
		return m_input;
	}	
}
