package fr.univmrs.tagc.datastore;

public interface GenericListListener {

	public void ItemAdded(Object item, int pos);
	public void itemRemoved(Object item, int pos);
	public void ContentChanged();
	public void StructureChanged();
}
