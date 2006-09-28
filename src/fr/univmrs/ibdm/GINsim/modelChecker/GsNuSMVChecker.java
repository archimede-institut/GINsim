package fr.univmrs.ibdm.GINsim.modelChecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMutants;

/**
 * a model checker using NuSMV
 */
public class GsNuSMVChecker implements GsModelChecker {

    String name;
    String thetest;
    GsRegulatoryGraph graph;
    
    public GsNuSMVChecker(String name, GsRegulatoryGraph graph) {
        this.name = name;
        this.graph = graph;
    }


    public String getName() {
        return name;
    }
    
    public String toString() {
        return name;
    }
    
    public boolean[] run(GsRegulatoryMutants mutants) {
        // TODO: really run the test, on a list of mutants!
        boolean[] ret = new boolean[mutants.getNbElements()+1];
        try {
            Process p = Runtime.getRuntime().exec("NuSMV -h");
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            System.out.println("Here is the standard output of the command:\n");
//            String s;
//            while ((s = stdInput.readLine()) != null) {
//                System.out.println(s);
//            };
//            
//            stdInput = new BufferedReader(new InputStreamReader(p.getErrorStream()));
//            System.out.println("Here is the standard error of the command:\n");
//            while ((s = stdInput.readLine()) != null) {
//                System.out.println(s);
//            };
        } catch (IOException e) {
            return null;
        }
        return ret;
    }


    public void edit() {
        // TODO edit MC test
    }
}
