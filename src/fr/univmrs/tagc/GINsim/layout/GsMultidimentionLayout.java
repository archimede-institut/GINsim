package fr.univmrs.tagc.GINsim.layout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
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

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.dynamicGraph.GsDynamicGraph;
import fr.univmrs.tagc.GINsim.dynamicGraph.GsDynamicNode;
import fr.univmrs.tagc.GINsim.graph.GsActionProvider;
import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.plugin.GsPlugin;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.common.ColorPalette;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.manageressources.Translator;
import fr.univmrs.tagc.common.widgets.StockButton;

public class GsMultidimentionLayout implements GsPlugin, GsActionProvider {
    private static final int MULTIDIMENTION = 1;
    
    private Color[] colorPalette;

    private static final int padx = 25;
    private static final int pady = 25;
   
    private int width;
	private int height;
    
    private int pivot;
   
    
    private GsPluggableActionDescriptor[] t_layout = {
		new GsPluggableActionDescriptor("STR_multidimention_placement", "STR_multidimention_placement_descr", null, this, ACTION_LAYOUT, MULTIDIMENTION),
    };
	private GsEdgeAttributesReader ereader;
    private GsVertexAttributesReader vreader;
	private byte[] newNodeOrder;
	private MdLayoutTableModel tableModel;
	private GsDynamicGraph graph;
	private JTable table;
	private JDialog frame;

	private JCheckBox straightEdges;

	private boolean useStraightEdges;
	
    public void registerPlugin() {
        GsGraph.registerLayoutProvider(this);
    }

    public GsPluggableActionDescriptor[] getT_action(int actionType, GsGraph graph) {
        if (actionType != ACTION_LAYOUT || !(graph instanceof GsDynamicGraph)) {
            return null;
        }
        return t_layout;
    }

    public void runAction(int actionType, int ref, GsGraph graph, JFrame parent) throws GsException {
        if (actionType != ACTION_LAYOUT) {
            return;
        }
        this.graph = (GsDynamicGraph)graph;
        initGUI(parent);
        
    }
    
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
            	run();
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
    protected void run() {
    	pivot = tableModel.getPivot();
    	newNodeOrder = tableModel.getNewNodeOrder();
    	useStraightEdges = straightEdges.isSelected();
    	runLayout();
    }
    protected void close() {
    	frame.setVisible(false);
    	frame.dispose();
    	frame = null;
	}

	public void runLayout() {
        //Check if it is a DynamicGraph
        GsGraphManager gmanager = graph.getGraphManager();
		Iterator it = gmanager.getVertexIterator();
		Object v = it.next();
	    if (v == null || !(v instanceof GsDynamicNode)) {
			System.out.println("wrong type of graph for this layout");
	    	return;
	    }
		vreader = gmanager.getVertexAttributesReader();
		ereader = gmanager.getEdgeAttributesReader();
	    byte[] maxValues = getMaxValues(((GsDynamicGraph)graph).getAssociatedGraph().getNodeOrder());
	    
	    //move the nodes
	    GsDynamicNode vertex = (GsDynamicNode)v;
	    vreader.setVertex(vertex);
	    this.width = vreader.getWidth() + padx*maxValues.length/2;
	    this.height = vreader.getHeight() + pady*maxValues.length/2;	   
	    
	    do {
	    	moveVertex(vertex, maxValues);
		    vertex = (GsDynamicNode)it.next();
		} while (it.hasNext());
    	moveVertex(vertex, maxValues);
    	
    	//move the edges
    	it = gmanager.getEdgeIterator();
    	while (it.hasNext()) {
    		GsDirectedEdge edge = (GsDirectedEdge) it.next();
    		moveEdge(edge, maxValues);
    	}
    }
	
	/**
	 * Move the vertex to its correct position.
	 * @param vertex
	 * @param maxValues
	 */
	private void moveVertex(GsDynamicNode vertex, byte[] maxValues) {
	    vreader.setVertex(vertex);
    	byte[] state = vertex.state;
       	int x = 0;
    	int dx = 1;
    	for (int i = 0; i < pivot; i++) {
			x += getState(state, i)*dx;
			dx *= maxValues[i];
		}
    	int y = 0;
    	int dy = 1;
    	for (int i = pivot; i < maxValues.length; i++) {
			y += getState(state, i)*dy;
			dy *= maxValues[i];
		}
	    vreader.setPos(5+x*width, 5+y*height);
        vreader.refresh();		
	}
	
