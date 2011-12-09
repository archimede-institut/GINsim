package org.ginsim.servicegui.tool.interactionanalysis;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.ginsim.common.document.DocumentWriter;
import org.ginsim.common.document.GenericDocumentFormat;
import org.ginsim.common.utils.GUIMessageUtils;
import org.ginsim.common.utils.gui.FileFormatFilter;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.mutant.Perturbation;
import org.ginsim.core.utils.data.ObjectStore;
import org.ginsim.core.utils.log.LogManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphSelection;
import org.ginsim.gui.graph.regulatorygraph.mutant.MutantSelectionPanel;
import org.ginsim.gui.graph.view.css.ColorizerPanel;
import org.ginsim.gui.resource.Translator;
import org.ginsim.gui.shell.FileSelectionHelper;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.service.ServiceManager;
import org.ginsim.service.tool.interactionanalysis.InteractionAnalysisAlgoResult;
import org.ginsim.service.tool.interactionanalysis.InteractionAnalysisService;


public class InteractionAnalysisFrame extends StackDialog implements ActionListener {
	private JFrame frame;
	private RegulatoryGraph regGraph;
	private Container mainPanel;
	private JButton saveReportButton;
	
	private InteractionAnalysisService iaService = null;
	private InteractionAnalysisAlgoResult algoResult = null;
	private MutantSelectionPanel mutantSelectionPanel;
	private ObjectStore mutantStore;
	private ColorizerPanel colorizerPanel;
	
	private static final long serialVersionUID = -9126723853606423085L;
	private static final String OPT_REPORTDIRECTORY = "interactionAnalysis.reportDirectory";

	public InteractionAnalysisFrame( JFrame frame, RegulatoryGraph graph) {
		super(frame, "interactionAnalysis", 420, 260);
		this.frame = frame;
		this.regGraph = graph;
        initialize();
    }

	public void initialize() {
		setMainPanel(getMainPanel());
	}
	
	private Container getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new javax.swing.JPanel();
			mainPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
		
			c.gridx = 0;
			c.gridy = 0;
			c.fill = GridBagConstraints.BOTH;
			c.ipadx = 10;
			mainPanel.add(new JLabel(Translator.getString("STR_interactionAnalysis_ask")), c);
			
			
			c.gridy++;
			c.gridx = 0;
			c.ipadx = 0;
			c.ipady = 0;
		    mutantStore = new ObjectStore();
			mutantSelectionPanel = new MutantSelectionPanel(this, regGraph, mutantStore);
			mainPanel.add(mutantSelectionPanel, c);
		    
			c.gridy++;
			c.ipady = 20;
			mainPanel.add(new JLabel(""), c);

		    c.gridy++;
			c.weightx = 0;
			c.weighty = 0;
			c.ipady = 0;
		    saveReportButton = new JButton(Translator.getString("STR_interactionAnalysis_saveReport"));
		    saveReportButton.setEnabled(false);
		    mainPanel.add(saveReportButton, c);
		    saveReportButton.addActionListener(this);
		
		    c.gridy++;
			colorizerPanel = new ColorizerPanel(true, "interactionAnalysis.", regGraph);
		    mainPanel.add(colorizerPanel, c);
		}
		return mainPanel;
	}

	protected void run() {
		List<RegulatoryNode> selectedNodes = null;
		GraphSelection<RegulatoryNode, RegulatoryMultiEdge> selection = GUIManager.getInstance().getGraphGUI(regGraph).getSelection();
		if (selection == null) {
			selectedNodes = null;
		} else {
			selectedNodes = (List<RegulatoryNode>) selection.getSelectedNodes();

		}
		iaService = ServiceManager.getManager().getService( InteractionAnalysisService.class);
		algoResult = iaService.run(regGraph, (Perturbation) mutantStore.getObject(0), selectedNodes);
	    saveReportButton.setEnabled(true);
	    colorizerPanel.setNewColorizer(algoResult.getColorizer());
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == saveReportButton){
			if (algoResult == null) {
				LogManager.error("Trying to save a report without result");
				return;
			}
			try {
				GenericDocumentFormat format = GenericDocumentFormat.XHTMLDocumentFormat;
				String dest = FileSelectionHelper.selectSaveFilename(frame, new FileFormatFilter(format));
				
				//Object[] fileAndFormat = GenericDocumentFileChooser.saveDialog(OPT_REPORTDIRECTORY, this, format);
				if (dest != null) {
					DocumentWriter doc = format.factory.getDocumentWriter();
					doc.setOutput(new File(dest));
					algoResult.getReport().saveReport(doc, regGraph);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				GUIMessageUtils.openErrorDialog("An error has occured while saving", this.frame);
			}
		}
	}

	public void cancel() {
		if (!colorizerPanel.frameIsClosing()) {
				return;
		}
		super.cancel();
	}
}
