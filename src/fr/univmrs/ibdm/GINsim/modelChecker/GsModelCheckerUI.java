package fr.univmrs.ibdm.GINsim.modelChecker;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import fr.univmrs.ibdm.GINsim.gui.GsJTable;
import fr.univmrs.ibdm.GINsim.gui.GsStackDialog;
import fr.univmrs.ibdm.GINsim.gui.GsValueList;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMutants;

/**
 * Generic UI to setup/run model checking on model/mutants
 */
public class GsModelCheckerUI extends GsStackDialog {
    private static final long serialVersionUID = 8241761052780368139L;

    JTable table;
    JComboBox combo_tests;
    JComboBox combo_mchecker;
    JTextField tf_name;
    JButton b_addTest;
    JButton b_EditTest;
    JButton b_EditMutant;
    JButton b_DelTest;
    modelCheckerTableModel model;
    GsRegulatoryGraph graph;
    
    /**
     * @param graph
     */
    public GsModelCheckerUI(GsRegulatoryGraph graph) {
    	super(graph.getGraphManager().getMainFrame());
        this.graph = graph;
        model = new modelCheckerTableModel(graph);
        JPanel panel = new JPanel();
        
        b_addTest = new JButton("+");
        b_addTest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addTest();
            }
        });
        b_DelTest = new JButton("X");
        b_DelTest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                delTest();
            }
        });
        b_EditTest = new JButton("Edit");
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
        combo_tests = new JComboBox(model.v_check);
        combo_mchecker = new JComboBox(GsModelCheckerPlugin.v_checker);
        table = new GsJTable(model);
        tf_name = new JTextField();
        
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(combo_tests, c);
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 1;
        panel.add(b_EditTest, c);
        c = new GridBagConstraints();
        c.gridx = 3;
        c.gridy = 1;
        panel.add(b_DelTest, c);

        c = new GridBagConstraints();
        c.gridx = 4;
        c.gridy = 1;
        panel.add(b_EditMutant, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(combo_mchecker, c);
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 2;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(tf_name, c);
        c = new GridBagConstraints();
        c.gridx = 4;
        c.gridy = 2;
        panel.add(b_addTest, c);
        
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 4;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        JScrollPane sp = new JScrollPane();
        sp.setViewportView(table);
        panel.add(sp, c);
        setMainPanel(panel, "display.mchecker", 450, 300);
    }
    
    protected void addTest() {
        GsModelCheckerDescr mchecker =  (GsModelCheckerDescr)GsModelCheckerPlugin.v_checker.get(combo_mchecker.getSelectedIndex());
        model.AddTest(mchecker.createNew(tf_name.getText(), graph));
    }
    protected void editTest() {
        GsModelChecker checker = (GsModelChecker)combo_tests.getSelectedObjects()[0];
        checker.edit();
    }
    
    protected void editMutants() {
        GsRegulatoryMutants.getMutantConfigPanel(graph);
    }
    protected void delTest() {
        model.removeTest(combo_tests.getSelectedIndex());
    }
    
    protected void run() {
        for (int i=0 ; i<model.v_check.size() ; i++) {
            GsModelChecker checker = (GsModelChecker)model.v_check.get(i);
            checker.run(model.mutants);
        }
    }

	protected void cancel() {
		super.cancel();
		dispose();
	}
}

class modelCheckerTableModel extends DefaultTableModel {
    private static final long serialVersionUID = 2629792309036499189L;
    
    GsRegulatoryMutants mutants;
    Vector v_check;
    Vector v_items;
    
    Vector v_data;
    
    modelCheckerTableModel(GsRegulatoryGraph graph) {
        v_items = new Vector();
        v_items.add("YES");
        v_items.add("NO");
        v_items.add("N/A");
        
        mutants = GsRegulatoryMutants.getMutants(graph);
        v_check = (Vector)graph.getObject("modelChecker");
        if (v_check == null) {
            v_check = new Vector();
            graph.addObject("modelChecker", v_check);
            v_data = new Vector();
            graph.addObject("modelCheckerData", v_data);
        } else {
            v_data = (Vector)graph.getObject("modelCheckerData");
        }
    }
 
    void AddTest(Object test) {
        v_check.add(test);
        fireTableStructureChanged();
        Vector v_newTest = new Vector();
        GsValueList l = new GsValueList(v_items, 2);
        v_newTest.add(l);
        if (mutants != null) {
            for (int i=0 ; i<mutants.getNbElements() ; i++) {
                l = new GsValueList(v_items, 0);
                v_newTest.add(l);
            }
        }
        v_data.add(v_newTest);
    }
    
    void removeTest(int index) {
        v_check.remove(index);
        v_data.remove(index);
        fireTableStructureChanged();
    }
    
    public int getColumnCount() {
        if (v_check == null) {
            return 1;
        }
        return v_check.size() + 1;
    }

    public String getColumnName(int column) {
        if (column == 0) {
            return "mutant";
        }
        return v_check.get(column-1).toString();
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
        Vector v = (Vector)v_data.get(column-1);
        return v.get(row);
    }

    public boolean isCellEditable(int row, int column) {
        return true;
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
