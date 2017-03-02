package org.ginsim.core.graph.objectassociation;

import java.util.*;
import java.util.Map.Entry;

import org.colomoto.biolqm.services.ExtensionLoader;
import org.ginsim.common.application.LogManager;
import org.ginsim.core.service.ServiceClassInfo;
import org.ginsim.core.graph.Graph;
import org.ginsim.common.utils.IntrospectionUtils;

/**
 * The association manager can be used as proxy to retrieve or create associated objects.
 * It loads all known specific managers and maintains the list of objects associated to all graphs.
 *
 * @author Lionel Spinelli
 * @author Aurelien Naldi
 */
public class ObjectAssociationManager {

	private static ObjectAssociationManager instance;
	
	private List<GraphAssociatedObjectManager> objectManagers = null;
	private HashMap<Class,List<GraphAssociatedObjectManager>> specializedObjectManagers = null;
	
    // The map linking objects associated to the Graph with their representative key
    private HashMap<Graph, Map<String, Object>> objectsOfGraph;
	
	private ObjectAssociationManager(){
		
		objectManagers = new ArrayList<GraphAssociatedObjectManager>();
		specializedObjectManagers = new HashMap<Class, List<GraphAssociatedObjectManager>>();
		objectsOfGraph = new HashMap<Graph, Map<String,Object>>();

        Iterator<GraphAssociatedObjectManager> managers = ExtensionLoader.iterator(GraphAssociatedObjectManager.class);
        while (managers.hasNext()) {
            try {
                GraphAssociatedObjectManager mgr = managers.next();
                if (mgr != null) {
                    registerObjectManager(mgr);
                }
            }
            catch (ServiceConfigurationError e){
                LogManager.debug(e);
            }
        }

	}
	
	static public ObjectAssociationManager getInstance(){
		
		if( instance == null){
			instance = new ObjectAssociationManager();
		}
		
		return instance;
	}
	
	/**
	 * Signal that a data user has been updated (removed or renamed)
	 * 
	 * @param graph graph to which the user was associated
	 * @param prefix prefix to add before the IDs, can be null
	 * @param oldKey old ID under which the user was known
	 * @param newKey new ID, or null if it was removed
	 */
	public void fireUserUpdate(Graph<?,?> graph, String prefix, String oldKey, String newKey) {
		if (prefix == null) {
			fireUserUpdate(graph, oldKey, newKey);
		} else if (newKey == null) {
			fireUserUpdate(graph, prefix+"::"+oldKey, null);
		} else {
			fireUserUpdate(graph, prefix+"::"+oldKey, prefix+"::"+newKey);
		}
	}
	
	/**
	 * Signal that a data user has been updated (removed or renamed)
	 * 
	 * @param graph graph to which the user was associated
	 * @param oldKey old ID under which the user was known
	 * @param newKey new ID, or null if it was removed
	 */
	public void fireUserUpdate(Graph<?,?> graph, String oldKey, String newKey) {
		Map<String,Object> m_objects = objectsOfGraph.get(graph);
		if (m_objects == null) {
			return;
		}
		
		for (Object o: m_objects.values()) {
			if (o instanceof UserSupporter) {
				((UserSupporter)o).update(oldKey, newKey);
			}
		}
	}
	
	/**
     * Register an object manager not associated with a graph class
     *
     * @param manager
     */
    private void registerObjectManager( GraphAssociatedObjectManager manager) {

        Class graph_class = manager.getGraphType();
        if (graph_class == null) {
            objectManagers.add( manager);
        } else {

            List<GraphAssociatedObjectManager> specialized_managers =  specializedObjectManagers.get( graph_class);

            if( specialized_managers == null){
                specialized_managers = new Vector<GraphAssociatedObjectManager>();
                specializedObjectManagers.put( graph_class, specialized_managers);
            }

            for (GraphAssociatedObjectManager m: specialized_managers) {
                if (m.getObjectName().equals(manager.getObjectName())) {
                    return;
                }
            }

            specialized_managers.add( manager);
        }
    }
    
    
    /**
     * Give access to the list of registered object managers that are not associated with a graph class
     * 
     * @return the list of registered object managers
     */
    public List<GraphAssociatedObjectManager> getObjectManagerList() {
    	
        return objectManagers;
    }
    
    /**
     * Give access to the list of classes for which object managers are registered
     * 
     * @return the set of class associated to specialised object managers
     */
    public Collection<Entry<Class, List<GraphAssociatedObjectManager>>> getManagedClasses() {
    	return specializedObjectManagers.entrySet();
    }
    
