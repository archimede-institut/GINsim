package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
<<<<<<< TREE
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
=======
>>>>>>> MERGE-SOURCE
import javax.swing.JTree;

import fr.univmrs.ibdm.GINsim.global.GsEnv;
<<<<<<< TREE

public class GsTreeCellPanel extends JPanel implements ItemListener, ActionListener {
  private Font font = new Font("Monospaced", Font.PLAIN, 10);
=======
import java.awt.Dimension;
import javax.swing.JTextArea;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.JTextPane;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleContext;
import javax.swing.text.StyleConstants;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
import java.awt.FontMetrics;
import java.util.Hashtable;

public class GsTreeCellPanel extends JPanel implements ItemListener, ActionListener, KeyListener {
  private static Font defaultFont = new Font("monospaced", Font.PLAIN, 10);
  //private static Font geneFont = new Font("monospaced", Font.BOLD, 10);
  //private static Font interFont = new Font("monospaced", Font.BOLD, 10);
  //private static Font operatorFont = new Font("monospaced", Font.PLAIN, 10);
  //private static Font addedFont = new Font("monospaced", Font.BOLD | Font.ITALIC, 10);

  //private static Hashtable fontMetrics;

>>>>>>> MERGE-SOURCE
  private GsTreeElement treeElement;
  private JTree tree = null;
  private boolean selected, leaf;
  private JTextArea textArea;
  //private JTextPane textPane;
  private String text;
  //private static Style default_style, gene_style, inter_style, operator_style, added_style;
  private static int charWidth = 8, charHeight = 8;
  private int width = 0;

  /*static {
    fontMetrics = new Hashtable();
    StyleContext styleContext = StyleContext.getDefaultStyleContext();
    default_style = (StyleContext.NamedStyle)styleContext.getStyle(StyleContext.DEFAULT_STYLE);

    Style def = styleContext.addStyle("default", default_style);
    StyleConstants.setFontFamily(def, defaultFont.getFamily());
    StyleConstants.setFontSize(def, defaultFont.getSize());
    StyleConstants.setBold(def, defaultFont.isBold());
    StyleConstants.setItalic(def, defaultFont.isItalic());
    StyleConstants.setForeground(def, Color.black);

    gene_style = styleContext.addStyle("gene", default_style);
    StyleConstants.setFontFamily(gene_style, geneFont.getFamily());
    StyleConstants.setFontSize(gene_style, geneFont.getSize());
    StyleConstants.setBold(gene_style, geneFont.isBold());
    StyleConstants.setItalic(gene_style, geneFont.isItalic());
    StyleConstants.setForeground(gene_style, Color.black);

    inter_style = styleContext.addStyle("inter", default_style);
    StyleConstants.setFontFamily(inter_style, interFont.getFamily());
    StyleConstants.setFontSize(inter_style, interFont.getSize());
    StyleConstants.setBold(inter_style, interFont.isBold());
    StyleConstants.setItalic(inter_style, interFont.isItalic());
    StyleConstants.setForeground(inter_style, Color.blue);

    operator_style = styleContext.addStyle("operator", default_style);
    StyleConstants.setFontFamily(operator_style, operatorFont.getFamily());
    StyleConstants.setFontSize(operator_style, operatorFont.getSize());
    StyleConstants.setBold(operator_style, operatorFont.isBold());
    StyleConstants.setItalic(operator_style, operatorFont.isItalic());
    StyleConstants.setForeground(operator_style, Color.red);

    added_style = styleContext.addStyle("added", default_style);
    StyleConstants.setFontFamily(operator_style, addedFont.getFamily());
    StyleConstants.setFontSize(operator_style, addedFont.getSize());
    StyleConstants.setBold(operator_style, addedFont.isBold());
    StyleConstants.setItalic(operator_style, addedFont.isItalic());
    StyleConstants.setForeground(operator_style, Color.green);
  }*/

  public GsTreeCellPanel(Object value, boolean leaf, int row, JTree tree, boolean sel, boolean check, int w) {
    super();
    width = w;
    /*textPane = new JTextPane(){
      public Insets getInsets() {
        return new Insets(0, 0, 0, 0);
      }
    };*/
    text = value.toString();
    /*StyledDocument doc = textPane.getStyledDocument();

    doc.addStyle("default", default_style);
    doc.addStyle("gene", gene_style);
    doc.addStyle("inter", inter_style);
    doc.addStyle("operator", operator_style);

    try {
      doc.insertString(doc.getLength(), text, doc.getStyle("default"));
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }*/

    selected = sel;
    this.leaf = leaf;
    this.tree = tree;
    treeElement = (GsTreeElement)value;
    setLayout(new GridBagLayout());
    if (row > 0) {
      JCheckBox cb = new JCheckBox(){
        public Insets getInsets() {
          return new Insets(2, 2, 2, 2);
        }
      };
      cb.setSelected(check);
      cb.addItemListener(this);
      if (sel)
        cb.setBackground(Color.yellow);
      else if (value.toString().equals(""))
        cb.setBackground(Color.cyan);
      else
        cb.setBackground(Color.white);
      cb.setMargin(new Insets(0, 0, 0, 0));
      this.add(cb, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                          GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      if (!leaf) {
        JButton but = new JButton(GsEnv.getIcon("close.png")) {
          public Insets getInsets() {
            return new Insets(2, 2, 2, 2);
          }
        };
        but.addActionListener(this);
        this.add(but, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                             GridBagConstraints.NONE, new Insets(2, 0, 0, 0), 0, 0));
      }
    }
    textArea = new JTextArea(text);
    textArea.setFont(defaultFont);
    textArea.setEditable(true);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(false);
    this.add(textArea, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                              GridBagConstraints.NONE, new Insets(2, 5, 0, 0), 0, 0));
    /*textPane.setFont(defaultFont);
    textPane.setEditable(true);
    this.add(textPane, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                              GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));*/
    if (sel) {
      setBackground(Color.yellow);
      textArea.setBackground(Color.yellow);
      //textPane.setBackground(Color.yellow);
    }
    else {
      setBackground(Color.white);
      textArea.setBackground(Color.white);
      //textPane.setBackground(Color.white);
    }
    textArea.setForeground(treeElement.getForeground());
    //textPane.setForeground(treeElement.getForeground());

