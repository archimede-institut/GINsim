package fr.univmrs.tagc.widgets;

import java.awt.Dimension;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import fr.univmrs.ibdm.GINsim.manageressources.ImageLoader;


public class StockButton extends JButton {
	private static final long	serialVersionUID	= -9206518187476502060L;

	static int w = 30;
	static int h = 25;

	private static String s_base;
	
	static {
		File f = new File("/usr/share/icons/Tango/22x22/actions/");
		if (f.exists()) {
			s_base = "/usr/share/icons/Tango/22x22/actions/";
		} else {
			s_base = "/fr/univmrs/tagc/ressources/icons/actions/";
		}
	}
	
	public StockButton(String text, Icon icon) {
		setAll(text, icon);
	}
	public StockButton(String text) {
		setAll(text, null);
	}
	public StockButton(String text, boolean isStock) {
		if (isStock) {
			URL url = getURL(text);
			if (url != null) {
				setAll(null, new ImageIcon(url));
			}
		} else {
			setAll(null, ImageLoader.getImageIcon(text));
		}
	}
	static public URL getURL(String name) {
		String s_url = s_base+name;
        URL url = ImageLoader.class.getResource(s_url) ;
        if (url == null) {
        	File f = new File(s_url);
        	if (f.exists()) {
        		try {
					url = new URL("file", "", f.getAbsolutePath());
				} catch (MalformedURLException e) {}
        	}
        }
        return url;
	}
	private void setAll(String text, Icon icon) {
		if (text != null) {
			setText(text);
		}
		if (icon != null) {
			setIcon(icon);
		}
		setBorder(BorderFactory.createEtchedBorder());
		setMinimumSize(new Dimension(w, h));
	}
}
