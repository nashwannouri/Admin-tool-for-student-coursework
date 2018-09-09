package utility;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import windows.MainWindow;
import windows.StudentInfoWindow;
import data.Assessment;
import data.Student;

/**
 * Custom tabbed pane designed to store results table.
 * @author Dovydas Rupsys
 */
public class ResultTabbedPane extends JTabbedPane {
	private static final long serialVersionUID = 1L;
	
	MainWindow _wndMain;															//Holds a reference to the main window.
	
	// Creates mouse click event for the exam result table
	private MouseAdapter _showStudentInfoEvent = new MouseAdapter() {
		private StudentInfoWindow _wndPopUp = null;  								//Creates a reference to the info pop-up window
		
		@Override
		public void mousePressed(MouseEvent e) {
			//Reference to the table clicked
			JTable jtResults = (JTable) e.getComponent();
			
			//get cell that was clicked
			int row = jtResults.rowAtPoint(e.getPoint());
			int col = jtResults.columnAtPoint(e.getPoint());
			
			jtResults.getTableHeader();
			//if student is found, display info window
			Student student = _wndMain.findStudent((String)jtResults.getValueAt(row, col));
			if(col == 0 && student != null){
				if (_wndPopUp == null) {
					_wndPopUp = new StudentInfoWindow(student);
				}
				else {
					// if window already exists, destroy and make a new one
					_wndPopUp.dispose();
					_wndPopUp = new StudentInfoWindow(student);
				}
			}
		}
	};
	
	/**
	 * Gets the table model from a currently selected tabbed pane tab.
	 * @return Table Model of a table in a selected tab.
	 */
	public TableModel getTableModel(){
		JPanel panel = (JPanel)getSelectedComponent();
		JScrollPane scrollPane = (JScrollPane)panel.getComponent(0);
		JTable table = (JTable)scrollPane.getViewport().getComponent(0);
		
		return table.getModel();
	}

	/**
	 * Constructs the tabbed pane.
	 * @param window A reference to the main window object.
	 */
	public ResultTabbedPane(MainWindow window) {
		super();
		_wndMain = window;
	}
	
	/**
	 * Creates a new tab on the tabbed pane with assessment table.
	 * @param assessment Assessment data to be loaded in the table.
	 */
	public void newResultTab(final Assessment assessment){
		final JPanel tabPanel = new JPanel(new BorderLayout());
		
		//create un-editable table
		JTable tabTable = new JTable(new DefaultTableModel(assessment.dataArray(), assessment.columnNamesArray()){
			private static final long serialVersionUID = 1L;
			
		    @Override
		    public boolean isCellEditable(int row, int column) {
		        return false;
		    }
		});
		
		//sets default table parameters and assigns event listener
		tabTable.getTableHeader().setReorderingAllowed(false);
		tabTable.getColumnModel().getColumn(0).setMinWidth(100);
		tabTable.addMouseListener(_showStudentInfoEvent);
		
		//wrap scroll panel around table
		JScrollPane resultscroll = new JScrollPane(tabTable);
		tabPanel.add(resultscroll);
		
		//create title panel that contains exit button and title
		JPanel titlePanel= new JPanel();
		JButton btnClose = new JButton("x");
		JLabel title = new JLabel(assessment.toString());
		titlePanel.add(title, BorderLayout.EAST);
		titlePanel.add(btnClose, BorderLayout.WEST);
		
		//closes the selected tab
		btnClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_wndMain.removeAssessment(assessment);
				ResultTabbedPane.this.remove(tabPanel);
			}
		});
		
		//creates a tab and adds a title panel which contains and a title label and exit button
		addTab(assessment.toString(), tabPanel);
		int index = indexOfComponent(tabPanel);
		setTabComponentAt(index, titlePanel);
	}

}
