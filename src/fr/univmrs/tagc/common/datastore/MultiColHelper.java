package fr.univmrs.tagc.common.datastore;


public interface MultiColHelper {

	public Object getVal(Object o, int index);
	public boolean setVal(Object o, int index, Object value);
}
