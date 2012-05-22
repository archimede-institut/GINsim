package org.ginsim.gui.shell;

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
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.ginsim.common.application.Translator;
import org.ginsim.commongui.dialog.SimpleDialog;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;



/**
 * This class provide a frame to search the nodes of a graph correponding to a certain pattern.
 * On a regular graph, search using the node toString() method
 * 
 * The nodes founds are displayed into a list supporting multiple selection. A button allow to select in the graph, the node selected in the list.
 *
 */
public class SearchFrame extends SimpleDialog implements ListSelectionListener {
	private static final long serialVersionUID = 381064983897248950L;

	private static final int MAX_FOUND_NODES_DISPLAYED = 50;

	private GraphGUI<?, ?, ?> gui;
	private Graph<?,?> g;
	
	private JPanel mainPanel;
	private JTextField searchTextField;
	private JButton searchButton;
	private JTable table;
	private MyTableModel tableModel;
	private Timer autoFillTimer;
	
	public SearchFrame(GraphGUI<?, ?, ?> gui) {
		super(GUIManager.getInstance().getFrame(gui.getGraph()), Translator.getString("STR_searchNode_title"),300,400);
		this.gui = gui;
		this.g = gui.getGraph();
		this.autoFillTimer = new Timer(100, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				search();
			}
		});
        initialize();
	}
	
	private void initialize() {
		this.add(getMainPanel());
		this.setVisible(true);
		search();
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
					autoFillTimer.restart();
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
		}
		return mainPanel;
	}
	
	private Component getTableInScrollPane() {
		tableModel = new MyTableModel();
		table = new JTable(tableModel);
		table.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setColumnSelectionAllowed(false);
		table.getSelectionModel().addListSelectionListener(this);
		
		JScrollPane scrollPane = new JScrollPane(table);		
		return scrollPane;
	}

	public void search() {
		tableModel.setData(new Vector());
		List<?> foundNodes = g.searchNodes(searchTextField.getText());
		if (foundNodes.size() > MAX_FOUND_NODES_DISPLAYED) {
			foundNodes = (List<?>) foundNodes.subList(0, MAX_FOUND_NODES_DISPLAYED);
		}
		tableModel.setData(foundNodes);
	}

	public void selectNodes() {
		List l = new Vector();
		int[] t = table.getSelectedRows();
		for (int i = 0; i < t.length; i++) {
			l.add(tableModel.getNodeAt(t[i]));
		}
		gui.getSelection().setSelectedNodes(l);
	}
		
	public void doClose() {
		this.setVisible(false);
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		selectNodes();
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

    public Object getNodeAt(int row) {
        return ldata.get(row);
    }

    public boolean isCellEditable(int row, int col) { return false; }
    
    public void setData(List ldata) {
    	this.ldata = ldata;
    	fireTableDataChanged();
    }
}