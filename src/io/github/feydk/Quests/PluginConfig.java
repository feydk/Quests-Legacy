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
	
	public static boolean SOFT_LAUNCH;									// If true, no notifications are sent on player join, when quests are expired or new quests are created.
	
	public static double CYCLE_MULTIPLIER;								// Multiplier for calculating cycle bonuses.
	public static double STREAK_BONUS_CAP;								// Cap the streak bonus percentage at this value.
	public static double STREAK_BONUS_INCREMENT;						// Increment streak bonus percentage by this value for every "streak point".
		
	public static int QUEST_LIFESPAN;									// Defines the lifespan of a single quest, in minutes.
	public static double QUEST_COOLDOWN_FACTOR;							// Defines by which factor the remaining time before a new quest is offered should be divided when completing a quest.
	public static int SERIES_ID;										// What series of quests the plugin is currently set up to use.
	
	public static boolean BROADCAST_COMPLETIONS;						// Whether it should be broadcast when a player completes a quest.
	public static boolean TITLE_ON_COMPLETION;							// Whether the player should see a title when completing a quest.
	
	public static Map<String, List<QuestReward>> MILESTONE_REWARDS;		// List of milestone rewards.
	
	public PluginConfig(FileConfiguration config)
	{
		DB_HOST = config.getString("mysql.host", "localhost");
		DB_PORT = config.getString("mysql.port", "3306");
		DB_USER = config.getString("mysql.user", "root");
		DB_PASS = config.getString("mysql.password", "password");
		DB_NAME = config.getString("mysql.database", "quests");
		
		SOFT_LAUNCH = config.getBoolean("soft_launch", false);
		
		CYCLE_MULTIPLIER = config.getDouble("cycle_multiplier", .05);
		
		STREAK_BONUS_CAP = config.getDouble("streak_bonus_cap", 100);
		STREAK_BONUS_INCREMENT = config.getDouble("streak_bonus_increment", 1);
		
		QUEST_LIFESPAN = config.getInt("quest_lifespan", 1440);
		QUEST_COOLDOWN_FACTOR = config.getDouble("quest_cooldown_factor", 2.0);
		SERIES_ID = config.getInt("series", 1);
		
		BROADCAST_COMPLETIONS = config.getBoolean("broadcast_completions", true);
		TITLE_ON_COMPLETION = config.getBoolean("title_on_completion", true);
		
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