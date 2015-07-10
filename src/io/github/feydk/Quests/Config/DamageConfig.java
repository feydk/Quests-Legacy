package io.github.feydk.Quests.Config;

import java.util.List;

import org.bukkit.entity.EntityType;

import io.github.feydk.Quests.BaseConfig;

public class DamageConfig extends BaseConfig
{
	public List<EntityType> Entities;
	
	public void parse(String json)
	{
		super.parse(json);
		
		Entities = parseEntities(json);
	}
}