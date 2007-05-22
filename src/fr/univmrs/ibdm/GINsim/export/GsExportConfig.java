package fr.univmrs.ibdm.GINsim.export;

import java.util.Vector;

import fr.univmrs.ibdm.GINsim.global.GsOptions;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;

public class GsExportConfig {

	GsGraph graph;
	int ref;
	int format = -1;
	Object specificConfig = null;
	String filename;
	
	public GsExportConfig(GsGraph graph, GsAbstractExport export, int ref) {
		super();
		this.graph = graph;
		this.ref = ref;

		// set the format
		Vector v_format = export.getSubFormat();
		if (v_format != null) {
			String s_format = (String)GsOptions.getOption("export."+export.getID()+".format", 
					v_format.get(0).toString());
			format = 0;
			for (int i=0 ; i<v_format.size() ; i++) {
				if (s_format.equals(v_format.get(i).toString())) {
					format = i;
					break;
				}
			}
			setFormat(format, export);
		}
	}
		
	public void setFormat(int index, GsAbstractExport export) {
		Vector v_format = export.getSubFormat();
		if (v_format == null) {
			return;
		}
		if (index >-1 && index < v_format.size()) {
			GsOptions.setOption("export."+export.getID()+".format", v_format.get(index).toString());
			format = index;
		}
	}

	public Object getSpecificConfig() {
		return specificConfig;
	}
	
	public void setSpecificConfig(Object specificConfig) {
		this.specificConfig = specificConfig;
	}
	
	public GsGraph getGraph() {
		return graph;
	}
	public int gerRef() {
		return ref;
	}
	public String getFilename() {
		return filename;
	}
}
