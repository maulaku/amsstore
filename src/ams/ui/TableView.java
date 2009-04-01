package ams.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import ams.Controller;

public class TableView extends JPanel
{
	
	public static final String ID = "TABLEVIEW";
	
	private JList list;
	private JTable table, insertTable;
	private JButton insertButton;
	
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
		
		insertTable = new JTable();
		insertTable.setBackground(Color.WHITE);
		insertTable.getTableHeader().setReorderingAllowed(false);
		insertButton = new JButton("Insert");
		
		JPanel modifyPanel = new JPanel();
		modifyPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		modifyPanel.setLayout(new BoxLayout(modifyPanel, BoxLayout.Y_AXIS));
		JPanel panel = new JPanel();
		panel.add(new JLabel("To delete, select a tuple from the table above and press delete."));
		modifyPanel.add(panel);
		panel = new JPanel(new BorderLayout(5,5));
		panel.add(new JLabel("To insert, enter values: "), BorderLayout.WEST);
		JScrollPane insertPane = new JScrollPane(insertTable);
		insertPane.setPreferredSize(new Dimension(insertPane.getPreferredSize().width, insertTable.getRowHeight() * 2 + 8));
		insertPane.setMaximumSize(new Dimension(insertPane.getPreferredSize().width, insertTable.getRowHeight() * 2 + 8));
		panel.add(insertPane, BorderLayout.CENTER);		
		panel.add(insertButton, BorderLayout.EAST);
		modifyPanel.add(panel);
		
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(tablePane, BorderLayout.CENTER);
		rightPanel.add(modifyPanel, BorderLayout.SOUTH);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setLeftComponent(listPane);
		splitPane.setRightComponent(rightPanel);
		
		add(splitPane, BorderLayout.CENTER);
	}
	
	private void initListeners()
	{
		list.addListSelectionListener(new ListSelectionListener() {
//			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				table.removeAll();
				insertTable.removeAll();
				if (!list.isSelectionEmpty())
				{
					String tableName = (String) list.getSelectedValue();
					Vector<String> columns = Controller.getInstance().getColumnNames(tableName);
					
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
					DefaultTableModel model = (DefaultTableModel) table.getModel();
					model.setDataVector(data, columns);
					
					Vector<Object> emptyVector = new Vector<Object>();
					for (int i = 0; i < columns.size(); ++i)
						emptyVector.add("");
					data = new Vector<Vector<Object>>();
					data.add(emptyVector);
					DefaultTableModel insertModel = (DefaultTableModel) insertTable.getModel();
					Vector<Object> a = new Vector<Object>(columns);
					insertModel.setDataVector(data, a);
					
				}
			}
		});
		
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (table.getSelectedRowCount() <= 0)
					return;
				if (e.getKeyCode() == KeyEvent.VK_DELETE)
					deleteTuple(table.getSelectedRow());
			}
		});
		
		insertButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				insertTuple();
			}
		});
	}
	
	private void populateTableList()
	{
		for (String tableName : Controller.getInstance().getTableNames())
			((DefaultListModel) list.getModel()).addElement(tableName);		
	}
	
	private void insertTuple()
	{
		Vector<Object> values = new Vector<Object>();
		DefaultTableModel insertModel = (DefaultTableModel) insertTable.getModel();
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		for (int i = 0; i < insertModel.getColumnCount(); ++i)
			values.add(insertModel.getValueAt(0, i));
		
		try
		{
			Controller.getInstance().insertTuple((String) list.getSelectedValue(), values);
			model.addRow(values);
			for (int i = 0; i < insertModel.getColumnCount(); ++i)
				insertModel.setValueAt("", 0, i);
			Controller.getInstance().setStatusString("Insert Successful.", AMSFrame.SUCCESS);
		} 
		catch (SQLException e)
		{
			Controller.getInstance().setStatusString("Insert Failed: " + e.getMessage(), AMSFrame.FAILURE);
			e.printStackTrace();
		}
	}
	
	private void deleteTuple(int rowNum)
	{		
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		
		try
		{
			Controller.getInstance().deleteTuple((String) list.getSelectedValue(), (Vector<Object>) model.getDataVector().get(rowNum));
			model.removeRow(rowNum);
			Controller.getInstance().setStatusString("Tuple Deleted.", AMSFrame.SUCCESS);
		} catch (SQLException e)
		{
			Controller.getInstance().setStatusString("Delete Failed: " + e.getMessage(), AMSFrame.FAILURE);
			e.printStackTrace();
		}		
	}
}
