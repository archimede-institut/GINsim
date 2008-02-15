package fr.univmrs.tagc.common.widgets;

import javax.swing.JFrame;

import fr.univmrs.tagc.common.OptionStore;

public abstract class Frame extends JFrame {

	String id;
	public Frame(String id, int w, int h) {
		this.id = id;
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				closeEvent();
			}
		});
		
		this.setSize(((Integer)OptionStore.getOption(id+".width", new Integer(w))).intValue(),
        		((Integer)OptionStore.getOption(id+".height", new Integer(h))).intValue());

	}
	
	public void closeEvent() {
		// TODO: save maximised state
		OptionStore.setOption(id+".width", new Integer(getWidth()));
		OptionStore.setOption(id+".height", new Integer(getHeight()));
		doClose();
	}
	
	abstract public void doClose();
}
