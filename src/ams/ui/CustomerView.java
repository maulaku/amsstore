package ams.ui;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import ams.ui.clerk.ItemPurchasePanel;
import ams.ui.clerk.ItemReturnPanel;
import ams.ui.customer.PurchaseOnlinePanel;
import ams.ui.customer.RegistrationView;

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
		addTab("<html><body leftmargin=25 topmargin=8 marginwidth=45 marginheight=10>Purchase Online</body></html>", new PurchaseOnlinePanel());
	}
}
