package io.github.feydk.Quests;

import io.github.feydk.Quests.Config.TameConfig;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.permissions.Permissible;

public class TameQuest extends BaseQuest implements Listener
{
	TameQuest(QuestsPlugin plugin)
	{
		super(plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void onEntityTame(EntityTameEvent event)
	{
		// Figures; the owner must be a player.
		if(!(event.getOwner() instanceof Player))
			return;
		
		if(!((Permissible)event.getOwner()).hasPermission("quests.quests"))
		{
			return;
		}
		
		Player p = (Player)event.getOwner();
		QuestPlayer player = plugin.players.get(p.getUniqueId());
		
		if(player.getCurrentQuest() == null)
		{
			plugin.handleNullQuest(p);
			return;
		}
		
		if(player.getCurrentQuest().getPlayerQuestModel().Status != QuestStatus.Accepted)
			return;

		if(player.getCurrentQuest().getQuest().getQuestType() == QuestType.Tame)
		{
			if(!checkGenericRequirements(player.getCurrentQuest().getQuest().getConfig(), p))
				return;
			
			TameConfig config = (TameConfig)player.getCurrentQuest().getQuest().getConfig();
			
			EntityType tamed = event.getEntityType();
			
			if(config.Entities != null && config.Entities.contains(tamed))
			{
				plugin.updateProgress(player, p, 1);
			}
		}
    }
}