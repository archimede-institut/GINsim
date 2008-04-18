package fr.univmrs.tagc.GINsim.export.generic;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;

import com.ibm.jvm.util.ByteArrayOutputStream;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

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
    public static void exportImage(GsGraph graph, boolean selectedOnly, String fileName) {

    	BufferedImage img = graph.getGraphManager().getImage();
    	if (img != null) {
	    	ByteArrayOutputStream out = new ByteArrayOutputStream();
	    	JPEGImageEncoder enc = JPEGCodec.createJPEGEncoder(out);
	
	    	try {
	        	enc.encode(img);
	    		FileOutputStream fimage = new FileOutputStream(fileName);
	    		out.writeTo(fimage);
	    		fimage.close();
	    		return;
	    	} catch (IOException e) {
			}
    	}
    }
    
}
