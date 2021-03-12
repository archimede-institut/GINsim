package org.ginsim.gui.annotation.classes;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.colomoto.biolqm.metadata.annotations.Metadata;

/**
 * Class to manage annotations involving authors, uris or tags
 *
 * @author Martin Boutroux
 */
class ElementsPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private Metadata metadata;
	private String qualifier;
	private AtomicInteger alternative;
	
	ElementsPanel(Metadata newMetadata, String newQualifier, AtomicInteger newAlternative) {
		super();
		
		metadata = newMetadata;
		qualifier = newQualifier;
		alternative = newAlternative;
	}
	
	void removeElement(String element) {
		if (element.matches(".+:.+")) {
			String[] elementBroken = element.split(":", 2);
			metadata.removeURI(qualifier, alternative.intValue(), elementBroken[0], elementBroken[1]);
		}
		else if (element.matches("^#.+")) {
			metadata.removeTag(qualifier, alternative.intValue(), element.split("#", 2)[1]);
		}
		else if (element.matches(".*;.*;.*;.*;.*")) {
			ArrayList<String> array = new ArrayList<String>();
			
			String[] elementBroken = element.split(";", 5);
			for (String piece: elementBroken) {
				if (piece.equals("") || piece.equals("null")) {
					array.add(null);
				} else {
					array.add(piece);
				}
			}
			metadata.removeAuthor(qualifier, array.get(0), array.get(1), array.get(2), array.get(3), array.get(4));
		}
	}
	
	void addElement (String element) {
		JPanel panelElement = new JPanel();
		panelElement.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 1));
        
        JLabel labelElement = new JLabel(element);
        panelElement.add(labelElement);
        
        panelElement.add(Box.createHorizontalStrut(2));
        
        final CircleButton buttonElement = new CircleButton("-", false);
        panelElement.add(buttonElement);
        
        panelElement.add(Box.createHorizontalStrut(4));
        
	    buttonElement.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent event) {
	    		JComponent button = (JComponent) event.getSource();
	    		JPanel panelElement = (JPanel) button.getParent();
	    		JPanel panelElements = (JPanel) panelElement.getParent();
	    		JPanel panelExternal = (JPanel) panelElements.getParent();
	    		
	    		JLabel labelElement = (JLabel) panelElement.getComponent(0);
	    		String element = labelElement.getText();
	    		removeElement(element);
	    		
	    		panelElements.remove(panelElement);
	    		panelElements.revalidate();
	    		panelElements.repaint();
	    		
	    		if (panelElements.getComponents().length == 0) {
	    			panelExternal.remove(1);
	    		}
	    	}
	    });
	    
	    panelElement.setFocusable(true);
	    panelElement.addFocusListener(new FocusListenerUpdatingBorders(Color.black, null));
	    
	    Action eraseAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				buttonElement.doClick();
	        }
	    };
	    panelElement.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "eraseAction");
	    panelElement.getActionMap().put("eraseAction", eraseAction);
        
        add(panelElement);
        
        JPanel panelElements = (JPanel) panelElement.getParent();
        JPanel panelExternal = (JPanel) panelElements.getParent();
		if (panelElements.getComponents().length == 1) {
			panelExternal.add(Box.createVerticalStrut(4));
		}
	}
}
