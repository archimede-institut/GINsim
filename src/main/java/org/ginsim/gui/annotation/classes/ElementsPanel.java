package org.ginsim.gui.annotation.classes;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
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
	
	private void addElement(JPanel panelElement, String element) {
		panelElement.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 1));
        
        JLabel labelElement = new JLabel(element);
        panelElement.add(labelElement);
        
        panelElement.add(Box.createHorizontalStrut(2));
        
        final CircleButton buttonElement = new CircleButton("-", false);
        panelElement.add(buttonElement);
        
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
	
	private void setLinkLabel(JPanel panelElement, JLabel labelElement, String startURL) {
		String element = labelElement.getText();
		
		labelElement.setForeground(Color.BLUE.darker());
		labelElement.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		labelElement.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
	    	        Desktop.getDesktop().browse(new URL(startURL+element).toURI());
	    	    } catch (Exception e1) {
	    	        e1.printStackTrace();
	    	    }
			}

			@Override
			public void mousePressed(MouseEvent e) { }

			@Override
			public void mouseReleased(MouseEvent e) { }

			@Override
			public void mouseEntered(MouseEvent e) {
				labelElement.setText("<html><a href=''>"+element+"</a></html>");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				labelElement.setText(element);
			}
		});
	    
	    Action uriAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				try {
	    	        Desktop.getDesktop().browse(new URL("https://identifiers.org/"+element).toURI());
	    	    } catch (Exception e1) {
	    	        e1.printStackTrace();
	    	    }
	        }
	    };
	    panelElement.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "uriAction");
	    panelElement.getActionMap().put("uriAction", uriAction);
	}
	
	void addTag(String element) {
		JPanel panelElement = new JPanel();
        
		this.addElement(panelElement, element);
		panelElement.add(Box.createHorizontalStrut(4));
	}
	
	void addURI(String element) {
		JPanel panelElement = new JPanel();
        
		this.addElement(panelElement, element);
		
		JLabel labelElement = (JLabel) panelElement.getComponent(0);
		this.setLinkLabel(panelElement, labelElement, "https://identifiers.org/");
	}
	
	void addAuthor(String name, String surname, String organisation, String email, String orcid) {
		JPanel panelElement = new JPanel();
		panelElement.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 1));
		
		JLabel labelElement = null;
		JLabel labelOrcid = null;
		JLabel labelFinal = null;
		
		String strAuthor = surname+" "+name;
		if (organisation != null) {
			strAuthor += " - "+organisation;
		}
		if (email != null || orcid != null) {
			strAuthor += " (";
			if (email != null) {
				strAuthor += email;
				if (orcid != null) {
					strAuthor += " - ";
				}
			}
			if (orcid != null) {
				labelElement = new JLabel(strAuthor);
				panelElement.add(labelElement);
				strAuthor = "";
				
				labelOrcid = new JLabel(orcid);
				panelElement.add(labelOrcid);
			}
			strAuthor += ")";
			labelFinal = new JLabel(strAuthor);
			panelElement.add(labelFinal);
		}
		
		if (labelOrcid != null) {
			this.setLinkLabel(panelElement, labelOrcid, "https://orcid.org/");
		}
        
        panelElement.add(Box.createHorizontalStrut(2));
        
        final CircleButton buttonElement = new CircleButton("-", false);
        panelElement.add(buttonElement);
        
	    buttonElement.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent event) {
	    		String author = name+";"+surname+";"+organisation+";"+email+";"+orcid;
	    		removeElement(author);
	    		
	    		JComponent button = (JComponent) event.getSource();
	    		JPanel panelElement = (JPanel) button.getParent();
	    		JPanel panelElements = (JPanel) panelElement.getParent();
	    		JPanel panelExternal = (JPanel) panelElements.getParent();
	    		
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
