package fr.univmrs.ibdm.GINsim.export;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.initialState.GsInitialStatePanel;
import fr.univmrs.tagc.widgets.StackDialog;

public class GsExportDialog extends StackDialog {
	private static final long serialVersionUID = -6796117147061825176L;

	GsAbstractExport export;
	GsExportConfig config;
	GsInitialStatePanel initPanel = null;
	GsGraph graph;
	
	public GsExportDialog(GsAbstractExport export, GsExportConfig config) {
		super(config.getGraph().getGraphManager().getMainFrame(), "exportDialog_"+export.getID(), 400, 300);
		this.export = export;
		this.config = config;
		if (export.getSubFormat() != null) {
			setMainPanel(getMainPanel());
		} else {
			setMainPanel(export.getConfigPanel(config, this));
		}
	}

	protected void run() {
		setVisible(false);
		try {
			export.selectFile(config);
		} catch (GsException e) {
			GsEnv.error(e, null);
		}
		dispose();
	}
	
	private JPanel getMainPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		panel.add(getPNPanel(config), c);
		
		// TODO: add specific panel in the middle
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		panel.add(export.getConfigPanel(config, this), c);
		
		return panel;
	}
	
    private JPanel getPNPanel(GsExportConfig config) {
    	JPanel panel = new JPanel();
    	panel.setLayout(new GridBagLayout());
    	GridBagConstraints c = new GridBagConstraints();
    	c.gridx = 0;
    	c.gridy = 0;
    	panel.add(new JLabel(Translator.getString("STR_format")), c);
    	c = new GridBagConstraints();
    	c.gridx = 1;
    	c.gridy = 0;
    	JComboBox combo = new JComboBox(new PNComboBox(config, export));
    	panel.add(combo, c);
    	return panel;
    }
}


class PNComboBox extends DefaultComboBoxModel {
	private static final long serialVersionUID = 1353355880383099564L;
	
	GsExportConfig config;
	GsAbstractExport export;
	
	protected PNComboBox(GsExportConfig cfg, GsAbstractExport export) {
		super(export.getSubFormat());
		this.config = cfg;
		this.export = export;
	}
	public Object getSelectedItem() {
		if (config == null) {
			return null;
		}
		return getElementAt(config.format);
	}
	public void setSelectedItem(Object anObject) {
		if (config == null) {
			return;
		}
		
		config.setFormat(getIndexOf(anObject), export);
	}
}
