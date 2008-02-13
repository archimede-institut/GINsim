package fr.univmrs.ibdm.GINsim.export.regulatoryGraph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.TableModel;

import fr.univmrs.ibdm.GINsim.export.GsAbstractExport;
import fr.univmrs.ibdm.GINsim.export.GsExportConfig;
import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsMutantListManager;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.initialState.GsInitStateTableModel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.initialState.GsInitialStateList;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.initialState.GsInitialStateManager;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeInteractionsModel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant.GsRegulatoryMutants;
import fr.univmrs.ibdm.GINsim.stableStates.GsSearchStableStates;
import fr.univmrs.ibdm.GINsim.stableStates.StableTableModel;
import fr.univmrs.ibdm.GINsim.xml.GsXMLWriter;

public class GsHTMLExport extends GsAbstractExport {

	OmddNode stable = null;
	GsSearchStableStates stableSearcher;
	GsExportConfig config = null;
	GsRegulatoryMutants mlist = null;
	FileWriter fout = null;
	GsXMLWriter out = null;
	TableModel model;
	List v_no;
	int len;
	boolean fullHTML = false;
	
	public GsHTMLExport() {
		id = "HTML";
		extension = ".html";
		filter = new String[] { "html" };
		filterDescr = "HTML files";
	}

	public GsPluggableActionDescriptor[] getT_action(int actionType,
			GsGraph graph) {
		if (graph instanceof GsRegulatoryGraph) {
			return new GsPluggableActionDescriptor[] { new GsPluggableActionDescriptor(
					"STR_html", "STR_html_descr", null, this, ACTION_EXPORT, 0) };
		}
		return null;
	}

	protected void doExport(GsExportConfig config) {
		this.config = config;
		stableSearcher = new GsSearchStableStates(config.getGraph(), null, null);
		try {
			long l = System.currentTimeMillis();
			run();
			System.out.println("html export: done in "+(System.currentTimeMillis()-l)+"ms");
		} catch (IOException e) {
			e.printStackTrace();
			GsEnv.error(new GsException(GsException.GRAVITY_ERROR, e), null);
		}
	}
	
