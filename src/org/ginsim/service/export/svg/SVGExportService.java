package org.ginsim.service.export.svg;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.view.EdgeAttributesReader;
import org.ginsim.graph.view.NodeAttributesReader;
import org.ginsim.service.Service;
import org.jgraph.util.Bezier;
import org.mangosdk.spi.ProviderFor;



/**
 * export the graph to SVG
 */
@ProviderFor( Service.class)
public class SVGExportService implements Service{

	
    /**
     * Export the graph to a SVG file
     * 
     * @param graph the graph to export
	 * @param nodes the list of nodes that must be exported
	 * @param edges the list of edges that must be exported
	 * @param fileName the path to the output file
	 */
	public void run( Graph graph, Collection nodes,  Collection<Edge> edges, String fileName) throws IOException{
		
		SVGEncoder encoder = new SVGEncoder();
	
		encoder.exportSVG( graph, nodes, edges, fileName);
	}
}
