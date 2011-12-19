package org.ginsim.servicegui.export.documentation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSeparator;

import org.ginsim.common.document.GenericDocumentFormat;
import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.utils.data.ValueList;
import org.ginsim.gui.graph.regulatorygraph.initialstate.InitialStatePanel;
import org.ginsim.gui.utils.data.models.ValueListComboModel;
import org.ginsim.gui.utils.dialog.stackdialog.AbstractStackDialogHandler;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialogHandler;
import org.ginsim.service.export.documentation.DocumentExportConfig;
import org.ginsim.service.export.documentation.LRGDocumentationService;
import org.ginsim.servicegui.common.ExportAction;

public class LRGDocumentationAction  extends ExportAction<RegulatoryGraph> {

	private final LRGDocumentationService service;
	private DocumentExportConfig config;

    public LRGDocumentationAction(RegulatoryGraph graph, LRGDocumentationService service) {
    	super(graph, "STR_Generic", "STR_Generic_descr");
    	this.service = service;
    }

	public StackDialogHandler getConfigPanel() {
		config = new DocumentExportConfig();
		return new GDExportConfigPanel( graph, config, this);
	}

	@Override
	protected void doExport( String filename) throws IOException {
		service.run(graph, config, filename);
	}

	@Override
	public FileFormatDescription getFileFilter() {
		return config.format;
	}
}

class GDExportConfigPanel extends AbstractStackDialogHandler {
    private static final long serialVersionUID = 9043565812912568136L;
    
    protected final DocumentExportConfig cfg;
    private final LRGDocumentationAction action;
	private final RegulatoryGraph graph;

	JCheckBox cb_stable, cb_init, cb_mutants, cb_multicellular, cb_comment;

	ValueList<GenericDocumentFormat> format;
    
	protected GDExportConfigPanel ( RegulatoryGraph graph, DocumentExportConfig config, LRGDocumentationAction action) {
		this.cfg = config;
		this.graph = graph;
		this.action = action;
		format = new ValueList<GenericDocumentFormat>(config.getSubFormat());
	}
	
	@Override
	public void run() {
		// read the current status
        cfg.searchStableStates = cb_stable.isSelected();
        cfg.putComment = cb_comment.isSelected();
        cfg.multicellular = cb_multicellular.isSelected();
        cfg.exportInitStates = cb_init.isSelected();
        cfg.exportMutants = cb_mutants.isSelected();

        cfg.format = format.get(format.getSelectedIndex());
        
		action.selectFile();
	}
	
	@Override
	protected void init() {
    	InitialStatePanel initPanel = new InitialStatePanel(stack, graph, false);
    	initPanel.setParam(cfg);

    	setLayout(new GridBagLayout());
    	GridBagConstraints c = new GridBagConstraints();
        c.gridx = c.gridy = 0;
        add( new JLabel("Format"), c);
    	
        c.gridx++;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        JComboBox combo_format = new JComboBox(new ValueListComboModel(format));
        add(combo_format, c);
        
        c.gridwidth = 2;
        c.gridy++;
        c.gridx = 0;
        add(new JSeparator(), c);
        
        c.gridy++;
        c.weightx = c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        add(initPanel, c);
        
        c.weightx = c.weighty = 0;

        cb_stable = new JCheckBox("stable");
        cb_stable.setSelected(cfg.searchStableStates);
        c.gridy++;
        add(cb_stable, c);
        cb_init = new JCheckBox("initial states");
        cb_init.setSelected(cfg.exportInitStates);
        c.gridy++;
        add(cb_init, c);
        cb_mutants = new JCheckBox("mutants");
        cb_mutants.setSelected(cfg.exportMutants);
        c.gridy++;
        add(cb_mutants, c);
        cb_multicellular = new JCheckBox("multicellular");
        cb_multicellular.setSelected(cfg.multicellular);
        c.gridy++;
        add(cb_multicellular, c);
        cb_comment = new JCheckBox("comments");
        cb_comment.setSelected(cfg.putComment);
        c.gridy++;
        add(cb_comment, c);
    }
	
}
