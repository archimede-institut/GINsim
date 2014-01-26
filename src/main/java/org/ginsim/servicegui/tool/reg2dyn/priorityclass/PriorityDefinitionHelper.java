package org.ginsim.servicegui.tool.reg2dyn.priorityclass;

import org.ginsim.gui.utils.data.ListEditionPanel;
import org.ginsim.gui.utils.data.ListPanelCompanion;
import org.ginsim.gui.utils.data.ListPanelHelper;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClassDefinition;
import org.ginsim.service.tool.reg2dyn.priorityclass.Reg2dynPriorityClass;

/**
 * GUI helper for the list of priority classes in a PriorityDefinition
 *
 * @author Aurelien Naldi
 */
public class PriorityDefinitionHelper extends ListPanelHelper<Reg2dynPriorityClass, PriorityClassDefinition> {

    public static final PriorityDefinitionHelper HELPER = new PriorityDefinitionHelper();

    @Override
    public ListPanelCompanion getCompanion(ListEditionPanel<Reg2dynPriorityClass, PriorityClassDefinition> editPanel) {
        if (editPanel instanceof PriorityDefinitionPanel) {
            PriorityDefinitionPanel parent = (PriorityDefinitionPanel)editPanel;
            PriorityClassContentEditor p_edit = new PriorityClassContentEditor(parent);
            return p_edit;
        }
        return null;
    }

    @Override
    public int doCreate(PriorityClassDefinition list, Object arg) {
        return list.add();
    }

    @Override
    public boolean doRemove(PriorityClassDefinition list, int[] sel) {
        return list.remove(sel);
    }

    public boolean moveData(PriorityClassDefinition list, int[] sel, int diff) {
        return list.moveSelection(sel, diff);
    }

}