	/**
	 * Move an edge and set the proper style.
	 * @param edge
	 * @param maxValues
	 */
	private void moveEdge(GsDirectedEdge edge, byte[] maxValues) {
		byte[] diffstate = getDiffStates((GsDynamicNode)edge.getSourceVertex(), (GsDynamicNode)edge.getTargetVertex());
		int change = get_change(diffstate);
		
		ereader.setEdge(edge);
	   	List points = ereader.getPoints();
		Point2D first, p1, p2, last;
		first = (Point2D)points.get(0);
		last =  (Point2D)points.get(points.size()-1);
		p1 =(Point2D) first.clone();
		p2 = null;
		double dx, dy;
		double pad = 25;

		if (useStraightEdges) {
			dx = get_dx(diffstate, maxValues, 0);
			dy = get_dy(diffstate, maxValues, 0);
			if (dx > 0 && dy > 0 ) { //the edge is diagonal
				dx = get_dx(diffstate, maxValues, 1);
				dy = get_dy(diffstate, maxValues, 1);
				p1.setLocation(first.getX()+(last.getX()-first.getX())/2+dy*pad, first.getY()+(last.getY()-first.getY())/2+dx*pad);
			} else {
				p2 =(Point2D) last.clone();
				int w = vreader.getWidth();
				int h = vreader.getHeight();
				p1.setLocation(first.getX()+gap(dx, dy, w, h), first.getY()+gap(dy, dx, h, w));
				p2.setLocation( last.getX()+gap(dx, dy, w, h),  last.getY()+gap(dy, dx, h, w));
			}
		} else {
			dx = get_dx(diffstate, maxValues, 1);
			dy = get_dy(diffstate, maxValues, 1);
			p1.setLocation(p1.getX()+(last.getX()-p1.getX())/2+dy*pad, p1.getY()+(last.getY()-p1.getY())/2+dx*pad);
		}
		points = new LinkedList();
    	points.add(first);
    	points.add(p1);
		if (p2 != null) points.add(p2);
    	points.add(last);
		ereader.setPoints(points);
		
		ereader.setLineColor(colorPalette[change]);
		ereader.setLineWidth(reduceChange(change)*0.5f+1.5f);
		ereader.setRouting(GsEdgeAttributesReader.ROUTING_NONE);
		if (p2 != null) {
			ereader.setStyle(GsEdgeAttributesReader.STYLE_STRAIGHT);
		} else {
			ereader.setStyle(GsEdgeAttributesReader.STYLE_CURVE);
		}
		ereader.refresh();
	}

    /**
     * Compute the gap in a complex way for straight edges
     * @param d_main
     * @param d_orth
     * @param size_main
     * @param size_orth
     * @return
     */
    private  double gap(double d_main, double d_orth, int size_main, int size_orth) {
		return size_main/1.75*(d_orth>0?1:0)+d_orth*12+d_main*3-size_orth/4;
	}

	/**
     * return the coordinate of the first change between the two states.
     * @param diffstate
     * @return
     */
	private int get_change(byte[] diffstate) {
    	for (int i = 0; i < diffstate.length; i++) {
    		if (diffstate[i] != 0) {
    			return i;
    		}
    	}
		return 0;
	}
    
	/**
	 * Transform the change accordingly to the number of row.
	 * @param change
	 * @return
	 */
    private int reduceChange(int change) {
    	if (change >= pivot) {
			return change - pivot;
		} else {
			return change;
		}
    }

    /**
     * Compute a distance from "no change between two states". The change have different weights depending on their index in the newNodeOrder.
     * @param diffstate
     * @param maxValues
     * @param start 
     * @return
     */
	private double get_dx(byte[] diffstate, byte[] maxValues, int start) {
    	int dx = 0;
    	int ddx = 1;
    	for (int i = start; i < diffstate.length/2; i++) {
			dx +=  diffstate[i]*ddx;
			ddx *= maxValues[i];
		}
		return dx;
	}
    /**
     * Compute a distance from "no change between two states". The change have different weights depending on their index in the newNodeOrder.
     * @param diffstate
     * @param maxValues
     * @param start 
     * @return
     */
	private double get_dy(byte[] diffstate, byte[] maxValues, int start) {
      	int dx = 0;
    	int ddx = 1;
    	for (int i = pivot+start; i < diffstate.length; i++) {
			dx +=  diffstate[i]*ddx;
			ddx *= maxValues[i];
		}
		return dx;
	}

    /**
     * Construct the | bit operator for a table of byte.
     * A value in the table is 0, 
     *   if the corresponding gene (according to the newNodeOrder) did not change between the vertices.
     *   otherwise its the absolute difference (1 normally)
     * 
     * @param sourceVertex
     * @param targetVertex
     * @return
     */
	private byte[] getDiffStates(GsDynamicNode sourceVertex, GsDynamicNode targetVertex) {
		byte[] delta = new byte[sourceVertex.state.length];
		for (int i = 0; i < delta.length; i++) {
			delta[i] = (byte) Math.abs(getState(sourceVertex.state,i) - getState(targetVertex.state,i));
		}
		return delta;
	}

    /**
     * return the value of the state i according to the newNodeOrder
     * @param state
     * @param i
     * @return
     */
    private int getState(byte[] state, int i) {
		return state[newNodeOrder[i]];
	}

    /**
     * Get the maxvalues (the level max of each node) and return it. 
     * The nodes are correctly indexed with newNodeOrder
     */
	public byte[] getMaxValues(List nodeOrder) {
    	byte[] maxValues = new byte[nodeOrder.size()];
    	int i = 0;
    	for (Iterator it = nodeOrder.iterator(); it.hasNext();) {
    		GsRegulatoryVertex v = (GsRegulatoryVertex) it.next();
    		maxValues[newNodeOrder[i++]] = (byte) (v.getMaxValue()+1);
    	}			
    	return maxValues;
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
		System.out.println("up");
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
		System.out.println("down");
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
