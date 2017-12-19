package org.ginsim.servicegui.tool.avatar.parameters;

import org.ginsim.core.utils.data.NamedList;
import org.ginsim.gui.graph.regulatorygraph.initialstate.CompleteStatePanel;
import org.ginsim.gui.utils.data.ColumnDefinition;
import org.ginsim.gui.utils.data.ListEditionPanel;
import org.ginsim.gui.utils.data.ListPanelCompanion;
import org.ginsim.gui.utils.data.ListPanelHelper;
import org.ginsim.service.tool.avatar.params.AvatarParameterList;
import org.ginsim.service.tool.avatar.params.AvatarParameters;
import org.ginsim.servicegui.tool.avatar.AvatarConfigFrame;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper for loading, unloading and displaying a list of simulation parameters
 * @author Rui Henriques
 * @version 1.0
 */
public class AvatarParametersHelper extends ListPanelHelper<AvatarParameters, AvatarParameterList> {

    private AvaParameterEditionPanel editionPanel;

    /**
     * Instantiates a helper using the panel of parameters to display, load or unload
     * @param panel the panel maintaining the context of a simulation (list of parameters)
     */
    public AvatarParametersHelper(AvaParameterEditionPanel panel) {
    	editionPanel = panel;
    }

    /**
     * Populates the fields of the main panel from a given simulation context (parameters) 
     * @param param the context of a simulation (parameters)
     * @param acf the main panel to be populated from the given context
     */
    public static void unload(AvatarParameters param, AvatarConfigFrame acf){
    	acf.jcbAlgorithm.setSelectedIndex(param.algorithm);
    	acf.quiet.setSelected(param.quiet);
		acf.statestore = param.statestore;
		acf.states = new CompleteStatePanel(acf.statestore.nstates,acf.statestore.instates,acf.statestore.oracles,true);
		acf.states.setParam(acf.statestore);
		acf.states.setSelection(param.statesSelected, param.istatesSelected); //param.oraclesSelected, param.ioraclesSelected, param.enabled, param.ienabled);
    	acf.panelAvatar.unload(param);
    	acf.panelFF.unload(param);
    	acf.panelMC.unload(param);
    }
    	
    /**
     * Creates a simulation context (parameters) using the fields from the main panel
     * @param main the main panel from which fields are to be read
     * @return the context of a simulation (parameters)
     */
    public static AvatarParameters load(AvatarConfigFrame main){
    	AvatarParameters p = new AvatarParameters();
    	p.algorithm = main.jcbAlgorithm.getSelectedIndex();
    	p.quiet = main.quiet.isSelected();
    	
    	p.statestore = main.statestore;
    	p.statesSelected = main.states.getSelection(false);
    	p.istatesSelected = main.states.getSelection(true);
    	/*p.oraclesSelected = main.states.getOracleSelection(false);
    	p.ioraclesSelected = main.states.getOracleSelection(true);
    	p.enabled = main.states.getDisabledEdition(false);
    	p.ienabled = main.states.getDisabledEdition(true);*/
    	//if(p.enabled!=null) System.out.println(">>"+AvatarUtils.toString(p.enabled));
    	
    	main.panelAvatar.load(p);
    	main.panelFF.load(p);
    	main.panelMC.load(p);
    	return p;
    }

    @Override
    public int doCreate(AvatarParameterList list, Object arg) {
    	AvatarParameters p = load(editionPanel.stackDialog);
        list.add(p);
        return list.indexOf(p);
    }

    @Override
    public boolean doRemove(AvatarParameterList list, int[] sel) {
        List<AvatarParameters> l = new ArrayList<AvatarParameters>();
        for (int idx: sel) l.add(list.get(idx));
        list.removeAll(l);
        return true;
    }

    @Override
    public ListPanelCompanion<AvatarParameters,AvatarParameterList> getCompanion(ListEditionPanel<AvatarParameters, AvatarParameterList> editPanel) {
    	return editionPanel;
    }

    @Override
    public ColumnDefinition[] getColumns() {
        return new ColumnDefinition[]{ColumnDefinition.EDITME};
    }
    
    @Override
    public boolean setValue(AvatarParameterList list, int row, int column, Object value) {
        if (column == 0 && list instanceof NamedList) {
        	AvatarParameters param = editionPanel.paramList.get(row);
        	param.setName((String)value);
        	editionPanel.paramList.set(row, param);
        	//System.out.println(editionPanel.paramList.toString());
            editionPanel.update();
            return true;
        }
        return false;
    }

}