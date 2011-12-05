package org.ginsim.gui.service.common;

import java.awt.event.ActionEvent;
import java.io.IOException;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.gui.shell.FileSelectionHelper;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.gui.utils.dialog.stackdialog.HandledStackDialog;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialogHandler;
import org.ginsim.utils.log.LogManager;



public abstract class ExportAction<G extends Graph> extends BaseAction {

	protected final G graph;
	private final String id;
	
	/**
     * 
     * @param graph
     * @param name Entry to insert in the menu
     * @param tooltip Long description of the action
     */
	public ExportAction(G graph, String name, String tooltip) {
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
			LogManager.error("Error in export "+getID());
			LogManager.error(e);
		}
	}

	public StackDialogHandler getConfigPanel() {
		return null;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		StackDialogHandler handler = getConfigPanel();
		if (handler != null) {
			new HandledStackDialog(handler);
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
