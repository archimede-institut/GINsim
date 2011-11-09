package org.ginsim.gui.shell.callbacks;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;

import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.widgets.AboutDialog;

/**
 * Here are the (few) callback for entry in the "help" menu
 */
public class GsHelpCallBack {
	
	static {
		AboutDialog.setDOAPFile("/fr/univmrs/tagc/GINsim/resources/GINsim-about.rdf");
	}
	
	public static List<Action> getActions() {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new HelpAction());
		actions.add(new AboutAction());
		return actions;
	}
}

class AboutAction extends AbstractAction {
	public AboutAction() {
		super("About");
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		new AboutDialog().setVisible(true);
	}
}

class HelpAction extends AbstractAction {
	public HelpAction() {
		super("Help");
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
        if (new File("/" + GsEnv.getGinsimDir() + "Documentation/html/index.html").exists()) {
            Tools.openURI("file://" + GsEnv.getGinsimDir() + "Documentation/html/index.html");
        } else {
            Tools.openURI("http://gin.univ-mrs.fr/GINsim/doc.html");
            // GsEnv.error(new GsException(GsException.GRAVITY_ERROR, Translator.getString("STR_docPathError")), null);
        }
	}
}
