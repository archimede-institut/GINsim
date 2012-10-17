package org.ginsim.service.export.petrinet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.colomoto.logicalmodel.LogicalModel;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(Service.class)
@Alias("PN")
public class PetrinetExportService implements Service {

	public static final List<PNFormat> FORMATS = new ArrayList<PNFormat>();
	
	static {
		// load available PN formats
        Iterator<PNFormat> formats = ServiceLoader.load( PNFormat.class).iterator(); 
        while (formats.hasNext()) {
            try {
            	PNFormat fmt = formats.next();
            	if( fmt != null) {
            		FORMATS.add(fmt);
            	}
            }
            catch (ServiceConfigurationError e){
            }
        }
	}
	
	public List<PNFormat> getAvailableFormats() {
		return FORMATS;
	}
	
	public void export(LogicalModel model, PNFormat format, PNConfig config, String filename) throws IOException {
		format.getWriter( model).export( config, filename);
	}
	
}
