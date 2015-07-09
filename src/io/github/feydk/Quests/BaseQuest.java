package io.github.feydk.Quests;

import java.util.List;

import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

/*
 * Config examples of the common, generic requirements. These can be used in all quests:
 * { <quest specific params>, "when_raining": true, "at_night": true, "biomes": [{ "biome": "BEACH" }, { "biome": "DESERT" }] }
 * 
 * Example: catch a fish in a desert at night
 * { "items": [{ "id": 349 }], "at_night": true, "biomes": [{ "biome": "DESERT" }] }
 */

public class BaseQuest
{
	protected QuestsPlugin plugin;
	
	BaseQuest(QuestsPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	boolean checkGenericRequirements(BaseConfig config, Player entity)
	{
		return checkForBiome(config.Biomes, entity) && checkForNight(config.AtNight, entity) && checkForRain(config.WhenRaining, entity);
	}
	
	boolean checkForBiome(List<Biome> biomes, Player entity)
	{
		if(biomes != null && biomes.size() > 0)
		{
			return biomes.contains(entity.getWorld().getBiome(entity.getLocation().getBlockX(), entity.getLocation().getBlockZ()));
		}
		
		return true;
	}
	
	boolean checkForNight(boolean at_night, Player entity)
	{
		if(at_night)
		{
			long time = entity.getWorld().getTime();
			return time >= 12500;
		}
		
		return true;
	}
	
	boolean checkForRain(boolean when_raining, Player entity)
	{
		if(when_raining)
		{
			return entity.getWorld().hasStorm();
		}
		
		return true;
	}
}