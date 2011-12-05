package org.ginsim.common.document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.ginsim.gui.shell.GsFileFilter;


/**
 * This class contain the informations about each kind of document like id, extension, filter and its class.
 */
public class GenericDocumentFormat {
	
	public static final GenericDocumentFormat XHTMLDocumentFormat = new GenericDocumentFormat(XHTMLDocumentWriter.class, "xHTML", new String[] {"html", "xhtml"}, "xHTML files (.html, .xhtml)");
	public static final GenericDocumentFormat OOoDocumentFormat = new GenericDocumentFormat(OOoDocumentWriter.class, "OpenDocument", new String[] {"odt"}, "OpenDocument Text files (.odt)");
	private static List<GenericDocumentFormat> formats = new ArrayList<GenericDocumentFormat>();
	static {
		formats.add(XHTMLDocumentFormat);
		formats.add(OOoDocumentFormat);
        //FIXME: WIKI and LaTeX formats are disabled
        //formats.add(new GenericDocumentFormat(WikiDocumentWriter.class, "Wiki", "Text files (.txt)", "txt", new String[] {"txt"}));
        // formats.add(new GenericDocumentFormat(LaTeXDocumentWriter.class, "LaTeX", "LaTeX files (.tex)", "tex", new String[] {"tex"})); 			
	}
	
	/**
	 * The identifier of the document (eg. xHTML)
	 */
	public final String id;
	/**
	 * The descritpion of the document (eg. xHTML files)
	 */
	public final GsFileFilter ffilter;
	public final String defaultExtension;
	/**
	 * The documentWriter to instanciates to write a document of this type (eg. XHTMLDocumentWriter)
	 */
	public final Class documentWriterClass;
		
	/**
	 * Define a new generic document format.
	 * @param documentWriterClass : The DocumentWriter sub-class for the format
	 * @param id : The name of the format (for the dropdown menu)
	 * @param filter : an array of filter for the file extension the format can overwrite
	 * @param fillterDescr : a description
	 * @param extension : the extension to add to the exported file
	 */
	public GenericDocumentFormat(Class documentWriterClass, String id, String[] extensionArray, String filterDescr) {
		this.documentWriterClass = documentWriterClass;
		this.id = id;
		this.ffilter = new GsFileFilter(extensionArray, filterDescr);
		this.defaultExtension = extensionArray[0];
	}
	
	public static GenericDocumentFormat getFormatById(String id) {
		for (Iterator iterator = formats.iterator(); iterator.hasNext();) {
			GenericDocumentFormat format = (GenericDocumentFormat) iterator.next();
			if (format.id.equals(id)) return format;
		}
		return null;
	}
	
	public static GenericDocumentFormat getFormatByExtension(String defaultExtension) {
		for (Iterator iterator = formats.iterator(); iterator.hasNext();) {
			GenericDocumentFormat format = (GenericDocumentFormat) iterator.next();
			if (format.defaultExtension.equals(defaultExtension)) return format;
		}
		return null;
	}
	
	public static List<GenericDocumentFormat> getAllFormats() {
		return formats;
	}
	
	public static String[] getFilterOfAllExtensions() {
		String[] s = new String[formats.size()];
		int i = 0;
		for (Iterator iterator = formats.iterator(); iterator.hasNext();) {
			GenericDocumentFormat format = (GenericDocumentFormat) iterator.next();
			s[i++] = "."+format.defaultExtension;
		}
		return s;
	}
	
	public static Vector getAllExtensionsNames() {
		Vector v = new Vector(formats.size());
		for (Iterator iterator = formats.iterator(); iterator.hasNext();) {
			GenericDocumentFormat format = (GenericDocumentFormat) iterator.next();
			v.add(format.defaultExtension);
		}
		return v;
	}
}
