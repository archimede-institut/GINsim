package org.ginsim.gui.service.common;

import java.awt.event.ActionEvent;
import java.io.IOException;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.gui.FileSelectionHelper;

import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.common.Debugger;
import fr.univmrs.tagc.common.gui.dialog.stackdialog.StackDialog;
import fr.univmrs.tagc.common.gui.dialog.stackdialog.StackDialogHandler;


public abstract class GsExportAction<G extends Graph> extends BaseAction {

	protected final G graph;
	private final String id;
	
	/**
     * 
     * @param graph
     * @param name Entry to insert in the menu
     * @param tooltip Long description of the action
     */
	public GsExportAction(G graph, String name, String tooltip) {
		super(name, null, tooltip, null);
		this.graph = graph;
		String className = getClass().getName();
		this.id = className.substring(className.lastIndexOf('.')+1);
	}
	
	/**
	 * @return the identifier for this export.
	 * The identifier is based on the class name
	 * and should be used to store settings.
	 */
	public String getID() {
		return id;
	}

	/**
	 * The action was launched, show a GUI if needed, select a file and if all went fine, call the backend
	 * 
	 * @param config
	 * @throws GsException
	 */
	public void selectFile() {
		// TODO: restore file filters
		String filename = FileSelectionHelper.selectSaveFilename(null);
		if (filename == null) {
			return;
		}
		try {
			doExport(filename);
		} catch (Exception e) {
			Debugger.log("Error in export "+getID());
			Debugger.log(e);
		}
	}

	public StackDialogHandler getConfigPanel() {
		return null;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		StackDialogHandler handler = getConfigPanel();
		if (handler != null) {
			Debugger.log("exports with config panel not yet supported");
			
			// handler.setStackDialog(new StackDialog(null, "", 200, 500);
				
		} else {
			selectFile();
		}
	}

	/**
	 * Get the file filter to be used for this export.
	 * It will be called after showing the configuration panel and before selecting the file
	 * Thus, the configuration can be used to return a different filter.
	 * 
	 * @return the active file filter
	 */
	abstract protected GsFileFilter getFileFilter();
	
	/**
	 * Main export function: will be called after the target file was selected
	 */
	abstract protected void doExport(String filename) throws GsException, IOException;
}
