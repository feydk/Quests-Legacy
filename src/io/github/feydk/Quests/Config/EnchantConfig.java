package io.github.feydk.Quests.Config;

import io.github.feydk.Quests.BaseConfig;

import java.util.List;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class EnchantConfig extends BaseConfig
{
	public List<ItemStack> Items;
	public int Level;
	public Enchantment Enchantment;
	public boolean Xp;
	
	public void parse(String json)
	{
		super.parse(json);
		
		Items = parseItems(json);
		
		JSONObject o = (JSONObject)JSONValue.parse(json);
		JSONObject eo = (JSONObject)o.get("enchantment");
		
		if(eo != null)
		{
			if(eo.get("level") != null)
				Level = Integer.parseInt(eo.get("level").toString());
			
			if(eo.get("name") != null)
				Enchantment = org.bukkit.enchantments.Enchantment.getByName(eo.get("name").toString());
			
			if(eo.get("xp") != null)
				Xp = Boolean.parseBoolean(eo.get("xp").toString());
		}
	}
}