package ams.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import ams.Controller;
import ams.ui.AMSFrame;

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
	
	private int findNextShipmentID()
	{
		int shipmentId = 0;
		try
		{
			String query = "SELECT MAX(sid) FROM SHIPMENT";
			PreparedStatement statement = Controller.getInstance().getConnection().prepareStatement(query);
			ResultSet result = statement.executeQuery();
			result.next();
			shipmentId = result.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return shipmentId;
	}
	
	public void processShipment(Shipment shipment)
	{
		int sId = findNextShipmentID() + 1; 
		try
		{
			Vector<Object> values = new Vector<Object>();
			values.add(sId);
			values.add(shipment.getSupplierName());
			values.add(shipment.getStoreName());
			values.add(shipment.getShipmentDate());
			Controller.getInstance().insertTuple("SHIPMENT", values);
			insertShipItems(sId, shipment.getShipItems());
			Controller.getInstance().setStatusString("Shipment successfully processed.", AMSFrame.SUCCESS);
		} catch (SQLException e)
		{			
			e.printStackTrace();
			Controller.getInstance().setStatusString("Shipment Process Failed: " + e.getMessage(), AMSFrame.FAILURE);
		}
	}
	
	public void insertShipItems(int sId, Vector<ShipItem> items) throws SQLException
	{		
		Vector<Object> values = new Vector<Object>();
		for (ShipItem item: items)
		{		
			values.clear();
			values.add(sId);
			values.add(item.getUPC());
			values.add(item.getPrice());
			values.add(item.getQuantity());
			
			Controller.getInstance().insertTuple("SHIPITEM", values);
			ItemDAO.getInstance().updatePrice(item.getUPC(), item.getPrice() * 1.20);
			ItemDAO.getInstance().updateStock(item.getUPC(), item.getQuantity());
		}
	}
}
