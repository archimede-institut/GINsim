package fr.univmrs.tagc.datastore;

public interface GenericListListener {

	public void ItemAdded(Object item);
	public void itemRemoved(Object item);
	public void ContentChanged();
	public void StructureChanged();
}
