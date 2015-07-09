package io.github.feydk.Quests;

import io.github.feydk.Quests.Config.CraftConfig;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

// Special notice about crafting quests:
// The API has no way of telling us how many items where actually crafted, only what was crafted.
// So quests must always only require ONE item to be crafted. Which of course makes them sort of boring, but.. ¯\_(ツ)_/¯

/*
 * Config examples:
 * Craft a bed: { "materials": [{ "id": 355 }] }
 * Craft a jukebox: { "materials": [{ "id": 84 }] }
 */
public class CraftQuest extends BaseQuest implements Listener
{
	CraftQuest(QuestsPlugin plugin)
	{
		super(plugin);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void onCraftItem(CraftItemEvent event)
	{
		if(!event.getWhoClicked().hasPermission("quests.quests"))
		{
			return;
		}
		
		Player p = (Player)event.getWhoClicked();
		QuestPlayer player = plugin.players.get(p.getUniqueId());
				
		if(player.getCurrentQuest() == null)
		{
			plugin.handleNullQuest(p);
			return;
		}
		
		if(player.getCurrentQuest().getPlayerQuestModel().Status != QuestStatus.Accepted)
			return;

		if(player.getCurrentQuest().getQuest().getQuestType() == QuestType.Craft)
		{
			if(!checkGenericRequirements(player.getCurrentQuest().getQuest().getConfig(), p))
				return;
			
			CraftConfig config = (CraftConfig)player.getCurrentQuest().getQuest().getConfig();
			
			Material crafted = event.getRecipe().getResult().getType();
			
			if(config.Materials != null && config.Materials.contains(crafted))
			{
				plugin.updateProgress(player, p, event.getRecipe().getResult().getAmount());
			}
		}
	}
}