package fr.univmrs.tagc.common.datastore;


public interface MultiColHelper<T> {

	public Object getVal(T o, int index);
	public boolean setVal(T o, int index, T value);
}
