package org.ginsim.graph;

import java.io.File;
import java.util.Collection;
import java.util.List;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.GraphChangeListener;
import fr.univmrs.tagc.GINsim.graph.GsGinsimGraphDescriptor;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphEventCascade;
import fr.univmrs.tagc.GINsim.graph.GsGraphListener;
import fr.univmrs.tagc.GINsim.graph.GsGraphSelectionChangeEvent;
import fr.univmrs.tagc.GINsim.graph.GsNewGraphEvent;
import fr.univmrs.tagc.GINsim.gui.GsOpenAction;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.managerresources.Translator;


public class AbstractAssociatedGraphFrontend<V, E extends Edge<V>, AV, AE extends Edge<AV>>
			 extends AbstractGraphFrontend<V,E>
			 implements AssociatedGraph<AV, AE>, GsGraphListener<AV,AE>, GraphChangeListener {

    protected Graph<AV,AE> associatedGraph = null;
    protected String associatedID = null;

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
	public void setAssociatedGraph( Graph<AV,AE> associated_graph) {

        if (associated_graph == null || !isAssociationValid( associated_graph)) {
            return;
        }

        if (associatedGraph != null) {
            associatedGraph.removeGraphListener( this);
            associatedGraph.getGraphManager().getEventDispatcher().removeGraphChangeListener(this);
            associatedGraph = null;
            return;
        }
        associatedGraph = associated_graph;
        associatedGraph.addGraphListener(this);
        associated_graph.getGraphManager().getEventDispatcher().addGraphChangedListener(this);
    }
	
    /**
     * @return the graph associated with this one.
     */
	@Override
    public Graph<AV,AE> getAssociatedGraph() {

        if ( associatedGraph == null && getAssociatedGraphID() != null) {
            Graph<AV,AE> ag = GsEnv.getRegistredGraph( associatedID);
            if (ag != null) {
                setAssociatedGraph( ag);
            } else {
                File f = new File(associatedID);
                if (f.exists()) {
                    ag = GsGinsimGraphDescriptor.getInstance().open(f);
                    GsEnv.newMainFrame(ag);
                    setAssociatedGraph(ag);
                } else {
                    GsEnv.error(new GsException(GsException.GRAVITY_INFO, "STR_openAssociatedGraphFailed"+"\n"+associatedID), mainFrame);
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
    public String getAssociatedGraphID() {
        if (associatedGraph != null) {
            associatedID = associatedGraph.getSaveFileName();
            if (associatedID == null) {
                GsEnv.error(new GsException(GsException.GRAVITY_INFO, Translator.getString("STR_associate_save")), mainFrame);
                return null;
            }
        }

        if (associatedID != null) {
            File f = new File(associatedID);
            if (!f.exists() || !f.canRead()) {
                GsEnv.error(new GsException(GsException.GRAVITY_INFO, Translator.getString("STR_associate_notfound")+associatedID), mainFrame);
                associatedID = null;
            }
        } else {
            GsEnv.error(new GsException(GsException.GRAVITY_INFO, Translator.getString("STR_associate_manual")), mainFrame);
        }

        if (associatedID == null) {
            associatedID = GsOpenAction.selectFileWithOpenDialog( mainFrame);
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
	private boolean isAssociationValid( Graph<?,?> graph) {
    	
        return false;
    }
    
    /**
     * set the path to the associated graph.
     * @param value
     */
	@Override	
    public void setAssociatedGraphID(String value) {
        associatedID = value;
    }

    public GsGraphEventCascade edgeAdded(AE data) {
        setAssociatedGraph(null);
        return null;
    }
    public GsGraphEventCascade edgeRemoved(AE data) {
        setAssociatedGraph(null);
        return null;
    }
    public GsGraphEventCascade edgeUpdated(AE data) {
        return null;
    }


    public GsGraphEventCascade vertexAdded(AV data) {
        setAssociatedGraph(null);
        return null;
    }

	public GsGraphEventCascade graphMerged(Collection<AV> data) {
        setAssociatedGraph(null);
		return null;
	}
    public GsGraphEventCascade vertexUpdated(AV data) {
        return null;
    }

    public GsGraphEventCascade vertexRemoved(AV data) {
        setAssociatedGraph(null);
        return null;
    }

    public void graphChanged(GsNewGraphEvent event) {
        if (event.getOldGraph() == associatedGraph) {
            setAssociatedGraph(null);
        }
    }

    public void graphClosed(GsGraph graph) {
        // it must be the associated regulatory graph
        if (graph == associatedGraph) {
            associatedID = associatedGraph.getSaveFileName();
            setAssociatedGraph(null);
        }
    }

	@Override
	public void graphSelectionChanged(GsGraphSelectionChangeEvent event) {
	}

	@Override
	public void updateGraphNotificationMessage(GsGraph graph) {
	}

}
