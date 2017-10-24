package org.ginsim.servicegui.tool.reg2dyn;

import java.util.List;

import org.ginsim.common.application.Txt;
import org.ginsim.gui.utils.data.ListEditionPanel;
import org.ginsim.gui.utils.data.ListSelectionPanel;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.service.tool.reg2dyn.priorityclass.PrioritySetList;
import org.ginsim.service.tool.reg2dyn.priorityclass.UpdaterDefinitionStore;
import org.ginsim.service.tool.reg2dyn.updater.UpdaterDefinition;
import org.ginsim.servicegui.tool.reg2dyn.priorityclass.PriorityManagerHelper;


public class PrioritySelectionPanel extends ListSelectionPanel<UpdaterDefinition> {
	private static final long serialVersionUID = 1213902700181873169L;

	private ListEditionPanel pcpanel;
	private final PrioritySetList pcmanager;
    private UpdaterDefinitionStore store;

    public PrioritySelectionPanel(StackDialog dialog, PrioritySetList pcmanager) {
        super(dialog, Txt.t("STR_UpdatingMode"));
        this.pcmanager = pcmanager;
        initialize("", false);
	}

    @Override
    protected List getList() {
        return pcmanager;
    }

    @Override
    public UpdaterDefinition getSelected() {
        if (store == null) {
            return null;
        }
        return store.getUpdatingMode();
    }

    @Override
    public void setSelected(UpdaterDefinition sel) {
        if (store == null) {
            return;
        }
        store.setUpdatingMode(sel);
    }

    public void setStore(UpdaterDefinitionStore store) {
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
