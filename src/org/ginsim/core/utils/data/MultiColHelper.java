package org.ginsim.core.utils.data;


public interface MultiColHelper<T> {

	public Object getVal(T o, int index);
	public boolean setVal(T o, int index, Object value);
}
