package fr.univmrs.ibdm.GINsim.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.StringCharacterIterator;
import java.util.Vector;

/**
 *
 */
public class GsXMLWriter {

    private OutputStreamWriter out = null;
    private Vector v_stack = new Vector();
    private boolean inTag;
    private boolean inContent;
    private boolean indent;
    private StringBuffer buf = null;
    
    /**
     * 
     * @param out
     * @param dtdFile
     * @throws IOException
     */
    public GsXMLWriter(OutputStreamWriter out, String dtdFile) throws IOException {
        this(out,dtdFile,true);
    }
    public GsXMLWriter(OutputStream out, String dtdFile) throws IOException {
        this(new OutputStreamWriter(out, "UTF-8"),dtdFile,true);
    }

    /**
     * 
     * @param out
     * @param dtdFile
     * @param indent
     * @throws IOException
     */
    public GsXMLWriter(OutputStreamWriter out, String dtdFile, boolean indent) throws IOException {
        this.indent = indent;
        this.out = out;
        write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        if (dtdFile != null) {
            write("<!DOCTYPE gxl SYSTEM \""+dtdFile+"\">\n");
        }
    }
    /**
     * ask to store the next calls into a string buffer.
     * Use <code>getBuffer()</code> to stop it and get the content of the buffer.
     * @throws IOException
     */
    public void toBuffer()  throws IOException {
        if (inTag) {
            write(">");
            if (indent) {
                write("\n");
            }
            inTag = false;
        }
        buf = new StringBuffer();
    }

    /**
     * If <code>toBuffer</code> has previously been called, stop saving to buffer.
     * 
     * @return the content of the buffer.
     */
    public String getBuffer() {
        if (buf == null) {
            return null;
        }
        String s = buf.toString();
        buf = null;
        return s;
    }
    
    /**
     * @param s
     * @param isAttVal
     * @throws IOException
     */
    public void writeEsc (String s, boolean isAttVal) throws IOException
    {
    	StringCharacterIterator iterator = new StringCharacterIterator(s);
    	char cur = iterator.current();
    	while (cur != StringCharacterIterator.DONE) {
           switch (cur) {
           case '&':
               write("&amp;");
               break;
           case '<':
               write("&lt;");
               break;
           case '>':
               write("&gt;");
               break;
           case '\"':
                if (isAttVal) {
                    write("&quot;");
                } else {
                    write('\"');
                }
                break;
           default:
        	   write(cur);
           }
           cur = iterator.next();
        }
    }

    /**
     * @param s
     * @throws IOException
     */
    public void write(String s) throws IOException {
        if (buf != null) {
            buf.append(s);
            return;
        } 
        out.write(s);
    }
    
    /**
     * @param c
     * @throws IOException
     */
    public void write(char c) throws IOException {
        if (buf != null) {
            buf.append(c);
            return;
        } 
        out.write(c);
    }
    
    /**
     * open a tag in the XML output file
     * @param name
     * @throws IOException
     */
    public void openTag(String name) throws IOException {
    	if (inTag) {
    		write(">");
    		if (indent) {
    			write("\n");
    		}
    	}
        inTag = true;
        if (indent) {
        	for (int i=0 ; i<v_stack.size() ; i++) {
        		write("  ");
        	}
        }
        write("<"+name);
        v_stack.add(name);
        inContent = false;
    }

    /**
     * close the currently opened tag.
     * depending on context it will use "/&gt; or "&lt;/name&gt;"
     * @throws IOException
     */
    public void closeTag() throws IOException {
        int l = v_stack.size()-1;
        if (inTag) {
            write("/>");
        } else {
            if (!inContent && indent) {
                for (int i=0 ; i<l ; i++) {
                    write("  ");
                }
            }
            write("</"+v_stack.get(l)+">");
        }
        if (indent) {
        	write("\n");
        }
        v_stack.remove(l);
        inTag = false;
        inContent = false;
    }

    /**
     * add an attribute to the opened tag.
     * If the tag is no-more really opened it will return silently without writing anything.
     * @param name
     * @param value
     * @throws IOException
     */
    public void addAttr(String name, String value) throws IOException {
        if (!inTag) {
            return;
        }
        write(" "+name+"=\"");
        if (value == null) {
        	writeEsc("", true);
        } else {
        	writeEsc(value, true);
        }
        write("\"");
    }
    
    /**
     * add a "text child"
     * @param s
     * @throws IOException 
     */
    public void addContent(String s) throws IOException {
        if (inTag) {
            write(">");
            inTag = false;
        }
        writeEsc(s, false);
        inContent = true;
    }
    /**
     * add a "text child", already formated: should _NOT_ be escaped
     * @param s
     * @param b if true, then the file might get indented
     * @throws IOException 
     */
    public void addFormatedContent(String s, boolean b) throws IOException {
        if (inTag) {
            write(">");
            inTag = false;
            if (b && indent) {
                write("\n");
            }
        }
        write(s);
    }
}
