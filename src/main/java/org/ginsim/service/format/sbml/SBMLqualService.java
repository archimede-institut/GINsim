package org.ginsim.service.format.sbml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.NodeInfo;
import org.colomoto.biolqm.io.sbml.SBMLFormat;
import org.colomoto.biolqm.io.sbml.SBMLQualBundle;
import org.colomoto.biolqm.io.sbml.SBMLqualExport;
import org.colomoto.biolqm.io.sbml.SBMLqualImport;
import org.ginsim.core.annotation.AnnotationLink;
import org.ginsim.core.graph.regulatorygraph.LogicalModel2RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedState;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.service.*;
import org.kohsuke.MetaInfServices;
import org.sbml.jsbml.Annotation;
import org.sbml.jsbml.CVTerm;
import org.sbml.jsbml.CVTerm.Qualifier;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBase;
import org.sbml.jsbml.ext.layout.*;
import org.sbml.jsbml.ext.qual.QualitativeSpecies;
import org.sbml.jsbml.util.StringTools;
import org.sbml.jsbml.xml.XMLNode;

@MetaInfServices( Service.class)
@Alias("SBML")
@ServiceStatus(EStatus.DEVELOPMENT)
public class SBMLqualService extends FormatSupportService<SBMLFormat> {

	public SBMLqualService() {
		super(new SBMLFormat());
	}

	/**
	 * Return the graph built from the SBML file at the given path
	 * 
	 * @param filename the path of the SBML file describing the graph
	 * @return the graph built from the SBML file at the given path
	 */
	public RegulatoryGraph importLRG( String filename) {

		try {
			SBMLqualImport simport = new SBMLqualImport();
			simport.setSource(filename);
			LogicalModel model = simport.call();
			RegulatoryGraph lrg = LogicalModel2RegulatoryGraph.importModel(model);
			lrg.setAnnotationModule(model.getAnnotationModule());
			
			SBMLQualBundle qbundle = simport.getQualBundle();
			
			// TODO: add unused interactions and consistency checks



			// Handle annotations
			importAnnotation(lrg, qbundle.document.getModel(), lrg.getAnnotation());
			for (QualitativeSpecies sp: qbundle.qmodel.getListOfQualitativeSpecies()) {
				RegulatoryNode node = lrg.getNodeByName(sp.getId());
				importAnnotation(lrg, sp, node.getAnnotation());
			}
			
			return lrg;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Convenience method to export without having to configure anything.
	 * 
	 * @param graph
	 * @param filename
	 * @throws IOException
	 */
    @Override
	public String export( RegulatoryGraph graph, String filename) throws IOException {
		return export(new SBMLQualConfig(graph), filename);
	}

	
	/**
	 * Perform the SBML export, using the JSBML-based encoder from LogicalModel.
	 * 
	 * @param config the configuration structure
	 * @param filename the path to the target file
	 * @throws IOException
	 */
	public String export( SBMLQualConfig config, String filename) throws IOException{
        RegulatoryGraph graph = config.getGraph();
		LogicalModel model = graph.getModel(null, true);
		try {
			SBMLqualExport sExport = new SBMLqualExport(model, true);
			SBMLQualBundle qbundle = sExport.getSBMLBundle();

			// set the initial state
            List<NodeInfo> nodes = model.getComponents();
            byte[] state = new byte[nodes.size()];
            NamedState initState = config.getSelectedInitialState();
            NamedState inputState = config.getSelectedInputState();
            for (int idx=0 ; idx<state.length ; idx++) {
                NodeInfo ni = nodes.get(idx);
                byte v = -1;
                if (ni.isInput()) {
                    if (inputState != null) {
                        v = inputState.getFirstValue(ni);
                    }
                } else if (initState != null) {
                    v = initState.getFirstValue(ni);
                }
                state[idx] = v;
            }
            sExport.setInitialCondition(state);

			// Add annotations
			Model smodel = qbundle.document.getModel();
			try {
				exportAnnotation(smodel, graph.getAnnotation());
			} catch (XMLStreamException e) {
				System.err.println("Error exporting model annotation");
			}
			for (RegulatoryNode node: graph.getNodeOrder()) {
				QualitativeSpecies qs = qbundle.qmodel.getQualitativeSpecies(node.getId());
				org.ginsim.core.annotation.Annotation annotation = node.getAnnotation();
				try {
					exportAnnotation(qs, annotation);
				} catch (XMLStreamException e) {
					System.err.println("Error exporting annotation for "+ node.getId());
				}
			}
			

			sExport.setDestination(filename);
			sExport.export();
			return null;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public void exportAnnotation(SBase elt, org.ginsim.core.annotation.Annotation gsnote) throws XMLStreamException {
		Annotation as = elt.getAnnotation();
		for (AnnotationLink link: gsnote) {
			CVTerm term = new CVTerm(Qualifier.BQB_UNKNOWN, link.getLink());
			as.addCVTerm(term);
		}
		String txt = gsnote.getComment().trim();
		if (txt != null && txt.length() > 0) {
			String[] lines = txt.split("\n");
			StringBuffer sb = new StringBuffer(txt.length() + 100);
			sb.append("<notes><body xmlns=\"http://www.w3.org/1999/xhtml\">\n<p>");
			boolean parStart = true;
			for (String line: lines) {
				line =  line.trim();
				if (line.length() > 1) {
					if (!parStart) {
						sb.append("\n");
					} else {
						parStart = false;
					}
					sb.append(StringTools.encodeForHTML(line));
				} else {
					if (!parStart) {
						sb.append("</p>\n<p>");
						parStart = true;
					}
				}
			}
			sb.append("</p>\n</body></notes>");
			elt.setNotes(sb.toString());
		}
	}
	
	public void importAnnotation(RegulatoryGraph lrg, SBase elt, org.ginsim.core.annotation.Annotation gsnote) throws XMLStreamException {
		Annotation as = elt.getAnnotation();
		for (CVTerm term: as.getListOfCVTerms()) {
			for (String s: term.getResources()) {
				gsnote.add(new AnnotationLink(s, lrg));
			}
		}

		XMLNode notes = elt.getNotes();
		if (notes != null && notes.getChildCount() > 0) {
			StringBuffer sb = new StringBuffer();
			fillNote(sb, notes);
			gsnote.setComment(sb.toString());
		}
	}

	private void fillNote(StringBuffer sb, XMLNode node) {
		// TODO: proper note filtering
		if (node.isText()) {
			String text = node.getCharacters().trim();
			if (text.length() > 0) {
				sb.append(text);
			}
			return;
		}

		// TODO: handle more types of HTML tags
		String name = node.getName();
		if ("p".equals(name)) {
			if (sb.length() > 0) {
				sb.append("\n\n");
			}
		} else if ("br".equals(name)) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
		}

		for (int idx=0 ; idx<node.getChildCount() ; idx++) {
			fillNote(sb, node.getChild(idx));
		}
	}
}
