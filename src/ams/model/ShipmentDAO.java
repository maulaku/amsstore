package ams.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import ams.Controller;

public class ShipmentDAO
{

	private static ShipmentDAO instance;
	
	private ShipmentDAO()
	{
		
	}
	
	public static ShipmentDAO getInstance()
	{
		if (instance == null)
			instance = new ShipmentDAO();
		return instance;
	}
	
	public long findMaxShipmentID()
	{
		long shipmentId = 0;
		try
		{
			String query = "SELECT MAX(sid) FROM SHIPMENT";
			PreparedStatement statement = Controller.getInstance().getConnection().prepareStatement(query);
			ResultSet result = statement.executeQuery();
			result.next();
			shipmentId = result.getLong(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return shipmentId;
	}
	
	public void insertShipment(long sId, Shipment shipment) throws SQLException
	{
		Vector<Object> values = new Vector<Object>();
		values.add(sId);
		values.add(shipment.getSupplierName());
		values.add(shipment.getStoreName());
		values.add(shipment.getShipmentDate());
		Controller.getInstance().insertTuple("SHIPMENT", values);
	}
	
	public void insertShipItem(long sId, ShipItem item) throws SQLException
	{		
		Vector<Object> values = new Vector<Object>();
		values.add(sId);
		values.add(item.getUPC());
		values.add(item.getPrice());
		values.add(item.getQuantity());
		
		Controller.getInstance().insertTuple("SHIPITEM", values);		
	}
}
