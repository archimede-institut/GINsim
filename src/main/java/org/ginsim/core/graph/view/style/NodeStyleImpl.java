package org.ginsim.core.graph.view.style;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ginsim.common.application.LogManager;
import org.ginsim.common.utils.ColorPalette;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.core.io.parser.GinmlHelper;
import org.xml.sax.Attributes;

/**
 * Simple implementation for NodeStyle.
 * It can be used to override a parent style (defining some properties)
 * or as default style (defining all properties).
 * 
 * @author Aurelien Naldi
 *
 * @param <V>
 */
public class NodeStyleImpl<V> implements NodeStyle<V> {

	private static NodeShape DEFAULT_SHAPE = NodeShape.RECTANGLE;
	private static NodeBorder DEFAULT_BORDER = NodeBorder.SIMPLE;
	
	private static final Color DEFAULT_BACKGROUND = Color.WHITE;
	private static final Color DEFAULT_FOREGROUND = Color.BLACK;

	public static final int DEFAULT_WIDTH = 45;
	public static final int DEFAULT_HEIGHT = 25;
	public static final int MAX_SIZE = 500;
	public static final int MIN_SIZE = 15;

	private static final StyleProperty[] DEFAULT_PROPERTIES = {
		StyleProperty.BACKGROUND,
		StyleProperty.FOREGROUND,
		StyleProperty.TEXT,
		StyleProperty.SHAPE,
		StyleProperty.BORDER,
	};
	
	private Color bg, fg, txt;
	
	private int width=-1, height=-1;
	
	private NodeShape shape;
	private NodeBorder border;
	
	private final NodeStyle<V> parent;
	private final int key;
	
	public NodeStyleImpl() {
		this(0, null);
	}
	
	public NodeStyleImpl(int key, NodeStyle<V> parent) {
		if (parent == null) {
			bg = DEFAULT_BACKGROUND;
			fg = DEFAULT_FOREGROUND;
			txt = DEFAULT_FOREGROUND;
			
			width = DEFAULT_WIDTH;
			height = DEFAULT_HEIGHT;
			
			shape = DEFAULT_SHAPE;
			border = DEFAULT_BORDER;
		}
		this.parent = parent;
		this.key = key;
	}
	
	@Override
	public Color getBackground(V obj) {
		if (bg == null) {
			if (parent == null) {
				return DEFAULT_BACKGROUND;
			}
			return parent.getBackground(obj);
		}
		
		if (parent != null && parent.enforceColors()) {
			return parent.getBackground(obj);
		}
		return bg;
	}

	@Override
	public Color getForeground(V obj) {
		if (fg == null) {
			if (parent == null) {
				return DEFAULT_FOREGROUND;
			}
			return parent.getForeground(obj);
		}

		if (parent != null && parent.enforceColors()) {
			return parent.getForeground(obj);
		}
		return fg;
	}

	@Override
	public Color getTextColor(V obj) {
		if (txt == null) {
			if (parent == null) {
				return DEFAULT_FOREGROUND;
			}
			return parent.getTextColor(obj);
		}

		if (parent != null && parent.enforceColors()) {
			return parent.getTextColor(obj);
		}
		return txt;
	}

	@Override
	public int getWidth(V obj) {
		if (width < 0) {
			if (parent == null) {
				return DEFAULT_WIDTH;
			}
			return parent.getWidth(obj);
		}

		if (parent != null && parent.enforceSize()) {
			return parent.getWidth(obj);
		}
		return width;
	}

	@Override
	public int getHeight(V obj) {
		if (height < 0) {
			if (parent == null) {
				return DEFAULT_HEIGHT;
			}
			return parent.getHeight(obj);
		}

		if (parent != null && parent.enforceSize()) {
			return parent.getHeight(obj);
		}
		return height;
	}

	@Override
	public NodeShape getNodeShape(V obj) {
		if (shape == null) {
			if (parent == null) {
				return DEFAULT_SHAPE;
			}
			return parent.getNodeShape(obj);
		}

		if (parent != null && parent.enforceShape()) {
			return parent.getNodeShape(obj);
		}
		return shape;
	}

