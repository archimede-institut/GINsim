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
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
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
import org.ginsim.gui.utils.data.GenericPropertyInfo;
import org.ginsim.gui.utils.data.ObjectEditor;
import org.ginsim.gui.utils.widgets.StatusTextField;
import org.python.modules.math;

public class StyleEditionPanel extends JPanel {

	private static final Insets inset_l = new Insets(3, 15, 3, 2);
	private static final Insets inset_c = new Insets(3, 2, 3, 2);
	private static final Insets inset_r = new Insets(3, 2, 3, 15);
	
	private final StyleManager styleManager;
	private final GraphGUI gui;

	private final Map<StyleProperty, PropertyEditor> m_properties= new HashMap<StyleProperty, PropertyEditor>();
	
	private final JLabel label = new JLabel();
	private final StatusTextField nameField = new StatusTextField();
	private final JLabel nameLabel = new JLabel("Name");
	
	private final StyleEditor editor;
	
	private final GridBagConstraints c;
	
	public StyleEditionPanel(GraphGUI gui, StyleManager styleManager) {
		super(new GridBagLayout());
		this.gui = gui;
		this.styleManager = styleManager;
		this.editor = new StyleEditor(styleManager);
		
		this.c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		
		GenericPropertyInfo pinfo = new GenericPropertyInfo(editor, StyleEditor.PROP_NAME, "", Action.class);
		nameField.setEditedProperty(pinfo, null);
		
		setStyle(null);
	}
	
	public void setStyle(Style style) {
		removeAll();
		editor.setStyle(style);
		if (editor.getRawValue( StyleEditor.PROP_NAME) == null) {
			nameField.setEditable(false);
		} else {
			nameField.setEditable(true);
		}
		
		if (style == null) {
			label.setText("no style to edit");
			c.gridx = 0;
			c.gridy = 0;
			add(label, c);
			revalidate();
			repaint();
			return;
		}
		
		c.gridx = 0;
		c.gridy = 0;
		c.insets = inset_l;
		add(nameLabel, c);
		c.gridx = 1;
		c.gridwidth = 3;
		c.insets = inset_r;
		nameField.refresh(true);
		add(nameField, c);
		
		c.gridwidth = 1;
		boolean inherit = style.getName() != null;
		int y1=1, y2=1, y3=1;
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
		revalidate();
		repaint();
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
			//ped = new PropertyEditor<Component>(styleManager, property, gui, new JLabel("TODO"), 2);
		}
		m_properties.put(property, ped);
		return ped;
	}
}

abstract class PropertyEditor<C extends Component> {

	public final int col;
	
	protected final StyleProperty property;
	protected final StyleManager styleManager;
	protected final GraphGUI gui;
	
	private final JLabel label;
	private final JButton b_reset;

	protected final C component;

	protected Style style = null;
	protected boolean inherit = false;

	public PropertyEditor(StyleManager styleManager, StyleProperty property, GraphGUI gui, int col) {
		this.property = property;
		this.styleManager = styleManager;
		this.gui = gui;
		this.component = createComponent();
		this.label = new JLabel(property.name);
		this.b_reset = new JButton("x");
		this.b_reset.setToolTipText("Reset to default");
		this.b_reset.setForeground(Color.RED.darker());
		this.b_reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				reset();
			}
		});
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
	
	protected final void update() {
		if (style == null) {
			b_reset.setEnabled( false );
			component.setEnabled(false);
			return;
		}
		Object val = style.getProperty(property);
		b_reset.setEnabled( inherit && val != null );
		component.setEnabled(true);

		if (val == null) {
			val = style.getParentProperty(property);
			doUpdate(val, true);
		} else {
			doUpdate(val, false);
		}
	}
	
	abstract protected void doUpdate(Object val, boolean parent);

	abstract protected C createComponent();
	
}

class ColorPropertyButton extends PropertyEditor<JButton> implements ActionListener {
	
	private Color currentColor = Color.white;
	
	public ColorPropertyButton(StyleManager styleManager, StyleProperty property, GraphGUI gui) {
		super(styleManager, property, gui, 0);
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
	
	@Override
	protected void doUpdate(Object val, boolean parent) {
		this.currentColor = (Color)val;
		if (currentColor == null) {
			updateComponent(Color.WHITE, "#??????");
		} else {
			updateComponent(currentColor, property.getString( val ));
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

	@Override
	protected JButton createComponent() {
		return new JButton();
	}
}

class EnumPropertyBox extends PropertyEditor<JComboBox> implements ActionListener {

	private UndefinedValue undefined;
	private DefaultComboBoxModel model;
	
	protected JComboBox createComponent() {
		Object[] values = ((EnumProperty)property).getValues();
		undefined = new UndefinedValue();
		model = new DefaultComboBoxModel();
		model.addElement(undefined);
		int i=1;
		for (Object o: values) {
			model.addElement(o);
		}
		return new JComboBox(model);
	}
	
	public EnumPropertyBox(StyleManager styleManager, EnumProperty property, GraphGUI gui) {
		super(styleManager, property, gui, 1);
		this.component.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (style == null) {
			return;
		}
		Object current = style.getProperty(this.property);
		Object next = component.getSelectedItem();
		
		if (next instanceof UndefinedValue) {
			next = null;
		}
		
		if (next != current) {
			style.setProperty(property, next);
			styleManager.styleUpdated(style);
			update();
		}
	}
	
	protected void doUpdate(Object val, boolean parent) {
		if (parent) {
			undefined.setDefault(val);
			this.component.setSelectedItem(undefined);
		} else {
			this.component.setSelectedItem(val);
		}
	}
}

class UndefinedValue<T> {
	
	public String defaultName;
	public T value;
	
	public String toString() {
		if (defaultName == null) {
			return "Default";
		}
		return "# "+defaultName;
	}

	public void setDefault(T val) {
		this.value = val;
		if (val == null) {
			defaultName = null;
		} else {
			defaultName = val.toString();
		}
	}
}

class IntegerPropertyBox extends PropertyEditor<JSpinner> implements ChangeListener {
	
	private final PropertySpinnerModel model;
	
	public IntegerPropertyBox(StyleManager styleManager, IntegerProperty property, GraphGUI gui) {
		super(styleManager, property, gui, 2);
		this.model = new PropertySpinnerModel(property);
		this.component.setModel(model);
		this.component.addChangeListener(this);
	}

	@Override
	protected void doUpdate(Object val, boolean parent) {
		Integer value = (Integer)val;
		model.apply(value, parent);
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

	@Override
	protected JSpinner createComponent() {
		return new JSpinner();
	}
}

class PropertySpinnerModel extends AbstractSpinnerModel {
	
	private final UndefinedValue<Integer> undefined = new UndefinedValue();
	private final int fallback, min, max, step;
	
	private int value;
	
	public PropertySpinnerModel(IntegerProperty prop) {
		this.fallback = prop.fallback;
		this.min = prop.min;
		this.max = prop.max;
		this.step = prop.step;
		this.value = -1;
	}

	public void apply(Integer value, boolean parent) {
		if (value == null) {
			value = -1;
		}
		if (parent) {
			undefined.setDefault(value);
			setValue(-1);
		} else {
			setValue(value);
		}
	}

	public int getRawValue() {
		return value;
	}

	@Override
	public Object getNextValue() {
		if (value < 0) {
			return undefined.value;
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
			return undefined.value;
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
		if (value == -1) {
			return undefined;
		}
		return ""+value;
	}

	@Override
	public void setValue(Object value) {
		int next = 0;
		
		if (value == null || value instanceof UndefinedValue) {
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
