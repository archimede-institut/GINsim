package fr.univmrs.tagc.GINsim.gui.tbclient;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.DTreeNodeBuilder;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.DTreeTableBuilder;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree.*;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.table.DecoTreeTable;
import fr.univmrs.tagc.GINsim.jgraph.GsJgraphDirectedEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.common.widgets.GsButton;
import fr.univmrs.tagc.common.widgets.GsPanel;

public class GsInteractionsPanel extends GsPanel implements ItemListener, ActionListener {
	class InteractionsTableRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (column != 0) {
				setHorizontalAlignment(JLabel.CENTER);
				setForeground(Color.black);
				setFont(new Font("arial", Font.PLAIN, 12));
			}
			else {
				setHorizontalAlignment(JLabel.LEFT);
				setForeground(Color.green.darker().darker());
				setFont(new Font("courier", Font.BOLD, 11));
			}
			return this;
		}
	}
	class ToggleButtonListener implements ActionListener {
		private DTreeElementToggleButton node;

		public ToggleButtonListener() {
			super();
		}
		public void setNode(AbstractDTreeElement n) {
			node = (DTreeElementToggleButton)n;
		}
		public void actionPerformed(ActionEvent e) {
			boolean b = ((JToggleButton)e.getSource()).isSelected();
			node.setSelected(b);
			init(graph);
		}
	}
	class ButtonListener implements ActionListener {
		private DTreeElementButton node;
		private GsRegulatoryVertex srcVertex, dstVertex;

		public ButtonListener(GsRegulatoryVertex src, GsRegulatoryVertex dst) {
			super();
			srcVertex = src;
			dstVertex = dst;
		}
		public void setNode(AbstractDTreeElement n) {
			node = (DTreeElementButton)n;
		}
		public void actionPerformed(ActionEvent e) {
			Vector v = new Vector();
			v.addElement(srcVertex);
			v.addElement(dstVertex);
			clientPanel.updateGeneTree(v);
		}
	}
	private DecoTreeTable table;
	private ImageIcon in_on, show, out_on;
	private GsGraph graph;
	private GsTBClientPanel clientPanel;
	private JComboBox orgComboBox;
	private JCheckBox selectAllCheckBox;
	private GsButton applyButton;

	public GsInteractionsPanel(GsTBClientPanel p) {
		super();
		try {
			URL url = JFrame.class.getResource("/fr/univmrs/tagc/GINsim/ressources/icons/in_on.png");
			in_on = new ImageIcon(url);
			url = JFrame.class.getResource("/fr/univmrs/tagc/GINsim/ressources/icons/out_on.png");
			out_on = new ImageIcon(url);
			url = JFrame.class.getResource("/fr/univmrs/tagc/GINsim/ressources/icons/show.png");
			show = new ImageIcon(url);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		clientPanel = p;
		initGraphic();
		initListeners();
	}
	private void initGraphic() {
		GsPanel treePanel = new GsPanel();
		DTreeNodeBuilder nb = new DTreeNodeBuilder(true);
		DTreeTableBuilder tb = new DTreeTableBuilder(nb);
		AbstractDTreeElement node;

		tb.newTree(18);
		tb.newNode("Interactions", Color.black);
		nb.setNode();
		ToggleButtonListener al1 = new ToggleButtonListener();
		nb.addToggleButton(out_on, in_on, null, al1);
		node = nb.getNode();
		al1.setNode(node);
    tb.addNode(node);

		tb.addColumn("").addColumn("Sign").addColumn("Min").addColumn("Max");

		table = tb.getTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setDefaultRenderer(String.class, new InteractionsTableRenderer());
		table.setSelectionBackground(Color.yellow.brighter());
		JScrollPane sp = new JScrollPane(table);
		sp.getViewport().setPreferredSize(table.getPreferredSize());

		treePanel.addComponent(sp, 0, 0, 1, 1, 1.0, 1.0, GsPanel.NORTH, GsPanel.BOTH, 0, 0, 0, 0, 0, 0);

		GsPanel commandPanel = new GsPanel();
		commandPanel.setBorder(BorderFactory.createEtchedBorder());
		GsPanel p1 = new GsPanel();
		p1.addComponent(new JLabel("Organism :"), 0, 0, 1, 1, 0.0, 0.0, GsPanel.WEST, GsPanel.NONE, 0, 0, 0, 0, 0, 0);
		orgComboBox = new JComboBox();
		p1.addComponent(orgComboBox, 1, 0, 1, 1, 0.0, 0.0, GsPanel.WEST, GsPanel.NONE, 0, 5, 0, 0, 0, 0);
		commandPanel.addComponent(p1, 0, 0, 1, 1, 0.0, 0.0, GsPanel.CENTER, GsPanel.NONE, 2, 2, 0, 2, 0, 0);
		GsPanel p2 = new GsPanel();
		selectAllCheckBox = new JCheckBox("Select all", false);
		p2.addComponent(selectAllCheckBox, 0, 0, 1, 1, 0.0, 0.0, GsPanel.WEST, GsPanel.NONE, 0, 0, 0, 0, 0, 0);
		applyButton = new GsButton("Apply");
		p2.addComponent(applyButton, 1, 0, 1, 1, 0.0, 0.0, GsPanel.WEST, GsPanel.NONE, 0, 5, 0, 0, 0, 0);
		commandPanel.addComponent(p2, 0, 1, 1, 1, 0.0, 0.0, GsPanel.CENTER, GsPanel.NONE, 2, 2, 2, 2, 0, 0);
		addComponent(treePanel, 0, 0, 1, 1, 1.0, 1.0, NORTH, BOTH, 0, 0, 0, 0, 0, 0);
		addComponent(commandPanel, 0, 1, 1, 1, 1.0, 0.0, NORTH, HORIZONTAL, 0, 0, 0, 0, 0, 0);
	}
	public void initListeners() {
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Vector v = new Vector();
				v.addElement("stat1");
				v.addElement("socs1");
				v.addElement("tbx21");
				int i = clientPanel.getClient().getNbSignatures(v, "Homo sapiens");
				System.err.println(i);
			}
		});
	}
	public void init(GsGraph graph) {
		this.graph = graph;
		DTreeNodeBuilder nb = new DTreeNodeBuilder(true);
		DTreeTableBuilder tb = new DTreeTableBuilder(nb);
		ButtonListener al;
		AbstractDTreeElement node;
		tb.clearTree(table);
		node = nb.getNode();

		Iterator it = graph.getGraphManager().getVertexIterator();
		GsRegulatoryVertex vertex;
		GsRegulatoryMultiEdge edge;

		while (it.hasNext()) {
			vertex = (GsRegulatoryVertex)it.next();
			tb.newNode(vertex.getName(), Color.black);
			nb.setNode();
			tb.addNode(nb.getNode());
			if (!((DTreeElementToggleButton)node).isSelected()) {
				Iterator it2 = graph.getGraphManager().getOutgoingEdges(vertex).iterator();
				while (it2.hasNext()) {
					edge = (GsRegulatoryMultiEdge)((GsJgraphDirectedEdge)it2.next()).getUserObject();
					for (int k = 0; k < edge.getEdgeCount(); k++) {
						tb.newNode(edge.getTargetVertex().toString() + "   ", Color.black);
						al = new ButtonListener(vertex, (GsRegulatoryVertex)edge.getTargetVertex());
						if (vertex != edge.getTargetVertex())	{
							nb.addButton(show, null, al, null);
							nb.setSelectable(false, this);
							if (clientPanel.getClient() != null) {
								Vector v = new Vector();
								v.addElement(vertex.toString());
								v.addElement(edge.getTargetVertex().toString());
								int i = clientPanel.getClient().getNbSignatures(v, orgComboBox.getSelectedItem().toString());
								System.err.println(vertex.toString() + " -> " + edge.getTargetVertex().toString() + " : " + i);
							}
						}
						nb.addValue(GsRegulatoryMultiEdge.SIGN_SHORT[edge.getSign(k)], false);
						nb.addValue(Integer.valueOf(edge.getEdge(k).getMin()), false);
						nb.addValue(edge.getEdge(k).getMaxAsString(), false);
						tb.addNode(nb.getNode());
					}
				}
			}
			else {
				Iterator it2 = graph.getGraphManager().getIncomingEdges(vertex).iterator();
				while (it2.hasNext()) {
					edge = (GsRegulatoryMultiEdge)((GsJgraphDirectedEdge)it2.next()).getUserObject();
					for (int k = 0; k < edge.getEdgeCount(); k++) {
						tb.newNode(edge.getSourceVertex().toString() + "   ", Color.black);
						al = new ButtonListener((GsRegulatoryVertex)edge.getSourceVertex(), vertex);
						if (vertex != edge.getSourceVertex()) {
							nb.addButton(show, null, al, null);
							nb.setSelectable(false, this);
							if (clientPanel.getClient() != null) {
								Vector v = new Vector();
								v.addElement(vertex.toString());
								v.addElement(edge.getSourceVertex().toString());
								int i = clientPanel.getClient().getNbSignatures(v, orgComboBox.getSelectedItem().toString());
								System.err.println(edge.getSourceVertex().toString() + " -> " + vertex.toString() + " : " + i);
							}
						}
						nb.addValue(GsRegulatoryMultiEdge.SIGN_SHORT[edge.getSign(k)], false);
						nb.addValue(Integer.valueOf(edge.getEdge(k).getMin()), false);
						nb.addValue(edge.getEdge(k).getMaxAsString(), false);
						tb.addNode(nb.getNode());
					}
				}
			}
			tb.decreaseLevel();
		}
		tb.updateTree();
		tb.expandtree();
		table.getColumnModel().getColumn(0).setPreferredWidth(table.getWidth() - 105);
		table.getColumnModel().getColumn(1).setPreferredWidth(35);
		table.getColumnModel().getColumn(2).setPreferredWidth(35);
		table.getColumnModel().getColumn(3).setPreferredWidth(35);
	}
	public void actionPerformed(ActionEvent e) {
	}
	public void itemStateChanged(ItemEvent e) {
	}
	public void initOrganisms(Vector v) {
		v.insertElementAt("ALL", 0);
		orgComboBox.setModel(new DefaultComboBoxModel(v));
		if (graph != null) init(graph);
	}
}
