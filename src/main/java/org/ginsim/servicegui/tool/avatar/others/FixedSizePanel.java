package org.ginsim.servicegui.tool.avatar.others;

import java.awt.Dimension;

import javax.swing.JPanel;

/**
 * Panel with fixed size
 * @author Rui Henriques
 * @version 1.0
 */
public class FixedSizePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private int height, width;
	
	/**
	 * Creates a panel with the inputted width and height
	 * @param w width of the panel
	 * @param h height of the panel
	 */
	public FixedSizePanel(int w, int h){
		super();
		setLayout(null);
		width=w;
		height=h;
	}
	
	@Override
	public Dimension getPreferredSize(){
	    return new Dimension(width,height);
	}
}
