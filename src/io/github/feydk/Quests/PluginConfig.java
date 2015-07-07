package io.github.feydk.Quests;

import org.bukkit.configuration.file.FileConfiguration;

public class PluginConfig
{
	public static String DB_HOST;
	public static String DB_PORT;
	public static String DB_USER;
	public static String DB_PASS;
	public static String DB_NAME;
	
	public static double STREAK_MULTIPLIER;
	public static double CYCLE_MULTIPLIER;
	
	public static int QUEST_LIFESPAN;
	public static int SERIES_ID;
	
	public static boolean BROADCAST_COMPLETIONS;
	
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
	}
}