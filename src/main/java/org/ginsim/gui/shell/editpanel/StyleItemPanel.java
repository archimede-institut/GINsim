package org.ginsim.gui.shell.editpanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.view.style.EdgeStyle;
import org.ginsim.core.graph.view.style.NodeStyle;
import org.ginsim.core.graph.view.style.Style;
import org.ginsim.core.graph.view.style.StyleManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.utils.widgets.StockButton;

public class StyleItemPanel extends JPanel {

    private final StyleManager manager;
    private final StyleComboModel styleModel;
    
    private final StyleEditionPanel stylePanel;
    private final JComboBox styleSelection;
    
    private final JButton newStyleButton;
    private final JButton deleteStyleButton;
	
	public StyleItemPanel(GraphGUI<?, ?, ?> gui) {
		super(new GridBagLayout());

		this.manager = gui.getGraph().getStyleManager();
    	this.stylePanel = new StyleEditionPanel(gui, manager);
    	this.styleModel = new StyleComboModel(manager, stylePanel);
    	this.styleSelection = new JComboBox(styleModel);

    	this.newStyleButton = new StockButton("list-add.png", true);
        newStyleButton.setToolTipText("Create a new style");
    	newStyleButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				createStyle();
			}
		});

    	this.deleteStyleButton = new StockButton("list-remove.png", true);
        deleteStyleButton.setToolTipText("Reset style");
    	deleteStyleButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				deleteStyle();
			}
		});

    	GridBagConstraints c = new GridBagConstraints();
    	c.gridx = 0;
    	c.gridy = 0;
    	c.weightx = 1;
    	c.fill = GridBagConstraints.HORIZONTAL;
		add(styleSelection, c);
		
		c.gridx = 1;
		c.weightx = 0;
		add(newStyleButton, c);

		c.gridx = 2;
		c.weightx = 0;
		add(deleteStyleButton, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		JScrollPane sp = new JScrollPane();
		sp.setViewportView(stylePanel);
		add(sp, c);
	}

	protected void createStyle() {
		styleModel.createStyle();
	}
	
	protected void deleteStyle() {
		styleModel.deleteStyle();
	}
	
	public void edit(Collection nodes, Collection<Edge> edges) {
		styleModel.edit(nodes, edges);
	}
}

class StyleComboModel extends AbstractListModel implements ComboBoxModel {

	private final StyleManager styleManager;
	
	private final List<NodeStyle> nodeStyles;
    private final List<EdgeStyle> edgeStyles;
    private final StyleEditionPanel editPanel;

    private Collection selectedNodes = null;
    private Collection<Edge> selectedEdges = null;
    
	public StyleComboModel(StyleManager styleManager, StyleEditionPanel editPanel) {
		this.styleManager = styleManager;
		this.nodeStyles = styleManager.getNodeStyles();
		this.edgeStyles = styleManager.getEdgeStyles();
		this.editPanel = editPanel;
	}
	
	public void createStyle() {
		Style newStyle = null;
		if (selectedNodes != null ) {
			newStyle = styleManager.addNodeStyle();
		} else if (selectedEdges != null) {
			newStyle = styleManager.addEdgeStyle();
		}
		setSelectedItem(newStyle);
	}

	public void deleteStyle() {
		Style style = (Style)getSelectedItem();
		if (style != null) {
			styleManager.deleteStyle(style);
            if (style instanceof NodeStyle) {
                setSelected(styleManager.getDefaultNodeStyle());
            } else if (style instanceof EdgeStyle) {
                setSelected(styleManager.getDefaultEdgeStyle());
            }
		}
	}

	private void disableEdit() {
		this.selectedEdges = null;
		this.selectedNodes = null;
		this.styles = null;
		setSelected(null);
	}

	private List styles = null;
	private Style selected = null;
	
	public void edit(Collection nodes, Collection<Edge> edges) {
		if (edges != null && edges.size() == 0) {
			edges = null;
		}
		if (nodes != null && nodes.size() == 0) {
			nodes = null;
		}

		this.selectedEdges = edges;
		this.selectedNodes = nodes;
		
		if (nodes == null && edges == null) {
			setSelectedItem(null);
			disableEdit();
			return;
		}
		
		if (nodes == null) {
			
			Style selected = null;
			for (Edge edge: edges) {
				Style ns = styleManager.getUsedEdgeStyle(edge);
				if (ns == null) {
					break;
				}
				if (selected == null) {
					selected = ns;
				} else if (selected != ns) {
					selected = null;
					break;
				}
			}
			
			this.styles = edgeStyles;
			setSelected(selected);
			return;
		}
		
		Style selected = null;
		for (Object node: nodes) {
			Style ns = styleManager.getUsedNodeStyle(node);
			if (ns == null) {
				break;
			}
			if (selected == null) {
				selected = ns;
			} else if (selected != ns) {
				selected = null;
				break;
			}
		}
		
		this.styles = nodeStyles;
		setSelected(selected);
	}

	@Override
	public Object getElementAt(int index) {
		if (styles == null || index >= styles.size()) {
			return null;
		}
		return styles.get(index);
	}

	@Override
	public int getSize() {
		if (styles == null) {
			return 0;
		}
		return styles.size();
	}

	@Override
	public Object getSelectedItem() {
		return selected;
	}

	@Override
	public void setSelectedItem(Object item) {
		if (item == null) {
			disableEdit();
			return;
		}
		if (selectedNodes != null && item instanceof NodeStyle) {
			styleManager.applyNodeStyle(selectedNodes, (NodeStyle)item);
		} else if (selectedEdges != null && item instanceof EdgeStyle) {
			styleManager.applyEdgeStyle(selectedEdges, (EdgeStyle)item);
		} else {
			return;
		}
		
		edit(selectedNodes, selectedEdges);
	}
	
	private void setSelected(Style sel) {
		this.selected = sel;
		editPanel.setStyle(sel);
		fireContentsChanged(this, 0, getSize());
	}
}