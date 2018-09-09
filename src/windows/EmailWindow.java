package windows;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import utility.CheckboxListRenderer;
import utility.EmailHost;
import utility.EmailSettings;
import data.Student;
import data.StudentResult;

/**
 * A window designed to write and send results to students.
 * @author Dovydas Rupsys
 */
public class EmailWindow extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private MainWindow _window;												//reference to the main window
	private DefaultListModel<JCheckBox> _checkboxListModel;					//reference to the items in the list box
	private JList<JCheckBox> _checkboxList;									//reference to the list box of students
	private JPanel _contentPanel;											//the panel that contains the content, email or the preview panel
	private JPanel _emailPanel;												//contains components that allow to write header and footer
	private JPanel _previewPanel;											//displays a preview of the email
	private JTextArea _jtaHeader;											//reference to the header text area
	private JTextArea _jtaFooter;											//reference to the footer text area
	private JTextArea _jtaPreview;											//reference to the preview text area
	private DefaultBoundedRangeModel _jpbProgressBarModel;					//the model used to update the email progress bar
	
	//button event that selects all items in the list of students
	private ActionListener _btnSelectAllEvent = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			int start = 0;
			int end = _checkboxListModel.getSize() - 1;
			if (end >= 0)
				_checkboxList.setSelectionInterval(start, end);
		}
	};
	
	//button event that unselects all items in the list of students
	private ActionListener _btnSelectNoneEvent = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			int start = 0;
			int end = _checkboxListModel.getSize() - 1;
			if (end >= 0)
				_checkboxList.removeSelectionInterval(start, end);
		}
	};
	
	//button event that displays the preview panel in the content area
	private ActionListener _btnEmailNextEvent = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			//displays an informing message if no student was selected in the list of students
			if (_checkboxList.getSelectedIndices().length == 0){
				JOptionPane.showMessageDialog(
						EmailWindow.this, 
						"No students are selected in the list of students.",
						"Information", 
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			//creates the preview text
			Student firstSelectedStudent = _window.findStudent(_checkboxListModel.get(_checkboxList.getSelectedIndices()[0]).getText());
			_jtaPreview.setText(createEmailMessage(firstSelectedStudent));
			
			//displays the preview panel in the content area
			displayContentPanel(_previewPanel);
			_checkboxList.setEnabled(false);
		}
	};
	
	//button event that displays the email panel in the content area
	private ActionListener _btnPreviewPreviousEvent = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			displayContentPanel(_emailPanel);
			_checkboxList.setEnabled(true);
		}
	};
	
	//button event that sends email to all selected students
	private ActionListener _btnPreviewSendEvent = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			Runnable runner = new Runnable(){
				@Override
				public void run() {
					//loads email settings from file
					EmailSettings settings = EmailSettings.loadFromFile(EmailSettings.DEFAULT_PATH);
					
					//if settings loaded are not default settings then
					if (settings.isValid()){
						int[] selectedItems = _checkboxList.getSelectedIndices();
						
						//Creates and displays a custom dialog window password text field
						JPanel inputPanel = new JPanel();
						JLabel lblPassword = new JLabel("Enter your email password: ");
						inputPanel.add(lblPassword);
						JPasswordField jpfPassword = new JPasswordField(10);
						inputPanel.add(jpfPassword);
						int dialogResult = JOptionPane.showOptionDialog(
								EmailWindow.this, 
								inputPanel, 
								"Password Required", 
								JOptionPane.OK_CANCEL_OPTION, 
								JOptionPane.WARNING_MESSAGE, null, null, JOptionPane.CANCEL_OPTION);
						
						//if password window was cancelled then exit this method
						if (JOptionPane.OK_OPTION != dialogResult)
							return;
						
						//create an email object with settings loaded and password provided.
						EmailHost email = null;
						try {
							email = new EmailHost(settings, new String(jpfPassword.getPassword()));
						} catch (MessagingException e) {
							JOptionPane.showMessageDialog(
									EmailWindow.this, 
									"There was an error connecting to the host, error received: " + e.getMessage(),
									"Error",
									JOptionPane.ERROR_MESSAGE);
						}
					
						//if email object failed to instantiate quit this method
						if (email == null)
							return;

						int failedEmails = 0;
						
						_jpbProgressBarModel.setMaximum(selectedItems.length);
						//send results to every selected student
						for (int i = 0; i < selectedItems.length; i++) {
							//gets the student's object
							Student student = _window.findStudent(_checkboxListModel.get(selectedItems[i]).getText());
							String message = createEmailMessage(student);
							
							try {
								email.sendEmail(student.getEmail(), "Assessment Results", message);
							} catch (UnsupportedEncodingException | MessagingException e) {
								JOptionPane.showMessageDialog(
										EmailWindow.this, 
										"There was an error sending this email: " + student.getEmail() + "\nError message received: " + e.getMessage(),
										"Error",
										JOptionPane.ERROR_MESSAGE);
								++failedEmails;
							}
							
							_jpbProgressBarModel.setValue(i+1);
						}
					
						JOptionPane.showMessageDialog(
								EmailWindow.this, 
								(selectedItems.length - failedEmails) + " emails sent successfully, " + failedEmails + " were not sent.");
						dispatchEvent(new WindowEvent(EmailWindow.this, WindowEvent.WINDOW_CLOSING));
					}
					else{
						//if settings loaded are default settings then display an informing message
						JOptionPane.showMessageDialog(
								EmailWindow.this, 
								"Email settings are missing. Open email settings window and provide your email settings.",
								"Missing Email Settings",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			};
			new Thread(runner).start();
		}
	};
	
	/**
	 * Constructs the message that is displayed in the preview text area.
	 * @param selectedItem An index of a selected student in the list of students.
	 * @return A string that contains all grade information.
	 */
	private String createEmailMessage(Student student){
		//creates results string
		String resultsString = "Results for student: " + student + "\n";
		for (int i = 0; i < student.getStudentResultsSize(); i++) {
			StudentResult result = student.getStudentResult(i);
			resultsString +=  "\tAssessment: " + result.getAssessmentName() + " - Result Achieved: " + result.getMark() + "%";
			
			if (i < student.getStudentResultsSize() - 1)
				resultsString += "\n";
		}
		
		return _jtaHeader.getText() + "\n\n" + resultsString + "\n\n" + _jtaFooter.getText();
	}
	
	/**
	 * Constructs the layout of the student checkboxes list. 
	 * @param students A list of all student currently loaded.
	 */
	private void initStudentListPanel(ArrayList<Student> students) {
		//Container for the student list components 
		JPanel studentListPanel = new JPanel(new BorderLayout());
		studentListPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
		add(studentListPanel, BorderLayout.WEST);
		
		//creates select all and select none buttons at the top of the students' list
		JPanel studentListTopPanel = new JPanel(new BorderLayout());
		studentListPanel.add(studentListTopPanel, BorderLayout.NORTH);
		
		JButton selectAll = new JButton("Select All");
		selectAll.addActionListener(_btnSelectAllEvent);
		studentListTopPanel.add(selectAll, BorderLayout.WEST);
		
		JButton selectNone = new JButton("Select None");
		selectNone.addActionListener(_btnSelectNoneEvent);
		studentListTopPanel.add(selectNone, BorderLayout.EAST);
		
		//creates student list with checkboxes
		_checkboxListModel = new DefaultListModel<JCheckBox>();
		_checkboxList = new JList<JCheckBox>(_checkboxListModel);
		_checkboxList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		//custom selection model that allows multiple item selection without holding down ctrl 
		_checkboxList.setSelectionModel(new DefaultListSelectionModel() {
			private static final long serialVersionUID = 1L;

			@Override
		    public void setSelectionInterval(int start, int end) {
		        if(super.isSelectedIndex(start) && start == end) {
		            super.removeSelectionInterval(start, end);
		        }
		        else {
		            super.addSelectionInterval(start, end);
		        }
		    }
		});
		
		_checkboxList.setCellRenderer(new CheckboxListRenderer());
		JScrollPane scrollPane = new JScrollPane(_checkboxList);
		scrollPane.setPreferredSize(new Dimension(200, 0));
		scrollPane.setBorder(new EmptyBorder(2, 0, 0, 0));
		studentListPanel.add(scrollPane, BorderLayout.CENTER);
		
		//adds a student to the list of students if that student has any grades associated
		for (int i = 0; i < students.size(); i++) {
			if (students.get(i).getStudentResultsSize() == 0)
				continue;
			
			//creates a checklist with the name of the student and adds it to the list of students.
			_checkboxListModel.addElement(new JCheckBox(students.get(i).toString()));
		}
	}
	
	/**
	 * Constructs the layout of the email panel.
	 */
	private void initEmailPanel() {
		//container for the contentArea and the buttons panel
		_emailPanel = new JPanel(new BorderLayout());
		_emailPanel.setBorder(new EmptyBorder(2, 0, 2, 2));

		//container for the header and footer
		JPanel headerAndFooterPanel = new JPanel(new GridLayout(2, 1));
		_emailPanel.add(headerAndFooterPanel, BorderLayout.CENTER);
		
		//creates header components
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBorder(new EmptyBorder(0, 0, 2, 0));
		headerAndFooterPanel.add(headerPanel);
		
		JLabel lblHeader = new JLabel("Header:");
		headerPanel.add(lblHeader, BorderLayout.NORTH);
		
		_jtaHeader = new JTextArea();
		_jtaHeader.setTabSize(4);
		JScrollPane jspHeader = new JScrollPane(_jtaHeader);
		headerPanel.add(jspHeader, BorderLayout.CENTER);
		
		//creates footer components
		JPanel footerPanel = new JPanel(new BorderLayout());
		footerPanel.setBorder(new EmptyBorder(0, 0, 2, 0));
		headerAndFooterPanel.add(footerPanel);
		
		JLabel lblFooter = new JLabel("Footer:");
		footerPanel.add(lblFooter, BorderLayout.NORTH);
		
		_jtaFooter = new JTextArea();
		_jtaFooter.setTabSize(4);
		JScrollPane jspFooter = new JScrollPane(_jtaFooter);
		footerPanel.add(jspFooter, BorderLayout.CENTER);
		
		//creates button panel components
		JPanel controlPanel = new JPanel(new BorderLayout());
		_emailPanel.add(controlPanel, BorderLayout.SOUTH);
		
		JButton btnNext = new JButton("Next");
		btnNext.addActionListener(_btnEmailNextEvent);
		controlPanel.add(btnNext, BorderLayout.EAST);
	}
	
	/**
	 * Constructs the layout of the preview panel.
	 */
	private void initPreviewPanel(){
		//container for the contentArea and the buttons panel
		_previewPanel = new JPanel(new BorderLayout());
		_previewPanel.setBorder(new EmptyBorder(2, 0, 2, 2));
		
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(0, 0, 2, 0));
		_previewPanel.add(contentPanel, BorderLayout.CENTER);
		
		JLabel lblPreview = new JLabel("Preview:");
		contentPanel.add(lblPreview, BorderLayout.NORTH);
		
		_jtaPreview = new JTextArea();
		_jtaPreview.setFocusable(false);
		_jtaPreview.setTabSize(4);
		JScrollPane jspPreview = new JScrollPane(_jtaPreview);
		contentPanel.add(jspPreview, BorderLayout.CENTER);
		
		//creates button panel components
		JPanel controlPanel = new JPanel(new BorderLayout());
		_previewPanel.add(controlPanel, BorderLayout.SOUTH);
		
		JButton btnPrevious = new JButton("Previous");
		btnPrevious.addActionListener(_btnPreviewPreviousEvent);
		controlPanel.add(btnPrevious, BorderLayout.WEST);
		
		_jpbProgressBarModel = new DefaultBoundedRangeModel();
		JProgressBar progress = new JProgressBar(_jpbProgressBarModel);
		controlPanel.add(progress, BorderLayout.CENTER);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(_btnPreviewSendEvent);
		controlPanel.add(btnSend, BorderLayout.EAST);
	}
	
	/**
	 * Displays the specified panel in the content area of the window.
	 * @param panel Panel to be displayed.
	 */
	private void displayContentPanel(JPanel panel){
		_contentPanel.removeAll();
		_contentPanel.add(panel, BorderLayout.CENTER);
		_contentPanel.revalidate();
		_contentPanel.repaint();
	}
	
	/**
	 * Constructs email window object.
	 * @param window A reference to the main program window.
	 */
	public EmailWindow(MainWindow window) {
		super(window, "Email to Students", true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(800, 600);
		_window = window;
		
		//initialise component panels
		initStudentListPanel(_window.getStudents());
		initEmailPanel();
		initPreviewPanel();
		
		//display initial content panel which is email panel
		_contentPanel = new JPanel(new BorderLayout());
		add(_contentPanel, BorderLayout.CENTER);
		displayContentPanel(_emailPanel);

		//Show window only if there are students with results assigned
		if (_checkboxListModel.getSize() == 0){
			JOptionPane.showMessageDialog(
					this,
					"Students do not have any results assigned to them. Try to load results first",
					"Information",
					JOptionPane.INFORMATION_MESSAGE);
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
		else{
			setVisible(true);
		}
	}
}
