package org.ginsim.service.export.nusmv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.tool.reduction.ModelReducer;
import org.ginsim.TestFileUtils;
import org.ginsim.common.application.GsException;
import org.ginsim.common.application.Translator;
import org.ginsim.common.utils.IOUtils;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.perturbation.Perturbation;
import org.ginsim.core.graph.regulatorygraph.perturbation.PerturbationFixed;
import org.ginsim.core.graph.regulatorygraph.perturbation.PerturbationMultiple;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClassDefinition;
import org.ginsim.service.tool.reg2dyn.priorityclass.Reg2dynPriorityClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class NuSMVExportMutantPriorityTest {

	private static final String module = "NuSMVExport";

	private static String sModel = null;
	private static RegulatoryGraph graph = null;
	private static NuSMVExportService service = null;
	private static File dir = null;

	@BeforeClass
	public static void setUp() {
		// TODO: this should not be here...
		// Should be either unnecessary or done Once before All tests
		Translator.pushBundle("org.ginsim.messages");
		dir = TestFileUtils.getTestFileDirectory("models");
		sModel = "toymodel4d";
		graph = TestFileUtils
				.loadGraph(new File(dir + "/" + sModel + ".ginml"));

		service = ServiceManager.getManager().getService(
				NuSMVExportService.class);
		assertNotNull("NuSMVExportService service is not available", service);
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

		NuSMVConfig config = new NuSMVConfig(graph);
		LogicalModel model = config.getModel();
		List<RegulatoryNode> nodeOrder = graph.getNodeOrder();

		// Priorities
		config.setUpdatePolicy(NuSMVConfig.CFG_PCLASS);
		Reg2dynPriorityClass class1 = new Reg2dynPriorityClass(1, "class_1");
		class1.setMode(Reg2dynPriorityClass.SYNCHRONOUS);
		Reg2dynPriorityClass class2 = new Reg2dynPriorityClass(2, "class_2");
		class2.setMode(Reg2dynPriorityClass.ASYNCHRONOUS);
		List<Reg2dynPriorityClass> pclist = new ArrayList<Reg2dynPriorityClass>();
		pclist.add(class1);
		pclist.add(class2);
		PriorityClassDefinition pcDef = new PriorityClassDefinition(nodeOrder,
				"pctest");
		pcDef.setData(pclist);
		config.setPriorityClasses(pcDef);
		pcDef.m_elt.put(nodeOrder.get(0), class1);
		pcDef.m_elt.put(nodeOrder.get(1), class1);
		pcDef.m_elt.put(nodeOrder.get(2), class2);
		pcDef.m_elt.put(nodeOrder.get(3), class2);

		// Perturbation
		List<Perturbation> lst = new ArrayList<Perturbation>();
		lst.add(new PerturbationFixed(model.getNodeOrder().get(0), 0));
		lst.add(new PerturbationFixed(model.getNodeOrder().get(1), 1));
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
