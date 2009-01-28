package fr.univmrs.tagc.GINsim.interactionAnalysis;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.common.manageressources.Translator;
import fr.univmrs.tagc.common.widgets.StackDialog;

public class SearchNonFunctionalInteractionsFrame extends StackDialog implements MouseListener {
	private JFrame frame;
	private GsGraph graph;
	private Container mainPanel;
	private JCheckBox[] runOptions;
	private Color option_lineColor = Color.red;
	private JTextArea resultsPane;
	
	private SearchNonFunctionalInteractions fii;
	private JPanel colorPanel;
	
	private static final long serialVersionUID = -9126723853606423085L;

	public SearchNonFunctionalInteractionsFrame(JFrame parent, String id, int w, int h) {
		super(parent, id, w, h);
	}

	public SearchNonFunctionalInteractionsFrame(JFrame frame, GsGraph graph) {
		super(frame, Translator.getString("STR_snfi"), 800, 600);
		this.frame = frame;
		this.graph = graph;
        this.frame = frame;
        initialize();
        this.setTitle(Translator.getString("STR_snfi"));
        this.addWindowListener(new java.awt.event.WindowAdapter() { 
            public void windowClosing(java.awt.event.WindowEvent e) {
                cancel();
            }
        });	
    }

	public void initialize() {
		setMainPanel(getMainPanel());
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
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 3;
			c.ipadx = 10;
			mainPanel.add(new JLabel(Translator.getString("STR_snfi_ask")), c);
			
			runOptions = new JCheckBox[4];
			
			c.gridy++;
			c.gridwidth = 1;
		    runOptions[0] = new JCheckBox(Translator.getString("STR_snfi_opt_color"));
		    runOptions[0].setMnemonic(KeyEvent.VK_C); 
		    runOptions[0].setSelected(true);
		    mainPanel.add(runOptions[0], c);

			c.gridx = 1;
		    mainPanel.add(new JLabel(Translator.getString("STR_snfi_opt_color_chooser")), c);
		    
			c.gridx = 2;
			c.ipadx = 8;
			c.ipady = 8;
			colorPanel = new JPanel();
			colorPanel.setBackground(option_lineColor);
			colorPanel.setBorder(BorderFactory.createLineBorder(Color.black));
			colorPanel.addMouseListener(this);
		    mainPanel.add(colorPanel, c);
		    
			c.gridy++;
			c.gridx = 0;
			c.ipadx = 0;
			c.ipady = 0;
			c.gridwidth = 3;
		    runOptions[2] = new JCheckBox(Translator.getString("STR_snfi_opt_annotate"));
		    runOptions[2].setMnemonic(KeyEvent.VK_A); 
		    runOptions[2].setSelected(true);
		    mainPanel.add(runOptions[2], c);

		    c.gridy++;
		    runOptions[1] = new JCheckBox(Translator.getString("STR_snfi_opt_simplify"));
		    runOptions[1].setMnemonic(KeyEvent.VK_S); 
		    //runOptions[1].setSelected(true);
		    mainPanel.add(runOptions[1], c);
		    c.gridy++;
		    runOptions[3] = new JCheckBox(Translator.getString("STR_snfi_opt_verbose"));
		    runOptions[3].setMnemonic(KeyEvent.VK_V); 
		    //runOptions[3].setSelected(true);
		    mainPanel.add(runOptions[3], c);

			c.gridy++;
			c.ipady = 20;
			mainPanel.add(new JLabel(""), c);

			c.gridy++;
			c.ipady = 0;
			mainPanel.add(new JLabel(Translator.getString("STR_snfi_results")), c);
			
			c.gridy++;
			resultsPane = new JTextArea("");
	        JScrollPane resultsScrollPane = new JScrollPane(resultsPane);
	        resultsScrollPane.setPreferredSize(new Dimension(250, 250));
			mainPanel.add(resultsScrollPane, c);
		}
		return mainPanel;
	}

	protected void run() {
		fii = new SearchNonFunctionalInteractions((GsRegulatoryGraph)graph, getOption(0), getOption(1), getOption(2), getOption(3), option_lineColor);
		resultsPane.setText(fii.getLog().toString());
	}
	
	private boolean getOption(int i) {
		return runOptions[i].getSelectedObjects() != null;
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == colorPanel) {
			option_lineColor = JColorChooser.showDialog(
			            frame,
			            Translator.getString("STR_snfi_opt_color_chooser"),
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

class ResultsDialog extends JDialog {
	private static final long serialVersionUID = -4012429687480688324L;
	private JPanel mainPanel;
	private String results;
	Action actionListener = new AbstractAction() {
		private static final long serialVersionUID = 448859746054492959L;
		public void actionPerformed(ActionEvent actionEvent) {
			doClose();
		}
	};

	public ResultsDialog(Frame parent, String results) {
		super(parent, Translator.getString("STR_snfi"), true);
		this.results = results;
		
		
        initialize();
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				doClose();
			}
		});
	}
	
	public void initialize() {
		JPanel content = getContentPanel();
		setContentPane(content);
	    content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "ESCAPE");
	    content.getActionMap().put("ESCAPE", actionListener);        setVisible(true);
		setSize(400, 300);
	}
	
	private JPanel getContentPanel() {
		if (mainPanel == null) {
			mainPanel = new javax.swing.JPanel();
			mainPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
		
		//Label
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 1;
			mainPanel.add(new JLabel(Translator.getString("STR_snfi_results")), c);
			
			c.gridy++;
			JTextArea resultsPane = new JTextArea(results);
	        JScrollPane resultsScrollPane = new JScrollPane(resultsPane);
	        resultsScrollPane.setVerticalScrollBarPolicy(
	                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	        resultsScrollPane.setPreferredSize(new Dimension(250, 250));
			mainPanel.add(resultsScrollPane);
			
			c.gridy++;
			JButton bcancel = new javax.swing.JButton(Translator.getString("STR_close"));
        	bcancel.setToolTipText(Translator.getString("STR_closedialog_descr"));
        	bcancel.addActionListener(new java.awt.event.ActionListener() { 
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    cancel();
                }
            });
			mainPanel.add(bcancel);

		}
		return mainPanel;
	}	
	public void doClose() {
		cancel();
	}
	protected void cancel() {
		setVisible(false);
	}

}
