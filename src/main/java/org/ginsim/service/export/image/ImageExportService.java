package org.ginsim.service.export.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.ginsim.common.application.LogManager;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

/**
 * export the graph to PNG
 * 
 */
@ProviderFor( Service.class)
@Alias("image")
public class ImageExportService implements Service {

    /**
     * @param graph
     * @param selectedOnly
     * @param fileName
     */
    public static void export( Graph<?, Edge<?>> graph, String fileName) {

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
    
}
