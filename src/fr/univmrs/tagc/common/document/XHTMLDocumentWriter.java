package fr.univmrs.tagc.common.document;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import fr.univmrs.tagc.common.xml.XMLWriter;
import fr.univmrs.tagc.common.Tools;


public class XHTMLDocumentWriter extends DocumentWriter {

	File file;
	XMLWriter xmlw;
	OutputStreamWriter writer;
	
	Map m_style = new HashMap();
	Vector v_table = new Vector();
	xHTMLTable curTable = null;
		
	public XHTMLDocumentWriter(File file) {
		this.file = file;
		registerForDocumentExtra("javascript");
	}
	
	protected void startDocument() throws IOException {
		writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
		xmlw = new XMLWriter(writer, null);
	
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
					+"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"\n"
					+"   \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n");
		writer.flush();
		
		xmlw.openTag("html");
		xmlw.addAttr("xmlns", "http://www.w3.org/1999/xhtml");
		xmlw.addAttr("xml:lang", "en");
		
		xmlw.openTag("head");
		if (documentProperties.containsKey(META_TITLE)) {
			xmlw.addTagWithContent("title", (String)documentProperties.get(META_TITLE));
		}
		doCreateMeta("author", META_AUTHOR);
		doCreateMeta("created-at", META_DATE);
		doCreateMeta("keywords", META_KEYWORDS);
		doCreateMeta("description", META_DESCRIPTION);
		doCreateMeta("generator", META_GENERATOR);
		
		doWriteStyles();
		
		StringBuffer javascript = getDocumentExtra("javascript");
		if (javascript.length() > 0) {
			xmlw.openTag("script");
			xmlw.addAttr("type", "text/javascript");
			xmlw.addContent(javascript.toString());
			xmlw.closeTag();
		}
		
		xmlw.closeTag();//head

		xmlw.openTag("body");
	}
	
	protected void doOpenParagraph(String style) throws IOException {
		xmlw.openTag("p");
		if (style != null) {
			xmlw.addAttr("style", style);
		}
	}
	
	protected void doWriteText(String text) throws IOException {
		xmlw.addContent(text);
	}
	
	protected void doOpenTable(String name, String style, String[] t_colStyle) throws IOException {
		curTable = new xHTMLTable(name, style, t_colStyle);
		v_table.add(curTable);
		xmlw.openTag("table");
		xmlw.addAttr("name", name);
		xmlw.addAttr("class", style);
	}
	
	protected void doCloseTable() throws IOException {
		xmlw.closeTag();
		v_table.remove(curTable);
		if (v_table.size() > 0) {
			curTable = (xHTMLTable)v_table.get(v_table.size()-1);
		} else {
			curTable = null;
		}
	}
	
	protected void doOpenTableRow() throws IOException {
		xmlw.openTag("tr");
		curTable.row++;
	}
	
	protected void doOpenTableCell(int colspan, int rowspan) throws IOException {
		if (curTable.row == 1) {
			xmlw.openTag("th");
			if (curTable.t_colStyle != null && curTable.t_colStyle[curTable.col] != null) {
				xmlw.addAttr("class", curTable.t_colStyle[curTable.col++]);
			}
		} else {
			xmlw.openTag("td");			
		}
		if (colspan > 1) {
			xmlw.addAttr("colspan", ""+colspan);
		}
		if (rowspan > 1) {
			xmlw.addAttr("rowspan", ""+rowspan);
		}
	}
	
	protected void doCloseDocument() throws IOException {
		xmlw.closeTag(); // body
		xmlw.closeTag(); // html
		writer.flush();
		writer.close();
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
		
	protected void doCreateMeta(String meta, String key) throws IOException {
		if (documentProperties.containsKey(key)) {
			xmlw.openTag("meta");
			xmlw.addAttr("name", meta);
			xmlw.addAttr("content", (String)documentProperties.get(key));
			xmlw.closeTag();
		}
	}
	
	protected void doWriteStyles() throws IOException {
		if (documentStyles != null) {
			xmlw.openTag("style");
			xmlw.addAttr("type", "text/css");
			Iterator styleIterator = documentStyles.getStyleIterator();
			while (styleIterator.hasNext()) {
				String style = (String) styleIterator.next();
				StringBuffer buf = new StringBuffer("\n.");
				buf.append(style);
				buf.append('{');
				Map properties = documentStyles.getPropertiesForStyle(style);
				Iterator propertiesIterator = documentStyles.getPropertiesIteratorForStyle(style);
				while (propertiesIterator.hasNext()) {
					String property = (String) propertiesIterator.next();
					buf.append(property);
					buf.append(':');
					buf.append(getStyleValue(property, properties.get(property)));
					buf.append(';');
				}
				buf.append("}\n");
				xmlw.addContent(buf.toString());
			}
			xmlw.closeTag();	
		}
	}

	private String getStyleValue(String property, Object value) {
		if (property.equals(DocumentStyle.COLOR)) {
			return "#"+Tools.getColorCode((Color)value);
		} else if (property.equals(DocumentStyle.FONT_SIZE)) {
			return value.toString()+"pt";
		} else if (property.equals(DocumentStyle.HEIGHT)) {
			return value.toString()+"px";
		} else if (property.equals(DocumentStyle.WIDTH)) {
			return value.toString()+"px";
		}
		return value.toString();
	}

}


class xHTMLTable {
	String name;
	String style;
	String[] t_colStyle;
	int row, col;

	public xHTMLTable(String name, String style, String[] t_colStyle) {
		this.name = name;
		this.style = style;
		this.t_colStyle = t_colStyle;
		this.row = 0;
		this.col = 0;//FIXME:
	}
}