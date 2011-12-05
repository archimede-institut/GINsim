package org.ginsim.core.notification.resolvable;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.notification.WarningNotification;
import org.ginsim.core.notification.resolvable.resolution.NotificationResolution;

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
	
	@Override
	public boolean performResolution( int index){
		
		return resolution.perform( graph, data, index);
	}
	
	@Override
	public String[] getOptionNames() {
		
		return resolution.getOptionsName();
	}
}
