package io.github.feydk.Quests;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class TradeQuest extends BaseQuest implements Listener
{
	TradeQuest(QuestsPlugin plugin)
	{
		super(plugin);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void onInventoryClick(InventoryClickEvent event)
	{
		if(!event.getWhoClicked().hasPermission("quests.quests"))
		{
			return;
		}
		
		if(!(event.getWhoClicked() instanceof Player))
			return;
		
		Player p = (Player)event.getWhoClicked();
		QuestPlayer player = plugin.players.get(p.getUniqueId());
		
		if(player.getCurrentQuest() == null)
		{
			plugin.handleNullQuest(p);
			return;
		}
		
		if(player.getCurrentQuest().getPlayerQuestModel().Status != QuestStatus.Accepted)
			return;

		if(player.getCurrentQuest().getQuest().getQuestType() == QuestType.Trade)
		{
			if(!checkGenericRequirements(player.getCurrentQuest().getQuest().getConfig(), p))
				return;
		
			if(event.getInventory().getType() == InventoryType.MERCHANT && event.getRawSlot() == 2 && event.getCurrentItem().getType() != Material.AIR)
			{
				// If player inventory is full the trade can't be completed, so don't count progress.
				if(p.getInventory().firstEmpty() == -1)
					return;
				
				plugin.updateProgress(player, p, 1);
			}
		}
	}
}