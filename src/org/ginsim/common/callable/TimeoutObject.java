package org.ginsim.common.callable;

/**
 * Objects waiting for a timeout
 */
public interface TimeoutObject {

    /**
     * the timeout is elapsed
     */
    public void timeout();
}
