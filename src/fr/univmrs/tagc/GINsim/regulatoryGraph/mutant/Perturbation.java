package fr.univmrs.tagc.GINsim.regulatoryGraph.mutant;

import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;

import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;


public interface Perturbation {

    /**
     * apply this perturbation on the OMDD.
     * 
     * @param t_tree OMDD for all genes of the model
     * @param graph the regulatory graph
     */
    public void apply(OmddNode[] t_tree, GsRegulatoryGraph graph);

}