package org.ginsim.service.tool.interactionanalysis;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ginsim.common.application.Translator;
import org.ginsim.common.document.DocumentStyle;
import org.ginsim.common.document.DocumentWriter;
import org.ginsim.common.utils.IOUtils;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdgeSign;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;



/**
 * A container class capable of exporting itself given a DocumentWriter
 * 
 * Use the following structure :
 * Map{
 *   target :=> List[
 *                SourceItem(source, level, reportItem(List[pathItem, pathItem, ....]), reportItem(List[pathItem, pathItem, ....]), ....),
 *                SourceItem(source, level, reportItem(List[pathItem, pathItem, ....]), reportItem(List[pathItem, pathItem, ....]), ....),
 *                SourceItem(source, level, reportItem(List[pathItem, pathItem, ....]), reportItem(List[pathItem, pathItem, ....]), ....)
 *              ],
 *   target :=> List[
 *                SourceItem(source, level, reportItem(List[pathItem, pathItem, ....]), reportItem(List[pathItem, pathItem, ....]), ....),
 *                SourceItem(source, level, reportItem(List[pathItem, pathItem, ....]), reportItem(List[pathItem, pathItem, ....]), ....),
 *                SourceItem(source, level, reportItem(List[pathItem, pathItem, ....]), reportItem(List[pathItem, pathItem, ....]), ....)
 *              ]
 * }
 * 
 * 
 *
 */
public class InteractionAnalysisReport {
	private static final String STYLE_POSITIVE = "positive"; 
	private static final String STYLE_NEGATIVE = "negative"; 
	private static final String STYLE_NONFUNCTIONAL = "nonFunctional"; 
	private static final String STYLE_DUAL = "dual"; 	

	/**
	 * How much time the algo did run
	 */
	protected long timeSpent;
	private Map<RegulatoryNode, List<SourceItem>> report;
	protected InteractionAnalysisReport() {
		report = new HashMap<RegulatoryNode, List<SourceItem>>();
	}
	
	/**
	 * Get the report for target from the map or creates i if it doesn't exists.
	 * Create a new sourceItem containing the source
	 * Add the sourceItem to its list and eventually create the list.
	 * 
	 * @param target the target regulator
	 * @param source the source regulator
	 * @return the newly created source item.
	 */
	protected SourceItem reportFor(RegulatoryNode target, RegulatoryNode source) {
		List<SourceItem> l = report.get(target);
		if (l == null) {
			l = new LinkedList<SourceItem>();
			report.put(target, l);
		}
		SourceItem si = new SourceItem();
		si.source = source;
		l.add(si);
		return si;
	}
	
