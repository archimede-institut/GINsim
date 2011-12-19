package org.ginsim.servicegui.export.documentation;

import org.ginsim.common.document.DocumentWriter;
import org.ginsim.service.export.documentation.LRGDocumentationService;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.SimpleServiceGUI;
import org.ginsim.servicegui.common.GUIFor;
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
public class LRGDocumentationServiceGUI extends SimpleServiceGUI<LRGDocumentationService> {

	public LRGDocumentationServiceGUI() {
		super(LRGDocumentationAction.class, W_INFO+3);
	}

}
