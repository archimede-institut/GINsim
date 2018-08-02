package org.ginsim.core.service;

import org.colomoto.biolqm.LQMServiceManager;
import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.NodeInfo;
import org.colomoto.biolqm.io.InputStreamProviderFileImpl;
import org.colomoto.biolqm.io.LogicalModelFormat;
import org.colomoto.biolqm.io.OutputStreamProvider;
import org.colomoto.biolqm.io.OutputStreamProviderFileImpl;
import org.colomoto.biolqm.modifier.booleanize.BooleanizeModifier;
import org.ginsim.core.graph.regulatorygraph.LogicalModel2RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Generic service to support format implemented in LogicalModel.
 * This provides an easy way to use the core capabilities provides by LogicalModel formats.
 * A static method allows to add automatic wrappers for all available formats.
 * 
 * @author Aurelien Naldi
 */
public class FormatSupportService<F extends LogicalModelFormat> implements Service {

	private static final Set<LogicalModelFormat> knownFormats = new HashSet<LogicalModelFormat>();

	/**
	 * register a format when creating an instance of this class.
	 * 
	 * @param f
	 */
	private static void registerFormat(LogicalModelFormat f) {
		knownFormats.add(f);
	}

	/**
	 * Blacklist a format to prevent the creation of an automatic wrapper for it.
	 * 
	 * @param f
	 */
	public static void blacklistFormat(LogicalModelFormat f) {
		knownFormats.add(f);
	}

	/**
	 * Trigger the creation of automatic wrappers for all available formats.
	 * Formats that have a custom service or have been blacklisted will be skipped.
	 */
	public static void addMissingFormats() {
		for (LogicalModelFormat format: LQMServiceManager.getFormats()) {
			if (!knownFormats.contains(format)) {
				System.out.println("Should create wrapper format for: "+format);
			}
		}
	}
	
	public final F format;
	
	public FormatSupportService(F format) {
		this.format = format;
		registerFormat(format);
	}

	public String export(RegulatoryGraph graph, String filename) throws IOException {
		return export(graph.getModel(), filename);
	}

	public String export(LogicalModel model, String filename) throws IOException {
		OutputStreamProvider out = new OutputStreamProviderFileImpl(filename);
		return export(model, out);
	}
	
	public String export(LogicalModel model, OutputStreamProvider out) throws IOException {
		String message = null;
		if (!model.isBoolean() && format.getMultivaluedSupport() == LogicalModelFormat.MultivaluedSupport.BOOLEANIZED) {
			model = new BooleanizeModifier(model).getModifiedModel();
			message = "Multivalued model was converted to Boolean";
		}
		format.export(model, out);
		return message;
	}
	
	public LogicalModel importFile(File f) throws IOException {
		return format.load( new InputStreamProviderFileImpl(f));
	}
	
	public LogicalModel importFile(String filename) throws IOException {
		return importFile( new File(filename));
	}
	
	public RegulatoryGraph importLRG(String filename) throws IOException {
		LogicalModel model = importFile(filename);
		return LogicalModel2RegulatoryGraph.importModel(model);
	}
	
	public boolean canExport() {
		return format.canExport();
	}
	
	public boolean canImport() {
		return format.canImport();
	}

    public boolean canExportModel(RegulatoryGraph graph) {
        if (!canExport()) {
            return false;
        }

		LogicalModelFormat.MultivaluedSupport mvs = format.getMultivaluedSupport();
        if (mvs == LogicalModelFormat.MultivaluedSupport.BOOLEAN_STRICT) {
            // check that the model is Boolean
            for (NodeInfo ni: graph.getNodeInfos()) {
                if (ni.getMax() > 1) {
                    return false;
                }
            }
        }

        return true;
    }

}
