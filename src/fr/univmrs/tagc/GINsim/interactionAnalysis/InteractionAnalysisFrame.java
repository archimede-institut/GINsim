package fr.univmrs.tagc.GINsim.interactionAnalysis;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.MutantSelectionPanel;
import fr.univmrs.tagc.common.OptionStore;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.datastore.ObjectStore;
import fr.univmrs.tagc.common.document.DocumentWriter;
import fr.univmrs.tagc.common.document.GenericDocumentFileChooser;
import fr.univmrs.tagc.common.document.GenericDocumentFormat;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.widgets.StackDialog;

public class InteractionAnalysisFrame extends StackDialog implements ActionListener {
	private JFrame frame;
	private Graph graph;
	private Container mainPanel;
	private JCheckBox[] runOptions;
	private JButton colorizeButton, saveReportButton;
	
	private InteractionAnalysis fii;
	private boolean isColorized = false;
	private MutantSelectionPanel mutantSelectionPanel;
	private ObjectStore mutantStore;
	
	private static final long serialVersionUID = -9126723853606423085L;
	private static final String OPT_COLORBYDEFAULT = "functionalityAnalysis.colorByDefault";
	private static final String OPT_REPORTDIRECTORY = "functionalityAnalysis.reportDirectory";

	public InteractionAnalysisFrame(JFrame parent, String id, int w, int h) {
		super(parent, id, w, h);
	}

	public InteractionAnalysisFrame( JFrame frame, Graph graph) {
		super(frame, "functionalityAnalysis", 420, 260);
		this.frame = frame;
		this.graph = graph;
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
			
			runOptions = new JCheckBox[3];
			
			c.gridy++;
			c.gridx = 0;
			c.ipadx = 0;
			c.ipady = 0;
		    runOptions[0] = new JCheckBox(Translator.getString("STR_interactionAnalysis_opt_annotate"));
		    runOptions[0].setMnemonic(KeyEvent.VK_A); 
		    runOptions[0].setSelected(false);
		    mainPanel.add(runOptions[0], c);
		    
			c.gridy++;
		    runOptions[2] = new JCheckBox(Translator.getString("STR_interactionAnalysis_opt_color_by_default"));
		    runOptions[2].setMnemonic(KeyEvent.VK_C); 
		    runOptions[2].setSelected(((Boolean)OptionStore.getOption(OPT_COLORBYDEFAULT, Boolean.FALSE)).booleanValue());
		    runOptions[2].addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					boolean b = runOptions[2].isSelected();
					OptionStore.setOption(OPT_COLORBYDEFAULT, Boolean.valueOf(b));
					if (b && !isColorized) forceColorize();
				}
		    });
		    mainPanel.add(runOptions[2], c);
		    
		    c.gridy++;
		    mutantStore = new ObjectStore();
			mutantSelectionPanel = new MutantSelectionPanel(this, (GsRegulatoryGraph) graph, mutantStore);
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
		    colorizeButton = new JButton(Translator.getString("STR_do_colorize"));
		    colorizeButton.setEnabled(false);
		    mainPanel.add(colorizeButton, c);
		    colorizeButton.addActionListener(this);
		}
		return mainPanel;
	}

	protected void run() {
		if (isColorized) {
			fii.undoColorize();
			colorizeButton.setText(Translator.getString("STR_do_colorize"));
			isColorized = false;
		}
		HashSet selectedNodes = new HashSet();
		for (Iterator it = graph.getGraphManager().getSelectedVertexIterator(); it.hasNext();) {
			selectedNodes.add(it.next());
		}
		if (selectedNodes.size() == 0) {
			selectedNodes = null;
		}
		fii = new InteractionAnalysis((GsRegulatoryGraph) graph, getOption(0), (GsRegulatoryMutantDef) mutantStore.getObject(0), selectedNodes);
	    saveReportButton.setEnabled(true);
		colorizeButton.setEnabled(true);
		if (getOption(2)) {
			doColorize();
		} else {
			isColorized = false;
		}
	}
	
	private boolean getOption(int i) {
		return runOptions[i].getSelectedObjects() != null;
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == colorizeButton) {
			if (fii.getFunctionality() != null) {	
				if (isColorized) {
					undoColorize();
				} else {
					doColorize();
				}
			}
		} else if (e.getSource() == saveReportButton){
			try {
				Vector format = new Vector(1);
				format.add(GenericDocumentFormat.XHTMLDocumentFormat);
				Object[] fileAndFormat = GenericDocumentFileChooser.saveDialog(OPT_REPORTDIRECTORY, this, format);
				if (fileAndFormat != null) {
					DocumentWriter doc = (DocumentWriter)((GenericDocumentFormat)fileAndFormat[1]).documentWriterClass.newInstance();
					doc.setOutput((File)fileAndFormat[0]);
					if (fii != null) fii.saveReport(doc);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				Tools.error("An error has occured while saving", this.frame);
			}
		}
	}
	
	private void doColorize() {
		if (fii != null) {
			fii.doColorize();
			colorizeButton.setText(Translator.getString("STR_undo_colorize"));
			isColorized = true;
		}
	}
	
	private void undoColorize() {
		if (fii != null) {
			fii.undoColorize();
			colorizeButton.setText(Translator.getString("STR_do_colorize"));
			isColorized = false;
		}
	}
	
	private void forceColorize() {
		if (isColorized) undoColorize();
		doColorize();
	}

	public void cancel() {
		if (isColorized) {
			int res = JOptionPane.showConfirmDialog(this, Translator.getString("STR_sure_close_undo_colorize"));
			if (res == JOptionPane.NO_OPTION) {
				super.cancel();
			} else if (res == JOptionPane.CANCEL_OPTION) {
				return;
			} else if (res == JOptionPane.YES_OPTION) {
				undoColorize();
				super.cancel();
			}
		}
		super.cancel();
	}
}
