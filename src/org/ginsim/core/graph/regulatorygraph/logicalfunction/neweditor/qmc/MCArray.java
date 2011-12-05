package org.ginsim.core.graph.regulatorygraph.logicalfunction.neweditor.qmc;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

public class MCArray {
	private Vector parameters, baseParameters;
	private boolean[][] array;
	private int nbCol;

	public MCArray(Vector pt, int col) {
		super();
		int row = 0;
		LogicalParameter lp;
		parameters = pt;
		nbCol = col;
		array = new boolean[parameters.size()][nbCol];
		for (Enumeration enu = pt.elements(); enu.hasMoreElements(); row++) {
			Arrays.fill(array[row], false);
			lp = (LogicalParameter)enu.nextElement();
			for (int i = 0; i < nbCol; i++) array[row][i] = lp.getId().get(i);
		}
	}
	public void run() {
		boolean[] b = new boolean[nbCol];
		Vector v;
		int row, red0 = 0, red;

		baseParameters = new Vector();
		do {
			v = getBaseRows();
			Arrays.fill(b, false);
			for (Enumeration enu = v.elements(); enu.hasMoreElements(); ) {
				row = ((Integer)enu.nextElement()).intValue();
				baseParameters.addElement(parameters.get(row));
				for (int i = 0; i < nbCol; i++) b[i] |= array[row][i];
			}
			for (int r = 0; r < parameters.size(); r++)
				for (int c = 0; c < nbCol; c++)
					array[r][c] = b[c] ? false : array[r][c];
			red = clearRedundancy();
			if (red == red0) {
				baseParameters.clear();
				break;
			}
			red0 = red;
		}
		while (red < parameters.size());
	}
	public String getString() {
		StringBuffer s = new StringBuffer("");
		for (Enumeration enu = baseParameters.elements(); enu.hasMoreElements(); ) {
			s.append(((LogicalParameter)enu.nextElement()).getString());
			s.append("\n");
		}
		return s.toString();
	}
	private Vector getBaseRows() {
		Vector v = new Vector();
		int n, ix;

		for (int c = 0; c < nbCol; c++) {
			n = ix = 0;
			for (int r = 0; r < parameters.size(); r++)
				if (array[r][c]) {
					if (++n == 2) break;
					ix = r;
				}
			if (n == 1) if (!v.contains(new Integer(ix))) v.addElement(new Integer(ix));
		}
		return v;
	}
	private int clearRedundancy() {
		int nz = 0, r, r2, c;
		boolean b, b2;

		for (r = 0; r < parameters.size(); r++) {
			b2 = false;
			for (c = 0; c < nbCol; c++) b2 |= array[r][c];
			if (!b2)
				nz++;
			else
				for (r2 = 0; r2 < parameters.size(); r2++) {
					if (r != r2) {
						b = true;
						for (c = 0; c < nbCol; c++) {
							b = array[r][c] && !array[r2][c];
							if (b) break;
						}
						if (!b) {
							nz++;
							for (c =0; c < nbCol; c++) array[r][c] = false;
							break;
						}
					}
				}
		}
		return nz;
	}
	public Vector getBaseParameters() {
		return baseParameters;
	}
}
