package fr.univmrs.tagc.GINsim.annotation;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.common.datastore.GenericPropertyInfo;
import fr.univmrs.tagc.common.datastore.ObjectPropertyEditorUI;
import fr.univmrs.tagc.common.datastore.SimpleGenericList;
import fr.univmrs.tagc.common.datastore.gui.GenericListPanel;
import fr.univmrs.tagc.common.datastore.gui.GenericPropertyHolder;
/**
 * Panel to edit annotations
 */
public class AnnotationPanel extends JPanel
	implements ObjectPropertyEditorUI, TableModelListener {

	private static final long serialVersionUID = -8542547209276966234L;

	private GsGraph graph;
	private Annotation currentNote = null;
    private boolean listenChanges = true;
	LinkList linkList;
	GenericListPanel linkListPanel = null;
	private JScrollPane jScrollPane = null;
	private JTextArea jTextArea = null;

	private GenericPropertyInfo	pinfo;
	/**
	 * This method initializes 
	 * 
	 */
	public AnnotationPanel() {
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
        this.add(getLinkList(), gridBagConstraints15);
        this.setPreferredSize(new java.awt.Dimension(800,60));
        gridBagConstraints16.weightx = 1.0;
        gridBagConstraints16.weighty = 1.0;
        gridBagConstraints16.fill = java.awt.GridBagConstraints.BOTH;
        this.add(getJScrollPane(), gridBagConstraints16);
	}
    public void setEditedObject(Object obj) {
        if (obj != null && obj instanceof Annotation) {
            listenChanges = false;
            currentNote = (Annotation)obj;
            linkList.setData(currentNote.linkList);
            jTextArea.setText(currentNote.getComment());
            listenChanges = true;
        }
    }
    public Component getLinkList() {
    	if (linkListPanel == null) {
    		linkList = new LinkList(graph);
    		linkListPanel = new GenericListPanel();
    		linkListPanel.setList(linkList);
    	}
    	return linkListPanel;
    }
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTextArea());
		}
		return jScrollPane;
	}
	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
            jTextArea.setLineWrap(true);
            jTextArea.setWrapStyleWord(true);
			jTextArea.addFocusListener(new java.awt.event.FocusAdapter() { 
				public void focusLost(java.awt.event.FocusEvent e) {
				    applyComment();
				}
			});
		}
		return jTextArea;
	}
    protected void applyComment() {
    	if (currentNote == null) {
    		return;
    	}
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
	public void apply() {
	}
	public void refresh(boolean force) {
		setEditedObject(pinfo.getRawValue());
	}
	public void setEditedProperty(GenericPropertyInfo pinfo,
			GenericPropertyHolder panel) {
		this.pinfo = pinfo;
		linkList.graph = (GsGraph)pinfo.editor.getMasterObject();
		panel.addField(this, pinfo, 0);
	}
}

class LinkList extends SimpleGenericList {
	GsGraph graph;
	LinkList(GsGraph graph) {
		this.graph = graph;
		canAdd = true;
		nbAction = 1;
		canRemove = true;
		canEdit = true;
		doInlineAddRemove = true;
	}
	public Object doCreate(String name) {
		return new AnnotationLink(name, graph);
	}
	public boolean doEdit(Object data, Object value) {
		((AnnotationLink)data).setText((String)value, graph);
		return true;
	}
	public void doRun(int row, int col) {
		((AnnotationLink)v_data.get(row)).open();
	}
}