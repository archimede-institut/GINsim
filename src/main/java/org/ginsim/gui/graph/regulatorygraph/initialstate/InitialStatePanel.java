package org.ginsim.gui.graph.regulatorygraph.initialstate;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.*;

import org.colomoto.biolqm.io.avatar.AvatarUtils;
import org.ginsim.common.application.Txt;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStateList;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStateStore;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStatesHandler;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStatesManager;

public class InitialStatePanel extends JPanel {
	
    private static final long serialVersionUID = -572201856207494392L;
    
    protected StateListPanel initPanel;
    protected StateListPanel inputPanel;
    protected JLabel messageLabel = new JLabel();
    
	public InitialStatePanel() {}
	
	public InitialStatePanel(NamedStatesHandler imanager, boolean several,
			String titleInternal, String titleInput) {
	    initPanel = new StateListPanel(this, imanager.getInitialStates(), several, titleInternal);
	    inputPanel = new StateListPanel(this, imanager.getInputConfigs(), several, titleInput);
	    doLayout(imanager.getNormalNodes().size()>0,imanager.getInputNodes().size()>0);
	}
	
	public InitialStatePanel(NamedStatesHandler imanager, boolean several) {
		this(imanager, several, Txt.t("STR_Initial_state"), Txt.t("STR_Fixed_inputs"));
	}
	protected void doLayout(boolean hasNormal, boolean hasInputs){
	    setLayout(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	    c.weightx = 1;
	    c.fill = GridBagConstraints.HORIZONTAL;
	    add(messageLabel, c);
	    messageLabel.setForeground(Color.RED);
	    
	    c.weightx = c.weighty = 1;
        c.gridy = 1;
	    c.fill = GridBagConstraints.BOTH;
	    if(hasNormal) add(initPanel, c);
        c.gridy = 2;
        if(hasInputs) add(inputPanel, c);
	}
	public InitialStatePanel(Graph graph, boolean several,
			String titleInternal, String titleInput) {
		this((NamedStatesHandler) ObjectAssociationManager.getInstance().getObject(graph, NamedStatesManager.KEY, true), several,
				titleInternal, titleInput);
	}
    public InitialStatePanel(Graph graph, boolean several) {
        this((NamedStatesHandler) ObjectAssociationManager.getInstance().getObject(graph, NamedStatesManager.KEY, true), several);
    }
	public void setParam(NamedStateStore currentParameter) {
        initPanel.setParam(currentParameter.getInitialState());
        inputPanel.setParam(currentParameter.getInputState());
    }
    public void setSelection(boolean[] selection, boolean[] iselection){
    	initPanel.setSelection(selection);
    	inputPanel.setSelection(iselection);
    }
	public boolean[] getSelection(boolean input){
		if(input) return inputPanel.getSelection();
		else return initPanel.getSelection();
	}
    public void setMessage(String message) {
    	this.messageLabel.setText(message);
    }
	public NamedStateList getStateList(){
		return initPanel.getSelectedStateList(false);
	}
	public NamedStateList getIStateList(){
		return inputPanel.getSelectedStateList(true);
	}
	public NamedStateList getAllStateList(){
		return initPanel.getStateList();
	}
	public NamedStateList getAllIStateList(){
		return inputPanel.getStateList();
	}
    public String toString(){
    	String result = "states=";
    	NamedStateList states = initPanel.getStateList(), istates = inputPanel.getStateList(); 
    	for(int i=0, l=states.size(); i<l; i++) 
    		result+=states.get(i).getMap().toString()+",";
    	result = result.substring(0,result.length()-1)+"\nstatesselection=";
    	result += AvatarUtils.toString(initPanel.getSelection())+"\nistates=";
    	for(int i=0, l=istates.size(); i<l; i++) 
    		result+=istates.get(i).getMap().toString()+",";
    	return result.substring(0,result.length()-1)+"\nistatesselection="+AvatarUtils.toString(inputPanel.getSelection());
    }
}
