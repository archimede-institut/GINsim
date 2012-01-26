package org.ginsim.gui.shell.editpanel;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ginsim.common.utils.Translator;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.EdgePattern;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.GraphSelection;


/**
 * panel to configure visual settings of the graph:
 * 	line color for edges
 * 	bg/fg/text color and shape for nodes
 */
public class GraphicAttributePanel extends AbstractParameterPanel implements EditTab {

	private static final long serialVersionUID = -1041894738941096989L;

	private String nodeShape=null;
	private Color backgroundColor=null;
	private Color foregroundColor=null;
	private Color textColor=null;
	private Color lineColor=null;
	private CardLayout cards;

	private JComboBox jComboBox_shape = null;
	private JButton jButton_bgcolor = null;
	private JButton jButton_textcolor = null;
	private JButton jBttn_setDefault = null;
	private JPanel jP_attr = null;
	private JPanel jP_bttn = null;
	private JButton jB_appToAll = null;
	private JButton jButton_fgcolor = null;
	private JButton jButton_linecolor = null;
	private JTextField jTF_width = null;
	private JTextField jTF_height = null;
	private JCheckBox jCB_lineStyle = null;
	private JComboBox jCB_linePattern = null;

	private JPanel jP_edge = null;
	private JPanel jP_node = null;
	private JPanel jP_empty = null;

	private JCheckBox jCB_selectLineColor = null;
	private JCheckBox jCB_selectLineStyle = null;
	private JCheckBox jCB_selectLinePattern = null;

	private JCheckBox jCB_selectShape = null;
	private JCheckBox jCB_selectBackground = null;
	private JCheckBox jCB_selectForeground = null;
	private JCheckBox jCB_selectSize = null;

	private final NodeAttributesReader vReader;
	private final EdgeAttributesReader eReader;

	private static final int EDGESELECTED = 0;
	private static final int VERTEXSELECTED = 1;
	private static final int NOTHINGSELECTED = 2;
	private int whatIsSelected;
	private Object selected;
	private List v_selection;

	private JCheckBox jCB_selectLinewidth;

	private JSpinner jSpinner_linewidth;

	/**
	 * This is the default constructor
	 */
	public GraphicAttributePanel(GraphGUI<?, ?, ?> gui) {
		super(gui);
		Graph graph = gui.getGraph();
		vReader = graph.getNodeAttributeReader();
		eReader = graph.getEdgeAttributeReader();
		initialize();
		reload();
	}

	/**
	 * This method initializes this
	 *
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(400,60));
		this.add(getJP_attr(), BorderLayout.CENTER);
		this.add(getJP_bttn(), BorderLayout.EAST);
	}

	/**
	 * This method initializes jComboBox_shape
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBox_shape() {
		if(jComboBox_shape == null) {
			jComboBox_shape = new JComboBox();
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
	private JButton getJButton_bgcolor() {
		if(jButton_bgcolor == null) {
			jButton_bgcolor = new JButton("");
			jButton_bgcolor.setEnabled(false);
			jButton_bgcolor.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setBackgroundColor(JColorChooser.showDialog(frame,Translator.getString("choose_color"), backgroundColor));
					applyBackgroundColor();
				}
			});
		}
		return jButton_bgcolor;
	}

	/**
	 * set the current selected background color
	 * @param color
	 */
	public void setBackgroundColor(Color color) {
		if (color!=null) {
			backgroundColor = color;
			jButton_bgcolor.setBackground(backgroundColor);
		}
	}

	/**
	 * This method initializes jButton_bgcolor
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_textcolor() {
		if(jButton_textcolor == null) {
			jButton_textcolor = new JButton("");
			jButton_textcolor.setEnabled(false);
			jButton_textcolor.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setTextColor(JColorChooser.showDialog(frame,Translator.getString("choose_color"), textColor));
					applyTextColor();
				}
			});
		}
		return jButton_textcolor;
	}

	/**
	 * set the current selected background color
	 * @param color
	 */
	public void setTextColor(Color color) {
		if (color!=null) {
			textColor = color;
			jButton_textcolor.setBackground(textColor);
		}
	}
	
