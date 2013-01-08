package org.ginsim.service.tool;

import org.ginsim.service.tool.connectivity.TestConnectivity;
import org.ginsim.service.tool.graphcomparator.TestGraphComparator;
import org.ginsim.service.tool.localgraph.TestLocalGraph;
import org.ginsim.service.tool.modelsimplifier.TestModifier;
import org.ginsim.service.tool.sccgraph.TestSCCGraph;
import org.ginsim.service.tool.stablestates.TestStableStates;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith( Suite.class)
@SuiteClasses( {TestConnectivity.class,
				TestGraphComparator.class,
				TestModifier.class,
				TestSCCGraph.class,
				TestLocalGraph.class,
				TestStableStates.class})

public class AllToolTests {

}
