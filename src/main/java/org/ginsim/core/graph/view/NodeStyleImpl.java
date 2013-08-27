package org.ginsim.core.graph.view;

import java.awt.Color;
import java.io.IOException;

import org.ginsim.common.application.LogManager;
import org.ginsim.common.utils.ColorPalette;
import org.ginsim.common.xml.XMLWriter;

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

	private Color bg, fg, txt;
	
	private int width=-1, height=-1;
	
	private NodeShape shape;
	private NodeBorder border;
	
	private final NodeStyle<V> parent;
	
	public NodeStyleImpl() {
		this(null);
	}
	
	public NodeStyleImpl(NodeStyle<V> parent) {
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
	
	public void writeGINML(XMLWriter writer) throws IOException {
		writer.openTag("nodestyle");
		
		if (width>=0 && height>=0) {
			writer.addAttr("width", ""+width);
			writer.addAttr("height", ""+height);
		}
		
		if (shape != null) {
			writer.addAttr("shape", shape.toString());
		}
		
		if (bg!= null) {
			writer.addAttr("background", ColorPalette.getColorCode(bg));
		}
		if (fg != null) {
			writer.addAttr("foreground", ColorPalette.getColorCode(fg));
		}
		if (txt != null) {
			writer.addAttr("text", ColorPalette.getColorCode(txt));
		}
		
		writer.closeTag();
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
}
