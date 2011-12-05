package org.ginsim.core.notification.resolvable.resolution;

import org.ginsim.core.graph.common.Graph;

public class NotificationResolution {

	private String[] optionNames;
	
	public NotificationResolution(){
		
		optionNames = null;
	}
	
	public NotificationResolution( String[] option_names){
		
		optionNames = option_names;
	}
	
	public boolean perform( Graph graph, Object[] data, int index){
		
		return false;
	}
	
	public String[] getOptionsName(){
		
		return optionNames;
	}
}
