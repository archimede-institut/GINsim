package fr.univmrs.tagc.datastore;

import java.util.Vector;

import fr.univmrs.tagc.datastore.gui.GenericPropertyEditorPanel;
import fr.univmrs.tagc.datastore.gui.GenericPropertyHolder;

/**
 * All we need to know about a property
 */
public class GenericPropertyInfo {
	public int id;
	public ObjectEditor editor;
	public String name;
	public Class type;
	public Vector v_position;
	public Object data;
	
	public GenericPropertyInfo(ObjectEditor editor, int id, String name, Class type) {
		this.editor = editor;
		this.id = id;
		this.name = name;
		this.type = type;
	}

	public String getStringValue() {
		return editor.getStringValue(id);
	}
    public boolean isValidValue(String value) {
    	return editor.isValidValue(id, value);
    }
	public void setValue(String value) {
		editor.setValue(id, value);
	}

	public void addPosition(int i, int j, int w, int h, int wx, int wy, int anchor) {
		if (v_position == null) {
			v_position = new Vector();
		}
		int[] t = new int[7];
		t[0] = i;
		t[1] = j;
		t[2] = w;
		t[3] = h;
		t[4] = wx;
		t[5] = wy;
		t[6] = anchor;
		v_position.add(t);
	}
	public void addPosition(int i, int j) {
		if (v_position == null) {
			v_position = new Vector();
		}
		int[] t = new int[2];
		t[0] = i;
		t[1] = j;
		v_position.add(t);
	}

	public Object getRawValue() {
		return editor.getRawValue(id);
	}

	public void build(GenericPropertyHolder panel) {
		Class cl = GenericPropertyEditorPanel.getSupportClass(type);
		ObjectPropertyEditorUI widget;
		try {
			widget = (ObjectPropertyEditorUI)cl.newInstance();
			widget.setEditedProperty(this, panel);
			editor.addListener(widget);
		} catch (Exception e) {
			System.out.println("how to deal with this: "+type);
			e.printStackTrace();
		}
	}
}
