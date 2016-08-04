package org.ginsim.servicegui.tool.avatar.parameters;

import org.colomoto.logicalmodel.io.avatar.AvatarUtils;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.utils.data.ListEditionPanel;
import org.ginsim.gui.utils.data.ListPanelCompanion;
import org.ginsim.service.tool.avatar.params.AvatarParameterList;
import org.ginsim.service.tool.avatar.params.AvatarParameters;
import org.ginsim.servicegui.tool.avatar.AvatarConfigFrame;
import javax.swing.*;

/** 
 * Panel to edit, add and remove the context of a simulation (parameters, states and changes to the model)
 * @author Rui Henriques
 * @version 1.0
 */
public class AvaParameterEditionPanel extends JPanel implements ListPanelCompanion<AvatarParameters,AvatarParameterList> {

	private static final long serialVersionUID = 1197188580286713173L;

	/** list of simulation contexts */
	public AvatarParameterList paramList;
	/** pointer to main/parent panel */
    public final AvatarConfigFrame stackDialog;
    
    private ListEditionPanel<AvatarParameters, AvatarParameterList> paramPanel;
    private RegulatoryGraph graph;
    private int[] currentParam = new int[0];
    
    /**
     * Creates the left panel to manage parameterizations
     * @param dialog pointer to the main/parent panel
     * @param rgraph the associated regulatory graph
     * @param paramL the current list of parameters
     */
    public AvaParameterEditionPanel(AvatarConfigFrame dialog, RegulatoryGraph rgraph, AvatarParameterList paramL) {
        this.stackDialog = dialog;
        this.graph = rgraph;
        this.paramList = paramL;
        AvatarParametersHelper helper = new AvatarParametersHelper(this);
    	paramPanel = new ListEditionPanel<AvatarParameters, AvatarParameterList>(
    			helper, paramList, "title", stackDialog, null); 
	}
    
	/**
	 * Accesses the left panel for editing the context of simulations
	 * @return the left panel with simulation contexts
	 */
	public ListEditionPanel<AvatarParameters, AvatarParameterList> getEditionPanel() {
		return paramPanel;
	}
    
	/**
	 * Updates the panel based on the most recent changes to the context of a simulation 
	 */
	public void update() {
		ObjectAssociationManager.getInstance().addObject(graph, "avatar_parameters", paramList);
	}
    
    @Override
    public void selectionUpdated(int[] sel) {
	    if(paramPanel==null) return;
    	if(currentParam.length>0){
    		AvatarParameters param = AvatarParametersHelper.load(stackDialog);
    		param.name = paramList.get(currentParam[0]).name;
    		paramList.set(currentParam[0], param);
    		ObjectAssociationManager.getInstance().addObject(graph, "avatar_parameters", paramList);
    	}
    	if(sel.length>0){
	    	if(currentParam.length>0 && currentParam[0]==sel[0]);
	    	else {
	        	System.out.println("Update:"+AvatarUtils.toString(sel)+",ParamList:#"+paramList.size());
	            AvatarParameters param = paramList.get(sel[0]);
	            stackDialog.refresh(param);
	    	}
    	}
		currentParam = sel;
    }

	public void setCurrent(AvatarParameters p) {
		if(currentParam.length==0) paramList.set(0,p);
		else paramList.set(currentParam[0], p); 
		update();
		System.out.println("HERE1");
		for(AvatarParameters pi : paramList)
			System.out.println("LONG\n"+pi.toFullString());
	}

	@Override
	public void setParentList(AvatarParameterList params) {
        this.paramList = params;
	}
}
