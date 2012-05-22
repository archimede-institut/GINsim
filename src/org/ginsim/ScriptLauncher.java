package org.ginsim;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.ginsim.common.application.GsException;
import org.ginsim.common.application.LogManager;
import org.ginsim.common.application.OptionStore;
import org.ginsim.common.document.DocumentWriter;
import org.ginsim.common.document.LaTeXDocumentWriter;
import org.ginsim.common.document.OOoDocumentWriter;
import org.ginsim.common.document.XHTMLDocumentWriter;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.EdgeAttributeReaderImpl;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.NodeAttributeReaderImpl;
import org.ginsim.core.graph.objectassociation.GraphAssociatedObjectManager;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.initialstate.GsInitialStateList;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialStateList;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialStateManager;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceManager;
import org.python.util.PythonInterpreter;

/**
 * A helper when running GINsim in script mode.
 * It provides a convenient API for common actions:
 * - open and save graphs
 * - run services
 * - create reports
 * 
 * @author Aurelien Naldi
 */
public class ScriptLauncher {

	private static boolean isInit = false;
	private boolean running = false;
	
	private final ServiceManager services = ServiceManager.getManager();
	private final ObjectAssociationManager associated = ObjectAssociationManager.getInstance();
	

	/**
	 * Arguments passed to the script
	 */
	public String[] args;
	

	
	/**
	 * Initialisation method for the script helper to ensure a working OptionStore.
	 */
	private static void initOptionStore() {
		if (isInit) {
			return;
		}
		isInit = true;

        // init the option store
		try {
			OptionStore.init( ScriptLauncher.class.getPackage().getName());
	    	OptionStore.getOption( EdgeAttributeReaderImpl.EDGE_COLOR, new Integer(-13395457));
	    	OptionStore.getOption( NodeAttributeReaderImpl.VERTEX_BG, new Integer(-26368));
	    	OptionStore.getOption( NodeAttributeReaderImpl.VERTEX_FG, new Integer(Color.WHITE.getRGB()));
	    	OptionStore.getOption( NodeAttributeReaderImpl.VERTEX_HEIGHT, new Integer(30));
	    	OptionStore.getOption( NodeAttributeReaderImpl.VERTEX_WIDTH, new Integer(55));
	    	OptionStore.getOption( NodeAttributeReaderImpl.VERTEX_SHAPE, NodeShape.RECTANGLE.name());
	    	OptionStore.getOption( NodeAttributeReaderImpl.VERTEX_BORDER, NodeBorder.SIMPLE.name());
		} catch (Exception e) {
			LogManager.error("Could not init the Option Store");
		}
	}

	/**
	 * Run a script: the method is called by the main launcher to launch a GINsim script
	 * 
	 * @param filename script filename
	 * @param args additional arguments passed on the command line
	 */
	public synchronized void run(String filename, String[] args) {
		if (running) {
			throw new RuntimeException("Script should run only once");
		}
		running = true;
		this.args = args;

        File f = new File(filename).getAbsoluteFile();
        if (!f.exists()) {
            LogManager.error( "No such script: "+filename);
        	return;
        }
		
        initOptionStore();

        // select a base directory for script files:
        // the first parent of the selected script without a __init__.py file
        File dir = f.getParentFile();
        while (true) {
        	File finit = new File(dir, "__init__.py");
        	if (!finit.exists()) {
        		break;
        	}
        	dir = dir.getParentFile();
        }
        
        // Create and set up python interpreter:
        //  * add the selected folder to the classpath
        //  * add a "gs" object pointing to this script launcher
        PythonInterpreter pi = new PythonInterpreter();
        pi.getSystemState().path.add(0, dir.getAbsolutePath());
		pi.set("gs", this);
		
        // actually run the script
        pi.execfile(filename);
        
        // reset the running status: should not be needed but may be convenient later
        running = false;
	}
	
	/**
	 * Open a graph from a file.
	 * 
	 * @param filename
	 * @return
	 * @throws GsException
	 */
	public Graph<?, ?> open(String filename) throws GsException {
		File file = new File(filename);
		return GraphManager.getInstance().open( file);
	}
	
	/**
	 * Lookup a named state matching the given state.
	 * Marked as deprecated as it should be part of the initial state service.
	 * 
	 * @param state
	 * @param graph
	 * @return the name or null
	 */
	@Deprecated
	public String nameState(byte[] state, RegulatoryGraph graph) {
        // FIXME: adapt it to deal with input configs !!
        InitialStateList init = ((GsInitialStateList)associated.getObject(graph, InitialStateManager.KEY, false)).getInitialStates();
        if (init != null) {
            List<RegulatoryNode> no = graph.getNodeOrder();
        	return init.nameState(state, no);
        }
        return null;
	}

	/**
	 * Get a service instance
	 * 
	 * @param cl the service class
	 * @return
	 */
	public Service service(Class<Service> cl) {
		return services.get(cl);
	}

