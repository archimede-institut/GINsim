package fr.univmrs.ibdm.GINsim.util.widget;

import javax.swing.JFrame;

import fr.univmrs.ibdm.GINsim.global.GsOptions;

public abstract class GsFrame extends JFrame {

	String id;
	public GsFrame(String id, int w, int h) {
		this.id = id;
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				closeEvent();
			}
		});
		
		this.setSize(((Integer)GsOptions.getOption(id+".width", new Integer(w))).intValue(),
        		((Integer)GsOptions.getOption(id+".height", new Integer(h))).intValue());

	}
	
	public void closeEvent() {
		// TODO: save maximised state
		GsOptions.setOption(id+".width", new Integer(getWidth()));
		GsOptions.setOption(id+".height", new Integer(getHeight()));
		doClose();
	}
	
	abstract public void doClose();
}
