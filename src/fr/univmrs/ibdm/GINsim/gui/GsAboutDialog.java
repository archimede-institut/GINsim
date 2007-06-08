package fr.univmrs.ibdm.GINsim.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import fr.univmrs.ibdm.GINsim.global.Tools;
import fr.univmrs.ibdm.GINsim.manageressources.ImageLoader;

/**
 * a nice (?) "About dialog" for GINsim
 */
//public class GsAboutDialog extends AboutDialog  {
  public class GsAboutDialog extends JFrame implements HyperlinkListener {

	private static final long serialVersionUID = 8297377937071144230L;
    
	private String aboutText;
	private javax.swing.JPanel jContentPane = null;

	private javax.swing.JEditorPane jEditorPane = null;
	private javax.swing.JScrollPane jScrollPane = null;
	/**
	 * This is the default constructor
	 */
	public GsAboutDialog() {
		super();
		aboutText="<body background='#33a'>" +
                "<table><tr><td>" +
                "GINsim 2.3, develloped by:" +
                "<br>Aurelien NALDI" +
                "<p>Frederic CORDEIL" +
                "<br>Thomas MARCQ" +
                "<br>Cecile MENAHEM" +
                "<br>Romain MUTI" +
                "<br><br>" +
                "<a href='http://gin.univ-mrs.fr/GINsim/'>Information GINsim</a>" +
                "<br>" +
                "</td><td>" +
                "<img src='"+ImageLoader.getImagePath("gs1.gif")+"'" +
                "</td></tr></table>"+
                "</body>";

        initialize();
    }
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(493, 272);
		this.setContentPane(getJContentPane());
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
			jContentPane.add(getJScrollPane(), c_sp);
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
			jEditorPane.setBackground(java.awt.SystemColor.menu);
			jEditorPane.setText(aboutText);
			jEditorPane.addHyperlinkListener(this);
		}
		return jEditorPane;
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
	 * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
	 */
	public void hyperlinkUpdate(HyperlinkEvent event) {
		if (event.getEventType()==HyperlinkEvent.EventType.ACTIVATED) {
			Tools.webBrowse(event.getURL().toString());
		}
	}
}  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"
