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

	private static String[] t_base = new String[2];
	
	static {
		t_base[0] = "/fr/univmrs/tagc/ressources/icons/actions/";
		File f = new File("/usr/share/icons/Human/22x22/actions/");
		if (f.exists()) {
			t_base[1] = f.getAbsolutePath();
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
		String s_url = t_base[0]+name;
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
