package org.ginsim.utils.data;

public enum GenericNamedListCapabilities {

	READ_ONLY (false, false, false, false, false, false),
	REORDER   (true,  false, false, false, false, false),
	EDIT      (true,  true,  false, false, false, false),
	CHANGE    (true,  true,  true,  true,  false, false),
	FULL      (true,  true,  true,  true,  false, true ),

	EDIT_IN   (true,  true,  false, false, true, false),
	CHANGE_IN (true,  true,  true,  true,  true, false),
	FULL_IN   (true,  true,  true,  true,  true, true );

	public final boolean order, edit;
	public final boolean add, remove, inline;
	public final boolean copy;
	
	private GenericNamedListCapabilities(boolean order, boolean edit, boolean add, boolean remove, boolean inline, boolean copy) {
		this.order = order;
		this.edit = edit;
		
		this.add = add;
		this.remove = remove;
		this.inline = inline;
		
		this.copy = copy;
	}
}
