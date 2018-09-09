package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * A class used to store, save and load email host settings.
 * @author Dovydas Rupsys
 */
public class EmailSettings implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public static final int SECURITY_STARTTLS = 0;										//SMTPS protocol constant
	public static final int SECURITY_SSL_TLS = 1;										//TLS protocol constant
	
	//Default location of the settings file
	public static final String DEFAULT_PATH = System.getProperty("user.dir").replaceAll("\\\\", "/") + "/res/settings.ini";
	
	private String _host;																//email host provider
	private int _port;																	//port used
	private String _userName;															//user name of the email
	private int _securityProtocol;														//protocol used
	private boolean _settingsValid;														//used to determine whether settings are not default
	
	/**
	 * Constructs object with default email settings.
	 */
	public EmailSettings(){
		_host = "";
		_port = 587;
		_userName = "";
		_securityProtocol = SECURITY_STARTTLS;
		
		_settingsValid = false;
	}
	
	/**
	 * Constructs object with custom email settings.
	 * @param host Email provider.
	 * @param port Port used to establish a connection.
	 * @param userName User name of the email.
	 * @param security Security protocol used when connecting.
	 */
	public EmailSettings(String host, int port, String userName, int security) {
		_host = host;
		_port = port;
		_userName = userName;
		_securityProtocol = security;
		
		_settingsValid = true;
	}
	
	/**
	 * Gets the validity of settings.
	 * @return
	 */
	public boolean isValid(){
		return _settingsValid;
	}
	
	/**
	 * Gets the email host. 
	 * @return Host name.
	 */
	public String getHost(){
		return _host;
	}
	
	/**
	 * Gets the port of the connection
	 * @return Port number.
	 */
	public int getPort(){
		return _port;
	}
	
	/**
	 * Gets the user name of the email.
	 * @return The user name.
	 */
	public String getUserName(){
		return _userName;
	}
	
	/**
	 * Gets the security protocol used.
	 * @return Security protocol enumerator.
	 */
	public int getSecurity(){
		return _securityProtocol;
	}
	
	/**
	 * Saves this object to a file.
	 * @param path Location where the file should be saved.
	 */
	public void saveToFile(String path){
		File file = new File(path);
		try {
			FileOutputStream os = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(os);
			
			out.writeObject(this);
			
			out.close();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads email settings from a file.
	 * @param path Location where the file to be loaded is.
	 * @return Returns an email object deserialised from that location or a default email settings object if no file was found.
	 */
	public static EmailSettings loadFromFile(String path){
		EmailSettings settings = null;
		
		File file = new File(path);
		if (file.exists() && !file.isDirectory()){
			try {
				FileInputStream is = new FileInputStream(file);
				ObjectInputStream in = new ObjectInputStream(is);
				
				Object o = in.readObject();
				if (o instanceof EmailSettings)
					settings = (EmailSettings)o;
				
				in.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else{
			settings = new EmailSettings();
		}
		
		return settings;
	}
}
