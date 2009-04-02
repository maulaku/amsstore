package ams.ui;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import ams.ui.clerk.ItemPurchasePanel;
import ams.ui.clerk.ItemReturnPanel;
import ams.ui.customer.PurchaseOnlinePanel;
import ams.ui.customer.RegistrationPanel;

public class CustomerView extends JTabbedPane {

	public static final String ID = "CUSTOMERVIEW";
	
	public CustomerView()
	{
		setFocusable(false);
		setTabPlacement(JTabbedPane.LEFT);
		initComponents();
	}
	
	private void initComponents()
	{
//		addTab("Purchase Item Online", new PurchaseOnlinePanel());
		addTab("<html><body leftmargin=25 topmargin=8 marginwidth=45 marginheight=10>Registration</body></html>", new RegistrationPanel());
	}
}
