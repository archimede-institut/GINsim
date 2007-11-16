package fr.univmrs.ibdm.GINsim.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.SystemColor;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import fr.univmrs.ibdm.GINsim.global.Tools;
import fr.univmrs.ibdm.GINsim.manageressources.ImageLoader;

/**
 * "About dialog" for GINsim
 */
  public class GsAboutDialog extends JFrame implements HyperlinkListener {

	private static final long serialVersionUID = 8297377937071144230L;
    
	private javax.swing.JPanel jContentPane = null;

	private javax.swing.JEditorPane jEditorPane = null;
	private javax.swing.JScrollPane jScrollPane = null;

	private JTabbedPane	tabpane;

	private JScrollPane	jScrollPane2;

	private JEditorPane	jEditorPane2;
	
	private static final String LOGO = "gs1.gif";
	private static final String NAME = "GINsim";
	private static final String VERSION = "2.3";
	private static final String LINK = "http://gin.univ-mrs.fr/GINsim";
	private static final String DESCRIPTION = "A computer tool for the modeling and simulation of genetic regulatory networks";
	
	private static final String[][] contributors = {
		{"Claudine CHAOUIYA", "Project coordination"},
		{"Adrien Fauré", "Biological applications"},
		{"Fabrice LOPEZ", "Software development"},
		{"Aurelien NALDI", "Development and biological applications"},
		{"Denis THIEFFRY", "Project coordination"},
	};
	private static final String[][] previousContributors = {
		{"Frederic CORDEIL", "Prototype development"},
		{"Aitor Gonzalez", "Prototype development and biological application"},
		{"Kevin MATHIEU", "APNN export"},
		{"Thomas MARCQ", "Prototype development"},
		{"Cecile MENAHEM", "Prototype development"},
		{"Romain MUTI", "Prototype development"},
	};
	private static final String aboutText;
	private static final String contribText;
	static {
		StringBuffer s = new StringBuffer("<body><center>");
		s.append("\n<img src='" + ImageLoader.getImagePath(LOGO) + "'/>");
		s.append("\n<h1>" + NAME+" "+VERSION+"</h1>");
		s.append(DESCRIPTION);
		s.append("\n<p/><a href='"+LINK+"'>Site web</a>");
		s.append("</center></body>");
		aboutText = s.toString();
		
		s = new StringBuffer("<body><table>");
		s.append("<tr><th colspan='2'>Current team</th></tr>");
		for (int i=0 ; i<contributors.length ; i++) {
			String[] t = contributors[i];
			s.append("<tr><td>"+t[0]+"</td><td>"+t[1]+"</td></tr>");
		}
		s.append("<tr><th colspan='2'>Previous contributors</th></tr>");
		for (int i=0 ; i<previousContributors.length ; i++) {
			String[] t = previousContributors[i];
			s.append("<tr><td>"+t[0]+"</td><td>"+t[1]+"</td></tr>");
		}
		s.append("</table></body>");
		contribText = s.toString();
	}
	
	/**
	 * This is the default constructor
	 */
	public GsAboutDialog() {
		setSize(400, 400);
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
		if(jEditorPane == null) {
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
		if(jEditorPane2 == null) {
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
		if(jScrollPane == null) {
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
		if(jScrollPane2 == null) {
			jScrollPane2 = new javax.swing.JScrollPane();
			jScrollPane2.setViewportView(getJEditorPane2());
		}
		return jScrollPane2;
	}
	/**
	 * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
	 */
	public void hyperlinkUpdate(HyperlinkEvent event) {
		if (event.getEventType()==HyperlinkEvent.EventType.ACTIVATED) {
			Tools.webBrowse(event.getURL().toString());
		}
	}
}
