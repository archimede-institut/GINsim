package fr.univmrs.ibdm.GINsim.util.widget;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import fr.univmrs.ibdm.GINsim.manageressources.ImageLoader;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;

public class GsLabel extends JLabel {
	private static final long serialVersionUID = 6165159464465715839L;
	
	public static final int MESSAGE_NORMAL = 0;
	public static final int MESSAGE_WARNING = 1;
	public static final int MESSAGE_ERROR = 2;
	
	public GsLabel(String text, int type) {
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
