package windows;

import java.awt.Shape;

import javax.swing.JDialog;
import javax.swing.table.TableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import data.Student;

/**
 * A window that displays a scatter graph with student grades plotted on it.
 * @author Darren Middleton, Dovydas Rupsys
 */
public class ScatterGraph extends JDialog {
	private static final long serialVersionUID = 1L;
	private MainWindow _window;															//Stores the reference to the main window
	
	/**
	 * Generates the ScatterGraph
	 * @param comparisonChartName The Name of the Chart to be created.
	 * @param window Gets the object from the Mainwindow so it can be called upon in later methods.
	 */
    public ScatterGraph(String comparisonChartName, MainWindow window) {
    	super(window, comparisonChartName, true);
    	//If closed the program will still be running and close the scattergraph
    	setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    	//Assigns the window object to _window.
    	_window = window;
    	
    	//creates the scatter graph with student results
        JFreeChart comparisonChart = ChartFactory.createScatterPlot("", "Assessment Mark", "Mean Average", generateDataset());
        ChartPanel comparisonChartPanel = new ChartPanel(comparisonChart, true);
        comparisonChartPanel.setPreferredSize(new java.awt.Dimension(800, 800));
        setContentPane(comparisonChartPanel);
        
        //change the shape of data points to crosses
        XYPlot plot = comparisonChart.getXYPlot();
        XYItemRenderer renderer = plot.getRenderer();
        Shape cross = ShapeUtilities.createDiagonalCross(2.5f, 0.3f);
        renderer.setSeriesShape(0, cross);
    }
    
    /**
     * Generates dataset for the scatter graph.
     */
    private XYDataset generateDataset(){
    	XYSeries series = new XYSeries("Students");
    	
    	//Gets the selected table
    	TableModel model = _window.getSelectedTable().getTableModel();
    	for (int i = 0; i < model.getRowCount(); i++) {
    		double assessmentGrade = 0;
        	double averageGrade = 0;
    		
    		//gets the assessment grade from the table
    		try{
    			assessmentGrade = Float.parseFloat(model.getValueAt(i, 2).toString());
    		}
    		catch (NumberFormatException e){
    			System.out.println(e.getStackTrace());
    		}
    		
    		//gets the student name from the results table
        	String name = model.getValueAt(i, 0).toString();
    		
        	//looks for the student in the list of students
        	Student student = _window.findStudent(name);
    		if(student != null){
    			//calculates the average of all grades
    	    	float result = 0;
    	    	for(int x = 0; x<student.getStudentResultsSize(); x++){
    	    		result += student.getStudentResult(x).getMark();
    	    	}
    	    	averageGrade = result / student.getStudentResultsSize();
    		}
    		
    		//adds a data point to the series
    		series.add(assessmentGrade, averageGrade);
		}
    	
    	return new XYSeriesCollection(series);
    }
}

 
