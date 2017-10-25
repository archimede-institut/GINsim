package org.ginsim.gui.graph.view.style;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.view.style.EdgeStyle;
import org.ginsim.core.graph.view.style.NodeStyle;
import org.ginsim.core.graph.view.style.Style;
import org.ginsim.core.graph.view.style.StyleManager;
import org.ginsim.core.graph.view.style.StyleProvider;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.GraphSelection;
import org.ginsim.gui.shell.editpanel.EditTab;
import org.ginsim.gui.utils.widgets.StockButton;

/**
 * The style tab: forward selection to the actual Style panel.
 *
 * @author Aurelien Naldi
 */
public class StyleTab extends JPanel
        implements ListSelectionListener, ItemListener, ActionListener, EditTab {

    private final StyleManager manager;

    private final ListTableModel nStyleModel;
    private final ListTableModel eStyleModel;
    private final JList list;

    private final StyleEditionPanel stylePanel;

    private Collection selectedNodes = null;
    private Collection<Edge> selectedEdges = null;

    private final JToggleButton bNodes, bEdges;
    private final JButton b_removeProvider;
    private final JCheckBox cb_compatibilityMode;

    private final JLabel label = new JLabel();
    private boolean providerMode = false;

    private boolean pending = false;

    private Style currentStyle = null;

    private final EdgeRoutingPanel routing;

    public StyleTab(GraphGUI gui) {
        super(new GridBagLayout());

        Graph graph = gui.getGraph();
        this.manager = graph.getStyleManager();
        this.nStyleModel = new ListTableModel(manager.getNodeStyles());
        this.eStyleModel = new ListTableModel(manager.getEdgeStyles());
        this.list = new JList(nStyleModel);
        list.setCellRenderer(new StyleCellRenderer());
        list.addListSelectionListener(this);

        this.routing = new EdgeRoutingPanel(manager);
        this.stylePanel = new StyleEditionPanel(gui, manager);

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 0;

        // select node or edge styles
        ButtonGroup group = new ButtonGroup();
        bNodes = new JToggleButton("Nodes");
        group.add(bNodes);
        bEdges = new JToggleButton("Edges");
        group.add(bEdges);
        bNodes.setSelected(true);
        bNodes.addItemListener(this);

        add(bNodes, c);
        c.gridx++;
        add(bEdges, c);

        // information label
        c.gridx += 2;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.NORTH;
        add(label, c);
        
        // routing panel
        c.gridx += 1;
        add(routing, c);

        // style provider reset button
        c.gridx++;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.NORTHEAST;
        b_removeProvider = new JButton("Remove style provider");
        b_removeProvider.setVisible(false);
        b_removeProvider.addActionListener(this);
        add(b_removeProvider, c);

        // compatibility mode checkbox
        if (manager.isCompatMode()) {
            c.gridx++;
            cb_compatibilityMode = new JCheckBox("Compatibility mode");
            cb_compatibilityMode.setSelected(manager.isCompatMode());
            add(cb_compatibilityMode, c);
            cb_compatibilityMode.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    switchStyles();
                }
            });
        } else {
            cb_compatibilityMode = null;
        }

        c.gridy++;
        c.gridwidth = 8;
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(new JSeparator(), c);

        // add the style list
        GridBagConstraints cst = new GridBagConstraints();
        cst.gridx = 0;
        cst.gridy = 1;
        cst.weighty = 1;
        cst.gridwidth = 2;
        cst.gridheight = 3;
        cst.fill = GridBagConstraints.BOTH;
        cst.anchor = GridBagConstraints.NORTHWEST;
        add(list, cst);

        // create and delete buttons
        c = new GridBagConstraints();
        c.gridx = cst.gridx+2;
        c.gridy = cst.gridy;
        JButton b_create = new StockButton("list-add.png", true);
        b_create.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                createStyle();
            }
        });
        add(b_create, c);

        c.gridy++;
        JButton b_del = new StockButton("list-remove.png", true);
        b_del.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                deleteStyle();
            }
        });
        add(b_del, c);


        // add the style edition panel
        cst.gridx += 3;
        cst.weightx = 1;
        cst.weighty = 1;
        cst.gridwidth = 4;
        cst.anchor = GridBagConstraints.CENTER;
        add(stylePanel, cst);

        edit(null, null);
    }

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
            // TODO: pick the first style
            routing.setSelected(null);
            bNodes.setEnabled(true);
            bEdges.setEnabled(true);
            label.setText("");
            
            return;
        }

        bNodes.setEnabled(false);
        bEdges.setEnabled(false);

        if (nodes == null) {
            Style selected = null;
            Edge first = null;
            for (Edge edge: edges) {
                if (first == null) {
                    first = edge;
                }
                Style ns = manager.getUsedEdgeStyle(edge);
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

            if (edges.size() == 1) {
                label.setText("");
                routing.setVisible(true);
                routing.setSelected(first);
            } else {
                routing.setSelected(null);
                label.setText("Style for "+edges.size()+" selected edges");
            }
            setCurrentStyle(selected);
            return;
        }

        routing.setSelected(null);
        Style selected = null;
        Object first = null;
        for (Object node: nodes) {
            if (first == null) {
                first = node;
            }
            Style ns = manager.getUsedNodeStyle(node);
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

        if (nodes.size() == 1) {
            label.setText("Style for node: "+first);
        } else {
            label.setText("Style for "+nodes.size()+" selected nodes");
        }
        setCurrentStyle(selected);
    }

    private void setCurrentStyle(Style style) {
        if (pending || style == currentStyle) {
            return;
        }

        pending = true;

        this.currentStyle = style;
        stylePanel.setStyle(style);

        if (style == null) {
            list.clearSelection();
            pending = false;
            return;
        }

        // update the list and edit panel
        if (style instanceof NodeStyle) {
            list.setModel(nStyleModel);
            manager.applyNodeStyle(selectedNodes, (NodeStyle) style);
        } else if (style instanceof EdgeStyle) {
            list.setModel(eStyleModel);
            manager.applyEdgeStyle(selectedEdges, (EdgeStyle) style);
        }
        list.setSelectedValue(style, true);

        updated();
        pending = false;
    }

    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        setCurrentStyle((Style) list.getSelectedValue());
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if (selectedNodes != null || selectedEdges != null) {
            return;
        }

        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
            list.setModel(nStyleModel);
        } else {
            list.setModel(eStyleModel);
        }
        updated();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        manager.setStyleProvider(null);
        updated();
    }

    protected void switchStyles() {
        if (cb_compatibilityMode == null) {
            return;
        }
        manager.setCompatMode(cb_compatibilityMode.isSelected());
    }

    private void updated() {
        StyleProvider styleProvider = manager.getStyleProvider();
        if (providerMode && styleProvider == null) {
            providerMode = false;
            b_removeProvider.setVisible(false);
        } else if (!providerMode && styleProvider != null) {
            providerMode = true;
            b_removeProvider.setVisible(true);
        }

        if (currentStyle instanceof EdgeStyle && !bEdges.isSelected()) {
            bEdges.setSelected(true);
        } else if (currentStyle instanceof NodeStyle && !bNodes.isSelected()) {
            bNodes.setSelected(true);
        }

        if (cb_compatibilityMode != null) {
            cb_compatibilityMode.setSelected(manager.isCompatMode());
        }
    }

    @Override
    public final Component getComponent() {
        return this;
    }

    @Override
    public String getTitle() {
        return "Style";
    }

    @Override
    public boolean isActive(GraphSelection<?, ?> selection) {
        Collection nodes = selection.getSelectedNodes();
        Collection<Edge> edges = (Collection<Edge>)selection.getSelectedEdges();
        edit(nodes, edges);
        return true;
    }

	public void createStyle() {
		Style newStyle = null;
		if (selectedNodes != null) {
			newStyle = manager.addNodeStyle();
			copyStyle(newStyle);
            nStyleModel.refresh();
		} else if (selectedEdges != null) {
			newStyle = manager.addEdgeStyle();
			copyStyle(newStyle);
            eStyleModel.refresh();
		}
		setCurrentStyle(newStyle);
	}
	
	private void copyStyle(Style newStyle) {
		if (currentStyle.getClass() == newStyle.getClass() && currentStyle.getParent() != null) {
			String name = newStyle.getName();
			newStyle.copy(currentStyle);
			newStyle.setName(name);
		}
	}

	public void deleteStyle() {
        if (currentStyle == null) {
            return;
        }

        manager.deleteStyle(currentStyle);
        if (currentStyle instanceof NodeStyle) {
            setCurrentStyle(manager.getDefaultNodeStyle());
            nStyleModel.refresh();
        } else if (currentStyle instanceof EdgeStyle) {
            setCurrentStyle(manager.getDefaultEdgeStyle());
            eStyleModel.refresh();
        }
	}
}