	/**
	 * set the current selected foreground color
	 * @param color
	 */
	public void setForegroundColor(Color color) {
		if (color!=null) {
			foregroundColor = color;
			jButton_fgcolor.setBackground(foregroundColor);
		}
	}

	/**
	 * set the current selected line color
	 * @param color
	 */
	public void setLineColor(Color color) {
		if (color!=null) {
			lineColor = color;
			jButton_linecolor.setBackground(lineColor);
		}
	}

	/**
	 * Enabled component for line style
	 */
	private void lineStyleEnabled() {
		jCB_lineStyle.setSelected(eReader.isCurve());
		jCB_lineStyle.setEnabled(true);
	}

	private void linePatternEnabled() {
		jCB_linePattern.setSelectedItem(eReader.getDash());
		jCB_linePattern.setEnabled(true);
	}

	/**
	 * Enabled component for node shape
	 */
	private void shapeEnabled() {
		jComboBox_shape.setSelectedItem(vReader.getShape());
		jComboBox_shape.setEnabled(true);
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
		jButton_textcolor.setBackground(vReader.getTextColor());
		jButton_bgcolor.setEnabled(true);
		jButton_fgcolor.setEnabled(true);
		jButton_textcolor.setEnabled(true);
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
		jButton_textcolor.setEnabled(false);
		jButton_linecolor.setEnabled(false);
		jComboBox_shape.setEnabled(false);
		jTF_height.setEnabled(false);
		jTF_width.setEnabled(false);
		jCB_lineStyle.setEnabled(false);
		jCB_linePattern.setEnabled(false);
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
	private JButton getJBttn_setDefault() {
		if(jBttn_setDefault == null) {
			jBttn_setDefault = new JButton(Translator.getString("STR_set_default"));
			jBttn_setDefault.setSize(100,25);
			jBttn_setDefault.setName("jBttn_setDefault");
			jBttn_setDefault.setToolTipText(Translator.getString("STR_set_default_descr"));
			jBttn_setDefault.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
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
	private JPanel getJP_attr() {
		if(jP_attr == null) {
			jP_attr = new JPanel();
			cards = new CardLayout();
			jP_attr.setLayout(cards);
			jP_attr.add(getJP_edge(), getJP_edge().getName());
			jP_attr.add(getJP_node(), getJP_node().getName());
			jP_attr.add(getJP_empty(), getJP_empty().getName());
			cards.show(jP_attr,getJP_empty().getName());
			jP_attr.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		}
		return jP_attr;
	}

	/**
	 * This method initializes jP_bttn
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJP_bttn() {
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
			jP_bttn.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		}
		return jP_bttn;
	}

	/**
	 * This method initializes jB_appToAll
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJB_appToAll() {
		if(jB_appToAll == null) {
			jB_appToAll = new JButton(Translator.getString("STR_apply_to_all"));
			jB_appToAll.setSize(100, 25);
			jB_appToAll.setToolTipText(Translator.getString("STR_apply_to_all_descr"));
			jB_appToAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					applyToAll();
				}
			});
		}
		return jB_appToAll;
	}

	/**
	 * This method initializes jButton_fgcolor
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_fgcolor() {
		if(jButton_fgcolor == null) {
			jButton_fgcolor = new JButton();
			jButton_fgcolor.setEnabled(false);
			jButton_fgcolor.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setForegroundColor(JColorChooser.showDialog(frame,Translator.getString("choose_color"), foregroundColor));
					applyForegroundColor();
				}
			});
		}
		return jButton_fgcolor;
	}

	/**
	 * This method initializes jButton_linecolor
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_linecolor() {
		if(jButton_linecolor == null) {
			jButton_linecolor = new JButton();
			jButton_linecolor.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setLineColor(JColorChooser.showDialog(frame,Translator.getString("choose_color"),null));
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
	 * This method initializes jTF_width
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTF_width() {
		if(jTF_width == null) {
			jTF_width = new JTextField();
			jTF_width.setEnabled(false);
			jTF_width.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					applyWidthHeight();
				}
			});
			jTF_width.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					if (!e.isTemporary()) {
						applyWidthHeight();
					}
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
	private JTextField getJTF_height() {
		if(jTF_height == null) {
			jTF_height = new JTextField();
			jTF_height.setEnabled(false);
			jTF_height.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					applyWidthHeight();
				}
			});
			jTF_height.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					if (!e.isTemporary()) {
						applyWidthHeight();
					}
				}
				public void focusGained(FocusEvent e) {
					refreshSize();
				}
			});
		}
		return jTF_height;
	}

	/**
	 * This method initializes jCB_lineStyle
	 *
	 * @return javax.swing.JComboBox
	 */
	private JCheckBox getJCB_lineStyle() {
		if(jCB_lineStyle == null) {
			jCB_lineStyle = new JCheckBox();
			jCB_lineStyle.setEnabled(false);
			jCB_lineStyle.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					applyLineStyle();
				}
			});
		}
		return jCB_lineStyle;
	}

