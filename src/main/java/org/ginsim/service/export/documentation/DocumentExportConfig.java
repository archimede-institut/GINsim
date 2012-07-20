package org.ginsim.service.export.documentation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ginsim.common.application.OptionStore;
import org.ginsim.common.document.GenericDocumentFormat;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialStateStore;

public class DocumentExportConfig implements InitialStateStore {

    public final Map m_init = new HashMap();
    public final Map m_input = new HashMap();

	public boolean exportInitStates = OptionStore.getOption("export.doc.init",  true);
	public boolean exportMutants = OptionStore.getOption("export.doc.perturbations",  true);;
	public boolean searchStableStates = OptionStore.getOption("export.doc.stable",  true);;
	public boolean putComment = OptionStore.getOption("export.doc.comment",  true);;
	
	public GenericDocumentFormat format = getSubFormat().get(0);
	
	// set to true to avoid generating redundant things for multicellular models
	public boolean multicellular = false;
	
	@Override
    public Map getInitialState() {
        return m_init;
    }
	@Override
    public Map getInputState() {
        return m_input;
    }
	
    /**
     * get a vector of all the GenericDocumentFormat the genericDocument can use.
     */
	public List<GenericDocumentFormat> getSubFormat() {
		return GenericDocumentFormat.getAllFormats();
	}
	
	/**
	 * Send the selected options to the OptionStore
	 */
	public void saveDefaults() {
		OptionStore.setOption("export.doc.init", exportInitStates);
		OptionStore.setOption("export.doc.perturbations", exportMutants);
		OptionStore.setOption("export.doc.stable", searchStableStates);
		OptionStore.setOption("export.doc.comment", putComment);
	}

}
