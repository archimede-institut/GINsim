package fr.univmrs.ibdm.GINsim.export.regulatoryGraph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
 * Export a regulatory graph to petri net (INA format).
 * The core of the translation is in GsPetriNetExport.
 *
 * <p>used petri net tools:
 * <ul>
 *  <li>INA: http://www.informatik.hu-berlin.de/~Estarke/ina.html</li>
 *  <li>PED: http://www-dssz.informatik.tu-cottbus.de/~wwwdssz</li>
 * </ul>
 *
 *<p>the INA file is composed of 3 parts:
 * <ul>
 *  <li>the first part (header: P   M   PRE,POST  NETZ 0:title) is the most important one.
 *      each row represents a place and give it's number, it's initial markup and
 *      the list of incoming  and outgoing transitions</li>
 *  <li>the second part (header: place nr.  name capacity time) gives more data on places</li>
 *  <li>the third part (header: trans nr.   name priority time) gives more data on transitions</li>
 * </ul>
 * <br>example INA file:
 * <pre>
 * P   M   PRE,POST  NETZ 0:test
 * 1 0  1
 * 2 1  1:2
 * 3 0  , 1
 * &#64;
 * place nr.             name capacity time
 *      1: pl1                 65535    0
 *      2: pl2                 65535    0
 *      3: source              65535    0
 * &#64;
 * trans nr.             name priority time
 *      1: t1                      0    0
 * &#64;
 *</pre>
 */
public class GsPetriNetExportINA extends GsAbstractExport {

	protected GsPetriNetExportINA() {
		id = "INA";
		extension = ".pnt";
		filter = new String[] { "pnt" };
		filterDescr = "INA files (.pnt)";
	}

	public GsPluggableActionDescriptor[] getT_action(int actionType,
			GsGraph graph) {
		return null;
	}

	// FIXME: INA does not like PN with "useless" places. Such places should be removed (with a warning)
	// to prevent INA from believing the PN is not bounded! (maybe this should be an option ?)

