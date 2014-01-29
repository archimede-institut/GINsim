package org.ginsim.common.utils;

/**
 * Interface for objects which provide a text to display in a tooltip.
 *
 * @author Aurelien Naldi
 */
public interface ToolTipsable {

		/**
         * retrieve the tooltip text
		 * @return the string to be displayed in the tooltip
		 */
		public String toToolTip();
}
