package fr.univmrs.tagc.GINsim.export;

import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.graph.GsActionProvider;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.gui.GsOpenAction;
import fr.univmrs.tagc.GINsim.plugin.GsPlugin;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.widgets.StackDialog;

/**
 * Basic helper class for exports.
 * 
 * <p>You need to set the values of <code>id</code> at least.
 * You can add a file filter and default extension by setting the values of
 * <code>filter</code>, <code>filterDescr</code> and <code>extension</code>.
 * 
 * <p>If the export requires some config dialog, the methods <code>needConfig</code> and
 * <code>getConfigPanel</code> must be overriden.
 * 
 * <p>The export itself happens within <code>doExport</code>.
 */
abstract public class GsAbstractExport implements GsPlugin, GsActionProvider  {

	protected String id;
	protected String[] filter = null;
	protected String filterDescr = null;
	protected String extension = null;

	/**
	 * if your export plugin supports several format, override this.
	 * 
	 * @return a vector listing all supported format or null if not applicable
	 */
	public Vector getSubFormat() {
		return null;
	}
	
	/**
	 * register the plugin as export capable.
	 * Override this to declare it as subformat if applicable.
	 */
	public void registerPlugin() {
		GsGraphManager.registerExportProvider(this);
	}

	public String toString() {
		return id;
	}

	public void runAction (int actionType, int ref, Graph graph, JFrame frame) throws GsException {
	    if (actionType != ACTION_EXPORT) {
	        return;
        }
	    GsExportConfig config = new GsExportConfig(graph, this, ref);
		if(needConfig(config)) {
			new GsExportDialog(this, config).setVisible(true);
		} else {
			selectFile(config);
		}
	}
	
	protected void selectFile(GsExportConfig config) throws GsException {
		GsAbstractExport export;
		Vector v_format = getSubFormat();
		if (v_format != null && config.format != -1) {
			export = (GsAbstractExport)v_format.get(config.format);
		} else {
			export = this;
		}
		String filterDescr = export.getFilterDescription(config);
		GsFileFilter ffilter = null;
		if (filterDescr != null) {
			ffilter = new GsFileFilter();
			ffilter.setExtensionList(export.getFilter(config), filterDescr);
		}
	    
		config.filename = GsOpenAction.selectSaveFile(null, ffilter, null, export.getExtension(config));
		if (config.filename == null) {
			return;
		}
		if (v_format != null && config.format != -1) {
			((GsAbstractExport)v_format.get(config.format)).doExport(config);
		} else {
			doExport(config);
		}
	}
	
	protected String getID() {
		return id;
	}
	protected String[] getFilter(GsExportConfig config) {
		return filter;
	}
	protected String getFilterDescription(GsExportConfig config) {
		return filterDescr;
	}
	protected String getExtension(GsExportConfig config) {
		return extension;
	}

	protected boolean needConfig(GsExportConfig config) {
		return false;
	}
	protected JComponent getConfigPanel(GsExportConfig config, StackDialog dialog) {
		return null;
	}

	abstract protected void doExport(GsExportConfig config);
}
