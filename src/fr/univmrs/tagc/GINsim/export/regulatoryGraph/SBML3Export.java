package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OMDDBrowserListener;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OMDDNodeBrowser;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.manageressources.Translator;
import fr.univmrs.tagc.common.xml.XMLWriter;

/**
 * Export a regulatory graph to the SBML format.
 * Each node is represented by a species and a (reversible) reaction.
 * Regulators of a given node are modifiers of this reaction.
 * Logical parameters (via OMDD) are translated in MathML
 */
public class SBML3Export implements OMDDBrowserListener {

	public static final String L3_QUALI_URL = "http://sbml.org/Community/Wiki/SBML_Level_3_Proposals/Qualitative_Models";
	
    List v_no;
    int len;
    OmddNode[] t_tree;
    OMDDNodeBrowser browser;

    int curValue;

    XMLWriter out;
    
	/**
	 * @param graph
	 * @param fileName
	 */
	public SBML3Export(GsGraph graph, String fileName) {
        v_no = graph.getNodeOrder();
        len = v_no.size();
        t_tree = ((GsRegulatoryGraph)graph).getAllTrees(true);
        browser = new OMDDNodeBrowser(this, t_tree.length);

        byte[][] t_markup = new byte[len][2];
        for (int i=0 ; i<len ; i++) {
            GsRegulatoryVertex vertex = (GsRegulatoryVertex)v_no.get(i);
            // proposed initial markup = 0
            t_markup[i][0] = 0;
            t_markup[i][1] = vertex.getMaxValue();
        }
        SBML3ExportConfigPanel cpanel = new SBML3ExportConfigPanel(graph.getNodeOrder(), t_markup);
        int ret = JOptionPane.showConfirmDialog(null, cpanel, "initial state", JOptionPane.OK_CANCEL_OPTION);
        if (ret != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            // FIXME: DTD for SBML ?
            OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
            out = new XMLWriter(os, null);
            String s_compartment = "c_"+graph.getGraphName();
            out.openTag("sbml");
            out.addAttr("xmlns", "http://www.sbml.org/sbml/level3/version1");
            out.addAttr("level", "3");
            out.addAttr("version", "1");
            out.addAttr("xmlns:qual", L3_QUALI_URL);
            out.addAttr("qual:required", "true");
            
            out.openTag("model");
            out.addAttr("id", "m_"+graph.getGraphName());
            out.openTag("listOfCompartments");
            out.openTag("compartment");
            out.addAttr("id", s_compartment);
            out.closeTag();
            out.closeTag();
            
            // List all components
            out.openTag("listOfQualitativeSpecies");
            out.addAttr("xmlns", L3_QUALI_URL);

            for (int i=0 ; i<t_tree.length ; i++) {
                GsRegulatoryVertex node = (GsRegulatoryVertex)v_no.get(i);
                String s_id = node.getId();
                String s_name = node.getName();
                out.openTag("qualitativeSpecies");
                out.addAttr("id", s_id);
                if (s_name != null) {
                	out.addAttr("name",s_name);
                }
                out.addAttr("compartment",s_compartment);
                out.addAttr("maxLevel",""+node.getMaxValue());
                out.addAttr("initialLevel",""+t_markup[i][0]);
                if (node.isInput()) {
                    out.addAttr("boundaryCondition", "true");
                }
                out.closeTag();
            }
            out.closeTag(); // list of species
            
            // Dynamical rules: a transition for each component
            out.openTag("listOfTransitions");
            out.addAttr("xmlns", L3_QUALI_URL);
            for (int i=0 ; i<t_tree.length ; i++) {
                GsRegulatoryVertex component = (GsRegulatoryVertex)v_no.get(i);
                if (component.isInput()) {
                	continue;
                }
                OmddNode node = t_tree[i];
                String s_node = component.getId();
                int max = component.getMaxValue();
                out.openTag("transition");
                out.addAttr("id", "tr_"+s_node);
                
                out.openTag("listOfInputs");

                Iterator it = graph.getGraphManager().getIncomingEdges(v_no.get(i)).iterator();
                while (it.hasNext()) {
                    GsDirectedEdge edge = (GsDirectedEdge)it.next();
                    out.openTag("input");
                    out.addAttr("qualitativeSpecies", edge.getSourceVertex().toString());
                    out.addAttr("transitionEffect","none");
                    // TODO: missing threshold
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
                out.addAttr("resultLevel", ""+0);
                out.closeTag();
                for (curValue=1 ; curValue<2 ; curValue++) {
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
                out.closeTag(); // listOfFunctionTerms
                out.closeTag(); // transition
            }
            out.closeTag(); // list of transitions
            
			// Close the file
            out.closeTag(); // model
            out.closeTag(); // sbml
            os.flush();
            os.close();
		} catch (IOException e) {
			GsEnv.error(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
		}
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
		out.openTag(cst);
		
		out.openTag("ci");
		out.addContent(""+v_no.get(idx));
        out.closeTag();  // ci
		
        out.openTag("ci");
		out.addContent(""+value);
        out.closeTag();  // ci

        out.closeTag();  // cst
        out.closeTag();  // apply
	}
}

class SBML3ExportConfigPanel extends JPanel {
    private static final long serialVersionUID = 9043565812912568136L;

    private SBML3markupModel model;
    
    protected SBML3ExportConfigPanel (List nodeOrder, byte[][] t_markup) {
        model = new SBML3markupModel(nodeOrder, t_markup);
        initialize();
    }
    
    private void initialize() {
        JScrollPane spane = new JScrollPane();
        spane.setViewportView(new JTable(model));
        this.add(spane);
        setSize(100, 250);
    }
}

class SBML3markupModel extends DefaultTableModel {
    private static final long serialVersionUID = -4867567086739357065L;

    private List nodeOrder;
    private byte[][] t_markup;
    
    protected SBML3markupModel (List nodeorder, byte[][] t_markup) {
        this.nodeOrder = nodeorder;
        this.t_markup = t_markup;
    }

    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        if (nodeOrder == null) {
            return 0;
        }
        return nodeOrder.size();
    }

    public Object getValueAt(int row, int column) {
        switch (column) {
            case 0:
                return nodeOrder.get(row);
            case 1:
                return ""+t_markup[row][0];
        }
        return null;
    }

    public boolean isCellEditable(int row, int column) {
        return column == 1;
    }
    
    public String getColumnName(int column) {
        switch (column) {
            case 0: return Translator.getString("STR_name");
            case 1: return Translator.getString("STR_value");
        }
        return super.getColumnName(column);
    }

    public void setValueAt(Object aValue, int row, int column) {
        if (column != 1) {
            return;
        }
        int val = Integer.parseInt((String)aValue);
        int d = val - t_markup[row][0];
        if (val < 0 || d > t_markup[row][1]) {
            return;
        }
        t_markup[row][0] += d;
        t_markup[row][1] -= d;
        fireTableCellUpdated(row, column);
    }
}
