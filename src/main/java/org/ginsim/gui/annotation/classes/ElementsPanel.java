package org.ginsim.gui.annotation.classes;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import org.colomoto.biolqm.metadata.annotations.Annotation;
import org.colomoto.biolqm.metadata.annotations.URI;
import org.colomoto.biolqm.metadata.constants.Collection;

/**
 * Display the content of a single (qualified) annotation block,
 * composed of collections of URIs, tags and key:value pairs.
 *
 * @author Martin Boutroux
 * @author Aurelien Naldi
 */
class ElementsPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final Color SELECTED = Color.ORANGE.brighter().brighter();
	private static final Color NORMAL = Color.GRAY.brighter();

	private static final Border SELECTED_BORDER = BorderFactory.createBevelBorder(BevelBorder.RAISED, SELECTED, SELECTED);
	private static final Border NORMAL_BORDER = BorderFactory.createBevelBorder(BevelBorder.RAISED, NORMAL, NORMAL);

	private boolean selected = false;

	private final Annotation annotation;
	private final JPanel content = new JPanel();
	private final JPanel header = new JPanel();
	private final JLabel qualifierLabel = new JLabel();
	private final JLabel summaryLabel = new JLabel();

	ElementsPanel(Annotation annotation) {
		this.annotation = annotation;
		this.setBorder(NORMAL_BORDER);

		// prepare the header
		header.setBackground(NORMAL);
		header.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2,5,2,5);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		header.add(new TriangleButton(), gbc);
		gbc.gridx = 2;
		header.add(qualifierLabel, gbc);
		gbc.anchor = GridBagConstraints.EAST;
		gbc.gridx = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		header.add(summaryLabel, gbc);

		this.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		this.add(header, gbc);
		gbc.gridy++;
		this.add(content, gbc);

		refresh();
	}

	private void refresh() {
		this.content.removeAll();

		if (this.annotation.qualifier == null) {
			this.qualifierLabel.setText("(no qualifier)");
		} else {
			this.qualifierLabel.setText(annotation.qualifier.term);
		}

		summaryLabel.setText(annotation.getShortDescription());

		for (URI uri: annotation.uris) {
			Collection col = uri.getCollection();
			if (col == null) {
				content.add(new JLabel(uri.getValue()));
			} else {
				content.add(new JLabel(col.name+":"+ uri.getValue()));
			}
		}

		for (Map.Entry<String,String> e: annotation.keyValues.entrySet()) {
			content.add(new JLabel(e.getKey() +":"+e.getValue()));
		}

		for (String tag: annotation.tags) {
			content.add(new JLabel("#"+tag));
		}

	}

	protected void setSelection(Annotation select) {
		if (this.annotation == select) {
			this.setBorder(SELECTED_BORDER);
			this.header.setBackground(SELECTED);
		} else {
			this.setBorder(NORMAL_BORDER);
			this.header.setBackground(NORMAL);
		}
	}
	
	void removeElement(String element) {
		// FIXME: remove element?

		if (element.matches("^#.+")) {
//			metadata.removeTag(qualifier, alternative.intValue(), element.split("#", 2)[1]);
		} else if (element.matches(".*;.*;.*;.*;.*")) {
			ArrayList<String> array = new ArrayList<String>();
			
			String[] elementBroken = element.split(";", 5);
			for (String piece: elementBroken) {
				if (piece.equals("") || piece.equals("null")) {
					array.add(null);
				} else {
					array.add(piece);
				}
			}
//			metadata.removeAuthor(qualifier, array.get(0), array.get(1), array.get(2), array.get(3), array.get(4));
		} else {
//			metadata.removeURI(qualifier, alternative.intValue(), element);
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
	
	void addURI(String type, String element) {
		JPanel panelElement = new JPanel();
        
		this.addElement(panelElement, element);
		
		JLabel labelElement = (JLabel) panelElement.getComponent(0);
		
		switch (type) {
			case "miriam":
				this.setLinkLabel(panelElement, labelElement, "https://identifiers.org/");
				break;
			case "url":
				this.setLinkLabel(panelElement, labelElement, "");
				break;
		}
		
		
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
