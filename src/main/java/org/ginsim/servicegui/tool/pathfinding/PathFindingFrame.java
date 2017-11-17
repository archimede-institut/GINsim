package org.ginsim.servicegui.tool.pathfinding;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import org.ginsim.common.application.Txt;
import org.ginsim.commongui.dialog.GUIMessageUtils;
import org.ginsim.commongui.utils.ImageLoader;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.GraphChangeType;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;


public class PathFindingFrame extends StackDialog implements ActionListener, ResultHandler {
	private static final long serialVersionUID = -7430762236435581864L;
	private Graph graph;
	private Container resultsPanel, progressionPanel;
	private JProgressBar progressBar;
	private JLabel progressionLabel;
	private JList pathList;
	private JButton colorizeButton;
	private JButton copyButton;
	private JScrollPane pathListScrollPane;
	private boolean isColorized = false;
	private List path;
	private PathStyleProvider selector;
	private JButton b_selectFromGraph;
	private JButton b_add;

    private JTable constraintTable;
    private ConstraintSelectionModel constraintModel;
	
	
	public PathFindingFrame( Graph graph) {
		super(GUIManager.getInstance().getFrame(graph), "pathFinding", 420, 260);
        setTitle("Search a path");
		this.setMinimumSize(new Dimension(300, 300));
		this.graph = graph;
        constraintModel = new ConstraintSelectionModel(graph);

        JPanel mainPanel = new javax.swing.JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(getGetSelectFromGraphButton(), c);
        
        c.gridx++;
        mainPanel.add(getAddButton(), c);

        c.gridx = 0;
        c.gridy++;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 5;
        c.fill = GridBagConstraints.BOTH;
        mainPanel.add(getNodeTable(), c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 5;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(new JSeparator(SwingConstants.HORIZONTAL), c);

        c.gridx = 0;
        c.gridy++;
        mainPanel.add(getProgressionPanel(), c);

        c.gridx = 0;
        c.gridy++;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        mainPanel.add(getResultPanel(), c);

        setMainPanel(mainPanel);
	}

	private Component getProgressionPanel() {
		if (progressionPanel == null) {
			progressionPanel = new JPanel();
			progressionPanel.setVisible(false);
			GridBagConstraints c = new GridBagConstraints();
			
			c.gridx = 0;
			c.gridy = 0;
			progressionPanel.add(getProgressionLabel(),c);
			c.gridx++;
			progressionPanel.add(getProgressBar(),c);
		}
		return progressionPanel;
	}

	private Component getResultPanel() {
		if (resultsPanel == null) {
			resultsPanel = new JPanel(new GridBagLayout());
			resultsPanel.setVisible(false);
			
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.gridheight = 3;
			c.weighty = 1;
			c.weightx = 1;
			c.fill = GridBagConstraints.BOTH;
			resultsPanel.add(getPathScrollPane(),c);

			c = new GridBagConstraints();
			c.gridx = 1;
			c.gridy = 0;
			resultsPanel.add(getColorizeButton(),c);
			
			c = new GridBagConstraints();
			c.gridx = 1;
			c.gridy = 1;
			resultsPanel.add(getCopyButton(),c);

		}
		return resultsPanel;
	}

	private Component getGetSelectFromGraphButton() {
		if (b_selectFromGraph == null) {
            b_selectFromGraph = new JButton(ImageLoader.getImageIcon("pick.png"));
            b_selectFromGraph.addActionListener(this);
            b_selectFromGraph.setToolTipText("get the selected node");
		}
		return b_selectFromGraph;
	}
	
	private Component getAddButton() {
		if (b_add == null) {
			b_add = new JButton(ImageLoader.getImageIcon("list-add.png"));
			b_add.addActionListener(this);
			b_add.setToolTipText("Add an intermediate");
		}
		return b_add;
	}

    private Component getNodeTable() {
        if (constraintTable == null) {
            constraintTable = new JTable(constraintModel);
        }
        return constraintTable;
    }

	private Component getCopyButton() {
		if (copyButton == null) {
			copyButton = new JButton(Txt.t("STR_pathFinding_copy"));
			copyButton.addActionListener(this);
		}
		return copyButton;
	}

	private Component getColorizeButton() {
		if (colorizeButton == null) {
			colorizeButton = new JButton(Txt.t("STR_do_colorize"));
			colorizeButton.addActionListener(this);
		}
		return colorizeButton;
	}

	private Component getPathScrollPane() {
		if (pathList == null) {
			pathList = new JList();
			pathList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			pathList.setLayoutOrientation(JList.VERTICAL);
			pathListScrollPane = new JScrollPane(pathList);
			pathListScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		}
		return pathListScrollPane;
	}

	private JLabel getProgressionLabel() {
		if (progressionLabel == null) {
			progressionLabel = new JLabel("");
		}
		return progressionLabel;
	}

	private Component getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar();
		}
		return progressBar;
	}

	protected void run() {
		if (isColorized  ) {
			undoColorize();
		}

        progressionPanel.setVisible(true);
        List nodes = constraintModel.getNodes();

        if (nodes == null) {
            setProgressionText("Search could not start! Invalid selection?");
            return;
        }

		setProgressionText("searching...");

        // TODO: support intermediate nodes
		Thread thread = new PathFinding(this, graph, nodes);
		thread.start();

	}
	
	public void setProgressionText(String text) {
		getProgressionLabel().setText(text);
	}
	
	public void setPath(List<Object> path) {
		this.path = path;
		if (path == null) {
			int r = 0;
			setProgressionText("There is no path between "+constraintModel.getValueAt(r, 1)+" and "+constraintModel.getValueAt(r+1, 1));
			resultsPanel.setVisible(false);
			return;
		}

		progressionPanel.setVisible(false);
		setProgressionText("Path found...");
		pathList.setListData(path.toArray(new Object[path.size()]));
		resultsPanel.setVisible(true);
		progressionPanel.setVisible(false);
		doColorize();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == colorizeButton) {
			if (isColorized  ) {
				undoColorize();
			} else {
				doColorize();
			}
		} else if (e.getSource() == copyButton) {
			Clipboard clipboard = getToolkit().getSystemClipboard();
			StringSelection data = new StringSelection(getPathAsText());
			clipboard.setContents(data, data);
		} else if (e.getSource() == b_selectFromGraph) {
			getSelectionFromGraph();
		} else if (e.getSource() == b_add) {
			constraintModel.add();
		}
	}
	
	private void getSelectionFromGraph() {
		GraphGUI<?, ?, ?> gui = GUIManager.getInstance().getGraphGUI(graph);
		Collection<?> selected = gui.getSelection().getSelectedNodes();
		if (selected == null || selected.size() < 1) {
			return;
		}

        // apply the selection
        constraintModel.setNode(selected.iterator().next(), constraintTable.getSelectedRows());
	}

	/**
	 * Transform the path list in a string like [elm0, elm1, elm2, ...]
	 * @return
	 */
	private String getPathAsText() {
		if (path.size() == 0) {
			return "[]";
		}
		StringBuffer s = new StringBuffer("[");
		for (Iterator iterator = path.iterator(); iterator.hasNext();) {
			s.append(iterator.next()+", ");
		}
		return s.substring(0, s.length()-2)+"]";
	}

	/**
	 * Search the node whose id correspond to the text in textField.
	 * If only one node is found, return it.
	 * Else display an error message.
	 * @param textField
	 * @return
	 */
	private Object getNode(JTextField textField) {
		
		String lookup = textField.getText();
		List foundNodes = graph.searchNodes( lookup);
		if (foundNodes == null) {
			GUIMessageUtils.openErrorDialog(Txt.t("STR_pathFinding_no_node")+textField.getText(), this);
			return null;
		} else if (foundNodes.size() == 1) {
			return foundNodes.get(0);
		} else if (foundNodes.size() > 1) {
			// roughly search for a perfect match
			for (Object cur: foundNodes) {
				if (cur.toString().equals( lookup)) {
					return cur;
				}
			}
			GUIMessageUtils.openErrorDialog(Txt.t("STR_pathFinding_too_much_nodes")+textField.getText(), this);
			return null;
		} else {
			GUIMessageUtils.openErrorDialog(Txt.t("STR_pathFinding_no_node")+textField.getText(), this);
			return null;
		}
	}

	private void doColorize() {
		if (path != null) {
			if (selector == null) {
				selector = new PathStyleProvider(graph.getStyleManager());
			}
			
			selector.setPath(path);
			graph.getStyleManager().setStyleProvider(selector);
		
			graph.fireGraphChange(GraphChangeType.GRAPHVIEWCHANGED, null);
			colorizeButton.setText(Txt.t("STR_undo_colorize"));
			isColorized = true;
		}
	}
	
	private void undoColorize() {
		graph.getStyleManager().setStyleProvider(null);
		colorizeButton.setText(Txt.t("STR_do_colorize"));
		isColorized = false;
	}

	public Graph getGraph() {
		
		return graph;
	}
	
	public void cancel() {
		undoColorize();
		super.cancel();
	}
}

