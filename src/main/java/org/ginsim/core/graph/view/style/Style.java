package org.ginsim.core.graph.view.style;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.ginsim.common.xml.XMLWriter;
import org.xml.sax.Attributes;

public interface Style {

	StyleProperty[] getProperties();
	
	Object getProperty(StyleProperty prop);
	void setProperty(StyleProperty prop, Object value);
}
