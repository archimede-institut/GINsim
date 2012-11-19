package org.ginsim.core.graph.objectassociation;

/**
 * A User Supporter handles some simple data store for "users".
 * Each of them will provide its own API, the only common part deals with
 * signals for the renaming and removal of users.
 * 
 * @author Aurelien Naldi
 */
public interface UserSupporter {

	/**
	 * A user was updated, make sure to clean related data.
	 * 
	 * @param oldID ID under which this user was previously known
	 * @param newID new ID, or null if it was removed
	 */
	void update(String oldID, String newID);
}
