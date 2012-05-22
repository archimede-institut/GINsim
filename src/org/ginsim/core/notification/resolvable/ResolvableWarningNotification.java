package org.ginsim.core.notification.resolvable;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.notification.WarningNotification;

public class ResolvableWarningNotification extends WarningNotification  implements ResolvableNotification{

	private Object[] data;
	private Graph graph;
	private NotificationResolution resolution;
	
	public ResolvableWarningNotification( Object topic, String message, Graph graph, Object[] data, NotificationResolution resolution) {
		
		super( topic, message);
		this.graph = graph;
		this.data = data;
		this.resolution = resolution;
	}
	
	/**
	 * Execute the chosen option of the resolution associated to the Notification
	 * 
	 * @param index the index of the chosen resolution option
	 * @return true if the resolution was correctly applied, false if not
	 */
	@Override
	public boolean performResolution( int index){
		
		if( resolution != null){
			return resolution.perform( graph, data, index);
		}
		else{
			return false;
		}
	}
	
	/**
	 * Returns the list of the associated resolution options
	 * 
	 * @return the list of the associated resolution options
	 */
	@Override
	public String[] getOptionNames() {
		
		if( resolution != null){
			return resolution.getOptionsName();
		}
		else{
			return null;
		}
		
		
	}
}