	@Override
	public NodeBorder getNodeBorder(V obj) {
		if (border == null) {
			if (parent == null) {
				return DEFAULT_BORDER;
			}
			return parent.getNodeBorder(obj);
		}

		if (parent != null && parent.enforceBorder()) {
			return parent.getNodeBorder(obj);
		}
		return border;
	}

	@Override
	public boolean setBackground(Color bg) {
		if (bg == null && parent == null) {
			return false;
		}
		this.bg = bg;
		return true;
	}

	@Override
	public boolean setForeground(Color fg) {
		if (fg == null && parent == null) {
			return false;
		}
		this.fg = fg;
		return true;
	}

	@Override
	public boolean setTextColor(Color txt) {
		if (txt == null && parent == null) {
			return false;
		}
		this.txt = txt;
		return true;
	}

	@Override
	public boolean setDimension(int w, int h) {
		this.width = w;
		this.height = h;
		return true;
	}

	@Override
	public boolean setNodeShape(NodeShape shape) {
		if (shape == null && parent == null) {
			return false;
		}
		if (this.shape == shape) {
			return false;
		}
		this.shape = shape;
		return true;
	}

	@Override
	public boolean setNodeBorder(NodeBorder border) {
		if (border == null && parent == null) {
			return false;
		}
		if (this.border == border) {
			return false;
		}
		this.border = border;
		return true;
	}

	@Override
	public boolean enforceColors() {
		if (parent != null) {
			return parent.enforceColors();
		}
		return false;
	}

	@Override
	public boolean enforceShape() {
		if (parent != null) {
			return parent.enforceShape();
		}
		return false;
	}

	@Override
	public boolean enforceSize() {
		if (parent != null) {
			return parent.enforceSize();
		}
		return false;
	}

	@Override
	public boolean enforceBorder() {
		if (parent != null) {
			return parent.enforceBorder();
		}
		return false;
	}

	@Override
	public int getKey() {
		return key;
	}

	public String toString() {
		if (key == 0) {
			return "Default node style";
		}
		return "node style "+key;
	}
	

	@Override
	public StyleProperty[] getProperties() {
		return DEFAULT_PROPERTIES;
	}

	@Override
	public Object getProperty(StyleProperty prop) {
		if (prop == StyleProperty.BACKGROUND) {
			return bg;
		}
		if (prop == StyleProperty.FOREGROUND) {
			return fg;
		}
		if (prop == StyleProperty.TEXT) {
			return txt;
		}
		if (prop == StyleProperty.SHAPE) {
			return shape;
		}
		
		if (prop == StyleProperty.BORDER) {
			return border;
		}
		
		return getCustomProperty(prop);
	}

	@Override
	public void setProperty(StyleProperty prop, Object value) {
		if (prop == StyleProperty.BACKGROUND) {
			this.bg = (Color)value;
		} else if (prop == StyleProperty.FOREGROUND) {
			this.fg = (Color)value;
		} else if (prop == StyleProperty.TEXT) {
			this.txt = (Color)value;
		} else if (prop == StyleProperty.SHAPE) {
			this.shape = (NodeShape)value;
		} else if (prop == StyleProperty.BORDER) {
			this.border = (NodeBorder)value;
		} else {
			setCustomProperty(prop, value);
		}
	}

	protected Object getCustomProperty(StyleProperty prop) {
		return null;
	}
	protected void setCustomProperty(StyleProperty prop, Object value) {
	}

	@Override
	public boolean matches(NodeShape shape, Color bg, Color fg, Color text, int w, int h) {
		return
				equals(this.bg, bg) &&
				equals(this.fg, fg) &&
				equals(this.txt, text) &&
				this.width == w && this.height == h;
	}
	
	public static boolean equals(Color c1, Color c2) {
		if (c1 == c2) {
			return true;
		}
		if (c1 == null || c2 == null) {
			return false;
		}
		return c1.equals(c2);
	}
}
