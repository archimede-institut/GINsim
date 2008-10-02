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
	private Vector linkList;
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
		return getLinkList().get(index).toString();
	}
	public void openLink(int index) {
		((AnnotationLink)getLinkList().get(index)).open();
	}
	public void addLink(String s, GsGraph graph) {
		setLink(s, getLinkList().size(), graph);
	}
	public void delLink(String s, GsGraph graph) {
		while (getLinkList().removeElement(new AnnotationLink(s, graph))) ;
	}
	public void setLink(String s, int index, GsGraph graph) {
		if (index == getLinkList().size()) {
			AnnotationLink al = new AnnotationLink(s, graph);
			if (!containsLink(al)) getLinkList().add(al);
		} else {
			((AnnotationLink)getLinkList().get(index)).setText(s, graph);
		}
	}
	public boolean containsLink(AnnotationLink al) {
		return getLinkList().contains(al);
	}
	/**
	 * return the String containing the comments.
	 * @return a String
	 */
	public String getComment() {
		return comment;
	}

	public Vector getLinkList() {
		return linkList;
	}

	
	/**
	 * Set the string containing the comments.
	 * @param comment the new comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public void toXML(XMLWriter out, Object param, int mode) throws IOException {
			if (comment.equals("") && getLinkList().size()==0) {
			    return;         
            }
			out.openTag("annotation");
            if (getLinkList().size() > 0) {
                out.openTag("linklist");
                for (int i=0 ; i<getLinkList().size() ; i++) {
                    out.openTag("link");
                    out.addAttr("xlink:href", getLinkList().elementAt(i).toString());
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
		int len = other.getLinkList().size();
		for (int i=0 ; i<len ; i++) {
			addLink(other.getLink(i), null);
		}
		setComment(other.comment);
	}
	
    /**
     * @return true if the annotation is empty
     */
    public boolean isEmpty() {
        return "".equals(comment) && getLinkList().size() == 0;
    }

	public String getHTMLComment() {
		StringBuffer buf = new StringBuffer();
		boolean hasLink = false;
		for (Iterator it = getLinkList().iterator() ; it.hasNext() ; ) {
			AnnotationLink lnk = (AnnotationLink)it.next();
			if (lnk.getHelper() != null) {
				String s = lnk.getHelper().getLink(lnk.proto, lnk.value);
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
