package ams.ui.customer;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ams.model.Item;


public class PurchaseOnlinePanel extends JPanel {
	
	private JPanel contentPanel;
	private CardLayout cardLayout;
	
	private ArrayList<MyPanel> views;
	private HashMap<Item,Integer> checkedOut;
	
	public int currentCustomerId = -1;
	public String currentCustomerName = "";
	
	public final int UPC_COLUMN = 0;
	public final int QUANTITY_COLUMN = 3;
	
	private JPanel logoutPanel;
	private JLabel welcomeLabel;
	private JButton logoutButton = new JButton("Logout");
	private JButton backButton = new JButton("Back");
	
	public PurchaseOnlinePanel() {
		setLayout(new BorderLayout());
		views = new ArrayList<MyPanel>();
		initComponents();
		initListeners();
	}

	private void initComponents() {		
		
		cardLayout = new CardLayout();
		contentPanel = new JPanel(cardLayout);
		MyPanel panel = new LoginView(this);
		views.add(panel);
		contentPanel.add(panel, LoginView.ID);
		panel = new RegistrationView(this);
		views.add(panel);
		contentPanel.add(panel, RegistrationView.ID);
		panel = new SearchView(this);
		views.add(panel);
		contentPanel.add(panel, SearchView.ID);
		panel = new CheckoutView(this);
		views.add(panel);
		contentPanel.add(panel, CheckoutView.ID);		
		
		welcomeLabel = new JLabel();
		logoutPanel = new JPanel(new GridLayout(1,2));
		logoutPanel.setBackground(Color.WHITE);
		logoutPanel.add(welcomeLabel);
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.add(backButton);
		buttonPanel.add(logoutButton);
		logoutPanel.add(buttonPanel);
		logoutPanel.setVisible(false);
		
		add(contentPanel, BorderLayout.CENTER);
		add(logoutPanel, BorderLayout.NORTH);
	}
	
	public void nextView(String ID)
	{
		cardLayout.show(contentPanel, ID);
		logoutPanel.setVisible(true);
		backButton.setVisible(false);
		if (ID.equals(LoginView.ID) || ID.equals(RegistrationView.ID))
			logoutPanel.setVisible(false);
		if (ID.equals(CheckoutView.ID))
			backButton.setVisible(true);
	}

	private void initListeners() 
	{
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				nextView(SearchView.ID);	
			}
		});
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
		currentCustomerId = -1;
		currentCustomerName = "";
		nextView(LoginView.ID);
		setWelcomeText("");
		for (MyPanel panel : views)
			panel.cleanUp();
	}
	
	public void setCheckoutItems(HashMap<Item, Integer> cartItems)
	{
		checkedOut = cartItems;
	}
	
	public HashMap<Item, Integer> getCartItems()
	{
		return checkedOut;
	}
	
	public void setWelcomeText(String text)
	{
		welcomeLabel.setText(text);
	}

}
