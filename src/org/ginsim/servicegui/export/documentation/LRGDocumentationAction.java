package org.ginsim.servicegui.export.documentation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.graph.regulatorygraph.initialstate.InitialStatePanel;
import org.ginsim.gui.shell.GsFileFilter;
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
	protected GsFileFilter getFileFilter() {
		return config.format.ffilter;
	}
}

class GDExportConfigPanel extends AbstractStackDialogHandler {
    private static final long serialVersionUID = 9043565812912568136L;
    
    protected final DocumentExportConfig cfg;
    private final LRGDocumentationAction action;
	private final RegulatoryGraph graph;

	JCheckBox cb_stable, cb_init, cb_mutants, cb_multicellular, cb_comment;

    
	protected GDExportConfigPanel ( RegulatoryGraph graph, DocumentExportConfig config, LRGDocumentationAction action) {
		this.cfg = config;
		this.graph = graph;
		this.action = action;
	}
	
	@Override
	public void run() {
		action.selectFile();
	}
	
	@Override
	protected void init() {
    	InitialStatePanel initPanel = new InitialStatePanel(stack, graph, false);
    	initPanel.setParam(cfg);

    	setLayout(new GridBagLayout());
    	GridBagConstraints c = new GridBagConstraints();
        c.gridx = c.gridy = 0;
        c.weightx = c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        add(initPanel, c);
        
        c.weightx = c.weighty = 0;
        ChangeListener listener = new MyListener();

        cb_stable = new JCheckBox("stable");
        cb_stable.addChangeListener(listener);
        cb_stable.setSelected(cfg.searchStableStates);
        c.gridy++;
        add(cb_stable, c);
        cb_init = new JCheckBox("initial states");
        cb_init.addChangeListener(listener);
        cb_init.setSelected(cfg.exportInitStates);
        c.gridy++;
        add(cb_init, c);
        cb_mutants = new JCheckBox("mutants");
        cb_mutants.addChangeListener(listener);
        cb_mutants.setSelected(cfg.exportMutants);
        c.gridy++;
        add(cb_mutants, c);
        cb_multicellular = new JCheckBox("multicellular");
        cb_multicellular.addChangeListener(listener);
        cb_multicellular.setSelected(cfg.multicellular);
        c.gridy++;
        add(cb_multicellular, c);
        cb_comment = new JCheckBox("comments");
        cb_comment.addChangeListener(listener);
        cb_comment.setSelected(cfg.putComment);
        c.gridy++;
        add(cb_comment, c);
    }
	class MyListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JCheckBox src = (JCheckBox)e.getSource();
            if (src == cb_stable) {
                cfg.searchStableStates = src.isSelected();
            } else if (src == cb_comment) {
                cfg.putComment = src.isSelected();
            } else if (src == cb_multicellular) {
                cfg.multicellular = src.isSelected();
            } else if (src == cb_init) {
                cfg.exportInitStates = src.isSelected();
            } else if (src == cb_mutants) {
                cfg.exportMutants = src.isSelected();
            }
        }
	}
}
