package org.ginsim.service.imports.truthtable;

import java.io.FileReader;
import java.io.IOException;

import org.ginsim.common.exception.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

/**
 * GINsim import service for truth tables text files.
 * <p>
 * The truth table file MUST be complete, i.e., it must contain all entries from
 * the table. These entries do not have to be sorted, the reader will always
 * sort them.
 * </p>
 * 
 * @author Pedro T. Monteiro
 */
@ProviderFor(Service.class)
@Alias("truthtable")
public class TruthTableImportService implements Service {
	public RegulatoryGraph run(String filename) throws IOException, GsException {

		FileReader fReader = new FileReader(filename);
		TruthTableParser ttParser = new TruthTableParser(fReader);
		RegulatoryGraph graph = ttParser.buildCompactLRG();
//		RegulatoryGraph graph = ttParser.buildNonCompactLRG();
		
		fReader.close();
		return graph;
	}
}
