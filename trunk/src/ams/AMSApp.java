package ams;

import java.sql.DriverManager;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class AMSApp
{
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater( new Runnable() {

			public void run() {
				
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					// oh well
				}
				
				try {
					DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				    
					String username = "ora_v6c6";
					String password = "a83455063";
								
					String dbUrl = "jdbc:oracle:thin:@localhost:9998:ug";
					
					Controller.getInstance().initializeConnection(dbUrl, username, password);
				} 
				catch (Exception e)
				{
					// cannot let application start
					System.err.println("Cannot initialize connection: " + e.getMessage());
					System.exit(1);
				}
				Controller.getInstance().start();	
			}
			
		});
	}
}
