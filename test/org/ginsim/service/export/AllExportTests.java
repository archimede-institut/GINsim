package org.ginsim.service.export;

import org.ginsim.service.export.sbml.SBMLQualExportTest;
import org.ginsim.service.imports.sbml.SBMLImportTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith( Suite.class)
@SuiteClasses( {SBMLQualExportTest.class})
public class AllExportTests {

}
