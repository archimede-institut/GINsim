package org.ginsim.core.notification.resolvable;

public interface ResolvableNotification{

	/**
	 * Execute the resolution
	 * 
	 * @param index The index of the chosen option
	 * @return true if the resolution has been correctly executed
	 */
	public boolean performResolution( int index); 

	/**
	 * Provide the list of options for the resolution
	 * 
	 * @return the list of options for the resolution
	 */
	public String[] getOptionNames();
	
}
