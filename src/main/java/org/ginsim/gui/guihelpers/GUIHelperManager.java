package org.ginsim.gui.guihelpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.colomoto.biolqm.service.ExtensionLoader;
import org.ginsim.common.application.LogManager;
import org.ginsim.gui.service.GUIFor;

public class GUIHelperManager {

	private static final GUIHelperManager instance = new GUIHelperManager();
	
	public static GUIHelperManager getInstance() {
		return instance;
	}
	
	private List<GUIHelper> helpers = new ArrayList<GUIHelper>();
	private Map<Class<?>, GUIHelper> classHelpers = new HashMap<Class<?>, GUIHelper>();
	
	private GUIHelperManager() {
		
        for (GUIHelper helper : ExtensionLoader.load_instances(GUIHelper.class)) {
        	GUIFor guifor = helper.getClass().getAnnotation(GUIFor.class);
        	if (guifor != null) {
        		Class<?> cl = guifor.value();
        		if (classHelpers.containsKey(cl)) {
        			LogManager.debug("Duplicate GUIHelper for class: "+cl);
        			continue;
        		}
        		classHelpers.put(cl, helper);
        	} else {
        		helpers.add(helper);
        	}
        }
	}
	
	public GUIHelper getHelper(Object o) {
		
		GUIHelper helper = classHelpers.get(o.getClass());
		if (helper != null) {
			return helper;
		}
		
		for (GUIHelper h: helpers) {
			if (h.supports(o)) {
				return h;
			}
		}
		return null;
	}
}
