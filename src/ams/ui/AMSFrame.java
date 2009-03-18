package ams.ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class AMSFrame extends JFrame
{
	public AMSFrame()
	{
		setTitle("Allegro Music Store");
		setPreferredSize(new Dimension(800,600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(Color.WHITE);
		initComponents();
	}
	
	private void initComponents()
	{
		JPanel mainPanel = new JPanel();
		
		
		
		setContentPane(mainPanel);
	}
}
