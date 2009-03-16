package ams.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ams.Controller;

public abstract class QueryFacade {
	
	public ResultSet select(Where where, String... attributes )
	{
		String query = getQueryString(attributes) + where.getRep();
		return null;
	}
	
	public ResultSet select(String... attributes)
	{
		String query = getQueryString(attributes);
		ResultSet results;
		Statement statement = null;
		try
		{
			statement = Controller.getInstance().getConnection().createStatement();
			results = statement.executeQuery(query);
		} 
		catch (SQLException e)
		{
			results = null;
		} finally
		{
			try {
			if (statement != null)
				statement.close();
			} catch (Exception ex) { System.err.println(ex.getMessage());}
		}
		return results;
	}
	
	private String getQueryString(String... attributes)
	{
		String query = "SELECT ";
		for (int i = 0; i < attributes.length; ++i)
		{
			query += attributes[i];
			if (i != attributes.length-1)
				query += ",";
		}
		query += " FROM " + getTableName();
		return query;
	}
	
	protected abstract String getTableName();
}