	/**
	 * Write the report using the specified DocumentWriter.
	 * This report use some javascript.
	 * 
	 * @param dw the DocumentWriter to write the report
	 * @param regGraph the graph the algo ran on
	 * @throws IOException
	 */
	public void saveReport(DocumentWriter dw, RegulatoryGraph regGraph) throws IOException {			
		DocumentStyle style = new DocumentStyle();
		style.addStyle(STYLE_POSITIVE);
		style.addProperty(DocumentStyle.COLOR, new Color(67, 200, 75));
		style.addStyle(STYLE_NEGATIVE);
		style.addProperty(DocumentStyle.COLOR, new Color(246, 57, 53));
		style.addStyle(STYLE_NONFUNCTIONAL);
		style.addProperty(DocumentStyle.COLOR, new Color(0, 0, 0));
		style.addStyle(STYLE_DUAL);
		style.addProperty(DocumentStyle.COLOR, new Color(16, 0, 255));
		dw.setStyles(style);
		
		StringBuffer css = dw.getDocumentExtra("css");
		if (css != null) {
			css.append("  h2,h3,h4 {display:none;}\n" +
					"  th, td, tr {border: 1px solid black;}\n" +
					"  table {width: auto; margin: 2px;}\n" +
					"  .summary>tbody>tr>th {background-color: blue; color: white}\n" +
					"  .summary td {background-color: lightblue}\n" +
					"  .summary td table, .summary td table td {background-color: lightgreen}\n" +
					"  .summary table th {background-color: green; color: white}\n" +
					"  th span {font-size: 60%;}"
				);
		}
		
		
		StringBuffer javascript = dw.getDocumentExtra("javascript");
		if (javascript != null) {
			javascript.append(IOUtils.readFromFile("src/org/ginsim/service/tool/interactionanalysis/interactionAnalysis.js"));
		}
		
		dw.startDocument();
		dw.openHeader(1, Translator.getString("STR_interactionAnalysis"), null);
		dw.openParagraph(null);
		dw.writeTextln("Analizing interactions of "+regGraph.getGraphName()+" ("+regGraph.getNodeCount()+" vertices)");
		dw.closeParagraph();		
		
		writeSummary(dw, regGraph);
				
		dw.openHeader(2, "Report", null);
		for (RegulatoryNode target : report.keySet()) {
			dw.openHeader(3, target.getId(), null);

			for (SourceItem sourceItem : report.get(target)) {
				
				if (sourceItem.sign == InteractionAnalysisAlgo.FUNC_NON) {
					dw.openHeader(4,sourceItem.source.getId()+" -> "+target.getId()+" is non functional.", STYLE_NONFUNCTIONAL);
				} else if (sourceItem.sign == InteractionAnalysisAlgo.FUNC_POSITIVE) {
					dw.openHeader(4,sourceItem.source.getId()+" -> "+target.getId()+" is positive.", STYLE_POSITIVE);
				} else if (sourceItem.sign == InteractionAnalysisAlgo.FUNC_NEGATIVE) {
					dw.openHeader(4,sourceItem.source.getId()+" -> "+target.getId()+" is negative.", STYLE_NEGATIVE);
				} else {
					dw.openHeader(4,sourceItem.source.getId()+" -> "+target.getId()+" is dual.", STYLE_DUAL);
				}
				
				dw.openTable(null, null, null);
				dw.openTableRow();
				if (dw.doesDocumentSupportExtra("javascript")) {
					dw.openTableCell("Id", true);
				}
				dw.openTableCell("Result", true);
				dw.openTableCell("Source level", true);
				dw.openTableCell("Target level", true);
				ReportItem r0 = sourceItem.reportItems.get(0);
				for (Iterator<PathItem> it_path = r0.path.iterator(); it_path.hasNext();) {
					PathItem pathItem = it_path.next();
					dw.openTableCell(pathItem.vertex.getId(), true);
				}
				dw.closeTableRow();			
				
				
				int i = 0;
				for (ReportItem reportItem : sourceItem.reportItems) {
					if (dw.doesDocumentSupportExtra("javascript")) {
						dw.openTableCell(""+i++);
					}
					if (reportItem.sign == InteractionAnalysisAlgo.FUNC_NON) {
						dw.openTableCell(1, 1, "=", STYLE_NONFUNCTIONAL, false);
					} else if (reportItem.sign == InteractionAnalysisAlgo.FUNC_POSITIVE) {
						dw.openTableCell(1, 1, "+", STYLE_POSITIVE, false);
					} else if (reportItem.sign == InteractionAnalysisAlgo.FUNC_NEGATIVE) {
						dw.openTableCell(1, 1, "-", STYLE_NEGATIVE, false);
					}
					dw.openTableCell(reportItem.sourceValue_low+"/"+(reportItem.sourceValue_low+1));
					dw.openTableCell(reportItem.targetValue_low+"/"+reportItem.targetValue_high);
					for (PathItem pathItem : reportItem.path) {
						if (pathItem.targetValue_high == -1) {
							dw.openTableCell(""+pathItem.targetValue_low);
						} else {
							dw.openTableCell(pathItem.targetValue_low+"/"+pathItem.targetValue_high+" ");
						}
					}
					dw.closeTableRow();
				}
				dw.closeTable();
			}
			
		}
		dw.close();
	}
	private void writeSummary(DocumentWriter dw, RegulatoryGraph regGraph) throws IOException {
		dw.openHeader(2, "Summary", null);
		
		dw.openTable("Summary", null, null);
		dw.openTableRow();
		dw.openTableCell("Source", true);
		dw.openTableCell("Target", true);
		dw.openTableCell("User's sign", true);
		dw.openTableCell("Computed sign", true);
		if (dw.doesDocumentSupportExtra("javascript")) {
			dw.openTableCell("View", true);
		}
		dw.closeTableRow();
		
		
		for (RegulatoryNode target : report.keySet()) {
			for (SourceItem sourceItem : report.get(target)) {
								
				RegulatoryMultiEdge e = regGraph.getEdge(sourceItem.source, target);
				dw.openTableRow();
				dw.openTableCell(sourceItem.source.getId());
				dw.openTableCell(target.getId());
				switch (e.getSign()) {
				case UNKNOWN:
					dw.openTableCell(1, 1, RegulatoryEdgeSign.UNKNOWN.getLongDesc(), STYLE_NONFUNCTIONAL, false);
					break;
				case POSITIVE:
					dw.openTableCell(1, 1, RegulatoryEdgeSign.POSITIVE.getLongDesc(), STYLE_POSITIVE, false);
					break;
				case NEGATIVE:
					dw.openTableCell(1, 1, RegulatoryEdgeSign.NEGATIVE.getLongDesc(), STYLE_NEGATIVE, false);
					break;
					
				}
				if (e.getSign() == RegulatoryEdgeSign.UNKNOWN) {
					dw.openTableCell(1, 1, "unknown", STYLE_NONFUNCTIONAL, false);
				} else if (e.getSign() == RegulatoryEdgeSign.POSITIVE) {
					dw.openTableCell(1, 1, "positive", STYLE_POSITIVE, false);
				} else if (e.getSign() == RegulatoryEdgeSign.NEGATIVE) {
					dw.openTableCell(1, 1, "negative", STYLE_NEGATIVE, false);
				} else {
					dw.openTableCell(1, 1, "dual", STYLE_DUAL, false);
				}
				if (sourceItem.sign == InteractionAnalysisAlgo.FUNC_NON) {
					dw.openTableCell(1, 1, "non functional", STYLE_NONFUNCTIONAL, false);
				} else if (sourceItem.sign == InteractionAnalysisAlgo.FUNC_POSITIVE) {
					dw.openTableCell(1, 1, "positive", STYLE_POSITIVE, false);
				} else if (sourceItem.sign == InteractionAnalysisAlgo.FUNC_NEGATIVE) {
					dw.openTableCell(1, 1, "negative", STYLE_NEGATIVE, false);
				} else {
					dw.openTableCell(1, 1, "dual", STYLE_DUAL, false);
				}
				if (dw.doesDocumentSupportExtra("javascript")) {
					dw.openTableCell(null);
					dw.addLink("#"+sourceItem.source.getId()+"__"+target.getId(), "view");
				}
				dw.closeTableRow();
			}
		}
		
		dw.closeTable();		
	}

	
}

class SourceItem {
	List<ReportItem> reportItems = new LinkedList<ReportItem>();
	RegulatoryNode source;
	byte sign;
}

class PathItem {
	byte targetValue_low, targetValue_high = -1;
	RegulatoryNode vertex;
}

/**
 * targetValue_low : value of the target for a low source
 * targetValue_high : value of the target for a high source
 * sourceValue_low : value of the low source, the high value is the low+1
 * sign : represent the sign of the interaction (-1, 0, +1)
 */
class ReportItem {
	byte targetValue_low, targetValue_high, sourceValue_low, sign;
	List<PathItem> path;
}