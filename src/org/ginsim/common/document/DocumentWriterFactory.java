package org.ginsim.common.document;

/**
 * A factory to create document writer instances.
 * Such factories are needed to abstract the instantiations of specific backends.
 *
 * @author Aurelien Naldi
 */
public interface DocumentWriterFactory {

	/**
	 * Create a document writer.
	 * 
	 * @return a new document writer instance.
	 */
	DocumentWriter getDocumentWriter();
}
