package org.ginsim.service;

import org.ginsim.service.export.AllExportTests;
import org.ginsim.service.imports.AllImportTests;
import org.ginsim.service.tool.AllToolTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith( Suite.class)
@SuiteClasses( {AllExportTests.class, AllImportTests.class, AllToolTests.class})
public class AllServiceTests {

}
