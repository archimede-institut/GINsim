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

import org.ginsim.common.utils.Translator;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdgeSign;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.utils.data.GenericPropertyHolder;
import org.ginsim.gui.utils.data.GenericPropertyInfo;
import org.ginsim.gui.utils.data.ObjectPropertyEditorUI;



public class RegulatoryEdgeEditPanel extends JPanel 
	implements ActionListener, ObjectPropertyEditorUI {
	private static final long	serialVersionUID	= 5147198338786927504L;

	RegulatoryEdge edge;
	RegulatoryGraph graph = null;
	GenericPropertyInfo pinfo;
	
	private EdgeThresholdModel thmodel = null;
	private JComboBox signcombo;

	public RegulatoryEdgeEditPanel() {
	}
	public RegulatoryEdgeEditPanel(RegulatoryGraph graph) {
		this();
		init(graph);
	}
	
	public void init (RegulatoryGraph graph) {
		this.graph = graph;
        thmodel = new EdgeThresholdModel(graph);
        signcombo = new JComboBox(RegulatoryEdgeSign.getShortDescForGUI());
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
	public void setEdge(RegulatoryEdge edge) {
		this.edge = edge;
		thmodel.setSelection(edge);
		signcombo.setSelectedIndex(edge.getSign().getIndexForGUI());
	}
	public void actionPerformed(ActionEvent e) {
		byte s = (byte)signcombo.getSelectedIndex();
		if (s != edge.getSign().getIndexForGUI() && s >= 0 && s<RegulatoryEdgeSign.values().length) {
			edge.me.setSign(edge.index, RegulatoryEdgeSign.getFromPos(s), graph);
		}
	}
	public void apply() {
	}
	@Override
	public void release() {
	}
	public void refresh(boolean force) {
		setEdge((RegulatoryEdge)pinfo.getRawValue());
	}
	public void setEditedProperty(GenericPropertyInfo pinfo, GenericPropertyHolder panel) {
		this.pinfo = pinfo;
		if (this.graph == null) {
			init((RegulatoryGraph)pinfo.data);
		}
		panel.addField(this, pinfo, 0);
	}
}

class EdgeThresholdModel extends AbstractSpinnerModel {

	private final RegulatoryGraph graph;
	RegulatoryEdge edge;
	
	public EdgeThresholdModel(RegulatoryGraph graph) {
		this.graph = graph;
	}
	
	public void setSelection(RegulatoryEdge edge) {
		this.edge = edge;
		fireStateChanged();
	}
	
	public Object getNextValue() {
		if (edge != null) {
			edge.me.setMin(edge.index, (byte)(edge.getMin()+1), graph);
			fireStateChanged();
		}
		return getValue();
	}

	public Object getPreviousValue() {
		if (edge != null) {
			edge.me.setMin(edge.index, (byte)(edge.getMin()-1), graph);
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
				edge.me.setMin(edge.index, ((Integer)value).byteValue(), graph);
				fireStateChanged();
			}
		}
	}
}
