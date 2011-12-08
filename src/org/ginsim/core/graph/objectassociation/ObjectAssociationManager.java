package org.ginsim.core.graph.objectassociation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.utils.IntrospectionUtils;


public class ObjectAssociationManager {

	private static ObjectAssociationManager instance;
	
	private List<GraphAssociatedObjectManager> objectManagers = null;
	private HashMap<Class,List<GraphAssociatedObjectManager>> specializedObjectManagers = null;
	
    // The map linking objects associated to the Graph with their representative key
    private HashMap<Graph,Map<Object,Object>> objectsOfGraph;
	
	private ObjectAssociationManager(){
		
		objectManagers = new Vector<GraphAssociatedObjectManager>();
		specializedObjectManagers = new HashMap<Class, List<GraphAssociatedObjectManager>>();
		objectsOfGraph = new HashMap<Graph, Map<Object,Object>>();
		
	}
	
	static public ObjectAssociationManager getInstance(){
		
		if( instance == null){
			instance = new ObjectAssociationManager();
		}
		
		return instance;
	}
	
	
	/**
     * Register an object manager not associated with a graph class
     *
     * @param manager
     */
    public void registerObjectManager( GraphAssociatedObjectManager manager) {
    	
    	objectManagers.add( manager);
    }
    
    /**
     * Register an object manager associated to a graph class.
     * 
     * @param manager
     */
    public void registerObjectManager( Class graph_class, GraphAssociatedObjectManager manager) {
    	
    	if( graph_class == null) {
    		return;
    	}
    	
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
    
    
    /**
     * Give access to the list of registered object managers that are not associated with a graph class
     * 
     * @return the list of registered object managers
     */
    public List<GraphAssociatedObjectManager> getObjectManagerList() {
    	
        return objectManagers;
    }
    
    /**
     * Give access to the list of registered object managers for the given graph class
     * 
     * @return the list of registered object managers
     */
    public List<GraphAssociatedObjectManager> getObjectManagerList( Class graph_class) {
    	
    	Class interface_class = IntrospectionUtils.getGraphInterface( graph_class);
    	
        return specializedObjectManagers.get( interface_class);
    }
    

    /**
     * Give access to the Object manager in charge of the given object
     * 
     * @return the Object manager in charge of the given object, null if no Manager is defined for this object
     */
    public GraphAssociatedObjectManager getObjectManager( Object key) {
    	
    	if (objectManagers == null) {
    		return null;
    	}
        for (int i=0 ; i < objectManagers.size() ; i++) {
        	GraphAssociatedObjectManager manager = (GraphAssociatedObjectManager) objectManagers.get(i);
        	if (manager.getObjectName().equals( key)) {
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
    public GraphAssociatedObjectManager getObjectManager( Class graph_class, Object key) {
    	
    	Class interface_class = IntrospectionUtils.getGraphInterface( graph_class);
    	
    	List<GraphAssociatedObjectManager> specialized_managers =  specializedObjectManagers.get( interface_class);
    	
    	if (specialized_managers == null) {
    		return null;
    	}
    	
        for (int i=0 ; i < specialized_managers.size() ; i++) {
        	GraphAssociatedObjectManager manager = (GraphAssociatedObjectManager) specialized_managers.get(i);
        	if (manager.getObjectName().equals( key)) {
        		return manager;
        	}
        }
        return null;
    }
    
    
    /**
     * Allow to associate objects with a graph to retrieve them later.
     * this (and <code>addObject(key, obj)</code>) makes it easy.
     *
     * @see #addObject(Object, Object)
     * @param key
     * @param create if true, a non-defined object will be created
     * @return the associated object
     */
    public Object getObject( Graph graph, Object key, boolean create) {
    	
    	Map<Object, Object> m_objects = objectsOfGraph.get( graph);
    	
        if (m_objects == null) {
        	if ( create) {
        		m_objects = new HashMap<Object,Object>();
        	} else {
        		return null;
        	}
        }
        Object ret = m_objects.get( key);
        if (create && ret == null) {
        	GraphAssociatedObjectManager manager = getObjectManager( key);
        	if (manager == null) {
        		manager = getObjectManager( graph.getClass(), key);
        	}
        	if (manager != null) {
        		ret = manager.doCreate( graph);
        		addObject(graph, key, ret);
        	}
        }
        return ret;
    }

    /**
     * Allow to associate objects with a graph to retrieve them later.
     *
     * @see #getObject(Object)
     * @see #removeObject(Object)
     * @param key
     * @param obj
     */
    public void addObject(Graph graph, Object key, Object obj) {
    	
    	Map<Object, Object> m_objects = objectsOfGraph.get( graph);
    	
    	if (m_objects == null) {
            m_objects = new HashMap<Object,Object>();
            objectsOfGraph.put( graph, m_objects);
        }
        m_objects.put(key, obj);
    }

    /**
     * remove an object previously associated to a graph with <code>addObject(Object, Object)</code>.
     *
     * @see #getObject(Object)
     * @see #addObject(Object, Object)
     * @param key
     */
    public void removeObject(Graph graph, Object key) {
    	
    	Map<Object, Object> m_objects = objectsOfGraph.get( graph);
    	
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
    	
    	Map<Object, Object> m_objects = objectsOfGraph.get( graph);
    	
        if (m_objects != null) {
            for( Object key : m_objects.keySet()){
            	Object obj = m_objects.get( key);
            	obj = null;
            }
            m_objects.clear();
            m_objects = null;
        }
    }
    
    /**
     * 
     * @param key
     * @return true if a manager with this key already exists
     */
    public boolean isObjectManagerRegistred( Class graph_class, String key) {
    	
    	Class interface_class = IntrospectionUtils.getGraphInterface( graph_class);
    	
    	List<GraphAssociatedObjectManager> specialized_managers =  specializedObjectManagers.get( interface_class);
    	
        if (specialized_managers == null) {
            return false;
        }
        for (int i=0 ; i<specialized_managers.size() ; i++) {
            if (((GraphAssociatedObjectManager)specialized_managers.get(i)).getObjectName().equals(key)) {
                return true;
            }
        }
        return false;
    }
   	

}
