package org.ginsim.service;

import org.ginsim.service.export.AllExportTests;
import org.ginsim.service.imports.AllImportTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith( Suite.class)
@SuiteClasses( {AllExportTests.class, AllImportTests.class})
public class AllServiceTests {

}
