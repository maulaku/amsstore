package ams.ui.customer;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.table.DefaultTableModel;


public class PurchaseOnlinePanel extends JPanel {
	private ArrayList<JPanel> views;
	private int currentView = 0;
	
	Vector<Vector<Object>> cartData;
	public Vector<String> queryTableColumns;
	public Vector<String> cartTableColumns;
	public int currentCustomerId = -1;
	
	public final int UPC_COLUMN = 0;
	public final int QUANTITY_COLUMN = 4;
	
	private JButton logoutButton = new JButton("Logout");
	
	public PurchaseOnlinePanel() {
		initComponents();
		initListeners();
	}

	private void initComponents() {		
		queryTableColumns = new Vector<String>();
		queryTableColumns.addElement("UPC");
		queryTableColumns.addElement("TITLE");
		queryTableColumns.addElement("CATEGORY");
		queryTableColumns.addElement("NAME");
		
		cartTableColumns = new Vector<String>();
		cartTableColumns.addElement("UPC");
		cartTableColumns.addElement("TITLE");
		cartTableColumns.addElement("CATEGORY");
		cartTableColumns.addElement("NAME");
		cartTableColumns.addElement("QUANTITY");
		
		cartData = new Vector<Vector<Object>>();
		
		views = new ArrayList<JPanel>();
		
		//View 0
		LoginView loginView = new LoginView(this);	
		loginView.setVisible(false);		
		views.add(loginView);
		add(loginView);
		
		//View 1
		RegistrationView registrationView = new RegistrationView(this);		
		registrationView.setVisible(false);		
		views.add(registrationView);
		add(registrationView);
		
		//View 2
		SearchView searchView = new SearchView(this);		
		searchView.setVisible(false);		
		views.add(searchView);
		add(searchView);
		
		//View 3
		CheckoutView checkoutView = new CheckoutView(this);
		checkoutView.setVisible(false);
		views.add(checkoutView);
		add(checkoutView);
		
		views.get(currentView).setVisible(true);
		
		logoutButton.setVisible(false);
		add(logoutButton, BorderLayout.SOUTH);
	}
	
	public void nextView(int flag)
	{
		views.get(currentView).setVisible(false);
		currentView += flag;
		views.get(currentView).setVisible(true);
		if(currentView == 3)
		{
			((CheckoutView)views.get(currentView)).initCart();
		}
		if(currentView > 1)
		{
			logoutButton.setVisible(true);
		}
		else
		{
			logoutButton.setVisible(false);
		}
	}

	private void initListeners() 
	{
		logoutButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e)
		{
			onLogout();
		}
		});
	}

	private void onLogout()
	{
		cartData = null;
		currentCustomerId = -1;
		nextView(-1*currentView);
		currentView=0;
		((LoginView)views.get(0)).cleanUp();
		((RegistrationView)views.get(1)).cleanUp();
		((SearchView)views.get(2)).cleanUp();
		((CheckoutView)views.get(3)).cleanUp();
	}

}
