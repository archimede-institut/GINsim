package org.ginsim.servicegui.export.documentation;

import org.ginsim.common.document.DocumentWriter;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.SimpleServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.service.export.documentation.LRGDocumentationService;
import org.mangosdk.spi.ProviderFor;


/**
 * GenericDocumentExport is a plugin to export the documentation of a model into multiples document format.
 * 
 * It export using a documentWriter. You can add support for your own document writer using <u>addSubFormat</u>
 * 
 * @see DocumentWriter
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(LRGDocumentationService.class)
@ServiceStatus( ServiceStatus.PUBLISHED)
public class LRGDocumentationServiceGUI extends SimpleServiceGUI<LRGDocumentationService> {

	public LRGDocumentationServiceGUI() {
		super(LRGDocumentationAction.class, W_INFO+3);
	}

}
