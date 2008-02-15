package fr.univmrs.tagc.common.widgets;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import fr.univmrs.tagc.common.manageressources.ImageLoader;
import fr.univmrs.tagc.common.manageressources.Translator;

public class Label extends JLabel {
	private static final long serialVersionUID = 6165159464465715839L;
	
	public static final int MESSAGE_NORMAL = 0;
	public static final int MESSAGE_WARNING = 1;
	public static final int MESSAGE_ERROR = 2;
	
	public Label(String text, int type) {
		setText(Translator.getString(text));
		setBorder(BorderFactory.createEtchedBorder());
		switch (type) {
		case MESSAGE_WARNING:
			setIcon(ImageLoader.getImageIcon("suppr.gif"));
			setBackground(Color.ORANGE);
			break;
		case MESSAGE_ERROR:
			setIcon(ImageLoader.getImageIcon("suppr.gif"));
			setBackground(Color.RED);
			break;
		}
	}
}
