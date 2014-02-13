package org.ginsim.core.graph.view.shapes;

import org.ginsim.core.graph.view.SVGShape;
import org.ginsim.service.export.image.SVGWriter;

import java.awt.geom.Ellipse2D;
import java.io.IOException;

/**
 * An ellipse shape which can be saved in SVG.
 *
 * @author Aurelien Naldi
 */
public class SVGEllipse extends Ellipse2D.Double implements SVGShape {

    public SVGEllipse(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    @Override
    public void toSVG(SVGWriter out) throws IOException {
        toSVG(out, null);
    }
    @Override
    public void toSVG(SVGWriter out, String[] attrs) throws IOException {
        out.openTag("ellipse", attrs);
        out.addAttr("cx", "" + getCenterX());
        out.addAttr("cy", "" + getCenterY());
        out.addAttr("rx", ""+getWidth()/2);
        out.addAttr("ry", ""+getHeight()/2);
        out.closeTag();
    }
}

