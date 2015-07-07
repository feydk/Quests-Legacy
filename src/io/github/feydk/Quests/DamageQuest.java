package io.github.feydk.Quests;

import io.github.feydk.Quests.Config.DamageConfig;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.permissions.Permissible;

// Take damage quests can be any of the following:
// 1. Take a certain amount of damage (hearts), no matter how.
// 2. Take a certain amount of damage (hearts) from a certain type of mob.
public class DamageQuest extends BaseQuest implements Listener
{
	DamageQuest(QuestsPlugin plugin)
	{
		super(plugin);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void onEntityDamage(EntityDamageEvent event)
	{
		// The damagee has to be a player.
		if(!(event.getEntity() instanceof Player))
			return;
		
		if(!((Permissible)event.getEntity()).hasPermission("quests.quests"))
		{
			return;
		}
		
		Player p = (Player)event.getEntity();
		QuestPlayer player = plugin.players.get(p.getUniqueId());
		
		if(player.getCurrentQuest() == null)
		{
			plugin.handleNullQuest(p);
			return;
		}
		
		if(player.getCurrentQuest().getPlayerQuestModel().Status != QuestStatus.Accepted)
			return;

		if(player.getCurrentQuest().getQuest().getQuestType() == QuestType.TakeDamage)
		{
			if(!checkGenericRequirements(player.getCurrentQuest().getQuest().getConfig(), p))
				return;
			
			boolean ok = false;
			
			DamageConfig config = (DamageConfig)player.getCurrentQuest().getQuest().getConfig();
			
			// Scenario 2.
			if(config.Entities != null && config.Entities.size() > 0)
			{
				if(event instanceof EntityDamageByEntityEvent)
				{
					Entity damager = ((EntityDamageByEntityEvent)event).getDamager();
					
					if(damager instanceof Arrow)
					{
						damager = ((Arrow)damager).getShooter();
					}
					
					if(config.Entities.contains(damager.getType()))
					{
						ok = true;
					}
				}
			}
			// Scenario 1.
			else
			{
				ok = true;
			}
			
			if(ok)
			{
				plugin.updateProgress(player, p, event.getFinalDamage() / 2);
			}
		}
	}
}