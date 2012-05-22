package org.ginsim.gui.utils.widgets;

import java.awt.Dimension;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.ginsim.commongui.utils.ImageLoader;



public class StockButton extends JButton {
	private static final long	serialVersionUID	= -9206518187476502060L;

	static int w = 30;
	static int h = 25;

	public StockButton(String text, Icon icon) {
		setAll(text, icon);
	}
	public StockButton(String text) {
		setAll(text, null);
	}
	public StockButton(String text, boolean isStock) {
		if (isStock) {
			setStockIcon(text);
		} else {
			setAll(null, ImageLoader.getImageIcon(text));
		}
	}
	public void setStockIcon(String iconname) {
		URL url = ImageLoader.getImagePath(iconname);
		if (url != null) {
			setAll(null, new ImageIcon(url));
		}
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
