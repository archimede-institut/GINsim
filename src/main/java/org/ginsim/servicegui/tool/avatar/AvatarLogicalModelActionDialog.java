package org.ginsim.servicegui.tool.avatar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.perturbation.PerturbationHolder;
import org.ginsim.gui.utils.dialog.stackdialog.LogicalModelActionDialog;
import org.ginsim.service.tool.modelreduction.ReductionHolder;
import org.ginsim.servicegui.tool.avatar.others.MyTitledBorder;
import org.ginsim.servicegui.tool.avatar.others.TitleToolTipPanel;

/**
 * Class for managing contextual aspects associated with the main panel (including the responsibility to update the applicable perturbations and reductions to the input model)
 * 
 * @author Rui Henriques
 * @version 1.0
 */
abstract public class AvatarLogicalModelActionDialog extends LogicalModelActionDialog implements PerturbationHolder, ReductionHolder, ChangeListener {

	private static final long serialVersionUID = 6959189835195994930L;	
    protected int yAdjustment = 130;
    protected boolean flexible = true;

    /**
     * Creates the necessary context to instantiate the panel of avatar simulations
     * @param lrg the target regulatory graph
     * @param flex true for a flexible gridbaglayout (default: true)
     * @param parent the parent panel
     * @param id the ID of the current panel (avatar simulations)
     * @param w the suggested width for the main panel
     * @param h the suggested height for the main panel
     */
    public AvatarLogicalModelActionDialog(RegulatoryGraph lrg, boolean flex, Frame parent, String id, int w, int h) {
        super(lrg, parent, id, w, h);
        flexible = flex;

		this.addWindowListener(new java.awt.event.WindowAdapter() { 
			public void windowClosing(java.awt.event.WindowEvent e) {
				cancel();
			}
		});
    }
    
    /**
     * Accesses the panel responsible to apply perturbations and reductions over the current graph
     * @return the panel with applicable perturbations and reductions to the inputted model
     */
    protected JPanel getTopPanel() {
    	JPanel topPanel = new TitleToolTipPanel();
    	topPanel.setLayout(new GridBagLayout());
    	Color blue = new Color(130,180,246), black = new Color(0, 0, 0);
    	String graphChangesVar = "Applicable perturbations and reductions to the inputted model";
    	
        if(flexible){
        	JLabel title = new JLabel("      Graph changes");
        	title.setIcon(new ImageIcon(getClass().getResource("/greyQuestionMark.png")));
            topPanel.setBorder(new MyTitledBorder(new LineBorder(blue,2),title, TitledBorder.LEADING, TitledBorder.TOP, null, black));
            topPanel.setToolTipText(graphChangesVar);
        } else {
        	topPanel.setBorder(new TitledBorder(new LineBorder(blue,2),"Graph changes     ", TitledBorder.LEADING, TitledBorder.TOP, null, black));
    		JLabel changesQuestion = new JLabel("");
    		changesQuestion.setBounds(103,5,15,15);
    		changesQuestion.setIcon(new ImageIcon(getClass().getResource("/greyQuestionMark.png")));
    		changesQuestion.setToolTipText(graphChangesVar);
    		mainPanel.add(changesQuestion);
        }
		ToolTipManager.sharedInstance().setInitialDelay(0);
        GridBagConstraints c = new GridBagConstraints();
        
        // perturbation panel
        c.weightx = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        topPanel.add(perturbationPanel, c);

        // reduction panel
        c.gridy = 1;
		c.weightx = 1;
        c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		topPanel.add(reductionPanel, c);

        // simplification checkboxes
        c.gridx = 1;
        c.weightx = 0;
        topPanel.add(cb_simplify, c);
    	return topPanel;
    }
    
	@Override
    protected JPanel getMainPanel() {
        return mainPanel;
    }
    
	/**
	 * Intercept the setMainPanel method to integrate with this frame's panel
	 * @param panel
	 */
	@Override
	public void setMainPanel(Component panel) {
		if(flexible) return;
		panel.setBounds(panel.getX(), panel.getY()+yAdjustment, panel.getWidth(), panel.getHeight());
		mainPanel.add(panel);
		mainPanel.validate();
	}
	
	/**
	 * Adds a component to the main panel satisfying the inputted gridbag layout constraint
	 * @param panel the component to be added to the main panel
	 * @param g the constraints associated with the positioning of the component (according to gridbaglayout)
	 */
	public void setMainPanel(Component panel, GridBagConstraints g) {
		mainPanel.add(panel,g);
	}
}
