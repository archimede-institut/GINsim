package org.ginsim.core.graph.dynamicgraph;

import java.util.ArrayList;
import java.util.List;

import org.colomoto.biolqm.NodeInfo;
import org.ginsim.core.graph.AbstractGraphFactory;
import org.ginsim.core.graph.GraphFactory;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.view.style.EdgeStyle;
import org.ginsim.core.graph.view.style.NodeStyle;
import org.ginsim.service.export.nusmv.NodeInfoSorter;
import org.kohsuke.MetaInfServices;


/**
 * Factory used to create dynamical graphs (STGs).
 *
 * @author Lionel Spinelli
 * @author Aurelien Naldi
 */
@MetaInfServices( GraphFactory.class)
public class DynamicGraphFactory extends AbstractGraphFactory<DynamicGraph> {

	public static final String KEY = "dynamical";
    private static DynamicGraphFactory instance = null;
	
    public DynamicGraphFactory(){
    	super(DynamicGraph.class, KEY);
    	if (instance == null) {
    		instance = this;
    	}
    }
    
    /**
     * @return an instance of this graphDescriptor.
     */
    public static DynamicGraphFactory getInstance() {
        if (instance == null) {
            instance = new DynamicGraphFactory();
        }
        return instance;
    }

    @Override
	public Class getParser(){
		return DynamicParser.class;
	}
	
    @Override
	public DynamicGraph create(){
		return new DynamicGraphImpl();
	}
	
    
	public DynamicGraph create( List<?> node_order){
    	List<NodeInfo> nis = new ArrayList<>(node_order.size());
    	for (Object node: node_order) {
    		if (node instanceof NodeInfo) {
				nis.add((NodeInfo) node);
			} else if (node instanceof RegulatoryNode) {
    			nis.add(((RegulatoryNode)node).getNodeInfo());
			}
		}
    	return new DynamicGraphImpl( nis);
	}

	@Override
	public NodeStyle<DynamicNode> createDefaultNodeStyle(DynamicGraph graph) {
		return new DefaultDynamicNodeStyle(graph);
	}
	@Override
	public EdgeStyle<DynamicNode,DynamicEdge> createDefaultEdgeStyle(DynamicGraph graph) {
		return new DefaultDynamicEdgeStyle();
	}
}