	protected void doExport(GsExportConfig config) {
		GsGraph graph = config.getGraph();

		List v_no = graph.getNodeOrder();
        int len = v_no.size();
        OmddNode[] t_tree = ((GsRegulatoryGraph)graph).getAllTrees(true);
        List[] t_transition = new List[len];
        short[][] t_markup = GsPetriNetExport.prepareExport(config, t_transition, t_tree);

        try {
	        FileWriter out = new FileWriter(config.getFilename());

	        List[][] t_prepost = new ArrayList[2*len][2];
	        List v_transition = new ArrayList();

            for (int i=0 ; i<t_prepost.length ; i++) {
                t_prepost[i][0] = new ArrayList();
                t_prepost[i][1] = new ArrayList();
            }

            for (int i=0 ; i<len ; i++) {
                String vertex = v_no.get(i).toString();
                if (t_transition[i] != null) {
                    int maxvalue = ((GsRegulatoryVertex)v_no.get(i)).getMaxValue();
                    List v_trst = t_transition[i];
                    for (int j=0 ; j<v_trst.size() ; j++) {
                        TransitionData td = (TransitionData)v_trst.get(j);
                        // transform the TransitionData into prepost matrix
                        int a = -1;
                        int b = -1;
                        int selfMinInf = td.minValue;
                        int selfMaxInf = -1;
                        int selfMinSup = -1;
                        int selfMaxSup = td.maxValue;

                        if (selfMinInf != 0 || selfMaxSup != maxvalue) { // autoregulated
                            if (td.value > 0 && td.minValue < td.value) {
                                v_transition.add("t_"+vertex+"_"+j+"+");
                                a = v_transition.size();
                                selfMaxInf = maxvalue - Math.min(td.value-1, selfMaxSup);
                            }
                            if (td.value < maxvalue && td.maxValue > td.value) {
                                v_transition.add("t_"+vertex+"_"+j+"-");
                                b = v_transition.size();
                                selfMinSup = Math.max(td.value+1, selfMinInf);
                                selfMaxSup = td.value - selfMaxSup;
                            }

                        } else { // not autoregulated
                            if (td.value > 0) {
                                v_transition.add("t_"+vertex+"_"+j+"+");
                                a = v_transition.size();
                                selfMaxInf = maxvalue - (td.value-1);
                            }
                            if (td.value < maxvalue) {
                                v_transition.add("t_"+vertex+"_"+j+"-");
                                b = v_transition.size();
                                selfMinSup = td.value+1;
                                selfMaxSup = 0;
                            }
                        }
                        if (td.t_cst != null) {
                            for (int c=0 ; c<td.t_cst.length ; c++) {
                                int index = td.t_cst[c][0];
                                if (index == -1) {
                                    break;
                                }
                                int min = td.t_cst[c][1];
                                int max = td.t_cst[c][2];
                                index *= 2;
                                if (min > 1) {
                                    if (a != -1) {
                                        t_prepost[index][0].add(a+":"+min);
                                        t_prepost[index][1].add(a+":"+min);
                                    }
                                    if (b != -1) {
                                        t_prepost[index][0].add(b+":"+min);
                                        t_prepost[index][1].add(b+":"+min);
                                    }
                                } else if (min == 1) {
                                    if (a != -1) {
                                        t_prepost[index][0].add(""+a);
                                        t_prepost[index][1].add(""+a);
                                    }
                                    if (b != -1) {
                                        t_prepost[index][0].add(""+b);
                                        t_prepost[index][1].add(""+b);
                                    }
                                }
                                if (max > 1) {
                                    if (a != -1) {
                                        t_prepost[index+1][0].add(a+":"+max);
                                        t_prepost[index+1][1].add(a+":"+max);
                                    }
                                    if (b != -1) {
                                        t_prepost[index+1][0].add(b+":"+max);
                                        t_prepost[index+1][1].add(b+":"+max);
                                    }
                                } else if (max == 1) {
                                    if (a != -1) {
                                        t_prepost[index+1][0].add(""+a);
                                        t_prepost[index+1][1].add(""+a);
                                    }
                                    if (b != -1) {
                                        t_prepost[index+1][0].add(""+b);
                                        t_prepost[index+1][1].add(""+b);
                                    }
                                }
                            }
                        }
                        // also add arcs to the main places!
                        if (a != -1) {
                            // add one marker to the "positive place"
                            if (selfMinInf > 1) {
                                t_prepost[2*i][1].add(a+":"+selfMinInf);
                                t_prepost[2*i][0].add(a+":"+(selfMinInf+1));
                            } else if (selfMinInf == 1) {
                                t_prepost[2*i][1].add(""+a);
                                t_prepost[2*i][0].add(a+":2");
                            } else {
                                t_prepost[2*i][0].add(a+"");
                            }
                            // remove one marker from the "negative place"
                            if (selfMaxInf > 2) {
                                t_prepost[2*i+1][0].add(a+":"+(selfMaxInf-1));
                                t_prepost[2*i+1][1].add(a+":"+selfMaxInf);
                            } else if (selfMaxInf == 2) {
                                t_prepost[2*i+1][0].add(""+a);
                                t_prepost[2*i+1][1].add(a+":"+selfMaxInf);
                            } else {
                                t_prepost[2*i+1][1].add(""+a);
                            }
                        }
                        if (b != -1) {
                            // remove one marker from the "positive place"
                            if (selfMinSup > 2) {
                                t_prepost[2*i][0].add(b+":"+(selfMinSup-1));
                                t_prepost[2*i][1].add(b+":"+selfMinSup);
                            } else if (selfMinSup == 2) {
                                t_prepost[2*i][0].add(b+"");
                                t_prepost[2*i][1].add(b+":2");
                            } else {
                                t_prepost[2*i][1].add(b+"");
                            }
                            // add one marker to the "negative place"
                            if (selfMaxSup > 1) {
                                t_prepost[2*i+1][1].add(b+":"+selfMaxSup);
                                t_prepost[2*i+1][0].add(b+":"+(selfMaxSup+1));
                            } else if (selfMaxSup == 1) {
                                t_prepost[2*i+1][1].add(""+b);
                                t_prepost[2*i+1][0].add(b+":2");
                            } else {
                                t_prepost[2*i+1][0].add(""+b);
                            }
                        }

                    }
                }
            }

            // write the pre-post matrix
	        out.write("P   M   PRE,POST  NETZ 0:"+graph.getGraphName() +"\n");
            for (int i=0 ; i<t_tree.length ; i++) {
                String s_pp = "";
                List v = t_prepost[2*i][0];
                for (int j=0 ; j<v.size() ; j++) {
                    s_pp += " "+v.get(j);
                }
                v = t_prepost[2*i][1];
                if (v.size() > 0) {
                    s_pp += " ,";
                    for (int j=0 ; j<v.size() ; j++) {
                        s_pp += " "+v.get(j);
                    }
                }
                out.write(2*i+1+" "+t_markup[i][0]+" \t "+s_pp+"\n");
                s_pp = "";
                v = t_prepost[2*i+1][0];
                for (int j=0 ; j<v.size() ; j++) {
                    s_pp += " "+v.get(j);
                }
                v = t_prepost[2*i+1][1];
                if (v.size() > 0) {
                    s_pp += " ,";
                    for (int j=0 ; j<v.size() ; j++) {
                        s_pp += " "+v.get(j);
                    }
                }
                out.write(2*i+2+" "+t_markup[i][1]+" \t "+s_pp+"\n");
            }

            // places data
            out.write("@\nplace nr.  name \t capacity time\n");
            for (int i=0 ; i<t_tree.length ; i++) {
                out.write(2*i+1+":  "+v_no.get(i)+" \t 0 0\n");
                out.write(2*i+2+": -"+v_no.get(i)+" \t 0 0\n");
            }

            // transitions data
            out.write("@\ntrans nr. \t name priority time\n");
            for (int i=0 ; i<v_transition.size() ; i++) {
                out.write(i+1+": "+v_transition.get(i)+" \t 0 0\n");
            }
            out.write("@\n");

			// Close the file
			out.close();
		} catch (IOException e) {
			GsEnv.error(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
		}
	}
}
