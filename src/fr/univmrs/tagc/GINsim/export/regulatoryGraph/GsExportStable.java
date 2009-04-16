package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;

import fr.univmrs.tagc.GINsim.export.GsExportConfig;
import fr.univmrs.tagc.GINsim.graph.GsExtensibleConfig;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialState;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStateList;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;
import fr.univmrs.tagc.GINsim.stableStates.GenericStableStateUI;
import fr.univmrs.tagc.GINsim.stableStates.GsSearchStableStates;
import fr.univmrs.tagc.GINsim.stableStates.StableTableModel;
import fr.univmrs.tagc.common.manageressources.Translator;
import fr.univmrs.tagc.common.widgets.EnhancedJTable;


public class GsExportStable extends JPanel implements GenericStableStateUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    JLabel  etatStable = null;
    StableTableModel tableModel = null;
    JButton calculer, formule, html = null;
    GsSMVexportConfig cfg;
    JTextArea text;
    GsInitialStateList initStates;
    GsInitialState name;
    GsExportConfig config;
    GsRegulatoryMutantDef mutant;
    
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
		// UI to test stable state reachability
        setLayout(new GridBagLayout()); 
        JScrollPane sp = new JScrollPane();
        tableModel = new StableTableModel(cfg.graph.getNodeOrder(), true);
        EnhancedJTable tableResult = new EnhancedJTable(tableModel);
        tableResult.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableResult.getTableHeader().setReorderingAllowed(false);
        sp.setViewportView(tableResult);
		GridBagConstraints cst = new GridBagConstraints();
        cst.gridx = 0;
        cst.gridy = 2;
        cst.gridwidth = 3;
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
	    formule = new JButton("Formule");
	    cst = new GridBagConstraints();
	    cst.gridx = 1;
	    cst.gridy = 3;
	    cst.anchor = GridBagConstraints.WEST;
	    add(formule,cst);
	    formule.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		switch(cfg.type) {
	    		case GsSMVexportConfig.CFG_ASYNC:
	    			
	    		StringBuffer s = new StringBuffer("");
	    		
	    		int i;
	    		List v_no = cfg.graph.getNodeOrder();
	    		short[] checked = tableModel.getCheckedRow();
	    		s.append("SPEC EF ( AG (");
	    		for ( i=0; i<checked.length-1 ;i++) {
		    		s.append(v_no.get(i));
		    		s.append(".level = ");
		    		s.append(checked[i]);
		    		s.append(" & ");
	    		}
	    		s.append(v_no.get(i));
	    		s.append(".level = ");
	    		s.append(checked[i]);
	    		s.append(") )");
	    		text.setText(s.toString());
	    	break;
	    	case GsSMVexportConfig.CFG_SYNC:
	    		s = new StringBuffer("");
		    	v_no = cfg.graph.getNodeOrder();
		    	checked = tableModel.getCheckedRow();
		    	s.append("SPEC EF ( AG (");
		    	for ( i=0; i<checked.length-1 ;i++) {
			    	s.append(v_no.get(i));
			    	s.append(" = ");
			    	s.append(checked[i]);
			    	s.append(" & ");
		    	}
		    	s.append(v_no.get(i));
		    	s.append(" = ");
		    	s.append(checked[i]);
		    	s.append(") )");
		    	text.setText(s.toString());
		    break;
	    	case GsSMVexportConfig.CFG_ASYNCBIS:
	    		s = new StringBuffer("");
		    	v_no = cfg.graph.getNodeOrder();
		    	checked = tableModel.getCheckedRow();
		    	s.append("SPEC EF ( AG (");
		    	for ( i=0; i<checked.length-1 ;i++) {
			    	s.append(v_no.get(i));
			    	s.append(" = ");
			    	s.append(checked[i]);
			    	s.append(" & ");
		    	}
		    	s.append(v_no.get(i));
		    	s.append(" = ");
		    	s.append(checked[i]);
		    	s.append(") )");
		    	text.setText(s.toString());
		    break;
	    	default:
	    		s = new StringBuffer("");
	    		v_no = cfg.graph.getNodeOrder();
	    		checked = tableModel.getCheckedRow();
	    		s.append("EF ( AG (");
	    		for ( i=0; i<checked.length-1 ;i++) {
	    			s.append(v_no.get(i));
	    			s.append(" == ");
	    			s.append(checked[i]);
	    			s.append(" & ");
	    		}
	    		s.append(v_no.get(i));
	    		s.append(" == ");
	    		s.append(checked[i]);
	    		s.append(") );");
	    		text.setText(s.toString());
	    	break;
	    		}
	    	}
	    	
	    });
	}
	
	protected void run() {
		new GsSearchStableStates(cfg.graph, cfg.mutant, this).start();
	}
	
	public void setResult(OmddNode stable) {
		tableModel.setResult(stable, config.getGraph());
	}
}


