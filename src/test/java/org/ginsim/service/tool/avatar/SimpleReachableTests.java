package org.ginsim.service.tool.avatar;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import org.colomoto.logicalmodel.StatefulLogicalModel;
import org.colomoto.logicalmodel.io.avatar.AvatarImport;
import org.ginsim.service.tool.avatar.simulation.Reachable;

public class SimpleReachableTests extends TestCase {

	public void testReachableStates() {
		List<StatefulLogicalModel> models = importAvatar(getAvatarInputFiles());
		for(StatefulLogicalModel model : models)
			System.out.println(Reachable.computeReachableStates(model));
	}

	private List<String> getAvatarInputFiles() {
		String dir = "C:\\Users\\Rui\\Documents\\00 PosDoc\\Avatar Material\\table-models\\";
		return Arrays.asList(dir+"mmc-cycD1.avatar");/*,dir+"mmc.avatar",
				dir+"random_001_v010_k2.avatar",dir+"random_002_v010_k2.avatar",
				dir+"random_003_v015_k2.avatar",dir+"random_004_v015_k2.avatar",
				dir+"sp1.avatar",dir+"sp2.avatar",dir+"sp4.avatar",
				dir+"synthetic_1.avatar",dir+"synthetic_2.avatar",dir+"th-reduced.avatar");*/
	}

	private List<StatefulLogicalModel> importAvatar(List<String> filenames) {
		List<StatefulLogicalModel> result = new ArrayList<StatefulLogicalModel>();
		for(String filename : filenames){
			System.out.println("FILE:"+filename);
			try {
				AvatarImport avatar = new AvatarImport(new File(filename));
				result.add(avatar.getModel());
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e);
				fail(e.getMessage());
			} 
		}
		return result;
	}
}
