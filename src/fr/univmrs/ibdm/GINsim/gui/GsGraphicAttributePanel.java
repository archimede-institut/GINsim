package fr.univmrs.ibdm.GINsim.gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;
import fr.univmrs.ibdm.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;

/**
 * panel to configure visual settings of the graph:
 * 	line color for edges
 * 	bg/fg color and shape for nodes
 */
public class GsGraphicAttributePanel extends GsParameterPanel {

	private static final long serialVersionUID = -1041894738941096989L;
	
	private String nodeShape=null;
	private Color backgroundColor=null;
	private Color foregroundColor=null;
	private Color lineColor=null;
	private CardLayout cards;

	private javax.swing.JLabel jLabel_color = null;
	private javax.swing.JLabel jLabel_shape = null;
	private javax.swing.JComboBox jComboBox_shape = null;
	private javax.swing.JButton jButton_bgcolor = null;
	private javax.swing.JButton jBttn_setDefault = null;
	private javax.swing.JPanel jP_attr = null;
	private javax.swing.JPanel jP_bttn = null;
    private javax.swing.JButton jB_appToAll = null;
	private javax.swing.JLabel jL_fg_color = null;
	private javax.swing.JButton jButton_fgcolor = null;
	private javax.swing.JLabel jL_lineColor = null;
	private javax.swing.JButton jButton_linecolor = null;
	private javax.swing.JLabel jL_width = null;
	private javax.swing.JLabel jL_height = null;
	private javax.swing.JTextField jTF_width = null;
	private javax.swing.JTextField jTF_height = null;
	private javax.swing.JLabel jL_edgeRouting = null;
	private javax.swing.JComboBox jCB_edgeRouting = null;
	private javax.swing.JLabel jL_lineStyle = null;
	private javax.swing.JComboBox jCB_lineStyle = null;

	private javax.swing.JPanel jP_edge = null;
	private javax.swing.JPanel jP_node = null;
	private javax.swing.JPanel jP_empty = null;
	private JComboBox jCB_simpleBorder = null;
	private javax.swing.JLabel jL_simpleBorder = null;

	private JCheckBox jCB_selectLineColor = null;
	private JCheckBox jCB_selectLineStyle = null;

	private JCheckBox jCB_selectShape = null;
	private JCheckBox jCB_selectBackground = null;
	private JCheckBox jCB_selectForeground = null;
	private JCheckBox jCB_selectSize = null;
	private JCheckBox jCB_selectBorder = null;
	
	private GsVertexAttributesReader vReader = null;
	private GsEdgeAttributesReader eReader = null;
	
	private static final int EDGESELECTED = 0;
	private static final int VERTEXSELECTED = 1;
	private static final int NOTHINGSELECTED = 2;
	private int whatIsSelected;
    private Object selected;
    private Vector v_selection;

    private JLabel jL_linewidth;

    private JCheckBox jCB_selectLinewidth;

    private JCheckBox jCB_selectLinerouting;

    private JSpinner jSpinner_linewidth;
	
