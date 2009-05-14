package fr.univmrs.tagc.common.document;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;

/**
 * LaTeX backend for DocumentWriter.
 * 
 * @see DocumentWriter
 */
public class LaTeXDocumentWriter extends DocumentWriter {

	OutputStreamWriter writer;
	
	Map m_style = new HashMap();
	Vector v_table = new Vector();
	LaTeXTable curTable = null;
	public String NEW_LINE = "\\newline\n";
	
	static Map m_stylesWriters = new HashMap();
	// FIXME: add latex style writers ?

	public LaTeXDocumentWriter() {
		registerForDocumentExtra("javascript");
	}

	public void startDocument() throws IOException {
	    if (documentProperties.get(PROP_SUBDOCUMENT) == null) {
    		writer = new OutputStreamWriter(output, "UTF-8");
            writer.write("\\documentclass{article}\n");
    		String[] packages = {"fontenc", "inputenc", "color","xcolor",
    							 "array","multirow","colortbl","graphicx"};
            for (int i=0 ; i<packages.length ; i++) {
            	writer.write("\\usepackage{"+packages[i]+"}\n");
            }
    
            doWriteStyles();
            writer.write("\n\\makeatletter\n\\makeatother\n");
            writer.write("\n\\begin{document}\n");
            
    		if (documentProperties.containsKey(META_TITLE)) {
    			writer.write("\n\\title{"+escape(documentProperties.get(META_TITLE))+"}\n");
    		}
    
    		doCreateMeta("author", META_AUTHOR);
    		doCreateMeta("created-at", META_DATE);
    		doCreateMeta("keywords", META_KEYWORDS);
    		doCreateMeta("description", META_DESCRIPTION);
    		doCreateMeta("generator", META_GENERATOR);
    		
    		writer.write("\\maketitle\n\n");
	    }
	}

	protected String escape(Object text) throws IOException {
		// FIXME: escape for LaTeX
	    return text.toString().replace("_", "\\_");
	}
	protected void writeEscaped(String text) throws IOException {
		writer.write(escape(text));
	}
	
	protected void doOpenParagraph(String style) throws IOException {
		writer.write("\n\n");
		if (style != null) {
		    writer.write("\\style"+style+"{");
		}
	}
	
	protected void doWriteText(String text, boolean newLine) throws IOException {
		writeEscaped(text);
		writer.write("\n");
		if (newLine) {
			writer.write(NEW_LINE);
		}
	}
	
	protected void doOpenTable(String name, String style, String[] t_colStyle) throws IOException {
		curTable = new LaTeXTable(name, style, t_colStyle);
		String scols = "|";
		for (int i=0 ; i<t_colStyle.length ; i++) {
		    scols += "l|";                            // TODO: better column indicators
		}
		v_table.add(curTable);
		writer.write("\n\n\\begin{tabular}{"+scols+"}\n\\hline\n");
	}
	
	protected void doCloseTable() throws IOException {
	    writer.write("\\end{tabular}\n");
		v_table.remove(curTable);
		if (v_table.size() > 0) {
			curTable = (LaTeXTable)v_table.get(v_table.size()-1);
		} else {
			curTable = null;
		}
	}
	
	protected void doOpenTableRow(String style) throws IOException {
		curTable.row++;
		curTable.col = 0;
		if (style != null) {
		    writer.write("\\style"+style+"{");
		}
	}
	
	protected void doOpenTableCell(int colspan, int rowspan, boolean header, String style) throws IOException {
		if (curTable.col > 0) {
			writer.write(" & ");
		}
		// add empty cells for previous multirow cells;
		if (curTable.multirow != null) {
			while (curTable.col < curTable.multirow.length) {
				if (curTable.multirow[curTable.col] > 0) {
					curTable.multirow[curTable.col]--;
					writer.write(" & " );
					curTable.col++;
				} else {
					break;
				}
			}
		}
		curTable.openBracket = 0;
		if (colspan > 1) {
			writer.write("\\multicolumn{"+colspan+"}{*}{");
			curTable.openBracket++;
		}
		if (rowspan > 1) {
			writer.write("\\multirow{"+rowspan+"}{*}{");
			curTable.openBracket++;
			if (curTable.multirow == null) {
				curTable.multirow = new int[curTable.t_colStyle.length];
			}
			for (int i=0 ; i<colspan ; i++) {
				curTable.multirow[curTable.col + i] = rowspan-1;
			}
		}
		if (style != null) {
			writer.write("\\style"+style+"{");
			curTable.openBracket++;
		}
		curTable.col++;
	}
	
