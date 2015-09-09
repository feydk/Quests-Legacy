package io.github.feydk.Quests;

import io.github.feydk.Quests.Config.MineConfig;

import java.util.Collection;

import com.winthier.exploits.bukkit.BukkitExploits;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

/*
 * Config examples:
 * Break a block of dirt: { "items": [{ "id": "3:0" }] }
 * Break any type of wood log: { "materials": [{ "id": 17 }, { "id": 162 }] }
 * Break 5 blocks of coal ore: { "materials": [{ "id": 16 }] }
 * 
 * Use items for blocks with data ids.
 */

public class MineQuest extends BaseQuest implements Listener
{
	MineQuest(QuestsPlugin plugin)
	{
		super(plugin);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void onBlockBreak(BlockBreakEvent event)
	{
		if(!event.getPlayer().hasPermission("quests.quests"))
		{
			return;
		}
		
		Player p = event.getPlayer();		
		QuestPlayer player = plugin.getQuestPlayer(p);
		
		if(player.getCurrentQuest().getPlayerQuestModel().Status != QuestStatus.Accepted)
			return;

		if(player.getCurrentQuest().getQuest().getQuestType() == QuestType.Mine)
		{
			if(!checkGenericRequirements(player.getCurrentQuest().getQuest().getConfig(), p))
				return;

			// Ignore if the block was placed by a player.
			if (BukkitExploits.getInstance().isPlayerPlaced(event.getBlock())) return;
			
			MineConfig config = (MineConfig)player.getCurrentQuest().getQuest().getConfig();
			
			Collection<ItemStack> dropped_items = event.getBlock().getDrops();
			ItemStack mined_item = new ItemStack(event.getBlock().getType());
			
			// Temp fix for redstone ore.
			if(event.getBlock().getType() == Material.GLOWING_REDSTONE_ORE)
				mined_item = new ItemStack(Material.REDSTONE_ORE);
			
			//Material.GLOWING_REDSTONE_ORE
			///System.out.println(dropped_items);
			//System.out.println(mined_item);
			//System.out.println(config.Items);
						
			if(config.Items != null && ((dropped_items.size() > 0 && config.Items.containsAll(dropped_items)) || config.Items.contains(mined_item)))
				plugin.updateProgress(player, p, 1);
		}
	}
}
