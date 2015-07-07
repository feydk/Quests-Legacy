package io.github.feydk.Quests;

import io.github.feydk.Quests.Config.MineConfig;

import java.util.Collection;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

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
		QuestPlayer player = plugin.players.get(p.getUniqueId());
		
		if(player.getCurrentQuest() == null)
		{
			plugin.handleNullQuest(p);
			return;
		}
		
		if(player.getCurrentQuest().getPlayerQuestModel().Status != QuestStatus.Accepted)
			return;

		if(player.getCurrentQuest().getQuest().getQuestType() == QuestType.Mine)
		{
			if(!checkGenericRequirements(player.getCurrentQuest().getQuest().getConfig(), p))
				return;
			
			// Ignore if the tool used to mine the block has silk touch on it.
			for(Entry<Enchantment, Integer> ench : p.getItemInHand().getEnchantments().entrySet())
			{	
				if(ench.getKey().getName().equals("SILK_TOUCH"))
					return;
			}
			
			MineConfig config = (MineConfig)player.getCurrentQuest().getQuest().getConfig();
			
			// Checking both blocks and drops / materials and items.
			Collection<ItemStack> mined_item = event.getBlock().getDrops();
			Material mined_material = event.getBlock().getType();

			if((config.Materials != null && config.Materials.contains(mined_material)) || (config.Items != null && config.Items.contains(mined_item)))
				plugin.updateProgress(player, p, 1);
		}
	}
}