package io.github.feydk.Quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

public class PluginConfig
{
	// Basic db stuff.
	public static String DB_HOST;
	public static String DB_PORT;
	public static String DB_USER;
	public static String DB_PASS;
	public static String DB_NAME;
	
	public static double STREAK_MULTIPLIER;								// Multiplier for calculating streak bonuses.
	public static double CYCLE_MULTIPLIER;								// Multiplier for calculating cycle bonuses.
	
	public static int QUEST_LIFESPAN;									// Defines the lifespan of a single quest, in minutes.
	public static int SERIES_ID;										// What series of quests the plugin is currently set up to use.
	
	public static boolean BROADCAST_COMPLETIONS;						// Whether it should be broadcast when a player completes a quest.
	
	public static Map<String, List<QuestReward>> MILESTONE_REWARDS;		// List of milestone rewards.
	
	public PluginConfig(FileConfiguration config)
	{
		DB_HOST = config.getString("mysql.host", "localhost");
		DB_PORT = config.getString("mysql.port", "3306");
		DB_USER = config.getString("mysql.user", "root");
		DB_PASS = config.getString("mysql.password", "password");
		DB_NAME = config.getString("mysql.database", "quests");
		
		STREAK_MULTIPLIER = config.getDouble("streak_multiplier", .15);
		CYCLE_MULTIPLIER = config.getDouble("cycle_multiplier", .15);
		
		QUEST_LIFESPAN = config.getInt("quest_lifespan", 1440);
		SERIES_ID = config.getInt("series", 1);
		
		BROADCAST_COMPLETIONS = config.getBoolean("broadcast_completions");
		
		MILESTONE_REWARDS = new HashMap<String, List<QuestReward>>();
		
		MemorySection rewards = (MemorySection)config.get("rewards");
        
		for(String reward : rewards.getKeys(true))
		{
			@SuppressWarnings("unchecked")
			ArrayList<LinkedHashMap<String, String>> l = (ArrayList<LinkedHashMap<String, String>>)rewards.get(reward);
			
			ArrayList<QuestReward> list = new ArrayList<QuestReward>();
			
			for(int i = 0; i < l.size(); i++)
			{
				LinkedHashMap<String, String> map = (LinkedHashMap<String, String>)l.get(i);
				
				QuestReward r = new QuestReward();
				r.Text = map.get("text");
				r.Command = map.get("command");
				list.add(r);
			}
			
			MILESTONE_REWARDS.put(reward, list);
		}
	}
}