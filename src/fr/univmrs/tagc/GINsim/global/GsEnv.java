package fr.univmrs.tagc.GINsim.global;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.swing.JFrame;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphDescriptor;
import fr.univmrs.tagc.GINsim.graph.GsGraphNotificationMessage;
import fr.univmrs.tagc.GINsim.gui.BaseMainFrame;
import fr.univmrs.tagc.GINsim.gui.GsMainFrame;
import fr.univmrs.tagc.GINsim.plugin.GsClassLoader;
import fr.univmrs.tagc.GINsim.plugin.GsPlugin;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.OptionStore;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.widgets.Frame;

/**
 * This class offers tons of static methods common to all ginsim's parts.
 * it loads plugins on startup
 * each frame is registred here, so that we can try to close them all when quitting
 */
public class GsEnv {

	private static String ginsimDir = null;
	private static String dtdDir = null;
	private static String pluginDir = null;
	private static List<BaseMainFrame> allFrames = new ArrayList<BaseMainFrame>(1);

    private static final Map m_graphs = new HashMap();

    public static final Integer[] t_integers = new Integer[] {
    	new Integer(0),
    	new Integer(1),
    	new Integer(2),
    	new Integer(3),
    	new Integer(4),
    	new Integer(5),
    	new Integer(6),
    	new Integer(7),
    	new Integer(8),
    	new Integer(9)
    };

	/** for doubleclick sensibility */
	public static final long TIMEOUT = 400;

	private static Vector v_graph = new Vector(0);


	/**
	 * add a frame to the list of available frames
	 *   => called by GsMainFrame constructor
	 *
	 * @param frame the frame to add
	 */
	public static void addFrame(BaseMainFrame frame) {
		allFrames.add(frame);
	}

	/**
	 * remove a frame from the list of available frames
	 * and exit when closing the last frame.
	 *   => called when closing a GsMainFrame
	 *
	 * @param frame the frame to remove
	 */
	public static void delFrame (BaseMainFrame frame) {
		allFrames.remove(frame);

		if (allFrames.size() == 0) {
            OptionStore.saveOptions();
			System.exit(0);
		}
	}

	/**
	 * exit the application: close all windows.
	 * in fact it won't really exit if we refuse to close some windows
	 */
	public static void exit() {
		// try to close all windows
		for ( BaseMainFrame frame: allFrames) {
			frame.closeEvent();
		}
	}

	/**
	 * @return a string containing the path to ginsim install dir
	 */
	public static String getGinsimDir() {
		return ginsimDir;
	}

	/**
	 * @return a string containing the path to ginsim dtd dir
	 */
	public static String getGinsimDTDdir() {
		return dtdDir;
	}

	/**
	 * set the ginsim install directory
	 * it HAS to be called once before running anything else
	 *
	 * @param dir the path to ginsim install dir
	 */
	public static void setGinsimDir(String dir) {

		if (dir.equals(".")) {
			ginsimDir =  System.getProperty("user.dir") + File.separator;
		} else if (dir.startsWith("."+File.separator)) {
			ginsimDir =  System.getProperty("user.dir") + File.separator + dir + File.separator;
		} else {
			ginsimDir = dir + File.separator;
		}

		dtdDir= ginsimDir + "data" + File.separator + "ginml" + File.separator;

		pluginDir = ginsimDir + "plugins" + File.separator;
		GsClassLoader cloader = new GsClassLoader();
		// look for other plugins
		String[] t_files = new File(pluginDir).list();
		if (t_files != null) {
			for ( int i=0 ; i<t_files.length ; i++ ) {
				if (t_files[i].endsWith(".jar")) {
					try {
						loadPlugin(cloader, cloader.getJarFile(new File(pluginDir+t_files[i])));
					} catch (GsException e) {
						System.err.println("[plugin loader] ("+t_files[i]+") "+e.getMessage());
					}
				}
			}
		}
	}

	/**
	 *
	 * @param cloader
	 * @param jf the jarfile to open
	 * @throws GsException if an error occurs
	 */
	private static void loadPlugin(GsClassLoader cloader, JarFile jf) throws GsException {
		if (jf == null) {
			throw new GsException (GsException.GRAVITY_NORMAL, "no such jarfile" );
		}
		Manifest manifest = cloader.getManifest(jf);
		if (manifest == null) {
			throw new GsException (GsException.GRAVITY_NORMAL, "no manifest in jar file" );
		}
		java.util.jar.Attributes attr = manifest.getMainAttributes();
		String className = attr.getValue("GsPluginClass");
		if (className == null) {
			throw new GsException (GsException.GRAVITY_NORMAL, "no main class defined" );
		}
        Class testclasse;
        try {
            testclasse = cloader.loadClass(jf, className, true);
            Object plugin = testclasse.newInstance();

            if ( plugin instanceof GsPlugin) {
                    ((GsPlugin) plugin).registerPlugin();
            } else {
    			throw new GsException (GsException.GRAVITY_NORMAL, "the given class is _NOT_ a GsPlugin !!!" );
            }
        } catch (Exception e) {
			throw new GsException (GsException.GRAVITY_NORMAL, "error loading main class" );
        }
	}

