package org.ginsim.gui.utils.dialog.stackdialog;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.LogicalModelModifier;
import org.colomoto.logicalmodel.perturbation.LogicalModelPerturbation;
import org.colomoto.logicalmodel.tool.reduction.ModelReducer;
import org.ginsim.common.application.GsException;
import org.ginsim.common.callable.ProgressListener;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.perturbation.ListOfPerturbations;
import org.ginsim.core.graph.regulatorygraph.perturbation.Perturbation;
import org.ginsim.core.graph.regulatorygraph.perturbation.PerturbationHolder;
import org.ginsim.core.graph.regulatorygraph.perturbation.PerturbationManager;
import org.ginsim.core.utils.data.ObjectStore;
import org.ginsim.gui.graph.regulatorygraph.perturbation.PerturbationSelectionPanel;

abstract public class LogicalModelActionDialog extends StackDialog implements ProgressListener, PerturbationHolder {

	private static final ObjectAssociationManager OManager = ObjectAssociationManager.getInstance();
	
	protected final RegulatoryGraph lrg;
	private final ListOfPerturbations perturbations;
	private final PerturbationSelectionPanel perturbationPanel;
	
	private Perturbation perturbation = null;
	private String userID = null;

	private JPanel mainPanel = new JPanel(new GridBagLayout());
	private JCheckBox cb_simplify = new JCheckBox("Strip outputs");
	
    public LogicalModelActionDialog(RegulatoryGraph lrg, Frame parent, String id, int w, int h) {
        super(parent, id, w, h);
        this.lrg = lrg;
        this.perturbations = (ListOfPerturbations)OManager.getObject(lrg, PerturbationManager.KEY, true);
        perturbationPanel = new PerturbationSelectionPanel(this, lrg, this);
        super.setMainPanel(getMainPanel());
        
		this.addWindowListener(new java.awt.event.WindowAdapter() { 
			public void windowClosing(java.awt.event.WindowEvent e) {
				cancel();
			}
		});
    }

    /**
     * Change the ID used to remember the selected perturbation (and other settings)
     * @param userID
     */
    protected void setUserID(String userID) {
    	this.userID = userID;
    	perturbationPanel.refresh();
    }
    
    private JPanel getMainPanel() {

		// simplification checkbox
    	GridBagConstraints c = new GridBagConstraints();
		mainPanel.add(cb_simplify, c);

		// mutant panel
		c = new GridBagConstraints();
		c.gridx = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(perturbationPanel, c);

		return mainPanel;
    }
    
    public void setProgress(int n) {
    	setMessage(""+n);
    }
    public void setProgress(String s) {
    	setMessage(s);
    }

	@Override
	public void milestone(Object data) {
    	setMessage(data.toString());
	}

	@Override
	public Perturbation getPerturbation() {
		if (userID != null) {
			return perturbations.getUsedPerturbation(userID);
		}
		return perturbation;
	}
	
	@Override
	public void setPerturbation(Perturbation perturbation) {
		if (userID != null) {
			perturbations.usePerturbation(userID, perturbation);
		} else {
			this.perturbation = perturbation;
		}
	}

	@Override
	public void setResult(Object result) {
		// empty implement implementation: not all derived classes will want to do something
	}

	
	/**
	 * Intercept the setMainPanel method to integrate with this frame's panel
	 * 
	 * @param model
	 */
	@Override
	public void setMainPanel(Component panel) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		
		mainPanel.add(panel, c);
		mainPanel.validate();
	}
	
	@Override
	protected void run() throws GsException {
		LogicalModel model = lrg.getModel();
		
		if (perturbation != null) {
			model = perturbation.apply(model);
		}
		
		if (cb_simplify.isSelected()) {
			ModelReducer reducer = new ModelReducer(model);
			reducer.removePseudoOutputs();
			model = reducer.getModel(); 
		}

		run(model);
	}

	/**
	 * @param model
	 */
	public abstract void run(LogicalModel model);
}
