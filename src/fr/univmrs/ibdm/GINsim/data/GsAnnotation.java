package fr.univmrs.ibdm.GINsim.data;

import java.io.IOException;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.xml.GsXMLWriter;
import fr.univmrs.ibdm.GINsim.xml.GsXMLize;

/**
 * Annotation for Gene : contains text and a list of url
 */
public class GsAnnotation implements GsXMLize
{
    /** list of links  */
	private Vector linkList;
	private String comment;

	/**
	 * create an empty annotation.
	 */
	public GsAnnotation() {
		linkList = new Vector();
		comment = "";
	}
	
	/**
	 * get the vector of url
	 * @return the vector of String (url).
	 */
	public Vector getLinkList() {
		return linkList;
	}

	/**
	 * Set the vector of url
	 * @param links the new linklist
	 */
	private void setLinkList(Vector links) {
		this.linkList = links;
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
	
	public void toXML(GsXMLWriter out, Object param, int mode) throws IOException {
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
		GsAnnotation clone = new GsAnnotation();
		int len = linkList.size();
		Vector ll = new Vector(len);
		for (int i=0 ; i<len ; i++) {
			ll.add(new String((String)linkList.get(i)));
		}
		clone.setLinkList((Vector)linkList.clone());
		clone.setComment(new String(comment));
		return clone;
	}

    /**
     * @return true if the annotation is empty
     */
    public boolean isEmpty() {
        return "".equals(comment) && linkList.size() == 0;
    }
}
