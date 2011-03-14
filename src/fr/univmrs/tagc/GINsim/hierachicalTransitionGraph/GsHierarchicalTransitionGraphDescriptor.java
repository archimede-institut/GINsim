package fr.univmrs.tagc.GINsim.hierachicalTransitionGraph;

import java.io.File;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;
import fr.univmrs.tagc.GINsim.graph.GsActionProvider;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphAssociatedObjectManager;
import fr.univmrs.tagc.GINsim.graph.GsGraphDescriptor;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.gui.GsMainFrame;
import fr.univmrs.tagc.common.manageressources.Translator;

/**
 * descriptor for hierarchical transition graphs.
 */
public class GsHierarchicalTransitionGraphDescriptor implements GsGraphDescriptor {
    private static GsFileFilter ffilter = null;
    private static GsHierarchicalTransitionGraphDescriptor instance = null;
    
	private static Vector v_layout = null;
	private static Vector v_export = null;
	private static Vector v_action = null;
	private static Vector v_OManager = null;

	public boolean canCreate() {
		return false;
	}

	public FileFilter getFileFilter() {
	    if (ffilter == null) {
	        ffilter = new GsFileFilter();
	        ffilter.setExtensionList(new String[] {"ginml", "zginml"}, "(z)ginml files");
	    }
		return ffilter;
	}

	public String getGraphDescription() {
		return Translator.getString("STR_HierarchicalTransitionGraph");
	}

	public ImageIcon getGraphIcon(int mode) {
		return null;
	}

	public String getGraphName() {
        return "STR_HierarchicalTransitionGraph";
	}

	public String getGraphType() {
		return "hierarchicalTransitionGraph";
	}

    /**
     * @see fr.univmrs.tagc.GINsim.graph.GsGraphDescriptor#getNew(fr.univmrs.tagc.GINsim.gui.GsMainFrame)
     * 
     * can't interactively create a dynamic hierarchical graph => disabled
     */
	public GsGraph getNew(GsMainFrame m) {
		return null;
	}

	public GsGraph open(File file) {
		return new GsHierarchicalTransitionGraph(null, file);
	}

	public GsGraph open(Map map, File file) {
		return new GsHierarchicalTransitionGraph(map, file);

	}
	
	/**
     * @return an instance of this graphDescriptor.
     */
    public static GsGraphDescriptor getInstance() {
        if (instance == null) {
            instance = new GsHierarchicalTransitionGraphDescriptor();
        }
        return instance;
    }
    
	/**
	 * @param layout
	 */
	public static void registerLayoutProvider(GsActionProvider layout) {
		if (v_layout == null) {
			v_layout = new Vector();
		}
		v_layout.add(layout);
	}
	/**
	 * @return a list of available layouts.
	 */
	public static Vector getLayout() {
		return v_layout;
	}

	/**
	 * @param export
	 */
	public static void registerExportProvider(GsActionProvider export) {
		if (v_export == null) {
			v_export = new Vector();
		}
		v_export.add(export);
	}
	/**
	 * @return a list of available export filters.
	 */
	public static Vector getExport() {
		return v_export;
	}

	/**
	 * 
	 * @param action
	 */
	public static void registerActionProvider(GsActionProvider action) {
		if (v_action == null) {
			v_action = new Vector();
		}
		v_action.add(action);
	}
	/**
	 * @return a list of available actions.
	 */
	public static Vector getAction() {
		return v_action;
	}
    /**
     * @param manager
     */
    public static void registerObjectManager(GsGraphAssociatedObjectManager manager) {
        if (v_OManager == null) {
            v_OManager = new Vector();
        }
        v_OManager.add(manager);
    }
    /**
     * @return associates object managers
     */
    public static Vector getObjectManager() {
        return v_OManager;
    }


}
