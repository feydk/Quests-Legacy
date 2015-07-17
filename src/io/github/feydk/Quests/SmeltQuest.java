package io.github.feydk.Quests;

import io.github.feydk.Quests.Config.SmeltConfig;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

/*
 * Config examples:
 * Smelt sand into glass: { "materials": [{ "id": 20 }] } (we only specify the resulting material, which is 20 = glass)
 */

public class SmeltQuest extends BaseQuest implements Listener
{
	SmeltQuest(QuestsPlugin plugin)
	{
		super(plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void onFurnaceExtract(FurnaceExtractEvent event)
	{
		if(!event.getPlayer().hasPermission("quests.quests"))
		{
			return;
		}
		
		Player p = (Player)event.getPlayer();
		QuestPlayer player = plugin.getQuestPlayer(p);
		
		if(player.getCurrentQuest().getPlayerQuestModel().Status != QuestStatus.Accepted)
			return;

		if(player.getCurrentQuest().getQuest().getQuestType() == QuestType.Smelt)
		{
			if(!checkGenericRequirements(player.getCurrentQuest().getQuest().getConfig(), p))
				return;
			
			SmeltConfig config = (SmeltConfig)player.getCurrentQuest().getQuest().getConfig();
								
			ItemStack smelted_item = new ItemStack(event.getItemType());
						
			if(config.Items != null && config.Items.contains(smelted_item))
			{
				int amount = event.getItemAmount();
				
				if(config.ClickType == 3)
				{
					amount = config.Amount;
					config.Amount = 0;
				}
				
				if(amount == 0)
					return;
								
				plugin.updateProgress(player, p, amount);
			}
		}
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
		QuestPlayer player = plugin.getQuestPlayer(p);
		
		if(player.getCurrentQuest().getPlayerQuestModel().Status != QuestStatus.Accepted)
			return;

		if(player.getCurrentQuest().getQuest().getQuestType() == QuestType.Smelt)
		{
			if(!checkGenericRequirements(player.getCurrentQuest().getQuest().getConfig(), p))
				return;
			
			if(event.getSlotType() != InventoryType.SlotType.RESULT)
				return;

			ItemStack item = event.getCurrentItem();

			if(item != null && item.getType() != Material.AIR)
			{
				SmeltConfig config = (SmeltConfig)player.getCurrentQuest().getQuest().getConfig();
				
				if(event.isRightClick())
					config.ClickType = 1;
				else if(event.isLeftClick())
					config.ClickType = 2;
				else if(event.getHotbarButton() != -1)
					config.ClickType = 4;
				
				if(event.isShiftClick())
					config.ClickType = 3;
				
				config.Amount = item.getAmount();
			}
		}
	}
}
