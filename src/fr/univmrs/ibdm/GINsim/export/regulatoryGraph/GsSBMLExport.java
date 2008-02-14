package fr.univmrs.ibdm.GINsim.export.regulatoryGraph;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;
import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.global.GsException;
import fr.univmrs.tagc.manageressources.Translator;
import fr.univmrs.tagc.xml.XMLWriter;

/**
 * Export a regulatory graph to the SBML format.
 * Each node is represented by a species and a (reversible) reaction.
 * Regulators of a given node are modifiers of this reaction.
 * Logical parameters (via their OMDD representation) are translated into a (dirty?)
 * mathml formula using "&lt;piecewise&gt;" tags.
 */
public class GsSBMLExport {
	/**
	 * @param graph
	 * @param fileName
	 */
	public static void export(GsGraph graph, String fileName) {
        List v_no = graph.getNodeOrder();
        int len = v_no.size();
        OmddNode[] t_tree = ((GsRegulatoryGraph)graph).getAllTrees(true);
        short[][] t_markup = new short[len][2];
        for (int i=0 ; i<len ; i++) {
            GsRegulatoryVertex vertex = (GsRegulatoryVertex)v_no.get(i);
            if (graph.getGraphManager().getIncomingEdges(vertex).size() == 0) {
                // input node: no transition, use basal value as initial markup
            	
            	// TODO: put back max value ?
            	
//                t_markup[i][0] = vertex.getBaseValue();
//                t_markup[i][1] = (short)(vertex.getMaxValue() - vertex.getBaseValue());
            } else {
                // normal node, initial markup = 0
                t_markup[i][0] = 0;
                t_markup[i][1] = vertex.getMaxValue();
            }
        }
        SBMLExportConfigPanel cpanel = new SBMLExportConfigPanel(graph.getNodeOrder(), t_markup);
        int ret = JOptionPane.showConfirmDialog(null, cpanel, "initial state", JOptionPane.OK_CANCEL_OPTION);
        if (ret != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            // FIXME: dtd for SBML ?
            FileOutputStream os = new FileOutputStream(fileName);
            XMLWriter out = new XMLWriter(os, null);
            String s_compartment = "c_"+graph.getGraphName();
            out.openTag("sbml");
            out.addAttr("xmlns", "http://www.sbml.org/sbml/level2");
            out.addAttr("level", "2");
            out.addAttr("version", "1");
            
            out.openTag("model");
            out.addAttr("id", "m_"+graph.getGraphName());
            out.openTag("listOfCompartments");
            out.openTag("compartment");
            out.addAttr("id", s_compartment);
            out.closeTag();
            out.closeTag();
            
            // places data
            out.openTag("listOfSpecies");
            for (int i=0 ; i<t_tree.length ; i++) {
                String s_id = v_no.get(i).toString();
                String s_name = ((GsRegulatoryVertex)v_no.get(i)).getName();
                out.openTag("species");
                out.addAttr("id", "s_"+s_id);
                out.addAttr("name",s_name);
                out.addAttr("compartment",s_compartment);
                out.addAttr("initialConcentration",""+t_markup[i][0]);
                out.closeTag();
            }
            out.closeTag(); // list of species
            
            // transitions data
            out.openTag("listOfReactions");
            for (int i=0 ; i<t_tree.length ; i++) {
                OmddNode node = t_tree[i];
                String s_node = v_no.get(i).toString();
                int max = ((GsRegulatoryVertex)v_no.get(i)).getMaxValue();
                out.openTag("reaction");
                out.addAttr("id", "r_"+s_node);
                out.addAttr("reversible", "true");
                out.openTag("listOfReactants");
                out.closeTag();
                out.openTag("listOfProducts");
                out.openTag("speciesReference");
                out.addAttr("species","s_"+s_node);
                out.addAttr("stoichiometry","1");
                out.closeTag();
                out.closeTag();
                out.openTag("listOfModifiers");
                Iterator it = graph.getGraphManager().getIncomingEdges(v_no.get(i)).iterator();
                while (it.hasNext()) {
                    GsDirectedEdge edge = (GsDirectedEdge)it.next();
                    out.openTag("modifierSpeciesReference");
                    out.addAttr("species","s_"+edge.getSourceVertex().toString());
                    out.closeTag();
                }
                out.closeTag(); // list of modifiers
                out.openTag("kineticLaw");
                out.openTag("math");
                out.addAttr("xmlns", "http://www.w3.org/1998/Math/MathML");
                writeNode(out, node, v_no, s_node, max, i, -1);
                out.closeTag(); //math
                out.closeTag(); // kineticLaw
                out.closeTag(); // reaction
                
            }
            out.closeTag(); // list of reaction
            
			// Close the file
            out.closeTag(); // model
            out.closeTag(); // sbml
            os.close();
		} catch (IOException e) {
			GsEnv.error(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
		}
	}

    private static void writeNode(XMLWriter out, OmddNode node, List v_no, String s_node, int max, int index, int val) throws IOException {
        if (node.next == null) {
            if (val != -1) { // self-regulation
                if (val > node.value) {
                    out.openTag("apply");
                    out.openTag("minus");
                    out.closeTag();
                    out.openTag("cn");
                    out.addContent("1");
                    out.closeTag();
                    out.closeTag(); //apply
                } else if (val > node.value) {
                    out.openTag("cn");
                    out.addContent("1");
                    out.closeTag();
                } else{
                    out.openTag("cn");
                    out.addContent("0");
                    out.closeTag();
                }
            } else { // val == -1: no self-regulation here
                out.openTag("piecewise");
                if (node.value > 0) {
                    out.openTag("piece");
                    out.openTag("cn");
                    out.addContent("1");
                    out.closeTag();
                    out.openTag("apply");
                    out.openTag("lt");
                    out.closeTag();
                    out.openTag("ci");
                    out.addContent("s_"+s_node);
                    out.closeTag();
                    out.openTag("cn");
                    out.addContent(""+node.value);
                    out.closeTag();
                    out.closeTag(); // apply
                    out.closeTag(); // piece
                }
                if (node.value < max) {
                    out.openTag("piece");
                    out.openTag("apply");
                    out.openTag("minus");
                    out.closeTag();
                    out.openTag("cn");
                    out.addContent("1");
                    out.closeTag();
                    out.closeTag(); //apply
                    out.openTag("apply");
                    out.openTag("gt");
                    out.closeTag();
                    out.openTag("ci");
                    out.addContent("s_"+s_node);
                    out.closeTag();
                    out.openTag("cn");
                    out.addContent(""+node.value);
                    out.closeTag();
                    out.closeTag(); // apply
                    out.closeTag(); // piece
                }
                out.openTag("otherwise");
                out.openTag("cn");
                out.addContent("0");
                out.closeTag();
                out.closeTag();
                out.closeTag(); //piecewise
            }
        } else { // node.next != null: this is a condition on the way
            out.openTag("piecewise");
            for (int i=0 ; i<node.next.length ; i++) {
                out.openTag("piece");
                if (node.level == index) {
                    writeNode(out, node.next[i], v_no, s_node, max, index, i);
                } else {
                    writeNode(out, node.next[i], v_no, s_node, max, index, val);
                }
                out.openTag("apply");
                out.openTag("eq");
                out.closeTag();
                out.openTag("ci");
                out.addContent("s_"+v_no.get(node.level).toString());
                out.closeTag();
                out.openTag("cn");
                out.addContent(""+i);
                out.closeTag();
                out.closeTag(); // apply
                out.closeTag(); // piece
            }
            out.closeTag(); //piecewise
        }
    }
}

class SBMLExportConfigPanel extends JPanel {
    private static final long serialVersionUID = 9043565812912568136L;

    private SBMLmarkupModel model;
    
    protected SBMLExportConfigPanel (List nodeOrder, short[][] t_markup) {
        model = new SBMLmarkupModel(nodeOrder, t_markup);
        initialize();
    }
    
    private void initialize() {
        JScrollPane spane = new JScrollPane();
        spane.setViewportView(new JTable(model));
        this.add(spane);
        setSize(100, 250);
    }
}

class SBMLmarkupModel extends DefaultTableModel {
    private static final long serialVersionUID = -4867567086739357065L;

    private List nodeOrder;
    private short[][] t_markup;
    
    protected SBMLmarkupModel (List nodeorder, short[][] t_markup) {
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
