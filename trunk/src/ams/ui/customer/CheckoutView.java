package ams.ui.customer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import ams.Controller;
import ams.exceptions.OutOfStockException;
import ams.model.Item;
import ams.model.ItemDAO;
import ams.model.Purchase;
import ams.model.PurchaseDAO;
import ams.model.PurchaseItem;
import ams.ui.AMSFrame;

public class CheckoutView extends MyPanel
{

	public static final String ID = "CHECKOUT";

	private PurchaseOnlinePanel parentPanel;
	private JPanel cardInfoPanel;
	private JTextField cardNumberField, cardMonthExpiryField, cardYearExpiryField;
	private JTable cartJTable;
	private JButton submitButton;

	private HashMap<Item, Integer> cartItems;
	private String cartTableColumns[] = new String[] { "UPC", "Title", "Category", "Quantity" };

	public CheckoutView(PurchaseOnlinePanel parent)
	{
		parentPanel = parent;
		setLayout(new BorderLayout(5, 5));
		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentShown(ComponentEvent e)
			{
				initCart();
			}
		});
		initComponents();
		initListeners();
	}

	private void initComponents()
	{
		cardInfoPanel = new JPanel();
		cardInfoPanel.setBackground(Color.WHITE);
		cardInfoPanel.setBorder(BorderFactory.createTitledBorder("Card Information"));

		JPanel subPanel = new JPanel();
		subPanel.setBackground(Color.WHITE);
		JLabel label = new JLabel("Card Number:");
		cardNumberField = new JTextField();
		cardNumberField.setPreferredSize(new Dimension(100, cardNumberField.getPreferredSize().height));
		subPanel.add(label);
		subPanel.add(cardNumberField);
		cardInfoPanel.add(subPanel);

		subPanel = new JPanel();
		subPanel.setBackground(Color.WHITE);
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

		cartJTable = new JTable() {
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		JScrollPane cartScrollPane = new JScrollPane(cartJTable);
		cartScrollPane.setBackground(Color.WHITE);
		cartScrollPane.setBorder(BorderFactory.createTitledBorder("Cart"));

//		JPanel p = new JPanel();
//		p.setBackground(Color.WHITE);
//		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
//		p.add(cartScrollPane);
//		p.add(cardInfoPanel);

//		JPanel verticalPanel = new JPanel();
//		verticalPanel.setBackground(Color.WHITE);
//		verticalPanel.setLayout(new BoxLayout(verticalPanel, BoxLayout.Y_AXIS));
//		verticalPanel.add(p);
		
		add(cartScrollPane, BorderLayout.CENTER);
		add(cardInfoPanel, BorderLayout.SOUTH);
	}

	private void initListeners()
	{
		submitButton.addActionListener(new ActionListener()
		{
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
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();

		cartItems = parentPanel.getCartItems();
		for (Item item : parentPanel.getCartItems().keySet())
		{
			Vector<Object> row = new Vector<Object>();
			row.add(item.getUPC());
			row.add(item.getTitle());
			row.add(item.getCategory());
			row.add(parentPanel.getCartItems().get(item));
			data.add(row);
		}
		Vector<Object> header = new Vector<Object>();
		Collections.addAll(header, cartTableColumns);
		model.setDataVector(data, header);
	}

	private void onSubmit()
	{
		PurchaseItem[] pItems = new PurchaseItem[cartItems.size()];
		int i = 0;
		for (Item item : cartItems.keySet())
		{
			pItems[i++] = new PurchaseItem(item.getUPC(), cartItems.get(item));
			try
			{
				ItemDAO.getInstance().updateStock("Online Store", item.getUPC(), -1*cartItems.get(item));
			}
			catch(OutOfStockException e)
			{
				Controller.getInstance().setStatusString("Cannot purchase: Out of stock on item "+item.getTitle()+".", AMSFrame.FAILURE);
				return;
			}
			catch(SQLException e)
			{
				e.printStackTrace();
				return;
			}
		}
		
		
		String dateString = "20" + cardYearExpiryField.getText() + "-" + cardMonthExpiryField.getText() + "-01";
		Date expiryDate = null;
		try
		{
			expiryDate = Date.valueOf(dateString);
		} catch (IllegalArgumentException e)
		{
			Controller.getInstance().setStatusString("Cannot purchase: card expiry date not valid", AMSFrame.FAILURE);
			e.printStackTrace();
			return;
		}
		

		Purchase purchase = new Purchase();
		purchase.setPurchaseOnline(true);
		purchase.setPurchaseItems(pItems);
		purchase.setCID(parentPanel.currentCustomerId);
		purchase.setStoreName("Online Store");

		try
		{
			long ccNum = Long.parseLong(cardNumberField.getText());
			purchase.setPayByCredit(ccNum, expiryDate);
		} catch (NumberFormatException e)
		{
			Controller.getInstance().setStatusString("Cannot complete purchase: credit card number invalid",
					AMSFrame.FAILURE);
			return;
		}
		Date eDate = PurchaseDAO.getExpectedDeliveryDate();
		purchase.setExpectedDate(eDate);
		Controller.getInstance().setStatusString("Purchase Successful: your purchases will be delivered on " + eDate, AMSFrame.SUCCESS);
		
		Controller.getInstance().purchase(purchase);
	}

	public void cleanUp()
	{
		cardNumberField.setText("");
		cardMonthExpiryField.setText("");
		cardYearExpiryField.setText("");
		if (cartItems != null)
			cartItems.clear();
		
		DefaultTableModel model = (DefaultTableModel) cartJTable.getModel();
		for (int i = 0; i < model.getRowCount(); ++i)
			model.removeRow(i);
	}
}
