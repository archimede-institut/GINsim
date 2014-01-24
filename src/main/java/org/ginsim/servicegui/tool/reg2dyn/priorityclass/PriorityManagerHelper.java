package org.ginsim.servicegui.tool.reg2dyn.priorityclass;

import org.ginsim.gui.utils.data.ListEditionPanel;
import org.ginsim.gui.utils.data.ListPanelCompanion;
import org.ginsim.gui.utils.data.ListPanelHelper;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClassAddMode;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClassDefinition;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClassManager;
import org.ginsim.service.tool.reg2dyn.priorityclass.Reg2dynPriorityClass;
import org.ginsim.servicegui.tool.reg2dyn.Reg2dynPriorityClassConfig;

/**
 * GUI helper for the list of priority class definitions
 *
 * @author Aurelien Naldi
 */
public class PriorityManagerHelper extends ListPanelHelper<PriorityClassDefinition, PriorityClassManager> {

    public static final String FILTER_NO_SYNCHRONOUS = "[no-synchronous]";
    public static final PriorityManagerHelper HELPER = new PriorityManagerHelper();

    public Object[] getCreateTypes() {
        return PriorityClassAddMode.values();
    }

    public int doCreate(PriorityClassManager list, Object arg) {
        return list.addDefinition(arg);
    }

    public boolean doRemove(PriorityClassManager list, int[] sel) {
        return list.removeSelection(sel);
    }

    @Override
    public ListPanelCompanion getCompanion(ListEditionPanel<PriorityClassDefinition, PriorityClassManager> editPanel) {
        Reg2dynPriorityClassConfig configPanel = new Reg2dynPriorityClassConfig(editPanel);
        return configPanel;
    }

    public boolean moveData(PriorityClassManager pcmanager, int[] sel, int diff) {
        if (!pcmanager.canMoveItems(sel)) {
            return false;
        }
        return super.moveData(pcmanager, sel, diff);
    }

    public boolean match(String filter, PriorityClassDefinition pcdef) {
        if (filter != null && filter.startsWith(FILTER_NO_SYNCHRONOUS)) {

            int l = pcdef.size();
            boolean hasSync = false;
            for (int i=0 ; i<l ; i++) {
                Reg2dynPriorityClass pc = pcdef.get(i);
                if (pc.getMode() == Reg2dynPriorityClass.SYNCHRONOUS) {
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
