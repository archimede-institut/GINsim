package fr.univmrs.ibdm.GINsim.export.regulatoryGraph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.export.GsAbstractExport;
import fr.univmrs.ibdm.GINsim.export.GsExportConfig;
import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;

/**
 * Export a regulatory graph to petri net (PNML format).
 * The core of the translation is in <code>GsPetriNetExport</code>.
 *
 * <p>petri net tools/format:
 * <ul>
 *  <li>PNML: http://www.informatik.hu-berlin.de/top/pnml/about.html</li>
 *  <li>PIPE2: http://pipe2.sourceforge.net/</li>
 * </ul>
 */
public class GsPetriNetExportPNML extends GsAbstractExport {

	protected GsPetriNetExportPNML() {
		id = "PNML";
		extension = "xml";
		filter = new String[] { "xml" };
		filterDescr = "INA files (.xml)";
	}

	public GsPluggableActionDescriptor[] getT_action(int actionType,
			GsGraph graph) {
		return null;
	}
	
	protected void doExport(GsExportConfig config) {
		GsGraph graph = config.getGraph();
        Vector v_no = graph.getNodeOrder();
        int len = v_no.size();
        OmddNode[] t_tree = ((GsRegulatoryGraph)graph).getAllTrees(true);
        Vector[] t_transition = new Vector[len];
        short[][] t_markup = GsPetriNetExport.prepareExport(config, t_transition, t_tree);

        try {
	        FileWriter out = new FileWriter(config.getFilename());
            
            out.write("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n"+
                    "<pnml>\n"+
                    "<net id=\""+graph.getGraphName()+"\" type=\"P/T net\">\n");
            
            // places data
            for (int i=0 ; i<t_tree.length ; i++) {
                out.write("  <place id=\""+v_no.get(i)+"\">\n"+
                          "     <graphics><position x=\""+(50)+"\" y=\""+(10+80*i)+"\"/></graphics>\n"+
                          "     <name><value>"+v_no.get(i)+"</value></name>\n"+
                          "     <initialMarking><value>"+t_markup[i][0]+"</value></initialMarking>\n"+
                          "  </place>\n");
                out.write("  <place id=\"-"+v_no.get(i)+"\">\n"+
                          "     <graphics><position x=\""+(100)+"\" y=\""+(10+80*i)+"\"/></graphics>\n"+
                          "     <name><value>-"+v_no.get(i)+"</value></name>\n"+
                          "     <initialMarking><value>"+t_markup[i][1]+"</value></initialMarking>\n"+
                          "  </place>\n");
            }
            
            // transitions data
            for (int i=0 ; i<t_transition.length ; i++) {
                Vector v_transition = t_transition[i];
                String s_node = v_no.get(i).toString();
                int max = ((GsRegulatoryVertex)v_no.get(i)).getMaxValue();
                int c = 0;
                if (v_transition != null) {
                    for (int j=0 ; j<v_transition.size() ; j++) {
                        TransitionData td = (TransitionData)v_transition.get(j);
                        
                        if (td.value > 0 && td.minValue < td.value) {
                            out.write("  <transition id=\"t_"+s_node+"_"+j+"+\">"+
                                      "     <graphics><position x=\""+(200+80*c)+"\" y=\""+(10+80*i)+"\"/></graphics>\n"+
                                      "     <name><value>t_"+s_node+"_"+j+"+</value></name>\n"+
                                      "     <orientation><value>0</value></orientation>\n"+
                                      "     <rate><value>1.0</value></rate>\n"+
                                      "     <timed><value>false</value></timed>\n"+
                                      "  </transition>\n");
                            c++;
                        }
                        if (td.value < max && td.maxValue > td.value) {
                            out.write("  <transition id=\"t_"+s_node+"_"+j+"-\">"+
                                      "     <graphics><position x=\""+(200+80*c)+"\" y=\""+(10+80*i)+"\"/></graphics>\n"+
                                      "     <name><value>t_"+s_node+"_"+j+"-</value></name>\n"+
                                      "     <orientation><value>0</value></orientation>\n"+
                                      "     <rate><value>1.0</value></rate>\n"+
                                      "     <timed><value>false</value></timed>\n"+
                                      "  </transition>\n");
                            c++;
                        }
                    }
                }
            }
            
            // arcs
            for (int i=0 ; i<t_transition.length ; i++) {
                Vector v_transition = t_transition[i];
                String s_node = v_no.get(i).toString();
                int max = ((GsRegulatoryVertex)v_no.get(i)).getMaxValue();
                if (v_transition != null) {
                    for (int j=0 ; j<v_transition.size() ; j++) {
                        
                        TransitionData td = (TransitionData)v_transition.get(j);
                        if (td.value > 0 && td.minValue < td.value) {
                            String s_transition = "t_"+s_node+"_"+j+"+";
                            String s_src = v_no.get(td.nodeIndex).toString();
                            if (td.minValue == 0) {
                                out.write("  <arc id=\"a_"+s_transition+"_"+s_src+"\" source=\""+s_transition+"\" target=\""+s_src+"\">\n" +
                                        "     <inscription><value>1</value></inscription>\n" +
                                        "  </arc>\n");
                            } else {
                                out.write("  <arc id=\"a_"+s_src+"_"+s_transition+"\" source=\""+s_src+"\" target=\""+s_transition+"\">\n" +
                                        "     <inscription><value>"+td.minValue+"</value></inscription>\n" +
                                        "  </arc>\n");
                                out.write("  <arc id=\"a_"+s_transition+"_"+s_src+"\" source=\""+s_transition+"\" target=\""+s_src+"\">\n" +
                                        "     <inscription><value>"+(td.minValue+1)+"</value></inscription>\n" +
                                        "  </arc>\n");
                            }
                            int a = (td.value <= td.maxValue ?  max-td.value+1 : max-td.maxValue);
                            out.write("  <arc id=\"a_-"+s_src+"_"+s_transition+"\" source=\"-"+s_src+"\" target=\""+s_transition+"\">\n" +
                                    "     <inscription><value>"+a+"</value></inscription>\n" +
                                    "  </arc>\n");
                            if (a > 1) {
                                out.write("  <arc id=\"a_"+s_transition+"_-"+s_src+"\" source=\""+s_transition+"\" target=\"-"+s_src+"\">\n" +
                                        "     <inscription><value>"+(a-1)+"</value></inscription>\n" +
                                        "  </arc>\n");
                            }
                            if (td.t_cst != null) {
                                for (int ti=0 ; ti< td.t_cst.length ; ti++) {
                                    int index = td.t_cst[ti][0]; 
                                    if (index == -1) {
                                        break;
                                    }
                                    int lmin = td.t_cst[ti][1];
                                    int lmax = td.t_cst[ti][2];
                                    s_src = v_no.get(index).toString();
                                    if (lmin != 0) {
                                        out.write("  <arc id=\"a_"+s_src+"_"+s_transition+"\" source=\""+s_src+"\" target=\""+s_transition+"\">\n" +
                                                "     <inscription><value>"+lmin+"</value></inscription>\n" +
                                                "  </arc>\n");
                                        out.write("  <arc id=\"a_"+s_transition+"_"+s_src+"\" source=\""+s_transition+"\" target=\""+s_src+"\">\n" +
                                                "     <inscription><value>"+lmin+"</value></inscription>\n" +
                                                "  </arc>\n");
                                    }
                                    if (lmax != 0) {
                                        out.write("  <arc id=\"a_-"+s_src+"-_"+s_transition+"\" source=\"-"+s_src+"\" target=\""+s_transition+"\">\n" +
                                                "     <inscription><value>"+lmax+"</value></inscription>\n" +
                                                "  </arc>\n");
                                        out.write("  <arc id=\"a_"+s_transition+"_-"+s_src+"\" source=\""+s_transition+"\" target=\"-"+s_src+"\">\n" +
                                                "     <inscription><value>"+lmax+"</value></inscription>\n" +
                                                "  </arc>\n");
                                    }
                                }
                            }
                        }
                        if (td.value < max && td.maxValue > td.value) {
                            String s_transition = "t_"+s_node+"_"+j+"-";
                            String s_src = v_no.get(td.nodeIndex).toString();
                            if (td.maxValue == max) {
                                out.write("  <arc id=\"a_"+s_transition+"_-"+s_src+"\" source=\""+s_transition+"\" target=\"-"+s_src+"\">\n" +
                                        "     <inscription><value>"+1+"</value></inscription>\n" +
                                        "  </arc>\n");
                            } else {
                                out.write("  <arc id=\"a_-"+s_src+"_"+s_transition+"\" source=\"-"+s_src+"\" target=\""+s_transition+"\">\n" +
                                        "     <inscription><value>"+td.maxValue+"</value></inscription>\n" +
                                        "  </arc>\n");
                                out.write("  <arc id=\"a_"+s_transition+"_-"+s_src+"\" source=\""+s_transition+"\" target=\"-"+s_src+"\">\n" +
                                        "     <inscription><value>"+(td.maxValue+1)+"</value></inscription>\n" +
                                        "  </arc>\n");
                            }
                          int a = td.value >= td.minValue ?  td.value+1 : td.minValue;
                            out.write("  <arc id=\"a_"+s_src+"_"+s_transition+"\" source=\""+s_src+"\" target=\""+s_transition+"\">\n" +
                                    "     <inscription><value>"+a+"</value></inscription>\n" +
                                    "  </arc>\n");
                            if (a > 1) {
                                out.write("  <arc id=\"a_"+s_transition+"_"+s_src+"\" source=\""+s_transition+"\" target=\""+s_src+"\">\n" +
                                        "     <inscription><value>"+(a-1)+"</value></inscription>\n" +
                                        "  </arc>\n");
                            }
                            if (td.t_cst != null) {
                                for (int ti=0 ; ti< td.t_cst.length ; ti++) {
                                    int index = td.t_cst[ti][0]; 
                                    if (index == -1) {
                                        break;
                                    }
                                    int lmin = td.t_cst[ti][1];
                                    int lmax = td.t_cst[ti][2];
                                    s_src = v_no.get(index).toString();
                                    if (lmin != 0) {
                                        out.write("  <arc id=\"a_"+s_src+"_"+s_transition+"\" source=\""+s_src+"\" target=\""+s_transition+"\">\n" +
                                                "     <inscription><value>"+lmin+"</value></inscription>\n" +
                                                "  </arc>\n");
                                        out.write("  <arc id=\"a_"+s_transition+"_"+s_src+"\" source=\""+s_transition+"\" target=\""+s_src+"\">\n" +
                                                "     <inscription><value>"+lmin+"</value></inscription>\n" +
                                                "  </arc>\n");
                                    }
                                    if (lmax != 0) {
                                        out.write("  <arc id=\"a_-"+s_src+"_"+s_transition+"\" source=\"-"+s_src+"\" target=\""+s_transition+"\">\n" +
                                                "     <inscription><value>"+lmax+"</value></inscription>\n" +
                                                "  </arc>\n");
                                        out.write("  <arc id=\"a_"+s_transition+"_-"+s_src+"\" source=\""+s_transition+"\" target=\"-"+s_src+"\">\n" +
                                                "     <inscription><value>"+lmax+"</value></inscription>\n" +
                                                "  </arc>\n");
                                    }
                                }
                            }
                        }
                    }
                }
            }
			// Close the file
            out.write("</net>\n</pnml>\n");
			out.close();
		} catch (IOException e) {
			GsEnv.error(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
		}
	}
}
