package fr.univmrs.tagc.GINsim.gui;

import java.io.File;

import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.widgets.AboutDialog;

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
            Tools.openURI("file://" + GsEnv.getGinsimDir() + "Documentation/html/index.html");
        } else {
            GsEnv.error(new GsException(GsException.GRAVITY_ERROR, Translator.getString("STR_docPathError")), null);
        }
	}
}