class ListTableModel extends AbstractListModel {

    private final java.util.List data;

    ListTableModel(java.util.List data) {
        this.data = data;
    }

    @Override
    public int getSize() {
        return data.size();
    }

    @Override
    public Object getElementAt(int i) {
        return data.get(i);
    }

    public void refresh() {
        fireContentsChanged(this, 0, getSize());
    }
}

class StyleCellRenderer extends JPanel implements ListCellRenderer {

    private final JLabel name = new JLabel();

    private NodeStyle nstyle;
    private EdgeStyle estyle;

    public StyleCellRenderer() {
        super(new GridBagLayout());

        GridBagConstraints cst = new GridBagConstraints();
        cst.gridx = 1;
        cst.gridy = 1;
        cst.weightx = 1;
        cst.anchor = GridBagConstraints.WEST;
        cst.fill = GridBagConstraints.BOTH;
        cst.insets = new Insets(2,45, 2, 2);
        add(name, cst);
    }

    @Override
    public Component getListCellRendererComponent(JList jList, Object obj, int i, boolean selected, boolean focused) {
        if (!(obj instanceof Style)) {
            return this;
        }

        Style style = (Style)obj;
        if (selected) {
            setBackground(Color.BLUE);
            name.setForeground(Color.WHITE);
        } else {
            setBackground(Color.WHITE);
            name.setForeground(Color.BLACK);
        }

        name.setText(style.toString());
        this.nstyle = null;
        this.estyle = null;
        if (style instanceof NodeStyle) {
            this.nstyle = (NodeStyle)style;
        } else if (style instanceof EdgeStyle) {
            this.estyle = (EdgeStyle)style;
        }

        repaint();

        return this;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        int w = getSize().width;
        int h = getSize().height;
        g2.setColor(getBackground());
        g2.fillRect(0,0,w,h);

        w = 35;
        g2.setColor(Color.WHITE);
        g2.fillRect(1,1,w,h-2);

        if (nstyle != null) {
            Shape shape = nstyle.getNodeShape(null).getShape(2,2, w-4, h-4);
            g2.setColor(nstyle.getBackground(null));
            g2.fill(shape);
            g2.setColor(nstyle.getForeground(null));
            g2.draw(shape);

            g2.setColor(nstyle.getTextColor(null));
            g2.drawString("id", w/3, h-5);

        } else if (estyle != null) {
            g2.setColor(estyle.getColor(null));
            g2.drawLine(5,h/2, w-5, h/2);
        } else {
            g2.setColor(Color.RED);
            g2.drawString("?", 5, 2*h/3);
        }
    }
}