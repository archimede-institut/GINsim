package org.ginsim.servicegui.tool.reg2dyn.priorityclass;

import org.ginsim.gui.utils.data.ListEditionPanel;
import org.ginsim.gui.utils.data.ListPanelCompanion;
import org.ginsim.gui.utils.data.ListPanelHelper;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClass;
import org.ginsim.service.tool.reg2dyn.priorityclass.PrioritySetAddMode;
import org.ginsim.service.tool.reg2dyn.priorityclass.PrioritySetDefinition;
import org.ginsim.service.tool.reg2dyn.priorityclass.PrioritySetList;

/**
 * GUI helper for the list of priority class definitions
 *
 * @author Aurelien Naldi
 */
public class PriorityManagerHelper extends ListPanelHelper<PrioritySetDefinition, PrioritySetList> {

    public static final String FILTER_NO_SYNCHRONOUS = "[no-synchronous]";
    public static final PriorityManagerHelper HELPER = new PriorityManagerHelper();

    public Object[] getCreateTypes() {
        return PrioritySetAddMode.values();
    }

    public int doCreate(PrioritySetList list, Object arg) {
        return list.addDefinition(arg);
    }

    public boolean doRemove(PrioritySetList list, int[] sel) {
        return list.removeSelection(sel);
    }

    @Override
    public ListPanelCompanion getCompanion(ListEditionPanel<PrioritySetDefinition, PrioritySetList> editPanel) {
        PriorityDefinitionPanel configPanel = new PriorityDefinitionPanel(editPanel);
        return configPanel;
    }

    public boolean moveData(PrioritySetList pcmanager, int[] sel, int diff) {
        if (!pcmanager.canMoveItems(sel)) {
            return false;
        }
        return super.moveData(pcmanager, sel, diff);
    }

    public boolean match(String filter, PrioritySetDefinition pcdef) {
        if (filter != null && filter.startsWith(FILTER_NO_SYNCHRONOUS)) {

            int l = pcdef.size();
            boolean hasSync = false;
            for (int i=0 ; i<l ; i++) {
                PriorityClass pc = pcdef.get(i);
                if (pc.getMode() == PriorityClass.SYNCHRONOUS) {
                    hasSync = true;
                    break;
                }
            }
            if (hasSync) {
                return false;
            }
            String realfilter = filter.substring(FILTER_NO_SYNCHRONOUS.length()).trim();
            if (realfilter.length() == 0) {
                return true;
            }
            return pcdef.match(realfilter);
        }
        return pcdef.match(filter);
    }

}
