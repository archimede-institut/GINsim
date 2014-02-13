package org.ginsim.core.graph.view.shapes;

import org.ginsim.core.graph.view.SVGShape;
import org.ginsim.service.export.image.SVGWriter;

import java.awt.*;
import java.io.IOException;

/**
 * A polygon shape which can be saved in SVG.
 *
 * @author Aurelien Naldi
 */
public class SVGPolygon extends Polygon implements SVGShape {

    public SVGPolygon(int[] xpoints, int[] ypoints) {
        super(xpoints, ypoints, xpoints.length);
    }

    @Override
    public void toSVG(SVGWriter out) throws IOException {
        toSVG(out, null);
    }

    @Override
    public void toSVG(SVGWriter out, String[] attrs) throws IOException {
        if (npoints < 2) {
            return;
        }

        out.openTag("path", attrs);
        StringBuffer sb = new StringBuffer("M "+xpoints[0] + " "+ypoints[0]);
        for (int i=1 ; i<npoints ; i++) {
            sb.append(" L "+xpoints[i]+" "+ypoints[i]);
        }
        sb.append(" z");
        out.addAttr("d", sb.toString());
        out.closeTag();
    }

}
