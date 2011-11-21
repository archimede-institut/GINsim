package org.ginsim.service.export.image;

import org.ginsim.graph.common.Graph;
import org.ginsim.service.Service;
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
    public static void exportImage( Graph graph, boolean selectedOnly, String fileName) {

//    	BufferedImage img = graph.getImage();
//    	
//    	if (img != null) {
//    			try {
//					ImageIO.write(img, "png", new File(fileName));
//				} catch (IOException e) {
//				}
//	    		return;
//    	}
    }
    
}
