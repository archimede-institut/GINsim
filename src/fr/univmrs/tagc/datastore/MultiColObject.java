package fr.univmrs.tagc.datastore;

public interface MultiColObject {
	public Object getVal(int index);
	public boolean setVal(int index, Object value);
}
