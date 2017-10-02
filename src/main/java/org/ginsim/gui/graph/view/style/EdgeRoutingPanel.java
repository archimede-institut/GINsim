package org.ginsim.gui.graph.view.style;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.view.style.StyleManager;

/**
 * The panel showing edge routing options.
 * It shows the curve flag, anchor selector and a reset button
 * 
 * @author Aurelien Naldi
 */
@SuppressWarnings("serial")
public class EdgeRoutingPanel extends JPanel implements ActionListener {

    private final JCheckBox curveCheckbox = new JCheckBox("curve");
    private final AnchorSelector anchorSelector = new AnchorSelector(this);
    private final JButton bClear = new JButton("X");

    private final StyleManager manager;

    private Edge selected = null;
    
    
    public EdgeRoutingPanel(StyleManager manager) {
    	super();
    	this.manager = manager;
    	
        curveCheckbox.addActionListener(this);
        add(curveCheckbox);

        add(anchorSelector);
        
        bClear.setForeground(Color.red);
        bClear.setToolTipText("Reset the curve flag and intermediate points)");
        bClear.addActionListener(this);
        add(bClear);
    }
    
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (selected == null) {
        	return;
        }
        
        Object src = actionEvent.getSource();
        if (src == curveCheckbox) {
        	manager.setEdgeCurved(selected, curveCheckbox.isSelected());
        }
        
        if (src == bClear) {
        	manager.clearEdgeRouting(selected);
        	setSelected(selected);
        }
    }

    protected void updateAnchor() {
        if (selected != null) {
            manager.setEdgeAnchor(selected, anchorSelector.getAnchor());
        }
    }

    public void setSelected(Edge selected) {
    	this.selected = selected;
    	boolean active = (selected != null);
		curveCheckbox.setVisible(active);
		bClear.setVisible(active);

		if (active) {
			if (selected.getSource() == selected.getTarget()) {
				anchorSelector.setVisible(true);
		        anchorSelector.setAnchor(manager.getEdgeAnchor(selected));
			} else {
				anchorSelector.setVisible(false);
			}
	        curveCheckbox.setSelected(manager.getEdgeCurved(selected));
    	} else {
    		anchorSelector.setVisible(false);
    	}
    	
    }
}
