package windows;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import utility.EmailSettings;

/**
 * Constructs a window that can be used to change or save email host settings.
 * @author Dovydas Rupsys
 */
public class EmailSettingsWindow extends JDialog {
	private static final long serialVersionUID = 1L;

	private EmailSettings _settings;													//contains settings of the email host
	private JTextField _serverName = new JTextField(20);								//component used to edit server name
	private JSpinner _port = new JSpinner();											//component used to edit port number
	private JComboBox<String> _securityProtocol;										//component used to edit security protocol
	private JTextField _userName = new JTextField();									//component used to edit user name of the email
	
	/**
	 * Constructs components of the settings area of the window.
	 */
	private void initSettingsArea(){
		//constructs the container panel for the settings area
		JPanel settingsPanel = new JPanel(new BorderLayout());
		add(settingsPanel, BorderLayout.NORTH);
		
		//Creates and adds settings label at the top of the settings area.
		settingsPanel.add(new JLabel("<html><font size=4>Settings</font></html>"), BorderLayout.NORTH);
		
		//creates panel for the left side of the settings area that holds label components.
		JPanel settingsLabelPanel = new JPanel(new GridLayout(2, 1, 0, 2));
		settingsLabelPanel.setBorder(new EmptyBorder(2, 20, 2, 2));
		settingsPanel.add(settingsLabelPanel, BorderLayout.WEST);
		
		settingsLabelPanel.add(new JLabel("Server Name:"));
		settingsLabelPanel.add(new JLabel("Port:"));
		
		//creates panel for the centre of the settings area that holds input components.
		JPanel settingsInputPanel = new JPanel(new GridLayout(2, 1, 0, 2));
		settingsInputPanel.setBorder(new EmptyBorder(2, 0, 2, 2));
		settingsPanel.add(settingsInputPanel, BorderLayout.CENTER);
		
		_serverName.setText(_settings.getHost());
		settingsInputPanel.add(_serverName);
		
		JPanel settingsInputPortPanel = new JPanel(new BorderLayout());
		settingsInputPanel.add(settingsInputPortPanel);
		
		_port.setValue(_settings.getPort());
		settingsInputPortPanel.add(_port, BorderLayout.CENTER);
		JLabel defaultPort = new JLabel("Default: 587");
		defaultPort.setBorder(new EmptyBorder(0, 2, 0, 0));
		settingsInputPortPanel.add(defaultPort, BorderLayout.EAST);
	}
	
	/**
	 * Constructs components of the security and authentication area of the window.
	 */
	private void initSecurityArea(){
		//constructs the container panel for the security and authentication area
		JPanel securityPanel = new JPanel(new BorderLayout());
		securityPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
		add(securityPanel, BorderLayout.CENTER);
		
		//Creates and adds security and authentication label at the top of the this area.
		securityPanel.add(new JLabel("<html><font size=4>Security and Authentication</font></html>"), BorderLayout.NORTH);
		
		//creates panel for the left side of the security and authentication area that holds label components.
		JPanel securityLabelPanel = new JPanel(new GridLayout(2, 1, 0, 2));
		securityLabelPanel.setBorder(new EmptyBorder(2, 20, 2, 2));
		securityPanel.add(securityLabelPanel, BorderLayout.WEST);
		
		securityLabelPanel.add(new JLabel("Connection Security:"));
		securityLabelPanel.add(new JLabel("User Name:"));
		
		//creates panel for the centre of the security and authentication area that holds input components.
		JPanel securityInputPanel = new JPanel(new GridLayout(2, 1, 0, 2));
		securityInputPanel.setBorder(new EmptyBorder(2, 0, 2, 2));
		securityPanel.add(securityInputPanel, BorderLayout.CENTER);
		
		_securityProtocol.setSelectedIndex(_settings.getSecurity());
		securityInputPanel.add(_securityProtocol);
		
		_userName.setText(_settings.getUserName());
		securityInputPanel.add(_userName);
	}
	
	/**
	 * Constructs the button area of the window.
	 */
	private void initButtonArea(){
		//constructs the container panel for the button area
		JPanel buttonsAreaPanel = new JPanel(new BorderLayout());
		buttonsAreaPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
		add(buttonsAreaPanel, BorderLayout.SOUTH);
		
		//creates panel for the right side of the buttons area that holds button components
		JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 5, 0));
		buttonsAreaPanel.add(buttonsPanel, BorderLayout.EAST);
		
		JButton btnCancel = new JButton("Cancel");
		buttonsPanel.add(btnCancel);
		//This event closes window when cancel button is pressed
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchEvent(new WindowEvent(EmailSettingsWindow.this, WindowEvent.WINDOW_CLOSING));
			}
		});
		
		JButton btnOk = new JButton("OK");
		buttonsPanel.add(btnOk);
		//This event saves you settings and closes window when ok button is pressed
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new EmailSettings(
						_serverName.getText(), 
						(int)_port.getValue(), 
						_userName.getText(), 
						_securityProtocol.getSelectedIndex())
				.saveToFile(EmailSettings.DEFAULT_PATH);
				dispatchEvent(new WindowEvent(EmailSettingsWindow.this, WindowEvent.WINDOW_CLOSING));
			}
		});
	}
	
	/**
	 * Constructs the window and displays it.
	 * @param window The owner of this window.
	 */
	public EmailSettingsWindow(MainWindow window){
		super(window, "Email Settings", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		//creates a border around the content panel of the window
		((JPanel)getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
		
		//loads settings from the file and constructs comboboxes with default options
		_settings = EmailSettings.loadFromFile(EmailSettings.DEFAULT_PATH);
		_securityProtocol = new JComboBox<String>(new String[] { "STARTTLS", "SSL/TLS"});
		
		//initialises three areas of the window
		initSettingsArea();
		initSecurityArea();
		initButtonArea();
		
		//displays this window
		pack();
		setVisible(true);
	}
}
