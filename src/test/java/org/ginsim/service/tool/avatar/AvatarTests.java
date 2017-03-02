package org.ginsim.service.tool.avatar;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import org.colomoto.biolqm.io.avatar.AvatarUtils;
import org.ginsim.service.tool.avatar.domain.Result;
import org.ginsim.service.tool.avatar.service.AvatarServiceFacade;
import org.ginsim.service.tool.avatar.simulation.Simulation;

public class AvatarTests extends TestCase {

	public void testAvatar() throws Exception {
		String dir = "C:\\Users\\Rui\\Documents\\00 Avatar\\Avatar Material\\table-models\\";
		
		List<String> filenames = Arrays.asList(
				"random_001_v010_k2.avatar");//"random_002_v010_k1.avatar");
				//"random_003_v015_k2.avatar","random_004_v015_k2.avatar",
				//"synthetic_1.avatar","synthetic_2.avatar",
				//"mmc.avatar","mmc-cycD1.avatar",
				//"sp1.avatar","sp2.avatar","sp4.avatar",
		List<String> options = Arrays.asList(
				"--strategy=UniformExits --runs=100 --maxDepth=100000 --tau=3 --minCycleSize=4 "
				+"--maxGrowthSize=200000 --maxRewiringSize=200000 --minTransientSize=200 --keepTransients --keepAttractors --quiet "
				+"--input="+dir);

		for(String option : options){
			for(String filename : filenames){
				String[] args = (option+filename).split("( --)|=|--");
				System.out.println("\nFilename = "+filename+"\nARGS = "+args.length+AvatarUtils.toString(args));
				Simulation sim = AvatarServiceFacade.getSimulation(args);
				Result result = sim.run();
				System.out.println("\n\n"+result.toString());
				assertNotNull(result);
			}
		}
	}
}
