package org.ginsim.servicegui.tool.reg2dyn;

import org.ginsim.gui.utils.data.ListEditionPanel;
import org.ginsim.gui.utils.data.ListPanelCompanion;
import org.ginsim.gui.utils.data.ListPanelHelper;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.service.tool.reg2dyn.SimulationParameterList;
import org.ginsim.service.tool.reg2dyn.SimulationParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper for displaying a list of simulation parameters.
 */
public class ListOfSimulationParametersHelper extends ListPanelHelper<SimulationParameters, SimulationParameterList> {

    public static final ListOfSimulationParametersHelper HELPER = new ListOfSimulationParametersHelper();

    private ListOfSimulationParametersHelper() {
    }

    @Override
    public int doCreate(SimulationParameterList list, Object arg) {
        SimulationParameters p = list.add();
        return list.indexOf(p);
    }

    @Override
    public boolean doRemove(SimulationParameterList list, int[] sel) {
        List<SimulationParameters> l = new ArrayList<SimulationParameters>();
        for (int idx: sel) {
            l.add(list.get(idx));
        }
        list.removeAll(l);
        return true;
    }

    @Override
    public ListPanelCompanion getCompanion(ListEditionPanel<SimulationParameters, SimulationParameterList> editPanel) {
        return new SimulationParameterEditionPanel(editPanel, editPanel.getDialog());
    }
}