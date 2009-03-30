package ams.ui;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import ams.ui.clerk.ItemPurchasePanel;
import ams.ui.clerk.ItemReturnPanel;
import ams.ui.customer.PurchaseOnlinePanel;
import ams.ui.customer.RegistrationPanel;
import ams.ui.manager.DailySalesPanel;
import ams.ui.manager.SupplierPanel;
import ams.ui.manager.TopSellingItemsPanel;




public class ManagerView extends JTabbedPane {

	public static final String ID = "MANAGERVIEW";
	
	public ManagerView()
	{
		setTabPlacement(JTabbedPane.LEFT);
		initComponents();
	}
	
	private void initComponents()
	{
//		addTab("Purchase Item Online", new PurchaseOnlinePanel());
		addTab("Registration", new RegistrationPanel());
		addTab("Supplier", new SupplierPanel());
		addTab("Daily Sales Report", new DailySalesPanel());
		addTab("Top Selling Items", new TopSellingItemsPanel());
	}
}
