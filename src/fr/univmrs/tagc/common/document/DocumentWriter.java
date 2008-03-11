package fr.univmrs.tagc.common.document;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

/**
 * 
 * 
 * <b>DocumentProperties :</b>
 * <p>The protected instance variable documentProperties is a Map of properties (meta) that contains some informations about the document such as the title, the authors...<br>
 * There is a list of class constants beginning with META_* for commonly used properties</p>
 * <p>There is 3 methods to manipulate documentProperties : </p>
 * <ul>
 * 	<li>setDocumentProperty(Object name, Object value) to define a new property</li>
 * 	<li>setDocumentProperties(Map map) to define multiple properties</li>
 * 	<li>setDocumentProperties(Object[] table) to define multiple properties</li>
 * </ul>
 * 
 * 
 * <b>DocumentExtras :</b>
 * <p>DocumentExtra is a concept to allow to define content for a specific type of document.<br>
 * A documentExtra is a StringBuffer containing the content.<br>
 * documentExtras is a map of documentExtra with a key representing the id of the extra</p>
 * <p>When you implement a DocumentWriter subclass, you should register for the extra you are able to use</p>
 * <p>When you use a DocumentWriter subclass, if you want to add specific content, first test if the document support the extra, if it does get the documentExtra and append it your content.</p>
 * <p>There is 3 methods to manipulate documentExtras : </p>
 * <ul>
 * 	<li>registerForDocumentExtra(String extra) register the document for an extra</li>
 * 	<li>getDocumentExtra(String extra) to get an extra</li>
 * 	<li>doesDocumentSupportExtra(String extra) to know if the current document support the extra</li>
 * </ul>
 * 
 * 
 * <b>DocumentStyles :</b>
 * <p>Allow you to set the style for the document.</p>
 * <p>There is 2 methods to manipulate documentStyles : </p>
 * <ul>
 * 	<li>getStyles() get the styles of the document</li>
 * 	<li>setStyles(DocumentStyle newStyle) define anotherStyle for the document</li>
 * </ul>
 * @see fr.univmrs.tagc.common.document.DocumentStyle;
 * 
 * @author Naldi Aurelien, Berenguier Duncan
 *
 */
public abstract class DocumentWriter {

	public static final int POS_OUT = 0;
	public static final int POS_PARAGRAPH = 1;
	public static final int POS_TABLE = 10;
	public static final int POS_TABLE_ROW = 11;
	public static final int POS_TABLE_CELL = 12;

	public static final String META_TITLE = "title";
	public static final String META_AUTHOR = "author";
	public static final String META_DATE = "date";
	public static final String META_KEYWORDS = "keywords";
	public static final String META_DESCRIPTION = "description";
	public static final String META_GENERATOR = "generator";

	DocumentPos pos = new DocumentPos();
	protected Map documentProperties; //The metadata of the document
	private Map documentExtras; //A list of all the extra (StringBuffer) the document can use.
	protected DocumentStyle documentStyles = null; //The style of the document.
	
	public DocumentWriter() {
		documentProperties = new Hashtable();
		documentExtras = new Hashtable();
	}
	
	protected abstract void startDocument() throws IOException;

	public void newParagraph() throws IOException {
		newParagraph(null);
	}
	public void newParagraph(String style) throws IOException {
		if (pos.pos == POS_PARAGRAPH) {
			closeParagraph();
		}
		doOpenParagraph(style);
		pos = new DocumentPos(pos, POS_PARAGRAPH);
	}
	public void closeParagraph() throws IOException {
		if (pos.pos != POS_PARAGRAPH) {
			// FIXME: error
			// throw new Exception("incoherent position");
		}
		doCloseParagraph();
		pos = pos.parent;
	}

	public void openTable(String name, String style, String[] t_colStyle) throws IOException {
		while (pos.pos == POS_PARAGRAPH) {
			closeParagraph();
		}
		doOpenTable(name, style, t_colStyle);
		pos = new DocumentPos(pos, POS_TABLE);
	}
	public void openTableCell(int colspan, int rowspan, String content) throws IOException {
		if (pos.pos == POS_TABLE) {
			openTableRow();
		} else if (pos.pos != POS_TABLE_ROW) {
			closeTableCell();
		}
		doOpenTableCell(colspan, rowspan);
		pos = new DocumentPos(pos, POS_TABLE_CELL);
		if (content != null) {
			writeText(content);
		}
	}
	public void openTableRow() throws IOException {
		if (pos.pos != POS_TABLE) {
			closeTableRow();
		}
		doOpenTableRow();
		pos = new DocumentPos(pos, POS_TABLE_ROW);
	}

