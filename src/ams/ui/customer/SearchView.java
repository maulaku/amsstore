package ams.ui.customer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import ams.Controller;
import ams.model.Item;
import ams.model.ItemDAO;
import ams.ui.AMSFrame;

public class SearchView extends MyPanel
{

	public static final String ID = "SEARCH";

	private PurchaseOnlinePanel parentPanel;

	private JButton searchButton, addToCartButton, checkoutButton;
	private JPanel searchParameterPanel;
	private JTextField titleField, categoryField, leadSingerNameField;

	public JTable queryJTable, cartJTable;

	String queryTableColumns[] = new String[] { "UPC", "Title", "Category", "Quantity", "Add to Cart" };
	String cartTableColumns[] = new String[] { "UPC", "Title", "Category", "Quantity" };

	private HashMap<Item, Integer> cartItems;

	public SearchView(PurchaseOnlinePanel parent)
	{
		setBackground(Color.WHITE);
		parentPanel = parent;
		cartItems = new HashMap<Item, Integer>();
		setLayout(new BorderLayout(5, 5));

		initComponents();
		initListeners();
	}

	private void initComponents()
	{
		searchParameterPanel = createSearchParameterPanel();

		queryJTable = new JTable()
		{
			@Override
			public Class<?> getColumnClass(int column)
			{
				if (column == 3)
					return String.class;
				if (column == 4)
					return Boolean.class;
				return getValueAt(0, column).getClass();
			}

			@Override
			public boolean isCellEditable(int row, int column)
			{
				if (column == 3 || column == 4)
					return true;
				return false;
			}
		};
		queryJTable.setDefaultRenderer(Boolean.class, new CheckBoxRenderer());
		queryJTable.setDefaultEditor(Boolean.class, new DefaultCellEditor(new JCheckBox()));

		JScrollPane queryScrollPane = new JScrollPane(queryJTable);
		queryScrollPane.setBackground(Color.WHITE);
		queryScrollPane.setBorder(BorderFactory.createTitledBorder("Search Results"));
		// queryScrollPane.setSize(tableDimension);
		DefaultTableModel model = (DefaultTableModel) queryJTable.getModel();
		model.setDataVector(null, queryTableColumns);

		addToCartButton = new JButton("Add To Cart");

		cartJTable = new JTable() {
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		JScrollPane cartScrollPane = new JScrollPane(cartJTable);
		cartScrollPane.setBackground(Color.WHITE);
		cartScrollPane.setBorder(BorderFactory.createTitledBorder("Cart"));
		model = (DefaultTableModel) cartJTable.getModel();
		model.setDataVector(null, cartTableColumns);

		checkoutButton = new JButton("Checkout");

		JPanel queryPanel = new JPanel(new BorderLayout());
		queryPanel.add(queryScrollPane, BorderLayout.CENTER);
		queryPanel.add(addToCartButton, BorderLayout.SOUTH);

		JPanel cartPanel = new JPanel(new BorderLayout());
		cartPanel.add(cartScrollPane, BorderLayout.CENTER);
		cartPanel.add(checkoutButton, BorderLayout.SOUTH);

		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(queryPanel, BorderLayout.CENTER);
		southPanel.add(cartPanel, BorderLayout.EAST);

		JPanel pane = new JPanel(new BorderLayout());
		pane.add(searchParameterPanel, BorderLayout.NORTH);
		pane.add(southPanel, BorderLayout.CENTER);

		add(pane, BorderLayout.CENTER);
	}

	private JPanel createSearchParameterPanel()
	{
		searchParameterPanel = new JPanel();
		searchParameterPanel.setBackground(Color.WHITE);
		searchParameterPanel.setBorder(BorderFactory.createTitledBorder("Search Parameters"));
		// searchParameterPanel.setSize(new Dimension(200, 200));

		JPanel subPanel = new JPanel();
		subPanel.setBackground(Color.WHITE);
		JLabel label = new JLabel("      Item Title:");
		titleField = new JTextField();
		titleField.setPreferredSize(new Dimension(100, titleField.getPreferredSize().height));
		subPanel.add(label);
		subPanel.add(titleField);
		searchParameterPanel.add(subPanel);

		subPanel = new JPanel();
		subPanel.setBackground(Color.WHITE);
		label = new JLabel("Item Category:");
		categoryField = new JTextField();
		categoryField.setPreferredSize(new Dimension(100, categoryField.getPreferredSize().height));
		subPanel.add(label);
		subPanel.add(categoryField);
		searchParameterPanel.add(subPanel);

		subPanel = new JPanel();
		subPanel.setBackground(Color.WHITE);
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
		searchButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				onSearch();
			}
		});

		addToCartButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				onAddToCart();
			}
		});

		checkoutButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				onCheckout();
			}
		});
		
		cartJTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (cartJTable.getSelectedRowCount() <= 0)
					return;
				if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
					onRemoveFromCart(cartJTable.getSelectedRow());
			}
		});
	}

	private void onSearch()
	{
		String targetTitle = titleField.getText();
		String targetCategory = categoryField.getText();
		String targetLeadSinger = leadSingerNameField.getText();

		Vector<Item> rs = ItemDAO.selectForItemSearch(targetTitle, targetCategory, targetLeadSinger);
		Object[][] data = new Object[rs.size()][5];
		for (int i = 0; i < rs.size(); ++i)
		{
			Item item = rs.get(i);
			data[i][0] = item.getUPC();
			data[i][1] = item.getTitle();
			data[i][2] = item.getCategory();
			data[i][3] = 0;
			data[i][4] = false;

			// System.out.println("--"+rs.getString("title"));
			// ((DefaultListModel)
			// resultsJList.getModel()).addElement(rs.getString("title"));
		}

		// display it in the table
		DefaultTableModel model = (DefaultTableModel) queryJTable.getModel();
		model.setDataVector(data, queryTableColumns);
	}

	private void onAddToCart()
	{
		DefaultTableModel queries = (DefaultTableModel) queryJTable.getModel();

		for (int i = 0; i < queries.getRowCount(); ++i)
		{
			if ((Boolean) queries.getValueAt(i, 4))
			{
				Item item = new Item((Long) queries.getValueAt(i, 0));
				item.setTitle((String) queries.getValueAt(i, 1));
				item.setCategory((String) queries.getValueAt(i, 2));
				try
				{
					int quantity = Integer.parseInt(queries.getValueAt(i, parentPanel.QUANTITY_COLUMN).toString());
					if (cartItems.containsKey(item))
						quantity += cartItems.get(item);
					if(quantity <= 0)
					{
						Controller.getInstance().setStatusString("Item quantity must be greater than zero.", AMSFrame.FAILURE);
						return;
					}
						

					cartItems.put(item, quantity);
				} catch (NumberFormatException e)
				{
					Controller.getInstance().setStatusString(
							"Cannot Add to Cart: quantity of " + item.getUPC() + " invalid", AMSFrame.FAILURE);
					return;
				}
			}
		}

		cartJTable.removeAll();
		DefaultTableModel purchases = (DefaultTableModel) cartJTable.getModel();
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		for (Item item : cartItems.keySet())
		{
			Vector<Object> row = new Vector<Object>();
			row.add(item.getUPC());
			row.add(item.getTitle());
			row.add(item.getCategory());
			row.add(cartItems.get(item));
			data.add(row);
		}
		Vector<Object> columns = new Vector<Object>();
		Collections.addAll(columns, cartTableColumns);
		purchases.setDataVector(data, columns);
	}

	private void onCheckout()
	{
		parentPanel.setCheckoutItems(cartItems);
		parentPanel.nextView(CheckoutView.ID);
	}

	public void cleanUp()
	{
		titleField.setText("");
		categoryField.setText("");
		leadSingerNameField.setText("");

		DefaultTableModel model = (DefaultTableModel) queryJTable.getModel();
		model.setDataVector(null, queryTableColumns);
		
		model = (DefaultTableModel) cartJTable.getModel();
		model.setDataVector(null, cartTableColumns);
		
		cartItems.clear();
		queryJTable.updateUI();
		cartJTable.updateUI();
	}

	private class CheckBoxRenderer implements TableCellRenderer
	{

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column)
		{

			JCheckBox box = new JCheckBox();
			box.setBackground(Color.WHITE);
			boolean val = (Boolean) value;
			box.setSelected(val);
			return box;
		}
	}
	
	private void onRemoveFromCart(int rowIndex)
	{
		DefaultTableModel cartRows = (DefaultTableModel) cartJTable.getModel();
		Item item = new Item((Long) cartRows.getValueAt(rowIndex, 0));
		cartRows.removeRow(rowIndex);
		
		cartItems.remove(item);
	}

}
