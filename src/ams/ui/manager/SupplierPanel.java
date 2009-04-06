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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ams.AMSApp;
import ams.Controller;
import ams.ui.AMSFrame;

public class SupplierPanel extends JPanel {

	private JButton addButton, delButton;
	private JTextField cityField, nameField, addressField;
	private JComboBox statusCombo;
	
	public SupplierPanel()
	{
		setBackground(Color.WHITE);
		initComponents();
		initListeners();
	}
	
	private void initComponents()
	{
		JPanel infoPanel = new JPanel(new GridLayout(0,1));
		infoPanel.setBackground(Color.WHITE);
		
		JPanel subPanel = new JPanel();
		subPanel.setBackground(Color.WHITE);
		JLabel label = new JLabel("    Name:");
		nameField = new JTextField();
		nameField.setPreferredSize(new Dimension(100, nameField.getPreferredSize().height));		
		subPanel.add(label);
		subPanel.add(nameField);
		infoPanel.add(subPanel);
		
		subPanel = new JPanel();	
		subPanel.setBackground(Color.WHITE);
		label = new JLabel("Address:");
		addressField = new JTextField();
		addressField.setPreferredSize(new Dimension(100, addressField.getPreferredSize().height));		
		subPanel.add(label);
		subPanel.add(addressField);
		infoPanel.add(subPanel);
		
		subPanel = new JPanel();
		subPanel.setBackground(Color.WHITE);
		label = new JLabel("      City:");
		cityField = new JTextField();
		cityField.setPreferredSize(new Dimension(100, cityField.getPreferredSize().height));		
		subPanel.add(label);
		subPanel.add(cityField);
		infoPanel.add(subPanel);
		
		subPanel = new JPanel();	
		subPanel.setBackground(Color.WHITE);
		label = new JLabel("  Status:");
		statusCombo = new JComboBox(new String[] {"active", "inactive" });		
		statusCombo.setPreferredSize(new Dimension(100, statusCombo.getPreferredSize().height));
		subPanel.add(label);
		subPanel.add(statusCombo);
		infoPanel.add(subPanel);
		
		infoPanel.setBorder(BorderFactory.createTitledBorder("Supplier Registration Information"));
		add(infoPanel);
		
		addButton = new JButton("Add");
		delButton = new JButton("Delete");
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.WHITE);
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
			String updateString = "INSERT INTO Supplier VALUES ('"+nameField.getText()+"', '"+addressField.getText()+"', '"+cityField.getText()+"',  '"+statusCombo.getSelectedItem().toString()+"')";
			showFeedback(s.executeUpdate(updateString));
			
		}
		catch(SQLException e)
		{
			Controller.getInstance().setStatusString("Cannot Add Supplier: duplicate supplier name", AMSFrame.FAILURE);
			System.err.println(e.getMessage());
		}
		nameField.setText("");
		addressField.setText("");
		cityField.setText("");
		statusCombo.setSelectedIndex(0);
	}
	
	
	private void onDelete() 
	{
		try
		{
			Statement s = Controller.getInstance().getConnection().createStatement();
			String updateString = "DELETE FROM Supplier s WHERE s.name = '"+nameField.getText()+"'";
			showFeedback(s.executeUpdate(updateString));
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}	
		
		nameField.setText("");
		addressField.setText("");
		cityField.setText("");
		statusCombo.setSelectedIndex(0);
	}
	
	private void showFeedback(int flag)
	{
		if(flag == 0)
			Controller.getInstance().setStatusString("Operation Failed.", AMSFrame.FAILURE);
		else
			Controller.getInstance().setStatusString("Operation Successful.", AMSFrame.SUCCESS);
	}

	

}

