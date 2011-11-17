package fr.univmrs.tagc.GINsim.gui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.common.BaseAction;
import org.ginsim.gui.shell.MainFrame;

import fr.univmrs.tagc.GINsim.graph.GsGraphDescriptor;
import fr.univmrs.tagc.GINsim.graph.GsGraphOptionPanel;
import fr.univmrs.tagc.common.OptionStore;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.managerresources.Translator;

/**
 * generic action for open methods: uses it's graphDescriptor to open a new graph
 */
public class GsOpenAction extends BaseAction {

	private static final long serialVersionUID = -4552503269216370623L;

    /** open a new graph from file */
    public static final int MODE_OPEN = 0;
    /** open a new graph from file and pass it directly to whattodo */
    public static final int MODE_OPEN_AND_DO = 1;
	/** create a new graph in the same frame */
	public static final int MODE_NEW = 2;
    /** open a recent file */
    public static final int MODE_RECENT = 3;

	private GsGraphDescriptor gd;
	private MainFrame main;
    private String path = null;
	private int mode;

	private static JFileChooser jfc;

	/**
	 * create a new action to open a graph from a file or create a new one.
	 *
	 * @param gd the graphDescriptor for the kind of graph to open
	 * @param mode open or create ? (one of MODE_OPEN, MODE_NEW, MODE_NEWFRAME)
	 * @param main the frame in which we want to open it (can be null)
	 */
	public GsOpenAction(GsGraphDescriptor gd, int mode, MainFrame main) {
		super(gd.getGraphName(), gd.getGraphIcon(mode), gd.getGraphDescription(), null);
		this.gd = gd;
		this.main = main;
		this.mode = mode;
	}

	/**
	 * create a new action to open a graph from a file or create a new one.
	 *
	 * @param gd the graphDescriptor for the kind of graph to open
	 * @param mode open or create ? (one of MODE_OPEN, MODE_NEW, MODE_NEWFRAME)
	 * @param main the frame in which we want to open it (can be null)
	 * @param name
	 * @param descr
     * @param accel
	 */
	public GsOpenAction(GsGraphDescriptor gd, int mode, MainFrame main, String name, String descr, KeyStroke accel) {
		super(name, gd.getGraphIcon(mode), descr, accel);
		this.gd = gd;
		this.main = main;
		this.mode = mode;
	}

