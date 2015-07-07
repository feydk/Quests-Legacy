package io.github.feydk.Quests.Db;

import io.github.feydk.Quests.PluginConfig;
import io.github.feydk.Quests.Quests;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerQuestModel
{
	public int Id;
	public int PlayerId;
	public int QuestId;
	public int SeriesId;
	public int Status;
	public double Progress;
	public long Created;
	public int Cycle;
	public int Reward;
	public double StreakBonus;
	public double CycleBonus;
	public int Processed;
	
	private boolean populate(ResultSet rs)
	{
		try
		{
			Id = rs.getInt("id");
			PlayerId = rs.getInt("player_id");
			QuestId = rs.getInt("quest_id");
			SeriesId = rs.getInt("series_id");
			Status = rs.getInt("status");
			Progress = rs.getDouble("progress");
			Cycle = rs.getInt("cycle");
			Reward = rs.getInt("reward");
			StreakBonus = rs.getDouble("streak_bonus");
			CycleBonus = rs.getDouble("cycle_bonus");
			Created = rs.getTimestamp("created").getTime();
			Processed = rs.getInt("processed");
			
			return true;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static PlayerQuestModel create(PlayerModel player, QuestModel quest)
	{
		String query = "insert into quest_player_quests (player_id, quest_id, series_id, status, progress, created, cycle, reward, streak_bonus, cycle_bonus, processed) ";
		query += "values (?, ?, ?, 0, 0, now(), ?, 0, 0, 0, 0)";
		
		HashMap<Integer, Object> params = new HashMap<Integer, Object>();
		params.put(1, player.Id);
		params.put(2, quest.Id);
		params.put(3, PluginConfig.SERIES_ID);
		params.put(4, player.Cycle);
		
		int id = Quests.db.insert(query, params);
		
		if(id > 0)
		{
			return PlayerQuestModel.loadById(id);
		}
		
		return null;
	}
	
	public boolean update()
	{
		String query = "update quest_player_quests set status = ?, progress = ?, cycle = ?, reward = ?, streak_bonus = ?, cycle_bonus = ?, processed = ? where id = ?";
		
		HashMap<Integer, Object> params = new HashMap<Integer, Object>();
		params.put(1, Status);
		params.put(2, Progress);
		params.put(3, Cycle);
		params.put(4, Reward);
		params.put(5, StreakBonus);
		params.put(6, CycleBonus);
		params.put(7, Processed);
		params.put(8, Id);
		
		return Quests.db.update(query, params);
	}
	
	public static PlayerQuestModel loadById(int id)
	{
		String query = "select * from quest_player_quests where id = ?";
		
		HashMap<Integer, Object> params = new HashMap<Integer, Object>();
		params.put(1, id);
		
		ResultSet rs = Quests.db.select(query, params);
		
		try
		{
			if(rs != null && rs.next())
			{
				PlayerQuestModel obj = new PlayerQuestModel();
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
	
	public static List<PlayerQuestModel> loadExpiredQuests()
	{
		String query = "select * from quest_player_quests where series_id = ? and processed = 0 and created < date_add(now(), INTERVAL -" + PluginConfig.QUEST_LIFESPAN + " MINUTE)";
		
		HashMap<Integer, Object> params = new HashMap<Integer, Object>();
		params.put(1, PluginConfig.SERIES_ID);
		
		ResultSet rs = Quests.db.select(query, params);
		
		List<PlayerQuestModel> list = new ArrayList<PlayerQuestModel>();
		
		try
		{
			if(rs != null)
			{
				while(rs.next())
				{
					PlayerQuestModel model = new PlayerQuestModel();
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