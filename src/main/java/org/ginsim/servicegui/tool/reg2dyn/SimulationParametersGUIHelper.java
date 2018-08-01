package org.ginsim.servicegui.tool.reg2dyn;

import org.ginsim.gui.guihelpers.GUIHelper;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.gui.utils.data.ListEditionPanel;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.service.tool.reg2dyn.SimulationParameterList;
import org.ginsim.service.tool.reg2dyn.SimulationParameters;
import org.kohsuke.MetaInfServices;

import java.awt.*;

/**
 * GUI helper for the simulation parameters: provide edition and selection panels
 */
@MetaInfServices(GUIHelper.class)
@GUIFor(SimulationParameterList.class)
public class SimulationParametersGUIHelper implements GUIHelper<SimulationParameterList> {

    private static final ListOfSimulationParametersHelper HELPER = ListOfSimulationParametersHelper.HELPER;

    @Override
    public Component getPanel(SimulationParameterList o, StackDialog dialog) {
        return new ListEditionPanel<SimulationParameters, SimulationParameterList>(HELPER, o, "", dialog, null);
    }

    @Override
    public Component getSelectionPanel(SimulationParameterList o, StackDialog dialog) {
        return null;
    }

    @Override
    public boolean supports(Object o) {
        return o instanceof SimulationParameterList;
    }

}