    /**
     * action for recent files
     *
     * @param gd
     * @param main
     * @param path path to the recent file
     */
    public GsOpenAction(GsGraphDescriptor gd, MainFrame main, String path) {
        super(path.substring(path.lastIndexOf(File.separator)+1, path.length()), null, path, null);
        this.gd = gd;
        this.main = main;
        this.mode = MODE_RECENT;
        this.path = path;
    }
	/**
	 * the menuitem has been activated, perform the action.
	 * depending on the mode it will open a graph from a file or create a new one.
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
		switch (mode) {
        case MODE_OPEN:
            if ( main.getGraph().getVertexCount() <= 0) {
                open(gd, main);
            } else {
                openInNew(gd);
            }
            break;
        case MODE_NEW:
            if (main.getGraph().getVertexCount() <= 0) {
                //newGraph(gd, main);
            } else {
                newFrame(gd);
            }
            break;
        case MODE_RECENT:
            if (path != null && new File(path).exists()) {
                Graph graph = gd.open(new File(path));
                OptionStore.addRecent(path);
                if (main.getGraph().getVertexCount() <= 0) {
                    GsEventDispatcher.associateGraphWithFrame(graph, main);
                } else {
                	GUIManager.getInstance().newFrame( graph);
                }
            }
            break;
       case MODE_OPEN_AND_DO:
           Graph g = open(gd, (MainFrame)null, null);
    	   new GsWhatToDoFrame( main, g, false);
		}
	}

    /**
     * open a new graph from a file.
     *
     * @param gd the graphDescriptor for the kind of graph to open
     * @param main the frame in which we want to open it (can be null)
     * @return the opened graph (or null if canceled or an error happened)
     */
    public static Graph open(GsGraphDescriptor gd, GsMainFrame main) {
        return open(gd, main, null);
    }
    /**
     * open in a new frame a new graph from a file.
     *
     * @param gd the graphDescriptor for the kind of graph to open
     * @return the opened graph (or null if canceled or an error happened)
     */
    public static Graph openInNew(GsGraphDescriptor gd) {
    	
        Graph g = open(gd, (MainFrame) null, null);
        if (g != null) {
            GUIManager.getInstance().newFrame( g);
        }
        return g;
    }
	/**
	 * open a new graph from a file.
	 *
	 * @param gd the graphDescriptor for the kind of graph to open
	 * @param filter
	 * @param main the frame in which we want to open it (can be null)
	 * @return the opened graph (or null if canceled or an error happened)
	 */
	public static Graph open(GsGraphDescriptor gd, Map filter, MainFrame main) {
	    return open(gd, main, filter);
	}
	/**
	 * open a new graph from a file.
	 *
	 * @param gd the graphDescriptor for the kind of graph to open
	 * @param main the frame in which we want to open it (can be null)
	 * @param filter if not null, only nodes listed in this Map will be created
	 * @return the opened graph (or null if canceled or an error happened)
	 */
    public static Graph open(GsGraphDescriptor gd, MainFrame main, Map filter) {
    	
        return open(gd, main, filter, null);
    }
    /**
     *
     * open a new graph from a file.
     *
     * @param gd the graphDescriptor for the kind of graph to open
     * @param main the frame in which we want to open it (can be null)
     * @param filter if not null, only nodes listed in this Map will be created
     * @param path path to the file to open (will be used only if not null and file exists)
     * @return the opened graph (or null if canceled or an error happened)
     */
    public static Graph open(GsGraphDescriptor gd, MainFrame main, Map filter, String path) {
    	
	    if (gd == null) {
	        return null;
	    }

	    if (main != null) {
			if (!main.confirmCloseGraph()) {
				return null;
			}
		}
		int result = 0;
        String absoluteNameFile;
        if (path != null) {
            File f = new File(path);
            if (f.exists() && f.canRead()) {
                result = 1;
            }
            f = null;
        }
		try {
            if (result == 0) {
        		    getJfc();
        			jfc.setFileFilter(gd.getFileFilter());
        			result = jfc.showOpenDialog(main);
        			if (null != jfc.getSelectedFile()
        					&& JFileChooser.APPROVE_OPTION == result && jfc.getSelectedFile().exists()) {
        				absoluteNameFile = jfc.getSelectedFile().getPath();
        				Graph graph = gd.open(filter, new File(absoluteNameFile));
        				GsEventDispatcher.associateGraphWithFrame(graph, main);
                    OptionStore.addRecent(absoluteNameFile);
                    OptionStore.setOption("currentDirectory", jfc.getCurrentDirectory().toString());
        				return graph;
        			} else if (!(JFileChooser.CANCEL_OPTION == result)) {
        			    JOptionPane.showMessageDialog(null, Translator.getString("STR_unableToOpen"),
    						Translator.getString("STR_openError"), JOptionPane.ERROR_MESSAGE);
        			}
            } else {
                Graph graph = gd.open(filter, new File(path));
                GsEventDispatcher.associateGraphWithFrame(graph, main);
                OptionStore.addRecent(path);
                return graph;
            }
		} catch (Exception e) {
            GsEnv.error(new GsException(GsException.GRAVITY_ERROR, e), main);
		}
		return null;
	}

	/**
     *
     */
    private static void getJfc() {
        File curDir = null;
        if (jfc != null) {
            curDir = jfc.getCurrentDirectory();
        } else {
            String path = (String)OptionStore.getOption("currentDirectory");
            if (path != null) {
                curDir = new File(path);
            }
        }
        if (curDir != null && !curDir.exists()) {
            curDir = null;
        }
        jfc = new JFileChooser(curDir);
    }

