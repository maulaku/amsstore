package ams.ui.customer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import ams.Controller;
import ams.model.Purchase;
import ams.model.PurchaseDAO;
import ams.model.PurchaseItem;

public class CheckoutView extends JPanel{
	
	private PurchaseOnlinePanel parentPanel;
	private JPanel cardInfoPanel;
	private JTextField cardNumberField, cardMonthExpiryField, cardYearExpiryField;
	private JTable cartJTable;
	private JButton submitButton;
	private JLabel deliveryDateLabel;
	
	public CheckoutView(PurchaseOnlinePanel parent) 
	{
		parentPanel = parent;
		setLayout(new BorderLayout(5,5));
		initComponents();
		initListeners();
	}
	
	private void initComponents()
	{		
		cardInfoPanel = new JPanel(new GridLayout(0,1));
		cardInfoPanel.setBorder(BorderFactory.createTitledBorder("Card Information"));
		
		JPanel subPanel = new JPanel();
		JLabel label = new JLabel("Card Number:");
		cardNumberField = new JTextField();
		cardNumberField.setPreferredSize(new Dimension(100, cardNumberField.getPreferredSize().height));		
		subPanel.add(label);
		subPanel.add(cardNumberField);
		cardInfoPanel.add(subPanel);
		
		subPanel = new JPanel();
		label = new JLabel("Card Expiry Date (MM/YY):");
		cardMonthExpiryField = new JTextField();
		cardMonthExpiryField.setPreferredSize(new Dimension(20, cardMonthExpiryField.getPreferredSize().height));		
		subPanel.add(label);
		subPanel.add(cardMonthExpiryField);
		label = new JLabel("/");
		cardYearExpiryField = new JTextField();
		cardYearExpiryField.setPreferredSize(new Dimension(20, cardYearExpiryField.getPreferredSize().height));		
		subPanel.add(label);
		subPanel.add(cardYearExpiryField);
		cardInfoPanel.add(subPanel);
		
		submitButton = new JButton("Submit");
		cardInfoPanel.add(submitButton);
		
		cartJTable = new JTable();
		JScrollPane cartScrollPane = new JScrollPane(cartJTable);
		cartScrollPane.setBorder(BorderFactory.createTitledBorder("Cart"));		
		
		deliveryDateLabel = new JLabel("");
		deliveryDateLabel.setVisible(false);
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(cartScrollPane);
		p.add(cardInfoPanel);
		
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.add(deliveryDateLabel);
		
		JPanel verticalPanel = new JPanel();
		verticalPanel.setLayout(new BoxLayout(verticalPanel, BoxLayout.Y_AXIS));
		add(verticalPanel, BorderLayout.CENTER);
		verticalPanel.add(p);
		verticalPanel.add(p2);
		
		
	}
	
	private void initListeners()
	{
		submitButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e)
		{
			onSubmit();
		}
		});
	}
	
	public void initCart()
	{
		DefaultTableModel model = (DefaultTableModel) cartJTable.getModel();
		model.setDataVector(parentPanel.cartData, parentPanel.cartTableColumns);		
	}
	
	private void onSubmit()
	{
		// Purchase (receiptId, date, cid, name, card#, expire, expectedDate, deliveredDate)
		// PurchaseItem (receiptId,  upc, quantity) 
		try
		{
			int receiptId = findNextReceiptID();
			String dateString = "20" + cardYearExpiryField.getText() + "-" + cardMonthExpiryField.getText() + "-01";
			Date expiryDate = null;
			try
			{
				expiryDate = Date.valueOf(dateString);
			}
			catch (IllegalArgumentException e)
			{
				e.printStackTrace();
			}			
			
			Vector<Object> values = new Vector<Object>();
			values.add(receiptId);
			values.add(new Date(System.currentTimeMillis()));
			values.add(parentPanel.currentCustomerId);
			values.add(parentPanel.currentCustomerName);
			values.add(cardNumberField.getText());
			values.add(expiryDate);
			values.add(PurchaseDAO.getExpectedDeliveryDate());
			values.add(null);//delivery date
			Controller.getInstance().insertTuple("PURCHASE", values);
			
			DefaultTableModel purchases = (DefaultTableModel) cartJTable.getModel();
			for(int i=0; i<purchases.getRowCount(); i++)
			{
				values = new Vector<Object>();
				values.add(receiptId);
				values.add(Integer.parseInt(purchases.getValueAt(i, parentPanel.UPC_COLUMN).toString()));
				values.add(Integer.parseInt(purchases.getValueAt(i, parentPanel.QUANTITY_COLUMN).toString()));
				Controller.getInstance().insertTuple("PURCHASEITEM", values);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		deliveryDateLabel.setText("Checkout successful, your purchases will be delivered on "+PurchaseDAO.getExpectedDeliveryDate()+".");
		deliveryDateLabel.setVisible(true);
	}
	private int findNextReceiptID()
	{
		int receiptId = -1;
		try
		{
			String query = "SELECT MAX(receiptId) FROM Purchase";
			PreparedStatement statement = Controller.getInstance().getConnection().prepareStatement(query);
			ResultSet result = statement.executeQuery();
			result.next();
			receiptId = result.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return receiptId+1;
	}
	
	public void cleanUp()
	{
		cardNumberField.setText("");
		cardMonthExpiryField.setText("");
		cardYearExpiryField.setText("");
		DefaultTableModel model = (DefaultTableModel) cartJTable.getModel();
		model.setDataVector(null, parentPanel.cartTableColumns);
	}
}
