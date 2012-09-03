package org.ginsim.service.export.sbml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLStreamException;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.io.sbml.SBMLQualBundle;
import org.colomoto.logicalmodel.io.sbml.SBMLqualExport;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;
import org.sbml.jsbml.ext.layout.BoundingBox;
import org.sbml.jsbml.ext.layout.Dimensions;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.Position;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;

@ProviderFor( Service.class)
@Alias("SBMLe")
public class SBMLQualExportService implements Service {

	/**
	 * Convenience method to export without having to configure anything.
	 * 
	 * @param graph
	 * @param filename
	 * @throws IOException
	 */
	public void export( RegulatoryGraph graph, String filename) throws IOException {
		export(graph, new SBMLQualConfig(graph), filename);
	}

	/**
	 * Perform the SBML export, using the JSBML-based encoder from LogicalModel.
	 * 
	 * @param graph the graph to export
	 * @param config the configuration structure
	 * @param filename the path to the target file
	 * @throws IOException
	 */
	public void export( RegulatoryGraph graph, SBMLQualConfig config, String filename) throws IOException{
		LogicalModel model = graph.getModel();
		OutputStream out = new FileOutputStream(new File(filename));
		try {
			SBMLqualExport sExport = new SBMLqualExport(model);
			SBMLQualBundle qbundle = sExport.getSBMLBundle();
			
			// add basic layout information
			if (qbundle.lmodel != null) {
				Layout layout = new Layout();
				qbundle.lmodel.addLayout(layout);
				NodeAttributesReader nreader = graph.getNodeAttributeReader();
				for (RegulatoryNode node: graph.getNodeOrder()) {
					nreader.setNode(node);
					SpeciesGlyph glyph = new SpeciesGlyph();
					glyph.setSpecies(sExport.getSpecies(node.getNodeInfo()).getId());
					BoundingBox bb = new BoundingBox();
					Position pos = bb.createPosition();
					pos.setX(nreader.getX());
					pos.setY(nreader.getY());
					Dimensions dim = bb.createDimensions();
					dim.setWidth(nreader.getWidth());
					dim.setHeight(nreader.getHeight());
					glyph.setBoundingBox(bb);
					layout.add(glyph);
				}
			}
			
			sExport.export(out);
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}
}
