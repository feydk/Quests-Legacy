package io.github.feydk.Quests.Db;

import io.github.feydk.Quests.PluginConfig;
import io.github.feydk.Quests.Quests;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerModel
{
	public int Id;
	public UUID UUID;
	public String Name;
	public int SeriesId;
	public int Tier;
	public int Streak;
	public int Cycle;
	public int CurrentQuestId;
	
	private boolean populate(ResultSet rs)
	{
		try
		{
			Id = rs.getInt("id");
			UUID = java.util.UUID.fromString(rs.getString("uuid"));
			Name = rs.getString("name");
			SeriesId = rs.getInt("series_id");
			Tier = rs.getInt("tier");
			Streak = rs.getInt("streak");
			Cycle = rs.getInt("cycle");
			CurrentQuestId = rs.getInt("current_quest_id");
			
			return true;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static PlayerModel create(UUID uuid, String name)
	{
		String query = "insert into quest_players (uuid, name, series_id, tier, streak, cycle, current_quest_id) values(?, ?, ?, 1, 0, 1, 0)";
		
		HashMap<Integer, Object> params = new HashMap<Integer, Object>();
		params.put(1, uuid.toString());
		params.put(2, name);
		params.put(3, PluginConfig.SERIES_ID);
		
		int id = Quests.db.insert(query, params);
		
		if(id > 0)
		{
			return PlayerModel.loadById(id);
		}
		
		return null;
	}
	
	public boolean update()
	{
		String query = "update quest_players set name = ?, tier = ?, streak = ?, cycle = ?, current_quest_id = ? where id = ?";
		
		HashMap<Integer, Object> params = new HashMap<Integer, Object>();
		params.put(1, Name);
		params.put(2, Tier);
		params.put(3, Streak);
		params.put(4, Cycle);
		params.put(5, CurrentQuestId);
		params.put(6, Id);
		
		return Quests.db.update(query, params);
	}
	
	public static PlayerModel loadByUUID(UUID uuid)
	{
		String query = "select * from quest_players where uuid = ? and series_id = ?";
		
		HashMap<Integer, Object> params = new HashMap<Integer, Object>();
		params.put(1, uuid.toString());
		params.put(2, PluginConfig.SERIES_ID);
		
		ResultSet rs = Quests.db.select(query, params);
		
		try
		{
			if(rs != null && rs.next())
			{
				PlayerModel obj = new PlayerModel();
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
	
	public static PlayerModel loadById(int player_id)
	{
		String query = "select * from quest_players where id = ? and series_id = ?";
		
		HashMap<Integer, Object> params = new HashMap<Integer, Object>();
		params.put(1, player_id);
		params.put(2, PluginConfig.SERIES_ID);
		
		ResultSet rs = Quests.db.select(query, params);
		
		try
		{
			if(rs != null && rs.next())
			{
				PlayerModel obj = new PlayerModel();
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
	
	public static List<PlayerModel> loadPlayersWithExpiredQuests()
	{
		String query = "select * from quest_players where id in (select player_id from quest_player_quests where series_id = ? and processed = 0 and created < date_add(now(), INTERVAL -" + PluginConfig.QUEST_LIFESPAN + " MINUTE))";
		
		HashMap<Integer, Object> params = new HashMap<Integer, Object>();
		params.put(1, PluginConfig.SERIES_ID);
		
		ResultSet rs = Quests.db.select(query, params);
		
		List<PlayerModel> list = new ArrayList<PlayerModel>();
		
		try
		{
			if(rs != null)
			{
				while(rs.next())
				{
					PlayerModel model = new PlayerModel();
					model.populate(rs);
					
					list.add(model);
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
