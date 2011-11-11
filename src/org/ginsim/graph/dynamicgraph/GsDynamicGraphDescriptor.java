package org.ginsim.graph.dynamicgraph;

import java.io.File;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.graph.GsGraphAssociatedObjectManager;
import fr.univmrs.tagc.GINsim.graph.GsGraphDescriptor;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
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
