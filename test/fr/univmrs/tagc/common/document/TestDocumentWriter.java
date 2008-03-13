package fr.univmrs.tagc.common.document;


import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipException;

import fr.univmrs.tagc.common.TestTools;

import junit.framework.TestCase;

public class TestDocumentWriter extends TestCase {

	List l;
	public TestDocumentWriter() {
		l = new ArrayList(2);
		File baseDir = TestTools.getTempDir();
		l.add(new OOoDocumentWriter(new File(baseDir,"testme.odt")));
		l.add(new XHTMLDocumentWriter(new File(baseDir,"testme.html")));
	}
	
	public void testDocumentWriters() {
		Iterator it = l.iterator();
		while (it.hasNext()) {
			DocumentWriter doc = (DocumentWriter) it.next();
			doTestWriter(doc);
		}		
	}
	/**
	 * @param args
	 */
	public static void doTestWriter(DocumentWriter doc) {
		try {
			//DocumentProperties
			doc.setDocumentProperties(new String[] {DocumentWriter.META_TITLE, "lorem", DocumentWriter.META_AUTHOR, "Moi"});
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//for dc:date
			doc.setDocumentProperty(DocumentWriter.META_DATE, simpleDateFormat.format(new Date()).toString());
			
			//DocumentStyles
			DocumentStyle styles = doc.getStyles();
			styles.addStyle("s_table");
			styles.addProperty(DocumentStyle.COLOR, new Color(0, 0, 255));	
			styles.addStyle("s1");
			styles.addProperty(DocumentStyle.COLOR, new Color(0, 255, 0));	
			styles.addProperty(DocumentStyle.FONT_SIZE, new Integer(18));	
			styles.addProperty(DocumentStyle.WIDTH, new Integer(75));	
			styles.addProperty("-webkit-border-radius", "5px");	
			
			//DocumentExtra : javascript
			if (doc.doesDocumentSupportExtra("javascript")) {
				StringBuffer javascript = doc.getDocumentExtra("javascript");
				javascript.append("\n\tfunction getElementsByContent(content) {\n" +
						"\t\tvar elements = new Array();\n" +
						"\t\tvar nodes = document.getElementsByTagName('*');\n" +
						"\t\tfor (var i = 0, node; node = nodes[i]; i++) {\n" +
						"\t\t\t	if (node.innerText == content) elements.push(node);\n" +
						"\t\t}\n" +
						"\t\treturn elements;\n" +
						"\t}");
				javascript.append("\n\tfunction addOnclickToTables() {\n" +
						"\tvar tables = getElementsByContent('Stable States');\n" +
						"\tvar i;\n" +
						"\tfor (i = 0 ; i - tables.length != 0 ; i++) {\n" +
						"\t\tvar tr = tables[i].parentNode.parentNode;\n" +
						"\t\tp = document.createElement('p');\n" +
						"\t\tp.innerHTML = 'Stable States';\n" +
						"\t\ta = document.createElement('a');\n" +
						"\t\ta.setAttribute('href', 'javascript:toggle(\"stableState'+i+'\")');\n" +
						"\t\ta.innerHTML = ' (View)';\n" +
						"\t\tp.appendChild(a);\n" +
						"\t\ttables[i].parentNode.replaceChild(p, tables[i]) ;\n" +
						"\t\t\n" +
						"\t\ttr = tr.nextSibling.nextSibling.firstChild.nextSibling;\n" +
						"\t\tp = document.createElement('p');\n" +
						"\t\tp.innerHTML = tr.firstChild.nextSibling.innerHTML;\n" +
						"\t\tp.setAttribute('id', 'stableState'+i);\n" +
						"\t\tp.style.display = 'none';\n" +
						"\t\ttr.replaceChild(p, tr.firstChild.nextSibling);\n" +
						"\t}\n" +
						"}\n");
				javascript.append("\n\tfunction toggle(id) {\n" +
						"\telm = document.getElementById(id);\n" +
						"\tif (elm.style.display == 'none') {\n" +
						"\t\telm.style.display = 'inline'\n" +
						"\t} else {\n" +
						"\t\telm.style.display = 'none'\n" +
						"\t}\n" +
						"}\n\n");
				javascript.append("window.onload = addOnclickToTables;\n");
			}
			
			doc.startDocument();
			doc.writeText("Il etait une fois, ... Blablabla");
			
			doc.newParagraph(null);
			doc.writeText("un autre paragraphe");
			
			doc.openTable("t_1", "s_table", new String[] { "s1", "s2" });
			doc.openTableCell("Salut");
			doc.openTableCell("Hello");
			
			doc.addTableRow(new String[] {"Stable States", "gnagnagna"});
			doc.addTableRow(new String[] {"2.2.2.2.2.", "Bye"});
			doc.closeTable();

			doc.newParagraph(null);
			doc.writeText("et un dernier brin pour finir!");
			doc.close();
		} catch (ZipException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
