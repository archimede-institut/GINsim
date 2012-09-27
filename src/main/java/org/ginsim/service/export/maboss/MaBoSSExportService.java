package org.ginsim.service.export.maboss;

import java.io.FileWriter;
import java.io.IOException;

import org.colomoto.logicalmodel.LogicalModel;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

/**
 * GINsim export service capable of encoding the working model into the MaBoSS format
 * <p>
 * It only writes the variables and 
 * </p>
 * 
 * @author Aurelien Naldi
 */
@ProviderFor(Service.class)
@Alias("maboss")
public class MaBoSSExportService implements Service {

	public void export(LogicalModel model, String filename) throws IOException {

		MaBoSSEncoder encoder = new MaBoSSEncoder(model);
		
		FileWriter writer = new FileWriter(filename);
		encoder.write(writer);
		writer.close();
		
		writer = new FileWriter(filename+".cfg");
		encoder.writeConfig(writer);
		writer.close();
	}
}
