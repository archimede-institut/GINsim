package org.ginsim.common.xml;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * XMLWriter extension with some SVG-specific methods.
 *
 * @author Aurelien Naldi
 */
public class SVGWriter extends XMLWriter {

    private final static String DOCTYPE = "svg PUBLIC \"-//W3C//DTD SVG 20001102//EN\" \"http://www.w3.org/TR/2000/CR-SVG-20001102/DTD/svg-20001102.dtd\"";

    public SVGWriter(String filename, Dimension dim) throws IOException {
        super(filename, DOCTYPE);
        start(dim);
    }

    public SVGWriter(OutputStreamWriter writer, Dimension dim) throws IOException {
        super(writer, DOCTYPE);
        start(dim);
    }
    
    void start(Dimension dim) throws IOException {
        String[] attrs = {
                "width", ""+dim.getWidth(),
                "height", ""+dim.getHeight(),
                "xmlns", "http://www.w3.org/2000/svg",
                "xmlns:inkscape", "http://www.inkscape.org/namespaces/inkscape",
                "version", "1.1"
        };
        openTag("svg", attrs);
    }

}