class ConstraintSelectionModel extends AbstractTableModel {

    private final Graph graph;
    private final List<String> constraints = new ArrayList<String>(2);
    private final Map<String, Object> m_constraint2node = new HashMap<String, Object>();

    public ConstraintSelectionModel(Graph graph) {
        this.graph = graph;
        constraints.add("");
        constraints.add("");
    }

    @Override
    public int getRowCount() {
        return constraints.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (row >= getRowCount()) {
            return null;
        }

        if (col == 0) {
            if (row == 0) {
                return "start";
            }
            if (row == getRowCount()-1) {
                return "target";
            }
            return "through";
        }

        return constraints.get(row);
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        if (col == 0) {
            return false;
        }
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int row, int col) {
        if (row >= getRowCount()) {
            return;
        }
        if (col == 0) {
            return;
        }

        String old = constraints.get(row);
        String lookup = aValue.toString();
        constraints.set(row, lookup);

        if (!constraints.contains(old)) {
            m_constraint2node.remove(old);
        }
        findInGraph(lookup);

    }
    
    public void add() {
    	constraints.add(constraints.get(getRowCount()-1));
    	fireTableDataChanged();
    }

    private void findInGraph(String lookup) {
        if (m_constraint2node.containsKey(lookup)) {
            return;
        }

        List foundNodes = graph.searchNodes( lookup);
        if (foundNodes == null || foundNodes.size() < 1) {
            return;
        }

        m_constraint2node.put(lookup, foundNodes.get(0));
    }

    public List getNodes() {

        List nodes = new ArrayList();
        for (String s:constraints) {
            Object n = m_constraint2node.get(s);
            if (n == null) {
                return null;
            }
            nodes.add(n);
        }

        if (nodes.size() < 2) {
            return null;
        }

        return nodes;
    }

    public void setNode(Object node, int[] selectedRows) {
        String txt = node.toString();
        for (int i: selectedRows) {
            setValueAt(txt, i, 1);
        }
        fireTableDataChanged();
    }
}
