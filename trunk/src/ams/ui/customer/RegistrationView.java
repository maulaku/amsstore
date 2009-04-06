package ams.ui.customer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ams.model.CustomerDAO;

public class RegistrationView extends MyPanel{

	public static final String ID = "REGISTRATION";
	
	private PurchaseOnlinePanel parentPanel;
	private JButton registerButton, continueButton, backButton;
	private JTextField passwordField, nameField, addressField, phoneField;
	
	private JLabel feedbackLabel;
	
	public RegistrationView(PurchaseOnlinePanel parent)
	{
		setBackground(Color.WHITE);
		parentPanel = parent;
		initComponents();
		initListeners();
	}
	
	private void initComponents()
	{
		JPanel superPanel = new JPanel();
		superPanel.setLayout(new BoxLayout(superPanel, BoxLayout.Y_AXIS));
		
		JPanel infoPanel = new JPanel(new GridLayout(0,1));
		
		JPanel subPanel = new JPanel();
		subPanel.setBackground(Color.WHITE);
		JLabel label = new JLabel("Password:");
		passwordField = new JTextField();
		passwordField.setPreferredSize(new Dimension(100, passwordField.getPreferredSize().height));		
		subPanel.add(label);
		subPanel.add(passwordField);
		infoPanel.add(subPanel);
		
		subPanel = new JPanel();
		subPanel.setBackground(Color.WHITE);
		label = new JLabel("     Name:");
		nameField = new JTextField();
		nameField.setPreferredSize(new Dimension(100, nameField.getPreferredSize().height));		
		subPanel.add(label);
		subPanel.add(nameField);
		infoPanel.add(subPanel);
		
		subPanel = new JPanel();
		subPanel.setBackground(Color.WHITE);
		label = new JLabel("  Address:");
		addressField = new JTextField();
		addressField.setPreferredSize(new Dimension(100, addressField.getPreferredSize().height));		
		subPanel.add(label);
		subPanel.add(addressField);
		infoPanel.add(subPanel);
		
		subPanel = new JPanel();	
		subPanel.setBackground(Color.WHITE);
		label = new JLabel("    Phone:");
		phoneField = new JTextField();
		phoneField.setPreferredSize(new Dimension(100, phoneField.getPreferredSize().height));		
		subPanel.add(label);
		subPanel.add(phoneField);
		infoPanel.add(subPanel);
		
		infoPanel.setBorder(BorderFactory.createTitledBorder("Registration Information"));
		infoPanel.setBackground(Color.WHITE);
		superPanel.add(infoPanel, BorderLayout.NORTH);
		registerButton = new JButton("Register");
		backButton = new JButton("Back");
		
		
		continueButton = new JButton("Continue");
		continueButton.setVisible(false);
		feedbackLabel = new JLabel("");
		feedbackLabel.setVisible(false);
		JPanel feedbackPanel = new JPanel(new GridLayout(0,1));
		feedbackPanel.setBackground(Color.WHITE);
		feedbackPanel.add(feedbackLabel);
		feedbackPanel.add(continueButton);
		feedbackPanel.add(backButton);
		
		superPanel.add(feedbackPanel);
		add(superPanel);
		

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.add(backButton);
		buttonPanel.add(registerButton);
		
		add(buttonPanel);
	}
	
	private void initListeners()
	{
		registerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				onRegister();
			}
		});
		
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentPanel.nextView(LoginView.ID);
			};
		});
		
		continueButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				onContinue();
			}
		});
	}
	
	/*
	 * Creates a new customer tuple
	 */
	private void onRegister()
	{
		feedbackLabel.setVisible(true);
		if(passwordField.getText().equals("") || 
			nameField.getText().equals("") ||
			addressField.getText().equals("") ||
			phoneField.getText().equals(""))
		{
			feedbackLabel.setText("Please fill in all fields.");
		}
		else
		{
			int cid = CustomerDAO.insert(new String[]{passwordField.getText(), nameField.getText(), 
					addressField.getText(), phoneField.getText()});
			feedbackLabel.setText("Your Customer ID is "+cid+", your password is "+
					passwordField.getText()+".");
			continueButton.setVisible(true);
			parentPanel.currentCustomerId = cid;
			parentPanel.currentCustomerName = nameField.getText();
		}
	}
	
	private void onContinue()
	{
		parentPanel.nextView(SearchView.ID);
	}
	
	public void cleanUp()
	{
		passwordField.setText("");
		nameField.setText("");
		addressField.setText("");
		phoneField.setText("");
		continueButton.setVisible(false);
		feedbackLabel.setVisible(false);
	}
}
