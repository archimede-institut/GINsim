package org.ginsim.service.tool;

import org.ginsim.service.tool.modelsimplifier.TestModifier;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith( Suite.class)
@SuiteClasses( {TestModifier.class }) // TestGraphComparator.class
public class AllToolTests {

}
