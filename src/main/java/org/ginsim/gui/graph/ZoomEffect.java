package org.ginsim.gui.graph;

import java.awt.event.KeyEvent;

public enum ZoomEffect {

    ZOOM_IN("Zoom in", KeyEvent.VK_PLUS),
    ZOOM_OUT("Zoom out", KeyEvent.VK_MINUS),
    ZOOM_RESET("Reset zoom", KeyEvent.VK_DIVIDE),
    ZOOM_FIT("Zoom to fit", KeyEvent.VK_MULTIPLY);

    ZoomEffect(String name, int key) {
        this.name = name;
        this.key = key;
    }

    public final String name;
    public final int key;
}
