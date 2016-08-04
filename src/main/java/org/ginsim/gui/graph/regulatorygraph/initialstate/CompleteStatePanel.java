package org.ginsim.gui.graph.regulatorygraph.initialstate;

import java.util.List;
import java.util.Map;

import org.colomoto.logicalmodel.io.avatar.AvatarUtils;
import org.ginsim.common.application.Txt;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedState;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStateList;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStateStore;
import org.ginsim.service.tool.avatar.params.AvatarStateStore;

/**
 * Extension of original initial state panel in order to support oracles and cloning
 * @author Rui Henriques
 * @version 1.0
 */
public class CompleteStatePanel extends InitialStatePanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a complete state panel from a given list of states
	 * @param states list of states from normal components
	 * @param istates list of states from input components
	 * @param several whether multiple states are allowed
	 */
	public CompleteStatePanel(NamedStateList states, NamedStateList istates, boolean several) {
		super();
	    initPanel = new StateListPanel(this, states, several, Txt.t("STR_Initial_state"));
	    inputPanel = new StateListPanel(this, istates, several, Txt.t("STR_Fixed_inputs"));
        doLayout(states.getNodeOrder().size()>0,istates.getNodeOrder().size()>0);
	}
	
    /**
     * Clones the state panel based on a named store
     * @param store the store with the list of states
     * @return the cloned state panel
     */
    public CompleteStatePanel clone(AvatarStateStore store){
    	AvatarStateStore clonedstore = new AvatarStateStore(store.nodes,store.inodes,store.allnodes,initPanel.getStateList(),inputPanel.getStateList());
    	CompleteStatePanel clone = new CompleteStatePanel(clonedstore.nstates,clonedstore.instates,true);
    	clone.setParam(clonedstore);
    	clone.initPanel.setSelection(initPanel.getSelection());
    	clone.inputPanel.setSelection(inputPanel.getSelection());
    	clone.initPanel.setOracleSelection(initPanel.getOracleSelection());
    	clone.inputPanel.setOracleSelection(inputPanel.getOracleSelection());
    	clone.initPanel.setDisabledEdition(initPanel.getDisabledEdition());
    	clone.inputPanel.setDisabledEdition(inputPanel.getDisabledEdition());
    	return clone;
    }
	public void setSelection(boolean[] statesSelected, boolean[] istatesSelected, boolean[] oraclesSelected, boolean[] ioraclesSelected, boolean[] enabled, boolean[] ienabled){
    	initPanel.setSelection(statesSelected);
    	inputPanel.setSelection(istatesSelected);
    	initPanel.setOracleSelection(oraclesSelected);
    	inputPanel.setOracleSelection(ioraclesSelected);
    	initPanel.setDisabledEdition(enabled);
    	inputPanel.setDisabledEdition(ienabled);
	}
		
	/**
	 * Updates the information on the state panel
	 * @param currentParameter the named store with the states to be updated
	 */
	public void updateParam(NamedStateStore currentParameter) {
		updateParam(currentParameter, null);
	}
	/**
	 * Updates the information on the state panel
	 * @param currentParameter the named store with the states to be updated
	 * @param sdisabled the number of oracle patterns (associated with normal components) to avoid their edition
	 * @param isdisabled the number of oracle patterns (associated with input components) to avoid their edition 
	 */
	public void updateParam(NamedStateStore currentParameter, Map<Boolean,List<NamedState>> oracles) {
		boolean[] s=getSelection(false), is=getSelection(true); 
		boolean[] o=getOracleSelection(false), io=getOracleSelection(true);
    	boolean[] e=getDisabledEdition(false), ie=getDisabledEdition(true);
		setParam(currentParameter);
        setSelection(s,is,o,io,e,ie);
		if(oracles!=null){
			initPanel.addDisabledEdition(oracles.get(false));
			inputPanel.addDisabledEdition(oracles.get(true));
		}
    	//System.out.println("AKI:"+AvatarUtils.toString(getDisabledEdition(false)));
	}
	
	@Override
	public void setParam(NamedStateStore currentParameter) {
		if(currentParameter instanceof AvatarStateStore){
			List<String> names = ((AvatarStateStore)currentParameter).getNames(false);
	        initPanel.setParam(currentParameter.getInitialState(),names);
			List<String> inames = ((AvatarStateStore)currentParameter).getNames(true);
	        inputPanel.setParam(currentParameter.getInputState(),inames);
		} else super.setParam(currentParameter);
    }

	/**
	 * Gets the list of oracles from selected patterns on the either normal or input components
	 * @param input whether the selection respects normal or input components
	 * @return list of oracles associated with the target components
	 */
	public NamedStateList getOracleStateList(boolean input) {
		return initPanel.getSelectedOracleStateList(input);
	}
	
	/**
	 * Gets the selected oracles given from patterns on the values of either normal or input components
	 * @param input whether the selection respects normal or input components
	 * @return the selected oracles given from patterns on the values of either normal or input components
	 */
	public boolean[] getOracleSelection(boolean input){
		if(input) return inputPanel.getOracleSelection();
		else return initPanel.getOracleSelection();
	}
	
	public boolean[] getDisabledEdition(boolean input){
		if(input) return inputPanel.getDisabledEdition();
		else return initPanel.getDisabledEdition();
	}
}
