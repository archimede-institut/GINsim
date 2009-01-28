package fr.univmrs.tagc.GINsim.graphComparator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.gui.GsMainFrame;
import fr.univmrs.tagc.common.manageressources.Translator;

public class GraphComparatorCaptionFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = -3613649496687281647L;
	private GsMainFrame mainFrame;
	private JFrame frame;
	private GsGraph g;
	private JPanel mainPanel;
	
	public GraphComparatorCaptionFrame(JFrame frame, GsGraph g, GsMainFrame mainFrame) {
        this.g = g;
        this.mainFrame = mainFrame;
        this.frame = frame;
        initialize();
        this.setTitle(Translator.getString("STR_gcmp_caption")+" ("+g.getGraphName()+")");
        this.addWindowListener(new java.awt.event.WindowAdapter() { 
            public void windowClosing(java.awt.event.WindowEvent e) {
                cancel();
            }
        });
        if (mainFrame != null) mainFrame.addWindowListener(new java.awt.event.WindowAdapter() { 
            public void windowClosing(java.awt.event.WindowEvent e) {
                cancel();
            }
        });	
    }

	public GraphComparatorCaptionFrame(JFrame frame, GsGraph g) {
        this(frame, g, null);
    }

	public void initialize() {
		setContentPane(getMainPanel());
		Dimension preferredSize = getPreferredSize();
		setSize(preferredSize.width+20, preferredSize.height+20); //Padding 10px;
		setVisible(true);
	}

	private JPanel getMainPanel() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
	
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.ipady = 20;
		mainPanel.add(getCaptionPanel(), c);

		c.gridy = 1;
		c.ipady = 5;
		mainPanel.add(getRadioPanel(), c);
		
		return mainPanel;
	}

	private JPanel getCaptionPanel() {
		JPanel captionPanel = new JPanel();
		captionPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
	
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.ipadx = 8;
		c.ipady = 8;
		
		captionPanel.add(new JLabel(Translator.getString("STR_gcmp_caption")+" : "), c);
		
		//COMMON_COLOR
		c.gridy = 1;
		JPanel p = new JPanel();
		p.setBackground(GraphComparator.COMMON_COLOR);
		p.setBorder(BorderFactory.createLineBorder(Color.black));
		captionPanel.add(p, c);
		
		c.gridx = 1;
		captionPanel.add(new JLabel(Translator.getString("STR_gcmp_diffGraph")), c);
		
		//SPECIFIC_G1_COLOR
		c.gridx = 0;
		c.gridy = 2;
		p = new JPanel();
		p.setBackground(GraphComparator.SPECIFIC_G1_COLOR);
		p.setBorder(BorderFactory.createLineBorder(Color.black));
		captionPanel.add(p, c);
		
		c.gridx = 1;
		captionPanel.add(new JLabel(Translator.getString("STR_gcmp_graph1")), c);

		//SPECIFIC_G2_COLOR
		c.gridx = 0;
		c.gridy = 3;
		p = new JPanel();
		p.setBackground(GraphComparator.SPECIFIC_G2_COLOR);
		p.setBorder(BorderFactory.createLineBorder(Color.black));
		captionPanel.add(p, c);
		
		c.gridx = 1;
		captionPanel.add(new JLabel(Translator.getString("STR_gcmp_graph2")), c);
		return captionPanel;
	}
	
	private Component getRadioPanel() {
		JPanel radioPanel = new JPanel();
		radioPanel.setLayout(new GridLayout(0,1));
		
		radioPanel.add(new JLabel(Translator.getString("STR_gcmp_captionRadio")+" : "));

		String name = Translator.getString("STR_gcmp_setcolor_before")+Translator.getString("STR_gcmp_diffGraph")+".";
	    JRadioButton diffColor = new JRadioButton(name);
	    diffColor.setMnemonic(KeyEvent.VK_B);
	    diffColor.setActionCommand(name);
	    diffColor.setSelected(true);
	    radioPanel.add(diffColor);

		name = Translator.getString("STR_gcmp_setcolor_before")+Translator.getString("STR_gcmp_graph1")+".";
	    JRadioButton specG1Color = new JRadioButton(name);
	    specG1Color.setMnemonic(KeyEvent.VK_C);
	    specG1Color.setActionCommand(name);
	    radioPanel.add(specG1Color);

		name = Translator.getString("STR_gcmp_setcolor_before")+Translator.getString("STR_gcmp_graph2")+".";
	    JRadioButton specG2Color = new JRadioButton(name);
	    specG2Color.setMnemonic(KeyEvent.VK_D);
	    specG2Color.setActionCommand(name);
	    radioPanel.add(specG2Color);

	    //Group the radio buttons.
	    ButtonGroup group = new ButtonGroup();
	    group.add(diffColor);
	    group.add(specG1Color);
	    group.add(specG2Color);

	    //Register a listener for the radio buttons.
	    diffColor.addActionListener(this);
	    specG1Color.addActionListener(this);
	    specG2Color.addActionListener(this);

		return radioPanel;
	}

	public void actionPerformed(ActionEvent e) {
	    System.out.println(e.getActionCommand());
	}
	
    public void doClose() {
    	cancel();
    }
	private void cancel() {
    	setVisible(false);		
	}

}
