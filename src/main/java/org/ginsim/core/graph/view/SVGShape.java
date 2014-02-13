package org.ginsim.core.graph.view;

import org.ginsim.service.export.image.SVGWriter;

import java.awt.*;
import java.io.IOException;

/**
 * A Shape which can be exported as SVG
 *
 * @author Aurelien Naldi
 */
public interface SVGShape extends Shape {

    void toSVG(SVGWriter out) throws IOException;

    void toSVG(SVGWriter out, String[] attrs) throws IOException;
}
