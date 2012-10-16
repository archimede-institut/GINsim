package org.ginsim.servicegui.export.cadp;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.gui.service.common.StandaloneGUI;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialogHandler;
import org.ginsim.service.export.nusmv.NuSMVConfig;
import org.ginsim.servicegui.export.nusmv.NuSMVExportConfigPanel;
import org.mangosdk.spi.ProviderFor;


@ProviderFor(ServiceGUI.class)
@StandaloneGUI
@ServiceStatus( ServiceStatus.RELEASED)
public class CADPExportServiceGUI extends AbstractServiceGUI
{

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new CADPExportAction((RegulatoryGraph) graph, this));
			return actions;
		}
		return null;
	}

	@Override
	public int getInitialWeight() {
		return W_EXPORT_SPECIFIC+40;
	}
	
	class CADPExportAction extends ExportAction<RegulatoryGraph> {
		private static final long serialVersionUID = -8586197112178912230L;

		public CADPExportAction(RegulatoryGraph graph, ServiceGUI serviceGUI) {
			super(graph, "STR_CADP", "STR_CADP_descr", serviceGUI);
		}

		@Override
		protected FileFormatDescription getFileFilter() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public StackDialogHandler getConfigPanel() {
			return null;
			// this StackDialogHandler should extend 
			// the dialog for the Composition
			
		//	config = new NuSMVConfig(graph);
		//	return new NuSMVExportConfigPanel(config, this);
		}
		
		@Override
		protected void doExport(String filename) {
			// TODO Auto-generated method stub
			
		}
	}
	
}
