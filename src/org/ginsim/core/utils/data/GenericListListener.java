package org.ginsim.core.utils.data;

public interface GenericListListener {

	public void itemAdded(Object item, int pos);
	public void itemRemoved(Object item, int pos);
	public void contentChanged();
	public void structureChanged();
}
