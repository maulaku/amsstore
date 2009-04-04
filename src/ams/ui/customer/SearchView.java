package ams.ui.customer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import ams.Controller;
import ams.model.ItemDAO;

public class SearchView extends JPanel{
	
	private PurchaseOnlinePanel parentPanel;
	
	private JButton searchButton, addToCartButton, checkoutButton;
	private JPanel searchParameterPanel;
	private JTextField titleField, categoryField, leadSingerNameField, quantityField;
	
	public JTable queryJTable, cartJTable;
	
	String queryFields[] = {"Title", "Category", "Quantity"};
	String cartFields[] = {"Title", "Quantity"};
	
	public SearchView(PurchaseOnlinePanel parent) 
	{
		parentPanel = parent;
		setLayout(new BorderLayout(5,5));
				
		initComponents();
		initListeners();
	}	

	private void initComponents() 
	{		
		searchParameterPanel = createSearchParameterPanel();
		
		Dimension tableDimension = new Dimension(150,150);
		queryJTable = new JTable();
//		queryJTable.setSize(tableDimension);
		JScrollPane queryScrollPane = new JScrollPane(queryJTable);
		queryScrollPane.setBorder(BorderFactory.createTitledBorder("Search Results"));
//		queryScrollPane.setSize(tableDimension);
		DefaultTableModel model = (DefaultTableModel) queryJTable.getModel();
		model.setDataVector(null, parentPanel.queryTableColumns);
		
		addToCartButton = new JButton("Add To Cart");	
				
		cartJTable = new JTable();
//		cartJTable.setSize(tableDimension);
		JScrollPane cartScrollPane = new JScrollPane(cartJTable);
//		cartScrollPane.setSize(tableDimension);
		cartScrollPane.setBorder(BorderFactory.createTitledBorder("Cart"));
		model = (DefaultTableModel) cartJTable.getModel();
		model.setDataVector(null, parentPanel.cartTableColumns);
		
		checkoutButton = new JButton("Checkout");
		
		JPanel subPanel = new JPanel();
		JLabel label = new JLabel("Quantity:");		
		quantityField = new JTextField();
		quantityField.setText("1");
		quantityField.setPreferredSize(new Dimension(100, titleField.getPreferredSize().height));		
		subPanel.add(label);
		subPanel.add(quantityField);
		
		
		JPanel p3 = new JPanel();
		p3.setLayout(new BoxLayout(p3, BoxLayout.Y_AXIS));
		p3.add(subPanel);
		p3.add(addToCartButton);
		p3.add(checkoutButton);	
		
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
//		add(p, BorderLayout.NORTH);
		p.add(searchParameterPanel);
		p.add(p3);

		
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
//		add(p2, BorderLayout.SOUTH);
//		p2.add(cartScrollPane);
		p2.add(queryScrollPane);
		p2.add(cartScrollPane);

		
		
		Container pane = new Container();
		add(pane);
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		pane.add(p);
		pane.add(p2);
//		pane.add(p3);
		
	}
	
	private JPanel createSearchParameterPanel()
	{
		searchParameterPanel = new JPanel(new GridLayout(0,1));
		searchParameterPanel.setBorder(BorderFactory.createTitledBorder("Search Parameters"));
//		searchParameterPanel.setSize(new Dimension(200, 200));
		
		JPanel subPanel = new JPanel();
		JLabel label = new JLabel("      Item Title:");
		titleField = new JTextField();
		titleField.setPreferredSize(new Dimension(100, titleField.getPreferredSize().height));		
		subPanel.add(label);
		subPanel.add(titleField);
		searchParameterPanel.add(subPanel);
		
		subPanel = new JPanel();
		label = new JLabel("Item Category:");
		categoryField = new JTextField();
		categoryField.setPreferredSize(new Dimension(100, categoryField.getPreferredSize().height));		
		subPanel.add(label);
		subPanel.add(categoryField);
		searchParameterPanel.add(subPanel);
		
		subPanel = new JPanel();
		label = new JLabel("    Artist Name:");
		leadSingerNameField = new JTextField();
		leadSingerNameField.setPreferredSize(new Dimension(100, leadSingerNameField.getPreferredSize().height));		
		subPanel.add(label);
		subPanel.add(leadSingerNameField);
		searchParameterPanel.add(subPanel);
		
		searchButton = new JButton("Search");
		searchParameterPanel.add(searchButton);	
		
		return searchParameterPanel;
	}
	
	private void initListeners()
	{
		searchButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e)
		{
			onSearch();
		}
		});
		
		addToCartButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e)
		{
			onAddToCart();
		}
		});
		
		checkoutButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e)
		{
			onCheckout();
		}
		});
	}
	
	private void onSearch()
	{
		String targetTitle = titleField.getText();
		String targetCategory = categoryField.getText();		
		String targetLeadSinger = leadSingerNameField.getText();
		
//		Vector<String> columns = Controller.getInstance().getColumnNames("ITEM");
//		Vector<String> columns = new Vector<String>();
//		columns.addElement("TITLE");
//		columns.addElement("CATEGORY");
//		columns.addElement("NAME");
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();		
		
		ResultSet rs = ItemDAO.selectForItemSearch(targetTitle, targetCategory, targetLeadSinger);
		try
		{
			while(rs.next())
			{
				Vector<Object> rowData = new Vector<Object>();
				for (String columnName : parentPanel.queryTableColumns)
					rowData.add(rs.getObject(columnName));
				data.add(rowData);
//				System.out.println("--"+rs.getString("title"));
//				((DefaultListModel) resultsJList.getModel()).addElement(rs.getString("title"));
			}			
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
		
		// display it in the table
		DefaultTableModel model = (DefaultTableModel) queryJTable.getModel();
		model.setDataVector(data, parentPanel.queryTableColumns);		
	}
	
	private void onAddToCart()
	{
		int index = queryJTable.getSelectedRow();
		if(index != -1)
		{			
			DefaultTableModel queries = (DefaultTableModel) queryJTable.getModel();
			DefaultTableModel purchases = (DefaultTableModel) cartJTable.getModel();
			
			Boolean alreadyAdded = false;
			for(int i=0; i<purchases.getRowCount(); i++)
			{
				if(purchases.getValueAt(i, parentPanel.UPC_COLUMN).equals(queries.getValueAt(index, parentPanel.UPC_COLUMN)))
				{
					alreadyAdded = true;
					int initialQuantity = Integer.parseInt(purchases.getValueAt(i,parentPanel.QUANTITY_COLUMN).toString());
					int dQuantity = Integer.parseInt(quantityField.getText());
					purchases.setValueAt(initialQuantity+dQuantity, i, parentPanel.QUANTITY_COLUMN);
				}
			}
			
			if(alreadyAdded == false)			
			{				
				Vector<Object> rowData = new Vector<Object>();
				for(int i=0; i<queries.getColumnCount(); i++)
					rowData.add(queries.getValueAt(index, i));
				rowData.add(quantityField.getText());
				purchases.addRow(rowData);
			}		
		}
	}
	
	private void onCheckout()
	{		
		parentPanel.cartData = ((DefaultTableModel) cartJTable.getModel()).getDataVector();
		parentPanel.nextView(1);
	}
	
	public void cleanUp()
	{
		titleField.setText("");
		categoryField.setText("");
		leadSingerNameField.setText("");
		
		DefaultTableModel model = (DefaultTableModel) queryJTable.getModel();
		model.setDataVector(null, parentPanel.queryTableColumns);
		
		model = (DefaultTableModel) cartJTable.getModel();
		model.setDataVector(null, parentPanel.cartTableColumns);
	}
}
