package org.ginsim.service.export.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.imageio.ImageIO;

import org.ginsim.common.application.LogManager;
import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceStatus;
import org.mangosdk.spi.ProviderFor;

/**
 * Export the graph view as image (PNG or SVG).
 *
 * @author Aurelien Naldi
 */
@ProviderFor( Service.class)
@Alias("image")
@ServiceStatus(EStatus.RELEASED)
public class ImageExportService implements Service {

    /**
     * @param graph
     * @param fileName
     */
    public void exportPNG( Graph<?, Edge<?>> graph, String fileName) {

    	Dimension dim = graph.getDimension();
    	int width = (int)dim.getWidth();
    	int height = (int)dim.getHeight();
    	
    	BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    	Graphics2D g = img.createGraphics();
    	g.setColor(Color.white);
    	g.fill(new Rectangle(width, height));
        EdgeAttributesReader ereader = graph.getEdgeAttributeReader();
    	for (Edge edge: graph.getEdges()) {
    		ereader.setEdge(edge);
    		ereader.render(g);
    	}

        NodeAttributesReader nreader = graph.getNodeAttributeReader();
    	for (Object node: graph.getNodes()) {
    		nreader.setNode(node);
    		nreader.render(g);
    	}
    	
    	try {
			ImageIO.write(img, "png", new File(fileName));
		} catch (IOException e) {
			LogManager.error(e);
		}
    }

    /**
     * Export the graph to a SVG file
     *
     * @param graph the graph to export
     * @param nodes the list of nodes that must be exported
     * @param edges the list of edges that must be exported
     * @param fileName the path to the output file
     */
    public void exportSVG( Graph graph, Collection nodes,  Collection<Edge> edges, String fileName) throws IOException{

        SVGEncoder encoder = new SVGEncoder(graph, nodes, edges, fileName);
        try {
            encoder.call();
        } catch (Exception e) {
            LogManager.error(e);
        }
    }

}
