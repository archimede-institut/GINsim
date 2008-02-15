package fr.univmrs.tagc.common.document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import fr.univmrs.tagc.common.xml.XMLWriter;


public class OOoDocumentWriter extends DocumentWriter {

	File file;
	XMLWriter xmlw;
	ZipOutputStream zo;
	OutputStreamWriter writer;
	
	Map m_style = new HashMap();
	Vector v_table = new Vector();
	OOoTable curTable = null;
	
	public OOoDocumentWriter(File file) {
		this.file = file;
	}
	
	protected void startDocument() throws ZipException, IOException {
		zo = new ZipOutputStream(new FileOutputStream(file));
		writer = new OutputStreamWriter(zo, "UTF-8");
		
		zo.putNextEntry(new ZipEntry("META-INF/manifest.xml"));
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<manifest:manifest xmlns:manifest=\"urn:oasis:names:tc:opendocument:xmlns:manifest:1.0\">\n" +
				"<manifest:file-entry manifest:media-type=\"application/vnd.oasis.opendocument.text\" manifest:full-path=\"/\"/>\n" +
				"<manifest:file-entry manifest:media-type=\"text/xml\" manifest:full-path=\"content.xml\"/>\n" +
				"</manifest:manifest>");
		writer.flush();
		zo.closeEntry();
		
		zo.putNextEntry(new ZipEntry("mimetype"));
		writer.write("application/vnd.oasis.opendocument.text");
		writer.flush();
		zo.closeEntry();

		zo.putNextEntry(new ZipEntry("content.xml"));
		xmlw = new XMLWriter(writer, null);
		xmlw.openTag("office:document-content");
		xmlw.addAttr("xmlns:office", "urn:oasis:names:tc:opendocument:xmlns:office:1.0");
		xmlw.addAttr("xmlns:style","urn:oasis:names:tc:opendocument:xmlns:style:1.0");
		xmlw.addAttr("xmlns:text","urn:oasis:names:tc:opendocument:xmlns:text:1.0");
		xmlw.addAttr("xmlns:table","urn:oasis:names:tc:opendocument:xmlns:table:1.0");
		xmlw.addAttr("xmlns:draw","urn:oasis:names:tc:opendocument:xmlns:drawing:1.0" );
		xmlw.addAttr("xmlns:fo","urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0");
		xmlw.addAttr("xmlns:xlink","http://www.w3.org/1999/xlink");
		xmlw.addAttr("xmlns:dc","http://purl.org/dc/elements/1.1/");
		xmlw.addAttr("xmlns:meta","urn:oasis:names:tc:opendocument:xmlns:meta:1.0");
		xmlw.addAttr("xmlns:number","urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0");
		xmlw.addAttr("xmlns:svg","urn:oasis:names:tc:opendocument:xmlns:svgcompatible:1.0");
		xmlw.addAttr("xmlns:chart","urn:oasis:names:tc:opendocument:xmlns:chart:1.0");
		xmlw.addAttr("xmlns:dr3d","urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0");
		xmlw.addAttr("xmlns:math","http://www.w3.org/1998/Math/MathML");
		xmlw.addAttr("xmlns:form","urn:oasis:names:tc:opendocument:xmlns:form:1.0");
		xmlw.addAttr("xmlns:script","urn:oasis:names:tc:opendocument:xmlns:script:1.0");
		xmlw.addAttr("xmlns:ooo","http://openoffice.org/2004/office");
		xmlw.addAttr("xmlns:ooow","http://openoffice.org/2004/writer");
		xmlw.addAttr("xmlns:oooc","http://openoffice.org/2004/calc");
		xmlw.addAttr("xmlns:dom","http://www.w3.org/2001/xml-events");
		xmlw.addAttr("xmlns:xforms","http://www.w3.org/2002/xforms");
		xmlw.addAttr("xmlns:xsd","http://www.w3.org/2001/XMLSchema");
		xmlw.addAttr("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
		xmlw.addAttr("office:version","1.0");
		
		Iterator it;
		
		xmlw.openTag("office:scripts");
		xmlw.closeTag();
		
		xmlw.openTag("office:font-face-decls");
		xmlw.closeTag();
		
		xmlw.openTag("office:automatic-styles");
		it = m_style.keySet().iterator();
		while (it.hasNext()) {
			DocumentStyle style = (DocumentStyle)m_style.get(it.next());
			xmlw.openTag("style:style");
			xmlw.addAttr("style:name", style.name);
			xmlw.addAttr("style:family", style.familly);
		}
		xmlw.closeTag();
		
		xmlw.openTag("office:body");
		xmlw.openTag("office:text");
		
		xmlw.openTag("office:forms");
		xmlw.addAttr("form:automatic-focus","false");
		xmlw.addAttr("form:apply-design-mode","false");
		xmlw.closeTag();
		
		xmlw.openTag("text:sequence-decls");
		xmlw.closeTag();
	}
	
	protected void doOpenParagraph(String style) throws IOException {
		xmlw.openTag("text:p");
		xmlw.addAttr("text:style-name", style==null ? "Standard" : style);
	}
	
	protected void doWriteText(String text) throws IOException {
		xmlw.addContent(text);
	}
	
	protected void doOpenTable(String name, String style, String[] t_colStyle) throws IOException {
		curTable = new OOoTable();
		curTable.name = name;
		curTable.style = style;
		curTable.t_colStyle = t_colStyle;
		v_table.add(curTable);
		xmlw.openTag("table:table");
		xmlw.addAttr("table:name", name);
		xmlw.addAttr("table:style-name", style);
		for (int i=0 ; i<t_colStyle.length ; i++) {
			xmlw.openTag("table:table-column");
			xmlw.addAttr("table:style-name", t_colStyle[i]);
			xmlw.closeTag();
		}
	}
	
	protected void doCloseTable() throws IOException {
		xmlw.closeTag();
		v_table.remove(curTable);
		if (v_table.size() > 0) {
			curTable = (OOoTable)v_table.get(v_table.size()-1);
		} else {
			curTable = null;
		}
	}
	
	protected void doOpenTableRow() throws IOException {
		xmlw.openTag("table:table-row");
	}
	
	protected void doOpenTableCell(int colspan, int rowspan) throws IOException {
		// TODO: support rowspan
		xmlw.openTag("table:table-cell");
		if (colspan > 1) {
			xmlw.addAttr("table:number-columns-spanned", ""+colspan);
		}
	}
	
	protected void doCloseDocument() throws IOException {
		xmlw.closeTag(); // "office:text"
		xmlw.closeTag(); // "office:body"
		xmlw.closeTag(); // "office:document-content"
		writer.flush();
		zo.closeEntry();
		zo.close();
	}

	protected void doCloseParagraph() throws IOException {
		xmlw.closeTag();
	}

	protected void doCloseTableCell() throws IOException {
		xmlw.closeTag();
	}

	protected void doCloseTableRow() throws IOException {
		xmlw.closeTag();
	}
}

class OOoTable {
	String name;
	String style;
	String[] t_colStyle;
}