package org.ginsim.gui.graph.regulatorygraph.initialstate;

import java.awt.Component;

import org.ginsim.core.graph.regulatorygraph.initialstate.GsInitialStateList;
import org.ginsim.gui.guihelpers.GUIHelper;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(GUIHelper.class)
@GUIFor(GsInitialStateList.class)
public class InitialStatesGUIHelper implements GUIHelper {

	@Override
	public Component getPanel(Object o, StackDialog dialog) {
		if ( o == null || !(o instanceof GsInitialStateList)) {
			throw new RuntimeException("Can only edit initial states");
		}
		
		InitialStatePanel panel = new InitialStatePanel( (GsInitialStateList)o, false);
    	return panel;

	}

	@Override
	public Component getSelectionPanel(Object o, StackDialog dialog) {
		return null;
	}

	@Override
	public boolean supports(Object o) {
		return o instanceof GsInitialStateList;
	}

}
