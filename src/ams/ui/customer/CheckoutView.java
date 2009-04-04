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
import ams.model.PurchaseItem;

public class CheckoutView extends JPanel{
	
	private PurchaseOnlinePanel parentPanel;
	private JPanel cardInfoPanel;
	private JTextField cardNumberField, cardExpiryField;
	private JTable cartJTable;
	private JButton submitButton;
	
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
		label = new JLabel("Card Expiry Date:");
		cardExpiryField = new JTextField();
		cardExpiryField.setPreferredSize(new Dimension(100, cardExpiryField.getPreferredSize().height));		
		subPanel.add(label);
		subPanel.add(cardExpiryField);
		cardInfoPanel.add(subPanel);
		
		submitButton = new JButton("Submit");
		cardInfoPanel.add(submitButton);
		
		cartJTable = new JTable();
		JScrollPane cartScrollPane = new JScrollPane(cartJTable);
		cartScrollPane.setBorder(BorderFactory.createTitledBorder("Cart"));		
				
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		add(p, BorderLayout.CENTER);
		p.add(cartScrollPane);
		p.add(cardInfoPanel);
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
			
			Vector<Object> values = new Vector<Object>();
			values.add(receiptId);
			values.add(new Date(System.currentTimeMillis()));
			values.add(parentPanel.currentCustomerId);
			values.add("fillerName");//??
			values.add(cardNumberField.getText());
			values.add(new Date(System.currentTimeMillis()));//need date picker
			values.add(new Date(System.currentTimeMillis()));//??
			values.add(new Date(System.currentTimeMillis()));//??
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
//		Purchase purchase = new Purchase();
//		purchase.setPayByCredit(123, "10");
//		
//		PurchaseItem[] purchaseItems = null;
//		purchase.setPurchaseItems(purchaseItems);
//		
//		purchase.setPurchaseDate(new Date(System.currentTimeMillis()));
		
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
		cardExpiryField.setText("");
		
		DefaultTableModel model = (DefaultTableModel) cartJTable.getModel();
		model.setDataVector(null, parentPanel.cartTableColumns);
	}
}
