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
import org.ginsim.gui.utils.GUIIOUtils;
import org.ginsim.utils.log.LogManager;

import fr.univmrs.tagc.common.utils.GUIMessageUtils;


/**
 * Here are the (few) callback for entry in the "help" menu
 */
public class HelpCallBack {
	
	public static List<Action> getActions() {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new HelpAction());
		actions.add(new AboutAction());
		
		return actions;
	}
	
	public static List<Action> getSupportActions(){
		
		List<Action> actions = new ArrayList<Action>();
		actions.add( new ProvideLogAction());
		actions.add( new ToggleTraceAction());
		
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
		GUIIOUtils.openURI("http://gin.univ-mrs.fr/GINsim/doc.html");
		// GsEnv.error(new GsException(GsException.GRAVITY_ERROR, Translator.getString("STR_docPathError")), null);
	}
}

/**
 * This Action concerns the export of log files into zip file that could be easily sent to support
 * 
 * @author spinelli
 *
 */
class ProvideLogAction extends AbstractAction {
	
	public ProvideLogAction() {
		super("Export logs");
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		String log_zip_path = LogManager.deliverLogs();
		if( log_zip_path != null){
			File zip_file = new File( log_zip_path);
			String save_path = FileSelectionHelper.selectSaveFilename( null, ".zip");
			if( save_path != null){
				boolean result = zip_file.renameTo( new File( save_path));
				if( !result){
					GUIMessageUtils.openErrorDialog( "Unable to save the log ZIP file to selected path.");
				}
			}
		}
		else{
			GUIMessageUtils.openErrorDialog( "Unable to build the log ZIP file. See logs for details.");
		}
	}
}

/**
 * This action permit to activate/unactivate the trace/info log levels
 * 
 * @author spinelli
 *
 */
class ToggleTraceAction extends AbstractAction{
	
	public ToggleTraceAction() {
		
		super( LogManager.getVerboseLevel() ==0?"Enable Traces":"Disable Traces");
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if( LogManager.getVerboseLevel() == 0){
			LogManager.setVerbose( 2);
			putValue( Action.NAME, "Disable Traces");
		}
		else{
			LogManager.setVerbose( 0);
			putValue( Action.NAME, "Enable Traces");
		}
	}
}