	/**
	 * Get a service instance by name
	 * 
	 * @param name the service name (class name or alias)
	 * @return
	 */
	public Service service(String name) {
		return services.get(name);
	}

	/**
	 * Get (existing) associated data
	 * 
	 * @param g
	 * @param key
	 * @return
	 */
	public Object associated(Graph g, String key) {
		return associated(g, key, false);
	}

	/**
	 * Get or create associated data.
	 * 
	 * @param g
	 * @param key
	 * @param create
	 * @return
	 */
	public Object associated(Graph<?,?> g, String key, boolean create) {
		return associated.getObject(g, key, create);
	}

	/**
	 * Create a report file, this creates the file and DocumentWriter, don't forget to close it.
	 * 
	 * @param path
	 * @param properties
	 * @return
	 */
	public DocumentWriter createReport(String path, Map<String, String> properties) {
		DocumentWriter dw;
		if (path.endsWith("html")) {
			dw = XHTMLDocumentWriter.FACTORY.getDocumentWriter();
		} else if (path.endsWith(".odt")) {
			dw = OOoDocumentWriter.FACTORY.getDocumentWriter();
		} else if (path.endsWith("tex")) {
			dw = LaTeXDocumentWriter.FACTORY.getDocumentWriter();
		} else {
			return null;
		}
		
		try {
			dw.setOutput(new File(path));
			if (properties != null) {
				dw.setDocumentProperties(properties);
			}
			dw.startDocument();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dw = null;
		}
		return dw;
	}
	
	/**
	 * Show some help about services and data available for scripts
	 */
	public void help() {
		System.out.println("Available services:");
		for (Class<Service> srv: ServiceManager.getManager().getAvailableServices()) {
			System.out.print("   * ");
			Alias alias = srv.getAnnotation(Alias.class);
			if (alias != null) {
				System.out.print(alias.value() + " -- ");
			}
			System.out.println(srv.getName());
		}
		System.out.println();

		List<GraphAssociatedObjectManager> managers = associated.getObjectManagerList();
		if (managers != null && managers.size() > 0) {
			System.out.println("Available data managers:");
			for (GraphAssociatedObjectManager amanager: managers) {
				System.out.print("   * ");
				Alias alias = amanager.getClass().getAnnotation(Alias.class);
				if (alias != null) {
					System.out.print(alias.value() + " -- ");
				}
				System.out.println(amanager.getObjectName());
			}
		}
		System.out.println("Available specialised data managers:");
		for (Entry<Class, List<GraphAssociatedObjectManager>> entry: associated.getManagedClasses()) {
			System.out.println("   * " + entry.getKey());
			for (GraphAssociatedObjectManager amanager: entry.getValue()) {
				System.out.print("     - ");
				System.out.println(amanager.getObjectName());
			}
		}
		System.out.println();
	}

	/**
	 * Write some ad-hoc documentation.
	 * It creates a simple HTML version of the help, with links to the javadoc.
	 * 
	 * @param filename output filename
	 * @param javadocbase path to the javadoc
	 */
	public void doc(String filename, String javadocbase) {
		DocumentWriter doc = createReport(filename, null);
		try {
			writeDoc(doc, javadocbase, "Services", ServiceManager.getManager().getAvailableServices());
			// TODO: names for associated data managers
			writeDoc(doc, javadocbase, "Associated data", getDataManagers());
			doc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<GraphAssociatedObjectManager> getDataManagers() {
		List datamanagers = new ArrayList();
		List<GraphAssociatedObjectManager> managers = associated.getObjectManagerList();
		if (managers != null) {
			datamanagers.addAll(managers);
		}
		
		Collection<Entry<Class, List<GraphAssociatedObjectManager>>> managed = associated.getManagedClasses();
		if (managed != null) {
			for (Entry<Class, List<GraphAssociatedObjectManager>> e: managed) {
				datamanagers.addAll(e.getValue());
			}
		}
		return datamanagers;
	}
	
	private void writeDoc(DocumentWriter doc, String javadocbase, String title, Collection<?> objects) throws IOException {
		
		if (objects == null || objects.size() == 0) {
			return;
		}
		
		doc.openHeader(1, title, null);
		doc.openList(null);
		for (Object o: objects) {
			Class<?> cl;
			if (o instanceof Class) {
				cl = (Class<?>)o;
			} else {
				cl = o.getClass();
			}
			Alias alias = cl.getAnnotation(Alias.class);
			String name = cl.getSimpleName();
			if (alias != null) {
				name = alias.value();
			}
			
			// TODO: javadoc link
			String scl = cl.getCanonicalName().replace('.', '/');
			doc.openListItem("");
			doc.addLink(javadocbase+"/"+scl+".html", name);
		}
		doc.closeList();
	}
}
