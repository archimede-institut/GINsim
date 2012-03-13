package org.ginsim.servicegui.imports.truthtable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.exception.GsException;
import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.common.utils.GUIMessageUtils;
import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ImportAction;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.service.imports.truthtable.TruthTableImportService;
import org.ginsim.service.layout.LayoutService;
import org.mangosdk.spi.ProviderFor;

/**
 * GUI Action to import a Truth Table model description
 * 
 * @author Pedro T. Monteiro
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(TruthTableImportService.class)
@ServiceStatus( ServiceStatus.RELEASED)
public class TruthTableImportServiceGUI extends AbstractServiceGUI {

	public static final FileFormatDescription FORMAT = new FileFormatDescription("TruthTable", "tt");

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new TruthTableImportAction());
		return actions;
	}
	
	@Override
	public int getInitialWeight() {
		return 2;
	}
}

/**
 * Import to TruthTable file Action
 * 
 * @author Pedro T. Monteiro
 */
class TruthTableImportAction extends ImportAction {

	private static final long serialVersionUID = 2590387719278822097L;

	public TruthTableImportAction() {
		super("STR_TruthTable", "STR_TruthTable_descr");
	}

	@Override
	public FileFormatDescription getFileFilter() {
		return TruthTableImportServiceGUI.FORMAT;
	}

	@Override
	protected void doImport(String filename) {
		if (filename == null) {
			return;
		}

		TruthTableImportService service = ServiceManager.getManager()
				.getService(TruthTableImportService.class);
		if (service == null) {
			LogManager
					.error("TruthTableImportService service is not available");
			return;
		}

		try {
			RegulatoryGraph newGraph = service.run(filename);
			GUIManager.getInstance().newFrame(newGraph);
			LayoutService.runLayout(LayoutService.RING, newGraph);
		} catch (IOException e) {
			GUIMessageUtils.openErrorDialog(e, null);
			LogManager.error(e);
		} catch (GsException e) {
			GUIMessageUtils.openErrorDialog(e, null);
			LogManager.error(e);
		}
	}
}