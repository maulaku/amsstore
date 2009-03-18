package ams.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class AMSFrame extends JFrame
{
	private JPanel contentPanel;
	private CardLayout cardLayout;
	
	public AMSFrame()
	{
		setTitle("Allegro Music Store");
		setPreferredSize(new Dimension(800,600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(Color.WHITE);
		initComponents();
	}
	
	private void addButtonListener(JButton button, final String id)
	{
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				cardLayout.show(contentPanel, id);
			}
		});
	}
	
	private void initComponents()
	{
		JPanel buttonPanel = new JPanel();
		JButton button = new JButton("Clerk");
		buttonPanel.add(button);
		addButtonListener(button, ClerkView.ID);
		button = new JButton("Customer");
		addButtonListener(button, CustomerView.ID);
		buttonPanel.add(button);
		button = new JButton("Manager");
		addButtonListener(button, ManagerView.ID);
		buttonPanel.add(button);
		
		buttonPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		contentPanel = new JPanel(cardLayout = new CardLayout());
		contentPanel.add(new ClerkView(), ClerkView.ID);
		contentPanel.add(new CustomerView(), CustomerView.ID);
		contentPanel.add(new ManagerView(), ManagerView.ID);
		
		contentPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(contentPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.NORTH);
		
		setContentPane(mainPanel);
	}
	
	// driver to test the GUI
	public static void main(String[] args)
	{
		AMSFrame frame = new AMSFrame();
		frame.pack();
		frame.setVisible(true);
	}
}
