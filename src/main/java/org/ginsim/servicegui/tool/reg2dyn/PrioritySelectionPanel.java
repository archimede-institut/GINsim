package org.ginsim.servicegui.tool.reg2dyn;

import java.util.List;

import org.ginsim.common.application.Translator;
import org.ginsim.gui.utils.data.ListEditionPanel;
import org.ginsim.gui.utils.data.ListSelectionPanel;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClassDefinition;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClassManager;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityDefinitionStore;
import org.ginsim.servicegui.tool.reg2dyn.priorityclass.PriorityManagerHelper;


public class PrioritySelectionPanel extends ListSelectionPanel<PriorityClassDefinition> {
	private static final long serialVersionUID = 1213902700181873169L;

	private ListEditionPanel pcpanel;
	private final PriorityClassManager pcmanager;
    private PriorityDefinitionStore store;

    public PrioritySelectionPanel(StackDialog dialog, PriorityClassManager pcmanager) {
        super(dialog, Translator.getString("STR_priorityclass"));
        this.pcmanager = pcmanager;
        initialize("", false);
	}

    @Override
    protected List getList() {
        return pcmanager;
    }

    @Override
    public PriorityClassDefinition getSelected() {
        if (store == null) {
            return null;
        }
        return store.getPriorityDefinition();
    }

    @Override
    public void setSelected(PriorityClassDefinition sel) {
        if (store == null) {
            return;
        }
        store.setPriorityDefinition(sel);
    }

    public void setStore(PriorityDefinitionStore store) {
        this.store = store;
        refresh();
    }

    @Override
	public void configure() {
    	if (pcpanel == null) {
            pcpanel = PriorityManagerHelper.HELPER.getEditPanel(pcmanager,dialog);
    	}
        dialog.addTempPanel(pcpanel);
	}
}
