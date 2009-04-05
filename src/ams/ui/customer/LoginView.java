package ams.ui.customer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ams.Controller;

public class LoginView extends JPanel {
	
	public static final String ID = "LOGIN";
	
	private JPanel loginPanel;
	private JTextField customerIdField, customerPasswordField;
	private JButton loginButton, registerButton;
	private JLabel loginFailedLabel;
	
	public PurchaseOnlinePanel parentPanel;
	
	
	public LoginView(PurchaseOnlinePanel parent) {
		parentPanel = parent;
		initComponents();
		initListeners();
	}

	private void initComponents() {
		loginPanel = new JPanel(new GridLayout(0,1));
		loginPanel.setBorder(BorderFactory.createTitledBorder("Login Credentials"));
		
		//loginPanel.setPreferredSize(new Dimension(300,500));
		
		JPanel subPanel = new JPanel();
		JLabel label = new JLabel("Customer ID:");
		customerIdField = new JTextField();
		customerIdField.setPreferredSize(new Dimension(100, customerIdField.getPreferredSize().height));		
		subPanel.add(label);
		subPanel.add(customerIdField);
		loginPanel.add(subPanel);
		
		subPanel = new JPanel();
		label = new JLabel("    Password:");
		customerPasswordField = new JTextField();
		customerPasswordField.setPreferredSize(new Dimension(100, customerPasswordField.getPreferredSize().height));		
		subPanel.add(label);
		subPanel.add(customerPasswordField);
		loginPanel.add(subPanel);
		
		
		loginFailedLabel = new JLabel("Login Failed");
		loginPanel.add(loginFailedLabel);
		loginFailedLabel.setVisible(false);
		
		add(loginPanel, BorderLayout.CENTER);
		
		registerButton = new JButton("Register");		
		loginButton = new JButton("Login");
		JPanel buttonPanel = new JPanel(new GridLayout(0,1));
		buttonPanel.add(loginButton);
		buttonPanel.add(registerButton);
		add(buttonPanel);
	}

	private void initListeners() {
		loginButton.addActionListener(new ActionListener() {
			//@Override
			public void actionPerformed(ActionEvent e)
			{
				onLogin();
			}
		});
		
		registerButton.addActionListener(new ActionListener() {
			//@Override
			public void actionPerformed(ActionEvent e)
			{
				onRegister();
			}
		});
	}
	
	/*
	 * Verifies that login information is valid
	 */
	private void onLogin()
	{
		try
		{
			if(customerIdField.getText().equals("") == false)
			{
				Statement s = Controller.getInstance().getConnection().createStatement();
				String updateString = "SELECT * from CUSTOMER WHERE cid = "+customerIdField.getText();
				ResultSet rs = s.executeQuery(updateString);
				
				if(rs.next())
				{
//					System.out.println(rs.getString("password"));
//					System.out.println(customerPasswordField.getText());
					if(rs.getString("password").equals(customerPasswordField.getText()))
					{
						//login successful
//						loginPanel.disable();
						parentPanel.nextView(SearchView.ID);
						parentPanel.currentCustomerId = Integer.parseInt(customerIdField.getText());
						parentPanel.currentCustomerName = rs.getString("NAME");
					}
				}
			}
			loginFailedLabel.setVisible(true);
			
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	private void onRegister()
	{	
		parentPanel.nextView(RegistrationView.ID);		
	}
	
	public void cleanUp()
	{
		customerIdField.setText("");
		customerPasswordField.setText("");
		loginFailedLabel.setVisible(false);
	}
}
