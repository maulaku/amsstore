package ams.ui;

import javax.swing.JTabbedPane;

import ams.ui.clerk.ItemPurchasePanel;
import ams.ui.clerk.ItemReturnPanel;

public class ClerkView extends JTabbedPane {
	
	public static final String ID = "CLERKVIEW";
	
	public ClerkView()
	{
		setTabPlacement(JTabbedPane.LEFT);
		initComponents();
	}
	
	private void initComponents()
	{
		addTab("Regular Item Purchase", new ItemPurchasePanel());
		addTab("Item Return", new ItemReturnPanel());
	}
	
}
