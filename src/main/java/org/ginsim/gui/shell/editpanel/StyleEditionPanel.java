package org.ginsim.gui.shell.editpanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ginsim.common.application.Translator;
import org.ginsim.core.graph.view.EdgePattern;
import org.ginsim.core.graph.view.style.ColorProperty;
import org.ginsim.core.graph.view.style.EnumProperty;
import org.ginsim.core.graph.view.style.Style;
import org.ginsim.core.graph.view.style.StyleManager;
import org.ginsim.core.graph.view.style.StyleProperty;
import org.ginsim.gui.graph.GraphGUI;
import org.python.modules.math;

public class StyleEditionPanel extends JPanel {

	private final StyleManager styleManager;
	private final GraphGUI gui;

	private final Map<StyleProperty, PropertyEditor> m_properties= new HashMap<StyleProperty, PropertyEditor>();
	private final List<PropertyEditor> editors = new ArrayList<PropertyEditor>();
	
	JLabel label = new JLabel();
	
	private final GridBagConstraints c;
	
	public StyleEditionPanel(GraphGUI gui, StyleManager styleManager) {
		super(new GridBagLayout());
		this.gui = gui;
		this.styleManager = styleManager;
		
		this.c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		setStyle(null);
	}
	
	public void setStyle(Style style) {
		removeAll();
		
		if (style == null) {
			label.setText("no style to edit");
			c.gridx = 0;
			c.gridy = 0;
			add(label, c);
			return;
		}
		
		int x = 0;
		int y = 0;
		for (StyleProperty prop: style.getProperties()) {
			if (!m_properties.containsKey(prop)) {
				addProperty(prop);
			}
			PropertyEditor ped = m_properties.get(prop);
			if (ped != null) {
				
				ped.setStyle(style);
				
				c.gridx = x;
				c.gridy = y;
				add(ped.getLabel(), c);
				c.gridx = x+1;
				c.gridy = y;
				add(ped.getComponent(), c);
				y++;
			}
		}
	}
	
	private PropertyEditor addProperty(StyleProperty property) {
		PropertyEditor ped = null;
		if (property instanceof ColorProperty) {
			ped = new ColorPropertyButton(styleManager, (ColorProperty)property, gui);
		} else if (property instanceof EnumProperty) {
			ped = new EnumPropertyBox(styleManager, (EnumProperty)property, gui);
		} else {
			ped = new PropertyEditor<Component>(styleManager, property, gui, new JLabel("TODO"));
		}
		m_properties.put(property, ped);
		return ped;
	}
}

class PropertyEditor<C extends Component> {

	protected final StyleProperty property;
	protected final StyleManager styleManager;
	protected final GraphGUI gui;
	
	private final JLabel label;

	protected final C component;

	protected Style style = null;

	public PropertyEditor(StyleManager styleManager, StyleProperty property, GraphGUI gui, C component) {
		this.property = property;
		this.styleManager = styleManager;
		this.gui = gui;
		this.label = new JLabel(property.name);
		this.component = component;
	}

	public Component getLabel() {
		return label;
	}

	public Component getComponent() {
		return component;
	}

	public StyleProperty getProperty() {
		return property;
	}

	public void setStyle(Style style) {
		this.style = style;
		update();
	}
	
	protected void update() {
	}
	
}

class ColorPropertyButton extends PropertyEditor<JButton> implements ActionListener {
	
	private Color currentColor = Color.white;
	
	public ColorPropertyButton(StyleManager styleManager, StyleProperty property, GraphGUI gui) {
		super(styleManager, property, gui, new JButton());
		this.component.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (style == null) {
			return;
		}
		Color selected = JColorChooser.showDialog(null, Translator.getString("choose_color"), currentColor);
		if (selected != null) {
			style.setProperty(property, selected);
			styleManager.styleUpdated(style);
		}
		update();
	}
	
	protected void update() {
		if (style == null) {
			this.currentColor = null;
			updateComponent(currentColor, "-----");
		} else {
			this.currentColor = (Color)style.getProperty(property);
			if (currentColor == null) {
				updateComponent(Color.WHITE, "UNDEFINED");
			} else {
				updateComponent(currentColor, property.getString( style.getProperty(property) ));
			}
		}
	}
	
	public void updateComponent(Color c, String text) {
		component.setBackground(c);
		component.setText(text);
		
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		double brightness = r*r*.241  +  g*g*.691 + b*b*.068;
		brightness = math.sqrt(brightness);
		
		if (brightness > 200) {
			component.setForeground(Color.BLACK);
		} else {
			component.setForeground(Color.WHITE);
		}
	}
}

class EnumPropertyBox extends PropertyEditor<JComboBox> implements ActionListener {
	
	public EnumPropertyBox(StyleManager styleManager, EnumProperty property, GraphGUI gui) {
		super(styleManager, property, gui, new JComboBox(property.getValues()));
		this.component.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (style == null) {
			return;
		}
		style.setProperty(property, component.getSelectedItem());
		styleManager.styleUpdated(style);
		update();
	}
	
	protected void update() {
		if (style == null) {
		} else {
			this.component.setSelectedItem(style.getProperty(this.property));
			// TODO
		}
	}
}