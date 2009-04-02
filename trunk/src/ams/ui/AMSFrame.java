package ams.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import ams.Controller;

public class AMSFrame extends JFrame
{
	public static final Color SUCCESS = new Color(0,200,0,180);
	
	public static final Color FAILURE = new Color(200,0,0,180);
	
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
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e)
			{
				Controller.getInstance().stop();
			}
		});
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
		
		JToggleButton button = new JToggleButton("Customer");
		button.setIcon(new ImageIcon(getClass().getResource("/ams/ui/icons/customers.png")));
		Dimension dim = button.getPreferredSize();
		
		button = new JToggleButton("Clerk");
		button.setIcon(new ImageIcon(getClass().getResource("/ams/ui/icons/clerk.png")));
		button.setPreferredSize(dim);
		button.setSelected(true); // default view
		button.setFocusable(false);
		group.add(button);
		buttonPanel.add(button);
		addButtonListener(button, ClerkView.ID);
		
		button = new JToggleButton("Customer");
		button.setFocusable(false);
		button.setIcon(new ImageIcon(getClass().getResource("/ams/ui/icons/customers.png")));
		addButtonListener(button, CustomerView.ID);
		group.add(button);
		buttonPanel.add(button);
		
		button = new JToggleButton("Manager");
		button.setFocusable(false);
		button.setIcon(new ImageIcon(getClass().getResource("/ams/ui/icons/manager.png")));
		button.setPreferredSize(dim);
		addButtonListener(button, ManagerView.ID);
		group.add(button);
		buttonPanel.add(button);
		
		button = new JToggleButton("Table");
		button.setFocusable(false);
		button.setIcon(new ImageIcon(getClass().getResource("/ams/ui/icons/tables.png")));
		button.setPreferredSize(dim);
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
		statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
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
