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
import org.bukkit.projectiles.ProjectileSource;

// Take damage quests can be any of the following:
// 1. Take a certain amount of damage (hearts), no matter how.
// 2. Take a certain amount of damage (hearts) from a certain type of mob.

/*
 * Config examples:
 * Take damage, no matter how: { } (empty config, since the "amount" column in the "quest_quests" table will contain the amount of hearts)
 * Take damage from a zombie: { "entities": [{ "entity": "ZOMBIE" }] } (again, "amount" determines the amount of hearts)
 */
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
		QuestPlayer player = plugin.getQuestPlayer(p);
		
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
						ProjectileSource shooter = ((Arrow)damager).getShooter();
						if (shooter instanceof Entity) damager = (Entity)shooter;
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
