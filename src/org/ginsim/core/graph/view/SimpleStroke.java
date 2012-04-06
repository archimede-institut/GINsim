package org.ginsim.core.graph.view;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;

/**
 * Simple stroke implementation, inspired by BasicStroke
 * but for which we can change the width and dash pattern.
 * 
 * @author Aurelien Naldi
 */
public class SimpleStroke implements Stroke {

	private final float[] DASHPATTERN = {4, 2};
	
	float width = 1;
	float[] dash = null;
	
	int cap = BasicStroke.CAP_SQUARE;
	int join = BasicStroke.JOIN_MITER;
	int miterlimit = 10;
	int dash_phase = 0;

	public void setWidth(float width) {
		if (width < 1) {
			width = 1;
		} else {
			this.width = width;
		}
	}

	public void setDashed(boolean dashed) {
		if (dashed) {
			this.dash = DASHPATTERN;
		} else {
			this.dash = null;
		}
	}
	
	@Override
	public Shape createStrokedShape(Shape s) {
        sun.java2d.pipe.RenderingEngine re =
                sun.java2d.pipe.RenderingEngine.getInstance();
            return re.createStrokedShape(s, width, cap, join, miterlimit,
                                         dash, dash_phase);
	}

	public Shape createSimpleStrokedShape(Shape s) {
        sun.java2d.pipe.RenderingEngine re =
                sun.java2d.pipe.RenderingEngine.getInstance();
            return re.createStrokedShape(s, width, cap, join, miterlimit, null, 0);
	}

}
