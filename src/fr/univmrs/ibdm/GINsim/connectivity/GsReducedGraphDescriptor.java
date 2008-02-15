package fr.univmrs.ibdm.GINsim.connectivity;

import java.io.File;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import fr.univmrs.ibdm.GINsim.graph.GsActionProvider;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphAssociatedObjectManager;
import fr.univmrs.ibdm.GINsim.graph.GsGraphDescriptor;
import fr.univmrs.ibdm.GINsim.gui.GsMainFrame;
import fr.univmrs.tagc.common.manageressources.Translator;

/**
 * descriptor for regulatoryGraph.
 */
public class GsReducedGraphDescriptor implements GsGraphDescriptor {

    private static Vector v_layout = null;
    private static Vector v_export = null;
    private static Vector v_action = null;
    private static Vector v_OManager = null;

    private static GsGraphDescriptor instance = null;
    
    public String getGraphName() {
        return "STR_reduced";
    }

    public String getGraphDescription() {
        return Translator.getString("STR_reducedGraph");
    }

    public boolean canCreate() {
        return false;
    }

    /**
     * @see fr.univmrs.ibdm.GINsim.graph.GsGraphDescriptor#getNew(fr.univmrs.ibdm.GINsim.gui.GsMainFrame)
     * 
     * can't interactivly create a dynamic graph => disabled
     */
    public GsGraph getNew(GsMainFrame m) {
    		return null;
    }

	public GsGraph open(File file) {
	    return open(null, file);
	}

	public FileFilter getFileFilter() {
		return null;
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
	 * @return a list of avaible layouts.
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
	 * @return a list of avaible export filters.
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
	 * @return a list of avaible actions.
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

	public ImageIcon getGraphIcon(int mode) {
		return null;
	}

    /**
     * @return an instance of this graphDescriptor.
     */
    public static GsGraphDescriptor getInstance() {
        if (instance == null) {
            instance = new GsReducedGraphDescriptor();
        }
        return instance;
    }

    public GsGraph open(Map map, File file) {
		return new GsReducedGraph(map, file);
    }

    public String getGraphType() {
        return "reduced";
    }
}
