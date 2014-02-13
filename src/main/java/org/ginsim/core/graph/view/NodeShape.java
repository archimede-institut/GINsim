package org.ginsim.core.graph.view;

import org.ginsim.service.export.image.SVGWriter;

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.io.IOException;

public enum NodeShape {

	RECTANGLE, ELLIPSE;
	
	public SVGShape getShape(int x, int y, int width, int height) {
		
		switch (this) {
		case ELLIPSE:
			return new SVGEllipsis(x, y, width, height);
		}
		return new SVGRect(x, y, width, height);
	}
}


class SVGRect extends Rectangle implements SVGShape {

    public SVGRect(int x, int y, int w, int h) {
        super(x,y, w,h);
    }

    public void toSVG(SVGWriter out) throws IOException {

        String[] attrs = {
            "width", ""+width,
            "height", ""+height,
            "x", ""+x,
            "y", ""+y
        };
        out.openTag("rect", attrs);
    }
}

class SVGEllipsis extends Ellipse2D.Double implements SVGShape {

    public SVGEllipsis(int x, int y, int w, int h) {
        super(x,y, w,h);
    }

    public void toSVG(SVGWriter out) throws IOException {
        double rx = width/2;
        double ry = height/2;

        String[] attrs = {
                "rx", ""+rx,
                "ry", ""+ry,
                "cx", ""+(x+rx),
                "cy", ""+(y+ry),
        };
        out.openTag("ellipse", attrs);
    }
}
