package io.github.feydk.Quests.Config;

import io.github.feydk.Quests.BaseConfig;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public class SmeltConfig extends BaseConfig
{
	public List<ItemStack> Items;
	public int Amount;
	public int ClickType;
	
	public void parse(String json)
	{
		super.parse(json);
		
		Items = parseItems(json);
	}
}