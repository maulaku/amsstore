package ams.ui.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import ams.Controller;

public class TopSellingItemsPanel extends JPanel
{
	private JTable table;
	private JButton searchButton;
	private JTextField numField;
	private CalendarPanel calendarPanel;
	
	public TopSellingItemsPanel()
	{
		setBackground(Color.WHITE);
		setLayout(new BorderLayout(5,5));
		initComponents();
		initListeners();
	}
	
	private void initComponents()
	{
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		buttonPanel.setBackground(Color.WHITE);
		
		calendarPanel = new CalendarPanel();
		calendarPanel.setBackground(Color.WHITE);
		calendarPanel.setPreferredSize(new Dimension(calendarPanel.getPreferredSize().width, 150));
		calendarPanel.setCalendar(Calendar.getInstance());
		calendarPanel.updateDisplay();
		buttonPanel.add(calendarPanel);

		JLabel resultsLabel = new JLabel("Number of Results:");
		buttonPanel.add(resultsLabel);
		numField = new JTextField();
		numField.setPreferredSize(new Dimension(100, numField.getPreferredSize().height));
		numField.setText("10");
		buttonPanel.add(numField);

		// button panel code
		searchButton = new JButton("Search");
		searchButton.setPreferredSize(new Dimension(100, searchButton.getPreferredSize().height));
		buttonPanel.add(searchButton);

		JPanel tablePanel = new JPanel(new BorderLayout(5,5));
		tablePanel.setBackground(Color.WHITE);
//		tablePanel.setBorder(BorderFactory.createTitledBorder("Top Selling Items"));
		tablePanel.add(buttonPanel, BorderLayout.NORTH);
		
		table = new JTable() {
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};

		table.setBackground(Color.WHITE);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBackground(Color.WHITE);
		tablePanel.add(scrollPane, BorderLayout.CENTER);
		
		add(tablePanel, BorderLayout.CENTER);
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
		columns.add("Title");
		columns.add("Company");
		columns.add("Store");
		columns.add("Stock");
		columns.add("Sold");

		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		if(columns != null)
		{	
			try
			{
				int limit;
				try
				{
					limit = Integer.parseInt(numField.getText().trim());
					if(limit < 0)
					{
						throw new NumberFormatException();
					}
				}
				catch(NumberFormatException e)
				{
					limit = 0;
				}
				
				PreparedStatement statement = Controller.getInstance().getConnection().prepareStatement("SELECT upc, title, company, name AS store, stock, sold FROM Item INNER JOIN (SELECT upc, name, stock FROM Stored) USING (upc) INNER JOIN (SELECT i.upc, SUM(pi.quantity) AS sold FROM Item i, PurchaseItem pi, Purchase p WHERE i.upc = pi.upc AND p.receiptId = pi.receiptId AND p.purchasedate = ? GROUP BY i.upc ORDER BY sold DESC) USING (upc) WHERE ROWNUM <= ?");
				statement.setDate(1, calendarPanel.getSelectedDate());
				statement.setInt(2, limit);
				ResultSet results = statement.executeQuery();
				
				// add data into the table
				while(results.next())
				{
					Vector<Object> rowData = new Vector<Object>();
					for (String columnName : columns)
					{
						rowData.add(results.getObject(columnName));
					}
					data.add(rowData);
				}
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

