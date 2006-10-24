package fr.univmrs.ibdm.GINsim.modelChecker;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import fr.univmrs.ibdm.GINsim.global.TempDir;
import fr.univmrs.ibdm.GINsim.graph.GsGraphEventCascade;
import fr.univmrs.ibdm.GINsim.graph.GsGraphListener;
import fr.univmrs.ibdm.GINsim.gui.GsJTable;
import fr.univmrs.ibdm.GINsim.gui.GsList;
import fr.univmrs.ibdm.GINsim.gui.GsListPanel;
import fr.univmrs.ibdm.GINsim.gui.GsStackDialog;
import fr.univmrs.ibdm.GINsim.gui.GsValueList;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.reg2dyn.GsRegulatoryMutantListener;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMutants;

/**
 * Generic UI to setup/run model checking on model/mutants
 */
public class GsModelCheckerUI extends GsStackDialog {
    private static final long serialVersionUID = 8241761052780368139L;

    JTable table;
    JButton b_EditTest;
    JButton b_EditMutant;
    modelCheckerTableModel model;
    GsRegulatoryGraph graph;
    GsList l_tests;
    JSplitPane splitTestEdit;
    GsListPanel panelEditTest;
    JLabel label = new JLabel(Translator.getString("STR_disabled"));
    
