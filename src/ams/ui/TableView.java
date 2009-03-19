package ams.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import ams.Controller;

public class TableView extends JPanel
{
	
	public static final String ID = "TABLEVIEW";
	
	private JList list;
	private JTable table;
	
	public TableView()
	{
		setLayout(new BorderLayout(5,5));
		initComponents();
		initListeners();
	}
	
	private void initComponents()
	{
		list = new JList(new DefaultListModel()); 
		JScrollPane listPane = new JScrollPane(list);
		populateTableList();
		
		table = new JTable() {
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		table.setBackground(Color.WHITE);
		JScrollPane tablePane = new JScrollPane(table);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setLeftComponent(listPane);
		splitPane.setRightComponent(tablePane);
		
		add(splitPane, BorderLayout.CENTER);
	}
	
	private void initListeners()
	{
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				table.removeAll();
				if (!list.isSelectionEmpty())
				{
					String tableName = (String) list.getSelectedValue();
					Vector<String> columns = Controller.getInstance().getColumnNames(tableName);
					DefaultTableModel model = (DefaultTableModel) table.getModel();
					Vector<Vector<Object>> data = new Vector<Vector<Object>>();
					if (columns != null)
					{	
						String queryString = "SELECT * FROM " + tableName;
						
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
						} catch (Exception ex)
						{}
						
					} 
					// display it in the table
					model.setDataVector(data, columns);
				}
			}
		});
	}
	
	private void populateTableList()
	{
		for (String tableName : Controller.getInstance().getTableNames())
			((DefaultListModel) list.getModel()).addElement(tableName);		
	}
}
