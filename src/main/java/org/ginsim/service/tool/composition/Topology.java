package org.ginsim.service.tool.composition;

import org.ginsim.common.application.GsException;

public class Topology {

	private int _numberInstances = 0;
	private boolean neighbourhoodRelation[][];

	/*
	 * The topology
	 * 
	 * @param numberInstances the number of identical modules
	 */
	public Topology(int numberInstances) {
		this.set_numberInstances(numberInstances);
		initNeighbours();
	}

	public int getNumberInstances() {
		return _numberInstances;
	}

	/*
	 * @param neighindex1 indicates the index of the first module
	 * 
	 * @param neighindex2 indicates the index of the second module
	 */
	public void addNeighbour(int neighindex1, int neighindex2)
			throws GsException {
		if (neighindex1 >= _numberInstances || neighindex2 >= _numberInstances) {
			throw new GsException(GsException.GRAVITY_NORMAL,
					"Invalid neighbor index values: (" + neighindex1 + ","
							+ neighindex2 + ")");
		}
		neighbourhoodRelation[neighindex1][neighindex2] = true;
	}

	public boolean areNeighbours(int neighindex1, int neighindex2) {
		return neighbourhoodRelation[neighindex1][neighindex2];
	}

	public boolean hasNeighbours(int index) throws GsException {
		boolean result = false;
		if (index >= _numberInstances)
			throw new GsException(GsException.GRAVITY_NORMAL, "Invalid neighbor index value : (" + index +")");
		
		for (int i = 0; i < neighbourhoodRelation.length; i++) {
			if (neighbourhoodRelation[index][i] == true) {
				result = true;
				continue;
			}
		}
		return result;
	}

	private void set_numberInstances(int _numberInstances) {
		this._numberInstances = _numberInstances;
	}

	private void initNeighbours() {
		int i, j;
		neighbourhoodRelation = new boolean[_numberInstances][_numberInstances];
		for (i = 0; i < _numberInstances; i++) {
			for (j = 0; j < _numberInstances; j++) {
				neighbourhoodRelation[i][j] = false;
			}
		}

	}
}
