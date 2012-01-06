package org.ginsim;

import org.ginsim.core.AllCoreTests;
import org.ginsim.service.AllServiceTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith( Suite.class)
@SuiteClasses( {AllCoreTests.class, AllServiceTests.class})
public class AllTests {

}
