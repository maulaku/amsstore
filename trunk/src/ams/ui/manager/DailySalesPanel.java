package ams.ui.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
		populateTableList();
		
		table = new JTable() {
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		table.setBackground(Color.WHITE);

		//TODO: Add Total Daily Sales
	}
	
	private void populateTableList()
	{
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		
		Vector<String> columns = Controller.getInstance().getColumnNames("Item");
		columns.add("upc");
		columns.add("quantity");

		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		if (columns != null)
		{	
			//TODO: Group by category
			String queryString = "SELECT I.*, PI.upc, PI.quantity FROM Item I, PurchaseItem PI WHERE PI.receiptId IN (SELECT receiptId FROM Purchase WHERE date = current_date)";
			
			try
			{
				PreparedStatement statement = Controller.getInstance().getConnection().prepareStatement(queryString);
				ResultSet results = statement.executeQuery();
				
				// add data into the table
				while (results.next())
				{
					Vector<Object> rowData = new Vector<Object>();
					for (String columnName : columns)
						rowData.add(results.getObject(columnName));
					data.add(rowData);
				}
				statement.close();
			}
			catch (Exception e)
			{
				
			}
			
		} 
		// display it in the table
		model.setDataVector(data, columns);
	}
}

