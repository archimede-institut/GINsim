package fr.univmrs.ibdm.GINsim.global;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphDescriptor;
import fr.univmrs.ibdm.GINsim.graph.GsGraphNotificationMessage;
import fr.univmrs.ibdm.GINsim.gui.GsMainFrame;
import fr.univmrs.ibdm.GINsim.manageressources.ImageLoader;
import fr.univmrs.ibdm.GINsim.plugin.GsClassLoader;
import fr.univmrs.ibdm.GINsim.plugin.GsPlugin;

/**
 * This class offers tons of static methods common to all ginsim's parts.
 * it loads plugins on startup
 * each frame is registred here, so that we can try to close them all when quitting
 */
public class GsEnv {

	protected static GsClassLoader cloader = new GsClassLoader();
	private static String ginsimDir = null;
	private static String dtdDir = null;
	private static String pluginDir = null;
	private static Vector allFrames = new Vector(1);
	
    private static final Map m_graphs = new HashMap(); 
    
	/** for doubleclick sensibility */
	public static final long TIMEOUT = 400;
	
	private static Vector v_graph = new Vector(0);
    
	
	/**
	 * add a frame to the list of avaible frames
	 *   => called by GsMainFrame constructor
	 * 
	 * @param frame the frame to add
	 */
	public static void addFrame(GsMainFrame frame) {
		allFrames.add(frame);
	}
	
	/**
	 * remove a frame from the list of avaible frames
	 * and exit when closing the last frame.
	 *   => called when closing a GsMainFrame
	 * 
	 * @param frame the frame to remove
	 */
	public static void delFrame (GsMainFrame frame) {
		allFrames.remove(frame);
		if (allFrames.size() == 0) {
            GsOptions.saveOptions();
			System.exit(0);
		}
	}

	/**
	 * exit the application: close all windows.
	 * in fact it won't really exit if we refuse to close some windows
	 */
	public static void exit() {
		// try to close all windows
		for ( int i=allFrames.size()-1 ; i>=0 ; i--) {
			((GsMainFrame)allFrames.get(i)).windowClose();
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
	 * @param iconname the name of the icon we want to load
	 * 
	 * @return an ImageIcon corresponding to the given iconname
	 */
	public static ImageIcon getIcon(String iconname) {
		return ImageLoader.getImageIcon(iconname);
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
		
		pluginDir = ginsimDir + "plugins" + File.separator;
		dtdDir= ginsimDir + "data" + File.separator + "ginml" + File.separator;
		
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
	 * @throws GsException if en error occur
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
    public static void newGraph(GsMainFrame m) {
        newGraph(m, 0);
    }
    
    /**
     * create a new graph of type graphType in the frame m.
     * @param m
     * @param graphType
     */
    public static void newGraph(GsMainFrame m, int graphType) {

        if (graphType > v_graph.size()) {
            error(new GsException(GsException.GRAVITY_ERROR, "STR_noSuchGraphType"), m);
            return;
        }
        
        GsGraph myGraph = ((GsGraphDescriptor)v_graph.get(graphType)).getNew(m);
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
    public static void error(GsException e, JFrame main) {
        if (main instanceof GsMainFrame) {
            GsGraph graph = ((GsMainFrame)main).getGraph();
            graph.addNotificationMessage(new GsGraphNotificationMessage(graph, e));
            return;
        }
        int i = -1;
        switch (e.getGravity()) {
            case GsException.GRAVITY_INFO:
            case GsException.GRAVITY_NORMAL:
                i = JOptionPane.INFORMATION_MESSAGE;
                break;
        	default:
                i = JOptionPane.ERROR_MESSAGE;
        }
        JOptionPane.showMessageDialog(main, e.getMessage()+"\n", e.getTitle(),i);
    }

    /**
     * an error occured, give the user some feedback.
     * 
     * @param s
     * @param main
     */
    public static void error(String s, JFrame main) {
        JOptionPane.showMessageDialog(main, s+"\n", "error",JOptionPane.ERROR_MESSAGE);
    }

	/**
	 * @param frame
	 * @param graph
	 */
	public static void whatToDoWithGraph(JFrame frame, GsGraph graph) {
		new GsWhatToDoFrame(frame, graph);
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
     * ask all GINsim frames to update their recent menus
     */
    public static void updateRecentMenu() {
        for (int i=0 ; i<allFrames.size() ; i++) {
            GsMainFrame frame = (GsMainFrame)allFrames.get(i);
            frame.getGsAction().updateRecentMenu();
        }
    }
}