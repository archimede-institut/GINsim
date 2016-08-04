package org.ginsim.servicegui.tool.avatar.others;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * Dedicated panel where tool tips are allowed to appear on the left superior area
 * @author Rui Henriques
 * @version 1.0
 */
public class TitleToolTipPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	@Override
    public String getToolTipText(MouseEvent e){
        Border border = getBorder();
        if (border instanceof TitledBorder){
            Rectangle bounds = new Rectangle(0, 0, 25, 20);
            return bounds.contains(e.getPoint()) ? super.getToolTipText() : null;
        }
        return super.getToolTipText(e);
    }
}