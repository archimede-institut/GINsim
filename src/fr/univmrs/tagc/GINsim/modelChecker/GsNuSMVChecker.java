package fr.univmrs.tagc.GINsim.modelChecker;

import java.awt.Component;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fr.univmrs.tagc.GINsim.export.regulatoryGraph.GsSMVExport;
import fr.univmrs.tagc.GINsim.export.regulatoryGraph.GsSMVExportConfigPanel;
import fr.univmrs.tagc.GINsim.export.regulatoryGraph.GsSMVexportConfig;
import fr.univmrs.tagc.GINsim.graph.GsExtensibleConfig;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialState;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStateList;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStateManager;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutants;
import fr.univmrs.tagc.common.datastore.ValueList;
import fr.univmrs.tagc.common.widgets.StackDialog;

/**
 * a model checker using NuSMV
 */
public class GsNuSMVChecker implements GsModelChecker {

	String name;
	GsRegulatoryGraph graph;
	Map m_info = new HashMap();
	GsSMVexportConfig cfg;
	GsInitialStateList initList;
	
	GsSMVExportConfigPanel editPanel = null;
	private static final String RUNCMD = "NuSMV -dynamic ";

	public GsNuSMVChecker(String name, GsRegulatoryGraph graph) {
		this.name = name;
		this.graph = graph;
	    this.initList = (GsInitialStateList)graph.getObject(GsInitialStateManager.key, true);
		this.cfg = new GsSMVexportConfig(graph);
	}
	
	public String getName() {
		return name;
	}

	public String getType() {
		return GsNuSMVCheckerDescr.key;
	}
	
	public Map getAttrList() {
		Map m = new HashMap();
		m.put("test", cfg.getTest());
		
		String s;
		switch (cfg.getType()) 
		{
		case GsSMVexportConfig.CFG_SYNC: 
			s = "sync";
			break;
		case GsSMVexportConfig.CFG_ASYNC: 
			s = "async";
			break;
		default: 
			s = "syncbis";
			break;
		}
		m.put("mode", s);
		Iterator it = cfg.getInitialState().keySet().iterator();
		if (it.hasNext()) {
			m.put("init", ((GsInitialState)it.next()).getName());
		}
		return m;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	public void run(GsRegulatoryMutants mutants, GsModelCheckerUI ui, File outputDir) throws InterruptedException {
		try {
			for (int i = -1; i < mutants.getNbElements(null); i++) {
				Object m;
				if (i == -1) {
					m = "-";
				} else {
					m = mutants.getElement(null, i);
				}
				File src = new File(outputDir, m+".in");

				if (m instanceof GsRegulatoryMutantDef) {
					cfg.mutant = (GsRegulatoryMutantDef)m;
				} else {
					cfg.mutant = null;
				}
				GsSMVExport.encode(graph, src.getAbsolutePath(), cfg);

				File output = new File(outputDir, m+".out");
				Object o = m_info.get(m);
				GsModelCheckerTestResult result = new GsModelCheckerTestResult();
				if (o == null) {
					result.expected = 0;
				} else if (o instanceof ValueList) {
					result.expected = ((ValueList) o).getSelectedIndex();
				} else {
					System.out.println("should not come here: result based on previous result");
					result.expected = ((GsModelCheckerTestResult) o).expected;
				}
				Process p = Runtime.getRuntime().exec(RUNCMD+src.getAbsolutePath());
				
				// get the output into a separate file
				final InputStream in = p.getInputStream();
				final OutputStream out = new FileOutputStream(output);

				int c;
				try {
					while ((c = in.read()) != -1) {
						out.write((char)c);
					}
					out.close();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
				p.waitFor();
				
				int rval = p.exitValue();
				if (rval == 0) {
					BufferedReader brd = new BufferedReader(new FileReader(output));
					String line;
					result.result = 2;
					while ((line = brd.readLine()) != null) {
						if (line.startsWith("-- specification ")) {
							if (line.endsWith(" is true")) {
								result.result = 1;
								break;
							}
						}
					}
					brd.close();
					result.output = output.getAbsolutePath();
				} else {
					result.result = -1;
					result.output = "NuSMV returned an error code: "+rval;
				}
				m_info.put(m, result);
				if (ui != null) {
					ui.updateResult(this, m);
				} else {
					System.out.println(result.output);
				}
			}
		} catch (IOException e) {
			return;
		}
	}

	public Object getInfo(Object mutant) {
		Object o = m_info.get(mutant);
		if (o == null) {
			o = new ValueList(GsModelCheckerPlugin.v_values, 0);
			m_info.put(mutant, o);
		}
		return o;
	}

	public Map getInfoMap() {
		return m_info;
	}
	
	public void delMutant(Object mutant) {
		m_info.remove(mutant);
	}

	public void cleanup() {
		Iterator it = m_info.keySet().iterator();
		while (it.hasNext()) {
			Object k = it.next();
			Object r = m_info.get(k);
			if (r instanceof GsModelCheckerTestResult) {
				m_info.put(k, new ValueList(GsModelCheckerPlugin.v_values,
						((GsModelCheckerTestResult) r).expected));
			}
		}
	}

	public Component getEditPanel(GsGraph graph, StackDialog dialog) {
		if (editPanel == null) {
			GsExtensibleConfig conf = new GsExtensibleConfig(graph);
			conf.setSpecificConfig(cfg);
			editPanel = new GsSMVExportConfigPanel(conf, dialog, false, true);
		}
		return editPanel;
	}

	public void setCfg(Map attr) {
		cfg.setTest((String)attr.get("test"));
		
		if ("sync".equals(attr.get("mode"))) {
			cfg.type = GsSMVexportConfig.CFG_SYNC;
		} else if ("async".equals(attr.get("mode"))) { 
			 cfg.type = GsSMVexportConfig.CFG_ASYNC;		
		} else { 
			cfg.type = GsSMVexportConfig.CFG_ASYNCBIS;
		}
		
        Map minit = cfg.getInitialState();
		String s_init = (String)attr.get("init");
		minit.put(initList.getInitState(s_init), null);
	}
}
