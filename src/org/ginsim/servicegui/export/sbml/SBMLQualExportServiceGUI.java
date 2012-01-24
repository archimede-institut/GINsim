package org.ginsim.servicegui.export.sbml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.common.utils.GUIMessageUtils;
import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialogHandler;
import org.ginsim.service.export.sbml.SBMLQualConfig;
import org.ginsim.service.export.sbml.SBMLQualExportService;
import org.mangosdk.spi.ProviderFor;

@ProviderFor( ServiceGUI.class)
@GUIFor( SBMLQualExportService.class)
@ServiceStatus( ServiceStatus.PUBLISHED)
public class SBMLQualExportServiceGUI implements ServiceGUI {

	public static final FileFormatDescription FORMAT = new FileFormatDescription("SBML-qual", "sbml");
	
	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new SBMLQualExportAction( (RegulatoryGraph) graph));
			return actions;
		}
		return null;
	}
	
	@Override
	public int getWeight() {
		return W_ANALYSIS + 2;
	}
}

class SBMLQualExportAction extends ExportAction<RegulatoryGraph> {
	
	SBMLQualConfig config;
	
	public SBMLQualExportAction(RegulatoryGraph graph) {
		
		super( graph, "STR_SBML_L3", "STR_SBML_L3_descr");
	}
	
	@Override
	public FileFormatDescription getFileFilter() {
		return SBMLQualExportServiceGUI.FORMAT;
	}
	
	protected void doExport( String filename) {
		// call the selected export method to do the job
		try {
			SBMLQualExportService service = ServiceManager.getManager().getService( SBMLQualExportService.class);
			service.run( this.graph, config, filename);
			
		} catch (IOException e) {
			LogManager.error( "Unable to export graph to SBML Format");
			LogManager.error(e);
			GUIMessageUtils.openErrorDialog( "STR_SBMLQual_UnableToExecuteExport");
		}
	}
	
	@Override
	public StackDialogHandler getConfigPanel() {
		
		config = new SBMLQualConfig(graph);
		return new SBMLQualExportConfigPanel(config, this);
	}
	
}
