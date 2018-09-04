package org.ginsim.service.export.nusmv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.modifier.reduction.ModelReducer;
import org.ginsim.TestFileUtils;
import org.ginsim.common.application.GsException;
import org.ginsim.common.application.Txt;
import org.ginsim.common.utils.IOUtils;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.perturbation.Perturbation;
import org.ginsim.core.graph.regulatorygraph.perturbation.PerturbationFixed;
import org.ginsim.core.graph.regulatorygraph.perturbation.PerturbationMultiple;
import org.ginsim.core.service.GSServiceManager;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClass;
import org.ginsim.service.tool.reg2dyn.priorityclass.PrioritySetDefinition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class NuSMVExportMutantPriorityTest {

	private static final String module = "NuSMVExport";

	private static String sModel = null;
	private static RegulatoryGraph graph = null;
	private static NuSMVExportService service = null;
	private static File dir = null;

	@BeforeAll
	public static void setUp() {
		// TODO: this should not be here...
		// Should be either unnecessary or done Once before All tests
		Txt.push("org.ginsim.messages");
		dir = TestFileUtils.getTestFileDirectory("models");
		sModel = "toymodel4d";
		graph = TestFileUtils
				.loadGraph(new File(dir + "/" + sModel + ".ginml"));

		service = GSServiceManager.getService(NuSMVExportService.class);
		assertNotNull( service, "NuSMVExportService service is not available");
	}

	private void runService(NuSMVConfig config, File tmpFile) {
		try {
			service.export(config, tmpFile);
		} catch (IOException e) {
			fail("Could not export to " + tmpFile);
		} catch (GsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private StringBuffer readFile(File file) {
		StringBuffer sb = null;
		try {
			sb = IOUtils.readFromFile(file.toString());
		} catch (IOException e) {
			fail("Could not read file: " + file.toString());
		}
		return sb;
	}

	@Test
	public void testExportMutantgAKOgBE1Priority2classes() {
		File tmpDir = TestFileUtils.getTempTestFileDirectory(module);
		File tmpFile = new File(tmpDir, sModel + ".smv");

		NuSMVConfig config = new NuSMVConfig(graph.getModel());
		LogicalModel model = config.getModel();
		List<RegulatoryNode> nodeOrder = graph.getNodeOrder();

		// Priorities
		PrioritySetDefinition pcDef = new PrioritySetDefinition(nodeOrder, "pctest");
        pcDef.clear();
        pcDef.m_elt.clear();

        PriorityClass class1 = pcDef.get(pcDef.add());
		class1.setMode(PriorityClass.SYNCHRONOUS);
        PriorityClass class2 = pcDef.get(pcDef.add());
		class2.setMode(PriorityClass.ASYNCHRONOUS);

		pcDef.associate(nodeOrder.get(0), class1);
		pcDef.associate(nodeOrder.get(1), class1);
		pcDef.associate(nodeOrder.get(2), class2);
		pcDef.associate(nodeOrder.get(3), class2);
		config.setUpdatingMode(pcDef);
		
		// Perturbation
		List<Perturbation> lst = new ArrayList<Perturbation>();
		lst.add(new PerturbationFixed(model.getComponents().get(0), 0));
		lst.add(new PerturbationFixed(model.getComponents().get(1), 1));
		Perturbation perturbation = new PerturbationMultiple(lst);

		model = perturbation.apply(model);
		ModelReducer reducer = new ModelReducer(model);
		reducer.removePseudoOutputs();
		config.updateModel(reducer.getModel());

		// Run test
		runService(config, tmpFile);
		StringBuffer sbtmp = readFile(tmpFile);

		File origFile = new File(TestFileUtils.getTestFileDirectory(module),
				sModel + ".gAKOgBE1.2classes.smv");
		StringBuffer sbOrig = readFile(origFile);

		// Discard first line, containing the generation date
		assertEquals(sbtmp.substring(sbtmp.indexOf("\n")),
				sbOrig.substring(sbOrig.indexOf("\n")));
	}
}
