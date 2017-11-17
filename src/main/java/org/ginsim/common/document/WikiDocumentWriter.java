package org.ginsim.common.document;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.imageio.ImageIO;

/**
 * Wiki-like backend for the document writer.
 * It is deprecated and should not be used.
 * 
 * @author Aurelien Naldi
 */
public class WikiDocumentWriter extends DocumentWriter {

	OutputStreamWriter writer;
	
	Map m_style = new HashMap();
	public String NEW_LINE = "\n";
	
	Stack lists = new Stack();
	String curList = null;
	

	public void startDocument() throws IOException {
		writer = new OutputStreamWriter(output, "UTF-8");
	}
	
	protected void doOpenParagraph(String style) throws IOException {
		writer.write("\n\n");
	}
	
	protected void doWriteText(String text, boolean newLine) throws IOException {
		writer.write(text);
		if (newLine) {
			writer.write("\n");
		}
	}
	
	protected String newLine() {
		return NEW_LINE;
	}

	protected void doOpenTable(String name, String style, String[] t_colStyle) throws IOException {
		writer.write("{{table}}\n");
	}
	
	protected void doCloseTable() throws IOException {
		writer.write("{{/table}}\n");
	}
	
	protected void doOpenTableRow(String style) throws IOException {
		writer.write("|--------\n");
	}
	
	protected void doOpenTableCell(int colspan, int rowspan, boolean header, String style) throws IOException {
		String c = header ? "!" : "|";
		String s = c;
		if (colspan > 1) {
			s += "c:"+colspan+c;
		}
		if (rowspan > 1) {
			s += "r:"+rowspan+c;
		}
		if (style != null) {
			s += style+c;
		}
		writer.write(s+" ");
	}
	
	protected void doCloseDocument() throws IOException {
		writer.flush();
		writer.close();
	}

	protected void doCloseParagraph() throws IOException {
		writer.write("\n");
		writer.write("\n");
	}

	protected void doCloseTableCell() throws IOException {
		writer.write("\n");
	}

	protected void doCloseTableRow() throws IOException {
	}
	
	protected void doOpenHeader(int level, String content, String style) throws IOException {
		String s = "";
		for (int i=0 ; i<level ; i++) {
			s += "=";
		}
		writer.write(s + " " + content + "\n");
	}
	
	protected void doAddLink(String href, String content) throws IOException {
		writer.write("[["+href+"|"+content+"]]");
	}
	protected void doAddAnchor(String name, String content) throws IOException {
		// TODO
	}
	protected void doOpenList(String style, boolean numbered) throws IOException {
		if (style != null) {
			Map m_style = documentStyles.getPropertiesForStyle(style);
			if (m_style != null) {
				numbered = "O".equals(m_style.get(DocumentStyle.LIST_TYPE));
			}
		}
		String c = numbered ? "#" : "*";
		int len;
		if (curList == null) {
			len = 1;
		} else {
			len = curList.length()+1;
			lists.add(curList);
		}
		String s = "";
		for (int i=0 ; i<len ; i++) {
			s += c;
		}
		curList = s;
	}
	protected void doOpenListItem() throws IOException {
		writer.write(curList + " ");
	}
	protected void doCloseListItem() throws IOException {
		writer.write("\n");
	}
	protected void doCloseList() throws IOException {
		if (lists.size() > 0) {
			curList = (String)lists.pop();
		} else {
			curList = null;
			writer.write("\n");
		}
	}

	protected void doAddImage(BufferedImage img, String name) throws IOException {
		if (outputDir != null) {
			if (!outputDir.exists()) {
				outputDir.mkdir();
			}
			File fout = new File(outputDir, name);
			ImageIO.write(img, "png", fout);
			writer.write("[[rel_img: "+name+"]]");
		} else {
			writer.write("[unable to add the image]");
		}
	}
}
