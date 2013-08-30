package org.ginsim.gui.shell.editpanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ginsim.common.application.Translator;
import org.ginsim.core.graph.view.style.Style;
import org.ginsim.core.graph.view.style.StyleManager;
import org.ginsim.core.graph.view.style.StyleProperty;
import org.python.modules.math;

public class StyleEditionPanel extends JPanel {

	private final StyleManager styleManager;
	private final Frame parent;

	private final Map<StyleProperty, PropertyEditor> m_properties= new HashMap<StyleProperty, PropertyEditor>();
	private final List<PropertyEditor> editors = new ArrayList<PropertyEditor>();
	
	JLabel label = new JLabel();
	
	
	public StyleEditionPanel(Frame parent, StyleManager styleManager) {
		this.parent = parent;
		this.styleManager = styleManager;
		
		add(label);
		setStyle(null);
		
		// some hardcoded properties for now
		addColorProperty(StyleProperty.BACKGROUND);
		addColorProperty(StyleProperty.FOREGROUND);
		addColorProperty(StyleProperty.TEXT);
		addColorProperty(StyleProperty.COLOR);
		
	}
	
	public void setStyle(Style style) {
		removeAll();
		
		if (style == null) {
			label.setText("no style to edit");
			add(label);
			return;
		}
		
		for (StyleProperty prop: style.getProperties()) {
			PropertyEditor ped = m_properties.get(prop);
			if (ped == null) {
				// TODO: create editors on demand
			}
			
			if (ped != null) {
				ped.setStyle(style);
				add(ped.getComponent());
			}
		}
	}
	
	private void addColorProperty(StyleProperty property) {
		PropertyEditor ped = new ColorPropertyButton(styleManager, property, parent);
		m_properties.put(property, ped);
	}
}

interface PropertyEditor {
	Component getComponent();
	StyleProperty getProperty();
	void setStyle(Style style);
}

class ColorPropertyButton extends JButton implements ActionListener, PropertyEditor {
	
	private final StyleProperty property;
	private final StyleManager styleManager;
	private final Frame parent;
	
	private Style style = null;
	private Color currentColor = Color.white;
	
	public ColorPropertyButton(StyleManager styleManager, StyleProperty property, Frame parent) {
		this.property = property;
		this.styleManager = styleManager;
		this.parent = parent;
		this.addActionListener(this);
	}

	@Override
	public void setStyle(Style style) {
		this.style = style;
		update();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (style == null) {
			return;
		}
		Color selected = JColorChooser.showDialog(parent,Translator.getString("choose_color"), currentColor);
		if (selected != null) {
			style.setProperty(property, selected);
		}
		update();
	}
	
	private void update() {
		if (style == null) {
			this.currentColor = null;
			setText("--");
			setBackground(currentColor);
		} else {
			this.currentColor = (Color)style.getProperty(property);
			if (currentColor == null) {
				setText("UNDEFINED");
				setBackground(Color.WHITE);
			} else {
				setText(property.getString( style.getProperty(property) ));
				setBackground(currentColor);
			}
		}
	}
	
	@Override
	public void setBackground(Color c) {
		super.setBackground(c);
		
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		double brightness = r*r*.241  +  g*g*.691 + b*b*.068;
		brightness = math.sqrt(brightness);
		
		if (brightness > 200) {
			setForeground(Color.BLACK);
		} else {
			setForeground(Color.WHITE);
		}
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public StyleProperty getProperty() {
		return property;
	}
}