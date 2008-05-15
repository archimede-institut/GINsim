package fr.univmrs.tagc.GINsim.regulatoryGraph.modelModifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.datastore.SimpleGenericList;
import fr.univmrs.tagc.common.datastore.gui.GenericListPanel;
import fr.univmrs.tagc.common.widgets.StackDialog;

public class ModelSimplifierConfigDialog extends StackDialog implements ListSelectionListener {
	private static final long	serialVersionUID	= 3618855894072951620L;

	GsRegulatoryGraph graph;
	GenericListPanel lp;
	SimplifierConfigContentList ctlist;
	boolean isRunning = false;
	
	ModelSimplifierConfigDialog(GsRegulatoryGraph graph) {
		super(graph.getGraphManager().getMainFrame(), "modelSimplifier", 600, 500);
		this.graph = graph;
		setTitle("select nodes to remove");
		
		
        ModelSimplifierConfigList cfgList = (ModelSimplifierConfigList)graph.getObject(ModelSimplifierConfigManager.key, true);
		if (cfgList.getNbElements(null) == 0) {
			cfgList.add();
		}
		ctlist = new SimplifierConfigContentList(graph.getNodeOrder());
		GenericListPanel panel = new GenericListPanel();
        panel.setList(ctlist);
        Map m = new HashMap();
        m.put(ModelSimplifierConfig.class, panel);
        lp = new GenericListPanel(m, "modelSimplifier");
        lp.addSelectionListener(this);
        lp.setList(cfgList);
		setMainPanel(lp);
		setVisible(true);
	}
	
	protected void run() {
		if (!isRunning && lp.getSelectedItem() != null) {
			isRunning = true;
			new ModelSimplifier(graph, (ModelSimplifierConfig)lp.getSelectedItem(), this);
	        brun.setEnabled(false);
		}
	}
	
    public void endSimu(GsGraph graph, Exception e) {
    	isRunning = false;
        if (null == graph) {
            Tools.error(e.getMessage(), this.graph.getGraphManager().getMainFrame());
            brun.setEnabled(true);
        } else {
            GsEnv.whatToDoWithGraph(this.graph.getGraphManager().getMainFrame(), graph, false);
            cancel();
        }
    }

	public void valueChanged(ListSelectionEvent e) {
		ctlist.mcolHelper = (ModelSimplifierConfig)lp.getSelectedItem();
		ctlist.refresh();
	}
}

class SimplifierConfigContentList extends SimpleGenericList {

	SimplifierConfigContentList(List nodeOrder) {
		super(nodeOrder);
		canAdd = false;
		canOrder = false;
		canEdit = true;
		nbcol = 2;
		t_type = new Class[2];
		t_type[0] = String.class;
		t_type[1] = Boolean.class;
	}
}
