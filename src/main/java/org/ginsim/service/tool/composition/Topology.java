package org.ginsim.service.tool.composition;

public class Topology {
	
	private int _numberInstances = 0;
	private boolean neighbourhoodRelation[][];

	/*
	 * The topology
	@param numberInstances the number of identical modules
	*/ 
	public Topology(int numberInstances){
		this.set_numberInstances(numberInstances);
		initNeighbours();
	}

	public int getNumberInstances() {
		return _numberInstances;
	}

	/*
	 @param neighindex1 indicates the index of the first module
	 @param neighindex2 indicates the index of the second module
	 */
	public void addNeighbour(int neighindex1, int neighindex2){
		if (neighindex1 >= _numberInstances || neighindex2 >= _numberInstances){
			// raise Exception ("invalid neighbour index")
		}
		neighbourhoodRelation[neighindex1][neighindex2] = true;
	}
	
	public boolean areNeighbours(int neighindex1, int neighindex2){
		return neighbourhoodRelation[neighindex1][neighindex2];
	}
	
	private void set_numberInstances(int _numberInstances) {
		this._numberInstances = _numberInstances;
	}

	private void initNeighbours(){
		int i,j;
		for (i=0;i<_numberInstances;i++){
			for(j=0;j<_numberInstances;j++){
				neighbourhoodRelation[i][j] = false;
			}
		}
		
	}
}
