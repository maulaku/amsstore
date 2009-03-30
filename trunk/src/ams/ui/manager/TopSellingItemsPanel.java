package ams.ui.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import ams.Controller;

public class TopSellingItemsPanel extends JPanel
{
	private JTable table;
	private JButton searchButton;
	private JTextField numField;
	
	public TopSellingItemsPanel()
	{
		setLayout(new BorderLayout(5,5));
		initComponents();
		initListeners();
	}
	
	private void initComponents()
	{
		// button panel code
		searchButton = new JButton("Search");
		searchButton.setPreferredSize(new Dimension(100, searchButton.getPreferredSize().height));

		//TODO: Add Date picker
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JLabel label = new JLabel("Number of Results:");
		buttonPanel.add(label);
		numField = new JTextField();
		numField.setPreferredSize(new Dimension(100, numField.getPreferredSize().height));
		numField.setEditable(false);
		buttonPanel.add(numField);
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
		if (columns != null)
		{	
			//TODO: Get date string
			int limit = Integer.parseInt(numField.getText());
			String date = "";
			String queryString = "SELECT I.upc, I.title, I.company, S.stock, SUM(PI.quantity) AS total, P.date FROM Item I, Stored S, PurchaseItem PI, Purchase P WHERE I.upc = PI.upc AND I.upc = S.upc AND PI.receiptId = P.receiptId AND P.date = '" + date + "' GROUP BY I.upc ORDER BY total DESC LIMIT " + limit;
			
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

