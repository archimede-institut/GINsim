package org.ginsim.service.layout;

import org.ginsim.graph.common.VertexAttributesReader;

/**
 * level placement of nodes
 */
public class GsLevelLayout implements GsLayoutAlgo {

    private int width;
    private int height;
    
    private int nbMaxNodeByRow;
    
    private int rootStartx;
    private int rootStarty;
    private int stableStartx;
    private int stableStarty;
    private int classicStartx;
    private int classicStarty;
    
    private VertexAttributesReader vreader;
    
    private int curRoot;
    private int curStable;
    private int curClassic;
    
    public void configure(VertexAttributesReader vreader, int nbRoot, int nbStable, int nbClassic, int maxHeight, int maxWidth) {
        this.vreader = vreader;
        this.width = maxWidth + 30;
        this.height = maxHeight + 40;
        
        // choose the maximum number of nodes on a single row
        nbMaxNodeByRow = 10;
        if (nbRoot + nbStable + nbClassic > 100) {
            nbMaxNodeByRow = (int)Math.sqrt(nbRoot + nbStable + nbClassic);
        }
        
        rootStartx = 10;
        rootStarty = 10;
        classicStartx = 10;
        classicStarty = rootStarty + height*((nbRoot+nbMaxNodeByRow)/nbMaxNodeByRow);
        stableStartx = 10;
        stableStarty = classicStarty + height * ((nbClassic+nbMaxNodeByRow)/nbMaxNodeByRow);
    }
    
    public void placeNextRoot() {
        int rowNum = curRoot/nbMaxNodeByRow;
        vreader.setPos (rootStartx+(rowNum%2)*(height/2)+(width*(curRoot%nbMaxNodeByRow)), 
                		rootStarty+(height*(rowNum)));
        curRoot++;
    }

    public void placeNextStable() {
        int rowNum = curStable/nbMaxNodeByRow;
        vreader.setPos (stableStartx+(rowNum%2)*(height/2)+(width*(curStable%nbMaxNodeByRow)), 
                		stableStarty+(height*(curStable/nbMaxNodeByRow)));
        curStable++;
    }

    public void placeNextClassic() {
        int rowNum = curClassic/nbMaxNodeByRow;
        vreader.setPos (classicStartx+(rowNum%2)*(height/2)+(width*(curClassic%nbMaxNodeByRow)), 
                		classicStarty+(height*(curClassic/nbMaxNodeByRow)));
        curClassic++;
    }
}
