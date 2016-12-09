package org.ginsim.servicegui.tool.avatar.others;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * Dedicated titled border where the help icon is also printed before the title
 * @author Rui Henriques
 * @version 1.0
 */
public class MyTitledBorder extends TitledBorder {

	private static final long serialVersionUID = 1L;
	private Icon icon; 
	
	/**
	 * Titled border with the help icon to be printed before text
	 * @param border the border
	 * @param label a component containing the title and a pointer to the help icon
	 * @param titleJustification title text justification
	 * @param titlePosition title text position
	 * @param titleFont title text font
	 * @param titleColor title text color
	 */
	public MyTitledBorder(Border border, JLabel label, int titleJustification, int titlePosition, Font titleFont, Color titleColor){
		super(border,label.getText(),titleJustification,titlePosition,titleFont,titleColor);
		icon = label.getIcon();
	}
	
	 @Override
	 public void paintBorder(Component c, Graphics g, int x, int y, int width, int height){
	     super.paintBorder(c, g, x, y, width, height);
	     icon.paintIcon(c, g, 10, 2);
	 }
}
