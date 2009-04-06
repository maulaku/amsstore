package ams.ui;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import ams.ui.clerk.ItemPurchasePanel;
import ams.ui.clerk.ItemReturnPanel;
import ams.ui.customer.PurchaseOnlinePanel;
import ams.ui.manager.DailySalesPanel;
import ams.ui.manager.DeliveryPanel;
import ams.ui.manager.ShipmentPanel;
import ams.ui.manager.SupplierPanel;
import ams.ui.manager.TopSellingItemsPanel;




public class ManagerView extends JTabbedPane {

	public static final String ID = "MANAGERVIEW";
	
	public ManagerView()
	{
		setFocusable(false);
		setTabPlacement(JTabbedPane.LEFT);
		setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		initComponents();
	}
	
	private void initComponents()
	{
		addTab("<html><body leftmargin=15 topmargin=8 marginwidth=12 marginheight=10>Supplier</body></html>", new SupplierPanel());
		addTab("<html><body leftmargin=15 topmargin=8 marginwidth=12 marginheight=10>Process Supplier Shipment</body></html>", new ShipmentPanel());
		addTab("<html><body leftmargin=15 topmargin=8 marginwidth=12 marginheight=10>Daily Sales Report</body></html>", new DailySalesPanel());
		addTab("<html><body leftmargin=15 topmargin=8 marginwidth=12 marginheight=10>Top Selling Items</body></html>", new TopSellingItemsPanel());
		addTab("<html><body leftmargin=15 topmargin=8 marginwidth=12 marginheight=10>Process Order Delivery</body></html>", new DeliveryPanel());		
	}
}
