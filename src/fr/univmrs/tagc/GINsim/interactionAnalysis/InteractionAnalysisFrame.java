package fr.univmrs.tagc.GINsim.interactionAnalysis;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.MutantSelectionPanel;
import fr.univmrs.tagc.common.OptionStore;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.datastore.ObjectStore;
import fr.univmrs.tagc.common.document.DocumentWriter;
import fr.univmrs.tagc.common.document.XHTMLDocumentWriter;
import fr.univmrs.tagc.common.manageressources.Translator;
import fr.univmrs.tagc.common.widgets.StackDialog;

public class InteractionAnalysisFrame extends StackDialog implements MouseListener, ActionListener {
	private JFrame frame;
	private GsGraph graph;
	private Container mainPanel;
	private JCheckBox[] runOptions;
	private Color option_lineColor = Color.red;
//	private JTextArea resultsPane;
	private JButton colorizeButton, saveReportButton;
	private JFileChooser jfc = null;
	
	private InteractionAnalysis fii;
	private JPanel colorPanel;
	private boolean isColorized = false;
	private MutantSelectionPanel mutantSelectionPanel;
	private ObjectStore mutantStore;
	
	private static final long serialVersionUID = -9126723853606423085L;
	private static final String OPT_COLORBYDEFAULT = "functionalityAnalysis.colorByDefault";

	public InteractionAnalysisFrame(JFrame parent, String id, int w, int h) {
		super(parent, id, w, h);
	}

	public InteractionAnalysisFrame(JFrame frame, GsGraph graph) {
		super(frame, Translator.getString("STR_interactionAnalysis"), 800, 600);
		this.frame = frame;
		this.graph = graph;
        this.frame = frame;
        initialize();
        this.setTitle(Translator.getString("STR_interactionAnalysis"));
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
		    saveReportButton = new JButton(Translator.getString("STR_interactionAnalysis_saveReport"));
		    mainPanel.add(saveReportButton, c);
		    saveReportButton.addActionListener(this);
		   
		    c.gridy++;
		    colorizeButton = new JButton(Translator.getString("STR_interactionAnalysis_do_colorize"));
		    colorizeButton.setEnabled(false);
		    mainPanel.add(colorizeButton, c);
		    colorizeButton.addActionListener(this);
		}
		return mainPanel;
	}

	protected void run() {
		if (isColorized) {
			fii.undoColorize();
			colorizeButton.setText(Translator.getString("STR_interactionAnalysis_do_colorize"));
			isColorized = false;
		}
		fii = new InteractionAnalysis((GsRegulatoryGraph)graph, getOption(0), (GsRegulatoryMutantDef) mutantStore.getObject(0));
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
			File f;
			try {
				f = chooseFile();
				if (f != null) {
					DocumentWriter doc = new XHTMLDocumentWriter();
					doc.setOutput(f);
					if (fii != null) fii.saveReport(doc);
				}
			} catch (IOException e1) {
				Tools.error("An error has occured while saving", this.frame);
			}
		}
	}
	
	private void doColorize() {
		if (fii != null) {
			fii.doColorize();
			colorizeButton.setText(Translator.getString("STR_interactionAnalysis_undo_colorize"));
			isColorized = true;
		}
	}
	
	private void undoColorize() {
		if (fii != null) {
			fii.undoColorize();
			colorizeButton.setText(Translator.getString("STR_interactionAnalysis_do_colorize"));
			isColorized = false;
		}
	}
	
	private void forceColorize() {
		if (isColorized) undoColorize();
		doColorize();
	}

	public void cancel() {
		if (isColorized) {
			int res = JOptionPane.showConfirmDialog(this, Translator.getString("STR_interactionAnalysis_sure_close"));
			if (res == JOptionPane.OK_OPTION) fii.undoColorize();
			else if (res == JOptionPane.CANCEL_OPTION) return;
		}
		super.cancel();
	}
	
	private File chooseFile() throws IOException {
        int returnVal = getJfc().showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();
            OptionStore.setOption("currentDirectory", file.getParent());
            return file;
        }
        return null;
	}
	
	private JFileChooser getJfc() {
       File curDir = null;
       if (jfc != null) {
           curDir = jfc.getCurrentDirectory();
       } else {
           String path = (String)OptionStore.getOption("currentDirectory");
           if (path != null) {
               curDir = new File(path);
           }
       }
       if (curDir != null && !curDir.exists()) {
           curDir = null;
       }
       jfc = new JFileChooser(curDir);
       GsFileFilter ffilter = new GsFileFilter();
       ffilter.setExtensionList(new String[] {"html"}, "html files");
       jfc.setFileFilter(ffilter);
       return jfc;
   }

	
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == colorPanel) {
			option_lineColor = JColorChooser.showDialog(
			            frame,
			            Translator.getString("STR_interactionAnalysis_opt_color_chooser"),
			            option_lineColor);
			if (option_lineColor == null) option_lineColor = Color.red;
			colorPanel.setBackground(option_lineColor);
		}     
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
}