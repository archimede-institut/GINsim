package org.ginsim.gui.shell;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.SystemColor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.ginsim.common.application.GsException;
import org.ginsim.common.utils.IOUtils;
import org.ginsim.common.utils.OpenUtils;
import org.ginsim.common.xml.XMLHelper;
import org.ginsim.commongui.dialog.GUIMessageUtils;
import org.ginsim.commongui.utils.ImageLoader;
import org.ginsim.gui.utils.widgets.Frame;
import org.xml.sax.Attributes;



/**
 * "About dialog" for GINsim
 */
public class AboutDialog extends Frame implements HyperlinkListener {

	private static final long		serialVersionUID = 8297377937071144230L;

	private javax.swing.JPanel		jContentPane = null;
	private javax.swing.JEditorPane	jEditorPane  = null;
	private javax.swing.JScrollPane	jScrollPane  = null;
	private JTabbedPane				tabpane;
	private JScrollPane				jScrollPane2;
	private JEditorPane				jEditorPane2;

	private static AboutData data;
	private static String aboutText = "";
	private static String contribText = "";

	/**
	 * setter for file
	 * @param path the path to set
	 */
	public static final void setDOAPFile(String path) {
		data = new AboutData();
		new DOAPParser(data, path);

		StringBuffer s = new StringBuffer("<body><center>");
		s.append("\n<img src='" + ImageLoader.getImagePath(data.logo) + "'/>");
		s.append("\n<h1>" + data.name + " " + data.version + "</h1>");
		s.append(data.description);
		s.append("\n<p/><a href='" + data.link + "'>"+data.link+"</a>");
		s.append("</center></body>");
		aboutText = s.toString();

		s = new StringBuffer("<body><table>");
		if (data.contributors.size() > 0) {
			s.append("<tr><th colspan='2'>Current team</th></tr>");
			for (Iterator it = data.contributors.iterator() ; it.hasNext() ; ) {
				String[] t = (String[])it.next();
				String s_name = t[1] != null ? "<a href='"+t[1]+"'>"+t[0]+"</a>" : t[0];
				s.append("<tr><td>" + s_name + "</td><td>" + t[2] + "</td></tr>");
			}
		}
		if (data.previousContributors.size() > 0) {
			s.append("<tr><th colspan='2'>Previous contributors</th></tr>");
			for (Iterator it = data.previousContributors.iterator() ; it.hasNext() ; ) {
				String[] t = (String[])it.next();
				String s_name = t[1] != null ? "<a href='"+t[1]+"'>"+t[0]+"</a>" : t[0];
				s.append("<tr><td>" + s_name + "</td><td>" + t[2] + "</td></tr>");
			}
		}
		s.append("</table></body>");
		contribText = s.toString();
	}

	/**
	 * This is the default constructor
	 */
	public AboutDialog() {
		super("aboutDialog", 430, 400);
		setContentPane(getJContentPane());
	}

	private JTabbedPane getTabPane() {
		if (tabpane == null) {
			tabpane = new JTabbedPane();
			tabpane.addTab("About", getJScrollPane());
			tabpane.addTab("Credits", getJScrollPane2());
		}
		return tabpane;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new GridBagLayout());
			GridBagConstraints c_img = new GridBagConstraints();
			c_img.gridx = 0;
			c_img.gridy = 0;
			GridBagConstraints c_sp = new GridBagConstraints();
			c_sp.gridx = 0;
			c_sp.gridy = 1;
			c_sp.weightx = 1;
			c_sp.weighty = 1;
			c_sp.fill = GridBagConstraints.BOTH;
			jContentPane.add(getTabPane(), c_sp);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jEditorPane
	 * 
	 * @return javax.swing.JEditorPane
	 */
	private javax.swing.JEditorPane getJEditorPane() {
		if (jEditorPane == null) {
			jEditorPane = new javax.swing.JEditorPane();
			jEditorPane.setContentType("text/html");
			jEditorPane.setEditable(false);
			jEditorPane.setEnabled(true);
			jEditorPane.setBackground(SystemColor.window);
			jEditorPane.setText(aboutText);
			jEditorPane.addHyperlinkListener(this);
		}
		return jEditorPane;
	}

