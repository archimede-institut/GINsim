package org.ginsim.gui.service.tool.graphcomparator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.EdgeAttributesReader;
import org.ginsim.graph.common.NodeAttributesReader;
import org.ginsim.graph.view.css.CascadingStyle;
import org.ginsim.graph.view.css.EdgeStyle;
import org.ginsim.graph.view.css.Style;
import org.ginsim.graph.view.css.NodeStyle;
import org.ginsim.gui.GUIManager;

import fr.univmrs.tagc.common.managerresources.Translator;

public class GraphComparatorCaptionFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = -3613649496687281647L;
	private Graph g;
	private JPanel mainPanel;
	private JTextArea resultsPane;
	private JRadioButton diffColor, specG1Color, specG2Color, intersectColor, exclusionColor, fusionColor1, fusionColor2;
	private GraphComparator gc;
	private CascadingStyle cs;
	private JButton automaticRoutingButton;
	
	private static final EdgeStyle clearEdgeStyle = new EdgeStyle(Color.black, EdgeStyle.NULL_SHAPE, EdgeStyle.NULL_LINEEND, 1);
	private static final NodeStyle clearVertexStyle = new NodeStyle(Color.white, Color.gray, 1, NodeStyle.NULL_SHAPE);
	
	public GraphComparatorCaptionFrame(Graph g, GraphComparator gc) {
        this.g = g;
        this.gc = gc;
        this.cs = new CascadingStyle(false);

        initialize();
        this.setTitle(Translator.getString("STR_gcmp_caption")+" ("+g.getGraphName()+")");
        this.addWindowListener(new java.awt.event.WindowAdapter() { 
            public void windowClosing(java.awt.event.WindowEvent e) {
                cancel();
            }
        });
        Frame frame = GUIManager.getInstance().getFrame(g);
        if (frame != null) {
        	frame.addWindowListener(new java.awt.event.WindowAdapter() { 
	            public void windowClosing(java.awt.event.WindowEvent e) {
	                cancel();
	            }
	        });	
        }
    }

	public void initialize() {
		setContentPane(getMainPanel());
		Dimension preferredSize = getPreferredSize();
		setSize(preferredSize.width+20, preferredSize.height+20); //Padding 10px;
		setVisible(true);
		//setResizable(false);
	}

	private JPanel getMainPanel() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
	
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.CENTER;
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
		
		c.gridy++;
		c.anchor = GridBagConstraints.EAST;
		automaticRoutingButton = new JButton(Translator.getString("STR_gcmp_automaticRouting"));
		automaticRoutingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
		    	gc.setEdgeAutomatingRouting();
			}
		});
		mainPanel.add(automaticRoutingButton, c);
		
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
		captionPanel.add(new JLabel(Translator.getString("STR_gcmp_commonColor")), c);
		
		//COMMON_COLOR_DIFF_MAXVALUES
		c.gridx = 0;
		c.gridy++;
		p = new JPanel();
		p.setBackground(RegulatoryGraphComparator.COMMON_COLOR_DIFF_MAXVALUES);
		p.setBorder(BorderFactory.createLineBorder(Color.black));
		captionPanel.add(p, c);
		
		c.gridx++;
		captionPanel.add(new JLabel(Translator.getString("STR_gcmp_diffmaxvalues")), c);

		//COMMON_COLOR_DIFF_FUNCTIONS
		c.gridx = 0;
		c.gridy++;
		p = new JPanel();
		p.setBackground(RegulatoryGraphComparator.COMMON_COLOR_DIFF_FUNCTIONS);
		p.setBorder(BorderFactory.createLineBorder(Color.black));
		captionPanel.add(p, c);
		
		c.gridx++;
		captionPanel.add(new JLabel(Translator.getString("STR_gcmp_difffunctions")), c);

		//SPECIFIC_G1_COLOR
		c.gridx = 0;
		c.gridy++;
		p = new JPanel();
		p.setBackground(GraphComparator.SPECIFIC_G1_COLOR);
		p.setBorder(BorderFactory.createLineBorder(Color.black));
		captionPanel.add(p, c);
		
		c.gridx++;
		captionPanel.add(new JLabel(Translator.getString("STR_gcmp_specificTo")+gc.g1m.getGraphName()), c);

		//SPECIFIC_G2_COLOR
		c.gridx = 0;
		c.gridy++;
		p = new JPanel();
		p.setBackground(GraphComparator.SPECIFIC_G2_COLOR);
		p.setBorder(BorderFactory.createLineBorder(Color.black));
		captionPanel.add(p, c);
		
		c.gridx++;
		captionPanel.add(new JLabel(Translator.getString("STR_gcmp_specificTo")+gc.g2m.getGraphName()), c);
		
		return captionPanel;
	}
	
	private Component getResultsPanel() {
		JPanel resultPanel = new JPanel();
		resultPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.ipadx = 8;
		c.ipady = 8;		
		resultPanel.add(new JLabel(Translator.getString("STR_function_results")+" : "), c);
		
		c.gridy++;
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

		String name = Translator.getString("STR_gcmp_legendColor")+".";
	    diffColor = new JRadioButton(name);
	    diffColor.setActionCommand(name);
	    diffColor.setSelected(true);
	    radioPanel.add(diffColor);

		name = Translator.getString("STR_gcmp_specificTo")+gc.g1m.getGraphName()+".";
	    specG1Color = new JRadioButton(name);
	    specG1Color.setActionCommand(name);
	    radioPanel.add(specG1Color);

		name = Translator.getString("STR_gcmp_specificTo")+gc.g2m.getGraphName()+".";
	    specG2Color = new JRadioButton(name);
	    specG2Color.setActionCommand(name);
	    radioPanel.add(specG2Color);

		name = Translator.getString("STR_gcmp_intersection");
	    intersectColor = new JRadioButton(name);
	    intersectColor.setActionCommand(name);
	    radioPanel.add(intersectColor);

		name = Translator.getString("STR_gcmp_exclusion");
		exclusionColor = new JRadioButton(name);
		exclusionColor.setActionCommand(name);
	    radioPanel.add(exclusionColor);

		name = Translator.getString("STR_gcmp_fusion1");
		fusionColor1 = new JRadioButton(name);
		fusionColor1.setActionCommand(name);
	    radioPanel.add(fusionColor1);

		name = Translator.getString("STR_gcmp_fusion2");
		fusionColor2 = new JRadioButton(name);
		fusionColor2.setActionCommand(name);
	    radioPanel.add(fusionColor2);

	    //Group the radio buttons.
	    ButtonGroup group = new ButtonGroup();
	    group.add(diffColor);
	    group.add(specG1Color);
	    group.add(specG2Color);
	    group.add(intersectColor);
	    group.add(exclusionColor);
	    group.add(fusionColor1);
	    group.add(fusionColor2);

	    //Register a listener for the radio buttons.
	    diffColor.addActionListener(this);
	    specG1Color.addActionListener(this);
	    specG2Color.addActionListener(this);
	    intersectColor.addActionListener(this);
	    exclusionColor.addActionListener(this);
	    fusionColor1.addActionListener(this);
	    fusionColor2.addActionListener(this);

		return radioPanel;
	}

	public void actionPerformed(ActionEvent e) {
	    doColorize((JRadioButton)e.getSource());
	}
	
    private void doColorize(JRadioButton source) {
    	HashMap styleMap = gc.getStyleMap();
    	NodeAttributesReader vreader = g.getVertexAttributeReader();
    	EdgeAttributesReader ereader = g.getEdgeAttributeReader();
    	
    	for (Iterator it = styleMap.keySet().iterator(); it.hasNext();) {
			Object o = it.next();
			Style style = null;
			ItemStore is = (ItemStore)styleMap.get(o);
			if (source == diffColor) {
				style = is.v;
			} else if (source == specG1Color) {
				style = is.v1;
			} else if (source == specG2Color) {
				style = is.v2;
			} else if (source == exclusionColor){
				style = is.v1;
				if (style == null) style = is.v2;
				else if (is.v2 != null) style = null;
			} else if (source == intersectColor){
				if (is.v1 != null && is.v2 != null) style = is.v1;
			} else if (source == fusionColor1) {
				style = is.v1;
				if (style == null) style = is.v2;
			} else if (source == fusionColor2) {
				style = is.v2;
				if (style == null) style = is.v1;
			}
			if (o instanceof Edge) { 	//edge
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
					cs.applyOnNode((NodeStyle)style, o, vreader);
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
