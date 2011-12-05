package org.ginsim.core.utils.timeout;

/**
 * Objects waiting for a timeout
 */
public interface TimeoutObject {

    /**
     * the timeout is elapsed
     */
    public void timeout();
}
