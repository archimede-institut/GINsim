package org.ginsim.service.export.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

/**
 * export the graph to PNG
 * 
 */
@ProviderFor( Service.class)
public class ImageExportService implements Service {

    /**
     * @param graph
     * @param selectedOnly
     * @param fileName
     */
    public static void export( Graph<?, Edge<?>> graph, boolean selectedOnly, String fileName) {

    	int width = 0;
    	int height = 0;
    	
    	// find image bounds
    	NodeAttributesReader nreader = graph.getNodeAttributeReader();
    	EdgeAttributesReader ereader = graph.getEdgeAttributeReader();
    	for (Object node: graph.getNodes()) {
    		nreader.setNode(node);
    		int x = nreader.getX() + nreader.getWidth();
    		if (x > width) {
    			width = x;
    		}
    		int y = nreader.getY() + nreader.getHeight();
    		if (y > height) {
    			height = y;
    		}
    	}
    	
    	BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    	Graphics2D g = img.createGraphics();
    	g.setColor(Color.white);
    	g.fill(new Rectangle(width, height));
    	for (Object node: graph.getNodes()) {
    		nreader.render(node, g);
    	}
    	
    	for (Edge edge: graph.getEdges()) {
    		ereader.render(nreader, edge, g);
    	}

    	try {
			ImageIO.write(img, "png", new File(fileName));
		} catch (IOException e) {
			LogManager.error(e);
		}
    }
    
}
