package org.ginsim.servicegui.export.documentation;

import org.ginsim.common.document.GenericDocumentFormat;
import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.shell.actions.ExportAction;
import org.ginsim.service.export.documentation.LRGDocumentationService;

public class LRGDocumentationAction  extends ExportAction<RegulatoryGraph> {

	private final LRGDocumentationService service;
	GenericDocumentFormat format;

    public LRGDocumentationAction(RegulatoryGraph graph, GenericDocumentFormat format, LRGDocumentationService service) {
    	super(graph, "STR_Doc_"+format.id, "STR_Doc_"+format.id+"_descr", null);
    	this.service = service;
    	this.format = format;
    }

	@Override
	protected void doExport( String filename) throws Exception {
		service.run(graph, format, filename);
	}

	@Override
	public FileFormatDescription getFileFilter() {
		return format;
	}

}
