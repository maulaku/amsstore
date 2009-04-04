package ams.ui.clerk;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import ams.Controller;
import ams.model.Purchase;
import ams.model.PurchaseItem;
import ams.model.Receipt;
import ams.ui.AMSFrame;

public class ItemPurchasePanel extends JPanel
{
	
	private ArrayList<ItemPanel> items;
	
	private JPanel itemPanel;
	
	private JButton addButton, clearButton, purchaseButton;
	
	private JRadioButton cardButton, cashButton;
	
	private JTextField cardNumField, expireMonthField, expireYearField;
	
	private JCheckBox receiptBox;
	
	public ItemPurchasePanel()
	{
		items = new ArrayList<ItemPanel>();
		setBackground(Color.WHITE);
		setLayout(new BorderLayout(5,5));
		initComponents();
		initListeners();

		// code to make sure the items spanned the pane
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				
				for (ItemPanel panel : items)
				{
					panel.setMinimumSize(new Dimension(getWidth()-20, 45));
					panel.setMaximumSize(new Dimension(getWidth()-20, 45));
					panel.updateUI();
				}
				updateUI();
			}
		});
		
	}
	
	private void initComponents()
	{
		// button panel code
		addButton = new JButton("Add Item");
		addButton.setPreferredSize(new Dimension(100, addButton.getPreferredSize().height));
		clearButton = new JButton("Clear Items");
		clearButton.setPreferredSize(new Dimension(100, clearButton.getPreferredSize().height));

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.add(addButton);
		buttonPanel.add(clearButton);
	
		// here are the items
		itemPanel = new JPanel();
		itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
	
		JScrollPane itemPane = new JScrollPane(itemPanel);
		
		JPanel purchaseItemsPanel = new JPanel(new BorderLayout(5,5));
		purchaseItemsPanel.setBackground(Color.WHITE);
		purchaseItemsPanel.setBorder(BorderFactory.createTitledBorder("Item Purchases"));
		purchaseItemsPanel.add(itemPane, BorderLayout.CENTER);
		purchaseItemsPanel.add(buttonPanel, BorderLayout.SOUTH);
	
		// purchase info code
		ButtonGroup group = new ButtonGroup();
		cashButton = new JRadioButton("Cash");
		cashButton.setSelected(true);
		cashButton.setBackground(Color.WHITE);
		group.add(cashButton);
		cardButton = new JRadioButton("Credit Card");
		cardButton.setBackground(Color.WHITE);
		group.add(cardButton);
		
		cardNumField = new JTextField();
		cardNumField.setPreferredSize(new Dimension(100, cardNumField.getPreferredSize().height));
		cardNumField.setEditable(false);
		expireMonthField = new JTextField(2);
//		expireMonthField.setPreferredSize(new Dimension(50, expireField.getPreferredSize().height));			
		expireMonthField.setEditable(false);
		expireYearField = new JTextField(2);
		expireYearField.setEditable(false);
		
		receiptBox = new JCheckBox("Show Receipt");
		receiptBox.setSelected(true);
		receiptBox.setBackground(Color.WHITE);
		
		purchaseButton = new JButton("Confirm Purchase");
		
		JPanel purchaseInfoPanel = new JPanel();
		purchaseInfoPanel.setBackground(Color.WHITE);
		purchaseInfoPanel.setBorder(BorderFactory.createTitledBorder("Purchase Information"));
		JLabel label = new JLabel("Method of Payment:");
		purchaseInfoPanel.add(label);
		purchaseInfoPanel.add(cashButton);
		purchaseInfoPanel.add(cardButton);
		purchaseInfoPanel.add(Box.createHorizontalStrut(25));
		label = new JLabel("Card Number:");
		purchaseInfoPanel.add(label);
		purchaseInfoPanel.add(cardNumField);
		label = new JLabel("Expiry Date:");
		purchaseInfoPanel.add(label);
		purchaseInfoPanel.add(expireMonthField);
		label = new JLabel("/");
		purchaseInfoPanel.add(label);
		purchaseInfoPanel.add(expireYearField);
		purchaseInfoPanel.add(receiptBox);
		purchaseInfoPanel.add(purchaseButton);
		purchaseInfoPanel.setMaximumSize(new Dimension(purchaseInfoPanel.getPreferredSize().width,50));
		
		add(purchaseItemsPanel, BorderLayout.CENTER);
		add(purchaseInfoPanel, BorderLayout.SOUTH);
	}
	
	private void initListeners()
	{
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				addItemPanel();
			}
		});
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				removeAllItemPanels();
			}
		});
		purchaseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				confirmPurchase();
			}
		});
		cashButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				cardNumField.setEditable(false);
				expireMonthField.setEditable(false);
				expireYearField.setEditable(false);
			}
		});
		cardButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				cardNumField.setEditable(true);
				expireMonthField.setEditable(true);
				expireYearField.setEditable(true);
			}
		});
	}
	
	private void addItemPanel()
	{
		ItemPanel item = new ItemPanel();
		items.add(item);
		itemPanel.add(item);
		updateUI();
	}
	
	private void removeAllItemPanels()
	{
		items.clear();
		itemPanel.removeAll();
		updateUI();
	}
	
	private void confirmPurchase()
	{
		Purchase purchase = new Purchase();
		PurchaseItem[] purchaseItems = new PurchaseItem[items.size()];
		for (int i = 0; i < items.size(); ++i)			
		{
			ItemPanel panel = items.get(i);
			try
			{
				purchaseItems[i] = new PurchaseItem(panel.getUPC(), panel.getQuantity());
			} catch (NumberFormatException e)
			{
				Controller.getInstance().setStatusString("Transaction Failed: item information is invalid", AMSFrame.FAILURE);
				reset();
				return;
			}
		}
		purchase.setPurchaseItems(purchaseItems);
		purchase.setPayByCash();
		if (cardButton.isSelected())
		{
			long cardNum = 0;
			try
			{
				cardNum = Long.parseLong(cardNumField.getText());
			} 
			catch (NumberFormatException e)
			{
				Controller.getInstance().setStatusString("Transaction Failed: credit card number is invalid", AMSFrame.FAILURE);
				reset();
				return;
			}
			
			String dateString = "20" + expireYearField.getText() + "-" + expireMonthField.getText() + "-01";
			Date expiryDate = null;
			try
			{
				expiryDate = Date.valueOf(dateString);
			} catch (IllegalArgumentException e)
			{
				Controller.getInstance().setStatusString("Transaction Failed: credit card expiry date is invalid. Please enter the date in the form MM/YY", AMSFrame.FAILURE);
				reset();
				return;
			}
			purchase.setPayByCredit(cardNum, expiryDate);
		} 
		Receipt receipt = Controller.getInstance().purchase(purchase);
		
		if (receiptBox.isSelected())
			printReceipt(receipt);
		reset();
	}
	
	public void printReceipt(Receipt receipt)
	{
		ReceiptDialog dialog = new ReceiptDialog(receipt);
		dialog.setDefaultCloseOperation(ReceiptDialog.DISPOSE_ON_CLOSE);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}
	
	public void reset()
	{
		removeAllItemPanels();
		cardNumField.setText("");
		expireMonthField.setText("");
		expireYearField.setText("");
		cashButton.setSelected(true);
		cardNumField.setEditable(false);
		expireMonthField.setEditable(false);
		expireYearField.setEditable(false);
	}
	
	void removePanel( ItemPanel panel )
	{
		items.remove(panel);
		itemPanel.remove(panel);
		updateUI();
	}
	
	private class ItemPanel extends JPanel
	{
		private JButton removeButton;
		private JTextField upcField, quantityField;
		
		public ItemPanel()
		{
			setLayout(new FlowLayout(FlowLayout.LEADING, 10, 10));
			setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			setBackground(new Color(220,220,245));
			setMinimumSize(new Dimension(ItemPurchasePanel.this.getWidth()-20, 45));
			setPreferredSize(new Dimension(ItemPurchasePanel.this.getWidth()-20, 45));
			setMaximumSize(new Dimension(ItemPurchasePanel.this.getWidth()-20, 45));
			initComponents();
			initListeners();
		}
		
		private void initComponents()
		{
			removeButton = new JButton(new ImageIcon(getClass().getResource("/ams/ui/icons/close.png")));
			removeButton.setContentAreaFilled(false);
			removeButton.setPreferredSize(new Dimension(22,22));
			removeButton.setFocusable(false);
			
			JLabel upcLabel = new JLabel("upc: ");
			JLabel quantityLabel = new JLabel("quantity: ");
			
			upcField = new JTextField();
			upcField.setPreferredSize(new Dimension(100, upcField.getPreferredSize().height));
			quantityField = new JTextField();
			quantityField.setPreferredSize(new Dimension(50, quantityField.getPreferredSize().height));
			
			add(removeButton);
			add(upcLabel);
			add(upcField);
			add(quantityLabel);
			add(quantityField);
		}
		
		private void initListeners()
		{
			removeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e)
				{
					ItemPurchasePanel.this.removePanel(ItemPanel.this);
				}
			});
		}
		
		public long getUPC()
		{
			return Long.parseLong(upcField.getText());
		}
		
		public int getQuantity()
		{
			return Integer.parseInt(quantityField.getText());
		}
		
	}
	
}