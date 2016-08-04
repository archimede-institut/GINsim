package org.ginsim.servicegui.tool.avatar.others;

import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * Action listener with internal state given by a key and a component
 * @author Rui Henriques
 * @version 1.0
 */
public abstract class IndexableActionListener implements ActionListener{
	
    private final String key;
    private final JDialog apanel;
    private final JFrame frame;
    
    /**
     * Creates an action listener with internal state
     * @param _key the associated key
     * @param _panel the associated panel
     */
    public IndexableActionListener(final String _key, final JDialog _panel){
        super();
        this.key = _key;
        this.apanel = _panel;
        this.frame = null;		
	}
    
    /**
     * Creates an action listener with internal state
     * @param output the associated component
     */
    public IndexableActionListener(final JFrame output) {
		super();
        this.key = "";
        this.apanel = null;
        this.frame = output;		
	}
    
    /**
     * @return the internal key
     */
    public String getKey(){
    	return key;
    }
    
    /**
     * @return the associated frame
     */
    public JFrame getFrame(){
    	return frame;
    }
    
    /**
     * @return the associated panel
     */
    public JDialog getDialog(){
    	return apanel;
    }
}