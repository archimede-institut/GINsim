package org.ginsim.service.imports.sbml;

import java.io.File;
import java.util.List;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.io.sbml.SBMLQualBundle;
import org.colomoto.logicalmodel.io.sbml.SBMLqualImport;
import org.ginsim.core.graph.regulatorygraph.LogicalModel2RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.ext.layout.BoundingBox;
import org.sbml.jsbml.ext.layout.Dimensions;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.Point;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;

@ProviderFor( Service.class)
@Alias("SBMLi")
public class SBMLImportService implements Service {

	private static boolean USEJSBML = true;
	
	/**
	 * Return the graph built from the SBML file at the given path
	 * 
	 * @param filename the path of the SBML file describing the graph
	 * @return the graph built from the SBML file at the given path
	 */
	public RegulatoryGraph run( String filename) {
		if (USEJSBML) {
			return runJSBML(filename);
		}
		
		return runLegacy(filename);
	}

	/**
	 * SBML import based on a custom parser.
	 * 
	 * @param filename
	 * @return
	 */
	@Deprecated
	public RegulatoryGraph runLegacy( String filename){
		
		SBMLXpathParser parser = new SBMLXpathParser(filename);
		RegulatoryGraph new_graph = parser.getGraph();
		
		return new_graph;
	}

	/**
	 * SBML import using The JSBML-based parser in LogicalModel.
	 * 
	 * @param filename
	 * @return
	 */
	public RegulatoryGraph runJSBML( String filename) {

		try {
			SBMLqualImport simport = new SBMLqualImport(new File(filename));
			LogicalModel model = simport.getModel();
			RegulatoryGraph lrg = LogicalModel2RegulatoryGraph.importModel(model);
			
			SBMLQualBundle qbundle = simport.getQualBundle();
			
			// TODO: add unused interactions and consistency checks
			
			// add layout information
			if (qbundle.lmodel != null) {
				ListOf<Layout> layouts = qbundle.lmodel.getListOfLayouts();
				if (layouts != null && layouts.size() > 0) {
					Layout layout = layouts.get(0);
					NodeAttributesReader nreader = lrg.getNodeAttributeReader();
					List<RegulatoryNode> nodes = lrg.getNodeOrder();
					for (SpeciesGlyph glyph: layout.getListOfSpeciesGlyphs()) {
						String sid = glyph.getSpecies();
						try {
							nreader.setNode( nodes.get( simport.getIndexForName(sid)));
							BoundingBox bb = glyph.getBoundingBox();
							Point pos = bb.getPosition();
							if (pos != null) {
								nreader.setPos((int)pos.getX(), (int)pos.getY());
							}
							Dimensions dim = bb.getDimensions();
							if (dim != null) {
								nreader.setSize((int)dim.getWidth(), (int)dim.getHeight());
							}
						} catch (Exception e) {
							
						}
					}
				}
			}
			
			return lrg;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
