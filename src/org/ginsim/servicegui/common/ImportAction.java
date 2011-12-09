package org.ginsim.servicegui.common;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.common.utils.gui.FileFormatFilter;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.shell.FileSelectionHelper;
import org.ginsim.gui.utils.widgets.Frame;


public abstract class ImportAction extends BaseAction {

	protected final Graph<?,?> graph;
	
	public ImportAction(String name, Graph<?,?> graph) {
		this(graph, name, null, null, null);
	}

	abstract public FileFormatDescription getFormat();
	
	abstract public void doImport( String filename);
	
	@Override
	public void actionPerformed(ActionEvent e) {
		FileFormatDescription format = getFormat();
		Frame frame = GUIManager.getInstance().getFrame( graph);
		
		// we should add a better way to select a file for import
		String filename = FileSelectionHelper.selectOpenFilename( frame, new FileFormatFilter(getFormat()));
		if (filename == null) {
			return;
		}

		doImport(filename);
	}
	
	/**
     * 
     * @param name Entry to insert in the menu
     * @param tooltip Long description of the action
     */
	public ImportAction(Graph<?,?> graph, String name, String tooltip) {
		
		this(graph, name, null, tooltip, null);
	}
	
	/**
     * 
     * @param name Entry to insert in the menu
     * @param icon icon image for menu and toolbar
     * @param tooltip Long description of the action
     * @param accelerator the keyboard bytecut
     */
	private ImportAction(Graph<?,?> graph, String name, ImageIcon icon, String tooltip, KeyStroke accelerator) {
		
		super(name, icon, tooltip, accelerator, null);
		this.graph = graph;
	}
}
