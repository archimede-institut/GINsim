package org.ginsim.gui.service.tools.jython;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.ginsim.graph.Graph;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GsToolsAction;
import org.ginsim.gui.service.common.StandaloneGUI;
import org.mangosdk.spi.ProviderFor;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import fr.univmrs.tagc.common.Tools;

/**
 * main method for the reg2dyn plugin
 */
@ProviderFor(GsServiceGUI.class)
@StandaloneGUI
public class JythonServiceGUI implements GsServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new JythonAction(graph));
		return actions;
	}

}

class JythonAction extends GsToolsAction {

	private final Graph<?, ?> graph;

	public JythonAction(Graph<?, ?> graph) {
		super("STR_jython", "STR_jython_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
        new JythonConsole( graph);
	}
	
}

class JythonConsole extends Thread {
	static {
        PySystemState.initialize();
	}
	
	Graph graph;
	JythonFrame frame;
	
    public JythonConsole( Graph graph) {
    	this.graph = graph;
    	this.frame = new JythonFrame();
    	this.start();
    }
    public void run() {
        PythonInterpreter pyi = new PythonInterpreter();   
        pyi.exec("import sys");    
        // you can pass the python.path to java to avoid hardcoding this
        // java -Dpython.path=/path/to/jythonconsole-0.0.6 EmbedExample
        
        String consolebase = "/fr/univmrs/tagc/GINsim/jython/console";
        URL url = Tools.class.getResource(consolebase);
    	String path = url.getPath();
    	// if running from a jar, this looks like "file:path/to/file.jar!/fr/..../console"
    	if (path.startsWith("file:") && path.endsWith(".jar!"+consolebase)) {
    		path = path.substring(5, path.length() - consolebase.length()-1) + consolebase;
    	}
    	pyi.exec("sys.path.append(r'"+path + "')");
        pyi.exec("from console import Console");
        // stuff some objects into the namespace
        pyi.set("current_graph", graph);
        pyi.set("namespace", pyi.getLocals());
        pyi.set("frame", frame);
        pyi.set("sp", frame.consoleScrollPanel);

        pyi.exec("console = Console(namespace, frame)");
        pyi.exec("sp.setViewportView(console.text_pane)");
        
        // namespace cleanups
        pyi.exec("del sp, frame, namespace, Console, sys, console, __doc__, __name__");
        frame.setVisible(true);
    }
}

class JythonFrame extends JFrame {
	private static final long serialVersionUID = -3652232265843777029L;
	JScrollPane consoleScrollPanel = new JScrollPane();
	
	public JythonFrame() {
		setTitle("GINsim: jython console");
		setSize(300, 400);
		getContentPane().add(consoleScrollPanel);
	}
	public void close() {
		setVisible(false);
		dispose();
	}
}