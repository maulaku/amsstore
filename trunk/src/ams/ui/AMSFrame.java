package ams.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class AMSFrame extends JFrame
{
	public static final Color SUCCESS = new Color(0,200,0,200);
	
	public static final Color FAILURE = new Color(200,0,0,200);
	
	private JPanel contentPanel;
	private CardLayout cardLayout;
	private JLabel statusLabel;
	
	public AMSFrame()
	{
		setTitle("Allegro Music Store");
		setPreferredSize(new Dimension(1024,768));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(Color.WHITE);
		initComponents();
	}
	
	private void addButtonListener(JToggleButton button, final String id)
	{
		button.addActionListener(new ActionListener() {
//			@Override
			public void actionPerformed(ActionEvent e)
			{
				cardLayout.show(contentPanel, id);
			}
		});
	}
	
	private void initComponents()
	{
		JPanel buttonPanel = new JPanel();
		ButtonGroup group = new ButtonGroup();
		JToggleButton button = new JToggleButton("Clerk");
		button.setSelected(true); // default view
		group.add(button);
		buttonPanel.add(button);
		addButtonListener(button, ClerkView.ID);
		button = new JToggleButton("Customer");
		addButtonListener(button, CustomerView.ID);
		group.add(button);
		buttonPanel.add(button);
		button = new JToggleButton("Manager");
		addButtonListener(button, ManagerView.ID);
		group.add(button);
		buttonPanel.add(button);
		button = new JToggleButton("Table");
		addButtonListener(button, TableView.ID);
		group.add(button);
		buttonPanel.add(button);
		
		buttonPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		contentPanel = new JPanel(cardLayout = new CardLayout());
		contentPanel.add(new ClerkView(), ClerkView.ID);
		contentPanel.add(new CustomerView(), CustomerView.ID);
		contentPanel.add(new ManagerView(), ManagerView.ID);
		contentPanel.add(new TableView(), TableView.ID);
		
		contentPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		statusLabel = new JLabel(" ");
		statusLabel.setOpaque(true);
		JPanel statusPanel = new JPanel(new BorderLayout());
		statusPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		statusPanel.add(statusLabel, BorderLayout.CENTER);
				
		JPanel mainPanel = new JPanel(new BorderLayout(5,5));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		mainPanel.add(contentPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.NORTH);
		mainPanel.add(statusPanel, BorderLayout.SOUTH);
		
		setContentPane(mainPanel);
	}
	
	public void setStatusString(String status)
	{
		statusLabel.setText(status);
	}
	
	public void setStatusColor(Color color)
	{
		statusLabel.setBackground(color);
	}
}
