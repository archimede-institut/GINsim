package fr.univmrs.tagc.GINsim.annotation;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.common.xml.XMLWriter;
import fr.univmrs.tagc.common.xml.XMLize;

/**
 * Annotation for Gene : contains text and a list of url
 */
public class Annotation implements XMLize
{
    /** list of links  */
	protected Vector linkList;
	private String comment;
	
	/**
	 * create an empty annotation.
	 */
	public Annotation() {
		linkList = new Vector();
		comment = "";
	}
	
	/**
	 * get the vector of url
	 * @return the vector of String (url).
	 */
	public String getLink(int index) {
		return linkList.get(index).toString();
	}
	public void openLink(int index) {
		((AnnotationLink)linkList.get(index)).open();
	}
	public void addLink(String s, GsGraph graph) {
		setLink(s, linkList.size(), graph);
	}
	public void setLink(String s, int index, GsGraph graph) {
		if (index == linkList.size()) {
			linkList.add(new AnnotationLink(s, graph));
		} else {
			((AnnotationLink)linkList.get(index)).setText(s, graph);
		}
	}
	/**
	 * return the String containing the comments.
	 * @return a String
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Set the string containing the comments.
	 * @param comment the new comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public void toXML(XMLWriter out, Object param, int mode) throws IOException {
			if (comment.equals("") && linkList.size()==0) {
			    return;         
            }
			out.openTag("annotation");
            if (linkList.size() > 0) {
                out.openTag("linklist");
                for (int i=0 ; i<linkList.size() ; i++) {
                    out.openTag("link");
                    out.addAttr("xlink:href", linkList.elementAt(i).toString());
                    out.closeTag();
                }
                out.closeTag();
            }
			if (!comment.equals("")) {
				out.openTag("comment");
				out.addContent(comment);
				out.closeTag();
			}
            out.closeTag();
	}
	
	public Object clone() {
		Annotation clone = new Annotation();
		clone.copyFrom(this);
		return clone;
	}

	public void copyFrom(Annotation other) {
		int len = other.linkList.size();
		for (int i=0 ; i<len ; i++) {
			addLink(other.getLink(i), null);
		}
		setComment(other.comment);
	}
	
    /**
     * @return true if the annotation is empty
     */
    public boolean isEmpty() {
        return "".equals(comment) && linkList.size() == 0;
    }

	public String getHTMLComment() {
		StringBuffer buf = new StringBuffer();
		boolean hasLink = false;
		for (Iterator it = linkList.iterator() ; it.hasNext() ; ) {
			AnnotationLink lnk = (AnnotationLink)it.next();
			if (lnk.helper != null) {
				String s = lnk.helper.getLink(lnk);
				if (s != null) {
					if (!hasLink) {
						hasLink = true;
						buf.append("<ul>\n");
					}
					if (s == lnk.toString() && s.length() >= 50) {
						buf.append("<li><a href='" + s +"'>" + s.substring(0, 45) + "...</a></li>\n");
					} else {
						buf.append("<li><a href='" + s +"'>" + lnk + "</a></li>\n");
					}
				}
			}
		}
		if (hasLink) {
			buf.append("</ul>\n<p/>");
		}
		buf.append(comment.replaceAll("\n", "<br/>"));
		return buf.toString();
	}
}
