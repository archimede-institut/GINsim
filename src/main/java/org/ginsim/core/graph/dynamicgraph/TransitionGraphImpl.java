package org.ginsim.core.graph.dynamicgraph;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.NodeInfo;
import org.colomoto.mddlib.MDDManager;
import org.ginsim.core.graph.AbstractDerivedGraph;
import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.GraphFactory;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;

import java.util.List;

public abstract class TransitionGraphImpl<V,E extends Edge<V>> extends AbstractDerivedGraph<V, E, RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge> implements TransitionGraph<V,E> {

    protected List<NodeInfo> nodeOrder;
    private String[] extraNames = null;
    private MDDManager ddmanager = null;
    private int[] extraFunctions = null;

    protected TransitionGraphImpl(GraphFactory factory) {
        super(factory);
    }

    @Override
    public String[] getExtraNames() {
        return extraNames;
    }

    @Override
    public byte[] fillExtraValues(byte[] state, byte[] extraValues) {
        if (extraFunctions == null) {
            return null;
        }

        byte[] extra = extraValues;

        if (extra == null || extra.length != extraFunctions.length) {
            extra = new byte[extraFunctions.length];
        }

        for (int i=0 ; i<extra.length ; i++) {
            extra[i] = ddmanager.reach(extraFunctions[i], state);
        }
        return extra;
    }

    @Override
    public void setLogicalModel(LogicalModel model) {
        List<NodeInfo> extraNodes = null;
        if (model != null) {
            extraNodes = model.getExtraComponents();
            if (extraNodes == null || extraNodes.size() < 1) {
                model = null;
            }
        }
        if (model == null) {
            // reset extra information
            ddmanager = null;
            extraNames = null;
            extraFunctions = null;
            return;
        }

        ddmanager = model.getMDDManager();
        extraFunctions = model.getExtraLogicalFunctions();
        extraNames= new String[extraFunctions.length];

        for (int i=0 ; i<extraNames.length ; i++) {
            extraNames[i] = extraNodes.get(i).getNodeID();
        }
    }

}
