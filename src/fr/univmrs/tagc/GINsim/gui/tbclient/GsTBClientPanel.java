package fr.univmrs.tagc.GINsim.gui.tbclient;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.univmrs.tagc.GINsim.graph.*;
import fr.univmrs.tagc.GINsim.gui.tbclient.genetree.*;
import fr.univmrs.tagc.GINsim.jgraph.GsJgraphDirectedEdge;
import tbrowser.data.module.*;
import tbrowser.ihm.widget.TBButton;
import tbrowser.ihm.widget.TBPanel;
import tbrowser.io.remote.client.TBClient;

public class GsTBClientPanel extends TBPanel implements GraphChangeListener, WindowListener {
  private TBPanel connexionPanel, queryPanel, /*resultsPanel,*/ resultsGenesPanel, resultsModulesPanel, resultsInfoPanel, infoPanel;
  private JTextField hostTextField;
  private TBButton openCloseButton;
  private JLabel portLabel;
  private TBClient client;
  private JProgressBar progressBar;
  private TBButton sendButton;
  private Socket socket = null;
  private GsTBSendThread t;
  private GsTBClientPanel instance;
  private JTree geneTree;
  private GeneTreeModel geneTreeModel;
  private Vector selectedGenes = null;
  private JList moduleList;
  private TBButton updateGenesInfoButton;
  private JTextField queryTextField;
  private JTable profileTable;

  public GsTBClientPanel() {
    super();
    initGraphic();
    initListeners();
    instance = this;
  }

  private void initGraphic() {
    connexionPanel = new TBPanel("Connexion");
    connexionPanel.addComponent(new JLabel("Host :"), 0, 0, 1, 1, 0.0, 0.0, EAST, NONE, 5, 5, 5, 0, 0, 0);
    hostTextField = new JTextField("localhost");
    connexionPanel.addComponent(hostTextField, 1, 0, 1, 1, 1.0, 0.0, EAST, HORIZONTAL, 5, 5, 5, 0, 0, 0);
    openCloseButton = new TBButton("Open");
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
    addComponent(connexionPanel, 0, 0, 1, 1, 1.0, 0.0, NORTHEAST, HORIZONTAL, 5, 5, 0, 0, 0, 0);

    queryPanel = new TBPanel("Query");
    updateGenesInfoButton = new TBButton("Update genes info");
    updateGenesInfoButton.setInsets(2, 3, 2, 3);
    queryPanel.addComponent(updateGenesInfoButton, 0, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
    queryTextField = new JTextField(30);
    queryPanel.addComponent(queryTextField, 1, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
    sendButton = new TBButton();
    resetSendButton();
    sendButton.setInsets(2, 5, 2, 5);
    sendButton.setEnabled(false);
    queryPanel.addComponent(sendButton, 2, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
    progressBar = new JProgressBar(0, 100);
    progressBar.setStringPainted(true);
    queryPanel.addComponent(progressBar, 3, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, 5, 5, 5, 5, 0, 0);
    addComponent(queryPanel, 0, 1, 2, 1, 1.0, 0.0, NORTHEAST, HORIZONTAL, 5, 5, 0, 5, 0, 0);

    JSplitPane resultsPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    resultsGenesPanel = new TBPanel("Genes");
    resultsGenesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), resultsGenesPanel.getTitle()));
    geneTreeModel = new GeneTreeModel(new TreeElementNode(new TreeElement("Nodes")));
    geneTree = new JTree(geneTreeModel);
    geneTree.setShowsRootHandles(true);
    GeneTreeCellRenderer gtcr = new GeneTreeCellRenderer();
    geneTree.setCellRenderer(gtcr);
    GeneTreeCellEditor gtce = new GeneTreeCellEditor(geneTree, gtcr);
    geneTree.setCellEditor(gtce);
    geneTree.setBackground(Color.white);
    geneTree.setEditable(true);

    resultsGenesPanel.addComponent(new JScrollPane(geneTree), 0, 0, 1, 1, 1.0, 1.0, WEST, BOTH, 5, 5, 5, 0, 0, 0);
    resultsPanel.add(resultsGenesPanel, JSplitPane.LEFT);
    resultsModulesPanel = new TBPanel("Modules");
    resultsModulesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), resultsModulesPanel.getTitle()));
    moduleList = new JList();
    moduleList.setFont(new Font("monospaced", Font.PLAIN, 12));
    JScrollPane jsp = new JScrollPane(moduleList);
    jsp.getViewport().setMinimumSize(new Dimension(800, 0));
    resultsModulesPanel.addComponent(jsp, 0, 0, 1, 1, 1.0, 1.0, WEST, BOTH, 5, 5, 5, 0, 0, 0);
    TBPanel moduleInfoPanel = new TBPanel();
    moduleInfoPanel.addComponent(resultsModulesPanel, 0, 0, 1, 1, 0.0, 1.0, WEST, BOTH, 5, 5, 5, 0, 0, 0);
    infoPanel = new GsTBInfoPanel();
    moduleInfoPanel.addComponent(infoPanel, 1, 0, 1, 1, 1.0, 1.0, WEST, BOTH, 5, 5, 5, 5, 0, 0);
    resultsPanel.add(moduleInfoPanel, JSplitPane.RIGHT);
    addComponent(resultsPanel, 0, 2, 1, 1, 1.0, 1.0, NORTHEAST, BOTH, 5, 5, 5, 5, 0, 0);
  }
  private void initListeners() {
    openCloseButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String s = hostTextField.getText();
        int ipadr = 20173;
        if (e.getActionCommand().equals("Open")) {
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
          }
          else
            JOptionPane.showMessageDialog(null, "Connexion refused !", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else if (e.getActionCommand().equals("Close")) {
          client.closeConnexion();
          client = null;
          portLabel.setText(" ");
          openCloseButton.setText("Open");
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
        Vector nodes;
        Hashtable h = new Hashtable();
        if (selectedGenes != null)
          for (int i = 0; i < selectedGenes.size(); i++) {
            nodes = (Vector)getClient().getGeneInfos(selectedGenes.elementAt(i).toString());
            h.put(selectedGenes.elementAt(i), nodes);
          }
        geneTreeModel.init(h);
        geneTreeModel.fireTreeStructureChanged((AbstractTreeElement)geneTreeModel.getRoot());
        sendButton.setEnabled(true);
      }
    });
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
    resultsModulesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
        resultsModulesPanel.getTitle() + " (" + m.getSize() + ")"));
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
