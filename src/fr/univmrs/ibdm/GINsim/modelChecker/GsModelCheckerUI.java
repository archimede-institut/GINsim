package fr.univmrs.ibdm.GINsim.modelChecker;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.univmrs.ibdm.GINsim.global.TempDir;
import fr.univmrs.ibdm.GINsim.gui.GsJTable;
import fr.univmrs.ibdm.GINsim.gui.GsList;
import fr.univmrs.ibdm.GINsim.gui.GsListPanel;
import fr.univmrs.ibdm.GINsim.gui.GsStackDialog;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
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
    
    modelCheckerRunner runner = null;
    
    /**
     * @param graph
     */
    public GsModelCheckerUI(GsRegulatoryGraph graph) {
    	super(graph.getGraphManager().getMainFrame());
        this.graph = graph;
        l_tests = (GsList)graph.getObject(GsModelCheckerAssociatedObjectManager.key, true);
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
        runner = new modelCheckerRunner(this, l_tests, model.mutants);
        runner.start();
    }
	protected void endRun() {
        model.fireTableDataChanged();
        bcancel.setText(Translator.getString("STR_close"));
        runner = null;
	}

	protected void cancel() {
		if (runner != null) {
			runner.interrupt();
			return;
		}
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

	public void updateResult(GsNuSMVChecker checker, Object m) {
		model.fireTableDataChanged();
	}
}

class modelCheckerRunner extends Thread {
	private GsModelCheckerUI ui;
	private GsList l_tests;
	private GsRegulatoryMutants mutants;
	
	protected modelCheckerRunner(GsModelCheckerUI ui, GsList l_tests, GsRegulatoryMutants mutants) {
		this.mutants = mutants;
		this.ui = ui;
		this.l_tests = l_tests;
	}
	
	public void run() {
        File output;
		try {
			output = TempDir.createGeneratedName("GINsim-mcheck_", null);
	        for (int i=0 ; i<l_tests.getNbElements() ; i++) {
	            GsModelChecker checker = (GsModelChecker)l_tests.getElement(i);
	        	File odir = TempDir.createNamed(checker.getName(), output);
	            try {
					checker.run(mutants, ui, odir);
				} catch (InterruptedException e) {
					break;
				}
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (ui != null) {
			ui.endRun();
		}
	}
}