package org.ginsim.gui.graph.dynamicgraph;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

import org.colomoto.logicalmodel.NodeInfo;
import org.ginsim.common.application.LogManager;
import org.ginsim.common.application.Txt;
import org.ginsim.core.annotation.Annotation;
import org.ginsim.core.graph.dynamicgraph.DynamicEdge;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.dynamicgraph.DynamicNode;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.annotation.AnnotationPanel;
import org.ginsim.gui.graph.EditAction;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graph.GraphGUIHelper;
import org.ginsim.gui.graph.regulatorygraph.RegulatoryGraphOptionPanel;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.gui.utils.data.GenericPropertyEditorPanel;
import org.ginsim.gui.utils.widgets.EnhancedJTable;
import org.ginsim.gui.utils.widgets.Frame;
import org.mangosdk.spi.ProviderFor;


@ProviderFor( GraphGUIHelper.class)
public class DynamicGraphGUIHelper implements GraphGUIHelper<DynamicGraph, DynamicNode, DynamicEdge> {

	static {
		GenericPropertyEditorPanel.addSupportedClass(Annotation.class, AnnotationPanel.class);
	}
	
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
		
        Object[] t_mode = { Txt.t("STR_saveNone"),
                    		Txt.t("STR_savePosition"),
                    		Txt.t("STR_saveComplet") };
        JPanel optionPanel = new RegulatoryGraphOptionPanel(t_mode, graph_frame != null ? 2 : 0);
		
		return optionPanel ;
	}
	
	@Override
	public GUIEditor<DynamicGraph> getMainEditionPanel(DynamicGraph graph) {
		StateTransitionGraphEditor editor = new StateTransitionGraphEditor(graph);
		return editor;
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
	public GUIEditor<DynamicEdge> getEdgeEditionPanel(
			DynamicGraph graph) {
		return new DynamicItemAttributePanel(graph);
	}

    /**
     * browse the graph, looking for stable states
     * @return the list of stable states found
     */
    private List<byte[]> getStableStates( DynamicGraph graph) {
    	// TODO: use cache from the graph itself?
    	
    	List<byte[]> stables = new ArrayList<byte[]>();
        for (DynamicNode node: graph.getNodes()) {
            if (node.isStable()) {
                stables.add(node.state);
            }
        }
        return stables;
    }


	@Override
	public JPanel getInfoPanel( DynamicGraph graph) {
        JPanel pinfo = new JPanel();
        List<byte[]> stables = getStableStates( graph);

        // just display the number of stable states here and a "View" button
        if (stables.size() > 0) {
            pinfo.add(new JLabel("nb stable: "+stables.size()));
            JButton b_view = new JButton(new ViewStableAction(graph, stables));
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

	@Override
	public boolean canCopyPaste(DynamicGraph graph) {
		return false;
	}
}


class ViewStableAction extends AbstractAction {
	
	private final DynamicGraph graph;
	private final List<byte[]> stables;
	
	public ViewStableAction(DynamicGraph graph, List<byte[]> stables) {
		super("View");
		this.graph = graph;
		this.stables = stables;
	}
	
    public void actionPerformed(ActionEvent e) {
		List<NodeInfo> nodeOrder = graph.getNodeOrder();
        int width = 30*(nodeOrder.size()+1);
        int height = 30 * (stables.size() + 2);
		JFrame frame = new JFrame(Txt.t("Stable States in this STG"));
        frame.setSize(width, height);
        
        JScrollPane scroll = new JScrollPane();
        StableTableModel model = new StableTableModel();
        try {
	        model.setResult(stables, graph.getNodeOrder());
	        scroll.setViewportView(new EnhancedJTable(model));
	        frame.setContentPane(scroll);
	        frame.setVisible(true);
        } catch (Exception ex) {
        	LogManager.error(ex);
        }
	}

}