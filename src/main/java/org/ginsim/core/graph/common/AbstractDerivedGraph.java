package org.ginsim.core.graph.common;

import java.io.File;

import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.GraphManager;



abstract public class AbstractDerivedGraph<V, E extends Edge<V>, AG extends Graph<AV, AE>, AV, AE extends Edge<AV>>
			 extends AbstractGraph<V,E>
			 implements GraphAssociation<AG, AV, AE>, GraphListener<AG> {

    protected AG associatedGraph = null;
    protected String associatedID = null;

    
    public AbstractDerivedGraph( boolean parsing) {
    	
    	super( parsing);
    }
    
    
    //----------------------   ASSOCIATED GRAPH METHODS --------------------------------------------
    //   TODO: should the associated graph move to specialised graph types?
	//        with an intermediate class providing the common code it would make sense
    //----------------------------------------------------------------------------------------------
	
    
    /**
     * Associate the given graph to the current one
     * 
     * @param associated_graph
     */
	@Override
	public void setAssociatedGraph( AG associated_graph) {

        if (associated_graph == null || !isAssociationValid( associated_graph)) {
            return;
        }

        if (associatedGraph != null) {
            GraphManager.getInstance().removeGraphListener( associatedGraph, this);
            associatedGraph = null;
            return;
        }
        associatedGraph = associated_graph;
        GraphManager.getInstance().addGraphListener( associatedGraph, this);
    }
	
    /**
     * @return the graph associated with this one.
     */
	@Override
    public AG getAssociatedGraph() throws GsException{

        if ( associatedGraph == null && getAssociatedGraphID() != null) {
        	
            AG ag = (AG) GraphManager.getInstance().getGraphFromPath( associatedID);
            if (ag != null) {
                setAssociatedGraph( ag);
            } else {
                File f = new File( associatedID);
                if (f.exists()) {
                    ag = (AG) GraphManager.getInstance().open(f);
                    setAssociatedGraph(ag);
                } else {
                	throw new GsException(GsException.GRAVITY_INFO, "STR_openAssociatedGraphFailed"+"\n"+associatedID);
                }
            }
        }

        // check association
        if (associatedGraph != null && !isAssociationValid(associatedGraph)) {
            associatedGraph = null;
            associatedID = null;
        }

        return associatedGraph;
    }
    
    /**
     * @return the ID (path) of the associated graph.
     */
	@Override
    public String getAssociatedGraphID() throws GsException{
		
        if (associatedGraph != null) {
            associatedID = GraphManager.getInstance().getGraphPath( associatedGraph);
            if (associatedID == null) {
                throw new GsException(GsException.GRAVITY_INFO, "STR_associate_save");
            }
        }

        if (associatedID != null) {
            File f = new File(associatedID);
            if (!f.exists() || !f.canRead()) {
            	throw new GsException(GsException.GRAVITY_INFO, new String[]{"STR_associate_notfound",associatedID});
            }
        } else {
        	throw new GsException(GsException.GRAVITY_INFO, "STR_associate_manual");
        }

        return associatedID;
    }
	
    /**
     * test if a graph can be associated with this one.
     * this is a default implementation and will always return false, override to do something useful.
     *
     * @param graph
     * @return true if this is a valid associated graph.
     */
	protected abstract boolean isAssociationValid( Graph<?,?> graph);
    
    /**
     * set the path to the associated graph.
     * @param value
     */
	@Override	
    public void setAssociatedGraphID(String value) {
        associatedID = value;
    }

	@Override
	public GraphEventCascade graphChanged(Graph g, GraphChangeType type, Object data) {
		if (g != this) {
			return null;
		}
		switch (type) {
		case EDGEADDED:
		case EDGEREMOVED:
		case NODEADDED:
		case NODEREMOVED:
		case GRAPHMERGED:
			setAssociatedGraph(null);
			break;
		}
        return null;
    }

}
