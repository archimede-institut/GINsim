package org.ginsim.service.tool.modelsimplifier;

/**
 * An interface for configuration objects which store a selected reduction.
 * 
 * @author Aurelien Naldi
 *
 */
public interface ReductionHolder {

	/**
	 * Retrieve the stored reduction.
	 * 
	 * @return the selected reduction
	 */
	ModelSimplifierConfig getReduction();
	
	/**
	 * Select a reduction.
	 * @param reduction
	 */
	void setReduction(ModelSimplifierConfig reduction);
}
