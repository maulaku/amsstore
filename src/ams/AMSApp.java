package ams;

import java.sql.DriverManager;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class AMSApp
{
	private static final String DB_URL = "jdbc:oracle:thin:@localhost:9998:ug";
	
	private static final String USERNAME = "ora_v6c6";
	
	private static final String PASSWORD = "a83455063";
	
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
					
					Controller.getInstance().initializeConnection(DB_URL, USERNAME, PASSWORD);
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
