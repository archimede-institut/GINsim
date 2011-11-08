package fr.univmrs.tagc.GINsim.export.generic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.graph.GsGraph;

/**
 * export the graph to SVG
 */
public class ImageExport {

    /**
     * @param graph
     * @param selectedOnly
     * @param fileName
     */
    public static void exportImage( Graph graph, boolean selectedOnly, String fileName) {

    	BufferedImage img = graph.getGraphManager().getImage();
    	
    	if (img != null) {
    			try {
					ImageIO.write(img, "png", new File(fileName));
				} catch (IOException e) {
				}
	    		return;
    	}
    }
    
}
