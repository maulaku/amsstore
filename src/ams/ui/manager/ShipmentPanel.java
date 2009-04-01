package ams.ui.manager;

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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import ams.Controller;
import ams.model.ShipItem;
import ams.model.Shipment;
import ams.ui.AMSFrame;

public class ShipmentPanel extends JPanel
{
	
	private ArrayList<ItemPanel> items;
	
	private JPanel itemPanel;
	
	private JButton addButton, clearButton, processButton;
	
	private JTextField storeField, supplierField;
	
	public ShipmentPanel()
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
		addButton = new JButton("Add Shipment Item");
		addButton.setPreferredSize(new Dimension(180, 30));
		clearButton = new JButton("Clear Shipment Items");
		clearButton.setPreferredSize(new Dimension(180, 30));
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		buttonPanel.add(addButton);
		buttonPanel.add(clearButton);
	
		// here are the items
		itemPanel = new JPanel();
		itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
	
		JScrollPane itemPane = new JScrollPane(itemPanel);
		
		JPanel shipItemsPanel = new JPanel(new BorderLayout(5,5));
		shipItemsPanel.setBorder(BorderFactory.createTitledBorder("Shipment Items"));
		shipItemsPanel.add(itemPane, BorderLayout.CENTER);
		shipItemsPanel.add(buttonPanel, BorderLayout.SOUTH);

		processButton = new JButton("Process Shipment");
		processButton.setPreferredSize(new Dimension(180, 30));
		
		storeField = new JTextField();
		storeField.setPreferredSize(new Dimension(100, storeField.getPreferredSize().height));
		supplierField = new JTextField();
		supplierField.setPreferredSize(new Dimension(100, supplierField.getPreferredSize().height));
		
		JPanel shipmentInfoPanel = new JPanel();
		shipmentInfoPanel.setBorder(BorderFactory.createTitledBorder("Shipment Information"));
		JLabel label = new JLabel("Supplier Name:");
		shipmentInfoPanel.add(label);
		shipmentInfoPanel.add(supplierField);
		label = new JLabel("Store Name:");
		shipmentInfoPanel.add(label);
		shipmentInfoPanel.add(storeField);
		shipmentInfoPanel.add(Box.createHorizontalStrut(25));
		shipmentInfoPanel.add(processButton);
		shipmentInfoPanel.setMaximumSize(new Dimension(shipmentInfoPanel.getPreferredSize().width,50));
		
		add(shipItemsPanel, BorderLayout.CENTER);
		add(shipmentInfoPanel, BorderLayout.SOUTH);
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
		processButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				processShipment();
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
	
	private void processShipment()
	{ 
		Shipment shipment = new Shipment();
		shipment.setSupplierName(supplierField.getText());
		shipment.setStoreName(storeField.getText());
		for (ItemPanel panel: items)
		{
			ShipItem item = null;
			try
			{
				item = new ShipItem(panel.getUPC());
				item.setPrice(panel.getCost());
				item.setQuantity(panel.getQuantity());
			} catch (NumberFormatException e)
			{
				Controller.getInstance().setStatusString("Shipment Process Failed: Ship items information not valid.", AMSFrame.FAILURE);
			}
			shipment.addShipItem(item);
		}
		
		Controller.getInstance().processShipment(shipment);
		
		removeAllItemPanels();
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
		private JTextField upcField, quantityField, costField;
		
		public ItemPanel()
		{
			setLayout(new FlowLayout(FlowLayout.LEADING, 10, 5));
			setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			setMinimumSize(new Dimension(ShipmentPanel.this.getWidth(), 35));
			setMaximumSize(new Dimension(ShipmentPanel.this.getWidth(), 35));
			initComponents();
			initListeners();
		}
		
		private void initComponents()
		{
			removeButton = new JButton("Remove") ;
			
			JLabel upcLabel = new JLabel("upc:");
			JLabel quantityLabel = new JLabel("quantity:");
			JLabel costLabel = new JLabel("cost:");
			
			upcField = new JTextField();
			upcField.setPreferredSize(new Dimension(100, upcField.getPreferredSize().height));
			quantityField = new JTextField();
			quantityField.setPreferredSize(new Dimension(50, quantityField.getPreferredSize().height));
			costField = new JTextField();
			costField.setPreferredSize(new Dimension(50, costField.getPreferredSize().height));
			
			add(removeButton);
			add(upcLabel);
			add(upcField);
			add(quantityLabel);
			add(quantityField);
			add(costLabel);
			add(costField);
		}
		
		private void initListeners()
		{
			removeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e)
				{
					ShipmentPanel.this.removePanel(ItemPanel.this);
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
		
		public double getCost()
		{
			return Double.parseDouble(costField.getText());
		}
		
	}
	
}