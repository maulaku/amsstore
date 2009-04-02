package ams.ui;

import javax.swing.JTabbedPane;

import ams.ui.clerk.ItemPurchasePanel;
import ams.ui.clerk.ItemReturnPanel;

public class ClerkView extends JTabbedPane {
	
	public static final String ID = "CLERKVIEW";
	
	public ClerkView()
	{
		setFocusable(false);
		setTabPlacement(JTabbedPane.LEFT);
		initComponents();
	}
	
	private void initComponents()
	{	
		addTab("<html><body leftmargin=15 topmargin=8 marginwidth=20 marginheight=10>Regular Item Purchase</body></html>", new ItemPurchasePanel());
		addTab("<html><body leftmargin=15 topmargin=8 marginwidth=20 marginheight=10>Item Return</body></html>", new ItemReturnPanel());
	}
	
}
