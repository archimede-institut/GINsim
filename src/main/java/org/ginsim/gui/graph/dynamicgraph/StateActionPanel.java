package org.ginsim.gui.graph.dynamicgraph;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.ginsim.gui.shell.FileSelectionHelper;
import org.ginsim.gui.shell.actions.BaseAction;

public class StateActionPanel extends JPanel {

	Action exportAction;
	
	public StateActionPanel(DynamicItemModel tableModel) {
		super(new GridBagLayout());
		
		exportAction = new ExportAction(tableModel);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
		add( new JButton(exportAction), c);
	}
}

class ExportAction extends BaseAction {
	
	StateTableModel model;

	public ExportAction(DynamicItemModel tableModel) {
		super("Export", null, "Export as CSV", null, null);
		this.model = tableModel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String filename = FileSelectionHelper.selectSaveFilename( null, new String[]{"tsv"}, "Tab Separated Value files");
		if (filename == null) {
			return;
		}
		
		try {
			Writer w = new FileWriter(filename);
			int nr = model.getRowCount();
			int nc = model.getComponentCount();
			for (int idx=0 ; idx<nc ; idx++) {
				if (idx > 0) {
					w.write("\t");
				}
				w.write(model.getComponentName(idx));
			}
			w.write("\n");
			
			for (int r=0 ; r<nr ; r++) {
				byte[] state = model.getState(r);
				boolean isFirst = true;
				for (byte b: state) {
					if (isFirst) {
						isFirst = false;
					} else {
						w.write("\t");
					}
					w.write(""+b);
				}
				w.write("\n");
			}
			w.close();
		} catch (IOException ex) {
			// TODO: handle errors
		}
	}
}
