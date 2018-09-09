package windows;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import data.Student;
import data.StudentResult;
import data.UnitVisit;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * The pop-up window class used to display student info when a student is clicked in the graphical list.
 * @author Nashwan Nouri, Dovydas Rupsys
 */
public class StudentInfoWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	private Student _student;												//Student assigned to this window
	
	/**
	 * Creates the top panel of the window.
	 * @param panel A panel for the top area of the window.
	 */
	private void createTopPanel(JPanel panel){
		//Panel containing student name and email
		JPanel topPanel = new JPanel(new GridLayout(2, 1));
		panel.add(topPanel, BorderLayout.NORTH);
		
		//Creates Label containing student name
		JLabel nameLabel = new JLabel(_student.getName(), SwingConstants.CENTER);
		nameLabel.setFont(nameLabel.getFont().deriveFont(40f));	
		topPanel.add(nameLabel);
		
		//Creates Label containing student email 
		JLabel emailLabel = new JLabel("<html><i>"+_student.getEmail()+"</i></html>", SwingConstants.CENTER);
		emailLabel.setFont(emailLabel.getFont().deriveFont(23f));
		topPanel.add(emailLabel);
	}
	
	/**
	 * Creates the middle area of the window
	 * @param panel A panel for the top area of the window.
	 */
	private void createMiddlePanel(JPanel panel){
		//Panel containing Student numbers tutor name
		JPanel middlePanel = new JPanel(new BorderLayout());
		panel.add(middlePanel, BorderLayout.CENTER);
		
		JPanel westOfMiddle = new JPanel(new GridLayout(2, 1));
		westOfMiddle.setBorder(new EmptyBorder(1, 1, 1, 5));
		middlePanel.add(westOfMiddle, BorderLayout.WEST);
		
		//Creates Label containing "Student No.:"
		JLabel numberLabel = new JLabel("Student No:");	
		westOfMiddle.add(numberLabel);
		//Creates Label containing "Tutor:"
		JLabel tutorLabel = new JLabel("tutor:");
		westOfMiddle.add(tutorLabel);
		
		JPanel centerOfMiddle = new JPanel(new GridLayout(2, 1));
		centerOfMiddle.setBorder(new EmptyBorder(1, 5, 1, 1));
		middlePanel.add(centerOfMiddle, BorderLayout.CENTER);
		
		//Creates Label containing student number
		JLabel numberLabel1 = new JLabel("" + _student.getNumber());
		centerOfMiddle.add(numberLabel1);
		//Creates Label containing tutor name
		JLabel tutorLabel1 = new JLabel(_student.getTutor());
		centerOfMiddle.add(tutorLabel1);
	}
	
	/**
	 * Creates the graphical list for the results of the student.
	 * @param bottomPanel A panel for the bottom area of the window.
	 */
	private void createResultsList(JPanel bottomPanel){
		JPanel results = new JPanel(new BorderLayout());
		results.setBorder(new EmptyBorder(5, 1, 1, 1));
		bottomPanel.add(results);
		
		//creates a label for the list
		JLabel resultsLabel = new JLabel("Results:");
		results.add(resultsLabel, BorderLayout.NORTH);
		
		//Creates Student results list
		DefaultListModel<StudentResult> jlsStudentResultModel = new DefaultListModel<StudentResult>();
		JList<StudentResult> jlsStudentResultList = new JList<StudentResult>(jlsStudentResultModel);
		JScrollPane jscStudentResult = new JScrollPane(jlsStudentResultList);
		results.add(jscStudentResult, BorderLayout.CENTER);
		
		//loads student results to the results list
		jlsStudentResultModel.clear();
		for (int i = 0; i < _student.getStudentResultsSize(); i++) {
			jlsStudentResultModel.addElement(_student.getStudentResult(i));
		}
	}
	
	/**
	 * Creates the graphical list for the student participation data.
	 * @param bottomPanel A panel for the bottom area of the window.
	 */
	private void createUnitVisitsList(JPanel bottomPanel){
		JPanel visits = new JPanel(new BorderLayout());
		visits.setBorder(new EmptyBorder(5, 1, 1, 1));
		bottomPanel.add(visits);
		
		//creates a label for the list
		JLabel visitsLabel = new JLabel("Last Unit Visits:");
		visits.add(visitsLabel, BorderLayout.NORTH);
		
		//Creates Student results list
		DefaultListModel<UnitVisit> jlsVisitsModel = new DefaultListModel<UnitVisit>();
		JList<UnitVisit> jlsVisitsList = new JList<UnitVisit>(jlsVisitsModel);
		JScrollPane jscVisits = new JScrollPane(jlsVisitsList);
		visits.add(jscVisits, BorderLayout.CENTER);
		
		//loads student results to the results list
		jlsVisitsModel.clear();
		for (int i = 0; i < _student.getUnitVisitedCount(); i++) {
			jlsVisitsModel.addElement(_student.getUnitVisited(i));
		}
	}
	
	/**
	 * Creates the pop-up window using information in the student argument.
	 * @param student Student who's info needs to be displayed.
	 */
	public StudentInfoWindow(Student student) {
		super();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		setLayout(new BorderLayout());
		((JPanel)getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
		_student = student;
		
		JPanel topPanel = new JPanel(new BorderLayout());
		add(topPanel, BorderLayout.NORTH);
		
		//instantiates top and middle panels
		createTopPanel(topPanel);
		createMiddlePanel(topPanel);
		
		//Stores the list of results and last visits
		JPanel bottomPanel = new JPanel(new GridLayout(1, 2));
		add(bottomPanel, BorderLayout.CENTER);
		createResultsList(bottomPanel);
		createUnitVisitsList(bottomPanel);
		
		this.pack();
		this.setVisible(true);
	}
}
	