package org.ginsim.gui.graph.dynamicgraph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

import org.ginsim.common.utils.Translator;
import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.NodeInfo;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.dynamicgraph.DynamicNode;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.EditAction;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graph.GraphGUIHelper;
import org.ginsim.gui.graph.regulatorygraph.RegulatoryGraphOptionPanel;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.gui.utils.widgets.EnhancedJTable;
import org.ginsim.gui.utils.widgets.Frame;
import org.ginsim.servicegui.tool.dynamicanalyser.DynamicItemAttributePanel;
import org.ginsim.servicegui.tool.stablestates.StableTableModel;
import org.mangosdk.spi.ProviderFor;


@ProviderFor( GraphGUIHelper.class)
public class DynamicGraphGUIHelper implements GraphGUIHelper<DynamicGraph, DynamicNode, Edge<DynamicNode>> {

	/**
	 * Provide the file filter to apply to a file chooser
	 * 
	 * @return the file filter to apply to a file chooser
	 */
	@Override
	public FileFilter getFileFilter() {
		
		GsFileFilter ffilter = new GsFileFilter();
	    ffilter.setExtensionList(new String[] {"ginml"}, "ginml files");

		return ffilter;
	}

	/**
	 * Create a panel containing the option for graph saving 
	 * 
	 * @param graph the edited graph
	 */
	@Override
	public JPanel getSaveOptionPanel( DynamicGraph graph) {
		
		Frame graph_frame = GUIManager.getInstance().getFrame( graph);
		
        Object[] t_mode = { Translator.getString("STR_saveNone"),
                    		Translator.getString("STR_savePosition"),
                    		Translator.getString("STR_saveComplet") };
        JPanel optionPanel = new RegulatoryGraphOptionPanel(t_mode, graph_frame != null ? 2 : 0);
		
		return optionPanel ;
	}
	
	@Override
	public GUIEditor<DynamicGraph> getMainEditionPanel(DynamicGraph graph) {
//		RegulatoryGraphEditor editor = new RegulatoryGraphEditor();
//		editor.setEditedObject(graph);
//		return editor;
		// TODO: rework the main graph panel
		return null;
	}

	@Override
	public String getEditingTabLabel(DynamicGraph graph) {
		return "STG";
	}

	@Override
	public GUIEditor<DynamicNode> getNodeEditionPanel(DynamicGraph graph) {
		return new DynamicItemAttributePanel(graph);
	}

	@Override
	public GUIEditor<Edge<DynamicNode>> getEdgeEditionPanel(
			DynamicGraph graph) {
		return new DynamicItemAttributePanel(graph);
	}

    /**
     * browse the graph, looking for stable states
     * @return the list of stable states found
     */
    private List getStableStates( DynamicGraph graph) {
    	// TODO: use cache from the graph itself?
    	
    	List<byte[]> stables = new ArrayList<byte[]>();
        for (DynamicNode node: graph.getNodes()) {
            if (node.isStable()) {
                stables.add(node.state);
            }
        }
        return stables;
    }
    
	
	/**
	 * Callback for the info panel: open a dialog with the list of stable states
	 */
	protected void viewStable( DynamicGraph graph, List<byte[]> stables) {
		List<NodeInfo> nodeOrder = graph.getNodeOrder();
        JFrame frame = new JFrame(Translator.getString("STR_stableStates"));
        frame.setSize(Math.min(30*(nodeOrder.size()+1), 800),
        		Math.min(25*(stables.size()+2), 600));
        JScrollPane scroll = new JScrollPane();
        StableTableModel model = new StableTableModel(nodeOrder);
        try {
	        model.setResult(stables, graph);
	        scroll.setViewportView(new EnhancedJTable(model));
	        frame.setContentPane(scroll);
	        frame.setVisible(true);
        } catch (Exception e) {
        	LogManager.error(e);
        }
	}
	
	@Override
	public JPanel getInfoPanel( DynamicGraph graph) {
        JPanel pinfo = new JPanel();
        List<byte[]> stables = getStableStates( graph);

        // just display the number of stable states here and a "show more" button
        if (stables.size() > 0) {
            pinfo.add(new JLabel("nb stable: "+stables.size()));
            JButton b_view = new JButton("view");
            // show all stables: quickly done but, it is "good enough" :)
            b_view.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	
                	// TODO : REFACTORING ACTION
                	// FIXME: restore info panel for the dynamic graph
                	
//                	try{
//                		viewStable(graph);
//                	}
//                	catch( GsException ge){
//                		// TODO : REFACTORING ACTION
//                		// TODO : Launch a message box to the user
//                		LogManager.log( "Unable to get the stable states" + ge);
//                	}
                }
            });
            pinfo.add(b_view);
        } else if (stables.size() > 1) {
            pinfo.add(new JLabel("no stable state."));
        }

        return pinfo;	}

	@Override
	public Class<DynamicGraph> getGraphClass() {
		return DynamicGraph.class;
	}

	@Override
	public List<EditAction> getEditActions(DynamicGraph graph) {
		return null;
	}
}
