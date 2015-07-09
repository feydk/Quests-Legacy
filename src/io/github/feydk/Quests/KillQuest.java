package io.github.feydk.Quests;

import io.github.feydk.Quests.Config.KillConfig;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

// Killing quests can be any of the following:
// 1. Kill a certain type of mob.
// 2. Kill a certain type of mob with a certain weapon (which doesn't have to be a weapon per se, can by any held item).

/*
 * Config examples:
 * Kill a skeleton: { "entities": [{ "entity": "SKELETON" }] }
 * Kill a zombie with a diamond: { "entities": [{ "entity": "ZOMBIE" }], "materials": [{ "id": 264 }] }
 * Kill a zombie, creeper or skeleton: { "entities": [{ "entity": "ZOMBIE" }, { "entity": "CREEPER" }, { "entity": "SKELETON" }] }
 */
public class KillQuest extends BaseQuest implements Listener
{
	KillQuest(QuestsPlugin plugin)
	{
		super(plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void onDeath(EntityDeathEvent event)
	{				
		if(event.getEntity() instanceof Player)
			return;
		
		if(!(event.getEntity().getKiller() instanceof Player))
			return;
		
		if(!event.getEntity().getKiller().hasPermission("quests.quests"))
		{
			return;
		}
		
		Player p = event.getEntity().getKiller();
		QuestPlayer player = plugin.players.get(p.getUniqueId());
		
		if(player.getCurrentQuest() == null)
		{
			plugin.handleNullQuest(p);
			return;
		}
		
		if(player.getCurrentQuest().getPlayerQuestModel().Status != QuestStatus.Accepted)
			return;

		if(player.getCurrentQuest().getQuest().getQuestType() == QuestType.Kill)
		{
			if(!checkGenericRequirements(player.getCurrentQuest().getQuest().getConfig(), p))
				return;
			
			KillConfig config = (KillConfig)player.getCurrentQuest().getQuest().getConfig();
			
			EntityType killed = event.getEntity().getType();
			
			if(config.Entities != null && config.Entities.contains(killed))
			{
				boolean ok = true;
				
				// Scenario 2.
				if(config.Materials != null && config.Materials.size() > 0)
				{
					Material weapon = p.getItemInHand().getType();
					
					ok = config.Materials.contains(weapon);
				}
				
				if(ok)
					plugin.updateProgress(player, p, 1);
			}
		}
	}
}