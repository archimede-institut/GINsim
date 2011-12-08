package org.ginsim.gui.graph.regulatorygraph;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractSpinnerModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import org.ginsim.core.graph.regulatorygraph.RegulatoryEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.gui.resource.Translator;
import org.ginsim.gui.utils.data.GenericPropertyHolder;
import org.ginsim.gui.utils.data.GenericPropertyInfo;
import org.ginsim.gui.utils.data.ObjectPropertyEditorUI;



public class RegulatoryEdgeEditPanel extends JPanel 
	implements ActionListener, ObjectPropertyEditorUI {
	private static final long	serialVersionUID	= 5147198338786927504L;

	RegulatoryEdge edge;
	RegulatoryGraph graph;
	GenericPropertyInfo pinfo;
	
	private EdgeThresholdModel thmodel = null;
	private JComboBox signcombo;

	public RegulatoryEdgeEditPanel() {
        thmodel = new EdgeThresholdModel();
        signcombo = new JComboBox(RegulatoryMultiEdge.SIGN_SHORT);
        setLayout(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        add(new JLabel(Translator.getString("STR_threshold")), c);
        
        c = new GridBagConstraints();
        c.gridx = 1;
        add(new JSpinner(thmodel), c);
        
        c = new GridBagConstraints();
        c.gridx = 2;
        c.anchor = GridBagConstraints.WEST;
        add(new JLabel(Translator.getString("STR_sign")), c);

        c = new GridBagConstraints();
        c.gridx = 3;
        c.anchor = GridBagConstraints.EAST;
        add(signcombo, c);
        signcombo.addActionListener(this); 
	}
	public RegulatoryEdgeEditPanel(RegulatoryGraph graph) {
		this();
		this.graph = graph;
	}
	public void setEdge(RegulatoryEdge edge) {
		this.edge = edge;
		thmodel.setSelection(edge);
		signcombo.setSelectedIndex(edge.sign);
	}
	public void actionPerformed(ActionEvent e) {
		byte s = (byte)signcombo.getSelectedIndex();
		if (s != edge.sign && s >= 0 && s<RegulatoryMultiEdge.SIGN_SHORT.length) {
			edge.me.setSign(edge.index, s, graph);
		}
	}
	public void apply() {
	}
	public void refresh(boolean force) {
		setEdge((RegulatoryEdge)pinfo.getRawValue());
	}
	public void setEditedProperty(GenericPropertyInfo pinfo,
			GenericPropertyHolder panel) {
		this.pinfo = pinfo;
		this.graph = (RegulatoryGraph)pinfo.data;
		panel.addField(this, pinfo, 0);
	}
}

class EdgeThresholdModel extends AbstractSpinnerModel {
	RegulatoryEdge edge;
	
	public void setSelection(RegulatoryEdge edge) {
		this.edge = edge;
		fireStateChanged();
	}
	
	public Object getNextValue() {
		if (edge != null) {
			edge.me.setMin(edge.index, (byte)(edge.getMin()+1));
			fireStateChanged();
		}
		return getValue();
	}

	public Object getPreviousValue() {
		if (edge != null) {
			edge.me.setMin(edge.index, (byte)(edge.getMin()-1));
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
				edge.me.setMin(edge.index, ((Integer)value).byteValue());
				fireStateChanged();
			}
		}
	}
}
