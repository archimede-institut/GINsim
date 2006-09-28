package fr.univmrs.ibdm.GINsim.modelChecker;

import java.io.IOException;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;

/**
 * a model checker using NuSMV
 */
public class GsNuSMVCheckerDescr implements GsModelCheckerDescr {

    public String getNonAvailableInfo() {
        return "Can not find NuSMV in your execution path, please install the NuSMV model checker and add it to the path if necessary";
    }

    public boolean isAvailable() {
        try {
            // FIXME: dirty test for NuSMV
            Runtime.getRuntime().exec("NuSMV -h");
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public String getName() {
        return "NuSMV";
    }
    
    public String toString() {
        return "NuSMV";
    }
    
    public GsModelChecker createNew(String name, GsRegulatoryGraph graph) {
        return new GsNuSMVChecker(name, graph);
    }
}
