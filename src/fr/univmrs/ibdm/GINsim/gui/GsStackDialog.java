package fr.univmrs.ibdm.GINsim.gui;

import java.awt.CardLayout;
import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * a generic stackable dialog.
 * It offers run/cancel buttons, a message area and a stackable main part.
 * All this is accessible via a simple API to set the main dialog and add
 * secondary dialogs or messages.
 */
public class GsStackDialog extends JDialog {
    private static final long serialVersionUID = -6696566567870168910L;
    
    private static final String s_mainkey = "_main";
    
    JLabel messageLabel;
    CardLayout cards;
    JPanel mainPanel;
    JButton brun;
    JButton bcancel;
    
    public GsStackDialog(JFrame parent) {
        super(parent);
        JPanel contentPane = new JPanel();
        mainPanel = new JPanel();
        cards = new CardLayout();
        mainPanel.setLayout(cards);
        
        setContentPane(contentPane);
        setVisible(true);
    }
    
    
    public void setMessage(String message, int timeout) {
        
        // TODO: implement timeout!
    }
    
    public void setMainPanel(Component panel) {
        cards.addLayoutComponent(panel, s_mainkey);
    }
    
    public void addSecondaryPanel(Component panel, String name) {
        cards.addLayoutComponent(panel, name);
    }
    
    public void setVisiblePanel(String name) {
        if (name == null) {
            // show main panel
            cards.show(mainPanel, s_mainkey);
            return;
        }
        cards.show(mainPanel, name);
    }
}
