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
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ams.Controller;

public class RegistrationPanel extends JPanel{

	private JButton registerButton;
	private JTextField passwordField, nameField, addressField, phoneField;
	
	public RegistrationPanel()
	{
		initComponents();
		initListeners();
	}
	
	private void initComponents()
	{
		JPanel infoPanel = new JPanel(new GridLayout(0,1));
		
		JPanel subPanel = new JPanel();
		JLabel label = new JLabel("Password:");
		passwordField = new JTextField();
		passwordField.setPreferredSize(new Dimension(100, passwordField.getPreferredSize().height));		
		subPanel.add(label);
		subPanel.add(passwordField);
		infoPanel.add(subPanel);
		
		subPanel = new JPanel();
		label = new JLabel("Name:");
		nameField = new JTextField();
		nameField.setPreferredSize(new Dimension(100, nameField.getPreferredSize().height));		
		subPanel.add(label);
		subPanel.add(nameField);
		infoPanel.add(subPanel);
		
		subPanel = new JPanel();	
		label = new JLabel("Address:");
		addressField = new JTextField();
		addressField.setPreferredSize(new Dimension(100, addressField.getPreferredSize().height));		
		subPanel.add(label);
		subPanel.add(addressField);
		infoPanel.add(subPanel);
		
		subPanel = new JPanel();	
		label = new JLabel("Phone:");
		phoneField = new JTextField();
		phoneField.setPreferredSize(new Dimension(100, phoneField.getPreferredSize().height));		
		subPanel.add(label);
		subPanel.add(phoneField);
		infoPanel.add(subPanel);
		
		infoPanel.setBorder(BorderFactory.createTitledBorder("Registration Information"));
		add(infoPanel, BorderLayout.CENTER);
		registerButton = new JButton("Register");
		
		add(registerButton);
	}
	
	private void initListeners()
	{
		registerButton.addActionListener(new ActionListener() {
			//@Override
			public void actionPerformed(ActionEvent e)
			{
				onExecute();
			}
		});
	}
	
	private void onExecute()
	{
		try
		{
			int cid = getUniqueCID();
			Statement s = Controller.getInstance().getConnection().createStatement();
			String updateString = "INSERT INTO Customer VALUES ("+cid+", '"+passwordField.getText()+"', '"+nameField.getText()+"', '"+addressField.getText()+"', '"+phoneField.getText()+"')";
			s.executeUpdate(updateString);
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	private int getUniqueCID()
	{
		ArrayList<Integer> cids = new ArrayList<Integer>();
		try
		{
			Statement s = Controller.getInstance().getConnection().createStatement();
			String updateString = "SELECT cid FROM Customer";
			ResultSet rs = s.executeQuery(updateString);
			while(rs.next())
			{
				cids.add(rs.getInt("cid"));
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
		
		int max = 0;
		for(int i : cids)
		{
			if(i > max)
				max = i;
		}
		return max+1;
	}
}
