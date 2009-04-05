package ams.ui.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
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

import com.greef.ui.calendar.DefaultDateSelectionModel;
import com.greef.ui.calendar.JCalendar;

import ams.Controller;
import assets.CalendarPanel;

public class TopSellingItemsPanel extends JPanel
{
	private JTable table;
	private JButton searchButton;
	private JTextField numField;
	private CalendarPanel calendarPanel;
	
	public TopSellingItemsPanel()
	{
		setLayout(new BorderLayout(5,5));
		initComponents();
		initListeners();
	}
	
	private void initComponents()
	{
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		
		calendarPanel = new CalendarPanel();
		calendarPanel.setPreferredSize(new Dimension(calendarPanel.getPreferredSize().width, 200));
		calendarPanel.setCalendar(Calendar.getInstance());
		calendarPanel.updateDisplay();
		buttonPanel.add(calendarPanel);

		JLabel resultsLabel = new JLabel("Number of Results:");
		buttonPanel.add(resultsLabel);
		numField = new JTextField();
		numField.setPreferredSize(new Dimension(100, numField.getPreferredSize().height));
		buttonPanel.add(numField);

		// button panel code
		searchButton = new JButton("Search");
		searchButton.setPreferredSize(new Dimension(100, searchButton.getPreferredSize().height));
		buttonPanel.add(searchButton);

		JPanel tablePanel = new JPanel(new BorderLayout(5,5));
		tablePanel.setBorder(BorderFactory.createTitledBorder("Top Selling Items"));
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
		tablePanel.add(scrollPane, BorderLayout.SOUTH);
		
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
		columns.add("upc");
		columns.add("title");
		columns.add("company");
		columns.add("stock");
		columns.add("total");

		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		if(columns != null)
		{	
			try
			{
				Date date = calendarPanel.getSelectedDate();
				
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
				
				PreparedStatement statement = Controller.getInstance().getConnection().prepareStatement("SELECT upc, title, company, stock, total FROM Item INNER JOIN (SELECT upc, stock FROM Stored) USING (upc) INNER JOIN (SELECT i.upc, SUM(pi.quantity) AS total FROM Item i, PurchaseItem pi, Purchase p WHERE i.upc = pi.upc AND p.receiptId = pi.receiptId AND p.purchasedate = ? GROUP BY i.upc) USING (upc) WHERE ROWNUM <= ? ORDER BY total DESC");
				statement.setDate(1, date);
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

