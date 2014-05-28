package org.ginsim.service.tool.modelreduction;

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
	ReductionConfig getReduction();
	
	/**
	 * Select a reduction.
	 * @param reduction
	 */
	void setReduction(ReductionConfig reduction);
}
