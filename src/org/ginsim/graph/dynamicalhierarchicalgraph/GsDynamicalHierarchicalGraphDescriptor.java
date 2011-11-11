package org.ginsim.graph.dynamicalhierarchicalgraph;

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
 * descriptor for dynamic hierarchical graphs.
 */
public class GsDynamicalHierarchicalGraphDescriptor implements GsGraphDescriptor {
    private static GsFileFilter ffilter = null;
    private static GsDynamicalHierarchicalGraphDescriptor instance = null;
    
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
		return Translator.getString("STR_dynamicalHierarchical graph");
	}

	public ImageIcon getGraphIcon(int mode) {
		return null;
	}

	public String getGraphName() {
        return "STR_dynamicalHierarchical";
	}

	public String getGraphType() {
		return "dynamicalHierarchicalGraph";
	}

	public Graph open(File file) {
		
		return new GsDynamicalHierarchicalGraph(null, file);
	}

	public Graph open(Map map, File file) {
		
		return new GsDynamicalHierarchicalGraph(map, file);

	}
	
	/**
     * @return an instance of this graphDescriptor.
     */
    public static GsGraphDescriptor getInstance() {
        if (instance == null) {
            instance = new GsDynamicalHierarchicalGraphDescriptor();
        }
        return instance;
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
