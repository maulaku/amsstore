package ams.ui.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import ams.Controller;

public class DailySalesPanel extends JPanel
{
	private JTable table;
	
	public DailySalesPanel()
	{
		setLayout(new BorderLayout(5,5));
		initComponents();
	}
	
	private void initComponents()
	{
		table = new JTable() {
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		table.setBackground(Color.WHITE);

		populateTableList();
		
		
		//TODO: Add Total Daily Sales
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
				//TODO: Change back to current_date
				PreparedStatement statement = Controller.getInstance().getConnection().prepareStatement("SELECT i.upc, i.category, i.price, pi.quantity FROM Item i, PurchaseItem pi WHERE pi.receiptId IN (SELECT receiptId FROM Purchase WHERE purchasedate = ?) ORDER BY i.category ASC");
				statement.setDate(1, java.sql.Date.valueOf("2009-04-01"));
				ResultSet results = statement.executeQuery();
				
				//Group results by category
				HashMap<String, Vector<Vector<Object>>> categories = new HashMap<String, Vector<Vector<Object>>>();
				while(results.next())
				{
					Vector<Object> rowData = new Vector<Object>();

					rowData.add(results.getObject("upc"));
					
					String category = (String) results.getObject("category");
					rowData.add(category);
					
					double price = Double.parseDouble((String) results.getObject("price"));
					rowData.add(price);
					
					double quantity = Double.parseDouble((String) results.getObject("quantity"));
					rowData.add(quantity);
					
					double total = price * quantity;
					rowData.add(total);
					
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
					double totalValue = 0.00;
					
					//Add category rows to data set
					for(Vector<Object> row : category)
					{
						totalValue += Double.parseDouble((String) row.get(row.size() - 1));
						data.add(row);
					}
					
					//Add to grand total
					grandTotalValue += totalValue;
					grandTotalUnits += category.size();
					
					//Add category's total to data set
					Vector<Object> categoryTotal = new Vector<Object>();
					categoryTotal.add(null);
					categoryTotal.add("Total");
					categoryTotal.add(null);
					categoryTotal.add(category.size());
					
					categoryTotal.add(totalValue);
					data.add(categoryTotal);
				}

				//Add spacer
				Vector<Object> spacer = new Vector<Object>();
				spacer.add(null);
				spacer.add(null);
				spacer.add(null);
				spacer.add(null);
				spacer.add(null);
				
				//TODO: Display total daily sales outside of table
				//Add total daily sales to data set
				Vector<Object> total = new Vector<Object>();
				total.add(null);
				total.add("Total Daily Sales");
				total.add(null);
				total.add(grandTotalUnits);
				total.add(grandTotalValue);
				
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

