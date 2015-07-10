package io.github.feydk.Quests.Config;

import io.github.feydk.Quests.BaseConfig;

import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class KillConfig extends BaseConfig
{
	public List<EntityType> Entities;
	public List<ItemStack> Items;
	
	public void parse(String json)
	{
		super.parse(json);
		
		Items = parseItems(json);
		Entities = parseEntities(json);
	}
}