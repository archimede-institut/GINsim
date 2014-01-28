package org.ginsim.servicegui.tool.reg2dyn.priorityclass;

import org.ginsim.gui.utils.data.ColumnDefinition;
import org.ginsim.gui.utils.data.ListEditionPanel;
import org.ginsim.gui.utils.data.ListPanelCompanion;
import org.ginsim.gui.utils.data.ListPanelHelper;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClass;
import org.ginsim.service.tool.reg2dyn.priorityclass.PrioritySetDefinition;

/**
 * GUI helper for the list of priority classes in a PriorityDefinition
 *
 * @author Aurelien Naldi
 */
public class PriorityDefinitionHelper extends ListPanelHelper<PriorityClass, PrioritySetDefinition> {

    public static final PriorityDefinitionHelper HELPER = new PriorityDefinitionHelper();

    private static final ColumnDefinition[] COLUMNS = new ColumnDefinition[] {
            new ColumnDefinition("Rank", String.class, false, 30),
            new ColumnDefinition("Sync", Boolean.class, true),
            new ColumnDefinition("Name", String.class, true),
    };

    @Override
    public ListPanelCompanion getCompanion(ListEditionPanel<PriorityClass, PrioritySetDefinition> editPanel) {
        if (editPanel instanceof PriorityDefinitionPanel) {
            PriorityDefinitionPanel parent = (PriorityDefinitionPanel)editPanel;
            PriorityClassContentEditor p_edit = new PriorityClassContentEditor(parent);
            return p_edit;
        }
        return null;
    }

    @Override
    public int doCreate(PrioritySetDefinition list, Object arg) {
        return list.add();
    }

    @Override
    public boolean doRemove(PrioritySetDefinition list, int[] sel) {
        return list.remove(sel);
    }

    public boolean moveData(PrioritySetDefinition list, int[] sel, int diff) {
        return list.moveSelection(sel, diff);
    }

    public ColumnDefinition[] getColumns() {
        return COLUMNS;
    }

    public Object getValue(PrioritySetDefinition list, PriorityClass cl, int column) {
        switch (column) {
            case 0:
                return cl.rank;
            case 1:
                return cl.getMode() == PriorityClass.SYNCHRONOUS;
            case 2:
                return cl.getName();
        }
        return cl;
    }

    public boolean setValue(PrioritySetDefinition list, int row, int column, Object value) {
        switch (column) {
            case 1:
                if (value instanceof Boolean) {
                    PriorityClass cl = list.get(row);
                    if ((Boolean)value) {
                        cl.setMode(PriorityClass.SYNCHRONOUS);
                    } else {
                        cl.setMode(PriorityClass.ASYNCHRONOUS);
                    }
                    return true;
                }
                return false;
            case 2:
                return list.rename(row, value.toString());
        }
        return false;
    }
}
