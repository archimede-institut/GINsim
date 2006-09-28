package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import fr.univmrs.ibdm.GINsim.data.GsAnnotation;
import fr.univmrs.ibdm.GINsim.gui.GsParameterPanel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.models.GsAnnotationTableModel;
/**
 * Panel to edit annotations
 */
public class GsAnnotationPanel extends GsParameterPanel implements TableModelListener {

	private static final long serialVersionUID = -8542547209276966234L;

	private GsAnnotation currentNote = null;
    private boolean listenChanges = true;
	
	private JTable jTable = null;
	private JScrollPane jScrollPane = null;
	private JScrollPane jScrollPane1 = null;
	private JTextArea jTextArea = null;
	/**
	 * This method initializes 
	 * 
	 */
	public GsAnnotationPanel() {
		super();
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
        this.setLayout(new GridBagLayout());
        this.setSize(542, 60);
        gridBagConstraints15.gridx = 0;
        gridBagConstraints15.gridy = 0;
        gridBagConstraints15.weightx = 1.0;
        gridBagConstraints15.weighty = 1.0;
        gridBagConstraints15.fill = java.awt.GridBagConstraints.BOTH;
        this.add(getJScrollPane(), gridBagConstraints15);
        this.setPreferredSize(new java.awt.Dimension(800,60));
        gridBagConstraints16.weightx = 1.0;
        gridBagConstraints16.weighty = 1.0;
        gridBagConstraints16.fill = java.awt.GridBagConstraints.BOTH;
        this.add(getJScrollPane1(), gridBagConstraints16);
	}
    /**
     * @see fr.univmrs.ibdm.GINsim.gui.GsParameterPanel#setEditedObject(java.lang.Object)
     */
    public void setEditedObject(Object obj) {
        if (obj != null && obj instanceof GsAnnotation) {
            listenChanges = false;
            currentNote = (GsAnnotation)obj;
            ((GsAnnotationTableModel)jTable.getModel()).setLinkList(currentNote.getLinkList());
            jTextArea.setText(currentNote.getComment());
            listenChanges = true;
        }
    }
    
	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */    
	private JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable();
			jTable.setModel(new GsAnnotationTableModel());
            jTable.getModel().addTableModelListener(this);
		}
		return jTable;
	}
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTable());
		}
		return jScrollPane;
	}
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getJTextArea());
		}
		return jScrollPane1;
	}
	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.addFocusListener(new java.awt.event.FocusAdapter() { 
				public void focusLost(java.awt.event.FocusEvent e) {
				    applyComment();
				}
			});
		}
		return jTextArea;
	}
    /**
     * 
     */
    protected void applyComment() {
        if (!currentNote.getComment().equals(jTextArea.getText())) {
        		currentNote.setComment(jTextArea.getText());
            if (listenChanges && graph != null) {
                graph.fireMetaChange();
            }
        }
    }
    
    public void tableChanged(TableModelEvent e) {
        if (listenChanges && graph != null) {
            graph.fireMetaChange();
        }
    }
    
}  //  @jve:decl-index=0:visual-constraint="2,10"
