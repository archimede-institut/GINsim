package org.ginsim.core.graph.view.shapes;


import org.ginsim.core.graph.view.SVGShape;
import org.ginsim.service.export.image.SVGWriter;

import java.awt.*;
import java.io.IOException;

public class SVGRectangle extends Rectangle implements SVGShape {

    public SVGRectangle(int x, int y, int w, int h) {
        super(x,y, w,h);
    }

    @Override
    public void toSVG(SVGWriter out) throws IOException {
        toSVG(out, null);
    }

    @Override
    public void toSVG(SVGWriter out, String[] attrs) throws IOException {

        out.openTag("rect", attrs);
        out.addAttr("width", ""+width);
        out.addAttr("height", ""+height);
        out.addAttr("x", ""+x);
        out.addAttr("y", ""+y);
        out.closeTag();
    }
}