	/**
	 * @return the number of opened frames.
	 */
	public static int getNbFrame() {
	    return allFrames.size();
	}

    /**
     * create a new frame.
     * @return the new mainFrame
     */
    public static GsMainFrame newMainFrame() {
		GsMainFrame m = new GsMainFrame();
		newGraph(m);
		addFrame(m);
		return m;
    }

    /**
     * open an existing graph in a new frame
     *
     * @param graph
     * @return the new frame
     */
    public static GsMainFrame newMainFrame(GsGraph graph) {
		GsMainFrame m = new GsMainFrame();
		GsEventDispatcher.associateGraphWithFrame(graph, m);
		addFrame(m);
		return m;
    }

    /**
     * create a new default graph in frame m.
     * @param m
     */
    public static void newGraph(BaseMainFrame m) {
        GsGraph myGraph = ((GsGraphDescriptor)v_graph.get(0)).getNew((GsMainFrame)m);
        myGraph.getGraphManager().ready();
		m.getEventDispatcher().fireGraphChange(null, null, myGraph, false);
    }

    /**
     * add a new type of graph.
     * @param gd
     */
    public static void addGraphType(GsGraphDescriptor gd ) {
        v_graph.add(gd);
    }

    /**
     * @param type
     * @return the graphDescriptor for this kind of graph (or null if not found)
     */
    public static GsGraphDescriptor getGraphType(String type) {
        for (int i=0 ; i<v_graph.size() ; i++) {
            if (type.equals(((GsGraphDescriptor)v_graph.get(i)).getGraphType())) {
                return (GsGraphDescriptor)v_graph.get(i);
            }
        }
    	return null;
    }

    /**
     * @return the list of registred graph types (as a Vector of GsGraphDescriptor)
     */
    public static Vector getGraphType() {
    	return v_graph;
    }

    /**
     * an error occured, give the user some feedback.
     *
     * @param e
     * @param main
     */
    public static void error(GsException e, Frame main) {
        if (main instanceof GsMainFrame) {
            GsGraph graph = ((GsMainFrame)main).getGraph();
            graph.addNotificationMessage(new GsGraphNotificationMessage(graph, e));
            return;
        }
        Tools.error(e, main);
    }

	/**
	 * @param frame
	 * @param graph
	 */
	public static void whatToDoWithGraph(JFrame frame, GsGraph graph) {
		whatToDoWithGraph(frame, graph, true);
	}

	/**
	 * @param frame
	 * @param graph
	 * @param needLayout TODO
	 */
	public static void whatToDoWithGraph(JFrame frame, GsGraph graph, boolean needLayout) {
		new GsWhatToDoFrame(frame, graph, needLayout);
	}

    /**
     * register a graph.
     *
     * @param graph
     * @param id
     * @return ths id under which the graph is registred
     */
    public static String registerGraph(GsGraph graph, String id) {
        if (graph == null) {
            return null;
        }
        if (id == null) {
            return null;
        }
        m_graphs.put(id, graph);
        return id;
    }
    /**
     * remove a graph's registration
     * @param id
     */
    public static void unregisterGraph(String id) {
        m_graphs.remove(id);
    }

    /**
     * change a graph's registration.
     *
     * @param oldId
     * @param newId
     */
    public static void renameGraph(String oldId, String newId) {
        GsGraph graph = (GsGraph)m_graphs.get(oldId);
        if (graph != null) {
            m_graphs.remove(oldId);
            m_graphs.put(newId, graph);
        }
    }

    /**
     * get a previously registred graph.
     *
     * @param id
     * @return the corresponding graph (null if no such id)
     */
    public static GsGraph getRegistredGraph(String id) {
        return (GsGraph)m_graphs.get(id);
    }
    
    /**
     * get the HashMap of containing all the registered graphs
     * @return 
     * 
     */
    public static Map getAllGraphs() {
		return m_graphs;
	}

    /**
     * ask all GINsim frames to update their recent menus
     */
    public static void updateRecentMenu() {
        for (BaseMainFrame frame: allFrames) {
            frame.updateRecentMenu();
        }
    }

	public static void readConfig(String path) throws IOException, FileNotFoundException {
		InputStream stream = Tools.getStreamForPath(path);
    	new ReadConfig().startParsing(stream, false);
	}
}
