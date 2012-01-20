package org.ginsim.gui.notifications;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ginsim.common.utils.Translator;
import org.ginsim.core.notification.Notification;
import org.ginsim.core.notification.detailed.DetailedNotification;
import org.ginsim.core.notification.resolvable.ResolvableNotification;


/**
 * A panel to show interactive notification messages.
 * 
 * @author Aurelien Naldi
 */
public class NotificationPanel extends JPanel {
	private static final long serialVersionUID = -7922279455055377865L;

	private final NotificationSource _source;
    private JLabel notificationMessage = null;
    private JButton bcloseNotification = null;
    private JComboBox cNotificationAction = null;
    private JButton bNotificationAction = null;
    private JButton bNotificationAction2 = null;
    private Notification notification = null;
	

	public NotificationPanel(NotificationSource source) {
		this._source = source;
		setVisible(notification != null);
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
        c.insets = new Insets(0,10,0,10);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		notificationMessage = new JLabel("no notification");
		add(notificationMessage, c);

		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
        bNotificationAction = new JButton();
        add(bNotificationAction, c);
        bNotificationAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                notificationAction(0);
            }
        });

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.EAST;
        bNotificationAction2 = new JButton();
        add(bNotificationAction2, c);
        bNotificationAction2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                notificationAction(1);
            }
        });
        cNotificationAction = new JComboBox();
        add(cNotificationAction, c);

		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 0;
        c.insets = new Insets(0,10,0,0);
		c.anchor = GridBagConstraints.EAST;
		bcloseNotification = new JButton(Translator.getString( "STR_close"));
		add(bcloseNotification, c);
		bcloseNotification.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_source.closeNotification();
			}
		});
	}

	protected void notificationAction(int index) {
		
		if (notification != null){
			
			if( notification instanceof ResolvableNotification) {
	            if (index == 0) {
	                if (cNotificationAction.isVisible()) {
	                    ((ResolvableNotification) notification).performResolution( cNotificationAction.getSelectedIndex());
	                    return;
	                }
	            }
	            ((ResolvableNotification) notification).performResolution(index);
			}
			else if( notification instanceof DetailedNotification){
				if (index == 0){
					new NotificationDetailFrame( ((DetailedNotification) notification).getDetails());
				}
			}
		}
		
		
		
		_source.closeNotification();
		
	}

	public synchronized void updateNotificationMessage() {
		
		// If the notification panel is visible, it means a notification is currently displayed
		if( isVisible()){
			return;
		}
		notification = _source.getTopNotification();
		if (notification == null) {
			return;
		} else {
            switch (notification.getType()) {
            	case Notification.NOTIFICATION_INFO:
            	case Notification.NOTIFICATION_INFO_LONG:
            		setBackground(Color.CYAN);
                break;
            	case Notification.NOTIFICATION_WARNING:
            	case Notification.NOTIFICATION_WARNING_LONG:
            		setBackground(Color.ORANGE);
                break;
            	case Notification.NOTIFICATION_ERROR:
            	case Notification.NOTIFICATION_ERROR_LONG:
            		setBackground(Color.RED);
                break;
            default:
                setBackground( null);
                break;
            }

			setVisible(true);
			notificationMessage.setText( Translator.getString( notification.getMessage()));
            String[] t_text = null;
            if( notification instanceof ResolvableNotification){
            	String[] option_names = ((ResolvableNotification) notification).getOptionNames();
            	if( option_names!= null && option_names.length > 0){
            		t_text = Translator.getStrings( option_names);
            	}
            	else{
            		t_text = new String[0];
            	}
            }
            else if( notification instanceof DetailedNotification){
            	t_text = new String[1];
            	t_text[0] = Translator.getString( "STR_showNotificationDetails");
            }
            else{
            	t_text = new String[0];
            }
			if (t_text != null && t_text.length > 0) {
                bNotificationAction.setVisible(true);
                if ( t_text.length == 1) {
                    cNotificationAction.setVisible(false);
                    bNotificationAction2.setVisible(false);
                    bNotificationAction.setText( t_text[0]);
                    bNotificationAction.requestFocusInWindow();
                } else if ( t_text.length == 2) {
                    bNotificationAction.setText( t_text[0]);
                    bNotificationAction2.setText( t_text[1]);
                    bNotificationAction2.setVisible(true);
                    cNotificationAction.setVisible(false);
                    bNotificationAction2.requestFocusInWindow();
                } else {
                    cNotificationAction.setVisible(true);
                    bNotificationAction2.setVisible(false);
                    bNotificationAction.setText( Translator.getString( "STR_OK"));
                    cNotificationAction.setModel(new DefaultComboBoxModel(t_text));
                    cNotificationAction.requestFocusInWindow();
                }
			} else {
                bNotificationAction.setVisible(false);
                bNotificationAction2.setVisible(false);
                cNotificationAction.setVisible(false);
                bcloseNotification.requestFocusInWindow();
			}
		}
	}

}
