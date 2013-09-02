package org.ginsim.gui.shell.editpanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractSpinnerModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ginsim.common.application.Translator;
import org.ginsim.core.graph.view.style.ColorProperty;
import org.ginsim.core.graph.view.style.EnumProperty;
import org.ginsim.core.graph.view.style.IntegerProperty;
import org.ginsim.core.graph.view.style.Style;
import org.ginsim.core.graph.view.style.StyleManager;
import org.ginsim.core.graph.view.style.StyleProperty;
import org.ginsim.gui.graph.GraphGUI;
import org.python.modules.math;

public class StyleEditionPanel extends JPanel {

	private static final Insets inset_l = new Insets(3, 15, 3, 2);
	private static final Insets inset_c = new Insets(3, 2, 3, 2);
	private static final Insets inset_r = new Insets(3, 2, 3, 15);
	
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
		
		boolean inherit = style.getKey() != 0;
		int y1=0, y2=0, y3=0;
		for (StyleProperty prop: style.getProperties()) {
			if (!m_properties.containsKey(prop)) {
				addProperty(prop);
			}
			PropertyEditor ped = m_properties.get(prop);
			if (ped != null) {
				
				ped.setStyle(style, inherit);
				
				int x;
				int y;
				if (ped.col == 0) {
					x = 0;
					y = y1++;
				} else if (ped.col == 1) {
					x = 3;
					y = y2++;
				} else {
					x = 6;
					y = y3++;
				}
				c.gridx = x;
				c.gridy = y;
				c.insets = inset_l;
				add(ped.getLabel(), c);
				c.gridx = x+1;
				c.gridy = y;
				c.insets = inset_c;
				add(ped.getComponent(), c);
				c.gridx = x+2;
				c.gridy = y;
				c.insets = inset_r;
				add(ped.getResetButton(), c);
			}
		}
	}
	
	private PropertyEditor addProperty(StyleProperty property) {
		PropertyEditor ped = null;
		if (property instanceof ColorProperty) {
			ped = new ColorPropertyButton(styleManager, (ColorProperty)property, gui);
		} else if (property instanceof EnumProperty) {
			ped = new EnumPropertyBox(styleManager, (EnumProperty)property, gui);
		} else if (property instanceof IntegerProperty) {
			ped = new IntegerPropertyBox(styleManager, (IntegerProperty)property, gui);
		} else {
			ped = new PropertyEditor<Component>(styleManager, property, gui, new JLabel("TODO"), 2);
		}
		m_properties.put(property, ped);
		return ped;
	}
}

class PropertyEditor<C extends Component> {

	public final int col;
	
	protected final StyleProperty property;
	protected final StyleManager styleManager;
	protected final GraphGUI gui;
	
	private final JLabel label;
	private final JButton b_reset;

	protected final C component;

	protected Style style = null;
	protected boolean inherit = false;

	public PropertyEditor(StyleManager styleManager, StyleProperty property, GraphGUI gui, C component, int col) {
		this.property = property;
		this.styleManager = styleManager;
		this.gui = gui;
		this.label = new JLabel(property.name);
		this.b_reset = new JButton("x");
		this.b_reset.setForeground(Color.RED.darker());
		this.b_reset.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				reset();
			}
		});
		this.component = component;
		this.col = col;
	}

	public Component getLabel() {
		return label;
	}

	public Component getComponent() {
		return component;
	}
	public Component getResetButton() {
		return b_reset;
	}

	public StyleProperty getProperty() {
		return property;
	}

	public void setStyle(Style style, boolean inherit) {
		this.style = style;
		this.inherit = inherit;
		update();
	}
	
	protected void reset() {
		if (style == null || !inherit) {
			return;
		}
		this.style.setProperty(property, null);
		styleManager.styleUpdated(style);
		update();
	}
	
	protected void update() {
		if (style == null) {
			return;
		}
		Object val = style.getProperty(property);
		b_reset.setEnabled( inherit && val != null );
	}
	
}

class ColorPropertyButton extends PropertyEditor<JButton> implements ActionListener {
	
	private Color currentColor = Color.white;
	
	public ColorPropertyButton(StyleManager styleManager, StyleProperty property, GraphGUI gui) {
		super(styleManager, property, gui, new JButton(), 0);
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
		super.update();
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
		super(styleManager, property, gui, new JComboBox(property.getValues()), 1);
		this.component.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (style == null) {
			return;
		}
		Object current = style.getProperty(this.property);
		Object next = component.getSelectedItem();
		
		if (next != current) {
			style.setProperty(property, next);
			styleManager.styleUpdated(style);
			update();
		}
	}
	
	protected void update() {
		super.update();
		if (style == null) {
			return;
		}
		this.component.setSelectedItem(style.getProperty(this.property));
	}
}

class IntegerPropertyBox extends PropertyEditor<JSpinner> implements ChangeListener {
	
	private final PropertySpinnerModel model;
	
	public IntegerPropertyBox(StyleManager styleManager, IntegerProperty property, GraphGUI gui) {
		super(styleManager, property, gui, new JSpinner(), 2);
		
		this.model = new PropertySpinnerModel(property);
		this.component.setModel(model);
		this.component.addChangeListener(this);
	}

	protected void update() {
		super.update();
		if (style == null) {
			return;
		}
		Integer value = (Integer)style.getProperty(property);
		if (value == null) {
			component.setBackground(Color.YELLOW);
			component.setValue(null);
		} else {
			component.setBackground(Color.WHITE);
			component.setValue(value);
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (style == null) {
			return;
		}
		
		Object current = style.getProperty(this.property);
		Object next = model.getRawValue();
		
		if (next != current) {
			style.setProperty(property, next);
			styleManager.styleUpdated(style);
			update();
		}
	}
}

class PropertySpinnerModel extends AbstractSpinnerModel {
	
	private final int fallback, min, max, step;
	
	private int value;
	
	public PropertySpinnerModel(IntegerProperty prop) {
		this.fallback = prop.fallback;
		this.min = prop.min;
		this.max = prop.max;
		this.step = prop.step;
		this.value = -1;
	}

	public int getRawValue() {
		return value;
	}

	@Override
	public Object getNextValue() {
		if (value < 0) {
			return fallback;
		}
		
		if (value < min) {
			return min;
		}
		
		int next = value+step;
		if (next > max) {
			return max;
		}
		
		return next;
	}

	@Override
	public Object getPreviousValue() {
		if (value < 0) {
			return fallback;
		}
		
		if (value > max) {
			return max;
		}
		
		int next = value-step;
		if (next < min) {
			return min;
		}
		
		return next;
	}

	@Override
	public Object getValue() {
		return ""+value;
	}

	@Override
	public void setValue(Object value) {
		int next = 0;
		
		if (value == null) {
			next = -1;
		} else if (value instanceof Integer) {
			next = (Integer)value;
		} else if (value instanceof String) {
			try {
				next = Integer.parseInt((String)value);
			} catch (Exception e) {}
		}
		
		if (next == 0) {
			// invalid input?
			return;
		}
		
		if (next > max) {
			next = max;
		} else if (next != -1 && next < min) {
			next = min;
		}
		
		if (next != this.value) {
			this.value = next;
			fireStateChanged();
		}
	}
}