    /**
     * Give access to the list of registered object managers for the given graph class
     * 
     * @return the list of registered object managers
     */
    public List<GraphAssociatedObjectManager> getObjectManagerList( Class graph_class) {
    	
    	Class interface_class = IntrospectionUtils.getChildInterface(graph_class, Graph.class);
    	
        return specializedObjectManagers.get( interface_class);
    }
    

    /**
     * Give access to the Object manager in charge of the given object
     * 
     * @return the Object manager in charge of the given object, null if no Manager is defined for this object
     */
    public GraphAssociatedObjectManager getObjectManager( String key) {
    	
    	if (objectManagers == null) {
    		return null;
    	}
        for (GraphAssociatedObjectManager manager: objectManagers) {
        	if (manager.handles( key)) {
        		return manager;
        	}
        }
        return null;
    }
    
    
    /**
     * Give access to the Object manager in charge of the given object
     * 
     * @return the Object manager in charge of the given object, null if no Manager is defined for this object
     */
    public GraphAssociatedObjectManager getObjectManager( Class graph_class, String key) {
    	
    	Class interface_class = IntrospectionUtils.getChildInterface(graph_class, Graph.class);
    	
    	List<GraphAssociatedObjectManager> specialized_managers =  specializedObjectManagers.get( interface_class);
    	
    	if (specialized_managers == null) {
    		return null;
    	}
    	
        for (GraphAssociatedObjectManager manager: specialized_managers) {
        	if (manager.handles(key)) {
        		return manager;
        	}
        }
        return null;
    }
    
    
    /**
     * Allow to associate objects with a graph to retrieve them later.
     * this (and <code>addObject(key, obj)</code>) makes it easy.
     *
     * @see #addObject(Graph, String, Object)
     * @param key
     * @param create if true, a non-defined object will be created
     * @return the associated object
     */
    public Object getObject( Graph graph, String key, boolean create) {
    	
    	Map<String, Object> m_objects = objectsOfGraph.get( graph);
    	
        if (m_objects == null) {
        	if ( create) {
        		m_objects = new HashMap<String,Object>();
        	} else {
        		return null;
        	}
        }

        GraphAssociatedObjectManager manager = getObjectManager( key);
        if (manager == null) {
            manager = getObjectManager( graph.getClass(), key);
            if (manager == null) {
                return null;
            }
        }
        key = manager.getObjectName();
        Object ret = m_objects.get( key);

        if (create && ret == null) {
            ret = manager.doCreate( graph);
            addObject(graph, key, ret);
        }
        return ret;
    }

    /**
     * Allow to associate objects with a graph to retrieve them later.
     *
     * @see #getObject(Graph, String, boolean)
     * @see #removeObject(Graph, String)
     * @param key
     * @param obj
     */
    public void addObject(Graph graph, String key, Object obj) {
    	
    	Map<String, Object> m_objects = objectsOfGraph.get( graph);
    	
    	if (m_objects == null) {
            m_objects = new HashMap<String, Object>();
            objectsOfGraph.put( graph, m_objects);
        }
        m_objects.put(key, obj);
    }

    /**
     * remove an object previously associated to a graph with <code>addObject(Object, Object)</code>.
     *
     * @see #getObject(Graph, String, boolean)
     * @see #addObject(Graph, String, Object)
     * @param key
     */
    public void removeObject(Graph graph, String key) {
    	
    	Map<String, Object> m_objects = objectsOfGraph.get( graph);
    	
        if (m_objects != null) {
        	m_objects.remove(key);
        }
        
    }
    
    /**
     * Remove all references from associated objects
     * 
     * @param graph
     */
    public void removeAllObjects( Graph graph){
    	
    	Map<String, Object> m_objects = objectsOfGraph.get(graph);
    	
        if (m_objects != null) {
        	// is that REALLY needed? (i.e. do we have copies of this map outside of here?)
            m_objects.clear();
            m_objects = null;
        }
    }

    public ServiceClassInfo[] getDataManagerInfo(Class graphType) {

        List<GraphAssociatedObjectManager> managers = objectManagers;
        if (graphType != null) {
            managers = specializedObjectManagers.get(graphType);
        }

        if (managers == null) {
            return new ServiceClassInfo[0];
        }

        ServiceClassInfo[] ret = new ServiceClassInfo[managers.size()];
        int idx = 0;
        for (GraphAssociatedObjectManager mgr: managers) {
            ret[idx++] = new ServiceClassInfo(mgr.getClass());
        }
        return ret;
    }
}
