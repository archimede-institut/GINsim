package fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import fr.univmrs.ibdm.GINsim.gui.GsStackDialog;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsMutantListManager;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;

public class MutantSelectionPanel extends JPanel {
	private static final long serialVersionUID = 1213902700181873169L;
	
	GsStackDialog dialog;
	GsRegulatoryGraph graph;
	GsMutantCombo comboMutant;
	
	public MutantSelectionPanel(GsStackDialog dialog, GsRegulatoryGraph graph) {
		this.dialog = dialog;
		this.graph = graph;
		
		setBorder(BorderFactory.createTitledBorder(Translator.getString("STR_mutants")));

		comboMutant = new GsMutantCombo(graph);
		add(comboMutant);
		
		if (dialog != null) {
	        JButton buttonConfigMutants = new JButton(Translator.getString("STR_configure"));
	        buttonConfigMutants.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent e) {
	            	configure();
	            }
	        });
	        add(buttonConfigMutants);
		}
	}
	
	protected void configure() {
        dialog.addTempPanel(GsRegulatoryMutants.getMutantConfigPanel(graph));
        comboMutant.refresh(graph);
	}
	
	public GsRegulatoryMutantDef getMutant() {
		return comboMutant.getMutant();
	}

	public void setSelectedItem(GsRegulatoryMutantDef mutant) {
		comboMutant.setSelectedItem(mutant);
	}
}


class GsMutantCombo extends JComboBox {
	private static final long serialVersionUID = -7848606073222946763L;

	GsMutantModel model;
	public GsMutantCombo(GsRegulatoryGraph graph) {
		refresh(graph);
	}
	
	public GsRegulatoryMutantDef getMutant() {
		return model.curMutant;
	}
	
	public void refresh(GsRegulatoryGraph graph) {
		if (model == null) {
			model = new GsMutantModel((GsRegulatoryMutants)graph.getObject(GsMutantListManager.key, false));
		} else {
			model.setMutantList((GsRegulatoryMutants)graph.getObject(GsMutantListManager.key, false));
		}
		setModel(model);
	}
}


class GsMutantModel extends DefaultComboBoxModel implements ComboBoxModel {
    private static final long serialVersionUID = 2348678706086666489L;
    
    GsRegulatoryMutants listMutants;
    GsRegulatoryMutantDef curMutant = null;
    
    public GsMutantModel(GsRegulatoryMutants listMutants) {
        this.listMutants = listMutants;
    }
    
    void setMutantList(GsRegulatoryMutants mutants) {
            this.listMutants = mutants;
            if (mutants == null || mutants.indexOf(curMutant) == -1) {
            	curMutant = null;
            }
            // it must be a nicer way to do it but at least it works
            fireContentsChanged(this, 0, getSize());
    }

    public Object getSelectedItem() {
    	if (curMutant == null) {
    		return "--";
    	}
    	return curMutant;
    }

    public void setSelectedItem(Object anItem) {
        if (anItem instanceof GsRegulatoryMutantDef && listMutants.indexOf(anItem) != -1) {
            curMutant = (GsRegulatoryMutantDef)anItem;
        } else {
            curMutant = null;
        }
        fireContentsChanged(this, 0, getSize());
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
}