package fr.univmrs.tagc.GINsim.graphComparator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import fr.univmrs.tagc.GINsim.css.CascadingStyle;
import fr.univmrs.tagc.GINsim.css.EdgeStyle;
import fr.univmrs.tagc.GINsim.css.Style;
import fr.univmrs.tagc.GINsim.css.VertexStyle;
import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.gui.GsMainFrame;
import fr.univmrs.tagc.GINsim.jgraph.GsJgraphDirectedEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.common.manageressources.Translator;

public class GraphComparatorCaptionFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = -3613649496687281647L;
//	private GsMainFrame mainFrame;
//	private JFrame frame;
	private GsGraph g;
	private JPanel mainPanel;
	private JTextArea resultsPane;
	private JRadioButton diffColor;
	private JRadioButton specG1Color;
	private JRadioButton specG2Color;
	private GraphComparator gc;
	private CascadingStyle cs;
	
	private static final EdgeStyle clearEdgeStyle = new EdgeStyle(Color.black, EdgeStyle.NULL_SHAPE, EdgeStyle.NULL_LINEEND, 1);
	private static final VertexStyle clearVertexStyle = new VertexStyle(Color.white, Color.gray, 1, VertexStyle.NULL_SHAPE);
	
	public GraphComparatorCaptionFrame(JFrame frame, GsGraph g, GsMainFrame mainFrame, GraphComparator gc) {
        this.g = g;
        this.gc = gc;
//        this.mainFrame = mainFrame;
//        this.frame = frame;
        this.cs = new CascadingStyle(false);

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

	public GraphComparatorCaptionFrame(JFrame frame, GsGraph g, GraphComparator gc) {
        this(frame, g, null, gc);
    }

	public void initialize() {
		setContentPane(getMainPanel());
		Dimension preferredSize = getPreferredSize();
		setSize(preferredSize.width+20, preferredSize.height+20); //Padding 10px;
		setVisible(true);
		setResizable(false);
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

		c.gridy++;
		c.ipady = 5;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 2.0;
		mainPanel.add(getResultsPanel(), c);
		
		c.gridy++;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		mainPanel.add(getRadioPanel(), c);
		
		return mainPanel;
	}

	private JPanel getCaptionPanel() {
		JPanel captionPanel = new JPanel();
		captionPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
	
		c.gridx = 0;
		c.gridy = 0;
		c.ipadx = 8;
		c.ipady = 8;
		
		captionPanel.add(new JLabel(Translator.getString("STR_gcmp_caption")+" : "), c);
		
		//COMMON_COLOR
		c.gridy++;
		JPanel p = new JPanel();
		p.setBackground(GraphComparator.COMMON_COLOR);
		p.setBorder(BorderFactory.createLineBorder(Color.black));
		captionPanel.add(p, c);
		
		c.gridx++;
		captionPanel.add(new JLabel(Translator.getString("STR_gcmp_diffGraph")), c);
		
		//SPECIFIC_G1_COLOR
		c.gridx = 0;
		c.gridy++;
		p = new JPanel();
		p.setBackground(GraphComparator.SPECIFIC_G1_COLOR);
		p.setBorder(BorderFactory.createLineBorder(Color.black));
		captionPanel.add(p, c);
		
		c.gridx++;
		captionPanel.add(new JLabel(gc.g1m.getGsGraph().getGraphName()), c);

		//SPECIFIC_G2_COLOR
		c.gridx = 0;
		c.gridy++;
		p = new JPanel();
		p.setBackground(GraphComparator.SPECIFIC_G2_COLOR);
		p.setBorder(BorderFactory.createLineBorder(Color.black));
		captionPanel.add(p, c);
		
		c.gridx++;
		captionPanel.add(new JLabel(gc.g2m.getGsGraph().getGraphName()), c);
		return captionPanel;
	}
	
	private Component getResultsPanel() {
		JPanel resultPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 2.0;
		resultsPane = new JTextArea(gc.getLog().toString());
        JScrollPane resultsScrollPane = new JScrollPane(resultsPane);
        resultsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        resultPanel.add(resultsScrollPane, c);
        resultsScrollPane.setPreferredSize(new Dimension(350, 250));
        return resultPanel;
	}
	
	private Component getRadioPanel() {
		JPanel radioPanel = new JPanel();
		radioPanel.setLayout(new GridLayout(0,1));
		
		radioPanel.add(new JLabel(Translator.getString("STR_gcmp_captionRadio")+" : "));

		String name = Translator.getString("STR_gcmp_setcolor_before")+Translator.getString("STR_gcmp_diffGraph")+".";
	    diffColor = new JRadioButton(name);
	    diffColor.setActionCommand(name);
	    diffColor.setSelected(true);
	    radioPanel.add(diffColor);

		name = Translator.getString("STR_gcmp_setcolor_before")+gc.g1m.getGsGraph().getGraphName()+".";
	    specG1Color = new JRadioButton(name);
	    specG1Color.setActionCommand(name);
	    radioPanel.add(specG1Color);

		name = Translator.getString("STR_gcmp_setcolor_before")+gc.g2m.getGsGraph().getGraphName()+".";
	    specG2Color = new JRadioButton(name);
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
	    doColorize((JRadioButton)e.getSource());
	}
	
    private void doColorize(JRadioButton source) {
    	HashMap styleMap = gc.getStyleMap();
    	GsGraphManager gm = g.getGraphManager();
    	GsVertexAttributesReader vreader = gm.getVertexAttributesReader();
    	GsEdgeAttributesReader ereader = gm.getEdgeAttributesReader();
    	
    	for (Iterator it = styleMap.keySet().iterator(); it.hasNext();) {
			Object o = it.next();
			Style style;
			if (source == diffColor) {
				style = ((ItemStore)styleMap.get(o)).v;
			} else if (source == specG1Color) {
				style = ((ItemStore)styleMap.get(o)).v1;
			} else {
				style = ((ItemStore)styleMap.get(o)).v2;
			}			
			if (o.getClass() == GsRegulatoryMultiEdge.class || o.getClass() == GsJgraphDirectedEdge.class || o.getClass() == GsDirectedEdge.class ) { 	//edge
				ereader.setEdge(o);
				if (style != null) {
					ereader.setDash(ereader.getPattern(0)); //FIXME : thats dirty, but copy/paste from DynamicGraph.
					cs.applyOnEdge((EdgeStyle)style, o, ereader);
				} else {
					ereader.setDash(ereader.getPattern(1)); //FIXME : thats dirty, but copy/paste from DynamicGraph.
					cs.applyOnEdge(clearEdgeStyle, o, ereader);
				}
			} else { //vertex
				vreader.setVertex(o);
				if (style != null) {
					cs.applyOnNode((VertexStyle)style, o, vreader);
				} else {
					cs.applyOnNode(clearVertexStyle, o, vreader);
				}
			}
		}
	}

	public void doClose() {
    	cancel();
    }
	private void cancel() {
    	setVisible(false);		
	}

	public void setResults(String string) {
		resultsPane.setText(string);
	}

}
