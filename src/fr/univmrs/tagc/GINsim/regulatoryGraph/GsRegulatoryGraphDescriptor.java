package fr.univmrs.tagc.GINsim.regulatoryGraph;

import java.io.File;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import fr.univmrs.tagc.GINsim.annotation.BiblioManager;
import fr.univmrs.tagc.GINsim.graph.GsActionProvider;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphAssociatedObjectManager;
import fr.univmrs.tagc.GINsim.graph.GsGraphDescriptor;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.gui.GsMainFrame;
import fr.univmrs.tagc.common.manageressources.Translator;

/**
 * descriptor for regulatoryGraph.
 */
public class GsRegulatoryGraphDescriptor implements GsGraphDescriptor {

    private static Vector v_layout = null;
    private static Vector v_export = null;
    private static Vector v_action = null;
    private static Vector v_OManager = null;
    private GsFileFilter ffilter;
    private static GsRegulatoryGraphDescriptor instance = null;

    public GsRegulatoryGraphDescriptor() {
    	if (GsRegulatoryGraphDescriptor.instance == null) {
    		GsRegulatoryGraphDescriptor.instance = this;
            registerObjectManager(new BiblioManager());
    	} else {
    		System.out.println("trying to create a new graphdescriptor!");
    	}
    }
    
    public String getGraphType() {
        return "regulatory";
    }

    public String getGraphName() {
        return "STR_regulatory";
    }

    public String getGraphDescription() {
        return Translator.getString("STR_regulatoryGraph");
    }

    public boolean canCreate() {
        return true;
    }

    public GsGraph getNew(GsMainFrame m) {
        GsGraph graph = new GsRegulatoryGraph();
        return graph;
    }

	public GsGraph open(File file) {
	    return open(null, file);
	}

	public FileFilter getFileFilter() {
		if (ffilter == null) {
			ffilter = new GsFileFilter();
			ffilter.setExtensionList(new String[] {"ginml", "zginml"}, "(z)ginml files");
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
        } else {
            for (int i=0 ; i<v_OManager.size(); i++) {
                if (((GsGraphAssociatedObjectManager)v_OManager.get(i)).getObjectName().equals(manager.getObjectName())) {
                    return;
                }            }
        }
        v_OManager.add(manager);
    }

    /**
     * 
     * @param key
     * @return true if a manager with this name already exists
     */
    public static boolean isObjectManagerRegistred(String key) {
        if (v_OManager == null) {
            return false;
        }
        for (int i=0 ; i<v_OManager.size() ; i++) {
            if (((GsGraphAssociatedObjectManager)v_OManager.get(i)).getObjectName().equals(key)) {
                return true;
            }
        }
        return false;
    }
    /**
     * @return associated specific objects manager
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
            instance = new GsRegulatoryGraphDescriptor();
        }
        return instance;
    }
    public GsGraph open(Map map, File file) {
        return new GsRegulatoryGraph(map, file);
    }
}
