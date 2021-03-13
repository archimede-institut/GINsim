package org.ginsim.gui.annotation.classes;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.colomoto.biolqm.metadata.annotations.Metadata;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Produces a GUI to modify the metadata of an object (model, nodes, annotations with nested parts...)
 *
 * @author Martin Boutroux
 */
public class AnnotationsComponent extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private ArrayList<String> indexQualifiers = new ArrayList<String>();
	private Map<JPanel, QualifierProperties> caracsQualifiers = new HashMap<JPanel, QualifierProperties>();
	private Map<String, String> typeQualifiers = new HashMap<String, String>();
	private Metadata metadata;
	private boolean nested;
	
	private void clicToHideNested(JPanel nestedPanel) {
		try {
			JPanel nestedHeader = (JPanel) nestedPanel.getComponent(0);
			JPanel innerNestedHeader = (JPanel) nestedHeader.getComponent(0);
			TriangleButton hideButton = (TriangleButton) innerNestedHeader.getComponent(0);
			
			JPanel evolvingNestedContent = (JPanel) nestedPanel.getComponent(1);
    		
    		if (!evolvingNestedContent.isVisible()) {
    			evolvingNestedContent.setVisible(true);
    			hideButton.setShow();
                hideButton.repaint();
    		}
    		else {
    			evolvingNestedContent.setVisible(false);
    			hideButton.setShow();
                hideButton.repaint();
    		}
    		
    		revalidate();
    		repaint();
		} catch (Exception e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, e1.getMessage(), "Error Message", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private JPanel createNestedBloc(final JPanel content) {
		
		final JPanel nestedPanel = new JPanel();
        RelativeLayout rlNested = new RelativeLayout(RelativeLayout.Y_AXIS);
        rlNested.setFill(true);
        nestedPanel.setLayout(rlNested);
		
		JPanel nestedHeader = new JPanel();
		nestedHeader.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 1));
		nestedPanel.add(nestedHeader);
		
		JPanel innerNestedHeader = new JPanel();
		nestedHeader.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		nestedHeader.add(innerNestedHeader);

		final TriangleButton hideButton = new TriangleButton(true, Color.orange);
		innerNestedHeader.add(hideButton);
		
		JLabel nestedLabel = new JLabel("Nested:");
		innerNestedHeader.add(nestedLabel);
		
		JPanel nestedContent = new JPanel();
		nestedPanel.add(nestedContent);
		
		nestedPanel.setVisible(false);
		
		innerNestedHeader.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
            	clicToHideNested(nestedPanel);
            }
        });
		
		nestedPanel.setFocusable(true);
		nestedPanel.addFocusListener(new FocusListenerUpdatingBorders(Color.black, null));
		
		nestedPanel.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JComponent component = (JComponent) e.getSource();
				component.grabFocus();
				component.requestFocus();
			}
			@Override
			public void mousePressed(MouseEvent e) { }
			@Override
			public void mouseReleased(MouseEvent e) { }
			@Override
			public void mouseEntered(MouseEvent e) { }
			@Override
			public void mouseExited(MouseEvent e) { }
	    });
		
	    // concerning the hide/unhide of the nestedpanel
	    Action hideAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				JPanel component = (JPanel) e.getSource();
				clicToHideNested(component);
	        }
	    };
	    nestedPanel.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_H, 0), "hideAction");
	    nestedPanel.getActionMap().put("hideAction", hideAction);
	    
	    // concerning the switch between the tabs
	    Action toggleAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				JPanel nestedPanel = (JPanel) e.getSource();
				JPanel nestedContent = (JPanel) nestedPanel.getComponent(1);
				JTabbedPane tabbedPane = (JTabbedPane) nestedContent.getComponent(0);
				
				if (tabbedPane.getSelectedIndex() == 0) {
					tabbedPane.setSelectedIndex(1);
				} else {
					tabbedPane.setSelectedIndex(0);
				}
	        }
	    };
	    nestedPanel.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_T, 0), "toggleAction");
	    nestedPanel.getActionMap().put("toggleAction", toggleAction);
	    
	    return nestedPanel;
	}
	
	private void createElementsBloc(JPanel content, String typeGiven) {
		
		QualifierProperties entry = caracsQualifiers.get(content);
		String qualifier = entry.getQualifier();
		AtomicInteger alternative = entry.getAlternative();
		
        RelativeLayout rlElements = new RelativeLayout(RelativeLayout.Y_AXIS);
        rlElements.setFill(true);
		
		switch (typeGiven) {
			case "GenericAnnotation":
				JPanel urisExternalPanel = new JPanel();
				urisExternalPanel.setLayout(rlElements);
				ElementsPanel urisPanel = new ElementsPanel(this.metadata, qualifier, alternative);
		        urisPanel.setLayout(rlElements);
		        urisExternalPanel.add(urisPanel);
		        content.add(urisExternalPanel);
		        
				JPanel tagsExternalPanel = new JPanel();
				tagsExternalPanel.setLayout(rlElements);
		        ElementsPanel tagsPanel = new ElementsPanel(this.metadata, qualifier, alternative);
		        tagsPanel.setLayout(new WrapLayout(WrapLayout.LEFT, 0, 0));
		        tagsExternalPanel.add(tagsPanel);
		        content.add(tagsExternalPanel);
		        
				JPanel keysvaluesExternalPanel = new JPanel();
				keysvaluesExternalPanel.setLayout(rlElements);
		        KeysValuesPanel keysvaluesPanel = new KeysValuesPanel(this.metadata, qualifier, alternative);
		        keysvaluesPanel.setLayout(rlElements);
		        keysvaluesExternalPanel.add(keysvaluesPanel);
		        content.add(keysvaluesExternalPanel);
				break;
			case "AuthorsAnnotation":
				JPanel authorsExternalPanel = new JPanel();
				authorsExternalPanel.setLayout(rlElements);
		        ElementsPanel authorsPanel = new ElementsPanel(this.metadata, qualifier, alternative);
		        authorsPanel.setLayout(rlElements);
		        authorsExternalPanel.add(authorsPanel);
		        content.add(authorsExternalPanel);
				break;
			case "DateAnnotation":
		        JPanel datePanel = new JPanel();
		        datePanel.setLayout(rlElements);
		        content.add(datePanel);
				break;
			case "DistributionAnnotation":
		        JPanel distributionPanel = new JPanel();
		        distributionPanel.setLayout(rlElements);
		        content.add(distributionPanel);
				break;
		}
	}
	
	private boolean populateElementsBloc(JPanel content, ArrayList<String> bricks, boolean creation) {
		
		QualifierProperties entry = caracsQualifiers.get(content);
		String qualifier = entry.getQualifier();
		AtomicInteger alternative = entry.getAlternative();

		String type = bricks.get(0);
		if (content.getComponents().length == 0) {
			createElementsBloc(content, type);
		}
		
		switch (type) {
			case "GenericAnnotation":
				String subtype = bricks.get(1);
				
		        switch (subtype) {
			        case "uri":
		        		try {
		        			if (creation) {
		        				this.metadata.addURI(qualifier, alternative.intValue(), bricks.get(2), bricks.get(3));
		        			}
		        			
							JPanel urisExternalPanel = (JPanel) content.getComponent(0);
				        	ElementsPanel urisPanel = (ElementsPanel) urisExternalPanel.getComponent(0);
				        	urisPanel.addElement(bricks.get(2)+":"+bricks.get(3));
				        	
				        	return true;
						} catch (Exception e) {
			    			JOptionPane.showMessageDialog(null, e.getMessage(), "Error Message", JOptionPane.ERROR_MESSAGE);
						}
			        	break;
			        case "tag":
			        	try {
		        			if (creation) {
				        		this.metadata.addTag(qualifier, alternative.intValue(), bricks.get(2));
				        	}
				        	
				        	JPanel tagsExternalPanel = (JPanel) content.getComponent(1);
				        	ElementsPanel tagsPanel = (ElementsPanel) tagsExternalPanel.getComponent(0);
				        	tagsPanel.addElement("#"+bricks.get(2));
				        	
				        	return true;
						} catch (Exception e) {
			    			JOptionPane.showMessageDialog(null, e.getMessage(), "Error Message", JOptionPane.ERROR_MESSAGE);
						}
			        	break;
			        case "keyvalue":
			        	try {
				        	if (creation) {
				        		this.metadata.addKeyValue(qualifier, alternative.intValue(), bricks.get(2), bricks.get(3));
				        	}
				        	
				        	JPanel keysvaluesExternalPanel = (JPanel) content.getComponent(2);
				        	KeysValuesPanel keysvaluesPanel = (KeysValuesPanel) keysvaluesExternalPanel.getComponent(0);
				        	keysvaluesPanel.addKeyValue(bricks.get(2)+"="+bricks.get(3));
				        	
				        	return true;
						} catch (Exception e) {
			    			JOptionPane.showMessageDialog(null, e.getMessage(), "Error Message", JOptionPane.ERROR_MESSAGE);
						}
			        	break;
		        }
				break;
			case "AuthorsAnnotation":
				try {
			        if (creation) {
			        	this.metadata.addAuthor(qualifier, bricks.get(1), bricks.get(2), bricks.get(3), bricks.get(4), bricks.get(5));
		        	}
			        
		        	JPanel authorsExternalPanel = (JPanel) content.getComponent(0);
		        	ElementsPanel authorsPanel = (ElementsPanel) authorsExternalPanel.getComponent(0);
			        authorsPanel.addElement(bricks.get(1)+";"+bricks.get(2)+";"+bricks.get(3)+";"+bricks.get(4)+";"+bricks.get(5));
			        
			        return true;
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error Message", JOptionPane.ERROR_MESSAGE);
				}
				break;
			case "DateAnnotation":
				try {
			        if (creation) {
			        	this.metadata.addDate(qualifier, bricks.get(1));
		        	}
			        
			        JPanel datePanel = (JPanel) content.getComponent(0);
			        if (datePanel.getComponents().length == 0) {
				        JLabel labelDate = new JLabel(bricks.get(1));
				        labelDate.setAlignmentX(JLabel.LEFT_ALIGNMENT);
				        datePanel.add(labelDate);
			        }
			        else {
				        JLabel dateValue = (JLabel) datePanel.getComponent(0);
				        dateValue.setText(bricks.get(1));
			        }
			        if (datePanel.getComponents().length == 1) {
			        	datePanel.add(Box.createVerticalStrut(4));
			        }
			        
			        return true;
				} catch (Exception e) {
	    			JOptionPane.showMessageDialog(null, e.getMessage(), "Error Message", JOptionPane.ERROR_MESSAGE);
				}
				break;
			case "DistributionAnnotation":
				try {
			        if (creation) {
			        	this.metadata.addDistribution(qualifier, bricks.get(1));
		        	}
			        
			        JPanel distributionPanel = (JPanel) content.getComponent(0);
			        if (distributionPanel.getComponents().length == 0) {
				        JLabel labelDistribution = new JLabel(bricks.get(1));
				        labelDistribution.setAlignmentX(JLabel.LEFT_ALIGNMENT);
				        distributionPanel.add(labelDistribution);
			        }
			        else {
				        JLabel distributionValue = (JLabel) distributionPanel.getComponent(0);
				        distributionValue.setText(bricks.get(1));
			        }
			        if (distributionPanel.getComponents().length == 1) {
			        	distributionPanel.add(Box.createVerticalStrut(4));
			        }
			        
			        return true;
				} catch (Exception e) {
	    			JOptionPane.showMessageDialog(null, e.getMessage(), "Error Message", JOptionPane.ERROR_MESSAGE);
				}
				break;
		}
		
		return false;
	}
	
	private String getTypeQualifier(String qualifier) {
		return this.metadata.suitedJavaClass(qualifier);
	}
	
	public void hasQualifierTypeChanged(String qualString) {
		if (typeQualifiers.containsKey(qualString) && getTypeQualifier(qualString) == null) {
			JOptionPane.showMessageDialog(null,
    				"The qualifier "+qualString+" is not used in any "+metadata.getType()+" anymore. Its blocs are removed so that its type can be redefined.", "Information Message",
    				JOptionPane.INFORMATION_MESSAGE);
			
			typeQualifiers.remove(qualString);
			
			ArrayList<JPanel> elementsToRemove = new ArrayList<JPanel>();
			for (Entry<JPanel, QualifierProperties> entry: caracsQualifiers.entrySet()) {
				JPanel localNotNestedElements = entry.getKey();
				QualifierProperties localQualifier = entry.getValue();
				
				if (localQualifier.getQualifier().equals(qualString)) {
					elementsToRemove.add(localNotNestedElements);
				}
			}
			
			for (JPanel localNotNestedElements: elementsToRemove) {
				JPanel localQualifierElements = (JPanel) localNotNestedElements.getParent();
				JPanel localPaneQualifier = (JPanel) localQualifierElements.getParent();
				JPanel localPaneCreation = (JPanel) localPaneQualifier.getComponent(1);
				JPanel localCompletePaneTitle = (JPanel) localPaneCreation.getComponent(0);
				JButton localEraseButton = (JButton) localCompletePaneTitle.getComponent(1);
				
				localEraseButton.doClick();
			}
		}
		// if we don't have a type for this qualifier yet and there is a new value not null we save it
		else if (!typeQualifiers.containsKey(qualString) && getTypeQualifier(qualString) != null) {
			typeQualifiers.put(qualString, getTypeQualifier(qualString));
		}
	}
	
	public ArrayList<String> breakElement(String element) {
		ArrayList<String> array = new ArrayList<String>();
		
		if (element.matches(".+:.+")) {
			array.add("GenericAnnotation");
			array.add("uri");
			String[] elementBroken = element.split(":", 2);
			array.add(elementBroken[0].trim());
			array.add(elementBroken[1].trim());
		}
		else if (element.matches("^\\s*#.+")) {
			array.add("GenericAnnotation");
			array.add("tag");
			array.add(element.split("#", 2)[1].trim());
		}
		else if (element.matches(".+=.+")) {
			array.add("GenericAnnotation");
			array.add("keyvalue");
			String[] elementBroken = element.split("=", 2);
			array.add(elementBroken[0].trim());
			array.add(elementBroken[1].trim());
		}
		else if (element.matches(".*;.*;.*;.*;.*")) {
			array.add("AuthorsAnnotation");
			String[] elementBroken = element.split(";", 5);
			for (String piece: elementBroken) {
				if (piece.equals("")) {
					array.add(null);
				} else {
					array.add(piece.trim());
				}
			}
		}
		else if (element.matches("^\\s*\\d{1,4}\\-\\d{1,2}\\-\\d{1,2}\\s*$")) {
			array.add("DateAnnotation");
			array.add(element.trim());
		}
		else if (element.matches("^\\s*\\(.*\\)\\s*$")) {
			array.add("NestedQualifier");
			String shortElement = element.trim();
			String qualifier = shortElement.substring(1, shortElement.length()-1);
			array.add(qualifier.trim());
		}
		else {
			array.add("DistributionAnnotation");
			array.add(element.trim());
		}
		return array;
	}
	
	private void goToNewNotNestedElements(String newQualifier, int newAlternative) {
		
		for (Entry<JPanel, QualifierProperties> element : caracsQualifiers.entrySet()) {
			QualifierProperties qual = element.getValue();
			
	        if (qual.getQualifier().equals(newQualifier) && qual.getAlternative().intValue() == newAlternative) {
	        	JPanel newNotNestedElements = element.getKey();
	        	
	        	JPanel newQualifierElements = (JPanel) newNotNestedElements.getParent();
	    		JPanel newPaneQualifier = (JPanel) newQualifierElements.getParent();
	    		
	    		newPaneQualifier.grabFocus();
	    		newPaneQualifier.requestFocus();
	    		
	    		return;
	        }
	    }
	}
	
	private void hideAction(JPanel notNestedElements) {
		JPanel qualifierElements = (JPanel) notNestedElements.getParent();
		JPanel paneQualifier = (JPanel) qualifierElements.getParent();
		JPanel paneCreation = (JPanel) paneQualifier.getComponent(1);
		
		JPanel completePaneTitle = (JPanel) paneCreation.getComponent(0);
		JPanel paneTitle = (JPanel) completePaneTitle.getComponent(0);
		TriangleButton hideButton = (TriangleButton) paneTitle.getComponent(0);
		
		JPanel paneCards = (JPanel) paneCreation.getComponent(1);
    	CardLayout cl = (CardLayout)(paneCards.getLayout());
    	JPanel paneInformation = (JPanel) paneCards.getComponent(1);
    	JLabel info = (JLabel) paneInformation.getComponent(0);
    	
		qualifierElements.setVisible(false);
		hideButton.setShow();
        hideButton.repaint();
        
		QualifierProperties entry = caracsQualifiers.get(notNestedElements);
		String qualifier = entry.getQualifier();
		AtomicInteger alternative = entry.getAlternative();
        
        info.setText(metadata.getShortDescriptionAlternative(qualifier, alternative.intValue()));
        cl.show(paneCards, "INFOPANEL");
	}
	
	private void showAction(JPanel notNestedElements) {
		JPanel qualifierElements = (JPanel) notNestedElements.getParent();
		JPanel paneQualifier = (JPanel) qualifierElements.getParent();
		JPanel paneCreation = (JPanel) paneQualifier.getComponent(1);
		
		JPanel completePaneTitle = (JPanel) paneCreation.getComponent(0);
		JPanel paneTitle = (JPanel) completePaneTitle.getComponent(0);
		TriangleButton hideButton = (TriangleButton) paneTitle.getComponent(0);
		
		JPanel paneCards = (JPanel) paneCreation.getComponent(1);
    	CardLayout cl = (CardLayout)(paneCards.getLayout());
		
		qualifierElements.setVisible(true);
		hideButton.setShow();
        hideButton.repaint();
        
        cl.show(paneCards, "ADDPANEL");
	}
	
	private void changeOfQualifierBloc(JPanel currentPaneQualifier, boolean sense) {
		int change = 1;
		if (!sense) {
			change = -1;
		}
		
		JPanel currentQualifierElements = (JPanel) currentPaneQualifier.getComponent(3);
		JPanel currentNotNestedElements = (JPanel) currentQualifierElements.getComponent(0);
		
		QualifierProperties entry = caracsQualifiers.get(currentNotNestedElements);
		String qualifier = entry.getQualifier();
		AtomicInteger alternative = entry.getAlternative();
		int newIndex = indexQualifiers.indexOf(qualifier)+alternative.intValue()+change;
		
		if (nested) {
			if (newIndex == -1) {
				newIndex = indexQualifiers.size()-1;
			} else if (newIndex == indexQualifiers.size()) {
				newIndex = 0;
			}
		} else if (newIndex == -1 || newIndex == indexQualifiers.size()) {
			JPanel currentSurroundingsQualifier = (JPanel) currentPaneQualifier.getParent();
			JPanel currentPaneQualifiers = (JPanel) currentSurroundingsQualifier.getParent();
			JViewport viewport = (JViewport) currentPaneQualifiers.getParent();
			JScrollPane scroll = (JScrollPane) viewport.getParent();
			JPanel paneAnnotations = (JPanel) scroll.getParent();
			JPanel paneCreation = (JPanel) paneAnnotations.getComponent(0);
			JPanel innerPaneCreation = (JPanel) paneCreation.getComponent(1);
			JTextField qualifierName = (JTextField) innerPaneCreation.getComponent(0);
			
			qualifierName.grabFocus();
			qualifierName.requestFocus();
			
			return;
		}
		
		String newQualifier = indexQualifiers.get(newIndex);
		int newAlternative = newIndex-indexQualifiers.indexOf(newQualifier);
		goToNewNotNestedElements(newQualifier, newAlternative);
	}
	
	private JPanel createQualifierBloc(String qualifier, JPanel paneQualifiers, boolean initial) throws Exception {
		if (!qualifier.equals("")) {
    		int index = indexQualifiers.size();
    		int alternative = 0;
    		
    		if (indexQualifiers.contains(qualifier)) {
    			if (getTypeQualifier(qualifier) == null) {
    				throw new Exception("You cannot create a second alternative while the first one is empty.");
    			} else if (!getTypeQualifier(qualifier).equals("GenericAnnotation")) {
    				throw new Exception("You cannot create alternatives for the qualifiers of type "+getTypeQualifier(qualifier)+".");
    			}
    			
    			index = indexQualifiers.lastIndexOf(qualifier)+1;
    			alternative = Collections.frequency(indexQualifiers, qualifier);
    			
    			if (alternative >= metadata.getNumberOfAlternatives(qualifier)) {
    				metadata.createAlternative(qualifier);
    			}
    		}
    		indexQualifiers.add(index, qualifier);
    		
    		// creation of the jpanels
			final JPanel surroundingsQualifier = new JPanel();
	        RelativeLayout rlQualifier = new RelativeLayout(RelativeLayout.Y_AXIS);
	        rlQualifier.setFill(true);
	        surroundingsQualifier.setLayout(rlQualifier);
			
	        final JPanel paneQualifier = new JPanel();
	        paneQualifier.setBorder(BorderFactory.createLineBorder(Color.gray));
	        paneQualifier.setLayout(rlQualifier);
			
			paneQualifier.add(Box.createRigidArea(new Dimension(0,3)));
			
	        JPanel paneCreation = new JPanel();
	        paneCreation.setLayout(new BorderLayout());
	        paneQualifier.add(paneCreation);
	        
	        paneQualifier.add(Box.createRigidArea(new Dimension(0,3)));
	        
	        JPanel completePaneTitle = new JPanel();
	        completePaneTitle.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
	        paneCreation.add(completePaneTitle, BorderLayout.WEST);
	        
	        JPanel paneTitle = new JPanel();
		    paneTitle.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
	        completePaneTitle.add(paneTitle);
	        
		    final TriangleButton hideButton = new TriangleButton();
		    paneTitle.add(hideButton);
		    
		    paneTitle.add(Box.createRigidArea(new Dimension(5,0)));
			
	        JLabel qualifierName = new JLabel(qualifier+":");
	        paneTitle.add(qualifierName);
	        
	        final CircleButton eraseButton = new CircleButton("-", false);
	        eraseButton.setVisible(true);
	        completePaneTitle.add(eraseButton);
	        
	        final JPanel paneCards = new JPanel(new CardLayout());
	        paneCreation.add(paneCards, BorderLayout.EAST);
	        
	        JPanel paneElement = new JPanel();
	        paneElement.setLayout(new GridBagLayout());
	        paneCards.add(paneElement, "ADDPANEL");
	        
	        GridBagConstraints gbcElement = new GridBagConstraints();
	        
	        final JTextField modificationField = new JTextField(18);
	        gbcElement.insets = new Insets(0, 0, 0, 6);
	        gbcElement.gridx = 0;
	        gbcElement.gridy = 0;
	        paneElement.add(modificationField, gbcElement);
	        
	        if (!initial) {
		        EventQueue.invokeLater(new Runnable() {
		        	@Override
		        	public void run() {
		        		modificationField.grabFocus();
		        		modificationField.requestFocus();
		        	}
		    	});
	        }
			
		    final CircleButton createButton = new CircleButton("+", true);
		    gbcElement.insets = new Insets(0, 0, 0, 8);
	        gbcElement.gridx = 1;
	        gbcElement.gridy = 0;
		    paneElement.add(createButton, gbcElement);
		    
	        JPanel paneInformation = new JPanel();
	        paneInformation.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
	        paneCards.add(paneInformation, "INFOPANEL");
	        
	        final JLabel info = new JLabel();
	        paneInformation.add(info);
			
	        final JPanel qualifierElements = new JPanel();
	        RelativeLayout rlContent = new RelativeLayout(RelativeLayout.Y_AXIS);
	        rlContent.setFill(true);
	        qualifierElements.setLayout(rlContent);
	        paneQualifier.add(qualifierElements);
	        
	        final JPanel notNestedElements = new JPanel();
	        RelativeLayout rlNotNested = new RelativeLayout(RelativeLayout.Y_AXIS);
	        rlNotNested.setFill(true);
	        rlNotNested.setFillGap(52);
	        notNestedElements.setLayout(rlNotNested);
	        qualifierElements.add(notNestedElements);
	        
	        final JPanel nestedElements = createNestedBloc(notNestedElements);
	        RelativeLayout rlNested = new RelativeLayout(RelativeLayout.Y_AXIS);
	        rlNested.setFill(true);
	        rlNested.setFillGap(10);
	        nestedElements.setLayout(rlNested);
	        qualifierElements.add(nestedElements);
	        qualifierElements.add(Box.createRigidArea(new Dimension(0,5)));
			
	        surroundingsQualifier.add(paneQualifier);
	        surroundingsQualifier.add(Box.createRigidArea(new Dimension(0,3)));
    		
    		QualifierProperties entry = new QualifierProperties(qualifier, alternative);
    		caracsQualifiers.put(notNestedElements, entry);
    		
    		paneQualifiers.add(surroundingsQualifier, index);
    		paneQualifiers.repaint();
    		paneQualifiers.revalidate();
    		
            paneTitle.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                	CardLayout cl = (CardLayout)(paneCards.getLayout());
                	
		    		if (qualifierElements.isVisible()) {
		    			qualifierElements.setVisible(false);
		    			hideButton.setShow();
		                hideButton.repaint();
		                
			    		QualifierProperties entry = caracsQualifiers.get(notNestedElements);
			    		String qualifier = entry.getQualifier();
			    		AtomicInteger alternative = entry.getAlternative();
		                
		                info.setText(metadata.getShortDescriptionAlternative(qualifier, alternative.intValue()));
		                cl.show(paneCards, "INFOPANEL");
		    		}
		    		else {
		    			qualifierElements.setVisible(true);
		    			hideButton.setShow();
		                hideButton.repaint();
		                
		                cl.show(paneCards, "ADDPANEL");
		    		}
                }
            });
	        
		    eraseButton.addActionListener(new ActionListener(){
		    	public void actionPerformed(ActionEvent e) {
		    		JPanel surroundingsQualifier = (JPanel) paneQualifier.getParent();
		    		JPanel paneQualifiers = (JPanel) surroundingsQualifier.getParent();
		    		
					JPanel paneQualifier = (JPanel) surroundingsQualifier.getComponent(0);
					JPanel qualifierElements = (JPanel) paneQualifier.getComponent(3);
					JPanel notNestedElements = (JPanel) qualifierElements.getComponent(0);
					
					QualifierProperties entry = caracsQualifiers.get(notNestedElements);
		    		String qualifier = entry.getQualifier();
		    		AtomicInteger alternative = entry.getAlternative();
		    		
					// if the type of the qualifier was not defined then it was brand new and we don't have to imply Metadata
		    		if (getTypeQualifier(qualifier) != null && alternative.intValue()<metadata.getNumberOfAlternatives(qualifier)) {
			    		boolean notlast = metadata.removeAlternative(qualifier, alternative.intValue());
			    		
			    		if (notlast) {
				    	    for (Map.Entry<JPanel, QualifierProperties> oneEntry : caracsQualifiers.entrySet()) {
				    	    	QualifierProperties oneValue = oneEntry.getValue();
				    	    	String oneQualifier = oneValue.getQualifier();
				    	    	AtomicInteger oneAlternative = oneValue.getAlternative();
					    		
					    		if (qualifier.equals(oneQualifier) && alternative.intValue() < oneAlternative.intValue()) {
					    			oneValue.setAlternative(oneAlternative.intValue()-1);
					    		}
				    	    }
			    		}
		    		}
		    		
		    		caracsQualifiers.remove(notNestedElements);
		    		
		    		paneQualifiers.remove(surroundingsQualifier);
		    		paneQualifiers.revalidate();
		    		paneQualifiers.repaint();
		    		
		    		indexQualifiers.remove(qualifier);
		    		
		    		// if the type of the qualifier has changed we update the blocs accordingly
					hasQualifierTypeChanged(qualifier);
		    	}
		    });
		    
		    modificationField.addActionListener(new ActionListener(){  
		    	public void actionPerformed(ActionEvent e) {
		    		
		    		createButton.doClick();
		    	}
		    });
		    
		    modificationField.getDocument().addDocumentListener(new DocumentListener() {
		    	public void changedUpdate(DocumentEvent e) {
		    		colorField();
		    	}
		    	public void removeUpdate(DocumentEvent e) {
		    		colorField();
		    	}
		    	public void insertUpdate(DocumentEvent e) {
		    		colorField();
		    	}
				
		    	public void colorField() {
		    		ArrayList<String> bricks = breakElement(modificationField.getText());
		    		String typeGiven = bricks.get(0);
		    		
		    		switch (typeGiven) {
						case "GenericAnnotation":
					        String subtype = bricks.get(1);
					        switch (subtype) {
						        case "uri":
						        	modificationField.setForeground(Color.MAGENTA);
						        	break;
						        case "tag":
						        	modificationField.setForeground(Color.RED);
						        	break;
						        case "keyvalue":
						        	modificationField.setForeground(Color.PINK);
						        	break;
					        }
							break;
						case "AuthorsAnnotation":
							modificationField.setForeground(Color.GREEN);
							break;
						case "DateAnnotation":
							modificationField.setForeground(Color.BLUE);
							break;
						case "DistributionAnnotation":
							modificationField.setForeground(Color.BLACK);
							break;
						case "NestedQualifier":
							modificationField.setForeground(Color.ORANGE);
							break;
		    		}
		    	}
		    });
	        
		    createButton.addActionListener(new ActionListener(){
		    	public void actionPerformed(ActionEvent e) {
		    		String element = modificationField.getText();
		    		
		    		ArrayList<String> bricks = breakElement(element);
		    		
		    		if (bricks.size() != 0) {
			    		String typeGiven = bricks.get(0);
			    		
			    		QualifierProperties entry = caracsQualifiers.get(notNestedElements);
			    		String qualifier = entry.getQualifier();
			    		
			    		if (typeGiven.equals("NestedQualifier")) {
							try {
					    		AtomicInteger alternative = entry.getAlternative();
					    		
					    		if (nestedElements.getComponent(1).getClass().getName().equals("javax.swing.JPanel")) {
									Metadata nestedMetadata = metadata.getMetadataOfQualifier(qualifier, alternative.intValue());
									
									nestedElements.remove(1);
									JPanel nestedContent = new AnnotationsComponent(nestedMetadata, true);
									nestedElements.add(nestedContent, 1);
									
									nestedElements.revalidate();
									nestedElements.repaint();
					    		}
					    		
					    		JPanel evolvingNestedContent = (JPanel) nestedElements.getComponent(1);
					    		
					    		JTabbedPane tabbedPane = (JTabbedPane) evolvingNestedContent.getComponent(0);
					    		JPanel paneAnnotations = (JPanel) tabbedPane.getComponent(0);
					    		JPanel paneQualifiers = (JPanel) paneAnnotations.getComponent(1);
					    		
					    		String nestedQualifier = bricks.get(1);
					    		if (!nestedQualifier.equals("")) {
					    			((AnnotationsComponent) evolvingNestedContent).createQualifierBloc(nestedQualifier, paneQualifiers, false);
					    		}
					    		
					    		nestedElements.setVisible(true);
								if (!hideButton.getShow()) {
									clicToHideNested(nestedElements);
								}
					    		
					    		revalidate();
					    		repaint();
					    		
					    		modificationField.setText("");
							} catch (Exception e1) {
								e1.printStackTrace();
								JOptionPane.showMessageDialog(null, e1.getMessage(), "Error Message", JOptionPane.ERROR_MESSAGE);
							}
			    		} else {
				    		String typeAsked = getTypeQualifier(qualifier);
				    		
				    		if (typeAsked == null || typeAsked.equals(typeGiven)) {
				    			
				    			boolean result = populateElementsBloc(notNestedElements, bricks, true);
				    			if (result) {
				    				modificationField.setText("");
				    			}
				    			
				    			paneQualifier.revalidate();
				    			paneQualifier.repaint();
				    		} else {
				    			JOptionPane.showMessageDialog(null,
				    				"Error: This qualifier is of type "+typeAsked+" not of type "+typeGiven+" for a "+metadata.getType()+".", "Error Message",
				    				JOptionPane.ERROR_MESSAGE);
				    		}
			    		}
		    		}
		    		
		    		revalidate();
		    		repaint();
		    	}
		    });
		    
		    // concerning the apparition of the erase button for the qualifier bloc
		    paneQualifier.addComponentListener(new ComponentListener() {
				@Override
				public void componentResized(ComponentEvent e) {
					QualifierProperties qual = caracsQualifiers.get(notNestedElements);
					String qualString = qual.getQualifier();
					int altInt = qual.getAlternative().intValue();
					
					try {
						if (!metadata.isAnnotationNotEmpty(qualString, altInt)) {
		    				if (!eraseButton.isVisible()) {
		    					eraseButton.setVisible(true);
		    				}
						} else {
							if (eraseButton.isVisible()) {
		    					eraseButton.setVisible(false);
		    				}
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
					// if the type of the qualifier has changed we update the blocs accordingly
					hasQualifierTypeChanged(qualString);
				}

				@Override
				public void componentMoved(ComponentEvent e) { }

				@Override
				public void componentShown(ComponentEvent e) { }

				@Override
				public void componentHidden(ComponentEvent e) { }
		    });
		    
		    // concerning the tab actions
		    paneQualifier.setFocusable(true);
		    
		    Action tabAfterAction = new AbstractAction() {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					JPanel currentPaneQualifier = (JPanel) e.getSource();
					changeOfQualifierBloc(currentPaneQualifier, true);
		        }
		    };
		    paneQualifier.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "tabAfterAction");
		    paneQualifier.getActionMap().put("tabAfterAction", tabAfterAction);
		    
		    Action tabBeforeAction = new AbstractAction() {
				private static final long serialVersionUID = 1L;
				
				public void actionPerformed(ActionEvent e) {
					JPanel currentpaneQualifier = (JPanel) e.getSource();
					changeOfQualifierBloc(currentpaneQualifier, false);
		        }
		    };
		    paneQualifier.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "tabBeforeAction");
		    paneQualifier.getActionMap().put("tabBeforeAction", tabBeforeAction);
		    
		    // concerning the hide/unhide of the paneQualifier
		    Action hideAction = new AbstractAction() {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
		    		if (qualifierElements.isVisible()) {
		    			hideAction(notNestedElements);
		    		}
		    		else {
		    			showAction(notNestedElements);
		    		}
		    		
					
		        }
		    };
		    paneQualifier.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_H, 0), "hideAction");
		    paneQualifier.getActionMap().put("hideAction", hideAction);
		    
		    Action hideAllAction = new AbstractAction() {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					boolean allVisible = true;
					for (JPanel localNotNestedElements: caracsQualifiers.keySet()) {
						JPanel localQualifierElements = (JPanel) localNotNestedElements.getParent();
						
						if (!localQualifierElements.isVisible()) {
							allVisible = false;
						}
					}
					
					if (allVisible) {
						for (JPanel localNotNestedElements: caracsQualifiers.keySet()) {
							hideAction(localNotNestedElements);
						}
					} else {
						for (JPanel localNotNestedElements: caracsQualifiers.keySet()) {
							JPanel localQualifierElements = (JPanel) localNotNestedElements.getParent();
							
							if (!localQualifierElements.isVisible()) {
								showAction(localNotNestedElements);
							}
						}
					}
		        }
		    };
		    paneQualifier.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.SHIFT_DOWN_MASK), "hideAllAction");
		    paneQualifier.getActionMap().put("hideAllAction", hideAllAction);
		    
		    // concerning the erasing of an empty qualifier bloc
		    Action eraseAction = new AbstractAction() {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
                	if (eraseButton.isVisible()) {
                		JPanel currentPaneQualifiers = (JPanel) surroundingsQualifier.getParent();
                		JPanel paneAnnotations;
                		if (nested) {
                			paneAnnotations = (JPanel) currentPaneQualifiers.getParent();
                			JTabbedPane tabbedPane = (JTabbedPane) paneAnnotations.getParent();
                			JPanel nestedContent = (JPanel) tabbedPane.getParent();
                			JPanel nestedElements = (JPanel) nestedContent.getParent();
                			JPanel qualifierElements = (JPanel) nestedElements.getParent();
                			JPanel paneQualifier = (JPanel) qualifierElements.getParent();
                			JPanel paneCreation = (JPanel) paneQualifier.getComponent(1);
                			JPanel paneCards = (JPanel) paneCreation.getComponent(1);
                			JPanel paneElements = (JPanel) paneCards.getComponent(0);
                			JTextField modificationField = (JTextField) paneElements.getComponent(0);
                			
                			modificationField.grabFocus();
                			modificationField.requestFocus();
                		} else {
                			JViewport viewport = (JViewport) currentPaneQualifiers.getParent();
                			JScrollPane scroll = (JScrollPane) viewport.getParent();
                			paneAnnotations = (JPanel) scroll.getParent();
                			
                			JPanel paneCreation = (JPanel) paneAnnotations.getComponent(0);
                			JPanel innerPaneCreation = (JPanel) paneCreation.getComponent(1);
                			JTextField qualifierName = (JTextField) innerPaneCreation.getComponent(0);
                			
                			qualifierName.grabFocus();
                			qualifierName.requestFocus();
                		}

                		eraseButton.doClick();
                	}
		        }
		    };
		    paneQualifier.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "eraseAction");
		    paneQualifier.getActionMap().put("eraseAction", eraseAction);
		    
		    // display when the focus is gained/lost
		    paneQualifier.addFocusListener(new FocusListenerUpdatingBorders(Color.black, Color.gray));
		    
		    paneQualifier.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					JComponent component = (JComponent) e.getSource();
					component.grabFocus();
					component.requestFocus();
				}
				@Override
				public void mousePressed(MouseEvent e) { }
				@Override
				public void mouseReleased(MouseEvent e) { }
				@Override
				public void mouseEntered(MouseEvent e) { }
				@Override
				public void mouseExited(MouseEvent e) { }
		    });
		    
    		return surroundingsQualifier;
		}
		return null;
	}
	
	private JPanel createPaneAnnotations() {
		JPanel paneAnnotations = new JPanel();
		paneAnnotations.setLayout(new GridBagLayout());
		
		// content of the annotations panel
		GridBagConstraints gbcAnnotations = new GridBagConstraints();
        gbcAnnotations.weightx = 1.0;
        
        JPanel paneCreation = new JPanel();
        gbcAnnotations.fill = GridBagConstraints.HORIZONTAL;
        gbcAnnotations.weighty = 0.0;
        gbcAnnotations.gridx = 0;
        gbcAnnotations.gridy = 0;
        gbcAnnotations.insets = new Insets(2, 0, 0, 0);
        paneAnnotations.add(paneCreation, gbcAnnotations);
        
	    // panel with the blocks of qualifiers
        final JPanel paneQualifiers = new JPanel();
        
        JScrollPane scrollPaneQualifiers = new JScrollPane(paneQualifiers);
        scrollPaneQualifiers.setBorder(BorderFactory.createEmptyBorder());
        RelativeLayout rl = new RelativeLayout(RelativeLayout.Y_AXIS);
        rl.setFill(true);
        paneQualifiers.setLayout(rl);
        gbcAnnotations.weighty = 1.0;
        gbcAnnotations.fill = GridBagConstraints.BOTH;
        gbcAnnotations.anchor = GridBagConstraints.CENTER;
        gbcAnnotations.gridx = 0;
        gbcAnnotations.gridy = 1;
        if (nested) {
        	paneAnnotations.add(paneQualifiers, gbcAnnotations);
        	rl.setFillGap(5);
        }
        else {
        	paneAnnotations.add(scrollPaneQualifiers, gbcAnnotations);
        }
        
        if (!nested) {
	        paneCreation.setLayout(new BorderLayout());
	        
	        JLabel labelAnnotations = new JLabel("Annotations:");
	        paneCreation.add(labelAnnotations, BorderLayout.WEST);
	        
	        JPanel innerPaneCreation = new JPanel();
	        paneCreation.add(innerPaneCreation, BorderLayout.EAST);
        	
            final JTextField qualifierName = new JTextField(15);
            innerPaneCreation.add(qualifierName);
            
            EventQueue.invokeLater(new Runnable() {
	        	@Override
	        	public void run() {
	        		qualifierName.grabFocus();
	        		qualifierName.requestFocus();
	        	}
	    	});
    		
    	    final CircleButton createButton = new CircleButton("+", true);
    	    innerPaneCreation.add(createButton);
    	    
            qualifierName.addActionListener(new ActionListener(){  
    	    	public void actionPerformed(ActionEvent e) {
    	    		
    	    		createButton.doClick();
    	    	}
    	    });
            
    	    createButton.addActionListener(new ActionListener(){  
    	    	public void actionPerformed(ActionEvent e) {
    	    		
    	    		String qualifier = qualifierName.getText();
    	    		qualifierName.setText("");
    	    		
    	    		try {
						createQualifierBloc(qualifier, paneQualifiers, false);
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage(), "Error Message", JOptionPane.ERROR_MESSAGE);
					}
    	    		revalidate();
    	    		repaint();
    	    	}
    	    });
    	    
    	    Action tabAfter = new AbstractAction() {
    			private static final long serialVersionUID = 1L;

    			public void actionPerformed(ActionEvent e) {
    				if (caracsQualifiers.size() > 0) {
        				String newQualifier = indexQualifiers.get(0);
        				int newAlternative = 0;
        				
        				goToNewNotNestedElements(newQualifier, newAlternative);
    				}
    	        }
    	    };
    	    qualifierName.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "tabafter");
    	    qualifierName.getActionMap().put("tabafter", tabAfter);
    	    
    	    Action tabBefore = new AbstractAction() {
    			private static final long serialVersionUID = 1L;
    			
    			public void actionPerformed(ActionEvent e) {
    				if (caracsQualifiers.size() > 0) {
        				String newQualifier = indexQualifiers.get(indexQualifiers.size()-1);
        				int newAlternative = indexQualifiers.lastIndexOf(newQualifier)-indexQualifiers.indexOf(newQualifier);
        				goToNewNotNestedElements(newQualifier, newAlternative);
    				}
    	        }
    	    };
    	    qualifierName.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "tabbefore");
    	    qualifierName.getActionMap().put("tabbefore", tabBefore);
        }

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
        		if (!(evt.getNewValue() instanceof JComponent)) {
        			return;
        		}
        		JComponent focused = (JComponent) evt.getNewValue();
        		
        		if (paneQualifiers.isAncestorOf(focused)) {
        			
            		if (focused instanceof JPanel) {
                		focused.scrollRectToVisible(focused.getBounds());
                		
            		} else if (focused instanceof JTextField) {
	        			JPanel newPaneElements = (JPanel) focused.getParent();
	        			JPanel newPaneCards = (JPanel) newPaneElements.getParent();
	        			JPanel newPaneCreation = (JPanel) newPaneCards.getParent();
	        			JPanel newPaneQualifier = (JPanel) newPaneCreation.getParent();
	        			
	        			newPaneQualifier.scrollRectToVisible(newPaneQualifier.getBounds());
            		}
        		}
			}
        });
	    
		return paneAnnotations;
	}
	
	private JPanel createPaneNotes() {
		final JPanel paneNotes = new JPanel();
        
        paneNotes.setLayout(new BorderLayout());
		
        final JTextArea areaNotes = new JTextArea();
        areaNotes.setLineWrap(true);
        areaNotes.setWrapStyleWord(true);
        
        JScrollPane scrollPaneNotes = new JScrollPane(areaNotes);
        paneNotes.add(scrollPaneNotes, BorderLayout.CENTER);
        
        areaNotes.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updatePanel();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            	updatePanel();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            	updatePanel();
            }

            private void updatePanel() {
            	areaNotes.setBackground(Color.ORANGE);
            }
        });
        
        areaNotes.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				
			}

			@Override
			public void focusLost(FocusEvent e) {
				String text = areaNotes.getText();
				metadata.setNotes(text);
				
				areaNotes.setBackground(Color.WHITE);
			}
        });
        
        if (nested) {
	        areaNotes.getDocument().addDocumentListener(new DocumentListener() {
	        	int rowLimit = 8;
	
	            @Override
	            public void insertUpdate(DocumentEvent e) {
	                updateLineCount();
	            }
	
	            @Override
	            public void removeUpdate(DocumentEvent e) {
	                updateLineCount();
	            }
	
	            @Override
	            public void changedUpdate(DocumentEvent e) {
	                updateLineCount();
	            }
	
	            private void updateLineCount() {
	                int lineCount = areaNotes.getLineCount();
	                if (lineCount <= rowLimit) {
	                	areaNotes.setRows(lineCount);
	                	paneNotes.revalidate();
	                }
	            }
	        });
        }
        
        return paneNotes;
	}
    
    private AnnotationsComponent(boolean newNested)
    {
    	super();
    	
    	this.nested = newNested;

        if (!nested) {
			setLayout(new GridBagLayout());
			GridBagConstraints gbcMain = new GridBagConstraints();
			gbcMain.weightx = 1.0;
	        gbcMain.weighty = 1.0;
	        gbcMain.fill = GridBagConstraints.BOTH;
	        gbcMain.anchor = GridBagConstraints.CENTER;
	        
	        JPanel fullPaneAnnotations = new JPanel();
	        fullPaneAnnotations.setLayout(new GridBagLayout());
	        JPanel fullPaneNotes = new JPanel();
	        fullPaneNotes.setLayout(new GridBagLayout());
			
			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fullPaneAnnotations, fullPaneNotes);
			splitPane.setResizeWeight(0.5);
			splitPane.setOneTouchExpandable(true);
			add(splitPane, gbcMain);
			
			JPanel paneAnnotations = createPaneAnnotations();
	        
	        gbcMain.insets = new Insets(0, 10, 10, 10);
	        fullPaneAnnotations.add(paneAnnotations, gbcMain);
			
	        gbcMain.gridy = 0;
	        gbcMain.weighty = 0.0;
	        gbcMain.insets = new Insets(5, 10, 0, 0);
	        JLabel labelNotes = new JLabel("Notes:");
	        fullPaneNotes.add(labelNotes, gbcMain);
	        
	        gbcMain.gridy = 1;
	        gbcMain.weighty = 1.0;
	        gbcMain.insets = new Insets(8, 10, 10, 10);
	        JPanel scrollPaneNotes = createPaneNotes();
	        fullPaneNotes.add(scrollPaneNotes, gbcMain);
        }
        else {
	        setLayout(new GridBagLayout());
	        
	        GridBagConstraints gbc = new GridBagConstraints();
	        gbc.weightx = 1.0;
	        gbc.fill = GridBagConstraints.BOTH;
	        gbc.anchor = GridBagConstraints.CENTER;
	        
        	JTabbedPane tabbedPane = new JTabbedPane();

        	JPanel paneAnnotations = createPaneAnnotations();
        	tabbedPane.addTab("Annotations", paneAnnotations);
        	
        	JPanel scrollPaneNotes = createPaneNotes();
        	tabbedPane.addTab("Notes", scrollPaneNotes);
        	
        	tabbedPane.setFocusable(false);
        	
        	add(tabbedPane, gbc);
        }
    }
    
    public AnnotationsComponent(Metadata newMetadata, boolean newNested)
    {
    	this(newNested);
    	
    	this.metadata = newMetadata;
    	
		JPanel paneAnnotations;
		JPanel paneNotes;
		if (getComponent(0) instanceof JTabbedPane) {
			JTabbedPane tabbedPane = (JTabbedPane) getComponent(0);
			paneAnnotations = (JPanel) tabbedPane.getComponent(0);
			paneNotes = (JPanel) tabbedPane.getComponent(1);
		}
		else {
			JSplitPane splitPane = (JSplitPane) getComponent(0);
			JPanel fullPaneAnnotations = (JPanel) splitPane.getComponent(0);
			JPanel fullPaneNotes = (JPanel) splitPane.getComponent(1);
			paneAnnotations = (JPanel) fullPaneAnnotations.getComponent(0);
			paneNotes = (JPanel) fullPaneNotes.getComponent(1);
		}
		Component intermediate = paneAnnotations.getComponent(1);
		
		JPanel paneQualifiers;
		if (intermediate.getClass().getName().equals("javax.swing.JScrollPane")) {
			JScrollPane scroll = (JScrollPane) intermediate;
			paneQualifiers = (JPanel) scroll.getViewport().getView();
		}
		else {
			paneQualifiers = (JPanel) intermediate;
		}
    	
    	JSONArray arrayQualifiers;
		try {
			arrayQualifiers = this.metadata.getJSONOfMetadata(false);

			for(int idQualifier = 0; idQualifier < arrayQualifiers.length(); idQualifier++)
			{
				JSONObject jsonQualifier = arrayQualifiers.getJSONObject(idQualifier);
				
				String qualifierName = jsonQualifier.getString("qualifier");
				String qualifierClass = jsonQualifier.getString("type");
				
				JSONArray arrayAlternatives = jsonQualifier.getJSONArray("alternatives");
				int numAltJson = arrayAlternatives.length();
				
				JSONObject jsonAlternative;
				for(int idAlternative = 0; idAlternative < numAltJson; idAlternative++)
				{
					JPanel surroundingsQualifier = createQualifierBloc(qualifierName, paneQualifiers, true);
					JPanel paneQualifier = (JPanel) surroundingsQualifier.getComponent(0);
					JPanel qualifierElements = (JPanel) paneQualifier.getComponent(3);
					JPanel notNestedElements = (JPanel) qualifierElements.getComponent(0);
					
					jsonAlternative = arrayAlternatives.getJSONObject(idAlternative);
					if (qualifierClass.equals("GenericAnnotation")) {
						
						if (jsonAlternative.has("uris") && !jsonAlternative.isNull("uris")) {
							JSONArray arrayURIs = jsonAlternative.getJSONArray("uris");
							for(int idUri = 0; idUri < arrayURIs.length(); idUri++)
							{
								ArrayList<String> array = new ArrayList<String>();
								array.add("GenericAnnotation");
								
								JSONObject jsonURI = arrayURIs.getJSONObject(idUri);
								
								String collection = jsonURI.getString("collection");
								String identifier = jsonURI.getString("identifier");
								array.add("uri");
								array.add(collection);
								array.add(identifier);
								
				    			populateElementsBloc(notNestedElements, array, false);
							}
						}
						if (jsonAlternative.has("tags") && !jsonAlternative.isNull("tags")) {
							JSONArray arrayTags = jsonAlternative.getJSONArray("tags");
							for(int idTag = 0; idTag < arrayTags.length(); idTag++)
							{
								ArrayList<String> array = new ArrayList<String>();
								array.add("GenericAnnotation");
								
								String tag = arrayTags.getString(idTag);
								array.add("tag");
								array.add(tag);
	
				    			populateElementsBloc(notNestedElements, array, false);
							}
						}
						if (jsonAlternative.has("keysvalues") && !jsonAlternative.isNull("keysvalues")) {
							JSONArray arrayKeys = jsonAlternative.getJSONArray("keysvalues");
							for(int idKey = 0; idKey < arrayKeys.length(); idKey++)
							{
								JSONObject jsonKey = arrayKeys.getJSONObject(idKey);
								JSONArray arrayValues = jsonKey.getJSONArray("values");
								
								for (int idValue = 0; idValue < arrayValues.length(); idValue++) {
									ArrayList<String> array = new ArrayList<String>();
									array.add("GenericAnnotation");
									array.add("keyvalue");
									
									String key = jsonKey.getString("key");
									String value = arrayValues.getString(idValue);
									array.add(key);
									array.add(value);
									
					    			populateElementsBloc(notNestedElements, array, false);
								}
							}
						}
					}
					else if (qualifierClass.equals("AuthorsAnnotation")) {
						jsonAlternative = arrayAlternatives.getJSONObject(0);
						
						if (jsonAlternative.has("authors") && !jsonAlternative.isNull("authors")) {
							JSONArray arrayAuthors = jsonAlternative.getJSONArray("authors");
							for(int idAuthor = 0; idAuthor < arrayAuthors.length(); idAuthor++)
							{
								ArrayList<String> array = new ArrayList<String>();
								array.add("AuthorsAnnotation");
								
								JSONObject author = arrayAuthors.getJSONObject(idAuthor);
								String name = author.getString("name");
								String surname = author.getString("surname");
								String email = null;
								if (author.has("email") && !author.isNull("email")) { email = author.getString("email"); }
								String organisation = null;
								if (author.has("organisation") && !author.isNull("organisation")) { organisation = author.getString("organisation"); }
								String orcid = null;
								if (author.has("orcid") && !author.isNull("orcid")) { orcid = author.getString("orcid"); }
								array.add(name);
								array.add(surname);
								array.add(email);
								array.add(organisation);
								array.add(orcid);
								
								populateElementsBloc(notNestedElements, array, false);
							}
						}
					}
					else if (qualifierClass.equals("DateAnnotation")) {
						ArrayList<String> array = new ArrayList<String>();
						array.add("DateAnnotation");
						
						jsonAlternative = arrayAlternatives.getJSONObject(0);
						
						String element = jsonAlternative.getString("date");
						array.add(element);
						
						populateElementsBloc(notNestedElements, array, false);
					}
					else if (qualifierClass.equals("DistributionAnnotation")) {
						ArrayList<String> array = new ArrayList<String>();
						array.add("DistributionAnnotation");
						
						jsonAlternative = arrayAlternatives.getJSONObject(0);
						
						String element = jsonAlternative.getString("distribution");
						array.add(element);
						
						populateElementsBloc(notNestedElements, array, false);
					}
					
					JPanel nestedElements = (JPanel) qualifierElements.getComponent(1);
					
					if (this.metadata.isSetMetadataOfQualifier(qualifierName, idAlternative)) {
						Metadata nestedMetadata = this.metadata.getMetadataOfQualifier(qualifierName, idAlternative);
						
						nestedElements.remove(1);
						JPanel nestedContent = new AnnotationsComponent(nestedMetadata, true);
						nestedContent.setVisible(false);
						nestedElements.add(nestedContent, 1);
						
						nestedElements.setVisible(true);
						JPanel nestedHeader = (JPanel) nestedElements.getComponent(0);
						JPanel innerNestedHeader = (JPanel) nestedHeader.getComponent(0);
						TriangleButton hideButton = (TriangleButton) innerNestedHeader.getComponent(0);
						hideButton.setShow();
					}
					
	    			paneQualifier.revalidate();
	    			paneQualifier.repaint();
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error Message", JOptionPane.ERROR_MESSAGE);
		}
		
		JScrollPane scrollPaneNotes = (JScrollPane) paneNotes.getComponent(0);
		JTextArea areaNotes = (JTextArea) scrollPaneNotes.getViewport().getView();
		areaNotes.setText(metadata.getNotes());
		areaNotes.setBackground(Color.WHITE);
    }
}