package org.ginsim.service.export.cadp;

/**
 * 
 * Class generating the dynamic README file for the CADP export bundle
 * 
 * @author Nuno D. Mendes
 * 
 */
public class CADPReadMeWriter extends CADPWriter {

	public CADPReadMeWriter(CADPExportConfig config) {
		super(config);
	}

	public String toString() {

		String out = "";

		out += "*** README ***\n\n";
		out += "\nThis file contains the intructions to invoke CADP to obtain the minimised composed LTS for the current composition and subsequently test the reachability of each stable state of the composed model.\n";
		
		out += "\n\nIn this bundle you will find the following files:\n";
		out += "\t";
		out += "\t";
		// ...
		
		out += "\n\n";
		
		out += "To calculate the minimised composed LTS it suffices to invoke:\n";
		out += "\t\t svl \n\n";
		
		// ...
		

		
		// out to test reachability
		// list stable states

		return out;

	}

}
