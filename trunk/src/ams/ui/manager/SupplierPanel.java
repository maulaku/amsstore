package ams.ui.manager;

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

public class SupplierPanel extends JPanel {

	private JButton addButton, delButton;
	private JTextField cityField, nameField, addressField, statusField;
	
	public SupplierPanel()
	{
		initComponents();
		initListeners();
	}
	
	private void initComponents()
	{
		JPanel infoPanel = new JPanel(new GridLayout(0,1));
		
		JPanel subPanel = new JPanel();
		JLabel label = new JLabel("Name:");
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
		label = new JLabel("City:");
		cityField = new JTextField();
		cityField.setPreferredSize(new Dimension(100, cityField.getPreferredSize().height));		
		subPanel.add(label);
		subPanel.add(cityField);
		infoPanel.add(subPanel);
		
		subPanel = new JPanel();	
		label = new JLabel("Status:");
		statusField = new JTextField();
		statusField.setPreferredSize(new Dimension(100, statusField.getPreferredSize().height));		
		subPanel.add(label);
		subPanel.add(statusField);
		infoPanel.add(subPanel);
		
		infoPanel.setBorder(BorderFactory.createTitledBorder("Supplier Registration Information"));
		add(infoPanel, BorderLayout.CENTER);
		
		addButton = new JButton("Add");
		delButton = new JButton("Delete");
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(addButton);
		buttonPanel.add(delButton);
		
		add(buttonPanel);
		
	}
	
	private void initListeners()
	{
		addButton.addActionListener(new ActionListener() {
			//@Override
			public void actionPerformed(ActionEvent e)
			{
				onExecute();
			}
		});
		
		delButton.addActionListener(new ActionListener(){
			//@Override
			public void actionPerformed(ActionEvent e)
			{
				onDelete();
			}
		});
	}
	
	
	private void onExecute()
	{
		try
		{
			Statement s = Controller.getInstance().getConnection().createStatement();
			String updateString = "INSERT INTO Supplier VALUES ('"+nameField.getText()+"', '"+addressField.getText()+"', '"+cityField.getText()+"',  '"+statusField.getText()+"')";
			s.executeUpdate(updateString);
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
		nameField.setText(" ");
		addressField.setText(" ");
		cityField.setText(" ");
		statusField.setText(" ");
	}
	
	
	private void onDelete() 
	{
		try
		{
			Statement s = Controller.getInstance().getConnection().createStatement();
			String updateString = "DELETE FROM Supplier s WHERE s.name = '"+nameField.getText()+"'";//, '"+addressField.getText()+"', '"+cityField.getText()+"',  '"+statusField.getText()+"')";
			s.executeUpdate(updateString);
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}	
		
		nameField.setText(" ");
		addressField.setText(" ");
		cityField.setText(" ");
		statusField.setText(" ");
	}

	

}