	protected synchronized void run() throws IOException {
		GsRegulatoryGraph graph = (GsRegulatoryGraph) config.getGraph();
		v_no = graph.getNodeOrder();
		len = v_no.size();
		fout = new FileWriter(config.getFilename());
		out = new GsXMLWriter(fout, null);

		if (fullHTML) {
			out.write("<!DOCTYPE html PUBLIC \""
					+ "-//W3C//DTD XHTML 1.0 Strict//EN"
					+ "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\""
					+ ">\n");
			out.openTag("html");
			out.openTag("head");
			out.openTag("title");
			out.addContent("Model " + graph.getGraphName());
			out.closeTag();
			out.closeTag();
			out.openTag("body");
		}
		// a bit of included CSS
		out.openTag("style");
		out.addAttr("type", "text/css");
		out.addContent(".GINsimModel table {border-collapse: collapse }\n"
				+ ".GINsimModel table td, table th {border: solid ; border-width: 1px }\n"
				+ ".GINsimModel .stable { display:none }\n");
		out.closeTag();
		// add some javascript for the stable states
		out.openTag("script");
		out.addAttr("language", "javascript");
		out.addContent("function ShowHide(id) {\n"
				+ "  obj = document.getElementById(id);\n"
				+ "  if (obj.style.display == 'block') {\n"
				+ "    obj.style.display = 'none';"
    			+ "  } else {\n"
    			+ "    obj.style.display = 'block';"
				+ "  }\n"
				+ "}\n");
		out.closeTag();
		out.openTag("div");
		out.addAttr("class", "GINsimModel");
		out.addTagWithContent("h1", "Description of the model \"" + graph.getGraphName() + "\"");
		out.write(graph.getAnnotation().getHTMLComment());
		
		// all nodes with comment and logical functions
		out.addTagWithContent("h2", "Nodes");
		out.openTag("table");
		out.addContent("");
		out.write("<tr><th>ID</th><th>Value</th><th width='60%'>Logical function</th><th>Comment</th></tr>\n");
		for (Iterator it=graph.getNodeOrder().iterator() ; it.hasNext() ;) {
			GsRegulatoryVertex vertex = (GsRegulatoryVertex)it.next();
			GsTreeInteractionsModel lfunc = vertex.getInteractionsModel();
			int nbval = 0;
			Object funcRoot = null;
			if (lfunc != null) {
				funcRoot = lfunc.getRoot();
				nbval = lfunc.getChildCount(funcRoot);
				if (nbval == 0) {
					funcRoot = null;
				}
			}
			out.write("<tr><td "+(nbval>0?"rowspan='"+nbval+"'":"")+">"+vertex.getId()+"</td>");
			// the first logical function
			out.write("<td>");
			if (nbval > 0) {
				Object val = lfunc.getChild(funcRoot, 0);
				int nbfunc = lfunc.getChildCount(val);
				out.write(val.toString()+"</td><td>\n");
				for (int j=0 ; j<nbfunc ; j++) {
					Object func = lfunc.getChild(val, j);
					out.write("<br/>* "+func+"\n");
				}
				out.write("</td>");
			} else {
				out.write("</td><td>no function</td>");
			}
			
			// comment
			out.write("<td "+(nbval>0?"rowspan='"+nbval+"'":"")+">"
					+vertex.getAnnotation().getHTMLComment()+"</td>");
			out.write("</tr>");

			// add the other functions
			if (nbval > 1) {
				for (int i=1 ; i<nbval ; i++) {
					Object val = lfunc.getChild(funcRoot, i);
					int nbfunc = lfunc.getChildCount(val);
					out.write("<tr><td>"+val.toString()+"</td><td>\n");
					for (int j=0 ; j<nbfunc ; j++) {
						Object func = lfunc.getChild(val, j);
						out.write("<br/>* "+func+"\n");
					}
					out.write("</td></tr>\n");
				}
			} 
		}
		out.closeTag();
		
		// initial states
		out.addTagWithContent("h2", "Initial states");
		GsInitialStateList initStates = (GsInitialStateList) graph.getObject(
				GsInitialStateManager.key, false);
		if (initStates != null && initStates.getNbElements(null) > 0) {
			model = new GsInitStateTableModel(v_no, null, initStates, false);
			out.openTag("table");
			out.openTag("tr");
			out.addTagWithContent("th", "Name");
			for (int i = 0; i < len; i++) {
				out.addTagWithContent("th", v_no.get(i));
			}
			for ( int i=0 ; i< initStates.getNbElements(null) ; i++ ) {
				out.closeTag();
				out.openTag("tr");
				out.addTagWithContent("td", model.getValueAt(i, 0));
				for (int j = 0; j < len; j++) {
					out.addTagWithContent("td", model.getValueAt(i, j+2));
				}
			}
			out.closeTag();
			out.closeTag();
		}
		
		// Dynamic properties
		out.addTagWithContent("h2", "Dynamic properties");
		mlist = (GsRegulatoryMutants)graph.getObject(GsMutantListManager.key, true);
		out.openTag("table");
		out.addContent("");
		out.write("<tr><th>Mutant</th><th>Gene</th><th>Min</th>\n<th>Max</th><th>Comment</th><th>Stable States</th>\n</tr>\n");
		model = new StableTableModel(v_no);
		for (int i=-1 ; i<mlist.getNbElements(null) ; i++) {
			GsRegulatoryMutantDef mutant = 
				i<0 ? null : (GsRegulatoryMutantDef)mlist.getElement(null, i);
			
			stableSearcher.setMutant(mutant);
			stable = stableSearcher.getStable();
			((StableTableModel)model).setResult(stable);
			int nbrow;
			if (i<0) { // wild type
				nbrow = 1;
				out.write("<tr>\n<th"+(model.getRowCount() > 0?" rowspan='2'":"")+">Wild Type</th>\n");
				out.write("<td>-</td><td>-</td><td>-</td><td></td>");
			} else {
				nbrow = mutant.getNbChanges();
				if (nbrow < 1) {
					nbrow = 1;
				}
				out.write("<tr>\n<th rowspan="+(nbrow+(model.getRowCount() > 0?1:0))+">" + mutant.getName()+"</th>\n");
				if (mutant.getNbChanges() == 0) {
					out.write("<td>-</td><td>-</td><td>-</td>");
				} else {
					out.write("<td>"
							+ mutant.getName(0)
							+ "</td><td>"+ mutant.getMin(0)
							+ "</td>\n<td>" + mutant.getMax(0)+"</td>");
				}
				out.write("<td rowspan='"+nbrow+"'>" + mutant.getAnnotation().getHTMLComment()+"</td>");
			}
			
			// the common part: stable states
			String s_tableID = "t_stable"+(mutant == null ? "" : "_"+mutant.getName());
			if (model.getRowCount() > 0) {
				out.write("<td rowspan='"+nbrow+"'>"+model.getRowCount()+" Stable states (<a href='javascript:ShowHide(\"" +
						s_tableID + "\")'>View</a>)</td></tr>\n");
			} else {
				out.write("<td rowspan='"+nbrow+"'></td></tr>\n");
			}

			// more data on mutants:
			if (mutant != null) {
				for (int j=1 ; j<nbrow ; j++) {
					out.write("<tr><td>"
							+ mutant.getName(j)
							+ "</td><td>"+ mutant.getMin(j)
							+ "</td>\n<td>" + mutant.getMax(j)+"</td></tr>");
				}
			}
			
			// more data on stable states:
			if (model.getRowCount() > 0) {
				out.openTag("tr");
				out.openTag("td");
				out.addAttr("colspan", "5");
				out.openTag("div");
				out.addAttr("class", "stable");
				out.addAttr("id", s_tableID);

				/*
				out.openTag("table");
				out.openTag("tr");
				for (int k = 0; k < len; k++) {
					out.addTagWithContent("th", v_no.get(k));
				}
				out.closeTag();
				for (int k=0 ; k<model.getRowCount() ; k++) {
					out.openTag("tr");
					for (int j=0 ; j<len ; j++) {
						out.addTagWithContent("td", model.getValueAt(k,j));
					}
					out.closeTag();
				}
				out.closeTag();
				*/
				out.openTag("ul");
				for (int k=0 ; k<model.getRowCount() ; k++) {
					out.openTag("li");
					boolean needPrev=false;
					for (int j=0 ; j<len ; j++) {
						Object val = model.getValueAt(k,j);
						if (!val.toString().equals("0")) {
							String s = needPrev ? " ; " : "";
							needPrev = true;
							if (val.toString().equals("1")) {
								out.addContent(s+v_no.get(j));
							} else {
								out.addContent(s+v_no.get(j)+"="+val);
							}
						}
					}
					out.closeTag();
				}
				out.closeTag();

				out.closeTag();
				out.closeTag();
				out.closeTag();
			}
		}
		out.closeTag();
		
		// close the html file
		out.closeTag();
		if (fullHTML) {
			out.closeTag();
			out.closeTag();
		}
		fout.close();
	}
}