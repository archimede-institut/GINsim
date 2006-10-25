package fr.univmrs.ibdm.GINsim.modelChecker;

import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fr.univmrs.ibdm.GINsim.export.GsSMVExport;
import fr.univmrs.ibdm.GINsim.export.GsSMVExportConfigPanel;
import fr.univmrs.ibdm.GINsim.export.GsSMVexportConfig;
import fr.univmrs.ibdm.GINsim.gui.GsValueList;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMutants;

/**
 * a model checker using NuSMV
 */
public class GsNuSMVChecker implements GsModelChecker {

	String name;
	GsRegulatoryGraph graph;
	Map m_info = new HashMap();
	GsSMVexportConfig cfg;
	
	static GsSMVExportConfigPanel editPanel = null;

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
		// TODO: save init state
		return m;
	}
	
	public void setName(String name) {
		this.name = name;
		;
	}

	public String toString() {
		return name;
	}

	public boolean[] run(GsRegulatoryMutants mutants, File outputDir) {
		boolean[] ret = new boolean[mutants.getNbElements() + 1];
		try {
			for (int i = -1; i < mutants.getNbElements(); i++) {
				Object m;
				if (i == -1) {
					m = "-";
				} else {
					m = mutants.getElement(i);
				}
				File src = File.createTempFile("mchecker_", ".in", outputDir);

				// TODO: this is ugly
				GsSMVexportConfig cfg = new GsSMVexportConfig(graph);
				GsSMVExport.encode(graph, src.getAbsolutePath(), cfg);

				File output = File.createTempFile("mchecker_", ".out", outputDir);
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

				//String[] ts = {"/bin/sh", "-c", "NuSMV " + src.getAbsolutePath()+" > "+output.getAbsolutePath()};
				Process p = Runtime.getRuntime().exec("NuSMV "+src.getAbsolutePath());

				// get the output into a separate file
				final InputStream in = p.getInputStream();
				final OutputStream out = new FileOutputStream(output);

				int c;
				try {
					while ((c = in.read()) != -1) {
						out.write((char)c);
					}
					out.close();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				// TODO: really run a NuSMV test

				result.result = 2;
				result.output = "test did _NOT_ really run";
				m_info.put(m, result);
			}
		} catch (IOException e) {
			return null;
		}
		return ret;
	}

	public Object getInfo(Object mutant) {
		Object o = m_info.get(mutant);
		if (o == null) {
			o = new GsValueList(GsModelCheckerPlugin.v_values, 0);
			m_info.put(mutant, o);
		}
		return o;
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
}