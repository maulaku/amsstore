package ams.ui.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import ams.Controller;

public class DailySalesPanel extends JPanel
{
	private JTable table;
	private JButton searchButton;
	private JTextField storeField;
	private CalendarPanel calendarPanel;
	
	public DailySalesPanel()
	{
		setBackground(Color.WHITE);
		setLayout(new BorderLayout(5,5));
		initComponents();
		initListeners();
	}
	
	private void initComponents()
	{
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		
		calendarPanel = new CalendarPanel();
		calendarPanel.setPreferredSize(new Dimension(calendarPanel.getPreferredSize().width, 150));
		calendarPanel.setCalendar(Calendar.getInstance());
		calendarPanel.updateDisplay();
		buttonPanel.add(calendarPanel);

		JLabel storeLabel = new JLabel("Store:");
		buttonPanel.add(storeLabel);
		storeField = new JTextField();
		storeField.setPreferredSize(new Dimension(200, storeField.getPreferredSize().height));
		buttonPanel.add(storeField);

		// button panel code
		searchButton = new JButton("Search");
		searchButton.setPreferredSize(new Dimension(100, searchButton.getPreferredSize().height));
		buttonPanel.add(searchButton);
		add(buttonPanel, BorderLayout.NORTH);

		table = new JTable() {
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		table.setBackground(Color.WHITE);

		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);
	}
	
	private void initListeners()
	{
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				populateTableList();
			}
		});
	}
	
	private void populateTableList()
	{
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		
		Vector<String> columns = new Vector<String>();
		columns.add("UPC");
		columns.add("Category");
		columns.add("Unit Price");
		columns.add("Units");
		columns.add("Total Value");

		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		if(columns != null)
		{	
			try
			{
				PreparedStatement statement = Controller.getInstance().getConnection().prepareStatement("SELECT upc, category, sellprice, units FROM Item INNER JOIN (SELECT upc, SUM(quantity) AS units FROM PurchaseItem WHERE receiptId IN (SELECT receiptId FROM Purchase WHERE purchasedate = ? AND name = ?) GROUP BY upc) USING (upc) ORDER BY category ASC");
				statement.setDate(1, calendarPanel.getSelectedDate());
				statement.setString(2, storeField.getText());
				ResultSet results = statement.executeQuery();
				
				DecimalFormat priceFormat = new DecimalFormat("#,##0.00");
				
				//Group results by category
				HashMap<String, Vector<Vector<Object>>> categories = new HashMap<String, Vector<Vector<Object>>>();
				while(results.next())
				{
					Vector<Object> rowData = new Vector<Object>();

					rowData.add(results.getObject("upc"));
					
					String category = (String) results.getObject("category");
					rowData.add(category);
					
					double price = ((BigDecimal) results.getObject("sellprice")).doubleValue();
					rowData.add(priceFormat.format(price));
					
					int quantity = ((BigDecimal) results.getObject("units")).intValue();
					rowData.add(quantity);
					
					double total = price * quantity;
					rowData.add(priceFormat.format(total));
					
					//Initialize category if it doesn't already exist in the map
					if(categories.get(category) == null)
					{
						categories.put(category, new Vector<Vector<Object>>());
					}
					
					categories.get(category).add(rowData);
				}
				
				double grandTotalValue = 0.00;
				int grandTotalUnits = 0;
				for(Vector<Vector<Object>> category : categories.values())
				{
					int totalUnits = 0;
					double totalValue = 0.00;
					
					//Add category rows to data set
					for(Vector<Object> row : category)
					{
						totalUnits += Integer.parseInt(row.get(row.size() - 2).toString());
						totalValue += Double.parseDouble(row.get(row.size() - 1).toString());
						data.add(row);
					}
					
					//Add to grand total
					grandTotalUnits += totalUnits;
					grandTotalValue += totalValue;
					
					//Add category's total to data set
					Vector<Object> categoryTotal = new Vector<Object>();
					categoryTotal.add(null);
					categoryTotal.add("Total");
					categoryTotal.add(null);
					categoryTotal.add(totalUnits);
					categoryTotal.add(priceFormat.format(totalValue));
					
					data.add(categoryTotal);
					
					//Add spacer
					data.add(new Vector<Object>());
				}
				
				//TODO: Display total daily sales outside of table
				//Add total daily sales to data set
				Vector<Object> total = new Vector<Object>();
				total.add(null);
				total.add("Total Daily Sales");
				total.add(null);
				total.add(grandTotalUnits);
				total.add(priceFormat.format(grandTotalValue));
				
				data.add(total);
				
				statement.close();
			}
			catch (Exception e)
			{
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		} 
		
		// display it in the table
		model.setDataVector(data, columns);
	}
}

