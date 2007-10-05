package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor.model.GsFunctionEditorModel;
import java.util.Vector;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor.model.GsFunctionTerm;
import java.util.Arrays;

public class GsFunctionEditorWindow extends JDialog {
  class GsCellRenderer extends JLabel implements ListCellRenderer {
    private static final long	serialVersionUID	= 1720724468548717415L;
    public GsCellRenderer() {
      setOpaque(true);
    }
    public Component getListCellRendererComponent(JList list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
      setText(value.toString());
      setBackground(isSelected ? Color.pink : Color.white);
      return this;
    }
  }
  class GsInteractionListSelectionListener implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent e) {
      if (!e.getValueIsAdjusting()) {
        Object[] sel = interactionsList.getSelectedValues();
        if (sel.length > 0)
          controler.exec(GsFunctionEditorControler.MODIF_TERM, new Vector(Arrays.asList(sel)), editorModel.getCurrentTerm());
        else
          controler.exec(GsFunctionEditorControler.MODIF_TERM, null, editorModel.getCurrentTerm());
      }
    }
  }

  private JList interactionsList = new JList();
  private JButton andButton = new JButton("AND");
  private JButton orButton = new JButton("OR");
  private JButton notButton = new JButton("NOT");
  private JButton previousButton = new JButton("<<");
  private JButton nextButton = new JButton(">>");
  private JButton deleteButton = new JButton("Delete");
  private JCheckBox dnfCheckBox = new JCheckBox("DNF");
  private JCheckBox compactCheckBox = new JCheckBox("Compact");
  private JButton commandCancelButton = new JButton("Cancel");
  private JButton commandOKButton = new JButton("OK");
  private GsFunctionEditorControler controler;
  private GsFunctionEditorModel editorModel;
  private GsInteractionListSelectionListener interactionsListSelectionListener;

  public GsFunctionEditorWindow(GsFunctionEditorControler c, GsFunctionEditorModel m) {
    super();
    UIManager.put("ScrollBar.width", new Integer(12));
    controler = c;
    editorModel = m;
    init();
    initListeners();
    refresh();
    pack();
  }

  private void initListeners() {
    commandCancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        controler.exec(GsFunctionEditorControler.CANCEL);
        dispose();
      }
    });
    commandOKButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        controler.exec(GsFunctionEditorControler.OK);
        dispose();
      }
    });
    andButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        controler.exec(GsFunctionEditorControler.ADD_EMPTY_TERM, new Integer(GsFunctionTerm.AND));
        interactionsList.clearSelection();
      }
    });
    orButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        controler.exec(GsFunctionEditorControler.ADD_EMPTY_TERM, new Integer(GsFunctionTerm.OR));
      }
    });
    previousButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        controler.exec(GsFunctionEditorControler.PREVIOUS_TERM);
        refresh();
      }
    });
    nextButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        controler.exec(GsFunctionEditorControler.NEXT_TERM);
        refresh();
      }
    });
    notButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        controler.exec(GsFunctionEditorControler.NOT);
      }
    });
    interactionsListSelectionListener = new GsInteractionListSelectionListener();
    interactionsList.addListSelectionListener(interactionsListSelectionListener);
  }
  private void refresh() {
    interactionsList.removeListSelectionListener(interactionsListSelectionListener);
    interactionsList.setListData(editorModel.getInteractions());
    interactionsList.clearSelection();
    if (editorModel.getCurrentTermIndex() >= 0) {
      Vector v = editorModel.getCurrentTerm().getInteractions();
      for (int i = 0; i < v.size(); i++) {
        GsListInteraction in = (GsListInteraction)v.elementAt(i);
        int p = editorModel.getInteractions().indexOf(in);
        interactionsList.getSelectionModel().addSelectionInterval(p, p);
      }
    }
    interactionsList.addListSelectionListener(interactionsListSelectionListener);
  }
  private void init() {
    JPanel mainPane = new JPanel(new BorderLayout());
    mainPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    getContentPane().add(mainPane);
    JPanel listPanel = new JPanel();
    JPanel commandPanel = new JPanel();
    JScrollPane listScrollPane = new JScrollPane(interactionsList);
    JPanel listCommandPanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JPanel browsePanel = new JPanel();
    this.setModal(true);
    this.setResizable(false);
    this.setUndecorated(true);
    this.setAlwaysOnTop(true);
    listPanel.setLayout(new GridBagLayout());
    commandPanel.setLayout(new GridBagLayout());
    listScrollPane.setBorder(BorderFactory.createEtchedBorder(Color.white, UIManager.getColor("TextField.shadow")));
    listScrollPane.setPreferredSize(new Dimension(150, 150));
    interactionsList.setVisibleRowCount(7);
    interactionsList.setCellRenderer(new GsCellRenderer());
    andButton.setPreferredSize(new Dimension(38, 20));
    andButton.setMargin(new Insets(2, 2, 2, 2));
    orButton.setPreferredSize(new Dimension(30, 20));
    orButton.setMargin(new Insets(2, 2, 2, 2));
    notButton.setPreferredSize(new Dimension(37, 20));
    notButton.setMargin(new Insets(2, 2, 2, 2));
    previousButton.setPreferredSize(new Dimension(30, 20));
    previousButton.setMargin(new Insets(0, 2, 2, 2));
    previousButton.setMnemonic('0');
    nextButton.setPreferredSize(new Dimension(30, 20));
    nextButton.setMargin(new Insets(0, 2, 2, 2));
    deleteButton.setPreferredSize(new Dimension(50, 20));
    deleteButton.setMargin(new Insets(2, 2, 2, 2));
    commandCancelButton.setPreferredSize(new Dimension(51, 20));
    commandCancelButton.setMargin(new Insets(2, 2, 2, 2));
    commandOKButton.setPreferredSize(new Dimension(29, 20));
    commandOKButton.setMargin(new Insets(2, 2, 2, 2));
    listCommandPanel.setLayout(new GridBagLayout());
    mainPane.add(listPanel, java.awt.BorderLayout.WEST);
    mainPane.add(commandPanel, java.awt.BorderLayout.CENTER);
    buttonPanel.add(commandCancelButton);
    buttonPanel.add(commandOKButton);
    listPanel.add(listScrollPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
        GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));
    listPanel.add(listCommandPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    commandPanel.add(browsePanel, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
        GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    commandPanel.add(notButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
        GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
    listCommandPanel.add(previousButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
        GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    listCommandPanel.add(nextButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
        GridBagConstraints.NONE, new Insets(5, 0, 5, 20), 0, 0));
    listCommandPanel.add(deleteButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
    commandPanel.add(dnfCheckBox, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));
    commandPanel.add(compactCheckBox, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
    commandPanel.add(buttonPanel, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
        GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    commandPanel.add(andButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
        GridBagConstraints.NONE, new Insets(0, 5, 0, 2), 0, 0));
    commandPanel.add(orButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
        GridBagConstraints.NONE, new Insets(0, 0, 0, 2), 0, 0));
  }
}
