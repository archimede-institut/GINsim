package org.ginsim.common.document;

import java.util.ArrayList;
import java.util.List;

import org.ginsim.common.utils.FileFormatDescription;

/**
 * This class contain the informations about each kind of document like id, extension, filter and its class.
 * 
 * @author Aurelien Naldi
 * @author Duncan Berenguier
 */
public class GenericDocumentFormat extends FileFormatDescription {
	
	public static final GenericDocumentFormat XHTMLDocumentFormat = new GenericDocumentFormat(XHTMLDocumentWriter.FACTORY, "xHTML", "xhtml");
	public static final GenericDocumentFormat OOoDocumentFormat = new GenericDocumentFormat(OOoDocumentWriter.FACTORY, "OpenDocument", "odt");
	private static List<GenericDocumentFormat> formats = new ArrayList<GenericDocumentFormat>();
	static {
		formats.add(XHTMLDocumentFormat);
		formats.add(OOoDocumentFormat);
        //FIXME: WIKI and LaTeX formats are disabled
        //formats.add(new GenericDocumentFormat(WikiDocumentWriter.class, "Wiki", "Text files (.txt)", "txt", new String[] {"txt"}));
        // formats.add(new GenericDocumentFormat(LaTeXDocumentWriter.class, "LaTeX", "LaTeX files (.tex)", "tex", new String[] {"tex"})); 			
	}
	
	/**
	 * The documentWriter to instanciate to write a document of this type (eg. XHTMLDocumentWriter)
	 */
	public final DocumentWriterFactory factory;
		
	/**
	 * Define a new generic document format.
	 * @param documentWriterClass : The DocumentWriter sub-class for the format
	 * @param id : The name of the format (for the dropdown menu)
	 * @param extension : the extension to add to the exported file
	 */
	public GenericDocumentFormat(DocumentWriterFactory factory, String id, String extension) {
		super( id, extension);
		this.factory = factory;
	}
	
	public DocumentWriter getWriter() {
		return factory.getDocumentWriter();
	}
	
	public static List<GenericDocumentFormat> getAllFormats() {
		return formats;
	}
	
	@Override
	public String toString() {
		return id;
	}
}
