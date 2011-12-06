package org.ginsim.service.export.documentation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ginsim.common.document.GenericDocumentFormat;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialStateStore;

public class DocumentExportConfig implements InitialStateStore {

    public final Map m_init = new HashMap();
    public final Map m_input = new HashMap();

	public boolean exportInitStates = true;
	public boolean exportMutants = true;
	public boolean searchStableStates = true;
	public boolean putComment = true;
	
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

}
