package fr.univmrs.tagc.GINsim.jython;

import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import fr.univmrs.tagc.GINsim.graph.GsActionProvider;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.plugin.GsPlugin;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraphDescriptor;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.Tools;

/**
 * main method for the reg2dyn plugin
 */
public class JythonPlugin implements GsPlugin, GsActionProvider {

    private GsPluggableActionDescriptor[] t_action = null;

    public void registerPlugin() {
        GsRegulatoryGraphDescriptor.registerActionProvider(this);
    }

    public GsPluggableActionDescriptor[] getT_action(int actionType,
            GsGraph graph) {
        if (actionType != ACTION_ACTION) {
            return null;
        }
        if (t_action == null) {
            t_action = new GsPluggableActionDescriptor[1];
            t_action[0] = new GsPluggableActionDescriptor("STR_jython",
                    "STR_jython_descr", null, this, ACTION_ACTION, 0);
        }
        return t_action;
    }

    public void runAction(int actionType, int ref, GsGraph graph, JFrame frame) throws GsException {
        if (actionType != ACTION_ACTION) {
            return;
        }
        new JythonConsole(graph);
	}
}

class JythonConsole extends Thread {
	static {
        PySystemState.initialize();
	}
	
	GsGraph graph;
	JythonFrame frame;
	
    public JythonConsole(GsGraph graph) {
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