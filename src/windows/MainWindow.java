package windows;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowEvent;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import studentdata.Connector;
import studentdata.DataTable;
import utility.ResultTabbedPane;
import data.Assessment;
import data.UnitParticipation;
import data.Result;
import data.Student;
import data.StudentResult;

/**
 * The main window of the application.
 * @author Dovydas Rupsys, Nashwan Nouri, Darren Middleton
 */
public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private ArrayList<Student> _students; 										//Contains all student objects
	private ArrayList<Assessment> _assessments; 								//Contains all loaded assessments
	public JList<Student> _jlsStudents; 										//Reference for the graphical student list
	private DefaultListModel<Student> _jlsModel; 								//Reference for the filtered student list
	private JTextField _txtListFilter; 											//Reference for the filter text field
	private StudentInfoWindow _wndPopUp = null;  								//Creates a reference to the info pop-up window
	private ResultTabbedPane _tpResult;
	
	// Creates mouse click event for the graphical student list
	private MouseAdapter _listListener = new MouseAdapter() {
		@Override
		public void mousePressed(java.awt.event.MouseEvent e) {
			// if there is nothing in the graphical list exit this method
			if (_jlsStudents.getSelectedValue() == null)
				return;

			// if window does not exist, make a new window
			if (_wndPopUp == null) {
				_wndPopUp = new StudentInfoWindow(_jlsStudents.getSelectedValue());
			} else {
				// if window already exists, destroy and make a new one
				_wndPopUp.dispose();
				_wndPopUp = new StudentInfoWindow(_jlsStudents.getSelectedValue());
			}
		}
	};

	// Creates A document listener for searching for students
	private DocumentListener _filterTextChangeEvent = new DocumentListener() {
		// Implements a method for each of the 3 document events
		@Override
		public void removeUpdate(DocumentEvent e) {
			updateStudentList();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			updateStudentList();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			updateStudentList();
		}
	};

	/**
	 * Checks if the users data, name or id, contains the filter passed.
	 * @param student Student that will be checked.
	 * @param pattern String that must be found within the student's data.
	 * @return true if pattern was found, false if it was not.
	 */
	private boolean studentDataMatchesString(Student student, String pattern) {
		return student.toString().toLowerCase().contains(pattern.toLowerCase());
	}

	/**
	 * Filters the students list, based on the input and updates JList
	 */
	private void updateStudentList() {
		// Clears the graphical list of student
		_jlsModel.clear();

		// Gets the filter of the text field
		String filter = _txtListFilter.getText();

		// Checks if any of the students have the filter as a substring
		for (int i = 0; i < _students.size(); i++) {
			if (studentDataMatchesString(_students.get(i), filter)) {
				_jlsModel.addElement(_students.get(i));
			}
		}
	}

	/**
	 * Creates a result and adds it to an assessment. In addition creates assessment class if that particular assessment doesn't exist the list of assessments provided.
	 * @param assessments A list of assessments created during this file loading operation.
	 * @param line processed data in the following format { module, assessment, code, mark, grade, name }
	 */
	private void createResult(ArrayList<Assessment> assessments, String[] line) {
		// Constants to aid readability
		final int MODULE = 0; final int ASSESSMENT = 1; final int CODE = 2; final int MARK = 3; final int GRADE = 4; final int NAME = 5;

		Assessment assessment = new Assessment(line[MODULE], line[ASSESSMENT]); // creates assessment with data on the line
		int index = assessments.indexOf(assessment); 							// checks if this type of assessment exists and gets its index

		// if index is more then -1 then this type of assessment exists
		if (index != -1)
			// get that assessment instead
			assessment = assessments.get(index);
		else
			// add this assessment to the list of assessments
			assessments.add(assessment);

		// Creates a result with data on this line
		Result result = new Result(line[GRADE], line[MARK], line[CODE]);
		result.setCandidateName(line[NAME]);

		// if this result already exists then ignore it
		if (!assessment.containsResult(result)) {
			assessment.addResult(result);
		}
	}
	
	/**
	 * Constructs the main window of the application.
	 */
	public MainWindow() {
		super("Window Name");													//Sets the title of the window
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);							//Terminates application whenever window close event is executed
		setSize(new Dimension(640, 480));										//Sets window's default size
		setJMenuBar(new MainWindowMenu(this));									//Assigns the main window menu bar to this window

		//Creates the panel for the left side of the window
		JPanel leftPanel = new JPanel(new BorderLayout());

		leftPanel.setPreferredSize(new Dimension(150, 0));
		
		//Creates a graphical list that displays student names and their ids
		_jlsModel = new DefaultListModel<Student>();
		_jlsStudents = new JList<Student>(_jlsModel);
		//Assigns an event that displays info window when an item in the graphical list of students is pressed
		_jlsStudents.addMouseListener(_listListener);
		
		//Creates a text fields used to filter students
		_txtListFilter = new JTextField(20);
		leftPanel.add(_txtListFilter, BorderLayout.NORTH);
		//Assigns a change event for the filter text field. 
		_txtListFilter.getDocument().addDocumentListener(_filterTextChangeEvent);
		
		//Wraps the scroll pane over the graphical list of students and adds it to the centre of the left pane
		leftPanel.add(new JScrollPane(_jlsStudents), BorderLayout.CENTER);

		//Adds the left panel to main window
		add(leftPanel, BorderLayout.WEST);
		
		//Instantiates the list of students and creates some temporary objects
		_students = new ArrayList<Student>();
		loadStudentData();
		
		//Instantiates the list that will hold assessment information
		_assessments = new ArrayList<Assessment>();
				
		//Creates and adds the tabbed pane to the window.
		_tpResult = new ResultTabbedPane(this);
		add(_tpResult, BorderLayout.CENTER);

		// Make the window visible
		setVisible(true);
	}

	/**
	 * Looks for a student in the list of student loaded from the interned.
	 * @param pattern String that must be part of the student's name or id.
	 * @return The first student's object that matches provided pattern.
	 */
	public Student findStudent(String pattern) {
		for (int i = 0; i < _students.size(); i++) {
			if (studentDataMatchesString(_students.get(i), pattern)) {
				return _students.get(i);
			}
		}

		return null;
	}
	
	/**
	 * Searches for a student by his email address.
	 * @param email Email address of the student.
	 * @return Returns the student object or null if no student was found.
	 */
	public Student findStudentByEmail(String email){
		for (int i = 0; i < _students.size(); i++) {
			if (_students.get(i).getEmail().equals(email)) {
				return _students.get(i);
			}
		}

		return null;
	}
	
	/**
	 * Removes the specified assessment from the list of assessments.
	 * @param assessment Assessment to be removed.
	 */
	public void removeAssessment(Assessment assessment){
		_assessments.remove(assessment);
	}
	
	/**
	 * Establishes connection to the server and loads student data into the
	 * graphical student list.
	 */
	public void loadStudentData() {
		// Object used to make a connection to the server
		Connector c = new Connector();

		// If connection is successful then
		if (c.connect("DDN", "2f39f5181c4edcb665856d25b68b7be3")) {
			// Get data from the server in a form of a spreadsheet
			DataTable data = c.getData();

			// An array that will store data of a single student
			String[] student = new String[4];

			// loops though the spreadsheet and creates student objects.
			for (int y = 0; y < data.getRowCount(); y++) {
				for (int x = 0; x < data.getColumnCount(); x++) {
					try {
						student[x] = new String(data.getCell(y, x).getBytes(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				_students.add(new Student(student[2], student[0], student[1], student[3]));
			}

			// Updates graphical student list.
			updateStudentList();
		}
		// if connection is not successful then
		else {
			// Display an informing message
			int dialogResult = JOptionPane.showConfirmDialog(
							this,
							"There was a problem loading student data from the server. Do you wish to continue?",
							"Warning",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE);

			// If user chooses to, terminate application
			if (dialogResult == JOptionPane.NO_OPTION)
				dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}

	/**
	 * Assigns anonymous codes to the users using the data that was provided.
	 * @param fileData Array list of student numbers and anonymous codes.
	 */
	public void loadAnonymousCodes(ArrayList<String[]> fileData) {
		// Constants to aid code readability
		final int STUDENT_NUMBER = 0;
		final int ANONYMOUS_CODE = 1;

		// Tracks all successful and unsuccessful code assignments.
		int importCount = 0;
		int missingCount = 0;

		// loops though the code data provided and...
		for (String[] data : fileData) {
			// Searches for user with specified student number
			Student student = findStudent(data[STUDENT_NUMBER]);
			// if such student is found then...
			if (student != null) {
				// assign anonymous code to that user and import count.
				student.addAnonymousCode(data[ANONYMOUS_CODE]);
				++importCount;
			}
			else {
				// otherwise increment failed import count.
				++missingCount;
			}
		}

		// Display message with import details.
		JOptionPane.showMessageDialog(
				this,
				"In total there were " + importCount + " codes successfully imported and " + missingCount + " codes that did not match any loaded student.",
				"Anonymous marking codes imported",
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Processes exam result data and creates assessments in the tabbed pane.
	 * @param data Unprocessed exam data read from the file.
	 */
	public void loadExamResults(ArrayList<String[]> data) {
		// Used to store mapped headers
		HashMap<String, Integer> headers = new HashMap<String, Integer>();

		// Gets the headers row from the data list
		String[] header = data.get(0);
		// Goes through every column in the header, removes unwanted characters and maps it to the index of the column
		for (int i = 0; i < header.length; i++) {
			String key = header[i].replaceAll("\"|#| ", "").toLowerCase();
			headers.put(key, i);
		}

		//variables that track results that have students associated as well as stray results
		int deanonymised = 0;
		int unidentified = 0;
		
		//This array will hold all assessments loaded from one file
		ArrayList<Assessment> assessments = new ArrayList<Assessment>();
		
		// Goes through the remaining rows
		for (int i = 1; i < data.size(); i++) {
			// gets the row
			String[] line = data.get(i);

			// removes unwanted characters from data and saves it
			String module = line[headers.get("module")].replaceAll("\"|#", "");
			String ass = line[headers.get("ass")].replaceAll("\"|#", "");
			String code = line[headers.get("candkey")].replaceAll("#|\"", "").split("/")[0];
			String mark = line[headers.get("mark")].replaceAll("\"|#", "");
			String grade = line[headers.get("grade")].replaceAll("\"|#", "");
			String name = "N/A";
			
			// tries to de-anonymises code
			for (int j = 0; j < _students.size(); j++) {
				Student s = _students.get(j);
				String id = s.deAnonymiseCode(code);

				//If student contained code specified then use student's id
				if (!id.equals("")) {
					code = id;
					name = s.getName();
					
					//Creates student's result object and adds it to the list of student's results.
					s.addResult(new StudentResult(module + " " + ass, mark, grade));
					
					++deanonymised;
					break;
				}
			}
			
			if (name.equals("N/A"))
				++unidentified;
			
			// Creates a result with processed data
			createResult(assessments, new String[] { module, ass, code, mark, grade, name });
		}
		
		//Adds loaded assessments to the list of all assessments
		_assessments.addAll(assessments);
		
		//creates a tab for every assessment
		for (int i = 0; i < assessments.size(); i++) {
			_tpResult.newResultTab(assessments.get(i));
		}
		
		// Display message with load details.
		JOptionPane.showMessageDialog(
				this,
				"In total there were " + deanonymised + " de-anonymised results and " + unidentified + " unidentified results.",
				"Results load information",
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Takes in a html file in a form of a string and extracts participation data from it.
	 * @param htmlTable raw table data in a form of html code.
	 */
	public void extractDataFromHtmlTable(String htmlTable){
		UnitParticipation participants = new UnitParticipation(
				JOptionPane.showInputDialog("Enter the name of the module."),
				htmlTable);
		
		participants.applyParticipantData(this);
	}
	
	/**
	 * Gets the list of students.
	 * @return students list.
	 */
	public ArrayList<Student> getStudents(){
		return _students;
	}
	
	/**
	 * Gets the tabbed panel.
	 * @return The tabbed panel of student results.
	 */
	public ResultTabbedPane getSelectedTable(){
		return _tpResult;
	}
}