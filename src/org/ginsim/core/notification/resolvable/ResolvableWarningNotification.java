package org.ginsim.core.notification.resolvable;

import org.ginsim.core.notification.WarningNotification;
import org.ginsim.core.notification.resolvable.resolution.NotificationResolution;
import org.ginsim.graph.common.Graph;

public class ResolvableWarningNotification extends WarningNotification  implements ResolvableNotification{

	
	private Object[] data;
	private Graph graph;
	private NotificationResolution resolution;
	
	public ResolvableWarningNotification( String message, Graph graph, Object[] data, NotificationResolution resolution) {
		
		super( message);
		this.data = data;
		this.graph = graph;
		this.resolution = resolution;
	}
	
	@Override
	public boolean performResolution( int index){
		
		return resolution.perform( graph, data, index);
	}
	
	@Override
	public String[] getOptionNames() {
		
		return resolution.getOptionsName();
	}
}
