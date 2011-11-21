package org.ginsim.gui.service.tools.reg2dyn;

import java.util.HashMap;
import java.util.Map;

import fr.univmrs.tagc.common.datastore.gui.GenericListPanel;
import fr.univmrs.tagc.common.datastore.gui.GenericListSelectionPanel;
import fr.univmrs.tagc.common.gui.dialog.stackdialog.StackDialog;
import fr.univmrs.tagc.common.managerresources.Translator;

public class PrioritySelectionPanel extends GenericListSelectionPanel {
	private static final long serialVersionUID = 1213902700181873169L;

	private GenericListPanel pcpanel;
	private PriorityClassManager pcmanager;

	public PrioritySelectionPanel(StackDialog dialog, PriorityClassManager pcmanager) {
		super(dialog, pcmanager, Translator.getString("STR_priorityclass"), false, Translator.getString("STR_configure_priorities"));
		this.pcmanager = pcmanager;
	}
	
	protected void configure() {
    	if (pcpanel == null) {
    		Reg2dynPriorityClassConfig p = new Reg2dynPriorityClassConfig(pcmanager.nodeOrder);
    		Map m = new HashMap();
    		m.put(PriorityClassDefinition.class, p);
    		pcpanel = new GenericListPanel(m, "pclassList");
    		p.setClassPanel(pcpanel);
    		pcpanel.setStartIndex(2);
    		pcpanel.setList(pcmanager);
    	}
        dialog.addTempPanel(pcpanel);
	}
}
