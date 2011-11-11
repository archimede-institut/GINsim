package org.ginsim.graph.tree;

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
public class GsTreeDescriptor implements GsGraphDescriptor {
    private static GsFileFilter ffilter = null;
    private static GsTreeDescriptor instance = null;

	private static Vector v_OManager = null;
    
	public boolean canCreate() {
		return false;
	}

	public FileFilter getFileFilter() {
	    if (ffilter == null) {
	        ffilter = new GsFileFilter();
	        //ffilter.setExtensionList(new String[] {"ginml", "zginml"}, "(z)ginml files");
	    }
		return ffilter;
	}

	public String getGraphDescription() {
		return Translator.getString("STR_tree");
	}

	public ImageIcon getGraphIcon(int mode) {
		return null;
	}

	public String getGraphName() {
        return "STR_tree";
	}

	public String getGraphType() {
		return "tree";
	}

	/**
     * @return an instance of this graphDescriptor.
     */
    public static GsGraphDescriptor getInstance() {
        if (instance == null) {
            instance = new GsTreeDescriptor();
        }
        return instance;
    }
    
	@Override
	public Graph open(File file) {
		return null;
	}

	@Override
	public Graph open(Map map, File file) {
		return null;
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