	private JComboBox getJCB_linePattern() {
		if(jCB_linePattern == null) {
			jCB_linePattern = new JComboBox();
			jCB_linePattern.setEnabled(false);
			jCB_linePattern.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					applyLinePattern();
				}
			});
		}
		return jCB_linePattern;
	}

	/**
	 * apply the current line style
	 */
	protected void applyLineStyle() {
		eReader.setCurve(jCB_lineStyle.isSelected());
		eReader.refresh();
	}

	protected void applyLinePattern() {
		eReader.setDash((EdgePattern)jCB_linePattern.getSelectedItem());
		eReader.refresh();
	}

	/**
	 * This method initializes jP_edge
	 *
	 * @return JPanel
	 */
	private JPanel getJP_edge() {
		if(jP_edge == null) {
			jP_edge = new javax.swing.JPanel();
			jP_edge.setName("jP_edge");
			jP_edge.setLayout(new java.awt.GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();

			c.gridx = 0;
			c.gridy = 0;
			c.fill = java.awt.GridBagConstraints.BOTH;
			c.weightx = 0;
			c.gridheight = 1;
			jP_edge.add(new JLabel(Translator.getString("STR_line_color")), c);

			c.gridx = 1;
			c.gridy = 0;
			c.fill = java.awt.GridBagConstraints.BOTH;
			c.weightx = 0;
			c.gridheight = 1;
			jP_edge.add(getJButton_linecolor(), c);

			c = new GridBagConstraints();
			c.gridx = 2;
			c.gridy = 0;
			jP_edge.add(getJCB_selectLineColor(), c);

			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 1;
			c.fill = java.awt.GridBagConstraints.BOTH;
			c.weightx = 0;
			c.gridheight = 1;
			jP_edge.add(new JLabel(Translator.getString("STR_line_width")), c);

			c = new GridBagConstraints();
			c.gridx = 1;
			c.gridy = 1;
			c.fill = java.awt.GridBagConstraints.BOTH;
			c.weightx = 0;
			c.gridheight = 1;
			jP_edge.add(getJSpinner_linewidth(), c);

			c = new GridBagConstraints();
			c.gridx = 2;
			c.gridy = 1;
			jP_edge.add(getJCB_selectLinewidth(), c);

			c = new GridBagConstraints();
			c.fill = java.awt.GridBagConstraints.BOTH;
			c.weightx = 0;
			c.gridx = 3;
			c.gridy = 0;
			jP_edge.add(new JLabel(Translator.getString("STR_curve")), c);

			c = new GridBagConstraints();
			c.gridx = 4;
			c.gridy = 0;
			c.weightx = 0;
			c.fill = java.awt.GridBagConstraints.BOTH;
			jP_edge.add(getJCB_lineStyle(), c);

			c = new GridBagConstraints();
			c.gridx = 5;
			c.gridy = 0;
			jP_edge.add(getJCB_selectLineStyle(), c);

			c = new GridBagConstraints();
			c.gridx = 3;
			c.gridy = 1;
			jP_edge.add(new JLabel(Translator.getString("STR_linePattern")), c);
			c = new GridBagConstraints();
			c.gridx = 4;
			c.gridy = 1;
			c.weightx = 0;
			c.fill = java.awt.GridBagConstraints.BOTH;
			jP_edge.add(getJCB_linePattern(), c);
			c = new GridBagConstraints();
			c.gridx = 5;
			c.gridy = 1;
			jP_edge.add(getJCB_selectLinePattern(), c);
		}
		return jP_edge;
	}
	/**
	 * This method initializes jP_node
	 *
	 * @return JPanel
	 */
	private JPanel getJP_node() {
		if(jP_node == null) {
			jP_node = new javax.swing.JPanel();
			jP_node.setLayout(new java.awt.GridBagLayout());
			jP_node.setName("jP_node");

			GridBagConstraints consGridBagConstraints3 = new java.awt.GridBagConstraints();
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

			consGridBagConstraints16.gridx = 0;
			consGridBagConstraints16.gridy = 3;
			consGridBagConstraints16.fill = java.awt.GridBagConstraints.BOTH;
			consGridBagConstraints17.gridx = 1;
			consGridBagConstraints17.gridy = 3;
			consGridBagConstraints17.fill = java.awt.GridBagConstraints.BOTH;


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

			jP_node.add(new JLabel(Translator.getString("STR_shape")),
					consGridBagConstraints111);
			jP_node.add(getJComboBox_shape(), consGridBagConstraints5);
			jP_node.add(getJCB_selectShape(), consGridBagConstraints20);
			jP_node.add(new JLabel(Translator.getString("STR_bg_color")),
					consGridBagConstraints12);
			jP_node.add(getJButton_bgcolor(), consGridBagConstraints13);
			jP_node.add(getJCB_selectBackground(), consGridBagConstraints21);
			jP_node.add(new JLabel(Translator.getString("STR_fg_color")),
					consGridBagConstraints14);
			jP_node.add(getJButton_fgcolor(), consGridBagConstraints101);
			
			jP_node.add(new JLabel(Translator.getString("STR_text_color")),
					consGridBagConstraints16);
			jP_node.add(getJButton_textcolor(), consGridBagConstraints17);
			
			
			jP_node.add(getJCB_selectForeground(), consGridBagConstraints22);

			jP_node.add(new JLabel(Translator.getString("STR_height")),
					consGridBagConstraints141);
			jP_node.add(getJTF_height(), consGridBagConstraints3);
			jP_node.add(getJCB_selectSize(), consGridBagConstraints23);
			jP_node.add(new JLabel(Translator.getString("STR_width")),
					consGridBagConstraints15);
			jP_node.add(getJTF_width(), consGridBagConstraints4);
		}
		return jP_node;
	}
	/**
	 * This method initializes jP_empty
	 *
	 * @return JPanel
	 */
	private JPanel getJP_empty() {
		if(jP_empty == null) {
			jP_empty = new javax.swing.JPanel();
			jP_empty.setName("jP_empty");
		}
		return jP_empty;
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
	private JCheckBox getJCB_selectLineStyle() {
		if (jCB_selectLineStyle == null) {
			jCB_selectLineStyle = new JCheckBox();
			jCB_selectLineStyle.setSelected(true);
		}
		return jCB_selectLineStyle;
	}
	private JCheckBox getJCB_selectLinePattern() {
		if (jCB_selectLinePattern == null) {
			jCB_selectLinePattern = new JCheckBox();
			jCB_selectLinePattern.setSelected(true);
		}
		return jCB_selectLinePattern;
	}
	protected JCheckBox getJCB_selectBackground() {
		if (jCB_selectBackground == null) {
			jCB_selectBackground = new JCheckBox();
			jCB_selectBackground.setSelected(true);
		}
		return jCB_selectBackground;
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

	private void reload() {
		// apply shape list
		jComboBox_shape.removeAllItems();
		for (NodeShape shape: NodeShape.values()) {
			jComboBox_shape.addItem(shape);
		}

		// apply line pattern list
		jCB_linePattern.removeAllItems();
		for (EdgePattern pattern: EdgePattern.values()) {
			jCB_linePattern.addItem(pattern);
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

	protected void applyTextColor() {
		vReader.setTextColor(jButton_textcolor.getBackground());
		vReader.refresh();
	}

	/**
	 * apply change on shape
	 */
	protected void applyShape() {
		vReader.setShape(NodeShape.values()[jComboBox_shape.getSelectedIndex()]);
		vReader.refresh();
	}

	/**
	 * apply all attribute to the selection if more than one cell is selected
	 * or to all the graph in only one cell is selected
	 */
	protected void applyToAll() {
		switch(whatIsSelected) {
		case EDGESELECTED:
			Collection edges = v_selection;
			if (v_selection == null) {
				edges = graph.getEdges();
			}
			for (Object edge: edges) {
				eReader.setEdge(edge);
				if (jCB_selectLineColor.isSelected()) {
					eReader.setLineColor(jButton_linecolor.getBackground());
				}
				if (jCB_selectLineStyle.isSelected()) {
					eReader.setCurve( jCB_lineStyle.isSelected());
				}
				if (jCB_selectLinePattern.isSelected()) {
					eReader.setDash((EdgePattern)jCB_linePattern.getSelectedItem());
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
			Collection<?> vertices = v_selection;
			if (v_selection == null) {
				vertices = graph.getNodes();
			}
			for (Object vertex: vertices) {
				vReader.setNode(vertex);
				if (jCB_selectShape.isSelected()) {
					vReader.setShape(NodeShape.values()[jComboBox_shape.getSelectedIndex()]);
				}
				if (jCB_selectForeground.isSelected()) {
					vReader.setForegroundColor(jButton_fgcolor.getBackground());
				}
				if (jCB_selectBackground.isSelected()) {
					vReader.setBackgroundColor(jButton_bgcolor.getBackground());
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
			vReader.setNode(selected);
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
				eReader.setDefaultCurve( jCB_lineStyle.isSelected());
			}
			if (jCB_selectLinePattern.isSelected()) {
				// TODO: make it work
			}
			if (jCB_selectLinewidth.isSelected()) {
				eReader.setDefaultEdgeSize(((Integer)jSpinner_linewidth.getValue()).intValue());
			}
			break;
		case VERTEXSELECTED:
			refreshSize();
			if (jCB_selectShape.isSelected()) {
				vReader.setDefaultNodeShape(NodeShape.values()[jComboBox_shape.getSelectedIndex()]);
			}
			if (jCB_selectForeground.isSelected()) {
				vReader.setDefaultNodeForeground(jButton_fgcolor.getBackground());
			}
			if (jCB_selectBackground.isSelected()) {
				vReader.setDefaultNodeBackground(jButton_bgcolor.getBackground());
			}
			if (jCB_selectSize.isSelected()) {
				try {
					int w = Integer.parseInt(jTF_width.getText());
					int h = Integer.parseInt(jTF_height.getText());
					vReader.setDefaultNodeSize(w, h);
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

	@Override
	public String getTitle() {
		return "Graphics";
	}

	@Override
	public boolean isActive( GraphSelection<?, ?> selection) {
		switch (selection.getSelectionType()) {
		case SEL_NODE:
			setEditedItem(selection.getSelectedNodes().get(0));
			return true;
		case SEL_EDGE:
			setEditedItem(selection.getSelectedEdges().get(0));
			return true;
		case SEL_MULTIPLE:
			if (selection.getSelectedNodes() != null) {
				if (selection.getSelectedNodes().size() == 1) {
					setEditedItem(selection.getSelectedNodes().get(0));
				} else {
					setEditedItem(selection.getSelectedNodes());
				}
			} else if (selection.getSelectedEdges().size() == 1) {
				setEditedItem(selection.getSelectedEdges().get(0));
			} else {
				setEditedItem(selection.getSelectedEdges());
			}
			return true;
		default:
			return false;
		}
	}

	@Override
	public void setEditedItem(Object obj) {
		allDisabled();
		if (obj == null) {
			whatIsSelected = NOTHINGSELECTED;
			jB_appToAll.setVisible(false);
			jBttn_setDefault.setVisible(false);
			return;
		}
		if (obj instanceof List) {
			v_selection = (List)obj;
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
		if (selected instanceof Edge) {
			whatIsSelected = EDGESELECTED;
			eReader.setEdge(selected);
			lineColorEnabled();
			lineStyleEnabled();
			linePatternEnabled();
			lineWidthEnabled();
			cards.show(jP_attr,getJP_edge().getName());
		} else {
			whatIsSelected = VERTEXSELECTED;
			vReader.setNode(selected);
			colorEnabled();
			shapeEnabled();
			sizeEnabled();
			widthHeightEnabled();
			cards.show(jP_attr,getJP_node().getName());
		}
	}

}
