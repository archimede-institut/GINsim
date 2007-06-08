package fr.univmrs.ibdm.GINsim.plugin;

/**
 * this interface describes the methods a plugin must implement
 */
public interface GsPlugin {
	/**
	 * this method is automatically called when the plugin is loaded
	 */
	public void registerPlugin();
}
