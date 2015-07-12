package io.github.feydk.Quests.Db;

import io.github.feydk.Quests.PluginConfig;
import io.github.feydk.Quests.QuestPlayer;
import io.github.feydk.Quests.QuestStatus;
import io.github.feydk.Quests.Quests;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuestModel
{
	public int Id;
	public int SeriesId;
	public String Name;
	public String Description;
	public int Tier;
	public int Reward;
	public String Type;
	public int Amount;
	public String Config;
	public String Commands;
	
	private boolean populate(ResultSet rs)
	{
		try
		{
			Id = rs.getInt("id");
			SeriesId = rs.getInt("series_id");
			Name = rs.getString("name");
			Description = rs.getString("description");
			Tier = rs.getInt("tier");
			Reward = rs.getInt("reward");
			Type = rs.getString("type");
			Amount = rs.getInt("amount");
			Config = rs.getString("config");
			Commands = rs.getString("commands");
			
			return true;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static QuestModel loadById(int id)
	{
		String query = "select * from quest_quests where id = ?";
		
		HashMap<Integer, Object> params = new HashMap<Integer, Object>();
		params.put(1, id);
		
		ResultSet rs = Quests.db.select(query, params);
		
		try
		{
			if(rs != null && rs.next())
			{
				QuestModel obj = new QuestModel();
				obj.populate(rs);
				
				return obj;
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static QuestModel loadRandom(QuestPlayer player)
	{
		String query = "select * from quest_quests where tier = ? and series_id = ? and id not in (select quest_id from quest_player_quests where player_id = ? and status = ? and cycle = ?) and active = 1 order by rand()";
		
		HashMap<Integer, Object> params = new HashMap<Integer, Object>();
		params.put(1, player.getModel().Tier);
		params.put(2, PluginConfig.SERIES_ID);
		params.put(3, player.getModel().Id);
		params.put(4, QuestStatus.Complete);
		params.put(5, player.getModel().Cycle);
		
		ResultSet rs = Quests.db.select(query, params);
		
		try
		{
			if(rs != null && rs.next())
			{
				QuestModel obj = new QuestModel();
				obj.populate(rs);
				
				return obj;
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static List<QuestModel> loadListAll()
	{
		List<QuestModel> list = new ArrayList<QuestModel>();
		
		String query = "select * from quest_quests order by id";
		
		ResultSet rs = Quests.db.select(query, null);
		
		try
		{
			if(rs != null)
			{
				while(rs.next())
				{
					QuestModel obj = new QuestModel();
					obj.populate(rs);
					
					list.add(obj);
				}
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
}