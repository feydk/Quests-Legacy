package io.github.feydk.Quests;

import io.github.feydk.Quests.Config.EnchantConfig;

import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

// Enchanting quests can be any of the following:
// 1. Enchant a specific item.
// 2. Enchant a specific item with a specific enchantment.
// 3. Enchant a specific item with a specific level.
// 4. Enchant a specific item with a specific enchantment at a specific level.
// 5. Enchant any item with a specific level.
// 6. Spend a specific amount of xp, no matter on what.

/*
 * Config examples:
 * Enchant a stone pick: { "materials": [{ "id": 274 }] }
 * Enchant a stone pick with silk touch: { "materials": [{ "id": 274 }], "enchantment": { "name": "SILK_TOUCH" } }
 * Enchant a stone pick with unbreaking 2: { "materials": [{ "id": 274 }], "enchantment": { "name": "UNBREAKING", "level": 2 } }
 * Enchant any item with any level 2: { "enchantment": { "level": 2 } }
 * Spend 20 xp: { "enchantment": { "xp": true } } (the "amount" column should have the value 20)
 */
public class EnchantQuest extends BaseQuest implements Listener
{
	EnchantQuest(QuestsPlugin plugin)
	{
		super(plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void onEnchantItem(EnchantItemEvent event)
	{
		if(!event.getEnchanter().hasPermission("quests.quests"))
		{
			return;
		}
		
		Player p = (Player)event.getEnchanter();
		QuestPlayer player = plugin.players.get(p.getUniqueId());
		
		if(player.getCurrentQuest() == null)
		{
			plugin.handleNullQuest(p);
			return;
		}
		
		if(player.getCurrentQuest().getPlayerQuestModel().Status != QuestStatus.Accepted)
			return;

		if(player.getCurrentQuest().getQuest().getQuestType() == QuestType.Enchant)
		{
			if(!checkGenericRequirements(player.getCurrentQuest().getQuest().getConfig(), p))
				return;
			
			boolean ok = false;
			
			EnchantConfig config = (EnchantConfig)player.getCurrentQuest().getQuest().getConfig();
			
			Material item = event.getItem().getType();
			
			if(config.Materials != null && config.Materials.size() > 0)
			{
				// Possible scenario 1 + 2 + 3 + 4.
				if(config.Materials.contains(item))
				{
					// Scenario 1.
					if(config.Enchantment == null && config.Level == 0)
					{
						ok = true;
					}
					else
					{
						for(Entry<Enchantment, Integer> enchant : event.getEnchantsToAdd().entrySet())
						{
							// Scenario 2.
							if(config.Enchantment != null && config.Level == 0)
							{
								if(enchant.getKey().equals(config.Enchantment))
								{
									ok = true;
									break;
								}
							}
							// Scenario 3.
							else if(config.Level > 0 && config.Enchantment == null)
							{
								if(enchant.getValue() == config.Level)
								{
									ok = true;
									break;
								}
							}
							// Scenario 4.
							else if(config.Enchantment != null && config.Level > 0)
							{
								if(enchant.getKey().equals(config.Enchantment) && enchant.getValue() == config.Level)
								{
									ok = true;
									break;
								}
							}
						}
					}
				}
			}
			else
			{
				// Scenario 5.
				if(config.Level > 0)
				{
					for(Entry<Enchantment, Integer> enchant : event.getEnchantsToAdd().entrySet())
					{
						if(enchant.getValue() == config.Level)
						{
							ok = true;
							break;
						}
					}
				}
				// Scenario 6.
				else if(config.Xp)
				{
					plugin.updateProgress(player, p, event.whichButton() + 1);
					ok = false; 	// To prevent double progress. Don't know if that would be possible at all, but just to be safe..
				}
			}
										
			if(ok)
			{
				plugin.updateProgress(player, p, 1);
			}
		}
	}
}