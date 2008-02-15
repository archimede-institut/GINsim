package fr.univmrs.tagc.common.widgets;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.SystemColor;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.xml.sax.Attributes;

import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.manageressources.ImageLoader;
import fr.univmrs.tagc.common.xml.XMLHelper;


/**
 * "About dialog" for GINsim
 */
public class AboutDialog extends Frame implements HyperlinkListener {

	private static final long		serialVersionUID		= 8297377937071144230L;

	private javax.swing.JPanel		jContentPane			= null;
	private javax.swing.JEditorPane	jEditorPane				= null;
	private javax.swing.JScrollPane	jScrollPane				= null;
	private JTabbedPane				tabpane;
	private JScrollPane				jScrollPane2;
	private JEditorPane				jEditorPane2;

	private static AboutData data;
	private static String aboutText = "";
	private static String contribText = "";
	
	public static final void setDOAPFile(String path) {
		data = new AboutData();
		new DOAPParser(data, path);

		StringBuffer s = new StringBuffer("<body><center>");
		s.append("\n<img src='" + ImageLoader.getImagePath(data.logo) + "'/>");
		s.append("\n<h1>" + data.name + " " + data.version + "</h1>");
		s.append(data.description);
		s.append("\n<p/><a href='" + data.link + "'>Site web</a>");
		s.append("</center></body>");
		aboutText = s.toString();

		s = new StringBuffer("<body><table>");
		s.append("<tr><th colspan='2'>Current team</th></tr>");
		for (int i = 0 ; i < data.contributors.length ; i++) {
			String[] t = data.contributors[i];
			s.append("<tr><td>" + t[0] + "</td><td>" + t[1] + "</td></tr>");
		}
		s.append("<tr><th colspan='2'>Previous contributors</th></tr>");
		for (int i = 0 ; i < data.previousContributors.length ; i++) {
			String[] t = data.previousContributors[i];
			s.append("<tr><td>" + t[0] + "</td><td>" + t[1] + "</td></tr>");
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
			Tools.webBrowse(event.getURL().toString());
		}
	}

	public void doClose() {
		setVisible(false);
	}
}


// FIXME: fill the "AboutData" object with the DOAP parser

class AboutData {

	String		logo, name, version, description, link;
	String[][]	contributors = {
			{ "Claudine CHAOUIYA", "Project coordination" },
			{ "Adrien Fauré", "Biological applications" },
			{ "Fabrice LOPEZ", "Software development" },
			{ "Aurelien NALDI", "Development and biological applications" },
			{ "Denis THIEFFRY", "Project coordination" },	};
	String[][]	previousContributors	= {
			{ "Frederic CORDEIL", "Prototype development" },
			{ "Aitor GONZALEZ",
			"Prototype development and biological application" },
			{ "Kevin MATHIEU", "APNN export" },
			{ "Thomas MARCQ", "Prototype development" },
			{ "Cecile MENAHEM", "Prototype development" },
			{ "Romain MUTI", "Prototype development" },	};
}

class DOAPParser extends XMLHelper {

	private static final Map CALLMAP = new HashMap();
	private static final int NAME =	0;
	private static final int DESCR =	1;
	private static final int LOGO =	2;
	private static final int VERSION =	3;
	private static final int HOMEPAGE =	4;
	private static final int MAINTAINER = 5;
		
	static {
		addCall("name", NAME, CALLMAP, ENDONLY, true);
		addCall("description", DESCR, CALLMAP, ENDONLY, true);
		addCall("logo", LOGO, CALLMAP, ENDONLY, true);
		addCall("version", VERSION, CALLMAP, ENDONLY, true);
		addCall("homepage", HOMEPAGE, CALLMAP, STARTONLY, false);
		addCall("maintainer", MAINTAINER, CALLMAP, NOCALL, false);
	}
	
	AboutData data;
	
	public DOAPParser(AboutData data, String path) {
		m_call = CALLMAP;
		this.data = data;
		try {
			startParsing(Tools.getStreamForPath(path), false);
		} catch (Exception e) {
			Tools.error(new GsException(GsException.GRAVITY_ERROR, e), null);
		}
	}
	
	protected void startElement(int id, Attributes attributes) {
		switch (id) {
			case HOMEPAGE:
				data.link = attributes.getValue("rdf:resource");
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
		}
	}
}
