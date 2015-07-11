package io.github.feydk.Quests;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;
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
	
	public static int getNumberOfQuestsInCycle()
	{
		int count = 0;
		
		for(Entry<Integer, Integer> set : tiers.entrySet())
			count += set.getValue();
		
		return count;
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
}