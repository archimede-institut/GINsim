package fr.univmrs.tagc.widgets;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.util.widget.GsDialog;

/**
 * a generic stackable dialog.
 * It offers run/cancel buttons, a message area and a stackable main part.
 * All this is accessible via a simple API to set the main dialog and add
 * secondary dialogs or messages.
 */
abstract public class StackDialog extends GsDialog {
    private static final long serialVersionUID = -6696566567870168910L;
    
    private static final String s_mainkey = "_main";
    
    JLabel messageLabel = new JLabel();
    CardLayout cards;
    JPanel mainPanel;
    JPanel bottomPanel;
    Component tmpPanel = null;
    protected JButton brun;
    protected JButton bcancel;
    protected JButton bclose;
    
    public StackDialog(JFrame parent, String id, int w, int h) {
        super(parent, id, w, h);
        JPanel contentPane = (JPanel)getContentPane();
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
        setVisible(true);
    }
    
    public void addSecondaryPanel(Component panel, String name) {
        mainPanel.add(panel, name);
    }

    public void addTempPanel(Component panel) {
    	tmpPanel = panel;
        mainPanel.add(tmpPanel, "_tmp");
        setVisiblePanel("_tmp");
    }
    
    public void setVisiblePanel(String name) {
    	if (!"_tmp".equals(name) && tmpPanel != null) {
    		mainPanel.remove(tmpPanel);
    		tmpPanel = null;
    	}
        if (name == null) {
            // show main panel
            cards.show(mainPanel, s_mainkey);
            bclose.setVisible(false);
            brun.setVisible(true);
            bcancel.setVisible(true);
            mainPanel.grabFocus();
            return;
        }
        cards.show(mainPanel, name);
        mainPanel.grabFocus();
        bclose.setVisible(true);
        brun.setVisible(false);
        bcancel.setVisible(false);
    }
    
    protected void escape() {
    	if (bclose.isVisible()) {
    		setVisiblePanel(null);
    	} else {
    		cancel();
    	}
    }
    
    /**
     * This method initializes buttonCancel
     * 
     * @return javax.swing.JButton
     */
    private javax.swing.JButton getBcancel() {
        if(bcancel == null) {
        	bcancel = new javax.swing.JButton(Translator.getString("STR_cancel"));
        	// TODO: get some nice default mnemonices everywhere 
        	bcancel.getModel().setMnemonic(KeyEvent.VK_N);
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
        	bclose.getModel().setMnemonic(KeyEvent.VK_E);
        	bclose.addActionListener(new java.awt.event.ActionListener() { 
                public void actionPerformed(java.awt.event.ActionEvent e) {
                	refreshMain();
                    setVisiblePanel(null);
                }
            });
        	bclose.setVisible(false);
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
        	brun.getModel().setMnemonic(KeyEvent.VK_R);
        	brun.setActionCommand("run");
        	brun.addActionListener(new java.awt.event.ActionListener() { 
                public void actionPerformed(java.awt.event.ActionEvent e)  {
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
    /**
     * called when the main panel takes the focus back
     */
    protected void refreshMain() {
    }
    
    public void doClose() {
    	cancel();
    }
    protected void cancel() {
    	setVisible(false);
    }
    
    protected void setRunning(boolean b) {
    	brun.setEnabled(b==false);
    }
}
