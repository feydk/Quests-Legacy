package io.github.feydk.Quests.Config;

import io.github.feydk.Quests.BaseConfig;

import java.util.List;

import org.bukkit.entity.EntityType;

public class TameConfig extends BaseConfig
{
	public List<EntityType> Entities;
	
	public void parse(String json)
	{
		super.parse(json);
		
		Entities = parseEntities(json);
	}
}