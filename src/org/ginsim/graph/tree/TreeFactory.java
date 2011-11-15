package org.ginsim.graph.tree;

import java.io.File;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.GraphFactory;
import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;
import org.ginsim.graph.regulatorygraph.GsRegulatoryParser;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.graph.GsGraphAssociatedObjectManager;
import fr.univmrs.tagc.GINsim.graph.GsGraphDescriptor;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.common.managerresources.Translator;

/**
 * descriptor for dynamic hierarchical graphs.
 */
@ProviderFor( GraphFactory.class)
public class TreeFactory implements GraphFactory {
	
    private static GraphFactory instance = null;
	
	private static Vector v_OManager = null;
    
	/**
     * @return an instance of this graphDescriptor.
     */
    public static GraphFactory getInstance() {
    	
        if (instance == null) {
            instance = new TreeFactory();
        }
        return instance;
    }


    /**
     * Return the type of graph this factory is managing
     * 
     * @return the name of the type of graph this factory is managing
     */
    public String getGraphType() {
    	
        return "tree";
    }
    
    /**
     * Return the class of graph this factory is managing
     * 
     * @return the name of the class of graph this factory is managing
     */
	public Class getGraphClass(){
		
		return GsTree.class;
	}
	


    /**
     * Create a new graph of the type factory is managing
     * 
     * @return an instance of the graph type the factory is managing
     */
    public Graph create() {
    	
        return null;
    }
    
    
	/**
	 * Return the class of the parser to use to read from file the type
	 * of graph the factory manager
	 * 
	 * @return the class of the parser to use with this factory
	 */
    public Class getParser() {
    	
    	return GsTreeParser.class;
    }
    

}
