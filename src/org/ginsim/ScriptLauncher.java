package org.ginsim;

import java.awt.Color;
import java.io.File;
import java.util.List;
import java.util.Map.Entry;

import org.ginsim.common.OptionStore;
import org.ginsim.common.exception.GsException;
import org.ginsim.common.utils.log.LogManager;
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
 * A helper when running GINsim without a GUI.
 * It provides a convenient API for common actions:
 * - open and save graphs
 * - run services
 * - write reports
 * 
 * @author Aurelien Naldi
 */
public class ScriptLauncher {

	private static boolean isInit = false;

	/**
	 * Main entry point for the script mode.y
	 * @param filename
	 * @param args
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

	private boolean running = false;
	
	/**
	 * Run the script
	 * 
	 * @param filename
	 * @param args
	 */
	public synchronized void run(String filename, String[] args) {
		if (running) {
			throw new RuntimeException("Script should run only once");
		}
		running = true;
		this.args = args;

        File f = new File(filename);
        if (!f.exists()) {
            LogManager.error( "No such script: "+filename);
        	return;
        }
		
        initOptionStore();
        // actually run the script
        PythonInterpreter pi = new PythonInterpreter();
		pi.set("gs", this);
        pi.execfile(filename);
        
        // reset the running status: should not be needed but may be convenient later
        running = false;
	}
	
	private final ServiceManager services = ServiceManager.getManager();
	private final ObjectAssociationManager associated = ObjectAssociationManager.getInstance();
	

	/**
	 * Arguments passed to the script
	 */
	public String[] args;
	
	
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
	 * 
	 * @param state
	 * @param graph
	 * @return the name or null
	 */
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
	public Object associated(Graph g, String key, boolean create) {
		return associated.getObject(g, key, create);
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
	
}
