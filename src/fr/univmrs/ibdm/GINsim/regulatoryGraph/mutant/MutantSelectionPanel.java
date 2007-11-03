package fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.*;

import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsMutantListManager;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.widgets.StackDialog;

public class MutantSelectionPanel extends JPanel {
	private static final long serialVersionUID = 1213902700181873169L;
	
	StackDialog dialog;
	GsRegulatoryGraph graph;
	GsMutantCombo comboMutant;
	
	public MutantSelectionPanel(StackDialog dialog, GsRegulatoryGraph graph, GsMutantStore store) {
		this.dialog = dialog;
		this.graph = graph;
		
		setBorder(BorderFactory.createTitledBorder(Translator.getString("STR_mutants")));
		setLayout(new GridBagLayout());
		comboMutant = new GsMutantCombo(graph, store);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		add(comboMutant, c);
		
		if (dialog != null) {
	        JButton buttonConfigMutants = new JButton(Translator.getString("STR_configure"));
	        buttonConfigMutants.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent e) {
	            	configure();
	            }
	        });
	        c = new GridBagConstraints();
			c.gridx = 1;
			c.gridy = 0;
			add(comboMutant, c);
	        add(buttonConfigMutants);
		}
	}
	
	protected void configure() {
        dialog.addTempPanel(GsRegulatoryMutants.getMutantConfigPanel(graph));
	}

	public void refresh() {
		comboMutant.refresh();
	}
	
	public void setStore(GsMutantStore store) {
		comboMutant.setStore(store);
	}
	
	public GsRegulatoryMutantDef getMutant() {
		Object obj = comboMutant.getSelectedItem();
		if (obj instanceof GsRegulatoryMutantDef) {
			return (GsRegulatoryMutantDef)obj;
		}
		return null;
	}
}


class GsMutantCombo extends JComboBox {
	private static final long serialVersionUID = -7848606073222946763L;

	GsMutantModel model;
	public GsMutantCombo(GsRegulatoryGraph graph, GsMutantStore store) {
		model = new GsMutantModel((GsRegulatoryMutants)graph.getObject(GsMutantListManager.key, false), store);
		setModel(model);
	}
	
	public void refresh() {
		model.refresh();
	}
	
	public void setStore(GsMutantStore store) {
		model.setStore(store);
	}
}


class GsMutantModel extends DefaultComboBoxModel implements ComboBoxModel {
    private static final long serialVersionUID = 2348678706086666489L;
    
    GsRegulatoryMutants listMutants;
    GsMutantStore store;
    
    public GsMutantModel(GsRegulatoryMutants listMutants, GsMutantStore store) {
    	setMutantList(listMutants, store);
    }
    
    void setMutantList(GsRegulatoryMutants mutants, GsMutantStore store) {
        this.listMutants = mutants;
        setStore(store);
    }

    void setStore(GsMutantStore newstore) {
        this.store = newstore;
        if (store == null) {
        	this.store = new BasicMutantStore();
        }
        if (listMutants == null || listMutants.indexOf(store.getMutant()) == -1) {
        	store.setMutant(null);
        }
        // it must be a nicer way to do it but at least it works
        fireContentsChanged(this, 0, getSize());
    }

    public void refresh() {
    	fireContentsChanged(this, 0, getSize());
    }
    
    public Object getSelectedItem() {
    	if (store == null) {
    		return "--";
    	}
    	GsRegulatoryMutantDef mutant = store.getMutant();
    	if (mutant == null) {
    		return "--";
    	}
    	return mutant;
    }

    public void setSelectedItem(Object anItem) {
        if (anItem instanceof GsRegulatoryMutantDef && listMutants.indexOf(anItem) != -1) {
            store.setMutant((GsRegulatoryMutantDef)anItem);
        } else {
        	store.setMutant(null);
        }
        fireContentsChanged(this, 0, getSize());
    }

    public Object getElementAt(int index) {
        if (index == 0 || listMutants == null) {
            return "--";
        }
        return listMutants.getElement(null, index-1);
    }

    public int getSize() {
        if (listMutants == null) {
            return 1;
        }
        return listMutants.getNbElements(null)+1;
    }
}

class BasicMutantStore implements GsMutantStore {
	GsRegulatoryMutantDef mutant = null;
	public GsRegulatoryMutantDef getMutant() {
		return mutant;
	}
	public void setMutant(GsRegulatoryMutantDef mutant) {
		this.mutant = mutant;
	}
}