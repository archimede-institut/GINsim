package fr.univmrs.ibdm.GINsim.modelChecker;

import java.io.IOException;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;

/**
 * a model checker using NuSMV
 */
public class GsNuSMVCheckerDescr implements GsModelCheckerDescr {

	public final static String key = "NuSMV";
	
    public String getNonAvailableInfo() {
        return "Can not find NuSMV in your execution path, please install the NuSMV model checker and add it to the path if necessary";
    }

    public boolean isAvailable() {
        try {
            Runtime.getRuntime().exec("NuSMV -h");
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public String getName() {
        return key;
    }
    
    public String toString() {
        return "NuSMV model checker plugin";
    }
    
    public GsModelChecker createNew(String name, GsRegulatoryGraph graph) {
        return new GsNuSMVChecker(name, graph);
    }
}
