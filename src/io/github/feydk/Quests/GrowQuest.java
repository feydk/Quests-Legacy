package io.github.feydk.Quests;

import io.github.feydk.Quests.Config.GrowConfig;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

// Growing quests can be any of the following:
// 1. Grow an amount of trees.
// 2. Grow an amount of a certain tree type.
// AVOID big jungle trees and big spruce trees, as the event is fired even if a tree isn't grown. Dark oaks seem to work fine.

/*
 * Config examples:
 * Grow a birch tree: { "treetypes": [{ "type": "BIRCH" }] }
 * Grow any tree: {  } (note the above comment hoever, so a player could complete this just by trying to grow a 2x2 tree)
 * Grow a birch tree or a small jungle tree: { "treetypes": [{ "type": "BIRCH" }, { "type": "SMALL_JUNGLE" }] }
 */
public class GrowQuest extends BaseQuest implements Listener
{
	GrowQuest(QuestsPlugin plugin)
	{
		super(plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void onStructureGrow(final StructureGrowEvent event)
	{
		if(event.getPlayer() == null)
			return;
		
		if(!(event.getPlayer() instanceof Player))
			return;
		
		if(!event.getPlayer().hasPermission("quests.quests"))
		{
			return;
		}
		
		if(!(event.getPlayer() instanceof Player))
			return;
		
		final Player p = event.getPlayer();
		final QuestPlayer player = plugin.players.get(p.getUniqueId());
		
		if(player.getCurrentQuest() == null)
		{
			plugin.handleNullQuest(p);
			return;
		}
		
		if(player.getCurrentQuest().getPlayerQuestModel().Status != QuestStatus.Accepted)
			return;

		if(player.getCurrentQuest().getQuest().getQuestType() == QuestType.Grow)
		{
			if(!checkGenericRequirements(player.getCurrentQuest().getQuest().getConfig(), p))
				return;
			
			final GrowConfig config = (GrowConfig)player.getCurrentQuest().getQuest().getConfig();
			
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
			{
				public void run()
				{
					if(event.getWorld().getBlockAt(event.getLocation()).getType() != Material.SAPLING)
					{
						if(config.TreeTypes != null && config.TreeTypes.size() > 0)
						{
							TreeType grown = event.getSpecies();
							
							if(config.TreeTypes.contains(grown))
							{
								plugin.updateProgress(player, p, 1);
							}
						}
						else
						{
							plugin.updateProgress(player, p, 1);
						}
					}
				}
			}, 1);
		}
	}
}