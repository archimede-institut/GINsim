package fr.univmrs.tagc.GINsim.gui.tbclient;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import fr.univmrs.tagc.GINsim.graph.*;
import fr.univmrs.tagc.GINsim.gui.tbclient.genetree.*;
import fr.univmrs.tagc.GINsim.jgraph.*;
import fr.univmrs.tagc.GINsim.regulatoryGraph.*;
import tbrowser.data.module.*;
import tbrowser.ihm.widget.*;
import tbrowser.io.remote.client.*;
import fr.univmrs.tagc.common.widgets.GsPanel;

public class GsTBClientPanel extends GsPanel implements GraphChangeListener, WindowListener {
  private TBPanel connexionPanel, queryPanel, infoPanel;
	private GsInteractionsPanel interactionsPanel;
  private JTextField hostTextField, queryTextField;
  private TBButton openCloseButton, sendButton, updateGenesInfoButton;
  private JLabel portLabel;
  private TBClient client;
  private JProgressBar progressBar;
  private Socket socket = null;
  private GsTBSendThread t;
  private GsTBClientPanel instance;
  private JTree geneTree;
  private GeneTreeModel geneTreeModel;
  private Vector selectedGenes = null;
  private JList moduleList;
  private GsRegulatoryGraph graph;
	private JTabbedPane toolsPane;

	private TBButton testButton = new TBButton("TEST");

  public GsTBClientPanel(GsGraph g) {
    super();
    initGraphic();
    initListeners();
    instance = this;
    graph = (GsRegulatoryGraph)g;
		interactionsPanel.init(graph);
  }

