import windows.MainWindow;
/**
 * Starting object of the application.
 * @author Dovydas Rupsys
 */
public class MainApp {
	//Starting point of the application
	public static void main(String[] args) {
		//Corrects an error with KEATs login page which appeared after this website was updated
		System.setProperty("jsse.enableSNIExtension", "false");
		
		//Proxy settings required to run this application on KCL lab computers.
//    	System.setProperty("http.proxyHost", "proxy.inf.kcl.ac.uk");
//    	System.setProperty("http.proxyPort", "3128");
		
		//Constructs and displays the main window of the application
		new MainWindow();
	}
}