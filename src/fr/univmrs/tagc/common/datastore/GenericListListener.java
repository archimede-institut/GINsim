package fr.univmrs.tagc.common.datastore;

public interface GenericListListener {

	public void itemAdded(Object item, int pos);
	public void itemRemoved(Object item, int pos);
	public void contentChanged();
	public void structureChanged();
}
