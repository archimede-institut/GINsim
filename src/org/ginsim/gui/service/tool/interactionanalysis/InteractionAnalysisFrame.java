package org.ginsim.gui.service.tool.interactionanalysis;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.graph.regulatorygraph.mutant.RegulatoryMutantDef;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphSelection;
import org.ginsim.gui.graph.regulatorygraph.mutant.MutantSelectionPanel;
import org.ginsim.gui.graph.view.css.ColorizerPanel;
import org.ginsim.gui.resource.Translator;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.service.ServiceManager;
import org.ginsim.service.tool.interactionanalysis.InteractionAnalysisAlgoResult;
import org.ginsim.service.tool.interactionanalysis.InteractionAnalysisService;
import org.ginsim.utils.data.ObjectStore;


import fr.univmrs.tagc.common.document.DocumentWriter;
import fr.univmrs.tagc.common.document.GenericDocumentFileChooser;
import fr.univmrs.tagc.common.document.GenericDocumentFormat;
import fr.univmrs.tagc.common.utils.GUIMessageUtils;

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
		algoResult = iaService.run(regGraph, (RegulatoryMutantDef) mutantStore.getObject(0), selectedNodes);
	    saveReportButton.setEnabled(true);
	    colorizerPanel.setNewColorizer(algoResult.getColorizer());
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == saveReportButton){
			try {
				Vector<GenericDocumentFormat> format = new Vector<GenericDocumentFormat>(1);
				format.add(GenericDocumentFormat.XHTMLDocumentFormat);
				Object[] fileAndFormat = GenericDocumentFileChooser.saveDialog(OPT_REPORTDIRECTORY, this, format);
				if (fileAndFormat != null) {
					DocumentWriter doc = (DocumentWriter)((GenericDocumentFormat)fileAndFormat[1]).documentWriterClass.newInstance();
					doc.setOutput((File)fileAndFormat[0]);
					if (algoResult != null) algoResult.getReport().saveReport(doc, regGraph);
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