	/**
	 * This is the default constructor
	 */
	public GsGraphicAttributePanel() {
		super();
		initialize();
	}
	
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setLayout(new java.awt.BorderLayout());
		this.setPreferredSize(new java.awt.Dimension(400,60));
		this.add(getJP_attr(), java.awt.BorderLayout.CENTER);
		this.add(getJP_bttn(), java.awt.BorderLayout.EAST);
	}
	
	/**
	 * get the main frame
	 * @return the main frame
	 */
	public GsMainFrame getMainFrame() {
		return mainFrame;
	}
	
	/**
	 * This method initializes jLabel_color
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel_color() {
		if(jLabel_color == null) {
			jLabel_color = new javax.swing.JLabel(Translator.getString("STR_bg_color"));
			jLabel_color.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			jLabel_color.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
		}
		return jLabel_color;
	}
	
	/**
	 * This method initializes jLabel_shape
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel_shape() {
		if(jLabel_shape == null) {
			jLabel_shape = new javax.swing.JLabel(Translator.getString("STR_shape"));
			jLabel_shape.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			jLabel_shape.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
		}
		return jLabel_shape;
	}
	
	/**
	 * This method initializes jComboBox_shape
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJComboBox_shape() {
		if(jComboBox_shape == null) {
			jComboBox_shape = new javax.swing.JComboBox();
			jComboBox_shape.setEnabled(false);
			jComboBox_shape.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					applyShape();
				}
			});
		}
		return jComboBox_shape;
	}
	
	/**
	 * This method initializes jButton_bgcolor
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton_bgcolor() {
		if(jButton_bgcolor == null) {
			jButton_bgcolor = new javax.swing.JButton("");
			jButton_bgcolor.setEnabled(false);
			jButton_bgcolor.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setBackgroundColor(JColorChooser.showDialog(mainFrame,Translator.getString("choose_color"),getBackgroundColor()));
					applyBackgroundColor();
				}
			});
		}
		return jButton_bgcolor;
	}
	
	/**
	 * Get the current selected background color
	 * @return a color
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * Get the current selected foreground color
	 * @return a color
	 */
	public Color getForegroundColor() {
		return foregroundColor;
	}
	
	/**
	 * Get the current selected line color
	 * @return Color
	 */
	public Color getLineColor() {
		return lineColor;
	}

	/**
	 * set the current selected background color
	 * @param color
	 */
	public void setBackgroundColor(Color color) {
		if (color!=null) backgroundColor = color;
		jButton_bgcolor.setBackground(backgroundColor);
	}

	/**
	 * set the current selected foreground color
	 * @param color
	 */
	public void setForegroundColor(Color color) {
		if (color!=null) foregroundColor = color;
		jButton_fgcolor.setBackground(foregroundColor);
	}
	
	/**
	 * set the current selected line color
	 * @param color
	 */
	public void setLineColor(Color color) {
		lineColor = color;
		if (color!=null) lineColor = color;
		jButton_linecolor.setBackground(lineColor);
	}

	/**
	 * Enabled component for line style 
	 */
	private void lineStyleEnabled() {
		jCB_lineStyle.setSelectedIndex(eReader.getStyle());
		jCB_lineStyle.setEnabled(true);
	}
	
	/**
	 * Enabled component for edge routing
	 */
	private void edgeRoutingEnabled() {
		jCB_edgeRouting.setSelectedIndex(eReader.getRouting());
		jCB_edgeRouting.setEnabled(true);
		
	}
	
	/**
	 * Enabled component for node shape
	 */
	private void shapeEnabled() {
		jComboBox_shape.setSelectedIndex(vReader.getShape());
		jComboBox_shape.setEnabled(true);
	}
	
	/**
	 * Enabled component for node shape
	 */
	private void borderEnabled() {
		jCB_simpleBorder.setSelectedIndex(vReader.getBorder());
		jCB_simpleBorder.setEnabled(true);
	}
	
    private void sizeEnabled() {
        jTF_height.setEnabled(true);
        jTF_width.setEnabled(true);
        refreshSize();
    }
    
	/**
	 * Enabled component for background color
	 */
	private void colorEnabled() {
		jButton_bgcolor.setBackground(vReader.getBackgroundColor());
		jButton_fgcolor.setBackground(vReader.getForegroundColor());
		jButton_bgcolor.setEnabled(true);
		jButton_fgcolor.setEnabled(true);
	}
	
	/**
	 * Enabled component for line color
	 */
    private void lineColorEnabled() {
        jButton_linecolor.setBackground(eReader.getLineColor());
        jButton_linecolor.setEnabled(true);
    }
    private void lineWidthEnabled() {
        jSpinner_linewidth.setValue(new Integer((int)eReader.getLineWidth()));
    }
	
	/**
	 * Enabled component for width and height
	 */
	private void widthHeightEnabled() {
		jTF_height.setEnabled(true);
		jTF_width.setEnabled(true);
	}
	
	/**
	 * Disabled all component 
	 */
	private void allDisabled() {
		cards.show(jP_attr,jP_empty.getName());
		jButton_bgcolor.setEnabled(false);
		jButton_fgcolor.setEnabled(false);
		jButton_linecolor.setEnabled(false);
		jCB_simpleBorder.setEnabled(false);
		jComboBox_shape.setEnabled(false);
		jTF_height.setEnabled(false);
		jTF_width.setEnabled(false);
		jCB_edgeRouting.setEnabled(false);
		jCB_lineStyle.setEnabled(false);
	}

	/**
	 * get the current node shape
	 * @return the name of the node's shape
	 */
	public String getNodeShape() {
		return nodeShape;
	}

	/**
	 * set the current node shape
	 * @param str GsGraphConstants.RECTANGULAR_VERTEX or GsGraphConstants.ELLIPTIC_VERTEX or other future implementation
	 */
	public void setNodeShape(String str) {
		nodeShape = str;
	}

	/**
	 * This method initializes jBttn_setDefault
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJBttn_setDefault() {
		if(jBttn_setDefault == null) {
			jBttn_setDefault = new javax.swing.JButton(Translator.getString("STR_set_default"));
            jBttn_setDefault.setSize(100,25);
			jBttn_setDefault.setName("jBttn_setDefault");
            jBttn_setDefault.setToolTipText(Translator.getString("STR_set_default_descr"));
			jBttn_setDefault.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					applyDefault();
				}
			});
		}
		return jBttn_setDefault;
	}
		
	/**
	 * This method initializes jP_attr
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJP_attr() {
		if(jP_attr == null) {
			jP_attr = new javax.swing.JPanel();
			cards = new java.awt.CardLayout();						
			jP_attr.setLayout(cards);
			jP_attr.add(getJP_edge(), getJP_edge().getName());
			jP_attr.add(getJP_node(), getJP_node().getName());
			jP_attr.add(getJP_empty(), getJP_empty().getName());
			cards.show(jP_attr,getJP_empty().getName());
			jP_attr.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,5,5,5));
		}
		return jP_attr;
	}
	
	/**
	 * This method initializes jP_bttn
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJP_bttn() {
		if(jP_bttn == null) {
			jP_bttn = new javax.swing.JPanel();
			jP_bttn.setLayout(new GridBagLayout());
            
            GridBagConstraints c_def = new GridBagConstraints();
            c_def.gridx = 0;
            c_def.gridy = 0;
            c_def.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints c_all = new GridBagConstraints();
            c_all.gridx = 0;
            c_all.gridy = 1;
            c_all.fill = GridBagConstraints.HORIZONTAL;
			jP_bttn.add(getJBttn_setDefault(), c_def);
			jP_bttn.add(getJB_appToAll(), c_all);
			jP_bttn.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,5,5,5));
		}
		return jP_bttn;
	}

    /**
     * This method initializes jB_appToAll
     * 
     * @return javax.swing.JButton
     */
    private javax.swing.JButton getJB_appToAll() {
        if(jB_appToAll == null) {
            jB_appToAll = new javax.swing.JButton(Translator.getString("STR_apply_to_all"));
            jB_appToAll.setSize(100, 25);
            jB_appToAll.setToolTipText(Translator.getString("STR_apply_to_all_descr"));
            jB_appToAll.addActionListener(new java.awt.event.ActionListener() { 
                public void actionPerformed(java.awt.event.ActionEvent e) {    
                    applyToAll();
                }
            });
        }
        return jB_appToAll;
    }
    
	/**
	 * This method initializes jL_fg_color
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJL_fg_color() {
		if(jL_fg_color == null) {
			jL_fg_color = new javax.swing.JLabel(Translator.getString("STR_fg_color"));
			jL_fg_color.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			jL_fg_color.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
		}
		return jL_fg_color;
	}
	
	/**
	 * This method initializes jButton_fgcolor
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton_fgcolor() {
		if(jButton_fgcolor == null) {
			jButton_fgcolor = new javax.swing.JButton();
			jButton_fgcolor.setEnabled(false);
			jButton_fgcolor.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					setForegroundColor(JColorChooser.showDialog(mainFrame,Translator.getString("choose_color"),null));
					applyForegroundColor();
				}
			});
		}
		return jButton_fgcolor;
	}
	
	/**
	 * This method initializes jL_lineColor
	 * 
	 * @return javax.swing.JLabel
	 */
    private javax.swing.JLabel getJL_lineColor() {
        if(jL_lineColor == null) {
            jL_lineColor = new javax.swing.JLabel(Translator.getString("STR_line_color"));
            jL_lineColor.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
            jL_lineColor.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        }
        return jL_lineColor;
    }
    
    private javax.swing.JLabel getJL_linewidth() {
        if(jL_linewidth == null) {
            jL_linewidth = new javax.swing.JLabel(Translator.getString("STR_line_width"));
            jL_linewidth.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
            jL_linewidth.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        }
        return jL_linewidth;
    }
    
	/**
	 * This method initializes jButton_linecolor
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton_linecolor() {
		if(jButton_linecolor == null) {
			jButton_linecolor = new javax.swing.JButton();
			jButton_linecolor.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					setLineColor(JColorChooser.showDialog(mainFrame,Translator.getString("choose_color"),null));
					applyLineColor();
				}
			});
		}
		return jButton_linecolor;
	}
	
    private JSpinner getJSpinner_linewidth() {
        if (jSpinner_linewidth == null) {
            jSpinner_linewidth = new JSpinner(new SpinnerNumberModel(1,1,50,1));
            jSpinner_linewidth.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    applyLineWidth();
                }
            });
        }
        return jSpinner_linewidth;
    }
    
	/**
	 * This method initializes jL_width
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJL_width() {
		if(jL_width == null) {
			jL_width = new javax.swing.JLabel(Translator.getString("STR_width"));
			jL_width.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			jL_width.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
		}
		return jL_width;
	}
	
	/**
	 * This method initializes jL_height
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJL_height() {
		if(jL_height == null) {
			jL_height = new javax.swing.JLabel(Translator.getString("STR_height"));
			jL_height.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			jL_height.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
		}
		return jL_height;
	}
	
	/**
	 * This method initializes jTF_width
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTF_width() {
		if(jTF_width == null) {
			jTF_width = new javax.swing.JTextField();
			jTF_width.setEnabled(false);
			jTF_width.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					applyWidthHeight();
				}
			});
			jTF_width.addFocusListener(new java.awt.event.FocusAdapter() { 
				public void focusLost(java.awt.event.FocusEvent e) {    
					if (!e.isTemporary()) applyWidthHeight();
				}
                public void focusGained(FocusEvent e) {
                    refreshSize();
                }
			});
		}
		return jTF_width;
	}
	
	/**
	 * get the current with and height
	 * @return Dimension
	 */
	protected Dimension getWidthHeight() {
		try {
			int w=Integer.parseInt(jTF_width.getText());
			int h=Integer.parseInt(jTF_height.getText());
			return new Dimension(w,h);
		} catch(Exception E) {
			return new Dimension(30,50);
		}
	}
	
	/**
	 * This method initializes jTF_height
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTF_height() {
		if(jTF_height == null) {
			jTF_height = new javax.swing.JTextField();
			jTF_height.setEnabled(false);
			jTF_height.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					applyWidthHeight();
				}
			});
			jTF_height.addFocusListener(new java.awt.event.FocusAdapter() { 
				public void focusLost(java.awt.event.FocusEvent e) { 
					if (!e.isTemporary()) applyWidthHeight();
				}
                public void focusGained(FocusEvent e) {
                    refreshSize();
                }
			});
		}
		return jTF_height;
	}
	
	/**
	 * This method initializes jL_edgeRouting
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJL_edgeRouting() {
		if(jL_edgeRouting == null) {
			jL_edgeRouting = new javax.swing.JLabel(Translator.getString("STR_edgeRouting"));
			jL_edgeRouting.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			jL_edgeRouting.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		}
		return jL_edgeRouting;
	}
	
	/**
	 * This method initializes jCB_edgeRouting
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJCB_edgeRouting() {
		if(jCB_edgeRouting == null) {
			jCB_edgeRouting = new javax.swing.JComboBox();
			jCB_edgeRouting.setEnabled(false);
			jCB_edgeRouting.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					applyEdgeRouting();
				}
			});
		}
		return jCB_edgeRouting;
	}
	
	/**
	 * This method initializes jL_lineStyle
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJL_lineStyle() {
		if(jL_lineStyle == null) {
			jL_lineStyle = new javax.swing.JLabel(Translator.getString("STR_lineStyle"));
			jL_lineStyle.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			jL_lineStyle.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		}
		return jL_lineStyle;
	}
	
	/**
	 * This method initializes jCB_lineStyle
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJCB_lineStyle() {
		if(jCB_lineStyle == null) {
			jCB_lineStyle = new javax.swing.JComboBox();
			jCB_lineStyle.setEnabled(false);
			jCB_lineStyle.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					applyLineStyle();
				}
			});
		}
		return jCB_lineStyle;
	}
	
	/**
	 * apply the current line style
	 */
	protected void applyLineStyle() {
		eReader.setStyle(jCB_lineStyle.getSelectedIndex());
		eReader.refresh();
	}
	
	/**
	 * This method initializes jP_edge
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJP_edge() {
		if(jP_edge == null) {
			jP_edge = new javax.swing.JPanel();
			jP_edge.setName("jP_edge");
			jP_edge.setLayout(new java.awt.GridBagLayout());

            java.awt.GridBagConstraints c_l_lcolor = new java.awt.GridBagConstraints();
            java.awt.GridBagConstraints c_b_lcolor = new java.awt.GridBagConstraints();
            java.awt.GridBagConstraints c_cb_lcolor = new java.awt.GridBagConstraints();
            java.awt.GridBagConstraints c_l_lw = new java.awt.GridBagConstraints();
            java.awt.GridBagConstraints c_b_lw = new java.awt.GridBagConstraints();
            java.awt.GridBagConstraints c_cb_lw = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints c_cb_lstyle = new java.awt.GridBagConstraints();
            java.awt.GridBagConstraints c_cb_erouting = new java.awt.GridBagConstraints();
            java.awt.GridBagConstraints c_cb_serouting = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints c_l_erouting = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints c_l_lstyle = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints c_cb_slstyle = new java.awt.GridBagConstraints();

            c_l_lcolor.gridx = 0;
            c_l_lcolor.gridy = 0;
            c_l_lcolor.fill = java.awt.GridBagConstraints.BOTH;
            c_l_lcolor.weightx = 0;
            c_l_lcolor.gridheight = 1;
            c_b_lcolor.gridx = 1;
            c_b_lcolor.gridy = 0;
            c_b_lcolor.fill = java.awt.GridBagConstraints.BOTH;
            c_b_lcolor.weightx = 0;
            c_b_lcolor.gridheight = 1;
            c_cb_lcolor.gridx = 2;
            c_cb_lcolor.gridy = 0;
            c_l_lw.gridx = 0;
            c_l_lw.gridy = 1;
            c_l_lw.fill = java.awt.GridBagConstraints.BOTH;
            c_l_lw.weightx = 0;
            c_l_lw.gridheight = 1;
            c_b_lw.gridx = 1;
            c_b_lw.gridy = 1;
            c_b_lw.fill = java.awt.GridBagConstraints.BOTH;
            c_b_lw.weightx = 0;
            c_b_lw.gridheight = 1;
            c_cb_lw.gridx = 2;
            c_cb_lw.gridy = 1;
			c_l_erouting.gridx = 3;
			c_l_erouting.gridy = 0;
			c_l_erouting.fill = java.awt.GridBagConstraints.BOTH;
			c_l_erouting.weightx = 0;
			c_cb_erouting.gridx = 4;
			c_cb_erouting.gridy = 0;
			c_cb_erouting.weightx = 0;
			c_cb_erouting.fill = java.awt.GridBagConstraints.BOTH;
            c_cb_serouting.gridx = 5;
            c_cb_serouting.gridy = 0;
			c_l_lstyle.fill = java.awt.GridBagConstraints.BOTH;
			c_l_lstyle.weightx = 0;
			c_l_lstyle.gridx = 3;
			c_l_lstyle.gridy = 1;
			c_cb_lstyle.gridx = 4;
			c_cb_lstyle.gridy = 1;
			c_cb_lstyle.weightx = 0;
			c_cb_lstyle.fill = java.awt.GridBagConstraints.BOTH;
			c_cb_slstyle.gridx = 5;
			c_cb_slstyle.gridy = 1;
			
            jP_edge.add(getJL_lineColor(), c_l_lcolor);
            jP_edge.add(getJButton_linecolor(), c_b_lcolor);
            jP_edge.add(getJCB_selectLineColor(), c_cb_lcolor);
            jP_edge.add(getJL_linewidth(), c_l_lw);
            jP_edge.add(getJSpinner_linewidth(), c_b_lw);
            jP_edge.add(getJCB_selectLinewidth(), c_cb_lw);
			jP_edge.add(getJL_edgeRouting(), c_l_erouting);
            jP_edge.add(getJCB_edgeRouting(), c_cb_erouting);
            jP_edge.add(getJCB_selectLinerouting(), c_cb_serouting);
			jP_edge.add(getJL_lineStyle(), c_l_lstyle);
			jP_edge.add(getJCB_lineStyle(), c_cb_lstyle);
			jP_edge.add(getJCB_selectLineStyle(), c_cb_slstyle);
		}
		return jP_edge;
	}
	/**
	 * This method initializes jP_node
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJP_node() {
		if(jP_node == null) {
			jP_node = new javax.swing.JPanel();
			jP_node.setLayout(new java.awt.GridBagLayout());
			jP_node.setName("jP_node");

			java.awt.GridBagConstraints consGridBagConstraints3 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints5 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints101 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints111 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints12 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints4 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints13 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints14 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints141 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints15 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints16 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints17 = new java.awt.GridBagConstraints();

			java.awt.GridBagConstraints consGridBagConstraints20 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints21 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints22 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints23 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints24 = new java.awt.GridBagConstraints();

			consGridBagConstraints111.gridx = 0;
			consGridBagConstraints111.gridy = 0;
			consGridBagConstraints111.fill = java.awt.GridBagConstraints.BOTH;
			consGridBagConstraints5.fill = java.awt.GridBagConstraints.BOTH;
			consGridBagConstraints5.gridx = 1;
			consGridBagConstraints5.gridy = 0;
			consGridBagConstraints20.gridx = 2;
			consGridBagConstraints20.gridy = 0;
			
			consGridBagConstraints12.gridx = 0;
			consGridBagConstraints12.gridy = 1;
			consGridBagConstraints12.fill = java.awt.GridBagConstraints.BOTH;
			consGridBagConstraints13.gridx = 1;
			consGridBagConstraints13.gridy = 1;
			consGridBagConstraints13.fill = java.awt.GridBagConstraints.BOTH;
			consGridBagConstraints21.gridx = 2;
			consGridBagConstraints21.gridy = 1;

			consGridBagConstraints14.gridx = 0;
			consGridBagConstraints14.gridy = 2;
			consGridBagConstraints14.fill = java.awt.GridBagConstraints.BOTH;
			consGridBagConstraints101.gridx = 1;
			consGridBagConstraints101.gridy = 2;
			consGridBagConstraints101.fill = java.awt.GridBagConstraints.BOTH;
			consGridBagConstraints22.gridx = 2;
			consGridBagConstraints22.gridy = 2;

			
			consGridBagConstraints141.gridx = 4;
			consGridBagConstraints141.gridy = 0;
			consGridBagConstraints141.fill = java.awt.GridBagConstraints.BOTH;
			consGridBagConstraints3.gridx = 5;
			consGridBagConstraints3.gridy = 0;
			consGridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH;
			consGridBagConstraints23.gridx = 6;
			consGridBagConstraints23.gridy = 0;
			
			consGridBagConstraints15.gridx = 4;
			consGridBagConstraints15.gridy = 1;
			consGridBagConstraints15.fill = java.awt.GridBagConstraints.BOTH;
			consGridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
			consGridBagConstraints4.gridx = 5;
			consGridBagConstraints4.gridy = 1;

			consGridBagConstraints17.gridx = 4;
			consGridBagConstraints17.gridy = 2;
			consGridBagConstraints17.fill = java.awt.GridBagConstraints.BOTH;
			consGridBagConstraints16.gridx = 5;
			consGridBagConstraints16.gridy = 2;
			consGridBagConstraints24.gridx = 6;
			consGridBagConstraints24.gridy = 2;
			
			jP_node.add(getJLabel_shape(), consGridBagConstraints111);
			jP_node.add(getJComboBox_shape(), consGridBagConstraints5);
			jP_node.add(getJCB_selectShape(), consGridBagConstraints20);
			jP_node.add(getJLabel_color(), consGridBagConstraints12);
			jP_node.add(getJButton_bgcolor(), consGridBagConstraints13);
			jP_node.add(getJCB_selectBackground(), consGridBagConstraints21);
			jP_node.add(getJL_fg_color(), consGridBagConstraints14);
			jP_node.add(getJButton_fgcolor(), consGridBagConstraints101);
			jP_node.add(getJCB_selectForeground(), consGridBagConstraints22);
			
			jP_node.add(getJL_height(), consGridBagConstraints141);
			jP_node.add(getJTF_height(), consGridBagConstraints3);
			jP_node.add(getJCB_selectSize(), consGridBagConstraints23);
			jP_node.add(getJL_width(), consGridBagConstraints15);
			jP_node.add(getJTF_width(), consGridBagConstraints4);
			jP_node.add(getJL_simpleBorder(), consGridBagConstraints17);
			jP_node.add(getJCB_simpleBorder(), consGridBagConstraints16);
			jP_node.add(getJCB_selectBorder(), consGridBagConstraints24);
		}
		return jP_node;
	}
	/**
	 * This method initializes jP_empty
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJP_empty() {
		if(jP_empty == null) {
			jP_empty = new javax.swing.JPanel();
			jP_empty.setName("jP_empty");
		}
		return jP_empty;
	}
	/**
	 * This method initializes jCB_simpleBorder
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JComboBox getJCB_simpleBorder() {
		if(jCB_simpleBorder == null) {
			jCB_simpleBorder = new JComboBox();
			jCB_simpleBorder.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					applyBorder();
				}
			});
		}
		return jCB_simpleBorder;
	}
	
    private JCheckBox getJCB_selectLineColor() {
        if (jCB_selectLineColor == null) {
            jCB_selectLineColor = new JCheckBox();
            jCB_selectLineColor.setSelected(true);
        }
        return jCB_selectLineColor;
    }
    private JCheckBox getJCB_selectLinewidth() {
        if (jCB_selectLinewidth == null) {
            jCB_selectLinewidth = new JCheckBox();
            jCB_selectLinewidth.setSelected(true);
        }
        return jCB_selectLinewidth;
    }
    private JCheckBox getJCB_selectLinerouting() {
        if (jCB_selectLinerouting == null) {
            jCB_selectLinerouting = new JCheckBox();
            jCB_selectLinerouting.setSelected(true);
        }
        return jCB_selectLinerouting;
    }
	private JCheckBox getJCB_selectLineStyle() {
		if (jCB_selectLineStyle == null) {
			jCB_selectLineStyle = new JCheckBox();
			jCB_selectLineStyle.setSelected(true);
		}
		return jCB_selectLineStyle;
	}
	protected JCheckBox getJCB_selectBackground() {
		if (jCB_selectBackground == null) {
			jCB_selectBackground = new JCheckBox();
			jCB_selectBackground.setSelected(true);
		}
		return jCB_selectBackground;
	}
	protected JCheckBox getJCB_selectBorder() {
		if (jCB_selectBorder == null) {
			jCB_selectBorder = new JCheckBox();
			jCB_selectBorder.setSelected(true);
		}
		return jCB_selectBorder;
	}
	protected JCheckBox getJCB_selectForeground() {
		if (jCB_selectForeground == null) {
			jCB_selectForeground = new JCheckBox();
			jCB_selectForeground.setSelected(true);
		}
		return jCB_selectForeground;
	}
	protected JCheckBox getJCB_selectShape() {
		if (jCB_selectShape == null) {
			jCB_selectShape = new JCheckBox();
			jCB_selectShape.setSelected(true);
		}
		return jCB_selectShape;
	}
	protected JCheckBox getJCB_selectSize() {
		if (jCB_selectSize == null) {
			jCB_selectSize = new JCheckBox();
			jCB_selectSize.setSelected(true);
		}
		return jCB_selectSize;
	}

	/**
	 * This method initializes jL_simpleBorder
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJL_simpleBorder() {
		if(jL_simpleBorder == null) {
			jL_simpleBorder = new javax.swing.JLabel(Translator.getString("STR_border"));
			jL_simpleBorder.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			jL_simpleBorder.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		}
		return jL_simpleBorder;
	}

	/*
	 * @see fr.univmrs.ibdm.GINsim.gui.GsParameterPanel#setEditedObject(java.lang.Object)
	 */
	public void setEditedObject(Object obj) {
        allDisabled();
        if (obj == null) {
            whatIsSelected = NOTHINGSELECTED;
            jB_appToAll.setVisible(false);
            jBttn_setDefault.setVisible(false);
            return;
        }
        if (obj instanceof Vector) {
            v_selection = (Vector)obj;
            selected = v_selection.get(0);
        } else {
            v_selection = null;
            selected = obj;
        }
		jB_appToAll.setVisible(true);
		jBttn_setDefault.setVisible(true);
        if (v_selection != null) {
            jBttn_setDefault.setVisible(true);
        }
		if (selected instanceof GsDirectedEdge) {
			whatIsSelected = EDGESELECTED;
			eReader.setEdge(selected);
			lineColorEnabled();
			lineStyleEnabled();
			edgeRoutingEnabled();
            lineWidthEnabled();
			cards.show(jP_attr,getJP_edge().getName());
		} else {
			whatIsSelected = VERTEXSELECTED;
			vReader.setVertex(selected);
			colorEnabled();
			shapeEnabled();
			borderEnabled();
            sizeEnabled();
			widthHeightEnabled();
			cards.show(jP_attr,getJP_node().getName());
		}
	}

	public void setGraph(GsGraph graph) {
		if (graph == null) {
			return;
		}
		vReader = graph.getGraphManager().getVertexAttributesReader();
		eReader = graph.getGraphManager().getEdgeAttributesReader();
		
		// apply shape list
		jComboBox_shape.removeAllItems();
		Vector v_tmp = vReader.getShapeList();
		for (int i=0 ; i<v_tmp.size() ; i++) {
			jComboBox_shape.addItem(v_tmp.get(i));
		}
		
		// apply border list
		jCB_simpleBorder.removeAllItems();
		v_tmp = vReader.getBorderList();
		for (int i=0 ; i<v_tmp.size() ; i++) {
			jCB_simpleBorder.addItem(v_tmp.get(i));
		}
		
		// apply routing list
		jCB_edgeRouting.removeAllItems();
		v_tmp = eReader.getRoutingList();
		for (int i=0 ; i<v_tmp.size() ; i++) {
			jCB_edgeRouting.addItem(v_tmp.get(i));
		}
		
		// apply line style list
		jCB_lineStyle.removeAllItems();
		v_tmp = eReader.getStyleList();
		for (int i=0 ; i<v_tmp.size() ; i++) {
			jCB_lineStyle.addItem(v_tmp.get(i));
		}
		
	}
	
	/**
	 * apply change on color
	 */
	protected void applyBackgroundColor() {
		vReader.setBackgroundColor(jButton_bgcolor.getBackground());
		vReader.refresh();
	}
	
	protected void applyForegroundColor() {
		vReader.setForegroundColor(jButton_fgcolor.getBackground());
		vReader.refresh();
	}
	
	/**
	 * apply change on shape
	 */
	protected void applyShape() {
		vReader.setShape(jComboBox_shape.getSelectedIndex());
		vReader.refresh();
	}
	
	/**
	 * apply all attribute to the selection if more than one cell is selected
	 * or to all the graph in only one cell is selected
	 */
	protected void applyToAll() {
		switch(whatIsSelected) {
			case EDGESELECTED:
                Iterator it;
                if (v_selection != null) {
                    it = v_selection.iterator();
                } else {
                    it = mainFrame.getGraph().getGraphManager().getEdgeIterator();
                }
				while (it.hasNext()) {
					eReader.setEdge(it.next());
					if (jCB_selectLineColor.isSelected()) {
						eReader.setLineColor(jButton_linecolor.getBackground());
					}
					if (jCB_selectLineStyle.isSelected()) {
						eReader.setStyle( jCB_lineStyle.getSelectedIndex());
					}
                    if (jCB_selectLinerouting.isSelected()) {
                        eReader.setRouting(jCB_edgeRouting.getSelectedIndex());
                    }
                    if (jCB_selectLinewidth.isSelected()) {
                        eReader.setLineWidth( ((Integer)jSpinner_linewidth.getValue()).intValue() );
                    }
					eReader.refresh();
				}
                eReader.setEdge(selected);
				break;
			case VERTEXSELECTED:
                refreshSize();
                if (v_selection != null) {
                    it = v_selection.iterator();
                } else {
                    it = mainFrame.getGraph().getGraphManager().getVertexIterator();
                }
				while (it.hasNext()) {
					vReader.setVertex(it.next());
					if (jCB_selectShape.isSelected()) {
						vReader.setShape(jComboBox_shape.getSelectedIndex());
					}
					if (jCB_selectForeground.isSelected()) {
						vReader.setForegroundColor(jButton_fgcolor.getBackground());
					}
					if (jCB_selectBackground.isSelected()) {
						vReader.setBackgroundColor(jButton_bgcolor.getBackground());
					}
					if (jCB_selectBorder.isSelected()) {
						vReader.setBorder(jCB_simpleBorder.getSelectedIndex());
					}
                    if (jCB_selectSize.isSelected()) {
                        try {
                            int w = Integer.parseInt(jTF_width.getText());
                            int h = Integer.parseInt(jTF_height.getText());
                            vReader.setSize(w,h);
                        } catch (NumberFormatException e) {}
                    }
					vReader.refresh();
				}
                vReader.setVertex(selected);
				break;
		}
	}
	/**
	 * apply as default attributes
	 */
	protected void applyDefault() {
		switch(whatIsSelected) {
			case EDGESELECTED:
				if (jCB_selectLineColor.isSelected()) {
					eReader.setDefaultEdgeColor(jButton_linecolor.getBackground());
				}
				if (jCB_selectLineStyle.isSelected()) {
					eReader.setDefaultStyle( jCB_lineStyle.getSelectedIndex());
				}
                if (jCB_selectLinewidth.isSelected()) {
                    eReader.setDefaultEdgeSize(((Integer)jSpinner_linewidth.getValue()).intValue());
                }
				break;
			case VERTEXSELECTED:
                refreshSize();
				if (jCB_selectShape.isSelected()) {
					vReader.setDefaultVertexShape(jComboBox_shape.getSelectedIndex());
				}
				if (jCB_selectForeground.isSelected()) {
					vReader.setDefaultVertexForeground(jButton_fgcolor.getBackground());
				}
				if (jCB_selectBackground.isSelected()) {
					vReader.setDefaultVertexBackground(jButton_bgcolor.getBackground());
				}
				if (jCB_selectBorder.isSelected()) {
					vReader.setDefaultVertexBorder(jCB_simpleBorder.getSelectedIndex());
				}
                if (jCB_selectSize.isSelected()) {
                    try {
                        int w = Integer.parseInt(jTF_width.getText());
                        int h = Integer.parseInt(jTF_height.getText());
                        vReader.setDefaultVertexSize(w, h);
                    } catch (NumberFormatException e) {}
                }
				break;
		}
	}
	
	/**
	 * apply with and height 
	 */
	protected void applyWidthHeight() {
        try {
            int w = Integer.parseInt(jTF_width.getText());
            int h = Integer.parseInt(jTF_height.getText());
            vReader.setSize(w,h);
            vReader.refresh();
        } catch (NumberFormatException e) {}
        refreshSize();
	}
	
	/**
	 * apply the border setting
	 */
	protected void applyBorder() {
		vReader.setBorder(jCB_simpleBorder.getSelectedIndex());
		vReader.refresh();
	}

	/**
	 * apply the current edge routing
	 */
	protected void applyEdgeRouting() {
		if (eReader.getRouting() != jCB_edgeRouting.getSelectedIndex()) {
			eReader.setRouting(jCB_edgeRouting.getSelectedIndex());
			eReader.refresh();
		}
	}
	
    protected void applyLineColor() {
        eReader.setLineColor(jButton_linecolor.getBackground());
        eReader.refresh();
    }
    
    protected void applyLineWidth() {
        int w = ((Integer)jSpinner_linewidth.getValue()).intValue();
        eReader.setLineWidth(w);
        eReader.refresh();
    }
    
    protected void refreshSize() {
        jTF_height.setText(""+vReader.getHeight());
        jTF_width.setText((""+vReader.getWidth()));
    }
}  //  @jve:visual-info  decl-index=0 visual-constraint="14,9"
