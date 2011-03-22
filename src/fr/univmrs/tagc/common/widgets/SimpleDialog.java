package fr.univmrs.tagc.common.widgets;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.*;

import fr.univmrs.tagc.common.OptionStore;

public abstract class SimpleDialog extends JDialog {
	private static final long	serialVersionUID	= -460464845250055098L;

	protected String id;
	
	Action actionListener = new AbstractAction() {
		private static final long serialVersionUID = 448859746054492959L;
		public void actionPerformed(ActionEvent actionEvent) {
			escape();
		}
	};
      
    /**
     * 
     * @param parent
     * @param id an id to store the windows size (will be "id.width" and "id.height")
     * @param w
     * @param h
     */
	public SimpleDialog(Frame parent, String id, int w, int h) {
		super(parent);
		this.id = id;
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				closeEvent();
			}
		});
		
		this.setSize(((Integer)OptionStore.getOption(id+".width", new Integer(w))).intValue(),
        		((Integer)OptionStore.getOption(id+".height", new Integer(h))).intValue());
		
	    JPanel content = (JPanel) getContentPane();
	    KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");

	    content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "ESCAPE");
	    content.getActionMap().put("ESCAPE", actionListener);
	}
	
	public void closeEvent() {
		OptionStore.setOption(id+".width", new Integer(getWidth()));
		OptionStore.setOption(id+".height", new Integer(getHeight()));
		doClose();
	}
	
	protected void escape() {
		closeEvent();
	}
	
	abstract public void doClose();
}
