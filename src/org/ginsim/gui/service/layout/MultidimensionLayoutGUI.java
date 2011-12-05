package org.ginsim.gui.service.layout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.ginsim.common.ColorPalette;
import org.ginsim.common.utils.GUIMessageUtils;
import org.ginsim.exception.GsException;
import org.ginsim.graph.dynamicgraph.DynamicGraph;
import org.ginsim.gui.resource.Translator;
import org.ginsim.gui.utils.widgets.StockButton;
import org.ginsim.service.layout.DynamicLayoutMultidimention;
import org.ginsim.utils.log.LogManager;



public class MultidimensionLayoutGUI {

    private Color[] colorPalette;

	private MdLayoutTableModel tableModel;
	private JCheckBox straightEdges;
	private DynamicGraph graph;
	private JTable table;
	private JDialog frame;

    private void initGUI(JFrame parent) {
		frame = new JDialog();
		frame.setMinimumSize(new Dimension(400, 300));
		frame.setTitle(Translator.getString("STR_multidimention_placement"));
		
		List nodeOrder = graph.getNodeOrder();
    	initColorPalette(nodeOrder.size());
		tableModel = new MdLayoutTableModel(nodeOrder, colorPalette);
		table = new JTable(tableModel);
		table.getColumn(table.getColumnName(2)).setCellEditor(new RadioButtonEditor(new JCheckBox(), table));
		table.getColumn(table.getColumnName(1)).setCellRenderer(new SimpleRenderer());
		table.getColumn(table.getColumnName(2)).setCellRenderer(new SimpleRenderer());
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setMinimumSize(new Dimension(320, 240));
		//table.setFillsViewportHeight(true); //FIXME: find a replacement working in 1.4 (table.setFillsViewportHeight is 1.6)
		
		frame.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
	
		c.gridx = 0;
		c.gridy = 0;
        c.weightx = 2;
        c.weighty = 2;
		c.gridheight = 3;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.PAGE_START;
		frame.add(scrollPane, c);
		
		c.gridx++;
		c.weightx = 0;
		c.weighty = 0;
		c.gridheight = 1;
		StockButton upButton = new StockButton("go-up.png", true);
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
            	moveUp();
            }
        });
		frame.add(upButton, c);
        c.gridy++;
		StockButton downButton = new StockButton("go-down.png", true);
		downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
            	moveDown();
            }
        });
		frame.add(downButton, c);
        c.gridy++;
		frame.add(new JLabel(""), c);
		
		c.gridx = 0;
		c.gridy++;
		c.fill = GridBagConstraints.NONE;
		straightEdges = new JCheckBox(Translator.getString("STR_multidimention_placement_straight_edges"), false); 
		frame.add(straightEdges, c);
		
		c.gridy++;
		c.anchor = GridBagConstraints.EAST;
		JButton closeButton = new JButton(Translator.getString("STR_close"));
		closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
            	close();
            }
        });
		frame.add(closeButton, c);
		c.gridx++;
		JButton runButton = new JButton(Translator.getString("STR_run"));
		runButton.setMnemonic(KeyEvent.VK_R);
		runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
            	try{
            		run();
            	}
            	catch( GsException ge){
            		GUIMessageUtils.openErrorDialog( "Unable to launch layout");
            		LogManager.error( "Unable to launch layout");
            		LogManager.error( ge);
            	}
            }
        });
		frame.add(runButton, c);
		
		frame.setVisible(true);
	}
    
    protected void moveUp() {
    	tableModel.moveUp(table);
    }
    protected void moveDown() {
    	tableModel.moveDown(table);
    }
    protected void run() throws GsException{
    	new DynamicLayoutMultidimention(graph, tableModel.getNewNodeOrder(), straightEdges.isSelected(), colorPalette);
    }

    protected void close() {
    	frame.setVisible(false);
    	frame.dispose();
    	frame = null;
	}
	/**
	 * Create a color palette by variing the hue.
	 * @param n the count of color in the palette
	 */
    public void initColorPalette(int n) {
    	if (n <= ColorPalette.defaultPalette.length) {
    		colorPalette = ColorPalette.defaultPalette;
    		return;
    	}
    	colorPalette = new Color[n];
    	for (int i = 0; i < n ; i++) {
			colorPalette[i] = Color.getHSBColor((float)i/(float)n , 0.85f, 1.0f);
		}
    }

}


class MdLayoutTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -7055104154574749713L;
	private String[] columnNames = {"Node Order", "Gene name", "Pivot"};
	private byte[] newNodeOrder;
	private LinkedList nodeOrder;
	private List radioButtons;
	private Color[] colorPalette;
	private JLabel[] labels;

	public MdLayoutTableModel(List nodeOrder, Color[] colorPalette) {
		this.colorPalette = colorPalette;
		this.nodeOrder = new LinkedList(nodeOrder);
    	newNodeOrder = new byte[nodeOrder.size()];
    	labels = new JLabel[nodeOrder.size()];
    	radioButtons = new LinkedList();
    	ButtonGroup group = new ButtonGroup();
    	for (byte i = 0; i < nodeOrder.size(); i++) {
    		newNodeOrder[i] = i;
    		JRadioButton button = new JRadioButton();
    		button.setPreferredSize(new Dimension(20,20));
    		radioButtons.add(button);
    		group.add(button);
    		if (i == nodeOrder.size()/2) {
    			button.setSelected(true);
    		}
		}
	}
	
	public byte[] getNewNodeOrder() {
		return newNodeOrder;
	}

	public int getColumnCount() {
		return 3;
	}

	public int getRowCount() {
		return newNodeOrder.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return new Byte(newNodeOrder[rowIndex]);
		} else if (columnIndex == 1) {
			if (labels[rowIndex] == null) {
				labels[rowIndex] = new JLabel(nodeOrder.get(rowIndex).toString());
			}
			labels[rowIndex].setForeground(colorPalette[rowIndex]);
			labels[rowIndex].setOpaque(true);
			return labels[rowIndex];
		} else {
			return radioButtons.get(rowIndex);
		}
	}
	
	public boolean isCellEditable(int row, int col) {
		if (col == 2) return true;
		return false;
	}
	
	public String getColumnName(int col) {
		return columnNames[col];
	}
	
	public int getPivot() {
		int i = 0;
		for (Iterator it = radioButtons.iterator(); it.hasNext();i++) {
			JRadioButton rb = (JRadioButton) it.next();
			if (rb.isSelected()) return i;
		}
		return i;
	}

	public void moveUp(JTable table) {
		int[] index = table.getSelectedRows();
		int min = Math.min(index[0], index[index.length-1]);
		if (min == 0) return;
		for (int i=0;i<index.length;i++) {
            int a = index[i];
           	swapTableRow(a, a-1);
            index[i]=a-1;
        }
        DefaultListSelectionModel selectionModel = (DefaultListSelectionModel)table.getSelectionModel();
        selectionModel.clearSelection();
        int i=0;
        while (i<index.length) {
        	int max;
            min = index[i++];
            max = min;
            while (i<index.length) {
                if (index[i] == max+1) {
                    i++;
                    max++;
                } else {
                    break;
                }
            }
            selectionModel.addSelectionInterval(min, max);
        }
        LogManager.trace( "up");
	}
	public void moveDown(JTable table) {
		int[] index=table.getSelectedRows();
		int max = Math.max(index[0], index[index.length-1]);
		if (max == labels.length-1) return;
		for (int i=index.length-1;i>=0;i--) {
            int a = index[i];
            swapTableRow(a, a+1);
            index[i]=a+1;
        }
        DefaultListSelectionModel selectionModel = (DefaultListSelectionModel)table.getSelectionModel();
        selectionModel.clearSelection();
        int i=0;
        while (i<index.length) {
        	int min;
            min = index[i++];
            max = min;
            while (i<index.length) {
                if (index[i] == max+1) {
                    i++;
                    max++;
                } else {
                    break;
                }
            }
            selectionModel.addSelectionInterval(min, max);
        }
        LogManager.trace( "down");
	}

	private void swapTableRow(int source, int target) {
		byte b_nodeOrder = newNodeOrder[source]; 
		newNodeOrder[source] = newNodeOrder[target];
		newNodeOrder[target] = b_nodeOrder;
		
		JLabel l_nodeOrder = labels[source]; 
		labels[source] = labels[target];
		labels[target] = l_nodeOrder;
		
		Object s_nodeOrder = nodeOrder.get(source);
		nodeOrder.set(source, nodeOrder.get(target));
		nodeOrder.set(target, s_nodeOrder);
	}
	
}

/**
 * A small class to render the JRadioButtons in the table properly
 *
 */
class RadioButtonEditor extends DefaultCellEditor implements ItemListener {
	private static final long serialVersionUID = 7274363144656779860L;
	private JRadioButton button;
	private JTable table;
	
	public RadioButtonEditor(JCheckBox checkBox, JTable table) {
		super(checkBox);
		this.table = table;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (value == null) return null;
		button = (JRadioButton) value;
		button.addItemListener(this);
		return (Component) value;
	}
	
	public Object getCellEditorValue() {
		button.removeItemListener(this);
		return button;
	}
	
	public void itemStateChanged(ItemEvent e) {
		super.fireEditingStopped();
		table.repaint();
	}
	
}

class SimpleRenderer implements TableCellRenderer {
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (value == null) return null;
		Component c = (Component) value;
		if (isSelected) {
			c.setBackground(table.getSelectionBackground());
		} else {
			c.setBackground(table.getBackground());
		}
		return c;
	}
}