	public void closeTableCell() throws IOException {
		while (pos.pos != POS_TABLE_CELL) {
			switch (pos.pos) {
				case POS_PARAGRAPH:
					closeParagraph();
					break;
				case POS_TABLE:
				case POS_TABLE_ROW:
					closeTable();
					break;
				default:
					// FIXME: error
					// throw new Exception("incoherent position");
			}
			
		}
		doCloseTableCell();
		pos = pos.parent;
	}
	public void closeTableRow() throws IOException {
		if (pos.pos != POS_TABLE_ROW) {
			closeTableCell();
		}
		doCloseTableRow();
		pos = pos.parent;
	}
	public void closeTable() throws IOException {
		if (pos.pos != POS_TABLE) {
			closeTableRow();
		}
		doCloseTable();
		pos = pos.parent;
	}
	
	public void openTableCell(String content) throws IOException {
		openTableCell(1,1, content);
	}
	
	public void writeText(String text) throws IOException {
		if (pos.pos != POS_PARAGRAPH) {
			newParagraph(null);
		}
		doWriteText(text);
	}
	
	public void addTableRow(String[] t_content) throws IOException {
		openTableRow();
		for (int i=0 ; i<t_content.length ; i++) {
			openTableCell(1, 1, t_content[i]);
		}
	}
	
	public void close() throws IOException {
		while (pos.pos != POS_OUT) {
			switch (pos.pos) {
				case POS_PARAGRAPH:
					closeParagraph();
					break;
				case POS_TABLE:
				case POS_TABLE_CELL:
				case POS_TABLE_ROW:
					closeTable();
					break;
			}
		}
		doCloseDocument();
	}
	
	protected abstract void doOpenParagraph(String style) throws IOException;
	protected abstract void doCloseParagraph() throws IOException;
	protected abstract void doWriteText(String text) throws IOException;
	protected abstract void doOpenTable(String name, String style,
			String[] t_colStyle) throws IOException;
	protected abstract void doCloseTable() throws IOException;
	protected abstract void doCloseTableRow() throws IOException;
	protected abstract void doCloseTableCell() throws IOException;
	protected abstract void doOpenTableRow() throws IOException;
	protected abstract void doOpenTableCell(int colspan, int rowspan) throws IOException;
	protected abstract void doCloseDocument() throws IOException;
	
	
	/**
	 * Set a property for the document.
	 * @param name : the name of the property (use 'META' constant if possible)
	 * @param value : the value of the property
	 */
	public void setDocumentProperty(Object name, Object value) {
		documentProperties.put(name, value);
	}
	/**
	 * Add a map of properties to the document.
	 * @param map : the map of properties
	 */
	public void setDocumentProperties(Map map) {
		documentProperties.putAll(map);
	}
	/**
	 * Add an array of properties to the document
	 * @param table : A table that alternate the name then the value of properties
	 * @throws ArrayIndexOutOfBoundsException if there is not the same number of name and values.
	 */
	public void setDocumentProperties(Object[] table) throws ArrayIndexOutOfBoundsException {
		if (table.length%2 == 1) {
			throw new ArrayIndexOutOfBoundsException();
		}
    	for (int i=0 ; i< table.length ; i+=2) {
    		documentProperties.put(table[i], table[i+1]);
    	}
	}
	
	/**
	 * Register the current document for extra (initialize the StringBuffer)
	 * @param extra : The name of the extra to create
	 */
	protected void registerForDocumentExtra(String extra) {
		documentExtras.put("javascript", new StringBuffer());
	}
	/**
	 * Get a documentExtra
	 * @param extra : The name of the extra to get
	 * @return the document extra
	 */
	protected StringBuffer getDocumentExtra(String extra) {
		return (StringBuffer)documentExtras.get(extra);
	}
	/**
	 * Indicate if the current document support a specific extra
	 * @param extra : The name of the extra
	 * @return true if the document support the specified extra
	 */
	public boolean doesDocumentSupportExtra(String extra) {
		return documentExtras.containsKey(extra);
	}

	/**
	 * Get the styles of the document, and initialize them if needed.
	 * @return the document style
	 */
	public DocumentStyle getStyles() {
		if (documentStyles == null) {
			documentStyles = new DocumentStyle();
		}
		return documentStyles;
	}
	/**
	 * Define a new DocumentStyle for the document
	 * @param newStyle : the new Style
	 * @return the old style
	 */
	public DocumentStyle setStyles(DocumentStyle newStyle) {
		DocumentStyle oldStyles = documentStyles;
		documentStyles = newStyle;
		return oldStyles;
	}
}

class DocumentPos {
	
	public DocumentPos parent;
	public Object data;
	public int pos;
	
	public DocumentPos() {
		this(null, DocumentWriter.POS_OUT, null);
	}
	
	public DocumentPos(DocumentPos parent, int pos) {
		this(parent, pos, null);
	}
	
	public DocumentPos(DocumentPos parent, int pos, Object data) {
		this.parent = parent;
		this.data = data;
		this.pos = pos;
	}
}
