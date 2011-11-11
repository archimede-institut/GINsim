package fr.univmrs.tagc.GINsim.gui;

import fr.univmrs.tagc.common.OptionStore;

/**
 * Here are the callback for entry in the "view" menu
 * TODO: delete it. A brand new view menu manager is in the new architecture
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
	    //main.getGraph().getGraphManager().zoomOut();
	}

	/**
	 * for now: simple zoom in
	 */
	public void zoomIn() {
	    //main.getGraph().getGraphManager().zoomIn();
	}
	
	/**
	 * come back to the default zoom level
	 */
	public void normalSize() {
	    //main.getGraph().getGraphManager().zoomNormal();
	}

	/**
	 * wether edges names must be displayed or not
	 * @param b
	 */
	public void displayEdgeName(boolean b) {
	    //main.getGraph().getGraphManager().displayEdgeName(b);
        OptionStore.setOption("display.edgename", b?Boolean.TRUE:Boolean.FALSE);
	}

	/**
	 * @param b should the tool panel be saparated?
	 */
	protected void divideWindow(boolean b) {
		main.divideWindow(b);
        OptionStore.setOption("display.dividewindow", b?Boolean.TRUE:Boolean.FALSE);
	}

	/**
	 * display graph grip
	 * @param b
	 */
	protected void displayGrid(boolean b) {
	    //main.getGraph().getGraphManager().showGrid(b);
        OptionStore.setOption("display.grid", b?Boolean.TRUE:Boolean.FALSE);
	}

    /**
     * make the grid (in)active.
     * @param b
     */
    public void gridActive(boolean b) {
        //main.getGraph().getGraphManager().setGridActive(b);
        OptionStore.setOption("display.gridactive", b?Boolean.TRUE:Boolean.FALSE);
    }
    
	/**
	 * display navigation map
	 * @param b
	 */
	protected void displayMiniMap(boolean b) {
		main.showMiniMap(b);
        OptionStore.setOption("display.minimap", b?Boolean.TRUE:Boolean.FALSE);
	}

    /**
     * move all vertices to front
     * @param b
     */
    public void vertexToFront(boolean b) {
        //main.getGraph().getGraphManager().vertexToFront(b);
    }

}
