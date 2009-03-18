package fr.univmrs.tagc.GINsim.interactionAnalysis;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.MutantSelectionPanel;
import fr.univmrs.tagc.common.datastore.ObjectStore;
import fr.univmrs.tagc.common.manageressources.Translator;
import fr.univmrs.tagc.common.widgets.StackDialog;

public class InteractionAnalysisFrame extends StackDialog implements MouseListener, ActionListener {
	private JFrame frame;
	private GsGraph graph;
	private Container mainPanel;
	private JCheckBox[] runOptions;
	private Color option_lineColor = Color.red;
	private JTextArea resultsPane;
	private JButton colorizeButton;
	
	private InteractionAnalysis fii;
	private JPanel colorPanel;
	private boolean isColorized = false;
	private MutantSelectionPanel mutantSelectionPanel;
	private ObjectStore mutantStore;
	
	private static final long serialVersionUID = -9126723853606423085L;

	public InteractionAnalysisFrame(JFrame parent, String id, int w, int h) {
		super(parent, id, w, h);
	}

	public InteractionAnalysisFrame(JFrame frame, GsGraph graph) {
		super(frame, Translator.getString("STR_function"), 800, 600);
		this.frame = frame;
		this.graph = graph;
        this.frame = frame;
        initialize();
        this.setTitle(Translator.getString("STR_function"));
        this.addWindowListener(new java.awt.event.WindowAdapter() { 
            public void windowClosing(java.awt.event.WindowEvent e) {
                cancel();
            }
        });	
    }

	public void initialize() {
		setMainPanel(getMainPanel());
		setMinimumSize(new Dimension(640,480));
		Dimension preferredSize = getPreferredSize();
		setSize(preferredSize.width+20, preferredSize.height+20); //Padding 10px;
	}
	
	private Container getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new javax.swing.JPanel();
			mainPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
		
		//Label
			c.gridx = 0;
			c.gridy = 0;
			c.fill = GridBagConstraints.BOTH;
			c.ipadx = 10;
			mainPanel.add(new JLabel(Translator.getString("STR_function_ask")), c);
			
			runOptions = new JCheckBox[2];
			
			c.gridy++;
			c.gridx = 0;
			c.ipadx = 0;
			c.ipady = 0;
		    runOptions[0] = new JCheckBox(Translator.getString("STR_function_opt_annotate"));
		    runOptions[0].setMnemonic(KeyEvent.VK_A); 
		    runOptions[0].setSelected(true);
		    mainPanel.add(runOptions[0], c);
		    
			c.gridy++;
		    runOptions[1] = new JCheckBox(Translator.getString("STR_function_opt_verbose"));
		    runOptions[1].setMnemonic(KeyEvent.VK_V); 
		    runOptions[1].setSelected(false);
		    mainPanel.add(runOptions[1], c);
		    
		    c.gridy++;
		    mutantStore = new ObjectStore();
			mutantSelectionPanel = new MutantSelectionPanel(this, (GsRegulatoryGraph) graph, mutantStore);
			mainPanel.add(mutantSelectionPanel, c);

		    
			c.gridy++;
			c.ipady = 20;
			mainPanel.add(new JLabel(""), c);

			c.gridy++;
			c.ipady = 0;
			mainPanel.add(new JLabel(Translator.getString("STR_function_results")), c);
			
			c.gridy++;
			c.weightx = 2.0;
			c.weighty = 2.0;
			resultsPane = new JTextArea("");
	        JScrollPane resultsScrollPane = new JScrollPane(resultsPane);
	        resultsPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
	        resultsScrollPane.setPreferredSize(new Dimension(250, 250));
			mainPanel.add(resultsScrollPane, c);
			
		    c.gridy++;
			c.weightx = 0;
			c.weighty = 0;
		    colorizeButton = new JButton(Translator.getString("STR_function_do_colorize"));
		    colorizeButton.setEnabled(false);
		    mainPanel.add(colorizeButton, c);
		    colorizeButton.addActionListener(this);
		    
		    bcancel.addActionListener(new java.awt.event.ActionListener() { //have a proper quit method
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    doClose();
                }
            });

		}
		return mainPanel;
	}

	protected void run() {
		if (isColorized) {
			fii.undoColorize();
			colorizeButton.setText(Translator.getString("STR_function_do_colorize"));
			isColorized = false;
		}
		fii = new InteractionAnalysis((GsRegulatoryGraph)graph, getOption(0), getOption(1), (GsRegulatoryMutantDef) mutantStore.getObject(0));
		resultsPane.setText(fii.getLog().toString());
		colorizeButton.setEnabled(true);
		isColorized = false;
	}
	
	private boolean getOption(int i) {
		return runOptions[i].getSelectedObjects() != null;
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == colorizeButton) {
			if (fii.getFunctionality() != null) {	
				if (isColorized) {
					fii.undoColorize();
					colorizeButton.setText(Translator.getString("STR_function_do_colorize"));
					isColorized = false;
				} else {
					fii.doColorize();
					colorizeButton.setText(Translator.getString("STR_function_undo_colorize"));
					isColorized = true;
				}
			}
		}
	}
	
	public void doClose() {
		if (isColorized) {
			int res = JOptionPane.showConfirmDialog(this, Translator.getString("STR_function_sure_close"));
			if (res == JOptionPane.OK_OPTION) fii.undoColorize();
			else if (res == JOptionPane.CANCEL_OPTION) return;
		}
	}
	
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == colorPanel) {
			option_lineColor = JColorChooser.showDialog(
			            frame,
			            Translator.getString("STR_function_opt_color_chooser"),
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