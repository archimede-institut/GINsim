package fr.univmrs.tagc.common.document;

import java.io.IOException;

public abstract class DocumentWriter {

	public static final int OUT = 0;
	public static final int PARAGRAPH = 1;
	public static final int TABLE = 10;
	public static final int TABLE_ROW = 11;
	public static final int TABLE_CELL = 12;
	
	DocumentPos pos = new DocumentPos();
	
	protected abstract void startDocument() throws IOException;

	public void newParagraph() throws IOException {
		newParagraph(null);
	}
	public void newParagraph(String style) throws IOException {
		if (pos.pos == PARAGRAPH) {
			closeParagraph();
		}
		doOpenParagraph(style);
		pos = new DocumentPos(pos, PARAGRAPH);
	}
	public void closeParagraph() throws IOException {
		if (pos.pos != PARAGRAPH) {
			// FIXME: error
			// throw new Exception("incoherent position");
		}
		doCloseParagraph();
		pos = pos.parent;
	}

	public void openTable(String name, String style, String[] t_colStyle) throws IOException {
		while (pos.pos == PARAGRAPH) {
			closeParagraph();
		}
		doOpenTable(name, style, t_colStyle);
		pos = new DocumentPos(pos, TABLE);
	}
	public void openTableCell(int colspan, int rowspan, String content) throws IOException {
		if (pos.pos == TABLE) {
			openTableRow();
		} else if (pos.pos != TABLE_ROW) {
			closeTableCell();
		}
		doOpenTableCell(colspan, rowspan);
		pos = new DocumentPos(pos, TABLE_CELL);
		if (content != null) {
			writeText(content);
		}
	}
	public void openTableRow() throws IOException {
		if (pos.pos != TABLE) {
			closeTableRow();
		}
		doOpenTableRow();
		pos = new DocumentPos(pos, TABLE_ROW);
	}

	public void closeTableCell() throws IOException {
		while (pos.pos != TABLE_CELL) {
			switch (pos.pos) {
				case PARAGRAPH:
					closeParagraph();
					break;
				case TABLE:
				case TABLE_ROW:
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
		if (pos.pos != TABLE_ROW) {
			closeTableCell();
		}
		doCloseTableRow();
		pos = pos.parent;
	}
	public void closeTable() throws IOException {
		if (pos.pos != TABLE) {
			closeTableRow();
		}
		doCloseTable();
		pos = pos.parent;
	}
	
	public void openTableCell(String content) throws IOException {
		openTableCell(1,1, content);
	}
	
	public void writeText(String text) throws IOException {
		if (pos.pos != PARAGRAPH) {
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
		while (pos.pos != OUT) {
			switch (pos.pos) {
				case PARAGRAPH:
					closeParagraph();
					break;
				case TABLE:
				case TABLE_CELL:
				case TABLE_ROW:
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
}

class DocumentPos {
	
	public DocumentPos parent;
	public Object data;
	public int pos;
	
	public DocumentPos() {
		this(null, DocumentWriter.OUT, null);
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