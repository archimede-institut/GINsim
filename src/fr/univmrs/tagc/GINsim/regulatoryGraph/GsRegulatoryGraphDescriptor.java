package fr.univmrs.tagc.GINsim.regulatoryGraph;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.annotation.BiblioManager;
import fr.univmrs.tagc.GINsim.graph.GsGraphAssociatedObjectManager;
import fr.univmrs.tagc.GINsim.graph.GsGraphDescriptor;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.common.managerresources.Translator;

/**
 * descriptor for regulatoryGraph.
 */
public class GsRegulatoryGraphDescriptor implements GsGraphDescriptor {

    private static List<GsGraphAssociatedObjectManager> v_OManager = null;
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

    public Graph getNew() {
        Graph graph = new GsRegulatoryGraph();
        return graph;
    }

	public Graph open(File file) {
		
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
    public static List<GsGraphAssociatedObjectManager> getObjectManager() {
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
    public Graph open(Map map, File file) {
    	
        return new GsRegulatoryGraph(map, file);
    }
}
