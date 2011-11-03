package org.ginsim.service;

import org.ginsim.graph.Graph;

/**
 * This interface is the central interface for GINsim services.
 * GINsim services are separated in four kind of services, all of them represented by distinct abstract classes
 * implementing this interface:
 *  - Import : those services correspond to data import from file of various format. They are represented by the
 *  		   GsImportService class
 *  - Export : those services correspond to data export to file of various format. They are represented by the
 *  		   GsExportService class
 *  - Layout : those services correspond to graph layout. They are represented by the
 *  		   GsLayoutService class
 *  - Action : those services correspond to various data management algorithm. They are represented by the
 *  		   GsActionService class
 *  
 *  Each new Service must extend one of these four abstract class and declare the annotation "@ProviderFor(GsService.class)"
 * 
 * @author Lionel Spinelli
 *
 */

public interface GsService {

	/**
	 * Declare the graph class the service is providing services to
	 * 
	 * @return the served graph class
	 */
	public Class<Graph<?,?>> getServedGraphClass();
	
}
