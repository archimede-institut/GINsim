package org.ginsim.core.service;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.NodeInfo;
import org.colomoto.biolqm.io.LogicalModelFormat;
import org.colomoto.biolqm.io.OutputStreamProvider;
import org.colomoto.biolqm.services.ServiceManager;
import org.colomoto.biolqm.modifier.booleanize.Booleanizer;
import org.ginsim.core.graph.regulatorygraph.LogicalModel2RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;

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
		ServiceManager manager = ServiceManager.getManager();
		for (LogicalModelFormat format: manager.getFormats()) {
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

	public void export(RegulatoryGraph graph, String filename) throws IOException {
		export(graph.getModel(), filename);
	}

	public void export(LogicalModel model, String filename) throws IOException {
		OutputStreamProvider out = new OutputStreamProvider(filename);
		export(model, out);
	}
	
	public void export(LogicalModel model, OutputStreamProvider out) throws IOException {
		if (!model.isBoolean() && format.getMultivaluedSupport() == LogicalModelFormat.MultivaluedSupport.BOOLEANIZED) {
			model = Booleanizer.booleanize(model);
		}
		format.export(model, out);
	}
	
	public LogicalModel importFile(File f) throws IOException {
		return format.importFile(f);
	}
	
	public LogicalModel importFile(String filename) throws IOException {
		return format.importFile(new File(filename));
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
