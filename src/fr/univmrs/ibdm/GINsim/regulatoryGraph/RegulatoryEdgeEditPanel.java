package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import fr.univmrs.tagc.datastore.GenericPropertyInfo;
import fr.univmrs.tagc.datastore.ObjectPropertyEditorUI;
import fr.univmrs.tagc.datastore.gui.GenericPropertyHolder;


public class RegulatoryEdgeEditPanel extends JPanel 
	implements ActionListener, ObjectPropertyEditorUI {
	private static final long	serialVersionUID	= 5147198338786927504L;

	GsRegulatoryEdge edge;
	GsRegulatoryGraph graph;
	GenericPropertyInfo pinfo;
	JLabel labelMax = new JLabel();
	
	private EdgeThresholdModel thmodel = null;
	private JComboBox signcombo;

	public RegulatoryEdgeEditPanel() {
        thmodel = new EdgeThresholdModel();
        signcombo = new JComboBox(GsRegulatoryMultiEdge.SIGN_SHORT);
        setLayout(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        add(new JLabel("[ "), c);
        
        c = new GridBagConstraints();
        c.gridx = 1;
        add(new JSpinner(thmodel), c);
        
        c = new GridBagConstraints();
        c.gridx = 2;
        c.anchor = GridBagConstraints.WEST;
        add(labelMax, c);
        labelMax.setSize(50, labelMax.getHeight());

        c = new GridBagConstraints();
        c.gridx = 3;
        c.anchor = GridBagConstraints.EAST;
        add(signcombo, c);
        signcombo.addActionListener(this);
	}
	public RegulatoryEdgeEditPanel(GsRegulatoryGraph graph) {
		this();
		this.graph = graph;
	}
	public void setEdge(GsRegulatoryEdge edge) {
		this.edge = edge;
		String smax = " , "+edge.getMaxAsString()+"]  ";
		labelMax.setText(smax);
		labelMax.setSize(70, labelMax.getHeight());
		thmodel.setSelection(edge);
		signcombo.setSelectedIndex(edge.sign);
	}
	public void actionPerformed(ActionEvent e) {
		short s = (short)signcombo.getSelectedIndex();
		if (s != edge.sign && s >= 0 && s<GsRegulatoryMultiEdge.SIGN_SHORT.length) {
			edge.me.setSign(edge.index, s, graph);
		}
	}
	public void apply() {
	}
	public void refresh(boolean force) {
		setEdge((GsRegulatoryEdge)pinfo.getRawValue());
	}
	public void setEditedProperty(GenericPropertyInfo pinfo,
			GenericPropertyHolder panel) {
		this.pinfo = pinfo;
		this.graph = (GsRegulatoryGraph)pinfo.data;
		panel.addField(this, pinfo, 0);
	}
}

class EdgeThresholdModel extends AbstractSpinnerModel {
	GsRegulatoryEdge edge;
	
	public void setSelection(GsRegulatoryEdge edge) {
		this.edge = edge;
		fireStateChanged();
	}
	
	public Object getNextValue() {
		if (edge != null) {
			edge.me.setMin(edge.index, (short)(edge.getMin()+1));
			fireStateChanged();
		}
		return getValue();
	}

	public Object getPreviousValue() {
		if (edge != null) {
			edge.me.setMin(edge.index, (short)(edge.getMin()-1));
			fireStateChanged();
		}
		return getValue();
	}

	public Object getValue() {
		if (edge != null) {
			return new Integer(edge.getMin());
		}
		return null;
	}

	public void setValue(Object value) {
		if (edge != null) {
			if (value == null) {
				return;
			}
			if (value instanceof Integer) {
				edge.me.setMin(edge.index, ((Integer)value).shortValue());
				fireStateChanged();
			}
		}
	}
}