package fr.univmrs.tagc.GINsim.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

import fr.univmrs.tagc.GINsim.dynamicalHierachicalGraph.GsDynamicalHierarchicalGraph;
import fr.univmrs.tagc.GINsim.dynamicalHierachicalGraph.GsDynamicalHierarchicalNode;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;
import fr.univmrs.tagc.common.manageressources.Translator;
import fr.univmrs.tagc.common.widgets.SimpleDialog;


/**
 * This class provide a frame to search the nodes of a graph correponding to a certain pattern.
 * On a regular graph, search using the node toString() method
 * 
 * The nodes founds are displayed into a list supporting multiple selection. A button allow to select in the graph, the node selected in the list.
 *
 */
public class GsSearchFrame extends SimpleDialog {
	private static final long serialVersionUID = 381064983897248950L;

	private GsGraph g;
	
	private JPanel mainPanel;
	private JTextField searchTextField;
	private JButton searchButton;
	private JButton selectButton;
	private JTable table;
	private MyTableModel tableModel;
	
	public GsSearchFrame(GsMainFrame main) {
		super(main, Translator.getString("STR_searchNode"),300,400);
		this.g = main.getGraph();
        initialize();
	}
	
	private void initialize() {
		this.add(getMainPanel());
		this.setVisible(true);
	}

	private Component getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			searchTextField = new JTextField();
			searchTextField.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent arg0) {
				}
				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						search();
					}
				}
				public void keyTyped(KeyEvent arg0) {
				}
			});
			mainPanel.add(searchTextField, c);
			
			c.gridx++;
			c.weightx = 0;
			c.weighty = 0;
			searchButton = new JButton(Translator.getString("STR_searchNode_search"));
			searchButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					search();
				}
			});
			mainPanel.add(searchButton, c);
			
			c.gridx = 0;
			c.gridy++;
			c.gridwidth = 2;
			c.weightx = 1;
			c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			mainPanel.add(getTableInScrollPane(), c);
			
			c.gridy++;
			c.weightx = 0;
			c.weighty = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			selectButton = new JButton(Translator.getString("STR_searchNode_selectNodes"));
			selectButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					select();
				}
			});
			mainPanel.add(selectButton, c);
		}
		return mainPanel;
	}
	
	private Component getTableInScrollPane() {
		if (g instanceof GsDynamicalHierarchicalGraph) {
			tableModel = new MyTableModelForHierarchical(g.getNodeOrder().size());

		} else {
			tableModel = new MyTableModel();
		}
		table = new JTable(tableModel);
		table.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setColumnSelectionAllowed(false);
		
		JScrollPane scrollPane = new JScrollPane(table);		
		return scrollPane;
	}

	public void search() {
		tableModel.setData(new Vector());
		tableModel.setData(g.searchNodes(searchTextField.getText()));
	}

	public void select() {
		GsGraphManager gm = g.getGraphManager();
		List l = new Vector();
		int[] t = table.getSelectedRows();
		for (int i = 0; i < t.length; i++) {
			l.add(tableModel.getVertexAt(t[i]));
		}
		gm.select(l);
	}
		
	public void doClose() {
		this.setVisible(false);
	}

}

class MyTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -9107751311783717887L;
	private static Object[] tableHeaders = {Translator.getString("STR_node")};

	public List ldata;
	
	public MyTableModel() {
		super();
		ldata = new ArrayList();
	}
	
	public String getColumnName(int col) {
        return tableHeaders[col].toString();
    }
	
    public int getRowCount() { 
    	return ldata.size(); 
    }
    public int getColumnCount() { 
    	return tableHeaders.length; 
    }
    
    public Object getValueAt(int row, int col) {
        return ldata.get(row);
    }

    public Object getVertexAt(int row) {
        return ldata.get(row);
    }

    public boolean isCellEditable(int row, int col) { return false; }
    
    public void setData(List ldata) {
    	this.ldata = ldata;
    	fireTableDataChanged();
    }
}

class MyTableModelForHierarchical extends MyTableModel {
	public int nbNodes;
	private static final long serialVersionUID = -89665179425980671L;

	public MyTableModelForHierarchical(int nbNodes) {
		super();
		this.nbNodes = nbNodes;
	}

	public Object getValueAt(int row, int col) {
        return ((GsDynamicalHierarchicalNode)ldata.get(row)).toString(nbNodes);
    }
	
}
