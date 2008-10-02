package fr.univmrs.tagc.GINsim.gui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Map;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.global.GsEventDispatcher;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphDescriptor;
import fr.univmrs.tagc.GINsim.graph.GsGraphOptionPanel;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.OptionStore;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.manageressources.Translator;
import fr.univmrs.tagc.common.widgets.BaseAction;

/**
 * generic action for open methods: uses it's graphDescriptor to open a new graph
 */
public class GsOpenAction extends BaseAction {

	private static final long serialVersionUID = -4552503269216370623L;

    /** open a new graph from file */
    public static final int MODE_OPEN = 0;
	/** create a new graph in the same frame */
	public static final int MODE_NEW = 2;
    /** open a recent file */
    public static final int MODE_RECENT = 1;

	private GsGraphDescriptor gd;
	private GsMainFrame main;
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
	public GsOpenAction(GsGraphDescriptor gd, int mode, GsMainFrame main) {
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
	public GsOpenAction(GsGraphDescriptor gd, int mode, GsMainFrame main, String name, String descr, KeyStroke accel) {
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
    public GsOpenAction(GsGraphDescriptor gd, GsMainFrame main, String path) {
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
            if (main.getGraph().isEmpty()) {
                open(gd, main);
            } else {
                openInNew(gd);
            }
            break;
        case MODE_NEW:
            if (main.getGraph().isEmpty()) {
                //newGraph(gd, main);
            } else {
                newFrame(gd);
            }
            break;
        case MODE_RECENT:
            if (path != null && new File(path).exists()) {
                GsGraph graph = gd.open(new File(path));
                OptionStore.addRecent(path);
                if (main.getGraph().isEmpty()) {
                    GsEventDispatcher.associateGraphWithFrame(graph, main);
                } else {
                    GsEnv.newMainFrame(graph);
                }
            }
		}
	}

    /**
     * open a new graph from a file.
     *
     * @param gd the graphDescriptor for the kind of graph to open
     * @param main the frame in which we want to open it (can be null)
     * @return the opened graph (or null if canceled or an error happened)
     */
    public static GsGraph open(GsGraphDescriptor gd, GsMainFrame main) {
        return open(gd, main, null);
    }
    /**
     * open in a new frame a new graph from a file.
     *
     * @param gd the graphDescriptor for the kind of graph to open
     * @return the opened graph (or null if canceled or an error happened)
     */
    public static GsGraph openInNew(GsGraphDescriptor gd) {
        GsGraph g = open(gd, (GsMainFrame)null, null);
        if (g != null) {
            GsEnv.newMainFrame(g);
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
	public static GsGraph open(GsGraphDescriptor gd, Map filter, GsMainFrame main) {
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
    public static GsGraph open(GsGraphDescriptor gd, GsMainFrame main, Map filter) {
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
    public static GsGraph open(GsGraphDescriptor gd, GsMainFrame main, Map filter, String path) {
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
        				GsGraph graph = gd.open(filter, new File(absoluteNameFile));
        				GsEventDispatcher.associateGraphWithFrame(graph, main);
                    OptionStore.addRecent(absoluteNameFile);
                    OptionStore.setOption("currentDirectory", jfc.getCurrentDirectory().toString());
        				return graph;
        			} else if (!(JFileChooser.CANCEL_OPTION == result)) {
        			    JOptionPane.showMessageDialog(null, Translator.getString("STR_unableToOpen"),
    						Translator.getString("STR_openError"), JOptionPane.ERROR_MESSAGE);
        			}
            } else {
                GsGraph graph = gd.open(filter, new File(path));
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
	public static GsGraph openInNewFrame(GsGraphDescriptor gd) {
	    GsGraph graph = open(gd, null);
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
	public static GsGraph openInNewFrame(GsGraphDescriptor gd, Map filter) {
	    GsGraph graph = open(gd, filter, null);
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
	public static GsGraph newGraph(GsGraphDescriptor gd, GsMainFrame main) {
		if (main != null) {
			if (!main.confirmCloseGraph()) {
				return null;
			}
		}
		GsGraph graph = gd.getNew(main);
		GsEventDispatcher.associateGraphWithFrame(graph, main);

		return graph;
	}

	/**
	 * create a new graph in another frame.
	 *
	 * @param gd the graphDescriptor for the kind of graph to open
	 * @return the opened graph (or null if canceled or an error happened)
	 */
	public static GsGraph newFrame(GsGraphDescriptor gd) {
		GsGraph graph = gd.getNew(null);
		GsEnv.newMainFrame(graph);
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
	public static String selectSaveFile(JFrame frame, FileFilter filter, JComponent accessory, String autoExtension) throws GsException {

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
            	if (extension != null && ! filename.endsWith(extension)) {
            		filename += extension;
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
    public static String selectFile(JFrame frame) {
        getJfc();
        int ret = jfc.showSaveDialog(frame);

        if (null != jfc.getSelectedFile() && ret == JFileChooser.APPROVE_OPTION) {
            String filename = jfc.getSelectedFile().getPath();
            return filename;
        }
        return null;
    }
}
