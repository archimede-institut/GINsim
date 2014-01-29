package org.ginsim.servicegui.tool.modelsimplifier;

import org.ginsim.gui.guihelpers.GUIHelper;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.service.tool.modelsimplifier.ModelSimplifierConfigList;
import org.mangosdk.spi.ProviderFor;

import java.awt.*;

@ProviderFor(GUIHelper.class)
@GUIFor(ModelSimplifierConfigList.class)
public class ReductionGUIHelper implements GUIHelper<ModelSimplifierConfigList> {

	public static ReductionConfigurationPanel getReductionPanel(ModelSimplifierConfigList reductions) {
        return new ReductionConfigurationPanel(reductions);
	}

	@Override
	public Component getPanel(ModelSimplifierConfigList reductions, StackDialog dialog) {
		return getReductionPanel(reductions);
	}

	@Override
	public Component getSelectionPanel(ModelSimplifierConfigList o, StackDialog dialog) {
		return null;
	}

	@Override
	public boolean supports(Object o) {
		return o instanceof ModelSimplifierConfigList;
	}

}
