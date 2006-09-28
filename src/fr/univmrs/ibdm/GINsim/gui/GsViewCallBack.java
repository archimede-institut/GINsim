package fr.univmrs.ibdm.GINsim.gui;

import fr.univmrs.ibdm.GINsim.global.GsOptions;

/**
 * Here are the callback for entry in the "view" menu
 */
public class GsViewCallBack {
	
	private GsMainFrame main;
	
	/**
	 * simple constructor
	 * @param m
	 */
	public GsViewCallBack(GsMainFrame m) {
		main = m;
	}

	/**
	 * for now: simple zoom out
	 */
	public void zoomOut() {
	    main.getGraph().getGraphManager().zoomOut();
	}

	/**
	 * for now: simple zoom in
	 */
	public void zoomIn() {
	    main.getGraph().getGraphManager().zoomIn();
	}
	
	/**
	 * come back to the default zoom level
	 */
	public void normalSize() {
	    main.getGraph().getGraphManager().zoomNormal();
	}

	/**
	 * wether edges names must be displayed or not
	 * @param b
	 */
	public void displayEdgeName(boolean b) {
	    main.getGraph().getGraphManager().displayEdgeName(b);
        GsOptions.setOption("display.edgename", b?Boolean.TRUE:Boolean.FALSE);
	}

	/**
	 * @param b should the tool panel be saparated?
	 */
	protected void divideWindow(boolean b) {
		main.divideWindow(b);
        GsOptions.setOption("display.dividewindow", b?Boolean.TRUE:Boolean.FALSE);
	}

	/**
	 * display graph grip
	 * @param b
	 */
	protected void displayGrid(boolean b) {
	    main.getGraph().getGraphManager().showGrid(b);
        GsOptions.setOption("display.grid", b?Boolean.TRUE:Boolean.FALSE);
	}

    /**
     * make the grid (in)active.
     * @param b
     */
    public void gridActive(boolean b) {
        main.getGraph().getGraphManager().setGridActive(b);
        GsOptions.setOption("display.gridactive", b?Boolean.TRUE:Boolean.FALSE);
    }
    
	/**
	 * display navigation map
	 * @param b
	 */
	protected void displayMiniMap(boolean b) {
		main.showMiniMap(b);
        GsOptions.setOption("display.minimap", b?Boolean.TRUE:Boolean.FALSE);

	}

    /**
     * move all vertices to front
     * @param b
     */
    public void vertexToFront(boolean b) {
        main.getGraph().getGraphManager().vertexToFront(b);
    }

}
