package org.ginsim.common.document;

import java.util.ArrayList;
import java.util.List;

import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.core.utils.log.LogManager;


/**
 * This class contain the informations about each kind of document like id, extension, filter and its class.
 */
public class GenericDocumentFormat extends FileFormatDescription {
	
	public static final String GROUPKEY = "DOC"; 
	
	public static final GenericDocumentFormat XHTMLDocumentFormat = new GenericDocumentFormat(XHTMLDocumentWriter.class, "xHTML", "xhtml");
	public static final GenericDocumentFormat OOoDocumentFormat = new GenericDocumentFormat(OOoDocumentWriter.class, "OpenDocument", "odt");
	private static List<GenericDocumentFormat> formats = new ArrayList<GenericDocumentFormat>();
	static {
		formats.add(XHTMLDocumentFormat);
		formats.add(OOoDocumentFormat);
        //FIXME: WIKI and LaTeX formats are disabled
        //formats.add(new GenericDocumentFormat(WikiDocumentWriter.class, "Wiki", "Text files (.txt)", "txt", new String[] {"txt"}));
        // formats.add(new GenericDocumentFormat(LaTeXDocumentWriter.class, "LaTeX", "LaTeX files (.tex)", "tex", new String[] {"tex"})); 			
	}
	
	/**
	 * The documentWriter to instanciates to write a document of this type (eg. XHTMLDocumentWriter)
	 */
	public final Class<? extends DocumentWriter> documentWriterClass;
		
	/**
	 * Define a new generic document format.
	 * @param documentWriterClass : The DocumentWriter sub-class for the format
	 * @param id : The name of the format (for the dropdown menu)
	 * @param extension : the extension to add to the exported file
	 */
	public GenericDocumentFormat(Class documentWriterClass, String id, String extension) {
		super(GROUPKEY, id, id, extension);
		this.documentWriterClass = documentWriterClass;
	}
	
	public DocumentWriter getWriter() {
		if (documentWriterClass == null) {
			return null;
		}
		
		try {
			return documentWriterClass.newInstance();
		} catch (Exception e) {
			LogManager.error("Could not create Document Writer: "+ documentWriterClass);
			LogManager.error(e);
			return null;
		}
	}
	
	public static List<GenericDocumentFormat> getAllFormats() {
		return formats;
	}
	
	@Override
	public String toString() {
		return id;
	}
}
