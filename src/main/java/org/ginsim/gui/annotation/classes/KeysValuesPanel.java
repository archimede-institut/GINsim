package org.ginsim.gui.annotation.classes;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.*;

import org.colomoto.biolqm.metadata.annotations.Metadata;

/**
 * Class to manage annotations involving pairs of key-values
 *
 * @author Martin Boutroux
 */
class KeysValuesPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private Map<String, Integer> keysIndex;
	private Metadata metadata;
	private String qualifier;
	private AtomicInteger alternative;
	
	KeysValuesPanel(Metadata newMetadata, String newQualifier, AtomicInteger newAlternative) {
		super();
		
		keysIndex = new HashMap<String, Integer>();
		
		metadata = newMetadata;
		qualifier = newQualifier;
		alternative = newAlternative;
	}
	
	void removeKeyValue(String key, String value) {
		// FIXME: remove value
//		metadata.removeKeyValue(qualifier, alternative.intValue(), key, value);
	}
	
	void addKeyValue (String element) {
		String[] bricks = element.split("=");
		final String key = bricks[0];
		String value = bricks[1];
		
		int numberPanelKeys = getComponents().length;
		
		JPanel panelKey;
		if (!keysIndex.containsKey(key)) {
	        panelKey = new JPanel();
	        panelKey.setLayout(new WrapLayout(WrapLayout.LEFT, 0, 1));
	        
	        JLabel labelKey = new JLabel(key+" =");
	        panelKey.add(labelKey);
	        
	        keysIndex.put(key, getComponents().length);
	        add(panelKey);
		}
		else {
			int index = keysIndex.get(key);
	        panelKey = (JPanel) getComponent(index);
		}
        
		JPanel panelValue = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
		
        JLabel labelValue = new JLabel(value);
        panelValue.add(labelValue);
        
        final CircleButton buttonValue = new CircleButton("-", false);
        panelValue.add(buttonValue);
        
        panelValue.setFocusable(true);
	    panelValue.addFocusListener(new FocusListenerUpdatingBorders(Color.black, null));
        
        panelKey.add(panelValue);
        
	    buttonValue.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent event) {
	    		JComponent button = (JComponent) event.getSource();
	    		JPanel panelV = (JPanel) button.getParent();
	    		JPanel panelK = (JPanel) panelV.getParent();
	    		JPanel panelKeysValues = (JPanel) panelK.getParent();
	    		JPanel panelExternal = (JPanel) panelKeysValues.getParent();
	    		
	    		JLabel labelK = (JLabel) panelK.getComponent(0);
	    		String key = labelK.getText().split(" =", 2)[0];
	    		JLabel labelV = (JLabel) panelV.getComponent(0);
	    		String value = labelV.getText();
	    		removeKeyValue(key, value);
	    		
	    		panelK.remove(panelV);
	    		panelK.revalidate();
	    		panelK.repaint();
	    		
	    		if (panelK.getComponents().length == 1) {
	    			JPanel panelQualifier = (JPanel) panelK.getParent();
		    		panelQualifier.remove(panelK);
		    		panelQualifier.revalidate();
		    		panelQualifier.repaint();
		    		keysIndex.remove(key);
	    		}
	    		
	    		if (panelKeysValues.getComponents().length == 0) {
	    			panelExternal.remove(1);
	    		}
	    	}
	    });
	    
	    Action eraseAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				buttonValue.doClick();
	        }
	    };
	    panelValue.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "eraseAction");
	    panelValue.getActionMap().put("eraseAction", eraseAction);
	    
        JPanel panelKeysValues = (JPanel) panelKey.getParent();
        JPanel panelExternal = (JPanel) panelKeysValues.getParent();
		if (numberPanelKeys == 0) {
			panelExternal.add(Box.createVerticalStrut(4));
		}
	}
}
