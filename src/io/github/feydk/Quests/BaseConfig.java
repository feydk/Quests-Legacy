package io.github.feydk.Quests;

import io.github.feydk.Quests.Config.*;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
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
		
		// Craft.
		if(this instanceof CraftConfig)
		{
			JSONArray mats = (JSONArray)o.get("materials");
			
			if(mats != null)
			{
				((CraftConfig)this).Materials = new ArrayList<Material>();
				
				for(int i = 0; i < mats.size(); i++)
				{
					String id = ((JSONObject)mats.get(i)).get("id").toString();
					
					((CraftConfig)this).Materials.add(Material.getMaterial(Integer.parseInt(id)));
				}
			}
		}
		
		// Take damage.
		if(this instanceof DamageConfig)
		{
			JSONArray entities = (JSONArray)o.get("entities");

			if(entities != null)
			{
				((DamageConfig)this).Entities = new ArrayList<EntityType>();
				
				for(int i = 0; i < entities.size(); i++)
				{
					((DamageConfig)this).Entities.add(EntityType.valueOf(((JSONObject)entities.get(i)).get("entity").toString()));
				}
			}
		}
		
		// Eat.
		if(this instanceof EatConfig)
		{
			JSONArray mats = (JSONArray)o.get("materials");
			
			if(mats != null)
			{
				((EatConfig)this).Materials = new ArrayList<Material>();
				
				for(int i = 0; i < mats.size(); i++)
				{
					String id = ((JSONObject)mats.get(i)).get("id").toString();
					
					((EatConfig)this).Materials.add(Material.getMaterial(Integer.parseInt(id)));
				}
			}
		}
		
		// Enchant.
		if(this instanceof EnchantConfig)
		{
			JSONArray mats = (JSONArray)o.get("materials");
			
			if(mats != null)
			{
				((EnchantConfig)this).Materials = new ArrayList<Material>();
				
				for(int i = 0; i < mats.size(); i++)
				{
					String id = ((JSONObject)mats.get(i)).get("id").toString();
					
					((EnchantConfig)this).Materials.add(Material.getMaterial(Integer.parseInt(id)));
				}
			}
			
			JSONObject eo = (JSONObject)o.get("enchantment");
			
			if(eo != null)
			{
				if(eo.get("level") != null)
					((EnchantConfig)this).Level = Integer.parseInt(eo.get("level").toString());
				
				if(eo.get("name") != null)
					((EnchantConfig)this).Enchantment = Enchantment.getByName(eo.get("name").toString());
				
				if(eo.get("xp") != null)
					((EnchantConfig)this).Xp = Boolean.parseBoolean(eo.get("xp").toString());
			}
		}
		
		// Fish.
		if(this instanceof FishConfig)
		{
			JSONArray items = (JSONArray)o.get("items");
			
			if(items != null)
			{
				((FishConfig)this).Items = new ArrayList<ItemStack>();
				
				for(int i = 0; i < items.size(); i++)
				{
					String id = ((JSONObject)items.get(i)).get("id").toString();
					
					if(!id.contains(":"))
						id += ":0";
					
					String[] parts = id.split(":");
					int main_id = Integer.parseInt(parts[0]);
					int variant_id = Integer.parseInt(parts[1]);
						
					ItemStack stack = new ItemStack(main_id, 1, (short)variant_id);
					((FishConfig)this).Items.add(stack);
				}
			}
		}
		
		// Grow.
		if(this instanceof GrowConfig)
		{
			JSONArray treetypes = (JSONArray)o.get("treetypes");

			if(treetypes != null)
			{
				((GrowConfig)this).TreeTypes = new ArrayList<TreeType>();
				
				for(int i = 0; i < treetypes.size(); i++)
				{
					((GrowConfig)this).TreeTypes.add(TreeType.valueOf(((JSONObject)treetypes.get(i)).get("type").toString()));
				}
			}
		}
		
		// Kill.
		if(this instanceof KillConfig)
		{
			JSONArray entities = (JSONArray)o.get("entities");

			if(entities != null)
			{
				((KillConfig)this).Entities = new ArrayList<EntityType>();
				
				for(int i = 0; i < entities.size(); i++)
				{
					((KillConfig)this).Entities.add(EntityType.valueOf(((JSONObject)entities.get(i)).get("entity").toString()));
				}
			}
			
			JSONArray mats = (JSONArray)o.get("materials");
			
			if(mats != null)
			{
				((KillConfig)this).Materials = new ArrayList<Material>();
				
				for(int i = 0; i < mats.size(); i++)
				{
					String id = ((JSONObject)mats.get(i)).get("id").toString();
					
					((KillConfig)this).Materials.add(Material.getMaterial(Integer.parseInt(id)));
				}
			}
		}
		
		// Mine.
		if(this instanceof MineConfig)
		{
			JSONArray mats = (JSONArray)o.get("materials");
			
			if(mats != null)
			{
				((MineConfig)this).Materials = new ArrayList<Material>();
				
				for(int i = 0; i < mats.size(); i++)
				{
					String id = ((JSONObject)mats.get(i)).get("id").toString();
					
					((MineConfig)this).Materials.add(Material.getMaterial(Integer.parseInt(id)));
				}
			}
			
			JSONArray items = (JSONArray)o.get("items");
			
			if(items != null)
			{
				((MineConfig)this).Items = new ArrayList<ItemStack>();
				
				for(int i = 0; i < items.size(); i++)
				{
					String id = ((JSONObject)items.get(i)).get("id").toString();
					
					if(!id.contains(":"))
						id += ":0";
					
					String[] parts = id.split(":");
					int main_id = Integer.parseInt(parts[0]);
					int variant_id = Integer.parseInt(parts[1]);
						
					ItemStack stack = new ItemStack(main_id, 1, (short)variant_id);
					((MineConfig)this).Items.add(stack);
				}
			}
		}
		
		// Smelt.
		if(this instanceof SmeltConfig)
		{
			JSONArray mats = (JSONArray)o.get("materials");
			
			if(mats != null)
			{
				((SmeltConfig)this).Materials = new ArrayList<Material>();
				
				for(int i = 0; i < mats.size(); i++)
				{
					String id = ((JSONObject)mats.get(i)).get("id").toString();
					
					((SmeltConfig)this).Materials.add(Material.getMaterial(Integer.parseInt(id)));
				}
			}
		}
		
		// Tame.
		if(this instanceof TameConfig)
		{
			JSONArray entities = (JSONArray)o.get("entities");

			if(entities != null)
			{
				((TameConfig)this).Entities = new ArrayList<EntityType>();
				
				for(int i = 0; i < entities.size(); i++)
				{
					((TameConfig)this).Entities.add(EntityType.valueOf(((JSONObject)entities.get(i)).get("entity").toString()));
				}
			}
		}
	}
}