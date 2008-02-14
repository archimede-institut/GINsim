package fr.univmrs.ibdm.GINsim.gui;

import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.tagc.global.GsException;
import fr.univmrs.tagc.global.OptionStore;

/**
 * Here are the callback for entry in the "file" menu
 * 
 *  
 */
public class GsFileCallBack {

	private GsMainFrame main;

	/**
	 * Default constructor
	 * @param m
	 */
	public GsFileCallBack(GsMainFrame m) {
		main = m;
	}

	/**
	 * just call windowclose from GsMainFrame
	 */
	public void close() {
	    main.close();
	}

	/**
	 * save the graph or save as if no filename/saveoption was previously
	 * specified
	 */
	public void save() {
	    try {
            main.getGraph().save();
            OptionStore.addRecent(main.getGraph().getSaveFileName());
        } catch (GsException e) {
            GsEnv.error(e, main);
        }
	}

	/**
	 * save the current graph in another file. offer some save options: no
	 * visual settings, position only or all of them and call save once this is
	 * done
	 */
	public void saveAs() {
	    try {
            main.getGraph().saveAs();
        } catch (GsException e) {
            GsEnv.error(e, main);
        }
	}

	/**
	 * save the selected subgraph very similar to copy except that it saves the
	 * subgraph immediatly into a new file
	 */
	public void saveSubGraph() {
		try {
			main.getGraph().saveSubGraph();
		} catch (GsException e) {
			GsEnv.error(e, main);
		}
	}

	/**
	 * quit GINsim: call GsEnv.exit to close all frames
	 */
	public void quit() {
		GsEnv.exit();
	}

	/**
	 * open a graph from a file and merge it with the current one.
	 */
	public void mergeGraph() {
	    main.getGraph().merge();
	}

}