	private javax.swing.JEditorPane getJEditorPane2() {
		if (jEditorPane2 == null) {
			jEditorPane2 = new javax.swing.JEditorPane();
			jEditorPane2.setContentType("text/html");
			jEditorPane2.setEditable(false);
			jEditorPane2.setEnabled(true);
			jEditorPane2.setBackground(SystemColor.window);
			jEditorPane2.setText(contribText);
		}
		return jEditorPane2;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new javax.swing.JScrollPane();
			jScrollPane.setViewportView(getJEditorPane());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPane2() {
		if (jScrollPane2 == null) {
			jScrollPane2 = new javax.swing.JScrollPane();
			jScrollPane2.setViewportView(getJEditorPane2());
		}
		return jScrollPane2;
	}

	/**
	 * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
	 */
	public void hyperlinkUpdate(HyperlinkEvent event) {
		if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			OpenUtils.openURI(event.getDescription());
		}
	}

	@Override
	public void close() {
		setVisible(false);
		dispose();
	}
}


class AboutData {

	String logo, name, version, description, link;
	List contributors = new ArrayList();
	List previousContributors = new ArrayList();
}

class DOAPParser extends XMLHelper {

	private static final Map CALLMAP = new HashMap();
	private static final int NAME =	0;
	private static final int DESCR =	1;
	private static final int LOGO =	2;
	private static final int VERSION =	3;
	private static final int HOMEPAGE =	4;
	private static final int PERSON = 5;
	private static final int FNAME =	6;
	private static final int FMBOX =	7;
	private static final int FMADE =	8;
	private static final int CONTRIB =	10;
	private static final int OLDCONTRIB =	11;
	
		
	static {
		addCall("name", NAME, CALLMAP, ENDONLY, true);
		addCall("description", DESCR, CALLMAP, ENDONLY, true);
		addCall("logo", LOGO, CALLMAP, ENDONLY, true);
		addCall("version", VERSION, CALLMAP, ENDONLY, true);
		addCall("homepage", HOMEPAGE, CALLMAP, STARTONLY, false);
		
		addCall("maintainer", CONTRIB, CALLMAP, BOTH, false);
		addCall("helper", OLDCONTRIB, CALLMAP, BOTH, false);
		
		addCall("foaf:Person", PERSON, CALLMAP, BOTH, false);
		addCall("foaf:name", FNAME, CALLMAP, ENDONLY, true);
		//addCall("foaf:mbox", FMBOX, CALLMAP, STARTONLY, false);
		addCall("foaf:made", FMADE, CALLMAP, ENDONLY, true);
	}
	
	AboutData data;
	List contribList = null;
	String[] s_currentContrib = null;
	
	public DOAPParser(AboutData data, String path) {
		m_call = CALLMAP;
		this.data = data;
		try {
			startParsing(IOUtils.getStreamForPath(path), false);
		} catch (Exception e) {
			GUIMessageUtils.openErrorDialog(new GsException(GsException.GRAVITY_ERROR, e), null);
		}
	}
	
	protected void startElement(int id, Attributes attributes) {
		switch (id) {
			case HOMEPAGE:
				data.link = attributes.getValue("rdf:resource");
				break;
			case FMBOX:
				if (s_currentContrib != null) {
					s_currentContrib[1] = attributes.getValue("rdf:resource");
				}
				break;
			case PERSON:
				if (contribList != null) {
					s_currentContrib = new String[3];
					contribList.add(s_currentContrib);
				}
				break;
			case CONTRIB:
				contribList = data.contributors;
				break;
			case OLDCONTRIB:
				contribList = data.previousContributors;
				break;
		}
	}
	
	protected void endElement(int id) {
		switch (id) {
			case NAME:
				data.name = curval;
				break;
			case DESCR:
				data.description = curval;
				break;
			case LOGO:
				data.logo = curval;
				break;
			case VERSION:
				data.version = curval;
				break;
			case PERSON:
				s_currentContrib = null;
				break;
			case CONTRIB:
			case OLDCONTRIB:
				contribList = null;
				break;
			case FNAME:
				if (s_currentContrib != null) {
					s_currentContrib[0] = curval;
				}
				break;
			case FMADE:
				if (s_currentContrib != null) {
					s_currentContrib[2] = curval;
				}
				break;
		}
	}
}
