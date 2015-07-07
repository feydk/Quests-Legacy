package io.github.feydk.Quests;

import io.github.feydk.Quests.Config.EatConfig;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class EatQuest extends BaseQuest implements Listener
{
	EatQuest(QuestsPlugin plugin)
	{
		super(plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void onPlayerItemConsume(PlayerItemConsumeEvent event)
	{
		if(!event.getPlayer().hasPermission("quests.quests"))
		{
			return;
		}
		
		Player p = event.getPlayer();
		QuestPlayer player = plugin.players.get(p.getUniqueId());
		
		if(player.getCurrentQuest() == null)
		{
			plugin.handleNullQuest(p);
			return;
		}
		
		if(player.getCurrentQuest().getPlayerQuestModel().Status != QuestStatus.Accepted)
			return;

		if(player.getCurrentQuest().getQuest().getQuestType() == QuestType.Eat)
		{
			if(!checkGenericRequirements(player.getCurrentQuest().getQuest().getConfig(), p))
				return;
			
			EatConfig config = (EatConfig)player.getCurrentQuest().getQuest().getConfig();
			
			Material eaten = event.getItem().getType();
			
			if(config.Materials != null && config.Materials.contains(eaten))
			{
				plugin.updateProgress(player, p, 1);
			}
		}
	}
		
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void onBlockClick(PlayerInteractEvent event)
	{
		if(!event.getPlayer().hasPermission("quests.quests"))
		{
			return;
		}
		
		// Only relevant for cake eating quests.
		if(event.getClickedBlock().getType() == Material.CAKE_BLOCK && event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			Player p = event.getPlayer();
			QuestPlayer player = plugin.players.get(p.getUniqueId());
			
			if(player.getCurrentQuest() == null)
			{
				plugin.handleNullQuest(p);
				return;
			}
			
			if(player.getCurrentQuest().getPlayerQuestModel().Status != QuestStatus.Accepted)
				return;

			if(player.getCurrentQuest().getQuest().getQuestType() == QuestType.Eat)
			{
				if(!checkGenericRequirements(player.getCurrentQuest().getQuest().getConfig(), p))
					return;
				
				EatConfig config = (EatConfig)player.getCurrentQuest().getQuest().getConfig();
				
				if(config.Materials != null && config.Materials.contains(Material.CAKE_BLOCK))
					plugin.updateProgress(player, p, 1);
			}
		}
	}
}