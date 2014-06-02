package org.ginsim.servicegui.tool.modelreduction;

import org.ginsim.gui.guihelpers.GUIHelper;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.service.tool.modelreduction.ListOfReductionConfigs;
import org.mangosdk.spi.ProviderFor;

import java.awt.*;

@ProviderFor(GUIHelper.class)
@GUIFor(ListOfReductionConfigs.class)
public class ReductionGUIHelper implements GUIHelper<ListOfReductionConfigs> {

	public static ReductionConfigurationPanel getReductionPanel(ListOfReductionConfigs reductions) {
        return new ReductionConfigurationPanel(reductions);
	}

	@Override
	public Component getPanel(ListOfReductionConfigs reductions, StackDialog dialog) {
		return getReductionPanel(reductions);
	}

	@Override
	public Component getSelectionPanel(ListOfReductionConfigs o, StackDialog dialog) {
		return null;
	}

	@Override
	public boolean supports(Object o) {
		return o instanceof ListOfReductionConfigs;
	}

}
