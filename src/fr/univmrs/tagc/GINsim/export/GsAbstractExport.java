package fr.univmrs.tagc.GINsim.export;

import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;

import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.gui.GsOpenAction;
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
abstract public class GsAbstractExport<G extends Graph> implements GsPlugin  {

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
	

	public String toString() {
		return id;
	}

	public void runAction (int actionType, int ref, Graph graph, JFrame frame) throws GsException {
	    GsExportConfig<G> config = new GsExportConfig(graph, this, ref);
		if(needConfig(config)) {
			new GsExportDialog(this, config).setVisible(true);
		} else {
			selectFile(config);
		}
	}
	
	protected void selectFile(GsExportConfig<G> config) throws GsException {
		GsAbstractExport<G> export;
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
	protected String[] getFilter(GsExportConfig<G> config) {
		return filter;
	}
	protected String getFilterDescription(GsExportConfig<G> config) {
		return filterDescr;
	}
	protected String getExtension(GsExportConfig<G> config) {
		return extension;
	}

	protected boolean needConfig(GsExportConfig<G> config) {
		return false;
	}
	protected JComponent getConfigPanel(GsExportConfig<G> config, StackDialog dialog) {
		return null;
	}

	abstract protected void doExport(GsExportConfig<G> config) throws GsException;
}
