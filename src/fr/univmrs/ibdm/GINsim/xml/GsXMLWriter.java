package fr.univmrs.ibdm.GINsim.xml;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

/**
 *
 */
public class GsXMLWriter {

    private FileWriter out = null;
    private Vector v_stack = new Vector();
    private boolean inTag;
    
    /**
     * 
     * @param fileName
     * @param dtdFile
     * @throws IOException 
     */
    public GsXMLWriter(String fileName, String dtdFile) throws IOException {
        out = new FileWriter(fileName);
        out.write("<?xml version=\"1.0\"?>\n");
        if (dtdFile != null) {
            out.write("<!DOCTYPE gxl SYSTEM \""+dtdFile+"\">\n");
        }
    }
    
    /**
     * 
     * @param s
     * @param isAttVal
     * @throws IOException
     */
    public void writeEsc (String s, boolean isAttVal) throws IOException
    {
        byte[] ch = s.getBytes();
        for (int i=0 ; i<ch.length ; i++) {
           switch (ch[i]) {
           case '&':
               out.write("&amp;");
               break;
           case '<':
               out.write("&lt;");
               break;
           case '>':
               out.write("&gt;");
               break;
           case '\"':
                if (isAttVal) {
                    out.write("&quot;");
                } else {
                    out.write('\"');
                }
                break;
           default:
               if (ch[i] > '\u007f') {
                   out.write("&#");
                   out.write(Integer.toString(ch[i]));
                   out.write(';');
               } else {
                   out.write(ch[i]);
               }
           }
        }
    }

    /**
     * @param s
     * @throws IOException
     */
    public void write(String s) throws IOException {
        out.write(s);
    }
    
    /**
     * @throws IOException
     */
    public void close() throws IOException {
        out.close();
    }

    /**
     * open a tag in the XML output file
     * @param name
     * @throws IOException
     */
    public void openTag(String name) throws IOException {
    	if (inTag) {
    		out.write(">");
    	}
        v_stack.add(name);
        inTag = true;
        out.write("<"+name);
        
    }

    /**
     * close the currently opened tag.
     * depending on context it will use "/&gt; or "&lt;/name&gt;"
     * @throws IOException
     */
    public void closeTag() throws IOException {
        int l = v_stack.size()-1;
        if (inTag) {
            out.write("/>");
        } else {
            out.write("</"+v_stack.get(l)+">");
        }
        v_stack.remove(l);
        inTag = false;
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
        out.write(" "+name+"=\"");
        if (value == null) {
        	writeEsc("", true);
        } else {
        	writeEsc(value, true);
        }
        out.write("\"");
    }
    
    /**
     * add a "text child"
     * @param s
     * @throws IOException 
     */
    public void addContent(String s) throws IOException {
    	if (inTag) {
    		out.write(">");
    		inTag = false;
    	}
    	writeEsc(s, false);
    }
}
