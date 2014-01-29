package org.ginsim.core.graph.regulatorygraph.namedstates;

import java.util.Map;

/**
 * Store a reference to a group of named states.
 *
 * @author Aurelien Naldi.
 */
public interface NamedStateStore {

    Map getInitialState();

    Map getInputState();
}
