package fr.univmrs.ibdm.GINsim.annotation;

import fr.univmrs.ibdm.GINsim.graph.GsGraph;


public interface AnnotationHelper {

	public void update(AnnotationLink l, GsGraph graph);
	public void open(AnnotationLink l);
}
