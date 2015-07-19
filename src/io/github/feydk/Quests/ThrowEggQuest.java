package io.github.feydk.Quests;

import io.github.feydk.Quests.Config.ThrowEggConfig;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEggThrowEvent;

// Can be any of the following:
// 1. Throw a certain amount of eggs.
// 2. Hatch a certain amount of chickens from thrown eggs.

/*
 * Config examples:
 * Throw egg: { "throw": true }
 * Hatch chicken: { "hatch": true }
 */
public class ThrowEggQuest extends BaseQuest implements Listener
{
	ThrowEggQuest(QuestsPlugin plugin)
	{
		super(plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void onPlayerEggThrow(PlayerEggThrowEvent event)
	{
		if(!event.getPlayer().hasPermission("quests.quests"))
		{
			return;
		}
		
		Player p = (Player)event.getPlayer();
		QuestPlayer player = plugin.getQuestPlayer(p);
		
		if(player.getCurrentQuest().getPlayerQuestModel().Status != QuestStatus.Accepted)
			return;

		if(player.getCurrentQuest().getQuest().getQuestType() == QuestType.ThrowEgg)
		{
			if(!checkGenericRequirements(player.getCurrentQuest().getQuest().getConfig(), p))
				return;
			
			ThrowEggConfig config = (ThrowEggConfig)player.getCurrentQuest().getQuest().getConfig();

			if(config.Throw)
			{
				plugin.updateProgress(player, p, 1);
			}
			else if(config.Hatch)
			{
				if(event.getHatchingType() == EntityType.CHICKEN && event.getNumHatches() > 0)
					plugin.updateProgress(player, p, event.getNumHatches());
			}
		}
	}
}
