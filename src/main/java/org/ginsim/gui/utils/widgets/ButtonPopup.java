package org.ginsim.gui.utils.widgets;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.ginsim.gui.utils.data.MultiActionListener;


public class ButtonPopup extends JPanel implements ActionListener {
	private static final long serialVersionUID = -6885596437610975591L;
	
	List listeners;
	List options;
	StockButton button;
	StockButton dropdown;
	JPopupMenu menu;

	public ButtonPopup(String iconName, boolean isStock, List options) {
		super();
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		button = new StockButton(iconName, isStock);
		add(button, c);
		button.addActionListener(this);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		dropdown = new StockButton("drop-down.png", true);
		dropdown.setMinimumSize(new Dimension(11, 25));
		add(dropdown, c);
		dropdown.addActionListener(this);
		refresh();
	}
	
	public void refresh() {
		if (menu == null) {
			menu = new JPopupMenu();
		} else {
			menu.removeAll();
		}
		if (options == null) {
			dropdown.setVisible(false);
			button.setMinimumSize(new Dimension(30, 25));
		} else {
			button.setMinimumSize(new Dimension(18, 25));
			dropdown.setVisible(true);
			if (options.size() > 1) {
				dropdown.setEnabled(true);
				int i=0;
				for (Iterator it=options.iterator() ; it.hasNext() ; ) {
					Object o = it.next();
					menu.add(new JMenuItem(new SelectAddModeAction(this, -1, i,""+o)));
					i++;
				}
			} else {
				dropdown.setEnabled(false);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		// just in case:
		menu.setVisible(false);
		Object src = e.getSource();
		if (src == dropdown) {
			menu.show(dropdown, 0, dropdown.getHeight());
			return;
		}
		int ref = -1;
		if (src == button) {
			ref = 0;
		} else {
			if (src instanceof JMenuItem) {
				ref = ((SelectAddModeAction)((JMenuItem)src).getAction()).mode;
			}
		}
		if (ref != -1 && listeners != null) {
			for (Iterator it = listeners.iterator() ; it.hasNext() ;) {
				MultiActionListener listener = (MultiActionListener)it.next();
				listener.actionPerformed(e, ref);
			}
		}
	}

	public void setOptions(List options) {
		this.options = options;
		refresh();
	}
	public void addActionListener(MultiActionListener listener) {
		if (listeners == null) {
			listeners = new ArrayList();
		}
		listeners.add(listener);
	}
}

class SelectAddModeAction extends AbstractAction {
	private static final long serialVersionUID = -7038482131591956858L;
	int position;
	int mode;
	ButtonPopup bp;
	
	SelectAddModeAction(ButtonPopup bp, int position, int mode, String s) {
		super(s);
		this.mode = mode;
		this.position = position;
		this.bp = bp;
		this.putValue(Action.NAME, s);
	}
	public void actionPerformed(ActionEvent e) {
		bp.actionPerformed(e);
	}
}
