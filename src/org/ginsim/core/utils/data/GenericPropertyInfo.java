package org.ginsim.core.utils.data;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.ginsim.core.utils.log.LogManager;
import org.ginsim.gui.utils.data.GenericPropertyEditorPanel;
import org.ginsim.gui.utils.data.GenericPropertyHolder;


/**
 * All we need to know about a property
 */
public class GenericPropertyInfo {
	public int id;
	public ObjectEditor editor;
	public String name;
	public Class type;
	public List l_position;
	public Object data;
	public boolean isEditable = true;
	
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
	public void setValue(int value) {
		editor.setValue(id, value);
	}
	public void setValue(String value) {
		editor.setValue(id, value);
	}

	public void addPosition(int i, int j, int w, int h, int wx, int wy, int anchor) {
		if (l_position == null) {
			l_position = new ArrayList();
		}
		int[] t = new int[7];
		t[0] = i;
		t[1] = j;
		t[2] = w;
		t[3] = h;
		t[4] = wx;
		t[5] = wy;
		t[6] = anchor;
		l_position.add(t);
	}
	public void addPosition(int i, int j) {
		if (l_position == null) {
			l_position = new ArrayList();
		}
		int[] t = new int[2];
		t[0] = i;
		t[1] = j;
		l_position.add(t);
	}

	public Object getRawValue() {
		return editor.getRawValue(id);
	}

	public void build(GenericPropertyHolder panel) {
		Class cl = GenericPropertyEditorPanel.getSupportClass(type);
		ObjectPropertyEditorUI widget = null;
		try {
			widget = (ObjectPropertyEditorUI)cl.newInstance();
		} catch (Exception e) {}
		
		if (widget == null) {
			// try to find a better constructor
			Object[] args = editor.getArgs();
			Constructor[] constructors = cl.getConstructors();
			for (Constructor constructor: constructors) {
				try {
					widget = (ObjectPropertyEditorUI)constructor.newInstance(args);
				} catch (Exception e) {
					LogManager.error( "Constructor failed: " + constructor + " for " + args);
					LogManager.error( e);
				}
			}
		}
		
		if (widget != null) {
			widget.setEditedProperty(this, panel);
			editor.addListener(widget);
		} else {
			LogManager.debug( "how to deal with this: "+type + " --> " + cl);
			for (Object o: editor.getArgs()) {
				System.err.println("  "+o);
			}
		}
	}

	public void run() {
		editor.performAction(id);
	}
}
