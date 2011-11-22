package org.ginsim.graph.common;

import java.io.File;
import java.util.Collection;

import org.ginsim.exception.GsException;
import org.ginsim.graph.GraphManager;

import fr.univmrs.tagc.GINsim.graph.GraphEventCascade;
import fr.univmrs.tagc.common.managerresources.Translator;


abstract public class AbstractDerivedGraph<V, E extends Edge<V>, AG extends Graph<AV, AE>, AV, AE extends Edge<AV>>
			 extends AbstractGraph<V,E>
			 implements GraphAssociation<AG, AV, AE>, GraphListener<AV,AE> {

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
            associatedGraph.removeGraphListener( this);
            associatedGraph = null;
            return;
        }
        associatedGraph = associated_graph;
        associatedGraph.addGraphListener(this);
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
                throw new GsException(GsException.GRAVITY_INFO, Translator.getString("STR_associate_save"));
            }
        }

        if (associatedID != null) {
            File f = new File(associatedID);
            if (!f.exists() || !f.canRead()) {
            	throw new GsException(GsException.GRAVITY_INFO, Translator.getString("STR_associate_notfound")+associatedID);
            }
        } else {
        	throw new GsException(GsException.GRAVITY_INFO, Translator.getString("STR_associate_manual"));
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

    public GraphEventCascade edgeAdded(AE data) {
        setAssociatedGraph(null);
        return null;
    }
    public GraphEventCascade edgeRemoved(AE data) {
        setAssociatedGraph(null);
        return null;
    }
    public GraphEventCascade edgeUpdated(AE data) {
        return null;
    }


    public GraphEventCascade vertexAdded(AV data) {
        setAssociatedGraph(null);
        return null;
    }

	public GraphEventCascade graphMerged(Collection<AV> data) {
        setAssociatedGraph(null);
		return null;
	}
    public GraphEventCascade vertexUpdated(AV data) {
        return null;
    }

    public GraphEventCascade vertexRemoved(AV data) {
        setAssociatedGraph(null);
        return null;
    }

}
