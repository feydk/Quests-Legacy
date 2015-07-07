package io.github.feydk.Quests;

import io.github.feydk.Quests.Db.PlayerQuestModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Quests
{
	public static MySQLDatabase db;
	private static NavigableMap<Integer, Integer> tiers;
	
	public Quests()
	{		
		db = new MySQLDatabase(PluginConfig.DB_HOST, PluginConfig.DB_PORT, PluginConfig.DB_USER, PluginConfig.DB_PASS, PluginConfig.DB_NAME);
		
		// Caching this because it doesn't make sense to look the max tier and number of tiers up in the db every time a quest is completed.
		tiers = getTiers();
	}
	
	public void shutdown()
	{
		db.Close();
	}
	
	public static int getNumberOfQuestsInTier(int tier)
	{
		return tiers.get(tier);
	}
	
	public static int getMaxNumberOfTiers()
	{
		return tiers.lastKey();
	}
	
	private TreeMap<Integer, Integer> getTiers()
	{
		String query = "select distinct A.tier, (select count(B.id) from quest_quests B where B.tier = A.tier and B.active = 1 and B.series_id = ?) as count from quest_quests A where active = 1 and series_id = ? order by A.tier";
		
		HashMap<Integer, Object> params = new HashMap<Integer, Object>();
		params.put(1, PluginConfig.SERIES_ID);
		params.put(2, PluginConfig.SERIES_ID);
		
		ResultSet rs = db.select(query, params);
		
		TreeMap<Integer, Integer> tiers = new TreeMap<Integer, Integer>();
		
		try
		{
			if(rs != null)
			{
				while(rs.next())
				{
					tiers.put(rs.getInt(1), rs.getInt(2));
				}
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return tiers;
	}
	
	public double calcStreakBonus(QuestPlayer player)
	{
		if(player.getModel().Streak == 0)
			return 0;
		
		return (double)player.getCurrentQuest().getQuestModel().Reward * ((player.getModel().Streak + 1) * PluginConfig.STREAK_MULTIPLIER);
	}
	
	public double calcCycleBonus(QuestPlayer player)
	{
		if(player.getModel().Cycle == 1)
			return 0;
		
		return (double)player.getCurrentQuest().getQuestModel().Reward * ((player.getModel().Cycle - 1) * PluginConfig.CYCLE_MULTIPLIER);
	}
	
	public List<PlayerQuest> getExpiredQuests()
	{
		 List<PlayerQuestModel> models = PlayerQuestModel.loadExpiredQuests();
		 List<PlayerQuest> list = new ArrayList<PlayerQuest>();
		 
		 if(models != null && models.size() > 0)
		 {
			 for(PlayerQuestModel m : models)
			 {
				 PlayerQuest obj = new PlayerQuest(m);
				 list.add(obj);
			 }
		 }
		 
		 return list;
	}
}