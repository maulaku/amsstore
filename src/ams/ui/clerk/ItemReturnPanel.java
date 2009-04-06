package ams.ui.clerk;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ams.Controller;
import ams.model.ItemDAO;
import ams.model.PurchaseDAO;
import ams.ui.AMSFrame;

public class ItemReturnPanel extends JPanel
{
	private JButton retButton;
	private JTextField retReceiptID, retItemUPC, quantityField, nameField;

	public ItemReturnPanel()
	{
		setBackground(Color.WHITE);
		initComponents();
		initListeners();
	}

	private void initComponents()
	{
		JPanel infoPanel = new JPanel(new GridLayout(0, 1));
		infoPanel.setBackground(Color.WHITE);

		JPanel subPanel = new JPanel();
		subPanel.setBackground(Color.WHITE);
		JLabel label = new JLabel("        Receipt ID:");
		retReceiptID = new JTextField();
		retReceiptID.setPreferredSize(new Dimension(100, retReceiptID.getPreferredSize().height));
		subPanel.add(label);
		subPanel.add(retReceiptID);
		infoPanel.add(subPanel);

		subPanel = new JPanel();
		subPanel.setBackground(Color.WHITE);
		label = new JLabel("                  UPC:");
		retItemUPC = new JTextField();
		retItemUPC.setPreferredSize(new Dimension(100, retItemUPC.getPreferredSize().height));
		subPanel.add(label);
		subPanel.add(retItemUPC);
		infoPanel.add(subPanel);

		subPanel = new JPanel();
		subPanel.setBackground(Color.WHITE);
		label = new JLabel("           Quantity:");
		quantityField = new JTextField();
		quantityField.setPreferredSize(new Dimension(100, quantityField.getPreferredSize().height));
		subPanel.add(label);
		subPanel.add(quantityField);
		infoPanel.add(subPanel);

		subPanel = new JPanel();
		subPanel.setBackground(Color.WHITE);
		label = new JLabel("   Store Name:");
		nameField = new JTextField();
		nameField.setPreferredSize(new Dimension(100, nameField.getPreferredSize().height));
		subPanel.add(label);
		subPanel.add(nameField);
		infoPanel.add(subPanel);

		infoPanel.setBorder(BorderFactory.createTitledBorder("Enter Return Information"));
		add(infoPanel, BorderLayout.CENTER);

		retButton = new JButton("Process Return");

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.add(retButton);

		add(buttonPanel);

	}

	private void initListeners()
	{
		retButton.addActionListener(new ActionListener()
		{
			// @Override
			public void actionPerformed(ActionEvent e)
			{
				onExecute();
			}
		});

	}

	private void onExecute()
	{

		boolean isWithinTimeLimit = checkDates();
		int quantity = 0;
		try
		{
			quantity = Integer.parseInt(quantityField.getText().trim());
		} catch (NumberFormatException e)
		{
			Controller.getInstance().setStatusString("Cannot return: quantity invalid", AMSFrame.FAILURE);
		}
		if (quantity > 0 && isWithinTimeLimit)
		{

			try
			{
				int retID = getUniqueRetID();

				String query = "SELECT * FROM Purchase, PurchaseItem WHERE " + "PurchaseItem.upc = "
						+ retItemUPC.getText().trim() + " AND PurchaseItem.receiptId = Purchase.receiptId "
						+ "AND Purchase.receiptId = " + retReceiptID.getText().trim();
				PreparedStatement statement = Controller.getInstance().getConnection().prepareStatement(query);
				ResultSet result = statement.executeQuery();
				boolean wasPurchased = result.next();

				String query2 = "SELECT SUM(ReturnItem.quantity) FROM Return, ReturnItem WHERE ReturnItem.upc = "
						+ retItemUPC.getText().trim() + " AND Return.receiptId = " + retReceiptID.getText().trim() + "";
				PreparedStatement statement2 = Controller.getInstance().getConnection().prepareStatement(query2);

				System.out.println(query2);

				ResultSet result2 = statement2.executeQuery();

				boolean wasReturned = result2.next();

				if (wasPurchased && wasReturned)
				{
					System.out.println(result.getInt(11) + " " + result2.getInt(1));
					System.out.println(retID);

					if (result.getInt(11) - result2.getInt(1) >= Integer.parseInt(quantityField.getText().trim()))
					{
						String query3 = "INSERT INTO Return VALUES(" + retID + ", ?, " + retReceiptID.getText().trim()
								+ ",  '" + nameField.getText() + "')";
						PreparedStatement statement3 = Controller.getInstance().getConnection()
								.prepareStatement(query3);
						statement3.setDate(1, new Date(System.currentTimeMillis()));
						statement3.executeUpdate();

						String query4 = "INSERT INTO ReturnItem VALUES(" + retID + ", " + retItemUPC.getText().trim()
								+ ", " + quantityField.getText().trim() + ")";
						PreparedStatement statement4 = Controller.getInstance().getConnection()
								.prepareStatement(query4);
						statement4.executeUpdate();
						statement3.close();
						statement4.close();
						ItemDAO.getInstance().updateStock(nameField.getText(),Long.parseLong(retItemUPC.getText()), 
								Integer.parseInt(quantityField.getText().trim()));

						Controller.getInstance().setStatusString("Return Processed Successfully", AMSFrame.SUCCESS);
					} else
					{
						Controller.getInstance().setStatusString("Could not process return: invalid quantity",
								AMSFrame.FAILURE);
						return;
					}
				} else if (wasPurchased && !wasReturned)
				{
					String query3 = "INSERT INTO Return VALUES(" + retID + ", ?, " + retReceiptID.getText().trim()
							+ ",  '" + nameField.getText() + "')";
					PreparedStatement statement3 = Controller.getInstance().getConnection().prepareStatement(query3);
					statement3.setDate(1, new Date(System.currentTimeMillis()));
					statement3.executeUpdate();

					String query4 = "INSERT INTO ReturnItem VALUES(" + retID + ", " + retItemUPC.getText().trim()
							+ ", " + quantityField.getText().trim() + ")";
					PreparedStatement statement4 = Controller.getInstance().getConnection().prepareStatement(query4);
					statement4.executeUpdate();

					statement3.close();
					statement4.close();
				}
				statement.close();
				statement2.close();

			} catch (SQLException e)
			{
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
			retReceiptID.setText("");
			retItemUPC.setText("");
			quantityField.setText("");
			nameField.setText("");
		} else
		{
			if (isWithinTimeLimit)
				Controller.getInstance()
						.setStatusString("Could not process return: invalid quantity", AMSFrame.FAILURE);
			else
				Controller.getInstance().setStatusString("Could not process return: return period expired",
						AMSFrame.FAILURE);

		}

	}

	private int getUniqueRetID()
	{
		int tempRetID = 0;
		try
		{
			String query = "SELECT MAX(retid) FROM Return";
			PreparedStatement statement = Controller.getInstance().getConnection().prepareStatement(query);
			ResultSet result = statement.executeQuery();
			result.next();
			tempRetID = result.getInt(1);
		} catch (SQLException e)
		{

		}

		return tempRetID + 1;
	}

	private boolean checkDates()
	{
		int receiptId = Integer.parseInt(retReceiptID.getText().trim());
		Date purchaseDate = PurchaseDAO.getPurchaseDate(receiptId);
		long calc = System.currentTimeMillis() - purchaseDate.getTime();
		return (calc >= 0 && calc <= 1296000000);
	}

}
