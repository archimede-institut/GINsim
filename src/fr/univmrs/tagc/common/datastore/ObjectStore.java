package fr.univmrs.tagc.common.datastore;

public class ObjectStore {
	Object[] t;
	
	public ObjectStore() {
		this(1);
	}
	public ObjectStore(int len) {
		t = new Object[len];
	}
	
	public Object getObject(int id) {
		return t[id];
	}
	public void setObject(int id, Object object) {
		t[id] = object;
	}
	public int getSize() {
	    return t.length;
	}
}
