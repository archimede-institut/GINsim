package fr.univmrs.tagc.common.document;


import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;

import fr.univmrs.tagc.common.document.DocumentWriter;
import fr.univmrs.tagc.common.document.OOoDocumentWriter;

public class TestOOoWriter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DocumentWriter doc = new OOoDocumentWriter(new File("testme.odt"));
		try {
			doc.startDocument();
			doc.writeText("Il etait une fois, ... Blablabla");
			
			doc.newParagraph(null);
			doc.writeText("un autre paragraphe");
			
			doc.openTable("t_1", "s_table", new String[] { "s1", "s2" });
			doc.openTableCell("some weird stuff here");
			doc.openTableCell("some other stuff");
			
			doc.addTableRow(new String[] {"some NEW stuff here", "some dumb stuff"});
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
