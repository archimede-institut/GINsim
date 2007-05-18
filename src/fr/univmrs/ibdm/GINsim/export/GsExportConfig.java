package fr.univmrs.ibdm.GINsim.export;

import java.util.Vector;

import fr.univmrs.ibdm.GINsim.global.GsOptions;
import fr.univmrs.ibdm.GINsim.graph.GsExtensibleConfig;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;

public class GsExportConfig extends GsExtensibleConfig {

	int ref;
	int format = -1;
	String filename;
	
	public GsExportConfig(GsGraph graph, GsAbstractExport export, int ref) {
		super(graph);
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

	public int gerRef() {
		return ref;
	}
	public String getFilename() {
		return filename;
	}
}
