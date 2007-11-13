package fr.univmrs.ibdm.GINsim.reg2dyn;

import fr.univmrs.tagc.datastore.gui.GenericListPanel;
import fr.univmrs.tagc.datastore.gui.GenericListSelectionPanel;
import fr.univmrs.tagc.widgets.StackDialog;

public class PrioritySelectionPanel extends GenericListSelectionPanel {
	private static final long serialVersionUID = 1213902700181873169L;

	private GenericListPanel pcpanel;
	private PriorityClassManager pcmanager;

	public PrioritySelectionPanel(StackDialog dialog, PriorityClassManager pcmanager) {
		super(dialog, pcmanager, "priority classes");
		this.pcmanager = pcmanager;
	}
	
	protected void configure() {
    	if (pcpanel == null) {
    		GsReg2dynPriorityClassConfig p = new GsReg2dynPriorityClassConfig(pcmanager.nodeOrder);
    		pcpanel = new GenericListPanel(p, null, null);
    		p.setClassPanel(pcpanel);
    		pcpanel.setList(pcmanager);
    	}
        dialog.addTempPanel(pcpanel);
	}
}
