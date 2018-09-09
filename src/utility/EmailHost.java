package utility;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

/**
 * This object represents a connection to an email host. With this object you can send email's through your email provider. 
 * @author Dovydas Rupsys
 */
public class EmailHost {
	private EmailSettings _settings;								//Contains all settings used to establish a connection with a host
	private boolean _debug = false;									//used to show debugging messages
	
	private Properties _properties;									//Contain the properties of the connection to be established
	private Session _session;										//Used to establish a connection to the email host
	private Transport _transport;
	
	/**
	 * Creates properties with specified security protocol.
	 * @param protocol smtp for starttls connection or smtps for ssl connection.
	 */
	private void createProperties(String protocol){
		_properties.put("mail."+protocol+".host", _settings.getHost());
		_properties.put("mail."+protocol+".port", ""+_settings.getPort());
		_properties.put("mail."+protocol+".auth", "true");
	}
	
	/**
	 * Creates a transport object with specified protocol.
	 * @param protocol smtp for starttls connection or smtps for ssl connection.
	 * @throws NoSuchProviderException
	 */
	private void createTransport(String protocol) throws NoSuchProviderException{
		_session = Session.getInstance(_properties);
		_session.setDebug(_debug);
		_transport = _session.getTransport(protocol);
	}
	
	/**
	 * Constructs an object with host settings and email password.
	 * @param settings An object that contains all email settings.
	 * @param password Password for the email used.
	 * @throws MessagingException 
	 */
	public EmailHost(EmailSettings settings, String password) throws MessagingException{
		_settings = settings;
		
		//creates property object with the provided settings
		_properties = new Properties();
		
		//creates properties for the specified connection type
		switch (_settings.getSecurity()) {
		case EmailSettings.SECURITY_STARTTLS:
			createProperties("smtp");
			_properties.put("mail.smtp.starttls.enable", "true");
			createTransport("smtp");
			break;
		case EmailSettings.SECURITY_SSL_TLS:
			createProperties("smtps");
			_properties.put("mail.smtps.socketFactory.port", ""+_settings.getPort());
			_properties.put("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			_properties.put("mail.smtps.socketFactory.fallback", "false");
			createTransport("smtps");
			break;
		default:
			break;
		}
	
		//connects to the host
		_transport.connect(_settings.getHost(), _settings.getUserName(), password);
	}
	
	/**
	 * Creates and email message and sends it to the specified recipient.
	 * @param recipient Destination of the email.
	 * @param subject Subject text of the email.
	 * @param body The actual email content.
	 * @throws MessagingException 
	 * @throws UnsupportedEncodingException 
	 */
	public void sendEmail(String recipient, String subject, String body) throws MessagingException, UnsupportedEncodingException{
		//creates an email message on the session established
		MimeMessage message = new MimeMessage(_session);
		
		//sets message data
		message.setContent(message, "text/plain; charset=UTF-8");
	    message.setFrom(_settings.getUserName());
	    message.addRecipient(Message.RecipientType.TO, new InternetAddress("\""+recipient+"\""));
	    message.setSubject(subject);
	    message.setSentDate(new Date());
	    message.setText(body);
	    
	    //encodes email names if they have any non ascii characters
	    String from = MimeUtility.decodeText(message.getHeader("from", ""));
	    message.setHeader("from", MimeUtility.encodeText(from, "UTF-8", "Q"));
	    String to = MimeUtility.decodeText(message.getHeader("to", ""));
	    message.setHeader("to", MimeUtility.encodeText(to, "UTF-8", "Q"));
	    
	    //saves settings
	    message.saveChanges();
	    
	    //sends a message
	    _transport.sendMessage(message, message.getAllRecipients());
	}
}
