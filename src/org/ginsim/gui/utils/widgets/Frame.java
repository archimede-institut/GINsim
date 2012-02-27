package org.ginsim.gui.utils.widgets;

import java.awt.event.WindowAdapter;

import javax.swing.JFrame;

import org.ginsim.common.OptionStore;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;


public abstract class Frame extends JFrame {
	private static final long	serialVersionUID	= -9024470351150546630L;

	String id;
	public Frame(String id, int w, int h) {
		this.id = id;
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				close();
			}
		});

		int width = OptionStore.getOption(id+".width", w);
		int height = OptionStore.getOption(id+".height", h);
		setLocationByPlatform(true);
		this.setSize( width, height);

	}

	public void dispose() {
		OptionStore.setOption(id+".width", getWidth());
		OptionStore.setOption(id+".height", getHeight());
		super.dispose();
	}
	
	/**
	 * Set the title of the frame
	 * 
	 * @param graph the graph the frame is currently opening
	 */
	public void setFrameTitle( Graph graph, boolean saved){
		
		String title = "GINsim - " + graph.getGraphName();
		
		String savePath = GraphManager.getInstance().getGraphPath( graph);
		if (savePath != null) {
			title += " ["+savePath+"]";
		}
		
		if( !saved){
			title = "* " + title;
		}
		
		setTitle( title);
	}

	abstract public void close();
}
