package windows;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.jfree.ui.RefineryUtilities;

import utility.CreatePDF;

/**
 * The menu bar that constructs all menus for the main window.
 * @author Dovydas Rupsys, Nashwan Nouri
 */
public class MainWindowMenu extends JMenuBar {
	private static final long serialVersionUID = 1L;
	private MainWindow _window; 						// will be used to access main window methods
	private BrowserWindow _browser; 					// Stores a reference to the browser window

	// Event handler designed for the Load Anonymous Marking Codes menu button
	private ActionListener _btnLoadAnonymousCodesEvent = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			// shows a file dialog window and gets selected path
			String path = showFileDialog();

			// if path is null then no file was selected, so exit method
			if (path == null)
				return;

			// Reads data in the file and passes that data to the main window.
			ArrayList<String[]> data = readFile(new File(path));
			_window.loadAnonymousCodes(data);
		}
	};

	//event handler designed for the the Compare to Average menu button
	private ActionListener _compareMarksListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			//generates a scatterGraph
			try {
				final ScatterGraph comparison = new ScatterGraph(
						"Comparison of marks", _window);
				comparison.pack();
				RefineryUtilities.centerFrameOnScreen(comparison);
				comparison.setVisible(true);
			}
			//displays a message if no exams are loaded
			catch (Exception e2) {
				{
					JOptionPane.showMessageDialog(
							_window,
							"Students do not have any results assigned to them. Try to load results first",
							"Information",
							JOptionPane.INFORMATION_MESSAGE);
					dispatchEvent(new WindowEvent(_window, WindowEvent.WINDOW_CLOSING));
				}
			}
		
		}
	};

	//event handler for the Load Exam Results menu button
	private ActionListener _btnLoadExamResultsEvent = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			// shows a file dialog window and gets selected path
			String path = showFileDialog();

			// if path is null then no file was selected, so exit method
			if (path == null)
				return;

			// Reads data in the file and passes that data to the main window.
			ArrayList<String[]> data = readFile(new File(path));
			_window.loadExamResults(data);
		}
	};
	
	//event handler for the Create PDF menu button
	private ActionListener 	_btnCreatePdfEvent = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			new CreatePDF(_window);
		}
	};
	
	//event handler for the Email to Students menu button
	private ActionListener _btnEmailEvent = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			new EmailWindow(_window);
		}
	};

	//event handler for the Email Settings menu button
	private ActionListener _btnEmailSettingsEvent = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			new EmailSettingsWindow(_window);
		}
	};

	//event handler for the Fetch Participation menu button
	private ActionListener _btnScrapeUrlEvent = new ActionListener() {
		@Override
		// Shows the browser window when scrape menu button is pressed
		public void actionPerformed(ActionEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					_browser.setVisible(true);
				}
			});
		}
	};

	/**
	 * Shows the file dialog window.
	 * @return Returns the path of the selected file or null if no file was selected.
	 */
	private String showFileDialog() {
		// creates a window to select a .CSV file
		FileDialog fileDialog = new FileDialog(_window, "Load a CSV file",
				FileDialog.LOAD);
		fileDialog.setFile("*.CSV");
		fileDialog.setDirectory("C:");
		fileDialog.setVisible(true);

		// Gets the file name and the path to the file
		String dir = fileDialog.getDirectory();
		String file = fileDialog.getFile();

		// If no file was selected return false
		if (dir == null || file == null)
			return null;
		else
			return dir + file;
	}

	/**
	 * Reads file line by line and stores each line in a list of string.
	 * @param file File to be read.
	 * @return Returns an array list with lines from the file.
	 */
	private ArrayList<String[]> readFile(File file) {
		// Stores data loaded from a file
		ArrayList<String[]> data = new ArrayList<String[]>();

		try {
			// Prepares file for loading
			FileReader reader = new FileReader(file);
			BufferedReader br = new BufferedReader(reader);

			// Reads in the first line
			String line = br.readLine();

			// Loop while there is a line
			while (line != null) {
				// Split the line into array and add that array to the list
				data.add(line.split(","));

				// Read in next line
				line = br.readLine();
			}

			// Releases file
			br.close();
			reader.close();
		} catch (IOException x) {
			x.printStackTrace();
		}

		return data;
	}

	/**
	 * Constructs the menu bar for the main window.
	 * @param window Reference to the main window.
	 */
	public MainWindowMenu(MainWindow window) {
		super(); 										// calls super constructor
		_window = window; 								// saves reference to the main window
		_browser = new BrowserWindow(_window); 			// creates an instance of a browser window

		// creates file menu
		JMenu file = new JMenu("File");
		add(file);

		// creates and adds an 'Load Anonymous Marking Codes' button to the file menu
		JMenuItem loadAnonymous = new JMenuItem("Load Anonymous Marking Codes");
		loadAnonymous.addActionListener(_btnLoadAnonymousCodesEvent);
		file.add(loadAnonymous);

		// creates and adds a menu button for loading exam results
		JMenuItem LoadExamResults = new JMenuItem("Load Exam Results");
		LoadExamResults.addActionListener(_btnLoadExamResultsEvent);
		file.add(LoadExamResults);
		
		file.addSeparator();
		
		// creates and adds export to pdf button to the file menu
		JMenuItem exportPdf = new JMenuItem("Export to PDF");
		exportPdf.addActionListener(_btnCreatePdfEvent);
		file.add(exportPdf);

		// Creates Data menu
		JMenu data = new JMenu("Data");
		add(data);

		// creates compare to average button to data
		JMenuItem compare = new JMenuItem("Compare to Average");
		data.add(compare);
		compare.addActionListener(_compareMarksListener);
		
		//Creates and adds scrape url menu button
		JMenuItem scrapeUrl = new JMenuItem("Fetch Participation");
		scrapeUrl.addActionListener(_btnScrapeUrlEvent);
		data.add(scrapeUrl);

		// Creates a email to students option under the data menu
		JMenuItem email = new JMenuItem("Email to Students");
		email.addActionListener(_btnEmailEvent);
		data.add(email);

		// Creates settings menu
		JMenu settings = new JMenu("Settings");
		add(settings);

		// Creates a email to students option under the data menu
		JMenuItem emailSettings = new JMenuItem("Email Settings");
		emailSettings.addActionListener(_btnEmailSettingsEvent);
		settings.add(emailSettings);
	}
}