package org.ginsim.service.tool.avatar.utils;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.terminal.ImageTerminal;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.colomoto.logicalmodel.io.avatar.AvatarUtils;
import org.ginsim.service.tool.avatar.domain.State;


/**
 * Plotting facilities using JavaPlot library
 * @author teras
 */
public class ChartGNUPlot {

	/**
	 * Creates an image from a given JavaPlot 
	 * @param p the JavaPlot to be plotted
	 * @return the associated image
	 */
	public static BufferedImage getImage(JavaPlot p){
		ImageTerminal png = new ImageTerminal();
	    p.setTerminal(png);
	    p.plot();
	    return png.getImage();
	}
	
	/**
	 * Saves a given image in a PNG file
	 * @param img the image to be saved
	 * @param file the PNG file where the image is to be saved
	 * @throws IOException
	 */
	public static void writePNGFile(BufferedImage img, File file) throws IOException{
		//System.out.println("File:"+new File("").getAbsolutePath()+file.toString());
        file.createNewFile();
		ImageIO.write(img, "png", file);
	}
	
	/**
	 * Creates a chart with statistics on the depth for a list of attractors
	 * @param depths the list of depths at which an attractor was found
	 * @param pointAttractors 
	 * @param title the title of the plot
	 * @param xaxis the x-axis label
	 * @param yaxis the y-axis label
	 * @return the associated plot with the mean depth and error bars associated with the list of attractors 
	 */
	public static JavaPlot getErrorBars(Map<String,List<Integer>> depths, Map<String,String> names, String title, String xaxis, String yaxis) {
        JavaPlot p = new JavaPlot();
        p.setTitle(title);
        p.setKey(JavaPlot.Key.TOP_RIGHT);
        PlotStyle myPlotStyle = new PlotStyle();
        myPlotStyle.setStyle(Style.CANDLESTICKS);
        myPlotStyle.setLineWidth(6);
        int k=0, it=0;
        double overallMax = 0;
        for(String att : depths.keySet()){
            double[][] datapoints = new double[1][6]; //new double[][]{{0,5,1,8,2,0.4},{1,4,1,8,2,0.4},{2,6,1,8,3,0.4}};
        	double mean=AvaMath.mean(depths.get(att)), std=AvaMath.std(depths.get(att));
        	double min=AvaMath.min(depths.get(att)), max=AvaMath.max(depths.get(att));
        	overallMax = Math.max(max,overallMax);
       		datapoints[k][0]=it++;
       		datapoints[k][1]=Math.max(min, mean-std);
       		datapoints[k][2]=min;
       		datapoints[k][3]=max;
       		datapoints[k][4]=Math.min(max, mean+std);
       		datapoints[k][5]=0.4;
            DataSetPlot s = new DataSetPlot(datapoints);
            s.setTitle(names.get(att));
            s.setPlotStyle(myPlotStyle);
            p.addPlot(s);
       	}
        p.getAxis("x").setLabel(xaxis);
        p.getAxis("y").setLabel(yaxis);
        p.getAxis("x").setBoundaries(-1,depths.size());
        p.getAxis("y").setBoundaries(0,overallMax*1.1+1);
        return p;
	}
	
	/**
	 * Creates a chart based on the convergence of probabilities per attractor
	 * @param dataset the evolution of probabilities per attractor as the number of iterations increases
	 * @param names 
	 * @param space number of iterations between two measured points
	 * @param title the title of the plot
	 * @param xaxis the x-axis label
	 * @param yaxis the y-axis label
	 * @return the associated plot with the convergence of probabilities per attractor across iterations 
	 */
	public static JavaPlot getConvergence(double[][] dataset, List<String> names, int space, String title, String xaxis, String yaxis) {
        JavaPlot p = new JavaPlot();
        p.setTitle(title);
        p.getAxis("x").setLabel(xaxis);
        p.getAxis("y").setLabel(yaxis);
        p.getAxis("y").setBoundaries(0,1);
        p.setKey(JavaPlot.Key.TOP_RIGHT);
        PlotStyle myPlotStyle = new PlotStyle();
        myPlotStyle.setStyle(Style.LINES);
        myPlotStyle.setLineWidth(3);
        for(int i=0, l=dataset.length; i<l; i++){
            double[][] datapoints = new double[dataset[i].length][2];
        	for(int j=0, l2=dataset[i].length; j<l2; j++){
        		datapoints[j][0]=j*space;
        		datapoints[j][1]=dataset[i][j];
        	}
            DataSetPlot s = new DataSetPlot(datapoints);
            s.setTitle(names.get(i));
            s.setPlotStyle(myPlotStyle);
            p.addPlot(s);
        }
        return p;
	}

	/**
	 * Creates a chart based on the progression of probabilities per state-set (F, N, A)
	 * @param progression the evolution of probabilities as the number of iterations increases
	 * @param title the title of the plot
	 * @param xaxis the x-axis label
	 * @param yaxis the y-axis label
	 * @return the associated plot with the progression of probabilities on state-sets across iterations 
	 */
	public static JavaPlot getProgression(List<double[]> progression, String title, String xaxis, String yaxis){
        JavaPlot p = new JavaPlot();
        p.setTitle(title);
        p.getAxis("x").setLabel(xaxis);
        p.getAxis("y").setLabel(yaxis);
        p.setKey(JavaPlot.Key.TOP_RIGHT);
        PlotStyle myPlotStyle = new PlotStyle();
        myPlotStyle.setStyle(Style.LINES);
        myPlotStyle.setLineWidth(3);

        for(int i=0; i<3; i++){
        	double[][] array = new double[progression.size()][2];
        	for(int k=0, l=progression.size(); k<l; k++){
        		array[k][0]=k;
        		array[k][1]=progression.get(k)[i];
        	}
            DataSetPlot s = new DataSetPlot(array);
            s.setTitle((i==0)?"F":((i==1)?"N":"A"));
            s.setPlotStyle(myPlotStyle);
            p.addPlot(s);
        }
        return p;
	}
}
