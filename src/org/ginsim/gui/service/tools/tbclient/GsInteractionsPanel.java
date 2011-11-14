package org.ginsim.gui.service.tools.tbclient;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.table.DefaultTableCellRenderer;

import org.ginsim.graph.Graph;

import tbrowser.data.TBProbe;
import tbrowser.data.module.TBModuleData;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.DTreeNodeBuilder;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.DTreeTableBuilder;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree.AbstractDTreeElement;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree.DTreeElementButton;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree.DTreeElementToggleButton;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.table.DecoTreeTable;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.common.widgets.GsButton;
import fr.univmrs.tagc.common.widgets.GsPanel;

public class GsInteractionsPanel extends GsPanel implements ItemListener, ActionListener {
	private static final long serialVersionUID = 6871206109510696996L;
	class InteractionsTableRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 2148184469971239501L;

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
			init(graph, false);
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
			//v = (Vector)signatures.get(new SigKey(orgComboBox.getSelectedItem().toString(),	srcVertex.getName(), dstVertex.getName()));
			//clientPanel.setModuleList(v);
		}
	}
  class SigKey {
		private String organism, gene1, gene2;

		public SigKey(String org, String g1, String g2) {
			organism = org;
			gene1 = g1;
			gene2 = g2;
		}
		public String getOrganism() {
			return organism;
		}
		public String getGene1() {
			return gene1;
		}
		public String getGene2() {
			return gene2;
		}
		public boolean equals(Object o) {
			SigKey sk = (SigKey)o;
			boolean b = sk.getOrganism().equalsIgnoreCase(organism) &&
									((sk.getGene1().equalsIgnoreCase(gene1) && sk.getGene2().equalsIgnoreCase(gene2)) ||
									 (sk.getGene1().equalsIgnoreCase(gene2) && sk.getGene2().equalsIgnoreCase(gene1)));
			return b;
		}
		public int hashCode() {
			String s = organism + (gene1.compareToIgnoreCase(gene2) <= 0 ? gene1 + gene2 : gene2 + gene1);
			return s.hashCode();
		}
	}
	private DecoTreeTable table;
	private ImageIcon in_on, show, out_on;
	private Graph graph;
	private GsTBClientPanel clientPanel;
	private JComboBox orgComboBox;
	private JCheckBox selectAllCheckBox;
	private GsButton sigButton, resetButton, scoreButton;
	private Hashtable taxId, signatures;

	public GsInteractionsPanel(GsTBClientPanel p) {
		super();
		try {
			URL url = JFrame.class.getResource("/fr/univmrs/tagc/GINsim/resources/icons/in_on.png");
			in_on = new ImageIcon(url);
			url = JFrame.class.getResource("/fr/univmrs/tagc/GINsim/resources/icons/out_on.png");
			out_on = new ImageIcon(url);
			url = JFrame.class.getResource("/fr/univmrs/tagc/GINsim/resources/icons/show.png");
			show = new ImageIcon(url);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		clientPanel = p;
		signatures = new Hashtable();
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

		tb.addColumn("").addColumn("Sign").addColumn("Min").addColumn("Max").addColumn("Sign.").addColumn("Score");

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
		sigButton = new GsButton("Sig");
		p2.addComponent(sigButton, 1, 0, 1, 1, 0.0, 0.0, GsPanel.WEST, GsPanel.NONE, 0, 5, 0, 0, 0, 0);
		resetButton = new GsButton("Reset");
		p2.addComponent(resetButton, 2, 0, 1, 1, 0.0, 0.0, GsPanel.WEST, GsPanel.NONE, 0, 5, 0, 0, 0, 0);
		scoreButton = new GsButton("Score");
		p2.addComponent(scoreButton, 3, 0, 1, 1, 0.0, 0.0, GsPanel.WEST, GsPanel.NONE, 0, 5, 0, 0, 0, 0);
		commandPanel.addComponent(p2, 0, 1, 1, 1, 0.0, 0.0, GsPanel.CENTER, GsPanel.NONE, 2, 2, 2, 2, 0, 0);
		addComponent(treePanel, 0, 0, 1, 1, 1.0, 1.0, NORTH, BOTH, 0, 0, 0, 0, 0, 0);
		addComponent(commandPanel, 0, 1, 1, 1, 1.0, 0.0, NORTH, HORIZONTAL, 0, 0, 0, 0, 0, 0);
	}
	public void initListeners() {
		sigButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AbstractDTreeElement root = (AbstractDTreeElement)table.getTree().getModel().getRoot();
				AbstractDTreeElement node;
				Vector v;
				SigKey sk;
				String org, gene1, gene2;
				GsRegulatoryVertex v1, v2, tmp;
				int ns, w;

				table.clearSelection();
				org = orgComboBox.getSelectedItem().toString();
				for (int i = 0; i < root.getChildCount(); i++) {
					node = root.getChild(i);
					v1 = (GsRegulatoryVertex)node.getUserObject();
					gene1 = v1.getName();
					if (gene1.equals("")) gene1 = v1.getId();
					for (int j = 0; j < node.getChildCount(); j++)
						if (node.getChild(j).isSelected()) {
							v2 = (GsRegulatoryVertex)node.getChild(j).getUserObject();
							gene2 = v2.getName();
							if (gene2.equals("")) gene2 = v2.getId();
							sk = new SigKey(org, gene1, gene2);
							if (!signatures.containsKey(sk)) {
								v = new Vector();
								v.addElement(gene1);
								v.addElement(gene2);
								v = clientPanel.getClient().getSignatures(v, org);
								signatures.put(sk, v);
							}
							else
								v = (Vector)signatures.get(sk);
							if (((DTreeElementToggleButton)root).isSelected()) {
								tmp = v1;
								v1 = v2;
								v2 = tmp;
							}
							ns = 0;
							if (v.size() > 0) {
								ns = ((Vector) v.firstElement()).size();
								w = 1;
								if (ns >= 100)
									w = 10;
								else if (ns >= 50)
									w = 9;
								else if (ns >= 40)
									w = 8;
								else if (ns >= 30)
									w = 7;
								else if (ns >= 20)
									w = 6;
								else if (ns >= 15)
									w = 5;
								else if (ns >= 10)
									w = 4;
								else if (ns >= 5)
									w = 3;
								else if (ns >= 1)
									w = 2;
								clientPanel.applyEdgeStyle((GsRegulatoryMultiEdge)graph.getEdge(v1, v2), w);
							}
							node.getChild(j).getValues().setValueAt(3, (v == null ? "?" : v.size() == 0 ? "?" : String.valueOf(ns)), false);
						}
				}
				table.repaint();
			}
		});
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clientPanel.restoreEdgeStyle();
			}
		});
		scoreButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AbstractDTreeElement root = (AbstractDTreeElement)table.getTree().getModel().getRoot();
				AbstractDTreeElement node;
				Vector v = new Vector(), probes;
				float f;
				Enumeration enu;
				SigKey sk;
				String org, mod, symb, gene1, gene2;
				TBModuleData d;
				Hashtable h;
				float[][] data;

				org = orgComboBox.getSelectedItem().toString();
				for (int i = 0; i < root.getChildCount(); i++) {
					node = root.getChild(i);
					gene1 = ((GsRegulatoryVertex)node.getUserObject()).getName().toLowerCase();
					if (gene1.equals("")) gene1 = ((GsRegulatoryVertex)node.getUserObject()).getId().toLowerCase();
					for (int j = 0; j < node.getChildCount(); j++) {
						if (node.getChild(j).isSelected()) {
							gene2 = ((GsRegulatoryVertex)node.getChild(j).getUserObject()).getName().toLowerCase();
							if (gene2.equals("")) gene2 = ((GsRegulatoryVertex)node.getChild(j).getUserObject()).getId().toLowerCase();
							sk = new SigKey(org,	gene1, gene2);
							if (!signatures.containsKey(sk)) {
								v = new Vector();
								v.addElement(gene1);
								v.addElement(gene2);
								//mods = clientPanel.getClient().getSignatures(v, org);
								v = clientPanel.getClient().getSignatures(v, org);
								signatures.put(sk, /*mods*/v);
							}
							else
								v = (Vector)signatures.get(sk);
							h = (Hashtable)v.lastElement();
							v = (Vector)v.firstElement();
							enu = v.elements();
							System.err.println("*** " + gene1 + " " + gene2);
							while (enu.hasMoreElements()) {
								mod = (String)enu.nextElement();
								System.err.println("    " + mod);
								d = (TBModuleData)clientPanel.getClient().getModuleData(mod);
								probes = d.getProbes();
								data = d.getData();
								for (int k = 0; k < probes.size(); k++) {
									symb = ((TBProbe)probes.elementAt(i)).getGene().getSymbol().toLowerCase();
									if (k == 0) System.err.println(symb);
									if (symb.equalsIgnoreCase(gene1) || symb.equalsIgnoreCase(gene2)) {
										System.err.print("    " + symb + " ");
										for (int l = 0; l < d.getSamples().size(); l++) {
											f = data[k][l];
											if (l < 10) System.err.print(f + " ");
										}
										System.err.println("");
									}
								}
							}
						}
					}
				}
				table.clearSelection();
				table.repaint();
			}
		});
		selectAllCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				AbstractDTreeElement root = (AbstractDTreeElement)table.getTree().getModel().getRoot();
				AbstractDTreeElement node;

				for (int i = 0; i < root.getChildCount(); i++) {
					node = root.getChild(i);
					for (int j = 0; j < node.getChildCount(); j++) node.getChild(j).setCheckBoxSelected(selectAllCheckBox.isSelected());
				}
				table.clearSelection();
				table.repaint();
			}
		});
	}
	public void init( Graph graph, boolean resizeColumns) {
		this.graph = graph;
		DTreeNodeBuilder nb = new DTreeNodeBuilder(true);
		DTreeTableBuilder tb = new DTreeTableBuilder(nb);
		ButtonListener al;
		AbstractDTreeElement node;
		tb.clearTree(table);
		node = nb.getNode();

		Iterator it = graph.getVertices().iterator();
		GsRegulatoryVertex vertex;

		while (it.hasNext()) {
			vertex = (GsRegulatoryVertex)it.next();
			tb.newNode(vertex.getId(), Color.black);
			nb.setNode();
			nb.getNode().setUserObject(vertex);
			tb.addNode(nb.getNode());
			if (!((DTreeElementToggleButton)node).isSelected()) {
				Collection<GsRegulatoryMultiEdge> edges = graph.getOutgoingEdges(vertex);
				for (GsRegulatoryMultiEdge edge: edges) {
					for (int k = 0; k < edge.getEdgeCount(); k++) {
						tb.newNode(edge.getTarget().toString() + "   ", Color.black);
						if (vertex != edge.getTarget())	{
							al = new ButtonListener(vertex, (GsRegulatoryVertex)edge.getTarget());
							nb.addButton(show, null, al, null);
							nb.setSelectable(false, this);
						}
						nb.addValue(GsRegulatoryMultiEdge.SIGN_SHORT[edge.getSign(k)], false);
						nb.addValue(new Integer(edge.getEdge(k).getMin()), false);
						nb.addValue(edge.getEdge(k).getMaxAsString(), false);
						nb.addValue("?", false);
						nb.addValue("?", false);
						nb.getNode().setUserObject(edge.getTarget());
						tb.addNode(nb.getNode());
					}
				}
			}
			else {
				Collection<GsRegulatoryMultiEdge> edges = graph.getIncomingEdges(vertex);
				for (GsRegulatoryMultiEdge edge: edges) {
					for (int k = 0; k < edge.getEdgeCount(); k++) {
						tb.newNode(edge.getSource().toString() + "   ", Color.black);
						al = new ButtonListener((GsRegulatoryVertex)edge.getSource(), vertex);
						if (vertex != edge.getSource()) {
							nb.addButton(show, null, al, null);
							nb.setSelectable(false, this);
						}
						nb.addValue(GsRegulatoryMultiEdge.SIGN_SHORT[edge.getSign(k)], false);
						nb.addValue(new Integer(edge.getEdge(k).getMin()), false);
						nb.addValue(edge.getEdge(k).getMaxAsString(), false);
						nb.addValue("?", false);
						nb.addValue("?", false);
						nb.getNode().setUserObject(edge.getSource());
						tb.addNode(nb.getNode());
					}
				}
			}
			tb.decreaseLevel();
		}
		if (resizeColumns) resizeColumns();
		tb.updateTree();
		tb.expandtree();
	}
	public void resizeColumns() {
		table.getColumnModel().getColumn(0).setPreferredWidth(getWidth() - 190);
		table.getColumnModel().getColumn(1).setPreferredWidth(35);
		table.getColumnModel().getColumn(2).setPreferredWidth(35);
		table.getColumnModel().getColumn(3).setPreferredWidth(35);
		table.getColumnModel().getColumn(4).setPreferredWidth(35);
		table.getColumnModel().getColumn(5).setPreferredWidth(50);
	}
	public void actionPerformed(ActionEvent e) {
	}
	public void itemStateChanged(ItemEvent e) {

	}
	public void initOrganisms(Vector v) {
		Vector o = new Vector();
		String[] tok;
		o.addElement("ALL");
		taxId = new Hashtable();
		for (int i = 0; i < v.size(); i++) {
			tok = v.elementAt(i).toString().split("\t");
			o.addElement(tok[0]);
			taxId.put(tok[0], tok[1]);
		}
		orgComboBox.setModel(new DefaultComboBoxModel(o));
		if (graph != null) init(graph, true);
	}
}
