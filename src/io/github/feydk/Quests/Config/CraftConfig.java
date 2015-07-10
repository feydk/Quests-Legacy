package io.github.feydk.Quests.Config;

import io.github.feydk.Quests.BaseConfig;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public class CraftConfig extends BaseConfig
{
	public List<ItemStack> Items;
	
	public void parse(String json)
	{
		super.parse(json);
		
		Items = parseItems(json);
	}
}