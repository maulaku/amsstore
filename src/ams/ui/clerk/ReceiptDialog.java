package ams.ui.clerk;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.text.DecimalFormat;
import java.util.Currency;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import ams.model.Item;
import ams.model.Purchase;
import ams.model.PurchaseItem;
import ams.model.Receipt;

public class ReceiptDialog extends JDialog
{
	private Receipt receipt;
	
	public ReceiptDialog(Receipt r)
	{
		receipt = r;
		setModal(true);
		setTitle("Receipt #" + r.getReceiptId());
		setBackground(Color.WHITE);
		setLayout(new BorderLayout());
		
		initComponents();	
	}
	
	private void initComponents()
	{
		
		// receipt that shows, a receipt number, 
		// the date, 
		// a list with the items purchased, their quantities and their prices, 
		// and the total amount for the purchase.  
		// If the customer pays by a credit card, 
		// the receipt should show the last 5 digits of the card's number.
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setOpaque(true);
		contentPanel.setBorder(BorderFactory.createTitledBorder("AMS Store: Thank you for your purchase!"));
		
		contentPanel.add(getLabelPanel("Receipt Number: " + receipt.getReceiptId()));
		contentPanel.add(getLabelPanel("Date: " + receipt.getPurchase().getPurchaseDate()));
		contentPanel.add(getLabelPanel("Items:"));
		
		DefaultTableModel model = new DefaultTableModel();
		Vector<Object> header = new Vector<Object>();
		header.add("Quantity");
		header.add("Item");
		header.add("Price");
		header.add("Amount");
		double totalAmount = 0;
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> row = new Vector<Object>();
		for (int i = 0; i < receipt.getItems().size(); ++i)
		{
			row.clear();
			Item item = receipt.getItems().get(i);
			PurchaseItem pItem = receipt.getPurchase().getPurchaseItems()[i];
			int quantity = pItem.getQuantity();
			double price = item.getPrice();
			totalAmount += quantity * price;
			row.add(quantity);
			row.add(item.getTitle());
			row.add(DecimalFormat.getCurrencyInstance().format(price));
			row.add(DecimalFormat.getCurrencyInstance().format(price*quantity));
			data.add(row);
		}
		model.setDataVector(data, header);
		JTable table = new JTable(model);
		table.getTableHeader().setReorderingAllowed(false);
		
		JScrollPane pane = new JScrollPane(table);
		pane.setBackground(Color.WHITE);
		contentPanel.add(pane);
		contentPanel.add(getLabelPanel("Total Amount: " + DecimalFormat.getCurrencyInstance().format(totalAmount)));
		
		if (receipt.getPurchase().isPaidByCredit())
		{
			int num = receipt.getPurchase().getCreditCardNum();
			String s = Integer.toString(num);
			String x = "";
			for (int i = s.length()-6; i >= 0; --i)
				x += "x";
			contentPanel.add(getLabelPanel("Paid by Credit Card: " + (x + s.substring(s.length()-5, s.length()))));
		}
		else
			contentPanel.add(getLabelPanel("Paid by Cash."));
		
		contentPanel.add(getLabelPanel("Thank you for shopping at AMS Store. Please come again."));
		add(contentPanel, BorderLayout.CENTER);
	}
	
	private JPanel getLabelPanel(String text)
	{
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
		panel.add(new JLabel(text));
		panel.setOpaque(true);
		panel.setBackground(Color.WHITE);
		return panel;
	}
}
