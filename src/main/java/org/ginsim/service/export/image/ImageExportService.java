package org.ginsim.service.export.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

import org.ginsim.common.application.LogManager;
import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.ViewHelper;
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

    private void setDPI(int f, IIOMetadata metadata) throws IIOInvalidTreeException {

        // for PNG, it's dots per millimeter: divide by 2.54
        double dotsPerMilli = f * 75 / 25.4;

        IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
        horiz.setAttribute("value", Double.toString(dotsPerMilli));

        IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
        vert.setAttribute("value", Double.toString(dotsPerMilli));

        IIOMetadataNode dim = new IIOMetadataNode("Dimension");
        dim.appendChild(horiz);
        dim.appendChild(vert);

        IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
        root.appendChild(dim);

        metadata.mergeTree("javax_imageio_1.0", root);
    }

    /**
     * Export a model view as PNG image.
     *
     * @param graph
     * @param fileName
     */
    public void exportPNG( Graph<?, Edge<?>> graph, String fileName) {
        exportPNG(graph, fileName, 4);
    }

    /**
     * Export a model view as PNG image.
     *
     * @param graph
     * @param fileName
     * @param f the zoom factor
     */
    public void exportPNG( Graph<?, Edge<?>> graph, String fileName, int f) {

    	Dimension dim = graph.getDimension();
    	int width = f*(int)dim.getWidth();
    	int height = f*(int)dim.getHeight();
    	
    	BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    	Graphics2D g = img.createGraphics();
        g.setFont(ViewHelper.GRAPHFONT);
    	g.setColor(Color.white);
    	g.fill(new Rectangle(width, height));
        g.scale(f,f);
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
            saveImage(img, f, fileName);
		} catch (IOException e) {
			LogManager.error(e);
		}
    }

    private void saveImage(BufferedImage img, String filename) throws  IOException {
        ImageIO.write(img, "png", new File(filename));
    }

    private void saveImage(BufferedImage img, int f, String filename) throws  IOException {
        if (f != 1) {
            for (Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName("png"); iw.hasNext();) {
                try {
                    ImageWriter writer = iw.next();
                    ImageWriteParam writeParam = writer.getDefaultWriteParam();
                    ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
                    IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
                    if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
                        continue;
                    }

                    setDPI(f, metadata);

                    File output = new File(filename);
                    final ImageOutputStream stream = ImageIO.createImageOutputStream(output);
                    try {
                        writer.setOutput(stream);
                        writer.write(metadata, new IIOImage(img, null, metadata), writeParam);
                    } finally {
                        stream.close();
                    }

                    return;
                } catch (Exception e) {
                    // just try the next one
                }
            }
        }

        // f is 1 or it failed somewhere: fallback to the DPI-free method
        saveImage(img, filename);
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
