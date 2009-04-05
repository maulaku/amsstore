package ams.ui.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ams.Controller;
import ams.ui.AMSFrame;

public class DeliveryPanel extends JPanel
{
	private JTextField receiptField;
	private JButton processButton;
	
	public DeliveryPanel()
	{
		setBackground(Color.WHITE);
		setLayout(new BorderLayout());
		initComponents();
		initListeners();		
	}
	
	private void initComponents()
	{
		receiptField = new JTextField(20);
		processButton = new JButton("Process");
		
		JLabel label = new JLabel("Receipt ID of the order delivered: ");
		
		JPanel contentPanel = new JPanel();
		contentPanel.setBackground(Color.WHITE);
		contentPanel.add(label);
		contentPanel.add(receiptField);
		contentPanel.add(processButton);
		
		add(contentPanel, BorderLayout.CENTER);
	}
	
	private void initListeners()
	{
		processButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				processDelivery();
			}
		});
	}
	
	private void processDelivery()
	{
		long receiptID = 0;
		try
		{
			receiptID = Long.parseLong(receiptField.getText());
		} catch (NumberFormatException e)
		{
			Controller.getInstance().setStatusString("Process Failed: receipt id format invalid", AMSFrame.FAILURE);
			return;
		}
		Controller.getInstance().processDelivery(receiptID);
		receiptField.setText("");
	}
}
