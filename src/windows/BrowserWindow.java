package windows;

import static javafx.concurrent.Worker.State.FAILED;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 * Creates a JavaFX browser window. This window is used to navigate to the right web page which then can be scraped for participant data.
 * @author Dovydas Rupsys
 */
public class BrowserWindow extends JDialog{
	private static final long serialVersionUID = 1L;
	
	private MainWindow _window;									//reference to the main program window
	
	private JFXPanel _browser = new JFXPanel();					//JavaFX panel used to store the browser fx component
	private WebEngine _engine;									//the engine of the browser component
	private String _defaultPage;								//default page to be loaded
	
	private JTextField _txtUrl;									//URL text field at the top of the window
	private JButton _btnLoad;									//load button at the top of the window
	private JProgressBar _pbrLoading;							//loading bar at the bottom of the window
	private JButton _btnScrape;									//scrape button at the bottom of the window
	
	//Loads default page when window is hidden
	private ComponentAdapter _windowHiddenEvent = new ComponentAdapter() {
		@Override
		public void componentHidden(ComponentEvent e) {
			loadPage();
		}
	};
	
	//updates the loading bar when page is being loaded
	private ChangeListener<Number> _loadingBarEvent = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, final Number newValue) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					_pbrLoading.setValue(newValue.intValue());
				}
			});
		}
	};
	
	//displays an error message if loading fails
	private ChangeListener<Throwable> _exceptionEvent = new ChangeListener<Throwable>() {
		@Override
		public void changed(ObservableValue<? extends Throwable> o, Throwable old, final Throwable value) {
			if (_engine.getLoadWorker().getState() == FAILED) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						//creates a message to be displayed
						String message = "";
						if (value != null)
							message = _engine.getLocation() + "\n" + value.getMessage();
						else
							message = _engine.getLocation() + "\nThere was an error loading this page.";
						
						JOptionPane.showMessageDialog(
							BrowserWindow.this,
							message,
							"Loading error...",
							JOptionPane.ERROR_MESSAGE);
					}
				});
			}
		}
	};
	
	//enables or disables the scrape button depending on the result of loading
	private ChangeListener<State> _scrapeContentEvent = new ChangeListener<State>() {
		public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
			if (newState == Worker.State.SUCCEEDED)
				_btnScrape.setEnabled(true);
			else
				_btnScrape.setEnabled(false);
		}
	};
	
	//updates the url text field with the url of the current page
	private ChangeListener<String> _updateUrlEvent = new ChangeListener<String>() {
		@Override
		public void changed(ObservableValue<? extends String> ov, String oldValue, final String newValue) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					_txtUrl.setText(newValue);
				}
			});
		}
	};
	
	//load the page in the url text field
	private ActionListener _btnLoadEvent = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			loadPage(_txtUrl.getText());
		}
	};
	
	//gets the html data and passes it to the main window
	private ActionListener _btnScrapeEvent = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			//gets the document of the browser object
			Document document = _engine.getDocument();
			
			//variables to store html data
			ByteArrayOutputStream htmlBytes = new ByteArrayOutputStream();
			String html = "";
			
			//tries to convert raw data to html
			try {
				Transformer converter = TransformerFactory.newInstance().newTransformer();
				converter.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				
				converter.transform(new DOMSource(document), new StreamResult(htmlBytes));				
				html = new String(htmlBytes.toByteArray(), "UTF-8").toLowerCase();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
			//looks for participant table
			int beginIndex = html.indexOf("<table id=\"participants\"");
			int endIndex = html.indexOf("</table>", beginIndex);
			
			//if this table exists in the current page then pass it to the main window
			if (beginIndex != -1 && endIndex != -1){
				_window.extractDataFromHtmlTable(html.substring(beginIndex, endIndex));
				setVisible(false);
			}
			//otherwise display an informing message
			else{
				JOptionPane.showMessageDialog(BrowserWindow.this, "No participant data was found on this page.");
			}
		}
	};

	/**
	 * Loads default html page.
	 */
	private void loadPage(){
		loadPage(_defaultPage);
	}
	
	/**
	 * Load specified html page.
	 * @param str path to the html page.
	 */
	private void loadPage(String str){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {	
				//constructs correct path variables
				String procUrl = str.replaceAll("(http://)|(https://)|(file:///)", "");
				String prefix = "file:///";
				
				if (str.contains("http://") || str.contains("www"))
					prefix = "http://";
				else if(str.contains("https://"))
					prefix = "https://";
				
				//tries to open specified page
				try {
					URL url = new URL(prefix + procUrl);
					_engine.load(url.toExternalForm());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Loads browser components and assigns event handlers.
	 */
	private void initBrowser(){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				//creates a browser component
				WebView view = new WebView();
				_engine = view.getEngine();
				
				//updates the loading bar
				_engine.getLoadWorker().workDoneProperty().addListener(_loadingBarEvent);
				
				//displays an error message if loading fails
				_engine.getLoadWorker().exceptionProperty().addListener(_exceptionEvent);
				
				//updates the url text field when loading completes
				_engine.locationProperty().addListener(_updateUrlEvent);
				
				//executes when web site finishes loading
				_engine.getLoadWorker().stateProperty().addListener(_scrapeContentEvent);
				
				//assigns the browser component to the JFXPanel
				_browser.setScene(new Scene(view));
			}
		});
	}
	
	public BrowserWindow(MainWindow window) {
		super(window, "Load Participant Data", true);
		setSize(1280, 1024);										//sets default browser size
		setDefaultCloseOperation(HIDE_ON_CLOSE);					//hides browser when x button is pressed
		_window = window;											//saves reference to the main window
		
		//sets the default html page to be loaded
		_defaultPage = System.getProperty("user.dir").replaceAll("\\\\", "/") + "/res/index_webpage/index.html";
		
		//create north panel Components
    	JPanel northBar = new JPanel(new BorderLayout());
    	add(northBar, BorderLayout.NORTH);
    	
    	//creates text field where url can be entered
    	_txtUrl = new JTextField();
    	_txtUrl.addActionListener(_btnLoadEvent);
    	northBar.add(_txtUrl, BorderLayout.CENTER);
    	
    	//creates the button that loads specified url
    	_btnLoad = new JButton("Load");
    	_btnLoad.addActionListener(_btnLoadEvent);
    	northBar.add(_btnLoad, BorderLayout.EAST);
    	
    	//create browser component
    	JScrollPane scrollPane = new JScrollPane(_browser);
		add(scrollPane, BorderLayout.CENTER);
    	
		//create south panel components
    	JPanel southBar = new JPanel(new BorderLayout());
		add(southBar, BorderLayout.SOUTH);
		
		//creates buttons that searches for participant data
		_btnScrape = new JButton("Scrape This Page");
		_btnScrape.addActionListener(_btnScrapeEvent);
		_btnScrape.setEnabled(false);
		southBar.add(_btnScrape, BorderLayout.EAST);
		
		//creates a loading bar used to show page loading progress
		_pbrLoading = new JProgressBar();
		_pbrLoading.setStringPainted(true);
		southBar.add(_pbrLoading, BorderLayout.WEST);
		
		//adds hide listener
		addComponentListener(_windowHiddenEvent);
		
		//Initialises javafx components and loads default page
		initBrowser();
		loadPage();
	}
}
