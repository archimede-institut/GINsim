package fr.univmrs.ibdm.GINsim.util.widget;

import java.awt.Frame;

import javax.swing.JDialog;

import fr.univmrs.ibdm.GINsim.global.GsOptions;

public abstract class GsDialog extends JDialog {

	String id;
	public GsDialog(Frame parent, String id, int w, int h) {
		super(parent);
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
