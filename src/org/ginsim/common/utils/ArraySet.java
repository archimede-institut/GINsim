package org.ginsim.common.utils;

import java.util.ArrayList;
import java.util.Set;

public class ArraySet<T> extends ArrayList<T> implements Set<T> {

	public boolean add(T element) {
		if (contains(element)) {
			return false;
		}
		super.add(element);
		return true;
	}
}
