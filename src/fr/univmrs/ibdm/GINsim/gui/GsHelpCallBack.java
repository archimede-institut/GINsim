package fr.univmrs.ibdm.GINsim.gui;

import java.io.File;

import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.tagc.global.GsException;
import fr.univmrs.tagc.global.Tools;
import fr.univmrs.tagc.manageressources.Translator;
import fr.univmrs.tagc.widgets.AboutDialog;

/**
 * Here are the (few) callback for entry in the "help" menu
 */
public class GsHelpCallBack {
	
	/**
	 * an about dialog
	 */
	public void about() {
		new AboutDialog().setVisible(true);
	}

	/**
	 * give some help: open the html doc in a navigator
	 * tips of the day ??
	 */
	public void help() {
        if (new File("/" + GsEnv.getGinsimDir() + "Documentation/html/index.html").exists()) {
            Tools.webBrowse("file://" + GsEnv.getGinsimDir() + "Documentation/html/index.html");
        } else {
            GsEnv.error(new GsException(GsException.GRAVITY_ERROR, Translator.getString("STR_docPathError")), null);
        }
	}
}