    /**
     * @param graph
     */
    public GsModelCheckerUI(GsRegulatoryGraph graph) {
    	super(graph.getGraphManager().getMainFrame());
        this.graph = graph;
        l_tests = (GsList)graph.getObject(GsModelCheckerAssociatedObjectManager.key);
        if (l_tests == null) {
        	l_tests = new modelCheckerList(graph);
        	graph.addObject(GsModelCheckerAssociatedObjectManager.key, l_tests);
        }
        model = new modelCheckerTableModel(graph);
        table = new GsJTable(model);
        JPanel panel = new JPanel();
        
        b_EditTest = new JButton("Edit tests");
        b_EditTest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editTest();
            }
        });
        b_EditMutant = new JButton("Edit mutants");
        b_EditMutant.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editMutants();
            }
        });
        
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        panel.add(b_EditTest, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        panel.add(b_EditMutant, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        JScrollPane sp = new JScrollPane();
        sp.setViewportView(table);
        panel.add(sp, c);
        setMainPanel(panel, "display.mchecker", 450, 300);
    }
    
    protected void editTest() {
    	if (splitTestEdit == null) {
    		splitTestEdit = new JSplitPane();
	    	splitTestEdit.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
	    	splitTestEdit.setDividerSize(5);
	    	splitTestEdit.setDividerLocation(100);
	    	panelEditTest = new GsListPanel();
    	}
    	panelEditTest.setList(l_tests);
    	splitTestEdit.setLeftComponent(panelEditTest);
    	addTempPanel(splitTestEdit);
    	
    	panelEditTest.addSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				updateRightSide();
			}
		});
    	updateRightSide();
    }
    
    protected void updateRightSide() {
    	// TODO: edit the test
    	int[] ts = panelEditTest.getSelection();
    	int location = splitTestEdit.getDividerLocation();
    	if (ts == null || ts.length != 1) {
    		splitTestEdit.setRightComponent(label);
    		splitTestEdit.setDividerLocation(location);
    		return;
    	}
    	GsModelChecker mchecker = (GsModelChecker)l_tests.getElement(ts[0]);
    	splitTestEdit.setRightComponent(mchecker.getEditPanel());
		splitTestEdit.setDividerLocation(location);
    }
    
    protected void editMutants() {
        addTempPanel(GsRegulatoryMutants.getMutantConfigPanel(graph));
    }
    /**
     * run the tests
     * TODO: split it from the UI and run in a separate thread
     */
    protected void run() {
        model.lock();
        brun.setVisible(false);
        File output;
		try {
			output = TempDir.createGeneratedName("GINsim-mcheck", null);
	        for (int i=0 ; i<l_tests.getNbElements() ; i++) {
	            GsModelChecker checker = (GsModelChecker)l_tests.getElement(i);
	        	File odir = TempDir.createGeneratedName("test", output);
	            checker.run(model.mutants, odir);
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
        model.fireTableDataChanged();
        bcancel.setText(Translator.getString("STR_close"));
    }

	protected void cancel() {
		super.cancel();
		dispose();
        for (int i=0 ; i<l_tests.getNbElements() ; i++) {
            GsModelChecker checker = (GsModelChecker)l_tests.getElement(i);
            checker.cleanup();
        }
	}
	
	protected void refreshMain() {
		model.fireTableStructureChanged();
	}
}

class modelCheckerList implements GsList, GsGraphListener, GsRegulatoryMutantListener {

	private Vector v_checker = new Vector();
	private GsRegulatoryGraph graph;
	
	protected modelCheckerList(GsRegulatoryGraph graph) {
		this.graph = graph;
	}
	
	public int add(int i, int type) {
        // find an unused name
        String s = null;
        boolean[] t = new boolean[getNbElements()];
        for (int j=0 ; j<t.length ; j++) {
            t[j] = true;
        }
        for (int j=0 ; j<t.length ; j++) {
            GsModelChecker test = (GsModelChecker)v_checker.get(j);
            if (test.getName().startsWith("test ")) {
                try {
                    int v = Integer.parseInt(test.getName().substring(5));
                    if (v > 0 && v <= t.length) {
                        t[v-1] = false;
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        for (int j=0 ; j<t.length ; j++) {
            if (t[j]) {
                s = "test "+(j+1);
                break;
            }
        }
        if (s == null) {
            s = "test "+(t.length+1);
        }

		Object test = ((GsModelCheckerDescr)GsModelCheckerPlugin.v_checker.get(type)).createNew(s, graph);
		v_checker.add(test);
		return v_checker.indexOf(test);
	}

	public boolean canAdd() {
		return true;
	}

	public boolean canCopy() {
		return false;
	}

	public boolean canEdit() {
		return true;
	}

	public boolean canOrder() {
		return true;
	}

	public boolean canRemove() {
		return true;
	}

	public int copy(int i) {
		return -1;
	}

	public boolean edit(int index, Object o) {
		GsModelChecker test = (GsModelChecker)v_checker.get(index);
		if (test.getName().equals(o.toString())) {
			return false;
		}
		for (int i=0 ; i<v_checker.size() ; i++) {
			if (i != index && ((GsModelChecker)v_checker.get(i)).equals(o.toString())) {
				return false;
			}
		}
		test.setName(o.toString());
		return true;
	}

	public Object getElement(int i) {
		return v_checker.get(i);
	}

	public int getNbElements() {
		return v_checker.size();
	}

    public boolean moveElement(int src, int dst) {
        if (src<0 || dst<0 || src >= v_checker.size() || dst>=v_checker.size()) {
            return false;
        }
        Object o = v_checker.remove(src);
        v_checker.add(dst, o);
        return true;
    }

	public boolean remove(int[] t_index) {
		for (int i=t_index.length-1 ; i>-1 ; i--) {
			v_checker.remove(t_index[i]);
		}
		return true;
	}

	public void mutantAdded(Object mutant) {
	}

	public void mutantRemoved(Object mutant) {
		for (int i=0 ; i<v_checker.size() ; i++) {
			GsModelChecker test = (GsModelChecker)v_checker.get(i);
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

	public Vector getObjectType() {
		return GsModelCheckerPlugin.v_checker;
	}
	
	public Object getInfo(int index, Object mutant) {
		GsModelChecker test = (GsModelChecker)v_checker.get(index);
		return test.getInfo(mutant);
	}
}

class modelCheckerTableModel extends DefaultTableModel {
    private static final long serialVersionUID = 2629792309036499189L;
    
    GsRegulatoryMutants mutants;
    modelCheckerList v_check;
    boolean editable = true;
    
    modelCheckerTableModel(GsRegulatoryGraph graph) {
        mutants = GsRegulatoryMutants.getMutants(graph);
        v_check = (modelCheckerList)graph.getObject(GsModelCheckerAssociatedObjectManager.key);
    }
 
    public void lock() {
    	editable = false;
    	//fireTableStructureChanged();
	}

	public int getColumnCount() {
    	if (v_check == null) {
    		return 1;
    	}
        return v_check.getNbElements() + 1;
    }

    public String getColumnName(int column) {
        if (column == 0) {
            return "mutant";
        }
        return v_check.getElement(column-1).toString();
    }

    public int getRowCount() {
    	if (mutants == null) {
    		return 1;
    	}
        return mutants.getNbElements()+1;
    }

    public Object getValueAt(int row, int column) {
        if (column == 0) {
            if (row == 0) {
                return "-";
            }
            return mutants.getElement(row-1);
        }
        if (row == 0) {
        	return v_check.getInfo(column-1, "-");
        }
        return v_check.getInfo(column-1, mutants.getElement(row-1));
    }

    public boolean isCellEditable(int row, int column) {
        return column>0 && editable;
    }

    public void setValueAt(Object aValue, int row, int column) {
        // do nothing: it IS done!
    }
    
    public Class getColumnClass(int columnIndex) {
        if (columnIndex > 0) {
            return GsValueList.class;
        }
        return super.getColumnClass(columnIndex);
    }
}
