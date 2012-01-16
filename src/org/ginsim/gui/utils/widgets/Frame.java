package org.ginsim.gui.utils.widgets;

import javax.swing.JFrame;

import org.ginsim.common.OptionStore;
import org.ginsim.core.graph.common.Graph;


public abstract class Frame extends JFrame {
	private static final long	serialVersionUID	= -9024470351150546630L;

	String id;
	public Frame(String id, int w, int h) {
		this.id = id;
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				close();
			}
		});

		this.setSize(((Integer)OptionStore.getOption(id+".width", new Integer(w))).intValue(),
        		((Integer)OptionStore.getOption(id+".height", new Integer(h))).intValue());

	}

	public void dispose() {
		OptionStore.setOption(id+".width", new Integer(getWidth()));
		OptionStore.setOption(id+".height", new Integer(getHeight()));
		super.dispose();
	}
	
	/**
	 * Set the title of the frame
	 * 
	 * @param graph the graph the frame is currently opening
	 */
	public void setFrameTitle( Graph graph, boolean saved){
		
		String title = "GINsim - " + graph.getGraphName();
		
		if( !saved){
			title += " *";
		}
		
		setTitle( title);
	}

	abstract public void close();
}
