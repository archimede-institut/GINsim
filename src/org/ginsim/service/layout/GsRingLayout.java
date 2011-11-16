package org.ginsim.service.layout;

import org.ginsim.graph.common.VertexAttributesReader;

/**
 * level placement of nodes
 */
public class GsRingLayout implements GsLayoutAlgo {

    
    private VertexAttributesReader vreader;
    
    private int centerx, centery;
    private int rayonRoot, rayonStable, rayonClassic;
    private double phiRoot, phiStable, phiClassic;
    private double tetaRoot, tetaStable, tetaClassic;
    
    public void configure(VertexAttributesReader vreader, int nbRoot, int nbStable, int nbClassic, int maxHeight, int maxWidth) {
        this.vreader = vreader;
        
		rayonRoot = (nbRoot*150)/8;
		tetaRoot = (2*Math.PI)/nbRoot;
		phiRoot=0;

		rayonClassic = (nbClassic*150)/8;
		if ((rayonRoot+150) > rayonClassic) {
		    rayonClassic = rayonRoot + 150;
		}
		tetaClassic = (2*Math.PI)/nbClassic;
		phiClassic=0;

		rayonStable = (nbStable * 150)/8;
		if ((rayonClassic+150)>rayonStable) {
		    rayonStable = rayonClassic+150;
		}
		tetaStable = (2*Math.PI)/nbStable;
		phiStable=0;

        centerx = 10 + rayonStable ;
        centery = 10 + rayonStable;

        
    }
    
    public void placeNextRoot() {
        vreader.setPos ((int)(centerx+rayonRoot*Math.cos(phiRoot)), 
                		(int)(centery-rayonRoot*Math.sin(phiRoot)));
        phiRoot += tetaRoot;
    }

    public void placeNextStable() {
        vreader.setPos ((int)(centerx+rayonStable*Math.cos(phiStable)), 
        		(int)(centery-rayonStable*Math.sin(phiStable)));
        phiStable += tetaStable;
    }

    public void placeNextClassic() {
        vreader.setPos ((int)(centerx+rayonClassic*Math.cos(phiClassic)), 
        		(int)(centery-rayonClassic*Math.sin(phiClassic)));
        phiClassic += tetaClassic;
    }
}
