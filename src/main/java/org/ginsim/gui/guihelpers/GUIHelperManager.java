package org.ginsim.gui.guihelpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.colomoto.logicalmodel.services.ExtensionLoader;
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
		
        Iterator<GUIHelper> it_helpers = ExtensionLoader.iterator(GUIHelper.class);
        while (it_helpers.hasNext()) {
            try {
            	GUIHelper helper = it_helpers.next();
            	if (helper == null) {
            		continue;
            	}
            	
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
            catch (ServiceConfigurationError e){

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
