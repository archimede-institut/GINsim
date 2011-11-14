package fr.univmrs.tagc.GINsim.hierachicalTransitionGraph;

import java.io.File;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.ginsim.graph.common.Graph;

import fr.univmrs.tagc.GINsim.graph.GsGraphAssociatedObjectManager;
import fr.univmrs.tagc.GINsim.graph.GsGraphDescriptor;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.common.managerresources.Translator;

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

	public Graph open(File file) {
		
		return new GsHierarchicalTransitionGraph(null, file);
	}

	public Graph open(Map map, File file) {
		
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
