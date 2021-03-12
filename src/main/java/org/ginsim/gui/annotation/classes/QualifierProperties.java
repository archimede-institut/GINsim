package org.ginsim.gui.annotation.classes;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Contains the qualifier properties of a qualifier bloc (the String of the qualifier and the number of the alternative)
 *
 * @author Martin Boutroux
 */
class QualifierProperties {

	// variables
	private String qualifier;
	private AtomicInteger alternative;
	
	// constructors
	QualifierProperties(String newQualifier, int intAlternative) {
		qualifier = newQualifier;
		alternative = new AtomicInteger(intAlternative);
	}
	
	// getters
	String getQualifier() {
		return qualifier;
	}
	AtomicInteger getAlternative() {
		return alternative;
	}
	
	// setters
	void setAlternative(int intAlternative) {
		alternative.set(intAlternative);
	}
}