    /**
	 * open a new graph from a file.
	 *
	 * @param gd the graphDescriptor for the kind of graph to open
	 * @return the opened graph (or null if canceled or an error happened)
	 */
	public static Graph openInNewFrame(GsGraphDescriptor gd) {
		
	    Graph graph = open(gd, null);
	    if (graph != null) {
	        GsEnv.newMainFrame(graph);
	    }
	    return graph;
	}
	/**
	 * open a new graph from a file.
	 *
	 * @param gd the graphDescriptor for the kind of graph to open
	 * @param filter
	 * @return the opened graph (or null if canceled or an error happened)
	 */
	public static Graph openInNewFrame(GsGraphDescriptor gd, Map filter) {
		
	    Graph graph = open(gd, filter, null);
	    if (graph != null) {
	        GsEnv.newMainFrame(graph);
	    }
	    return graph;
	}

	/**
	 * create a new graph.
	 *
	 * @param gd the graphDescriptor for the kind of graph to open
	 * @param main the frame in which we want to open it (can be null)
	 * @return the opened graph (or null if canceled or an error happened)
	 */
	public static Graph newGraph(GsGraphDescriptor gd, GsMainFrame main) {
		if (main != null) {
			if (!main.confirmCloseGraph()) {
				return null;
			}
		}
		Graph graph = gd.getNew(main);
		GsEventDispatcher.associateGraphWithFrame( graph, main);

		return graph;
	}

	/**
	 * create a new graph in another frame.
	 *
	 * @param gd the graphDescriptor for the kind of graph to open
	 * @return the opened graph (or null if canceled or an error happened)
	 */
	public static Graph newFrame(GsGraphDescriptor gd) {
		
		Graph graph = gd.getNew(null);
		GUIManager.getInstance().newFrame( graph);
		return graph;
	}

	/**
	 * run a fileChooser in save mode, with filter and accessory... :)
	 *
	 * @param frame
	 * @param filter
	 * @param accessory
	 * @param autoExtension
	 * @return the path to which we want to save (null if cancelled/protected)
	 * @throws GsException if an error occured
	 */
	public static String selectSaveFile(Frame frame, FileFilter filter, JComponent accessory, String autoExtension) throws GsException {

	    getJfc();
		jfc.setFileFilter(filter);
		jfc.setAccessory(accessory);
		int ret = jfc.showSaveDialog(frame);

        if (null != jfc.getSelectedFile() && ret == JFileChooser.APPROVE_OPTION) {
            OptionStore.setOption("currentDirectory", jfc.getCurrentDirectory());
            String filename = jfc.getSelectedFile().getPath();
    		String extension = autoExtension;
            if (accessory instanceof GsGraphOptionPanel) {
                extension = ((GsGraphOptionPanel)accessory).getExtension();
            }
        	if (extension != null) {
   	            if (!extension.startsWith(".")) { 
        	    	extension = "." + autoExtension;
        	    }
        	    if (!filename.endsWith(extension)) {
            		filename += extension;
            	}
        	}
            if (Tools.isFileWritable(filename, frame)) {
                return filename;
            }
        }
        return null;
	}

    /**
     * just select a file somewhere.
     *
     * @param frame
     * @return the path to the selected file
     */
    public static String selectFileWithSaveDialog(JFrame frame) {
        getJfc();
        //int ret = jfc.showSaveDialog(frame);
        int ret = jfc.showOpenDialog(frame);

        if (null != jfc.getSelectedFile() && ret == JFileChooser.APPROVE_OPTION) {
            String filename = jfc.getSelectedFile().getPath();
            return filename;
        }
        return null;
    }
    
    /**
     * just select a file somewhere.
     *
     * @param frame
     * @return the path to the selected file
     */
    public static String selectFileWithOpenDialog(Frame frame) {
        getJfc();
        int ret = jfc.showOpenDialog(frame);

        if (null != jfc.getSelectedFile() && ret == JFileChooser.APPROVE_OPTION) {
            String filename = jfc.getSelectedFile().getPath();
            return filename;
        }
        return null;
    }
}