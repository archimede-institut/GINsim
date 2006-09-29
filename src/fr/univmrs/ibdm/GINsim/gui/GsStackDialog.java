package fr.univmrs.ibdm.GINsim.gui;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.univmrs.ibdm.GINsim.manageressources.Translator;

/**
 * a generic stackable dialog.
 * It offers run/cancel buttons, a message area and a stackable main part.
 * All this is accessible via a simple API to set the main dialog and add
 * secondary dialogs or messages.
 */
abstract public class GsStackDialog extends JDialog {
    private static final long serialVersionUID = -6696566567870168910L;
    
    private static final String s_mainkey = "_main";
    
    JLabel messageLabel = new JLabel();
    CardLayout cards;
    JPanel mainPanel;
    JPanel bottomPanel;
    protected JButton brun;
    protected JButton bcancel;
    protected JButton bclose;
    
    public GsStackDialog(JFrame parent) {
        super(parent);
        JPanel contentPane = new JPanel();
        mainPanel = new JPanel();
        cards = new CardLayout();
        mainPanel.setLayout(cards);
        
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.weightx = 1;
        contentPane.add(mainPanel, c);
        //contentPane.add(new JLabel("salut"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        contentPane.add(getBottomPanel(), c);
        setContentPane(contentPane);
        setVisible(true);
    }
    
    /**
     * set the progress level, to give the user some feedback
     * @param s
     */
    public void setMessage(String message) {
    	setMessage(message, 0);
    }
    

    public void setMessage(String message, int timeout) {
        if (messageLabel != null) {
        	messageLabel.setText(message);
        }
        if (timeout > 0) {
            // TODO: implement timeout!
        }
    }
    
    public void setMainPanel(Component panel) {
        mainPanel.add(panel, s_mainkey);
        cards.show(mainPanel, s_mainkey);
    }
    
    public void addSecondaryPanel(Component panel, String name) {
        mainPanel.add(panel, name);
    }
    
    public void setVisiblePanel(String name) {
        if (name == null) {
            // show main panel
            cards.show(mainPanel, s_mainkey);
            bclose.setVisible(false);
            brun.setVisible(true);
            bcancel.setVisible(true);
            return;
        }
        cards.show(mainPanel, name);
        bclose.setVisible(true);
        brun.setVisible(false);
        bcancel.setVisible(false);
    }
    
    /**
     * This method initializes buttonCancel
     * 
     * @return javax.swing.JButton
     */
    private javax.swing.JButton getBcancel() {
        if(bcancel == null) {
        	bcancel = new javax.swing.JButton(Translator.getString("STR_cancel"));
        	bcancel.addActionListener(new java.awt.event.ActionListener() { 
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    cancel();
                }
            });
        }
        return bcancel;
    }
    private javax.swing.JButton getBclose() {
        if(bclose == null) {
        	bclose = new javax.swing.JButton(Translator.getString("STR_close"));
        	bclose.addActionListener(new java.awt.event.ActionListener() { 
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    setVisiblePanel(null);
                }
            });
        }
        return bclose;
    }
    /**
     * This method initializes buttonRun
     * 
     * @return javax.swing.JButton
     */
    private javax.swing.JButton getBrun() {
        if(brun == null) {
        	brun = new javax.swing.JButton(Translator.getString("STR_run"));
        	brun.setActionCommand("run");
        	brun.addActionListener(new java.awt.event.ActionListener() { 
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    run();
                }
            });
        }
        return brun;
    }
    private JPanel getBottomPanel() {
        if (bottomPanel == null) {
            bottomPanel = new JPanel();
            bottomPanel.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 3;
            c.gridy = 1;
            c.anchor = GridBagConstraints.EAST;
            bottomPanel.add(getBrun(), c);
            c = new GridBagConstraints();
            c.gridx = 2;
            c.gridy = 1;
            c.anchor = GridBagConstraints.EAST;
            bottomPanel.add(getBcancel(), c);
            bottomPanel.add(getBclose(), c);
            c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 1;
            c.weightx = 1;
            c.anchor = GridBagConstraints.WEST;
            bottomPanel.add(messageLabel, c);
        }
        return bottomPanel;
    }
    
    abstract protected void run(); 
    abstract protected void cancel(); 
}
