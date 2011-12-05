package fr.univmrs.tagc.common.utils;

import java.awt.Component;

import javax.swing.JOptionPane;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.gui.GUIManager;

public class GUIMessageUtils {

	/**
	 * Open a Message Dialog box to indicate users an exception occurred and give some feedback.
	 * 
	 * @param e the exception to display
	 * @param main the parent component
	 */
	public static void openErrorDialog( GsException e, Component main) {
		
		int i = -1;
		switch (e.getGravity()) {
		case GsException.GRAVITY_INFO:
		case GsException.GRAVITY_NORMAL:
			i = JOptionPane.INFORMATION_MESSAGE;
			break;
		default:
			i = JOptionPane.ERROR_MESSAGE;
		}
		
		JOptionPane.showMessageDialog( main, e.getMessage() + "\n", e.getTitle(), i);
	}

	/**
	 * Open a Message Dialog box to indicate users an error occurred and give some feedback.
	 * 
	 * @param message the message to show to the user
	 * @param graph the graph related to the error
	 */
	public static void openErrorDialog( String message, Graph<?, ?> graph) {
		
		openErrorDialog( message, GUIManager.getInstance().getFrame(graph));
	}
	
	/**
	 * Open a Message Dialog box to indicate users an error occurred and give some feedback.
	 * 
	 * @param message the message to show to the user
	 */
	public static void openErrorDialog( String message) {
		
		openErrorDialog( message, (Component) null);
	}
	
	/**
	 * Open a Message Dialog box to indicate users an error occurred and give some feedback.
	 * 
	 * @param message the message to show to the user
	 * @param main the parent component
	 */
	public static void openErrorDialog(String message, Component main) {

		JOptionPane.showMessageDialog(main, message + "\n", "error", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Open a confirmation Dialog box (OK/Cancel) with the given message and the given title 
	 * 
	 * @param msg the message to display in the box
	 * @param title the title of the box
	 * @return true if the user accepted, false if not
	 */
	public static boolean openConfirmationDialog(String msg, String title) {
		
		int ret = JOptionPane.showConfirmDialog(null, msg, title, JOptionPane.OK_CANCEL_OPTION);
		return ret == JOptionPane.OK_OPTION;
	}
}