	protected void doCloseDocument() throws IOException {
        if (documentProperties.get(PROP_SUBDOCUMENT) == null) {
            writer.write("\n\\end{document}\n");
        }
	    writer.flush();
		writer.close();
	}

	protected void doCloseParagraph() throws IOException {
		if (pos.style != null) {
		    writer.write("}\n");
		}
	}

	protected void doCloseTableCell() throws IOException {
		while (curTable.openBracket > 0) {
			writer.write("}");
			curTable.openBracket--;
		}
	}

	protected void doCloseTableRow() throws IOException {
        if (pos.style != null) {
            writer.write("}");
        }
	    writer.write("\\\\ ");
        boolean hasMultiRow = false;
        int start = 0;
	    if (curTable.multirow != null) {
	        // put the right "\cline" calls if multirows are pending
	        for (int i=0 ; i<curTable.multirow.length ; i++) {
	            if (curTable.multirow[i] > 0) {
	                hasMultiRow = true;
	                if (start < i) {
	                    writer.write("\\cline{"+(start+1)+"-"+i+"}");
	                }
	                start = i+1;
	            }
	        }
	    }
        if (!hasMultiRow) {
            curTable.multirow = null;
            writer.write("\\hline\n");
        } else {
            if (start < curTable.multirow.length) {
                writer.write("\\cline{"+(start+1)+"-"+curTable.multirow.length+"}\n");
            } else {
                writer.write("\n");
            }
        }
	}
	
	protected void doOpenHeader(int level, String content, String style) throws IOException {
		// FIXME: styles
	    switch (level) {
            case 4:
                writer.write("\\subsubsubsection");
                break;
            case 3:
                writer.write("\\subsubsection");
                break;
            case 2:
                writer.write("\\subsection");
                break;
            case 1:
                writer.write("\\section");
                break;
            default:
                return;
                // should not happen!!
	    }
	    writer.write("{"+escape(content)+"}\n");
	}
	
	protected void doAddLink(String href, String content) throws IOException {
	    // TODO
	}
	protected void doOpenList(String style) throws IOException {
		writer.write("\\begin{itemize}\n");
	}
	protected void doOpenListItem() throws IOException {
	    writer.write("\\item ");
	}
	protected void doCloseListItem() throws IOException {
	}
	protected void doCloseList() throws IOException {
        writer.write("\\end{itemize}\n");
	}

	protected void doCreateMeta(String meta, String key) throws IOException {
		if (documentProperties.containsKey(key)) {
		    // TODO
		}
	}
	
	protected void doWriteStyles() throws IOException {
		if (documentStyles != null) {

		    Iterator styleIterator = documentStyles.getStyleIterator();
			while (styleIterator.hasNext()) {
				String style = (String) styleIterator.next();
				Map properties = documentStyles.getPropertiesForStyle(style);
				Iterator propertiesIterator = documentStyles.getPropertiesIteratorForStyle(style);
				while (propertiesIterator.hasNext()) {
					String property = (String) propertiesIterator.next();
					// TODO
				}
			}
		}
	}

	protected void doAddImage(BufferedImage img, String name) throws IOException {
		if (outputDir != null) {
			if (!outputDir.exists()) {
				outputDir.mkdir();
			}
			File fout = new File(outputDir, name);
			ImageIO.write(img, "png", fout);

			// TODO

		} else {
			// TODO: add an error message ?
		}
	}
}

class LaTeXTable {
	String name;
	String style;
	String[] t_colStyle;
	int[] multirow = null;
	int row, col;
	int openBracket = 0;

	public LaTeXTable(String name, String style, String[] t_colStyle) {
		this.name = name;
		this.style = style;
		this.t_colStyle = t_colStyle;
		this.row = 0;
		this.col = 0;
	}
}
