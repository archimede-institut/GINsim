package org.ginsim.core.graph.common;

/**
 * Factory to open or create specialised gryph instances.
 * 
 * @author Aurelien Naldi
 *
 * @param <G>
 */
public interface GraphFactory<G extends Graph<?,?>> {
	
    /**
     * Return the class of graph this factory is managing
     * 
     * @return the name of the class of graph this factory is managing
     */
	public Class<G> getGraphClass();
	
    /**
     * Return the type of graph this factory is managing
     * 
     * @return the name of the type of graph this factory is managing
     */
	public String getGraphType();
	
	/**
	 * Return the class of the parser to use to read from file the type
	 * of graph the factory manager
	 * 
	 * @return the class of the parser to use with this factory
	 */
	public Class getParser();
	
    /**
     * Create a new graph of the type factory is managing
     * 
     * @return an instance of the graph type the factory is managing
     */
	public G create();

}
