package org.ginsim.service.export;

import org.ginsim.service.export.document.TestDocumentWriter;
import org.ginsim.service.export.document.TestGenericDocumentExport;
import org.ginsim.service.export.nusmv.NuSMVExportMutantPriorityTest;
import org.ginsim.service.export.nusmv.NuSMVExportTest;
import org.ginsim.service.export.sbml.SBMLQualExportTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith( Suite.class)
@SuiteClasses( {SBMLQualExportTest.class, NuSMVExportTest.class, NuSMVExportMutantPriorityTest.class, TestDocumentWriter.class, TestGenericDocumentExport.class})
public class AllExportTests {

}
