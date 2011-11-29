package org.ginsim.gui.shell.callbacks;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;

import org.ginsim.gui.shell.AboutDialog;
import org.ginsim.gui.shell.FileSelectionHelper;

import fr.univmrs.tagc.common.Debugger;
import fr.univmrs.tagc.common.Tools;

/**
 * Here are the (few) callback for entry in the "help" menu
 */
public class HelpCallBack {
	
	public static List<Action> getActions() {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new HelpAction());
		actions.add(new AboutAction());
		actions.add( new LogAction());
		
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
		// TODO: search for local help
		Tools.openURI("http://gin.univ-mrs.fr/GINsim/doc.html");
		// GsEnv.error(new GsException(GsException.GRAVITY_ERROR, Translator.getString("STR_docPathError")), null);
	}
}

/**
 * This Action concerns the export of log files into zip file that could be easily sent to support
 * 
 * @author spinelli
 *
 */
class LogAction extends AbstractAction {
	public LogAction() {
		super("Export logs");
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		String log_zip_path = Debugger.deliverLogs();
		if( log_zip_path != null){
			File zip_file = new File( log_zip_path);
			String save_path = FileSelectionHelper.selectSaveFilename( null, ".zip");
			if( save_path != null){
				boolean result = zip_file.renameTo( new File( save_path));
				if( !result){
					Tools.error( "Unable to save the log ZIP file to selected path.");
				}
			}
		}
		else{
			Tools.error( "Unable to build the log ZIP file. See logs for details.");
		}
	}
}
