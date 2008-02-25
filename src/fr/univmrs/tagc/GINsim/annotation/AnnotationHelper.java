package fr.univmrs.tagc.GINsim.annotation;

import fr.univmrs.tagc.GINsim.graph.GsGraph;


public interface AnnotationHelper {

	public void update(AnnotationLink l, GsGraph graph);
	public void open(AnnotationLink l);
	public String getLink(AnnotationLink l);
}
