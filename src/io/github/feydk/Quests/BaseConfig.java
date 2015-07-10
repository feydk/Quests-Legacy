package io.github.feydk.Quests;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class BaseConfig
{
	public int Amount;
	public boolean AtNight;
	public boolean WhenRaining;
	public List<Biome> Biomes;
	
	public void parse(String json)
	{
		// Generic stuff: at night, when raining and biomes.
		JSONObject o = (JSONObject)JSONValue.parse(json);
						
		if(o.get("at_night") != null)
			AtNight = Boolean.parseBoolean(o.get("at_night").toString());
		
		if(o.get("when_raining") != null)
			WhenRaining = Boolean.parseBoolean(o.get("when_raining").toString());
		
		JSONArray biomes = (JSONArray)o.get("biomes");

		if(biomes != null)
		{
			Biomes = new ArrayList<Biome>();
			
			for(int i = 0; i < biomes.size(); i++)
			{
				Biomes.add(Biome.valueOf(((JSONObject)biomes.get(i)).get("biome").toString()));
			}
		}
	}
	
	public ArrayList<ItemStack> parseItems(String json)
	{
		JSONObject o = (JSONObject)JSONValue.parse(json);
		JSONArray items = (JSONArray)o.get("items");
		
		if(items != null)
		{
			ArrayList<ItemStack> list = new ArrayList<ItemStack>();
			
			for(int i = 0; i < items.size(); i++)
			{
				int id = Integer.parseInt(((JSONObject)items.get(i)).get("id").toString());
				
				int data = 0;
				
				if(((JSONObject)items.get(i)).get("data") != null)
					data = Integer.parseInt(((JSONObject)items.get(i)).get("data").toString());
				
				list.add(new ItemStack(id, 1, (short)data));
			}
			
			return list;
		}
		
		return null;
	}
	
	public ArrayList<EntityType> parseEntities(String json)
	{
		JSONObject o = (JSONObject)JSONValue.parse(json);
		JSONArray entities = (JSONArray)o.get("entities");

		if(entities != null)
		{
			ArrayList<EntityType> list = new ArrayList<EntityType>();
			
			for(int i = 0; i < entities.size(); i++)
			{
				list.add(EntityType.valueOf(((JSONObject)entities.get(i)).get("entity").toString()));
			}
			
			return list;
		}
		
		return null;
	}
}