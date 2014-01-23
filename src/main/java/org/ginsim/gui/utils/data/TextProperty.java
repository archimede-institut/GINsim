package org.ginsim.gui.utils.data;

/**
 * Created by aurelien on 1/23/14.
 */
public interface TextProperty {

    String getValue();

    void setValue(String value);

    boolean isValidValue(String value);
}
