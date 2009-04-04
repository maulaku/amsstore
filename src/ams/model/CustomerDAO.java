package ams.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ams.Controller;

public class CustomerDAO {
	
	private static final String NAME = "Customer";
	public static String getTableName() {
		return NAME;
	}
	
	public static int insert(String[] tupleData)
	{
		int cid = getUniqueCID();
		try
		{
			Statement s = Controller.getInstance().getConnection().createStatement();
			
			String updateString = "INSERT INTO Customer VALUES ("+cid+", '"
									+tupleData[0]+"', '"+tupleData[1]+"', '"
									+tupleData[2]+"', '"+tupleData[3]+"')";
			
			s.executeUpdate(updateString);
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
		return cid;
	}
	
	/*
	 * Queries for all the existing cids, returns 1+ the largest existing cid
	 */
	private static int getUniqueCID()
	{
		ArrayList<Integer> cids = new ArrayList<Integer>();
		try
		{
			Statement s = Controller.getInstance().getConnection().createStatement();
			String updateString = "SELECT cid FROM Customer";
			ResultSet rs = s.executeQuery(updateString);
			while(rs.next())
			{
				cids.add(rs.getInt("cid"));
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
		
		int max = 0;
		for(int i : cids)
		{
			if(i > max)
				max = i;
		}
		return max+1;
	}
}
