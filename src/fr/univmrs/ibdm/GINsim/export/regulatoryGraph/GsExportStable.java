package fr.univmrs.ibdm.GINsim.export.regulatoryGraph;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import fr.univmrs.ibdm.GINsim.graph.GsExtensibleConfig;
import fr.univmrs.ibdm.GINsim.gui.GsJTable;
import fr.univmrs.ibdm.GINsim.gui.GsStackDialog;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.ibdm.GINsim.stableStates.GenericStableStateUI;
import fr.univmrs.ibdm.GINsim.stableStates.GsSearchStableStates;
import fr.univmrs.ibdm.GINsim.stableStates.StableTableModel;

public class GsExportStable extends JPanel implements GenericStableStateUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    JLabel  etatStable = null;
    StableTableModel tableModel = null;
    JButton calculer, sauvegarder = null;
    private GsSMVexportConfig cfg;
    private JTextArea text;

	public GsExportStable (GsExtensibleConfig config, JTextArea text){
		super();
		if (config.getSpecificConfig() == null) {
			config.setSpecificConfig(new GsSMVexportConfig((GsRegulatoryGraph)config.getGraph()));
		}
		this.text = text;
		this.cfg = (GsSMVexportConfig)config.getSpecificConfig();
    	initialize();
		
		
	}


	public void initialize(){
	 // création d'une interface pour tester l'atteignabilité d'un état stable
        setLayout(new GridBagLayout()); 

//        GsInitialStatePanel init = new GsInitialStatePanel(dialog, cfg.graph, false);
//        init.setParam(cfg);
//        GridBagConstraints cst = new GridBagConstraints();
//        cst.gridx = 0;
//        cst.gridy = 0;
//        cst.gridwidth = 2;
//        cst.weightx = 1;
//        cst.weighty = 1;
//        cst.fill = GridBagConstraints.BOTH;
//        add(init,cst);
//        
//        etatStable = new JLabel("Calculez un état stable :");
//        cst = new GridBagConstraints();
//        cst.gridx = 0;
//        cst.gridy = 1;
//        cst.gridwidth = 2;
//        cst.anchor = GridBagConstraints.WEST;
//        add(etatStable,cst);
        JScrollPane sp = new JScrollPane();
        tableModel = new StableTableModel(cfg.graph.getNodeOrder(), true);
        GsJTable tableResult = new GsJTable(tableModel);
        tableResult.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableResult.getTableHeader().setReorderingAllowed(false);
        sp.setViewportView(tableResult);
        System.out.println(tableResult);
		GridBagConstraints cst = new GridBagConstraints();
        cst.gridx = 0;
        cst.gridy = 2;
        cst.gridwidth = 2;
        cst.weightx = 1;
        cst.weighty = 1;
        cst.fill = GridBagConstraints.BOTH;
		add(sp,cst);
        
		calculer = new JButton(Translator.getString("STR_run"));
	    cst = new GridBagConstraints();
	    cst.gridx = 0;
	    cst.gridy = 3;
	    cst.anchor = GridBagConstraints.WEST;
	    add(calculer,cst);
	    calculer.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
			run();
			}
	    });
	    sauvegarder = new JButton(Translator.getString("STR_save"));
	    cst = new GridBagConstraints();
	    cst.gridx = 1;
	    cst.gridy = 3;
	    cst.anchor = GridBagConstraints.WEST;
	    add(sauvegarder,cst);
	    sauvegarder.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		text.setText("formule CTL");
	    	}
	    });
	    
	}
	
	protected void run() {
		new GsSearchStableStates(cfg.graph, cfg.mutant, this).start();
	}
	
	public void setResult(OmddNode stable) {
		tableModel.setResult(stable);
	}
}


