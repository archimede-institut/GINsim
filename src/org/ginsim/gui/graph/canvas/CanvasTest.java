package org.ginsim.gui.graph.canvas;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JFrame;

import org.ginsim.Launcher;
import org.ginsim.common.exception.GsException;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.service.ServiceManager;

public class CanvasTest {

	public static void main(String[] args) throws GsException {
		
		Launcher.init();

		Graph<?, ?> g;
		if (args.length == 1) {
			g = GraphManager.getInstance().open(args[0]);
		} else {
			g = GraphManager.getInstance().getNewGraph();
		}
		
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SimpleCanvas canvas = new SimpleCanvas();
		new GraphCanvasRenderer(g, canvas, null);
		f.setContentPane(canvas);
		
		f.setSize(800, 600);
		f.setVisible(true);
	}
}
