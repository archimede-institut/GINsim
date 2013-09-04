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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import org.ginsim.common.application.Translator;
import org.ginsim.commongui.dialog.GUIMessageUtils;
import org.ginsim.commongui.utils.ImageLoader;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.GraphChangeType;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;


public class PathFindingFrame extends StackDialog implements ActionListener, ResultHandler {
	private static final long serialVersionUID = -7430762236435581864L;
	private Graph graph;
	private Container mainPanel, resultsPanel, progressionPanel;
	private JProgressBar progressBar;
	private JLabel progressionLabel;
	private JTextField startTextField;
	private JTextField endTextField;
	private JList pathList;
	private JButton colorizeButton;
	private JButton copyButton;
	private JScrollPane pathListScrollPane;
	private boolean isColorized = false;
	private List path;
	private PathStyleProvider selector;
	private JButton selectionForEndButton, selectionForStartButton;
	
	
	public PathFindingFrame( Graph graph) {
		super(GUIManager.getInstance().getFrame(graph), "pathFinding", 420, 260);
		this.setMinimumSize(new Dimension(300, 300));
		this.graph = graph;
        initialize();
    }

	public void initialize() {
		setMainPanel(getMainPanel());
	}
	
	private Container getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new javax.swing.JPanel();
			mainPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
		
			c.gridx = 0;
			c.gridy = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.ipadx = 10;
			c.gridwidth = 3;
			mainPanel.add(new JLabel(Translator.getString("STR_pathFinding")), c);
			
			c.gridx = 0;
			c.gridy++;			
			c.gridwidth = 1;
			mainPanel.add(new JLabel(Translator.getString("STR_pathFinding_start")), c);
			c.gridx++;
			c.weightx = 1;
			mainPanel.add(getStartField(), c);
			c.gridx++;
			c.weightx = 0;
			mainPanel.add(getGetSelectionForStartButton(), c);
			
			c.gridx = 0;
			c.gridy++;			
			c.weightx = 0;
			mainPanel.add(new JLabel(Translator.getString("STR_pathFinding_end")), c);
			c.gridx++;
			c.weightx = 1;
			mainPanel.add(getEndField(), c);
			c.gridx++;
			c.weightx = 0;
			mainPanel.add(getGetSelectionForEndButton(), c);
			
			c.gridx = 0;
			c.gridy++;			
			c.gridwidth = 3;
			c.weightx = 1;
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
		}
		return mainPanel;
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

	private Component getGetSelectionForEndButton() {
		if (selectionForEndButton == null) {
			selectionForEndButton = new JButton(ImageLoader.getImageIcon("undo.gif"));
			selectionForEndButton.addActionListener(this);
			selectionForEndButton.setToolTipText("get the selected node");
		}
		return selectionForEndButton;
	}

	private Component getGetSelectionForStartButton() {
		if (selectionForStartButton == null) {
			selectionForStartButton = new JButton(ImageLoader.getImageIcon("undo.gif"));
			selectionForStartButton.addActionListener(this);
			selectionForStartButton.setToolTipText("get the selected node");
		}
		return selectionForStartButton;
	}

	private Component getCopyButton() {
		if (copyButton == null) {
			copyButton = new JButton(Translator.getString("STR_pathFinding_copy"));
			copyButton.addActionListener(this);
		}
		return copyButton;
	}

	private Component getColorizeButton() {
		if (colorizeButton == null) {
			colorizeButton = new JButton(Translator.getString("STR_do_colorize"));
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

	private Component getEndField() {
		if (endTextField == null) {
			endTextField = new JTextField();
		}
		return endTextField;
	}

	private Component getStartField() {
		if (startTextField == null) {
			startTextField = new JTextField();
		}
		return startTextField;
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
		setProgressionText("searching...");
		setProgress(0);
		
		if (graph instanceof RegulatoryGraph) {
			setProgressMax(graph.getNodeOrderSize());
		} else {
			setProgressMax(graph.getNodes().size());
		}
		if (startTextField.getText().length() == 0) {
			GUIMessageUtils.openErrorDialog(Translator.getString("STR_pathFinding_start")+" "+Translator.getString("STR_isempty"), this);
			return;
		} else if (endTextField.getText().length() == 0) {
			GUIMessageUtils.openErrorDialog(Translator.getString("STR_pathFinding_end")+" "+Translator.getString("STR_isempty"), this);
			return;
		}
		Object start = getNode(startTextField); 
		Object end = getNode(endTextField);
		if (start == null || end == null) {
			return;
		}
		Thread thread = new PathFinding(this, graph, start, end);
		thread.start();
	}
	
	public void setProgress(int progress) {
		progressBar.setValue(progress);
	}
	protected void setProgressMax(int max) {
		progressBar.setMaximum(max);
	}
	public void setProgressionText(String text) {
		this.progressionLabel.setText(text);
	}
	
	public void setPath(Vector path) {
		this.path = path;
		pathList.setListData(path);
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
		} else if (e.getSource() == selectionForStartButton) {
			getSelectionFromGraph(startTextField);
		} else if (e.getSource() == selectionForEndButton) {
			getSelectionFromGraph(endTextField);
		}
	}
	
	private void getSelectionFromGraph(JTextField textField) {
		GraphGUI<?, ?, ?> gui = GUIManager.getInstance().getGraphGUI(graph);
		Collection<?> selected = gui.getSelection().getSelectedNodes();
		if (selected == null || selected.size() < 1) {
			return;
		}
		textField.setText(selected.iterator().next().toString());
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
			GUIMessageUtils.openErrorDialog(Translator.getString("STR_pathFinding_no_node")+textField.getText(), this);
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
			GUIMessageUtils.openErrorDialog(Translator.getString("STR_pathFinding_too_much_nodes")+textField.getText(), this);
			return null;
		} else {
			GUIMessageUtils.openErrorDialog(Translator.getString("STR_pathFinding_no_node")+textField.getText(), this);
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
			colorizeButton.setText(Translator.getString("STR_undo_colorize"));
			isColorized = true;
		}
	}
	
	private void undoColorize() {
		graph.getStyleManager().setStyleProvider(null);
		colorizeButton.setText(Translator.getString("STR_do_colorize"));
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
