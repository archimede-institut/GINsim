package org.ginsim.servicegui.tool.modelsimplifier;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.guihelpers.GUIHelper;
import org.ginsim.gui.service.common.GUIFor;
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
	public Component getPanel(ModelSimplifierConfigList reductions) {
		return getReductionPanel(reductions);
	}

	@Override
	public Component getSelectionPanel(ModelSimplifierConfigList o) {
		return null;
	}

	@Override
	public boolean supports(Object o) {
		return o instanceof ModelSimplifierConfigList;
	}

}
