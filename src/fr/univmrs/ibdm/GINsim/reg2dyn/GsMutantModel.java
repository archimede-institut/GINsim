package fr.univmrs.ibdm.GINsim.reg2dyn;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMutantDef;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMutants;

public class GsMutantModel extends DefaultComboBoxModel implements ComboBoxModel {
    private static final long serialVersionUID = 2348678706086666489L;
    
    GsRegulatoryMutants listMutants;
    GsSimulationParameters currentParam = null;
    GsRegulatoryMutantDef curMutant = null;
    
    public GsMutantModel(GsRegulatoryMutants listMutants) {
        this.listMutants = listMutants;
    }
    
    void setMutantList(GsRegulatoryMutants mutants) {
            this.listMutants = mutants;
            fireContentsChanged(this, 0, getSize());
    }

    void setParam(GsSimulationParameters param) {
        currentParam = param;
        setSelectedItem(currentParam.mutant);
        fireContentsChanged(this, 0, getSize());
    }
    
    public Object getSelectedItem() {
    	if (currentParam != null) {
            if (currentParam.mutant == null) {
                return "--";
            }
            return currentParam.mutant;
    	}
    	return curMutant;
    }

    public void setSelectedItem(Object anItem) {
        if (anItem instanceof GsRegulatoryMutantDef) {
            curMutant = (GsRegulatoryMutantDef)anItem;
        } else {
            curMutant = null;
        }
        if (currentParam != null) {
        	currentParam.mutant = curMutant;
        }
    }

    public Object getElementAt(int index) {
        if (index == 0 || listMutants == null) {
            return "--";
        }
        return listMutants.getElement(index-1);
    }

    public int getSize() {
        if (listMutants == null) {
            return 1;
        }
        return listMutants.getNbElements()+1;
    }
    
    public GsRegulatoryMutantDef getMutant() {
    	if (currentParam != null) {
    		return currentParam.mutant;
    	}
    	return curMutant;
    }
}