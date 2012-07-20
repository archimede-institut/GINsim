package org.ginsim.servicegui.tool.css;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.view.css.AllSelector;
import org.ginsim.core.graph.view.css.CSSSyntaxException;
import org.ginsim.core.graph.view.css.CascadingStyleSheet;
import org.ginsim.core.graph.view.css.Selector;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.gui.service.common.ToolkitAction;
import org.ginsim.service.tool.connectivity.ConnectivityService;
import org.mangosdk.spi.ProviderFor;


/**
 * register the connectivity service
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(ConnectivityService.class)
@ServiceStatus( ServiceStatus.TOOLKIT)
public class CSSServiceGUI extends AbstractServiceGUI {
	
	static {
		Selector.registerSelector(AllSelector.IDENTIFIER, AllSelector.class);
	}

	private int initialWeight = W_TOOLKITS_MAIN + 30;

	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		actions.add( new CSSAction( graph, this));
		return actions;
	}

	@Override
	public int getInitialWeight() {
		return initialWeight;
	}
}

class CSSAction extends ToolkitAction {
	
	private Graph graph;
	private CSSFrame cssFrame;

	protected CSSAction( Graph graph, ServiceGUI serviceGUI) {
		super("STR_css", "STR_css_descr", serviceGUI);
		this.graph = graph; 
	}
	
	@Override
	public void actionPerformed( ActionEvent arg0) {
		if (cssFrame == null) {
			cssFrame = new CSSFrame(graph);
		}
		cssFrame.display();
	}
}
