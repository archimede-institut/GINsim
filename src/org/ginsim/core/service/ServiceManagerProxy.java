package org.ginsim.core.service;

import org.ginsim.common.utils.log.LogManager;

/**
 * Simple proxy for the service manager in script mode.
 * 
 * @author Aurelien Naldi
 */
public class ServiceManagerProxy {

	private ServiceManager srvManager = ServiceManager.getManager();
	
	public Service stable 			= load("org.ginsim.service.tool.stablestates.StableStatesService");
	public Service reduction 		= load("org.ginsim.service.tool.modelsimplifier.ModelSimplifierService");
	public Service documentation 	= load("org.ginsim.service.export.documentation.LRGDocumentationService");
	
	
	private Service load(String s_class) {
		Service ret = null;
		try {
			Class cl = ClassLoader.getSystemClassLoader().loadClass(s_class);
			ret = srvManager.getService(cl);
		} catch (Exception e) {
			LogManager.error("Could not find service for class: "+s_class);
		}
		return ret;
	}
	
	public <S extends Service> S get(Class<S> cl) {
		return srvManager.getService(cl);
	}

}
