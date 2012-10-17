package org.ginsim.service.export.boolsim;

import java.io.FileOutputStream;
import java.io.IOException;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.io.boolsim.BoolSimFormat;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

/**
 * GINsim export service for the boolsim format.
 * 
 * @author Aurelien Naldi
 */
@ProviderFor(Service.class)
@Alias("boolsim")
public class BoolsimExportService implements Service {

	private final BoolSimFormat support = new BoolSimFormat();
	
	public void export(LogicalModel model, String filename) throws IOException {

		FileOutputStream out = new FileOutputStream(filename);
		support.export(model, out);
		out.close();
	}
}