  private void initGraphic() {
		// Connexion panel
    connexionPanel = new TBPanel("Connexion");
    connexionPanel.addComponent(new JLabel("Host :"), 0, 0, 1, 1, 0.0, 0.0, EAST, NONE, 5, 5, 5, 0, 0, 0);
    hostTextField = new JTextField("localhost");
    connexionPanel.addComponent(hostTextField, 1, 0, 1, 1, 1.0, 0.0, EAST, HORIZONTAL, 5, 5, 5, 0, 0, 0);
    openCloseButton = new TBButton("Connect");
    openCloseButton.setInsets(2, 3, 2, 3);
    connexionPanel.addComponent(openCloseButton, 2, 0, 1, 1, 0.0, 0.0, EAST, NONE, 5, 20, 5, 0, 0, 0);
    connexionPanel.addComponent(new JLabel("Port :"), 3, 0, 1, 1, 0.0, 0.0, EAST, NONE, 5, 20, 5, 0, 0, 0);
    portLabel = new JLabel(" ");
    portLabel.setFont(new Font("dialog", Font.BOLD, 14));
    portLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.blue, 2),
        BorderFactory.createEmptyBorder(3, 3, 3, 3)));
    portLabel.setBackground(Color.white);
    portLabel.setForeground(Color.red);
    portLabel.setOpaque(true);
    portLabel.setPreferredSize(new Dimension(100, 25));
    portLabel.setHorizontalAlignment(JLabel.CENTER);
    connexionPanel.addComponent(portLabel, 4, 0, 1, 1, 0.0, 0.0, EAST, NONE, 5, 5, 5, 5, 0, 0);
    connexionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), connexionPanel.getTitle()));

		//geneTree
		AbstractTreeElement root = new TreeElement("Nodes");
		root = new TreeElementNode(root);
		geneTreeModel = new GeneTreeModel(root);
		geneTree = new JTree(geneTreeModel);
		GeneTreeCellRenderer renderer = new GeneTreeCellRenderer();
		GeneTreeCellEditor editor = new GeneTreeCellEditor(geneTree, renderer);
		geneTree.setCellEditor(editor);
		geneTree.setCellRenderer(renderer);
		geneTree.setRootVisible(true);
		geneTree.setShowsRootHandles(true);
    geneTree.setEditable(true);

		// Modules panel
		TBPanel modulesPanel = new TBPanel("Signatures");
		modulesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), modulesPanel.getTitle()));
		moduleList = new JList();
		moduleList.setFont(new Font("monospaced", Font.PLAIN, 12));
		JScrollPane jsp = new JScrollPane(moduleList);
		jsp.getViewport().setMinimumSize(new Dimension(80, 0));
    modulesPanel.addComponent(jsp, 0, 0, 1, 1, 1.0, 1.0, WEST, BOTH, 5, 5, 5, 0, 0, 0);

	  // infosPanel
		infoPanel = new GsTBInfoPanel(this);

	  // query panel
	  queryPanel = new TBPanel();
		updateGenesInfoButton = new TBButton("Update");
		queryPanel.addComponent(updateGenesInfoButton, 0, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
		queryPanel.addComponent(new JLabel("Query : "), 1, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
		queryTextField = new JTextField();
		queryPanel.addComponent(queryTextField, 2, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, 5, 5, 5, 0, 0, 0);
		sendButton = new TBButton();
		resetSendButton();
		sendButton.setInsets(2, 5, 2, 5);
		sendButton.setEnabled(false);
		queryPanel.addComponent(sendButton, 3, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);
		progressBar.setPreferredSize(new Dimension(100, 25));
		queryPanel.addComponent(progressBar, 4, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 5, 0, 0);

    // interactions panel
		interactionsPanel = new GsInteractionsPanel(this);

	  // toolsPanel
		toolsPane = new JTabbedPane();
		toolsPane.add("Query", queryPanel);
		toolsPane.add("Interactions", interactionsPanel);
		toolsPane.add("Genes", new JPanel());

	  // rightCommonPanel
		TBPanel rightCommonPanel = new TBPanel();
		rightCommonPanel.addComponent(modulesPanel, 0, 0, 1, 1, 0.0, 1.0, NORTH, VERTICAL, 0, 0, 0, 0, 0, 0);
		rightCommonPanel.addComponent(infoPanel, 1, 0, 1, 1, 1.0, 1.0, WEST, BOTH, 0, 0, 0, 0, 0, 0);

	  // common panel
		JSplitPane commonPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		commonPane.add(new JScrollPane(geneTree), JSplitPane.LEFT);
		commonPane.add(rightCommonPanel, JSplitPane.RIGHT);

	  // main panel
		TBPanel mainPane = new TBPanel();
		mainPane.addComponent(connexionPanel, 0, 0, 1, 1, 1.0, 0.0, NORTH, HORIZONTAL, 0, 0, 0, 0, 0, 0);
		mainPane.addComponent(commonPane, 0, 1, 1, 1, 1.0, 1.0, NORTH, BOTH, 0, 0, 0, 0, 0, 0);

    // root panel
		JSplitPane rootPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    rootPanel.add(mainPane, JSplitPane.RIGHT);
		rootPanel.add(toolsPane, JSplitPane.LEFT);

		addComponent(rootPanel, 0, 0, 1, 1, 1.0, 1.0, NORTHEAST, BOTH, 0, 0, 0, 0, 0, 0);
  }
  private void initListeners() {
    openCloseButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String s = hostTextField.getText();
        int ipadr = 20173;
        if (e.getActionCommand().equals("Connect")) {
        	if (hostTextField.getText().indexOf(':') != -1) {
        		s = hostTextField.getText().substring(0, hostTextField.getText().indexOf(':'));
        		ipadr = Integer.decode(hostTextField.getText().substring(hostTextField.getText().indexOf(':') + 1)).intValue();
        	}
        	client = new TBClient(s, ipadr);
          if (client.openConnexion()) {
            client.setProgressBar(progressBar);
            portLabel.setText(String.valueOf(client.getClientPort()));
            openCloseButton.setText("Close");
            hostTextField.setEditable(false);
            updateGenesInfoButton.setEnabled(true);
						interactionsPanel.initOrganisms(client.getOrganisms());
          }
          else
            JOptionPane.showMessageDialog(null, "Connexion refused !", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else if (e.getActionCommand().equals("Close")) {
          client.closeConnexion();
          client = null;
          portLabel.setText(" ");
          openCloseButton.setText("Connect");
          hostTextField.setEditable(true);
          updateGenesInfoButton.setEnabled(false);
          sendButton.setEnabled(false);
        }
      }
    });
    sendButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Send query")) {
          t = new GsTBSendThread(progressBar, instance);
          sendButton.setText("Abort");
          t.start();
        }
        else if (e.getActionCommand().equals("Abort")) {
          try {
            t.kill();
            client.kill();
            socket = new Socket(hostTextField.getText(), 20173);
            PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            writer.println("k" + portLabel.getText());
            writer.flush();
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
          resetSendButton();
        }
      }
    });
    moduleList.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent listSelectionEvent) {
        if (!listSelectionEvent.getValueIsAdjusting()) {
          if (moduleList.getSelectedValues().length == 1) {
            TBModule mod = (TBModule)moduleList.getSelectedValue();
            if (!mod.isDataLoaded()) {
              TBModuleData d = (TBModuleData)client.getModuleData(mod.getName());
              mod.setData(d);
            }
            ((GsTBInfoPanel)infoPanel).initModule(mod, geneTreeModel.getGeneSymbols());
          }
          else
            ((GsTBInfoPanel)infoPanel).clear();
        }
      }
    });
    updateGenesInfoButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      	updateGeneTree(selectedGenes);
      }
    });
		/*testButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Vector v_plugins = getClient().getAvailablePlugins();
				Vector v_functions;
				String pluginName, functionName;
				Object o;
				Vector v_params = new Vector();
				v_params.addElement("GPL96");
				v_params.addElement("GATA3");
				for (Enumeration enu_plugins = v_plugins.elements(); enu_plugins.hasMoreElements(); ) {
					pluginName = enu_plugins.nextElement().toString();
					System.err.println(pluginName);
					v_functions = getClient().getPluginFunctions(pluginName);
					for (Enumeration enu_functions = v_functions.elements(); enu_functions.hasMoreElements(); ) {
						functionName = enu_functions.nextElement().toString();
						System.err.println("  " + functionName);
						o = getClient().callPlugin(pluginName, functionName, v_params);
						System.err.println("    " + o.getClass() + " : " + o.toString());
						if (o instanceof String[]) {
							String[] s = (String[]) o;
							for (int i = 0; i < 10; i++) System.err.println("      " + s[i]);
						}
						else if (o instanceof String)
							System.err.println("      " + o);
					}
				}
			}
		});*/
  }
  public void closeTBConnexion() {
    if (client != null) client.closeConnexion();
  }
  public TBClient getClient() {
    return client;
  }
  public void resetSendButton() {
    sendButton.setText("Send query");
  }
  public void clearResults() {

  }
  public void setQuery(String q) {
    queryTextField.setText(q);
  }
  public void setModuleList(TBModules m) {
    moduleList.setListData(m.getModules());
  }
  public void setGenes(Vector v) {
    selectedGenes = v;
  }
  public Vector getSelectedGenes() {
    return selectedGenes;
  }
  public GeneTreeModel getGeneTreeModel() {
  	return geneTreeModel;
  }

  public void graphChanged(GsNewGraphEvent event) {
		graph = (GsRegulatoryGraph)event.getNewGraph();
		interactionsPanel.init(graph);
  }

  public void graphSelectionChanged(GsGraphSelectionChangeEvent event) {
    Vector v_edge = event.getV_edge();
    Vector v_vertex = event.getV_vertex();

    Vector v = new Vector();
    for (int i = 0; i < v_vertex.size(); i++)
      if (!v.contains(v_vertex.elementAt(i)))
        v.addElement(v_vertex.elementAt(i));
    for (int i = 0; i < v_edge.size(); i++) {
      GsJgraphDirectedEdge e = (GsJgraphDirectedEdge) v_edge.elementAt(i);
      if (!v.contains(e.getSourceVertex()))
        v.addElement(e.getSourceVertex());
      if (!v.contains(e.getTargetVertex()))
        v.addElement(e.getTargetVertex());
    }
    setGenes(v);
    if (v.size() > 0)
    	graph = ((GsRegulatoryVertex)v.firstElement()).getInteractionsModel().getGraph();
    else
    	graph = null;
  }
  public GsRegulatoryGraph getGraph() {
  	return graph;
  }
	public void updateGeneTree(Vector v) {
		GsRegulatoryVertex vertex;
		Vector par;
		Hashtable genes = new Hashtable();
		if (v != null)
			for (int i = 0; i < v.size(); i++) {
				vertex = (GsRegulatoryVertex)v.elementAt(i);
				par = (Vector)getClient().getGeneInfos(v.elementAt(i).toString());
				genes.put(vertex, par);
			}
		geneTreeModel.init(genes);
		geneTreeModel.fireTreeStructureChanged((AbstractTreeElement)geneTreeModel.getRoot());
		sendButton.setEnabled(true);
	}
  public void graphClosed(GsGraph graph) {
  }

  public void updateGraphNotificationMessage(GsGraph graph) {
  }
  public void windowOpened(WindowEvent windowEvent) {
  }

  public void windowClosing(WindowEvent windowEvent) {
    closeTBConnexion();
  }

  public void windowClosed(WindowEvent windowEvent) {
  }

  public void windowIconified(WindowEvent windowEvent) {
  }

  public void windowDeiconified(WindowEvent windowEvent) {
  }

  public void windowActivated(WindowEvent windowEvent) {
  }

  public void windowDeactivated(WindowEvent windowEvent) {
  }
}
