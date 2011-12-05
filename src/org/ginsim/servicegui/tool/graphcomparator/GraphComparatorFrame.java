package org.ginsim.servicegui.tool.graphcomparator;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.ginsim.common.OptionStore;
import org.ginsim.common.utils.GUIMessageUtils;
import org.ginsim.core.exception.GsException;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.resource.Translator;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.gui.utils.dialog.stackdialog.AbstractStackDialogHandler;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;



public class GraphComparatorFrame  extends AbstractStackDialogHandler implements ActionListener {

	private static final long serialVersionUID = -5166645414957087984L;
	private JFrame frame;
	private Graph graph;
	private GraphComparator gc;

	private JComboBox g1_modelComboBox, g2_modelComboBox;
	private JTextField g1_filepath, g2_filepath;
	private JButton g1_chooseFileButton, g2_chooseFileButton;
	private JFileChooser jfc = null;
	private JCheckBox displayGraphCheckBox;

	private String[] comboBoxEntries = null;
	private List graphList = null;//List Of GsGrahp
	private boolean opt_display_graph;

	private final int GRAPH_TYPE_UNCOMPATIBLE = -1;
	private final int GRAPH_TYPE_NULL = 0;
	private final int GRAPH_TYPE_REGULATORY = 1;
	private final int GRAPH_TYPE_DYNAMIC = 2;

	public GraphComparatorFrame(JFrame parent, String id, int w, int h) {
		super();
	}

	public GraphComparatorFrame(JFrame frame, Graph graph) {
		this(frame, "graphComparator", 800, 600);
		this.graph = graph;
		this.frame = frame;
	}

	@Override
	public void init() {
		stack.setTitle(Translator.getString("STR_gcmp"));
		Dimension preferredSize = getPreferredSize();
		stack.setSize(preferredSize.width+20, preferredSize.height+20); //Padding 10px;

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		//Label
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		c.ipadx = 10;
		add(new JLabel(Translator.getString("STR_gcmp_ask")), c);

		int openerGraphIndex = comboBoxItems();
		c.gridy++;
		c.ipadx = 0;
		c.gridwidth = 1;

		//First graph selector
		c.gridx = 0;
		add(new JLabel("Graph 1 : "), c);

		c.gridx = 1;
		g1_modelComboBox = new JComboBox(comboBoxEntries);
		g1_modelComboBox.setSelectedIndex(openerGraphIndex);
		add(g1_modelComboBox, c);

		c.gridx = 2;
		g1_filepath = new JTextField(20);
		add(g1_filepath, c);

		c.gridx = 3;
		g1_chooseFileButton = new JButton(Translator.getString("STR_select_a_file")+"...");
		add(g1_chooseFileButton, c);
		g1_chooseFileButton.addActionListener(this);	

		//Second graph selector
		c.gridy++;
		c.gridx = 0;
		add(new JLabel("Graph 2 : "), c);

		c.gridx = 1;
		g2_modelComboBox = new JComboBox(comboBoxEntries);
		g2_modelComboBox.setSelectedIndex(openerGraphIndex);
		add(g2_modelComboBox, c);

		c.gridx = 2;
		g2_filepath = new JTextField(20);
		add(g2_filepath, c);

		c.gridx = 3;
		g2_chooseFileButton = new JButton(Translator.getString("STR_select_a_file")+"...");
		add(g2_chooseFileButton, c);
		g2_chooseFileButton.addActionListener(this);

		c.gridy++;
		c.gridx = 0;
		c.gridwidth = 3;
		add(new JLabel(Translator.getString("STR_gcmp_masterGraph")), c);

		c.gridy++;
		displayGraphCheckBox = new JCheckBox(Translator.getString("STR_gcmp_displayGraph"));
		displayGraphCheckBox.setSelected(true);
		add(displayGraphCheckBox, c);
	}

