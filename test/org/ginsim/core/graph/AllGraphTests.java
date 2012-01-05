package org.ginsim.core.graph;

import org.ginsim.core.graph.dynamicgraph.AllDynamicGraphTests;
import org.ginsim.core.graph.regulatorygraph.AllRegulatoryGraphTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith( Suite.class)
@SuiteClasses( {AllRegulatoryGraphTests.class, AllDynamicGraphTests.class})
public class AllGraphTests {

}
