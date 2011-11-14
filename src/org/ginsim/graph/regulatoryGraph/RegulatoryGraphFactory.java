package org.ginsim.graph.regulatoryGraph;

import java.util.List;
import java.util.Vector;

import javax.swing.filechooser.FileFilter;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.GraphFactory;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.annotation.BiblioManager;
import fr.univmrs.tagc.GINsim.graph.GsGraphAssociatedObjectManager;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryParser;

/**
 * descriptor for regulatoryGraph.
 */
@ProviderFor( GraphFactory.class)
public class RegulatoryGraphFactory implements GraphFactory {

    private static List<GsGraphAssociatedObjectManager> v_OManager = null;
    private GsFileFilter ffilter;
    private static RegulatoryGraphFactory instance = null;

    public RegulatoryGraphFactory() {
    	
    	if (instance == null) {
    		instance = this;
    		registerObjectManager( new BiblioManager());
    	}
    }
    
    /**
     * @return an instance of this graphDescriptor.
     */
    public static GraphFactory getInstance() {
    	
        if (instance == null) {
            instance = new RegulatoryGraphFactory();
        }
        return instance;
    }
    
    /**
     * Return the type of graph this factory is managing
     * 
     * @return the name of the type of graph this factory is managing
     */
    public String getGraphType() {
    	
        return "regulatory";
    }


    /**
     * Create a new graph of the type factory is managing
     * 
     * @return an instance of the graph type the factory is managing
     */
    public Graph create() {
    	
    	GsRegulatoryGraph graph = new GsRegulatoryGraph();
        return graph;
    }
    
    
	/**
	 * Return the class of the parser to use to read from file the type
	 * of graph the factory manager
	 * 
	 * @return the class of the parser to use with this factory
	 */
    public Class getParser() {
    	
    	return GsRegulatoryParser.class;
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

	

    
}
