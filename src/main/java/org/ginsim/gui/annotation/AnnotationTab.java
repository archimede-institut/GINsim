package org.ginsim.gui.annotation;

import org.colomoto.biolqm.NodeInfo;
import org.colomoto.biolqm.metadata.AnnotationModule;
import org.colomoto.biolqm.metadata.annotations.Metadata;
import org.colomoto.biolqm.metadata.constants.Index;
import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.gui.annotation.classes.AnnotationsComponent;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.GraphSelection;
import org.ginsim.gui.shell.editpanel.EditTab;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class AnnotationTab extends JPanel implements EditTab {
	
	private static final long serialVersionUID = 1L;
	private AnnotationModule annotationModule;
	private GridBagConstraints gbc;

	public AnnotationTab(AnnotationModule newAnnotationModule) {
		this.annotationModule = newAnnotationModule;
		
		this.setLayout(new GridBagLayout());
		
		this.gbc = new GridBagConstraints();
		this.gbc.weightx = 1.0;
		this.gbc.weighty = 1.0;
		this.gbc.fill = GridBagConstraints.BOTH;
		this.gbc.anchor = GridBagConstraints.CENTER;
		this.gbc.gridx = 0;
		this.gbc.gridy = 0;
		
		try {
			this.add(new AnnotationsComponent(this.annotationModule.getMetadataOfModel(), false), this.gbc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public static AnnotationTab prepareTab(GraphGUI<?,?,?> gui) {
        Graph graph = gui.getGraph();

        // TODO: create the panel only of the graph supports annotations
        if (graph instanceof RegulatoryGraph) {
        	AnnotationModule newAnnotationModule = ((RegulatoryGraph) graph).getAnnotationModule();
        	
            return new AnnotationTab(newAnnotationModule);
        }

        return null;
    }

    @Override
    public String getTitle() {
        return "Annotation";
    }

    @Override
    public Component getComponent() {
        return this;
    }
    
    public void updateMetadata(Metadata meta) {
    	this.remove(0);
    	this.add(new AnnotationsComponent(meta, false), this.gbc);
    	this.revalidate();
    	this.repaint();
    }

    @Override
    public boolean isActive(GraphSelection<?, ?> selection) {
        if (selection == null) {
        	Metadata metadataModel = this.annotationModule.getMetadataOfModel();
        	updateMetadata(metadataModel);
            return true;
        }
        
        switch (selection.getSelectionType()) {
            case SEL_NONE:
            	Metadata metadataModel = this.annotationModule.getMetadataOfModel();
            	updateMetadata(metadataModel);
            	return true;
            case SEL_NODE:
            	RegulatoryNode interNode = (RegulatoryNode) selection.getSelectedNodes().get(0);
            	NodeInfo node = interNode.getNodeInfo();
            	
            	Map<NodeInfo, Index> elementNodes = this.annotationModule.nodesIndex;
            	for (NodeInfo element: elementNodes.keySet()) {
            		if(node.equals(element)) {
            			node = element;
            			break;
            		}
            	}
            	
				try {
					Metadata metadataNode = this.annotationModule.getMetadataOfNode(node);
					
					updateMetadata(metadataNode);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                return true;
            case SEL_EDGE:
            	Edge<Object> interEdge = (Edge<Object>) selection.getSelectedEdges().get(0);
            	
            	RegulatoryNode interNode1 = (RegulatoryNode) interEdge.getSource();
            	NodeInfo node1 = interNode1.getNodeInfo();
            	
            	RegulatoryNode interNode2 = (RegulatoryNode) interEdge.getTarget();
            	NodeInfo node2 = interNode2.getNodeInfo();
            	
            	System.out.println("node1");
            	System.out.println(node1);
            	System.out.println(node2);
            	
				try {
					Metadata metadataNode = this.annotationModule.getMetadataOfEdge(node1, node2);
					
					updateMetadata(metadataNode);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                return true;
            case SEL_MULTIPLE:
                return false;
        }
        return false;
    }
}