	private int comboBoxItems() {
		comboBoxEntries = new String[GraphManager.getInstance().getAllGraphs().size()+2];
		comboBoxEntries[0] = Translator.getString("STR_gcmp_from_file")+" :";
		comboBoxEntries[1] = " ";
		graphList = new ArrayList(GraphManager.getInstance().getAllGraphs().size());
		HashMap graphNames = new HashMap();
		int indexToSelect = 0;
		int i = 2;

		for (Iterator it = GraphManager.getInstance().getAllGraphs().iterator(); it.hasNext();) {

			Graph graph = (Graph) it.next();
			String name = graph.getGraphName();
			if (graph == this.graph) indexToSelect = i; 
			comboBoxEntries[i++] = name;
			graphList.add(graph);
			graphNames.put(name, graph);
			if (graphNames.containsKey(graph)) {
				GUIMessageUtils.openErrorDialog(new GsException(GsException.GRAVITY_INFO, Translator.getString("STR_gcmp_graphWithSameName"))+" : "+name, this.frame);
			}

		}

		return indexToSelect;
	}

	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == g1_chooseFileButton) {
				chooseFile(g1_filepath);
				g1_modelComboBox.setSelectedIndex(0);
			}else if (e.getSource() == g2_chooseFileButton) {
				chooseFile(g2_filepath);
				g2_modelComboBox.setSelectedIndex(0);
			}
		} catch (Exception ex) {
		}
	}

	@Override
	public void run() {

		Graph g1 = getGraph(g1_modelComboBox, g1_filepath);
		Graph g2 = getGraph(g2_modelComboBox, g2_filepath);		
		opt_display_graph = displayGraphCheckBox.getSelectedObjects() != null;//display the graph if displayGraphCheckBox is checked
		Graph g = null;

		int g_type= getGraphsType(g1, g2);
		switch (g_type) {
		case GRAPH_TYPE_UNCOMPATIBLE:
			GUIMessageUtils.openErrorDialog(new GsException(GsException.GRAVITY_ERROR, Translator.getString("STR_gcmp_graphFromDiffTypes")), this.frame);
			return;
		case GRAPH_TYPE_NULL:
			GUIMessageUtils.openErrorDialog(new GsException(GsException.GRAVITY_INFO, Translator.getString("STR_gcmp_graphNull")), this.frame);
			return;
		case GRAPH_TYPE_REGULATORY:
			g = GraphManager.getInstance().getNewGraph();
			if (opt_display_graph) {
				GUIManager.getInstance().newFrame(g);
			}
			gc = new RegulatoryGraphComparator(g1, g2, g);
			break;
		case GRAPH_TYPE_DYNAMIC:
			List nodeOrder = DynamicGraphComparator.getNodeOrder( (DynamicGraph) g1, (DynamicGraph) g2);
			if (nodeOrder != null) {
				g = GraphManager.getInstance().getNewGraph( DynamicGraph.class, nodeOrder);
				if (opt_display_graph) {
					GUIManager.getInstance().newFrame(g);
				}
				gc = new DynamicGraphComparator(g1, g2, g);
			} else {
				GUIMessageUtils.openErrorDialog("The node orders are different, therefore the comparaison would surelly not make senses.");
				stack.doClose();
				return;
			}
			break;
		}
		stack.doClose();
		if (opt_display_graph) {
			new GraphComparatorCaptionFrame( gc.getDiffGraph(), gc);
		} else {
			GUIManager.getInstance().whatToDoWithGraph(gc.getDiffGraph(), false);
		}
	}


	private int getGraphsType( Graph g1, Graph g2) {
		if (g1 == null || g2 == null) return GRAPH_TYPE_NULL;
		if (g1  instanceof RegulatoryGraph) {
			if (g2 instanceof RegulatoryGraph) 
				return GRAPH_TYPE_REGULATORY ;
		} else if ((g1  instanceof DynamicGraph) 	&& (g2 instanceof DynamicGraph)) 		return GRAPH_TYPE_DYNAMIC ;
		return GRAPH_TYPE_UNCOMPATIBLE;
	}

	public GraphComparator getGraphComparator() {
		return gc;
	}

	private Graph getGraph(JComboBox comboBox, JTextField filepath) {

		Graph g = null;
		int index = comboBox.getSelectedIndex() ;
		if (index == 0) {
			try{
				g = GraphManager.getInstance().open(new File(filepath.getText()));
			}
			catch( GsException ge){
				GUIMessageUtils.openErrorDialog(new GsException(GsException.GRAVITY_INFO, Translator.getString("STR_GraphNotOpened")), this.frame);
			}
		} else if (index == 1) { //has choose blank element
			GUIMessageUtils.openErrorDialog(new GsException(GsException.GRAVITY_INFO, Translator.getString("STR_gcmp_blankComboBox")), this.frame);
		} else {
			g = (Graph) graphList.get(index-2);
		}
		return g;
	}

	private void chooseFile(JTextField textField) throws IOException {
		int returnVal = getJfc().showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = jfc.getSelectedFile();
			OptionStore.setOption("currentDirectory", file.getParent());
			textField.setText(file.getCanonicalPath().toString());
		}
	}

	private JFileChooser getJfc() {
		File curDir = null;
		if (jfc != null) {
			curDir = jfc.getCurrentDirectory();
		} else {
			String path = (String)OptionStore.getOption("currentDirectory");
			if (path != null) {
				curDir = new File(path);
			}
		}
		if (curDir != null && !curDir.exists()) {
			curDir = null;
		}
		jfc = new JFileChooser(curDir);
		GsFileFilter ffilter = new GsFileFilter();
		ffilter.setExtensionList(new String[] {"ginml", "zginml"}, "(z)ginml files");
		jfc.setFileFilter(ffilter);
		return jfc;
	}
}
