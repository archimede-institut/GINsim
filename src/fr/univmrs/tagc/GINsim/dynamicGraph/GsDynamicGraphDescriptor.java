package fr.univmrs.tagc.GINsim.dynamicGraph;

import java.io.File;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.graph.GsActionProvider;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphAssociatedObjectManager;
import fr.univmrs.tagc.GINsim.graph.GsGraphDescriptor;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.gui.GsMainFrame;
import fr.univmrs.tagc.common.managerresources.Translator;

/**
 * descriptor for dynamic (state transition) graphs.
 */
public class GsDynamicGraphDescriptor implements GsGraphDescriptor {

    private static Vector v_layout = null;
    private static Vector v_export = null;
    private static Vector v_action = null;
    private static GsFileFilter ffilter = null;

    private static GsDynamicGraphDescriptor instance = null;
    private static Vector v_OManager;
    
    public String getGraphType() {
        return "dynamic";
    }

    public String getGraphName() {
        return "STR_dynamic";
    }

    public String getGraphDescription() {
        return Translator.getString("STR_dynamic graph");
    }

    public boolean canCreate() {
        return false;
    }

    /**
     * @see fr.univmrs.tagc.GINsim.graph.GsGraphDescriptor#getNew(fr.univmrs.tagc.GINsim.gui.GsMainFrame)
     * 
     * can't interactively create a dynamic graph => disabled
     */
    public Graph getNew(GsMainFrame m) {
    		return null;
    }

	public Graph open(File file) {
		
		return new GsDynamicGraph(null, file);
	}

	public FileFilter getFileFilter() {
	    if (ffilter == null) {
	        ffilter = new GsFileFilter();
	        ffilter.setExtensionList(new String[] {"ginml"}, "ginml files");
	    }
		return ffilter;
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
     * @return associated object manager
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
            instance = new GsDynamicGraphDescriptor();
        }
        return instance;
    }

    public Graph open(Map map, File file) {
    	
		return new GsDynamicGraph(map, file);
    }
}
