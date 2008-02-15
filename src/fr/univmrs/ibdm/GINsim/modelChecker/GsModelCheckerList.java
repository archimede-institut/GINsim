package fr.univmrs.ibdm.GINsim.modelChecker;

import javax.swing.table.DefaultTableModel;

import fr.univmrs.ibdm.GINsim.graph.GsGraphEventCascade;
import fr.univmrs.ibdm.GINsim.graph.GsGraphListener;
import fr.univmrs.ibdm.GINsim.reg2dyn.GsRegulatoryMutantListener;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsMutantListManager;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant.GsRegulatoryMutants;
import fr.univmrs.tagc.common.datastore.SimpleGenericList;
import fr.univmrs.tagc.common.datastore.ValueList;

public class GsModelCheckerList extends SimpleGenericList implements GsGraphListener, GsRegulatoryMutantListener {

	private GsRegulatoryGraph graph;
	
	protected GsModelCheckerList(GsRegulatoryGraph graph) {
		this.graph = graph;
		prefix = "test_";
		canAdd = true;
		canRemove = true;
		canEdit = true;
	}

	public Object doCreate(String name) {
		return ((GsModelCheckerDescr)GsModelCheckerPlugin.v_checker.get(0)).createNew(name, graph);
	}

	public void mutantAdded(Object mutant) {
	}

	public void mutantRemoved(Object mutant) {
		for (int i=0 ; i<v_data.size() ; i++) {
			GsModelChecker test = (GsModelChecker)v_data.get(i);
			test.delMutant(mutant);
		}
	}

	public GsGraphEventCascade edgeAdded(Object data) {
		return null;
	}

	public GsGraphEventCascade edgeRemoved(Object data) {
		return null;
	}

	public GsGraphEventCascade edgeUpdated(Object data) {
		return null;
	}

	public GsGraphEventCascade vertexAdded(Object data) {
		return null;
	}

	public GsGraphEventCascade vertexRemoved(Object data) {
		return null;
	}

	public GsGraphEventCascade vertexUpdated(Object data) {
		return null;
	}

	public GsGraphEventCascade graphMerged(Object data) {
		return null;
	}

	public Object getInfo(int index, Object mutant) {
		GsModelChecker test = (GsModelChecker)v_data.get(index);
		return test.getInfo(mutant);
	}
}

class modelCheckerTableModel extends DefaultTableModel {
    private static final long serialVersionUID = 2629792309036499189L;
    
    GsRegulatoryMutants mutants;
    GsModelCheckerList v_check;
    boolean editable = true;
    
    modelCheckerTableModel(GsRegulatoryGraph graph) {
        mutants = (GsRegulatoryMutants)graph.getObject(GsMutantListManager.key, true);
        v_check = (GsModelCheckerList)graph.getObject(GsModelCheckerAssociatedObjectManager.key, false);
    }
 
    public void lock() {
    	editable = false;
    	//fireTableStructureChanged();
	}

	public int getColumnCount() {
    	if (v_check == null) {
    		return 1;
    	}
        return v_check.getNbElements(null) + 1;
    }

    public String getColumnName(int column) {
        if (column == 0) {
            return "mutant";
        }
        return v_check.getElement(null, column-1).toString();
    }

    public int getRowCount() {
    	if (mutants == null) {
    		return 1;
    	}
        return mutants.getNbElements(null)+1;
    }

    public Object getValueAt(int row, int column) {
        if (column == 0) {
            if (row == 0) {
                return "-";
            }
            return mutants.getElement(null, row-1);
        }
        if (row == 0) {
        	return v_check.getInfo(column-1, "-");
        }
        return v_check.getInfo(column-1, mutants.getElement(null, row-1));
    }

    public boolean isCellEditable(int row, int column) {
        return column>0 && editable;
    }

    public void setValueAt(Object aValue, int row, int column) {
        // do nothing: it IS done!
    }
    
    public Class getColumnClass(int columnIndex) {
        if (columnIndex > 0) {
            return ValueList.class;
        }
        return super.getColumnClass(columnIndex);
    }
}
