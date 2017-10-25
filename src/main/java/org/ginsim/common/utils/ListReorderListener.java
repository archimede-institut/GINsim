package org.ginsim.common.utils;

public interface ListReorderListener {

	void reordered(int[] mapping);

	void deleted(int[] sel);
	
}
