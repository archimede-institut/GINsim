package org.ginsim.service.imports;

import org.ginsim.service.imports.sbml.SBMLImportTest;
import org.ginsim.service.imports.truthtable.TruthTableImportTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith( Suite.class)
@SuiteClasses( {SBMLImportTest.class, TruthTableImportTest.class})
public class AllImportTests {

}
