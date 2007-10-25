package fr.univmrs.tagc.widgets;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.*;

import fr.univmrs.ibdm.GINsim.global.GsOptions;

public abstract class SimpleDialog extends JDialog {

	String id;
	
	Action actionListener = new AbstractAction() {
		private static final long serialVersionUID = 448859746054492959L;
		public void actionPerformed(ActionEvent actionEvent) {
			escape();
		}
	};
      
	public SimpleDialog(Frame parent, String id, int w, int h) {
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
		
	    JPanel content = (JPanel) getContentPane();
	    KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");

	    content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "ESCAPE");
	    content.getActionMap().put("ESCAPE", actionListener);
	}
	
	public void closeEvent() {
		GsOptions.setOption(id+".width", new Integer(getWidth()));
		GsOptions.setOption(id+".height", new Integer(getHeight()));
		doClose();
	}
	
	protected void escape() {
		closeEvent();
	}
	
	abstract public void doClose();
}
