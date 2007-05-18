package fr.univmrs.ibdm.GINsim.modelChecker;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.export.regulatoryGraph.GsSMVExport;
import fr.univmrs.ibdm.GINsim.export.regulatoryGraph.GsSMVExportConfigPanel;
import fr.univmrs.ibdm.GINsim.export.regulatoryGraph.GsSMVexportConfig;
import fr.univmrs.ibdm.GINsim.gui.GsValueList;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant.GsRegulatoryMutants;

/**
 * a model checker using NuSMV
 */
public class GsNuSMVChecker implements GsModelChecker {

	String name;
	GsRegulatoryGraph graph;
	Map m_info = new HashMap();
	GsSMVexportConfig cfg;
	
	static GsSMVExportConfigPanel editPanel = null;
	private static final String RUNCMD = "NuSMV -dynamic ";

	public GsNuSMVChecker(String name, GsRegulatoryGraph graph) {
		this.name = name;
		this.graph = graph;
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
		m.put("mode", (cfg.isSync()?"sync":"async"));
		String s = "";
		Map minit = cfg.getInitStates();
		Iterator it = minit.keySet().iterator();
		while (it.hasNext()) {
			Object key = it.next();
			s += key+":"+minit.get(key)+" ";
		}
		m.put("init", s);
		return m;
	}
	
	public void setName(String name) {
		this.name = name;
		;
	}

	public String toString() {
		return name;
	}

	public void run(GsRegulatoryMutants mutants, GsModelCheckerUI ui, File outputDir) throws InterruptedException {
		try {
			for (int i = -1; i < mutants.getNbElements(); i++) {
				Object m;
				if (i == -1) {
					m = "-";
				} else {
					m = mutants.getElement(i);
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
				} else if (o instanceof GsValueList) {
					result.expected = ((GsValueList) o).getSelectedIndex();
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
			o = new GsValueList(GsModelCheckerPlugin.v_values, 0);
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
				m_info.put(k, new GsValueList(GsModelCheckerPlugin.v_values,
						((GsModelCheckerTestResult) r).expected));
			}
		}
	}

	public Component getEditPanel() {
		if (editPanel == null) {
			editPanel = new GsSMVExportConfigPanel(false, true);
		}
		editPanel.setCfg(cfg);
		return editPanel;
	}

	public void setCfg(Map attr) {
		cfg.setTest((String)attr.get("test"));
		cfg.type = "sync".equals(attr.get("mode")) ? GsSMVexportConfig.CFG_SYNC : GsSMVexportConfig.CFG_ASYNC;
		Map minit = cfg.getInitStates();
		String[] ts = ((String)attr.get("init")).split(" ");
		Vector norder = graph.getNodeOrder();
		for (int i=0 ; i<ts.length ; i++) {
			String[] tval = ts[i].split(":");
			if (tval.length == 2) {
				for (int j=0 ; j<norder.size() ; j++) {
					GsRegulatoryVertex vertex = (GsRegulatoryVertex)norder.get(j);
					if (tval[0].equals(vertex.getId())) {
						short val = (short)Integer.parseInt(tval[1]);
						if (val >= 0 && val <= vertex.getMaxValue()) {
							minit.put(vertex, new Integer(val));
						}
					}
				}
			}
		}
	}
}