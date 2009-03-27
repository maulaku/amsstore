package ams.ui.clerk;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import ams.Controller;
import ams.model.Purchase;
import ams.model.PurchaseItem;

public class ItemPurchasePanel extends JPanel
{
	
	private ArrayList<ItemPanel> items;
	
	private JPanel itemPanel;
	
	private JButton addButton, clearButton, purchaseButton;
	
	private JRadioButton cardButton, cashButton;
	
	private JTextField cardNumField, expireField;
	
	public ItemPurchasePanel()
	{
		items = new ArrayList<ItemPanel>();
		setLayout(new BorderLayout(5,5));
		initComponents();
		initListeners();

		// code to make sure the items spanned the pane
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				
				for (ItemPanel panel : items)
				{
					panel.setMinimumSize(new Dimension(getWidth(), 35));
					panel.setMaximumSize(new Dimension(getWidth(), 35));
					panel.updateUI();
				}
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
		buttonPanel.add(addButton);
		buttonPanel.add(clearButton);
	
		// here are the items
		itemPanel = new JPanel();
		itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
	
		JScrollPane itemPane = new JScrollPane(itemPanel);
		
		JPanel purchaseItemsPanel = new JPanel(new BorderLayout(5,5));
		purchaseItemsPanel.setBorder(BorderFactory.createTitledBorder("Item Purchases"));
		purchaseItemsPanel.add(itemPane, BorderLayout.CENTER);
		purchaseItemsPanel.add(buttonPanel, BorderLayout.SOUTH);
	
		// purchase info code
		ButtonGroup group = new ButtonGroup();
		cashButton = new JRadioButton("Cash");
		cashButton.setSelected(true);
		group.add(cashButton);
		cardButton = new JRadioButton("Credit Card");
		group.add(cardButton);
		
		cardNumField = new JTextField();
		cardNumField.setPreferredSize(new Dimension(100, cardNumField.getPreferredSize().height));
		cardNumField.setEditable(false);
		expireField = new JTextField();
		expireField.setPreferredSize(new Dimension(50, expireField.getPreferredSize().height));			
		expireField.setEditable(false);
		
		purchaseButton = new JButton("Confirm Purchase");
		
		JPanel purchaseInfoPanel = new JPanel();
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
		purchaseInfoPanel.add(expireField);
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
				expireField.setEditable(false);
			}
		});
		cardButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				cardNumField.setEditable(true);
				expireField.setEditable(true);
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
			purchaseItems[i] = new PurchaseItem(panel.getUPC(), panel.getQuantity());
		}
		purchase.setPurchaseItems(purchaseItems);
		purchase.setPayByCash();
		if (cardButton.isSelected())
		{
			int cardNum = Integer.parseInt(cardNumField.getText());
			String expiryDate = expireField.getText();
			purchase.setPayByCredit(cardNum, expiryDate);
		} 
		Controller.getInstance().purchase(purchase);
	
		removeAllItemPanels();
		cardNumField.setText("");
		expireField.setText("");
		cashButton.setSelected(true);
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
			setLayout(new FlowLayout(FlowLayout.LEADING, 10, 5));
			setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			setMinimumSize(new Dimension(ItemPurchasePanel.this.getWidth(), 35));
			setMaximumSize(new Dimension(ItemPurchasePanel.this.getWidth(), 35));
			initComponents();
			initListeners();
		}
		
		private void initComponents()
		{
			removeButton = new JButton("Remove") ;
			
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
		
		public int getUPC()
		{
			return Integer.parseInt(upcField.getText());
		}
		
		public int getQuantity()
		{
			return Integer.parseInt(quantityField.getText());
		}
		
	}
	
}