    if ((width >= 0) /*&& !fontMetrics.isEmpty()*/ && (charWidth > 0)) {
      int nbCols = width / charWidth;
      int nbRows = (text.length() + 1) / nbCols + 1;
      if (((text.length() + 1) % nbCols) == 0) nbRows--;
      textArea.setColumns(nbCols);
      textArea.setRows(nbRows);
      int ps = nbRows * charHeight;
      textArea.setPreferredSize(new Dimension(nbCols * charWidth, ps));
      //textPane.setPreferredSize(new Dimension(nbCols * charWidth, ps));
      //int maxRowHeight = ((FontMetrics)fontMetrics.get("default")).getHeight();

      /*int wtot = 0;
      String attributeName;
      for (int i = 0; i < doc.getLength(); i++) {
        attributeName = doc.getLogicalStyle(i).getName();
        try {
          wtot += ((FontMetrics) fontMetrics.get(attributeName)).charWidth(doc.getText(i, 1).charAt(0));
          maxRowHeight = Math.max(maxRowHeight, ((FontMetrics) fontMetrics.get(attributeName)).getHeight());
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      System.err.println(text);
      System.err.println("w = " + w + "   wtot = " + wtot + "   maxRowHeight = " + maxRowHeight);
      textPane.setPreferredSize(new Dimension(w, (wtot / w + 1) * (maxRowHeight + 1)));*/
      //setSize(new Dimension(w, (wtot / w + 1) * (maxRowHeight + 1)));
    }
    //else
    //  textPane.setPreferredSize(new Dimension(1, 50));
    textArea.addKeyListener(this);
    //textPane.addKeyListener(this);
  }
  public void paint(Graphics g) {
    charWidth = g.getFontMetrics(defaultFont).charWidth('A');
    charHeight = g.getFontMetrics(defaultFont).getHeight();
    /*if (fontMetrics.isEmpty()) {
      fontMetrics.put("default", g.getFontMetrics(defaultFont));
      fontMetrics.put("gene", g.getFontMetrics(geneFont));
      fontMetrics.put("inter", g.getFontMetrics(interFont));
      fontMetrics.put("operator", g.getFontMetrics(operatorFont));
      fontMetrics.put("added", g.getFontMetrics(addedFont));
    }*/
    super.paint(g);
    if (selected) {
      g.setColor(Color.blue);
      g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }
  }
  public void itemStateChanged(ItemEvent e) {
    boolean b = ((JCheckBox)e.getSource()).isSelected();
    treeElement.setSelected(b);
    for (int i = 0; i < treeElement.getChildCount(); i++) {
      treeElement.getChild(i).setSelected(b);
      for (int j = 0; j < treeElement.getChild(i).getChildCount(); j++)
        treeElement.getChild(i).getChild(j).setSelected(b);
    }
    if (tree != null) {
      ((GsTreeInteractionsModel)tree.getModel()).refreshVertex();
      tree.stopEditing();
      tree.repaint();
    }
  }
  public void actionPerformed(ActionEvent e) {
    treeElement.remove();
    //tree.collapseRow(0);
    ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged((GsTreeElement)tree.getModel().getRoot());
    ((GsTreeInteractionsModel)tree.getModel()).refreshVertex();
  }
  public Insets getInsets() {
    if (leaf) return new Insets(0, 0, 0, 0);
    return new Insets(3, 3, 3, 3);
  }
  public void keyPressed(KeyEvent e) {}
  public void keyReleased(KeyEvent e) {}
  public void keyTyped(KeyEvent e) {
    if (treeElement instanceof GsTreeExpression)
      if (System.getProperty("line.separator").charAt(0) == e.getKeyChar()) {
        try {
          textArea.getDocument().remove(textArea.getCaretPosition() - 1, 1);
          //textPane.getDocument().remove(textPane.getCaretPosition() - 1, 1);
          tree.stopEditing();
          ((GsTreeInteractionsModel)tree.getModel()).updateExpression((short)((GsTreeValue)treeElement.getParent()).getValue(), (GsTreeExpression)treeElement, textArea.getText());
          ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged((GsTreeElement)tree.getModel().getRoot());
          ((GsTreeInteractionsModel)tree.getModel()).refreshVertex();
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      else {
        text = textArea.getText();
        int nbCols = width / charWidth;
        int nbRows = (text.length() + 1) / nbCols + 1;
        if (((text.length() + 1) % nbCols) == 0) nbRows--;
        textArea.setRows(nbRows);
        int ps = nbRows * charHeight + 10;
        int ws = getSize().width;
        textArea.setPreferredSize(new Dimension(nbCols * charWidth, ps));
        setSize(new Dimension(ws, ps));
        textArea.repaint();
        invalidate();
        repaint();
        //System.err.println(textArea.getRows());
      }
  }
}
