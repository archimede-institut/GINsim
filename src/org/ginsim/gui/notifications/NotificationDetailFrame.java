package org.ginsim.gui.notifications;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;
import javax.swing.SwingConstants;

import org.ginsim.common.utils.Translator;

public class NotificationDetailFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public NotificationDetailFrame( String text) {
	
	    JFrame frame = new JFrame( Translator.getString( "STR_notificationDetailsTitle"));
	
	    //Add content to the window.
	    frame.add( new NotificationDetailPanel( text));
	
	    //Display the window.
	    frame.pack();
	    frame.setVisible(true);
	}
	

public class NotificationDetailPanel extends JPanel{
	
	public NotificationDetailPanel( String text) {

		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		
		JLabel theLabel = new JLabel( text) {
            public Dimension getPreferredSize() {
                return new Dimension(500, 500);
            }
            public Dimension getMinimumSize() {
                return new Dimension(500, 500);
            }
            
        };
        theLabel.setVerticalAlignment(SwingConstants.TOP);
        theLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JScrollPane rightPanel = new JScrollPane( theLabel);

        setBorder( BorderFactory.createEmptyBorder(10,10,10,10));
        add(rightPanel);
    }
}

}
