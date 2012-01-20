package org.ginsim.gui.notifications;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JButton;

import org.ginsim.common.utils.Translator;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NotificationDetailFrame extends JFrame {

	private static final long serialVersionUID = 8174381889090560842L;

	private JScrollPane scrollPane;
	private JButton btn_close;

	public NotificationDetailFrame( String text) {
		
		super( Translator.getString( "STR_notificationDetailsTitle"));
		
		getContentPane().setLayout(null);
		setSize( new Dimension( 500, 500));
		
		// Add a scroll pane containing a JLable with the details text
		scrollPane = new JScrollPane();
		scrollPane.setBounds( 12, 12, this.getWidth() - 24, this.getHeight() - 80);
		getContentPane().add( scrollPane);
		
		JLabel lbl_detailstext = new JLabel( text);
		scrollPane.setViewportView( lbl_detailstext);
		
		// Add the close button
		btn_close = new JButton( Translator.getString( "STR_Close"));
		btn_close.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dispose();
			}
		});
		btn_close.setBounds( (int) (this.getWidth()/(float) 2) - 55, this.getHeight() - 60, 110, 25);
		getContentPane().add( btn_close);
		
		// Add a listener that permits to resize the scrollpane and replace the button when window is resized
		addComponentListener( new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				scrollPane.setBounds( 12, 12, e.getComponent().getWidth() - 24, e.getComponent().getHeight() - 80);
				btn_close.setBounds( (int) (e.getComponent().getWidth()/(float) 2) - 55, e.getComponent().getHeight() - 60, 110, 25);
			}
		});
		
		// Display the frame
		setVisible( true);

	}
}
