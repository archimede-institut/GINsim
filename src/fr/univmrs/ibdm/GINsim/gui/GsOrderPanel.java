package fr.univmrs.ibdm.GINsim.gui;

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphListener;

/**
 * Editor for node order
 */
public class GsOrderPanel extends JPanel implements GsGraphListener {

	private static final long serialVersionUID = 2149784291778826854L;
	private javax.swing.JList orderList = null;
	private javax.swing.JScrollPane jScrollPane = null;
	private javax.swing.JPanel UpDownPanel = null;
	private javax.swing.JButton upButton = null;
	private javax.swing.JButton downButton = null;
	private GsMainFrame mainFrame;
	private GsGraph graph;

	/**
	 * This is the default constructor
	 */
	public GsOrderPanel() {
		super();
		initialize();
	}
	/**
	 * This method initializes this
	 */
	private void initialize() {
		java.awt.BorderLayout layBorderLayout1 = new java.awt.BorderLayout();
		layBorderLayout1.setHgap(2);
		layBorderLayout1.setVgap(2);
		this.setLayout(layBorderLayout1);
		this.add(getUpDownPanel(), java.awt.BorderLayout.EAST);
		this.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
		this.setMinimumSize(new Dimension(10,10));
	}
	/**
	 * @return the main frame
	 */
	public GsMainFrame getMainFrame() {
		return mainFrame;
	}

	/**
	 * @param frame
	 */
	public void setMainFrame(GsMainFrame frame) {
		mainFrame = frame;
		mainFrame.getGraph().addGraphListener(this);
		
		updateList();
	}

	/**
	 * This method initializes orderList
	 * 
	 * @return javax.swing.JList
	 */
	private javax.swing.JList getOrderList() {
		if(orderList == null) {
			orderList = new javax.swing.JList(new GsListModel(new Vector()));
			orderList.setVisibleRowCount(5);
		}
		return orderList;
	}

	private void updateList() {
		
		graph = mainFrame.getGraph();
		if (graph!=null) {
					Vector v=graph.getNodeOrder();
					((GsListModel)orderList.getModel()).setVector(v);
		} else removeAll();
		
	}
	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPane() {
		if(jScrollPane == null) {
			jScrollPane = new javax.swing.JScrollPane();
			jScrollPane.setViewportView(getOrderList());
			jScrollPane.setMinimumSize(new java.awt.Dimension(30,20));
		}
		return jScrollPane;
	}
	/**
	 * This method initializes UpDownPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getUpDownPanel() {
		if(UpDownPanel == null) {
			UpDownPanel = new javax.swing.JPanel();
			java.awt.GridLayout layGridLayout3 = new java.awt.GridLayout();
			layGridLayout3.setRows(4);
			layGridLayout3.setColumns(1);
			layGridLayout3.setHgap(2);
			layGridLayout3.setVgap(2);
			UpDownPanel.setLayout(layGridLayout3);
			JPanel jpl=new JPanel();
			jpl.setMinimumSize(new Dimension(1,1));
			UpDownPanel.add(jpl, null);
			UpDownPanel.add(getUpButton(), null);
			UpDownPanel.add(getDownButton(), null);
			UpDownPanel.setMinimumSize(new java.awt.Dimension(20,20));
		}
		return UpDownPanel;
	}
	/**
	 * This method initializes upButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getUpButton() {
		if(upButton == null) {
			upButton = new javax.swing.JButton(GsEnv.getIcon("upArrow.gif"));
			upButton.setName("upButton");
			upButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			upButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    moveUp();
				}
			});
		}
		return upButton;
	}
	/**
     * move the selected items up 
     */
    protected void moveUp() {
		int[] index=orderList.getSelectedIndices();
		for (int i=0;i<index.length;i++) {
			if (index[i]>0) {
				((GsListModel)orderList.getModel()).moveElementAt(index[i],index[i]-1);
				index[i]=index[i]-1;
			} else return;
		}
		orderList.setSelectedIndices(index);
    }
    /**
	 * This method initializes downButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getDownButton() {
		if(downButton == null) {
			downButton = new javax.swing.JButton(GsEnv.getIcon("downArrow.gif"));
			downButton.setName("downButton");
			downButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
				    moveDown();
				}
			});
		}
		return downButton;
	}
	/**
     * move the selected items down
     */
    protected void moveDown() {
		int[] index=orderList.getSelectedIndices();
		for (int i=index.length-1;i>=0;i--) {
			if (index[i]>=0 && index[i]+1<((GsListModel)orderList.getModel()).getSize()) {
				((GsListModel)orderList.getModel()).moveElementAt(index[i],index[i]+1);
				index[i]=index[i]+1;
			} else return;
		}
		orderList.setSelectedIndices(index);
    }
    public void edgeAdded() {
	}
	public void edgeRemoved() {
	}
	public void vertexAdded() {
		((GsListModel)orderList.getModel()).fireModelChange();
	}
	public void vertexRemoved() {
		((GsListModel)orderList.getModel()).fireModelChange();
	}
}


class GsListModel implements ListModel {
	
	private Vector listener;
	private Vector vec;
	
	/**
	 * @param v data for the model.
	 */
	public GsListModel(Vector v) {
		vec=v;
		listener= new Vector();
	}
	
	public void addListDataListener(ListDataListener l) {
		listener.add(l);
	}
	
	public void removeListDataListener(ListDataListener l) {
		listener.remove(l);
	}

	public Object getElementAt(int index) {
		return vec.get(index);
	}

	public int getSize() {
		return vec.size();
	}
	
	
	protected void moveElementAt(int index,int to) {
		Object obj=vec.remove(index);
		vec.insertElementAt(obj,to);
		fireModelChange();
	}
	
	protected void setVector(Vector v) {
		vec=v;
		fireModelChange();
	}
	
	protected void fireModelChange() {
		for (int i=0;i<listener.size();i++) {
			((ListDataListener)listener.get(i)).contentsChanged(new ListDataEvent(this,ListDataEvent.CONTENTS_CHANGED,0,vec.size()));
		}
	}
	
	protected void fireModelChange(int start,int end) {
		for (int i=0;i<listener.size();i++) {
			((ListDataListener)listener.get(i)).contentsChanged(new ListDataEvent(this,ListDataEvent.CONTENTS_CHANGED,start,end));
		}
	}

}
