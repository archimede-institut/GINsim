package org.ginsim.gui.utils.data;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JPanel;

import org.ginsim.core.utils.data.GenericList;
import org.ginsim.core.utils.data.GenericNamedList;
import org.ginsim.gui.utils.data.models.MinMaxSpinModel;
import org.ginsim.gui.utils.data.models.SpinModel;
import org.ginsim.gui.utils.widgets.BooleanEditor;
import org.ginsim.gui.utils.widgets.StatusTextField;


/**
 * A panel to edit the properties of an object.
 */
public class GenericPropertyEditorPanel extends JPanel implements GenericPropertyHolder {
	private static final long	serialVersionUID	= -4781276382228645091L;
	
	private static Map m_class = new HashMap();
	private static Insets insets = new Insets(2,5, 2, 5);
	
	static {
        m_class.put(String.class, StatusTextField.class);
        m_class.put(Boolean.class, BooleanEditor.class);
		m_class.put(GenericPropertyInfo[].class, PropertySwitch.class);
		m_class.put(MinMaxSpinModel.class, MinMaxEditor.class);
		m_class.put(SpinModel.class, SpinEditor.class);
		m_class.put(GenericList.class, GenericListPanel.class);
		m_class.put(GenericNamedList.class, GenericNamedListPanel.class);
		m_class.put(Action.class, PropertyActionButton.class);
	}
	public static void addSupportedClass(Class data, Class widget) {
		m_class.put(data, widget);
	}
	public static Class getSupportClass(Class data) {
		return (Class)m_class.get(data);
	}

	ObjectEditor editor;
	int count = -1;
	
	public GenericPropertyEditorPanel(ObjectEditor editor) {
		this.editor = editor;
		setLayout(new GridBagLayout());
		Iterator it = editor.getProperties().iterator();
		while (it.hasNext()) {
			((GenericPropertyInfo)it.next()).build(this);
		}
	}
	
	public void addField(Component cmp, GenericPropertyInfo pinfo, int index) {
		add(cmp, getCst(pinfo, index));
	}
	
	private GridBagConstraints getCst(GenericPropertyInfo pinfo, int num) {
		int[] t_position;
		if (pinfo.l_position == null) {
			t_position = new int[2];
			t_position[0] = num;
			if (num == 0) {
				count++;
			}
			t_position[1] = count;
		} else {
			t_position = (int[])pinfo.l_position.get(num);
		}
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = t_position[0];
		c.insets = insets;
		c.gridy = t_position[1];
		c.fill = GridBagConstraints.HORIZONTAL;
		if (t_position.length > 2) {
			c.gridwidth = t_position[2];
			c.gridheight = t_position[3];
			if (t_position.length > 4) {
				c.weightx = t_position[4];
				c.weighty = t_position[5];
				if (c.weightx != 0) {
					if (c.weighty != 0) {
						c.fill = GridBagConstraints.BOTH;
					} else {
						c.fill = GridBagConstraints.HORIZONTAL;
					}
				} else if (c.weighty != 0){
					c.fill = GridBagConstraints.VERTICAL;
				}
				c.anchor = t_position[6];
			}
		}
		return c;
	}
}
