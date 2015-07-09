package io.github.feydk.Quests;

import io.github.feydk.Quests.Config.FishConfig;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

/*
 * Config examples:
 * Catch a fish: { "items": [{ "id": 349 }] }
 * Catch a salmon: { "items": [{ "id": "349:1" }] }
 * Catch a salmon OR a clownfish: { "items": [{ "id": "349:1" }, { "id": "349:2" }] }
 */
public class FishQuest extends BaseQuest implements Listener
{
	FishQuest(QuestsPlugin plugin)
	{
		super(plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void onPlayerFish(PlayerFishEvent event)
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

		if(player.getCurrentQuest().getQuest().getQuestType() == QuestType.Fish)
		{
			if(!checkGenericRequirements(player.getCurrentQuest().getQuest().getConfig(), p))
				return;
			
			if(event.getCaught() == null)
				return;
			
			FishConfig config = (FishConfig)player.getCurrentQuest().getQuest().getConfig();
			
			ItemStack fish_item = ((Item)event.getCaught()).getItemStack();
			
			if(config.Items.contains(fish_item))
			{
				plugin.updateProgress(player, p, 1);
			}
		}
	}
}