package io.github.feydk.Quests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class MySQLDatabase
{
	private Connection connection;
	private boolean log_statements = false;
	
	public MySQLDatabase(String host, String port, String user, String password, String dbname)
	{				
		try
		{
			Class.forName("com.mysql.jdbc.Driver");

			this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + dbname, user, password);
		}
		catch(ClassNotFoundException | SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void Close()
	{
		try
		{
			if(connection != null && !connection.isClosed())
				connection.close();
		}
		catch(SQLException e)
		{}
	}
	
	public ResultSet select(String query, HashMap<Integer, Object> params)
	{
		PreparedStatement ps;
		
		try
		{
			ps = connection.prepareStatement(query);
			
			if(params != null)
			{
				for(Map.Entry<Integer, Object> param : params.entrySet())
				{
					if(param.getValue().getClass().equals(String.class))
						ps.setString(param.getKey(), param.getValue().toString());
					else if(param.getValue().getClass().equals(Integer.class))
						ps.setInt(param.getKey(), Integer.parseInt(param.getValue().toString()));
					else if(param.getValue().getClass().equals(Double.class))
						ps.setDouble(param.getKey(), Double.parseDouble(param.getValue().toString()));
				}
			}
			
			if(log_statements)
				System.out.println("db.select() called: " + query);

			return ps.executeQuery();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public int getInt(String query, HashMap<Integer, Object> params)
	{
		ResultSet rs = select(query, params);
		
		try
		{
			if(rs != null && rs.next())
			{
				return rs.getInt(1);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public double getDouble(String query, HashMap<Integer, Object> params)
	{
		ResultSet rs = select(query, params);
		
		try
		{
			if(rs != null && rs.next())
			{
				return rs.getDouble(1);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public int insert(String query, HashMap<Integer, Object> params)
	{
		PreparedStatement ps;
		
		try
		{
			ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			
			if(params != null)
			{
				for(Map.Entry<Integer, Object> param : params.entrySet())
				{
					if(param.getValue().getClass().equals(String.class))
						ps.setString(param.getKey(), param.getValue().toString());
					else if(param.getValue().getClass().equals(Integer.class))
						ps.setInt(param.getKey(), Integer.parseInt(param.getValue().toString()));
					else if(param.getValue().getClass().equals(Double.class))
						ps.setDouble(param.getKey(), Double.parseDouble(param.getValue().toString()));
				}
			}
			
			if(log_statements)
				System.out.println("db.insert() called");
			
			ps.executeUpdate();
			
			ResultSet rs = ps.getGeneratedKeys();
			
		    if(rs.next())
		    {
		    	int last_inserted_id = rs.getInt(1);
		    	
		    	return last_inserted_id;
		    }
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public boolean update(String query, HashMap<Integer, Object> params)
	{
		PreparedStatement ps;
		
		try
		{
			ps = connection.prepareStatement(query);
			
			if(params != null)
			{
				for(Map.Entry<Integer, Object> param : params.entrySet())
				{
					if(param.getValue().getClass().equals(String.class))
						ps.setString(param.getKey(), param.getValue().toString());
					else if(param.getValue().getClass().equals(Integer.class))
						ps.setInt(param.getKey(), Integer.parseInt(param.getValue().toString()));
					else if(param.getValue().getClass().equals(Double.class))
						ps.setDouble(param.getKey(), Double.parseDouble(param.getValue().toString()));
				}
			}
			
			if(log_statements)
				System.out.println("db.update() called");
			
			ps.executeUpdate();
			
			return true;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	/*
	
	
	public void updateProgress(PlayerQuest quest)
	{
		String query = "update quest_player_quests set data = ? where id = ?";
		
		PreparedStatement ps;
		
		try
		{
			ps = connection.prepareStatement(query);
			ps.setInt(1, quest.Progress);
			ps.setInt(2, quest.Id);
			
			ps.executeUpdate();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public Quest getQuest(int quest_id)
	{
		String query = "select * from quest_quests where id = ? and series_id = ?";
		
		PreparedStatement ps;
		
		try
		{
			ps = connection.prepareStatement(query);
			ps.setInt(1, quest_id);
			ps.setInt(2, plugin.series_id);
			
			ResultSet rs = ps.executeQuery();
			
			if(rs != null && rs.next())
				return populateQuest(rs);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
			
	public PlayerQuest getPlayerQuest(QuestPlayer player)
	{
		String query = "select B.name, B.description, B.tier, B.reward, B.config, A.quest_id, A.id, A.player_id, A.status, A.data from quest_player_quests A join quest_quests B on A.quest_id = B.id join quest_players C on A.player_id = C.id where A.player_id = ? and date(A.created) = date(now()) and C.series_id = ? and A.cycle = ?";
		
		PreparedStatement ps;
		
		try
		{
			ps = connection.prepareStatement(query);
			ps.setInt(1, player.Id);
			ps.setInt(2, plugin.series_id);
			ps.setInt(3, player.Cycle);
			
			ResultSet rs = ps.executeQuery();

			if(rs != null && rs.next())
				return populatePlayerQuest(rs);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	*/
}