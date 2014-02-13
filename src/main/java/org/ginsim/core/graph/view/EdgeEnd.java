package org.ginsim.core.graph.view;

import org.ginsim.core.graph.view.shapes.SVGEllipse;
import org.ginsim.core.graph.view.shapes.SVGPolygon;

/**
 * Available markers for arrow ends.
 *
 * @author Aurelien Naldi
 */
public enum EdgeEnd {


    POSITIVE(new SVGPolygon(
            new int[] {-4, -2, -4, 2},
            new int[] {-5,  0,  5, 0}
        )),
    NEGATIVE(new SVGPolygon(
            new int[] {-1, -1, 1,  1},
            new int[] {-4,  4, 4, -4}
        )),
    DUAL(new SVGPolygon(
            new int[] {-6, -6, -5, -5, -2, -4,  2, -4, -2, -5, -5},
            new int[] {-5,  5,  5,  0,  0,  5,  0, -5,  0,  0, -5}
        )),
    UNKNOWN(new SVGEllipse(-5, -4, 8, 8));


    private final SVGShape shape;
    
    private EdgeEnd(SVGShape shape) {
		this.shape = shape;
	}
    
    public SVGShape getShape() {
    	return shape;
    }
}

