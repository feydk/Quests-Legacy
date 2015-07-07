package io.github.feydk.Quests;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

// http://www.spigotmc.org/threads/symbols-in-motd.10092/

public class QuestsPlugin extends JavaPlugin implements Listener
{
	private Economy economy;
	public static PluginConfig Config;
	
	protected Quests quests;
	Map<UUID, QuestPlayer> players;	
	private QuestScheduler scheduler;
	
	private CraftQuest crafting_quest;
	private EatQuest eating_quest;
	private TameQuest taming_quest;
	private MineQuest mining_quest;
	private EnchantQuest enchanting_quest;
	private FishQuest fishing_quest;
	private DamageQuest damaging_quest;
	private SmeltQuest smelting_quest;
	private KillQuest killing_quest;
	private TradeQuest trading_quest;
	private GrowQuest growing_quest;
	
	@Override
	public void onEnable()
	{
		reloadConfig();
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		Config = new PluginConfig(getConfig());
		
		quests = new Quests();
		
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
		
        if(economyProvider != null)
        	economy = economyProvider.getProvider();
        else
        	throw new RuntimeException("Failed to setup economy.");
                        
        crafting_quest = new CraftQuest(this);
        eating_quest = new EatQuest(this);
        taming_quest = new TameQuest(this);
        mining_quest = new MineQuest(this);
        enchanting_quest = new EnchantQuest(this);
        fishing_quest = new FishQuest(this);
        damaging_quest = new DamageQuest(this);
        smelting_quest = new SmeltQuest(this);
        killing_quest = new KillQuest(this);
        trading_quest = new TradeQuest(this);
        growing_quest = new GrowQuest(this);
        
        // Listen for quest specific events.
        getServer().getPluginManager().registerEvents(crafting_quest, this);
        getServer().getPluginManager().registerEvents(eating_quest, this);
        getServer().getPluginManager().registerEvents(taming_quest, this);
        getServer().getPluginManager().registerEvents(mining_quest, this);
        getServer().getPluginManager().registerEvents(enchanting_quest, this);
        getServer().getPluginManager().registerEvents(fishing_quest, this);
        getServer().getPluginManager().registerEvents(damaging_quest, this);
        getServer().getPluginManager().registerEvents(smelting_quest, this);
        getServer().getPluginManager().registerEvents(killing_quest, this);
        getServer().getPluginManager().registerEvents(trading_quest, this);
        getServer().getPluginManager().registerEvents(growing_quest, this);
        
        // General events.
        getServer().getPluginManager().registerEvents(this, this);
        
        players = new HashMap<UUID, QuestPlayer>();
        
        for(Player p : getServer().getOnlinePlayers())
        {
        	players.put(p.getUniqueId(), QuestPlayer.getByUUID(p.getUniqueId()));
        }
        
        scheduler = new QuestScheduler(this);
        scheduler.start();
	}
	
	@Override
	public void onDisable()
	{
		scheduler.stop();
		quests.shutdown();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String args[])
	{
		Player player = null;

		if(sender instanceof Player)
			player = (Player)sender;

		if(player == null)
		{
			sender.sendMessage("Player expected");
			return true;
		}
		
		if(command.getName().equals("questadmin") && args.length == 0)
		{
			if(!player.hasPermission("quests.admin"))
				return true;
			
			showAdminCommands(player);
			return true;
		}
		
		if(command.getName().equals("quest"))
		{
			if(args.length == 0 || args[0].equals("info"))
				showQuestDetails(player);
			else if(args[0].equals("accept"))
				acceptQuest(player);
			else if(args[0].equals("cancel"))
				cancelQuest(player);
			else if(args[0].equals("me"))
				showPlayerStats(player);
		}
		else if(command.getName().equals("questadmin"))
		{
			if(!player.hasPermission("quests.admin"))
				return true;
			
			if(args[0].equals("new"))
				newQuest(player);
			else if(args[0].equals("complete"))
				completeQuest(player);
			else if(args[0].equals("reset"))
				resetPlayer(player);
			//else if(args[0].equals("replace") && args.length == 2)
			//	replaceQuest(player, Integer.parseInt(args[1]));
		}
		
		return true;
	}
	
	private void showAdminCommands(Player entity)
	{		
		String msg = " " + ChatColor.YELLOW + "Quests helper commands" + "\n";
		msg += " " + ChatColor.AQUA + "/questadmin" + ChatColor.GRAY + " - " + ChatColor.DARK_AQUA + "Show this menu" + "\n";
		msg += " " + ChatColor.AQUA + "/questadmin new" + ChatColor.GRAY + " - " + ChatColor.DARK_AQUA + "Force creation of a new quest if player has no active quest" + "\n";
		msg += " " + ChatColor.AQUA + "/questadmin complete" + ChatColor.GRAY + " - " + ChatColor.DARK_AQUA + "Force completion of current active quest" + "\n";
		msg += " " + ChatColor.AQUA + "/questadmin reset" + ChatColor.GRAY + " - " + ChatColor.DARK_AQUA + "Wipe all stats and progress and start from scratch at tier 1" + "\n";
		
		if(entity.isOp())
			msg += " " + ChatColor.AQUA + "/questadmin replace <id>" + ChatColor.GRAY + " - " + ChatColor.DARK_AQUA + "Replace the current quest with a specific quest" + "\n";
		
		entity.sendMessage(msg);
	}
	
	private void showPlayerStats(Player entity)
	{
		QuestPlayer player = players.get(entity.getUniqueId());
		
		String indent = " ";
		
		String msg = ChatColor.AQUA + "=== Quest Statistics for " + entity.getName() + " ===\n";
		msg += indent + ChatColor.DARK_AQUA + "Tier: " + ChatColor.AQUA + player.getModel().Tier + "\n";
		msg += indent + ChatColor.DARK_AQUA + "Streak: " + ChatColor.AQUA + player.getModel().Streak + "\n";
		
		if(player.getModel().Cycle > 1)
			msg += indent + ChatColor.DARK_AQUA + "Cycle: " + ChatColor.AQUA + player.getModel().Cycle + "\n";
		
		msg += indent + ChatColor.AQUA + "§m   §r " + ChatColor.AQUA + "Quests §m   §r\n";
		
		msg += indent + ChatColor.DARK_AQUA + "Completed: " + ChatColor.AQUA + player.getTotalQuests(QuestStatus.Complete) + "\n";
		msg += indent + ChatColor.DARK_AQUA + "Cancelled: " + ChatColor.AQUA + player.getTotalQuests(QuestStatus.Cancelled) + "\n";
		msg += indent + ChatColor.DARK_AQUA + "Not completed: " + ChatColor.AQUA + player.getTotalQuests(QuestStatus.Incomplete) + "\n";
		msg += indent + ChatColor.DARK_AQUA + "Total quests offered: " + ChatColor.AQUA + player.getTotalQuests(-1) + "\n";
		
		msg += indent + ChatColor.AQUA + "§m   §r " + ChatColor.AQUA + "Rewards §m   §r\n";
		
		double base = player.getTotalRewards("reward");
		double streak = player.getTotalRewards("streak_bonus");
		double cycle = player.getTotalRewards("cycle_bonus");
		
		msg += indent + ChatColor.DARK_AQUA + "Base rewards: " + ChatColor.AQUA + economy.format(base) + "\n";
		msg += indent + ChatColor.DARK_AQUA + "Streak bonuses: " + ChatColor.AQUA + economy.format(streak) + "\n";
		
		if(player.getModel().Cycle > 1)
			msg += indent + ChatColor.DARK_AQUA + "Cycle bonuses: " + ChatColor.AQUA + economy.format(cycle) + "\n";
		
		msg += indent + ChatColor.DARK_AQUA + "Total rewards: " + ChatColor.AQUA + economy.format(base + streak + cycle) + "\n";
		
		entity.sendMessage(msg);
	}
	
	private void showQuestDetails(Player entity)
	{
		QuestPlayer player = players.get(entity.getUniqueId());
		
		if(player.getCurrentQuest() == null)
		{
			handleNullQuest(entity);
			return;
		}
		
		// Auto accept quest.
		if(player.getCurrentQuest().getPlayerQuestModel().Status == QuestStatus.Created)
		{
			player.getCurrentQuest().accept();
		}
		
		double streak_bonus = 0;
		double cycle_bonus = 0;
		
		if(player.getCurrentQuest().getPlayerQuestModel().Status == QuestStatus.Complete)
		{
			streak_bonus = player.getCurrentQuest().getPlayerQuestModel().StreakBonus;
			cycle_bonus = player.getCurrentQuest().getPlayerQuestModel().CycleBonus;
		}
		else
		{
			streak_bonus = quests.calcStreakBonus(player);
			cycle_bonus = quests.calcCycleBonus(player);
		}
		
		double total_reward = player.getCurrentQuest().getQuestModel().Reward + streak_bonus + cycle_bonus;
		
		String json = "[";
		
		// === Today's Quest ===
		json += "{color: \"aqua\", text: \"=== \"}, {color: \"yellow\", text: \"✦\"}, {color: \"aqua\", text: \" Today's Quest ===\n\"}, ";
		
		// Checkmark
		if(player.getCurrentQuest().getPlayerQuestModel().Status == QuestStatus.Complete)
		{
			json += "{color: \"green\", text: \" ✔\", hoverEvent: {action: \"show_text\", value: \"You have completed\nthis quest.\"}}, ";
		}
		// Unticked box
		else if(player.getCurrentQuest().getPlayerQuestModel().Status == QuestStatus.Cancelled)
		{
			json += "{color: \"red\", text: \" ✖\", hoverEvent: {action: \"show_text\", value: \"You have cancelled\nthis quest.\"}}, ";
		}
		
		// Quest name
		json += "{color: \"green\", text: \" " + player.getCurrentQuest().getQuestModel().Name + "\n\"}, ";
		
		// --- Description ---
		json += "{color: \"aqua\", text: \" §m   §r \"}, {color: \"aqua\", text: \"Description \"}, {color: \"aqua\", text: \"§m   §r\n\"}, ";
		
		// Quest description
		json += "{color: \"gray\", text: \" " + player.getCurrentQuest().getQuestModel().Description + "\n\"}, ";
		
		// --- Stats ---
		json += "{color: \"aqua\", text: \" §m   §r \"}, {color: \"aqua\", text: \"Stats \"}, {color: \"aqua\", text: \"§m   §r\n\"}, ";
		
		// Tier
		json += "{color: \"dark_aqua\", text: \" Tier: \"}, {color: \"aqua\", text: \"" + player.getCurrentQuest().getQuestModel().Tier + "\n\"}, ";
		
		// Reward
		json += "{color: \"dark_aqua\", text: \" Reward: \"}, {color: \"aqua\", text: \"" + economy.format(total_reward) + "\n\"";
		
		if(streak_bonus > 0 || cycle_bonus > 0)
		{
			json += ", hoverEvent: {action: \"show_text\", value: \"Base reward: " + economy.format(player.getCurrentQuest().getQuestModel().Reward);
			
			if(streak_bonus > 0)
				json += "\nStreak bonus: " + economy.format(streak_bonus);
			
			if(cycle_bonus > 0)
				json += "\nCycle bonus: " + economy.format(cycle_bonus);
			
			json += "\"}";
		}
		
		json += "}, ";
		
		// Progress and time left
		if(player.getCurrentQuest().getPlayerQuestModel().Status == QuestStatus.Accepted)
		{
			Calendar c = Calendar.getInstance();
			long now = c.getTimeInMillis();
			c.setTimeInMillis(player.getCurrentQuest().getPlayerQuestModel().Created);
			c.add(Calendar.MINUTE, PluginConfig.QUEST_LIFESPAN);

			long ms = c.getTimeInMillis() - now;
			int minutes = (int)((ms / (1000*60)) % 60);
			int hours  = (int)((ms / (1000*60*60)) % 24);
			
			if(minutes < 0)
				minutes = 0;
						
			if((player.getCurrentQuest().getPlayerQuestModel().Progress == Math.floor(player.getCurrentQuest().getPlayerQuestModel().Progress)) && !Double.isInfinite(player.getCurrentQuest().getPlayerQuestModel().Progress))
			{
				json += "{color: \"dark_aqua\", text: \" Progress: \"}, {color: \"aqua\", text: \"" + (int)player.getCurrentQuest().getPlayerQuestModel().Progress + "/" + player.getCurrentQuest().getQuestModel().Amount + "\n\"}, ";
			}
			else
			{
				json += "{color: \"dark_aqua\", text: \" Progress: \"}, {color: \"aqua\", text: \"" + player.getCurrentQuest().getPlayerQuestModel().Progress + "/" + player.getCurrentQuest().getQuestModel().Amount + "\n\"}, ";
			}

			json += "{color: \"dark_aqua\", text: \" Time left: \"}, {color: \"aqua\", text: \"" + hours + " hours " + minutes + " mins\n\"}, ";
		}
		// Next quest
		else if(player.getCurrentQuest().getPlayerQuestModel().Status == QuestStatus.Complete || player.getCurrentQuest().getPlayerQuestModel().Status == QuestStatus.Cancelled)
		{
			Calendar c = Calendar.getInstance();
			long now = c.getTimeInMillis();
			c.setTimeInMillis(player.getCurrentQuest().getPlayerQuestModel().Created);
			c.add(Calendar.MINUTE, PluginConfig.QUEST_LIFESPAN);

			long ms = c.getTimeInMillis() - now;
			int minutes = (int)((ms / (1000*60)) % 60);
			int hours  = (int)((ms / (1000*60*60)) % 24);
			
			if(minutes < 0)
				minutes = 0;
			
			json += "{color: \"aqua\", text: \" §m   §r \"}, {color: \"aqua\", text: \"Next Quest\"}, {color: \"aqua\", text: \"§m   §r\n\"}, ";
			
			if(hours > 0 || minutes > 0)
				json += "{color: \"gray\", text: \" You will be offered a new quest in " + hours + " hours " + minutes + " mins" + "\n\"}, ";
			else
				json += "{color: \"gray\", text: \" You will be offered a new quest shortly.." + "\n\"}, ";
		}
		
		// --- Actions ---
		json += "{color: \"aqua\", text: \" §m   §r \"}, {color: \"aqua\", text: \"Actions \"}, {color: \"aqua\", text: \"§m   §r\n\"}, ";
		
		if(player.getCurrentQuest().getPlayerQuestModel().Status == QuestStatus.Cancelled)
			json += "{color: \"green\", text: \" [Accept]\", clickEvent: {action: \"run_command\", value: \"/quest accept\" }, hoverEvent: {action: \"show_text\", value: \"" + ChatColor.GREEN + "Accept this quest.\"}}, ";
		else if(player.getCurrentQuest().getPlayerQuestModel().Status == QuestStatus.Accepted)
			json += "{color: \"red\", text: \" [Cancel]\", clickEvent: {action: \"run_command\", value: \"/quest cancel\" }, hoverEvent: {action: \"show_text\", value: \"" + ChatColor.RED + "Cancel this quest.\nDoing so will disable\nreminders about this quest.\nIt will not break your streak.\"}}, ";
		
		json += "{text: \" \"},{color: \"gold\", text: \"[Stats]\", clickEvent: {action: \"run_command\", value: \"/quest me\" }, hoverEvent: {action: \"show_text\", value: \"View your personal stats.\"}}";
		
		json += "]";
		
		sendJsonMessage(entity, json);
	}
	
	private void acceptQuest(Player entity)
	{
		QuestPlayer player = players.get(entity.getUniqueId());
				
		if(player.getCurrentQuest() == null)
		{
			handleNullQuest(entity);
			return;
		}
		
		String msg = "";
		
		if(player.getCurrentQuest().accept())
			msg = ChatColor.AQUA + " Quest accepted. Good luck!";
		else
			msg = ChatColor.RED + " Could not accept quest.";
		
		entity.sendMessage(msg);
	}
	
	private void cancelQuest(Player entity)
	{
		QuestPlayer player = players.get(entity.getUniqueId());
		
		if(player.getCurrentQuest() == null)
		{
			handleNullQuest(entity);
			return;
		}
		
		String msg = "";
		
		if(player.getCurrentQuest().cancel())
		{
			msg = "[";
			msg += "{color: \"dark_aqua\", text: \" Quest cancelled. You won't be notified about this quest again, but if you change your mind you can just type \"},";
			msg += "{color: \"yellow\", text: \"/quest\", clickEvent: {action: \"run_command\", value: \"/quest\" }, hoverEvent: {action: \"show_text\", value: \"View the quest details\"}},";
			msg += "{color: \"dark_aqua\", text: \" and then accept the quest.\"}";
			msg += "]";
			
			sendJsonMessage(entity, msg);			
		}
		else
		{
			msg = ChatColor.RED + " Could not cancel quest.";
			entity.sendMessage(msg);
		}
	}
	
	private void completeQuest(Player entity)
	{
		QuestPlayer player = players.get(entity.getUniqueId());
		
		if(player.getCurrentQuest() == null)
		{
			handleNullQuest(entity);
			return;
		}
		
		String msg = "";
		
		player.getCurrentQuest().getPlayerQuestModel().StreakBonus = quests.calcStreakBonus(player);
		player.getCurrentQuest().getPlayerQuestModel().CycleBonus = quests.calcCycleBonus(player);
		player.getCurrentQuest().getPlayerQuestModel().Reward = player.getCurrentQuest().getQuestModel().Reward;
		
		if(player.getCurrentQuest().complete())
		{
			player.incrementStreak();
			
			String[] words = { "Nice", "Cool", "Sweet", "Awesome" };
			Random ran = new Random();
			
			msg = ChatColor.AQUA + " Quest completed! " + words[ran.nextInt(words.length)] + " ツ\n";
			
			msg += " " + ChatColor.GREEN + "✦ " + ChatColor.DARK_AQUA + "Reward: " + ChatColor.AQUA + economy.format(player.getCurrentQuest().getPlayerQuestModel().Reward) + "\n";
			
			if(player.getCurrentQuest().getPlayerQuestModel().StreakBonus > 0 || player.getCurrentQuest().getPlayerQuestModel().CycleBonus > 0)
			{
				if(player.getCurrentQuest().getPlayerQuestModel().StreakBonus > 0)
					msg += " " + ChatColor.GREEN + "✦ " + ChatColor.DARK_AQUA + "Streak bonus: " + ChatColor.AQUA + economy.format(player.getCurrentQuest().getPlayerQuestModel().StreakBonus) + "\n";
				
				if(player.getCurrentQuest().getPlayerQuestModel().CycleBonus > 0)
					msg += " " + ChatColor.GREEN + "✦ " + ChatColor.DARK_AQUA + "Cycle bonus: " + ChatColor.AQUA + economy.format(player.getCurrentQuest().getPlayerQuestModel().CycleBonus) + "\n";
			}
			
			// Handle streak rewards (set up in the config file).
			msg += getStreakResult(player.getModel().Streak, entity);
			
			// Issue command(s) for this quest.
			if(player.getCurrentQuest().getQuest().getRewards() != null)
			{
				for(QuestReward r : player.getCurrentQuest().getQuest().getRewards())
				{
					getServer().dispatchCommand(getServer().getConsoleSender(), r.Command.replaceAll("%player%", entity.getName()));
					msg += " " + ChatColor.GREEN + "✦ " + ChatColor.DARK_AQUA + "Special reward: " + ChatColor.AQUA + r.Text + "\n";
				}
			}
									
			boolean tier_increased = player.maybeIncrementTier();
						
			if(tier_increased)
			{
				if(player.maybeIncrementCycle())
				{
					msg += " " + ChatColor.GOLD + "Congratulations! You have completed all quests!\n";
					
					// Handle this special event! (set up in the config file)
					if(PluginConfig.MILESTONE_REWARDS.containsKey("cycle_complete"))
					{
						List<QuestReward> rewards = PluginConfig.MILESTONE_REWARDS.get("cycle_complete");
						
						if(!rewards.isEmpty())
						{
							for(QuestReward reward : rewards)
							{
								getServer().dispatchCommand(getServer().getConsoleSender(), reward.Command.replaceAll("%player%", entity.getName()));
								msg += " " + ChatColor.GOLD + "✦ " + reward.Text.replaceAll("%happy%", "ツ") + "\n";
							}
						}
					}
					
					msg += " " + ChatColor.AQUA + "Your quest cycle will now restart at Tier 1, but you will get an additional cycle bonus for every completed quest.";
				}
				else
				{
					msg += " " + ChatColor.YELLOW + "✦ " + ChatColor.DARK_AQUA + "You have moved up to Tier " + ChatColor.AQUA + player.getModel().Tier + "\n";
				}
				
				summonRocket(entity);
			}
			
			players.replace(entity.getUniqueId(), QuestPlayer.getByUUID(entity.getUniqueId()));
			player = players.get(entity.getUniqueId());
			
			giveMoney(player, player.getCurrentQuest().getPlayerQuestModel().Reward + player.getCurrentQuest().getPlayerQuestModel().StreakBonus + player.getCurrentQuest().getPlayerQuestModel().CycleBonus);
			
			entity.playSound(entity.getLocation(), Sound.ORB_PICKUP, 1, (float).5);
			
			// Broadcast to all players that this player completed a quest.
			if(PluginConfig.BROADCAST_COMPLETIONS)
			{
				String json = "[{color: \"white\", text: \"" + entity.getName() + " has completed the quest \"},";
				json += "{color: \"green\", text: \"[" + player.getCurrentQuest().getQuestModel().Name + "]\", hoverEvent: {action: \"show_text\", value: \"" + player.getCurrentQuest().getQuestModel().Description;
				
				if(player.getModel().Streak > 1)
					json += "\nNow on a streak of " + player.getModel().Streak;
				
				json += "\"}}]";
				
				broadcastJsonMessage(json);
			}
		}
		else
		{
			entity.sendMessage(ChatColor.RED + " Sorry, something went wrong. Please make a ticket and refer to this error code: 1");
			return;
		}
		
		//player.reloadQuest();
		
		entity.sendMessage(msg);
	}
	
	private void newQuest(Player entity)
	{		
		QuestPlayer player = players.get(entity.getUniqueId());
		
		if(player.getCurrentQuest().getPlayerQuestModel().Status != QuestStatus.Complete)
		{
			entity.sendMessage(ChatColor.RED + "You already have an active quest.");
			return;
		}
		
		player.getCurrentQuest().setProcessed();
		player.giveRandomQuest();
		
		notifyPlayerOfQuest(entity, player.getCurrentQuest().getPlayerQuestModel().Status, 0);
	}
	
	private void resetPlayer(Player entity)
	{		
		QuestPlayer player = players.get(entity.getUniqueId());
		
		player.reset();
		players.replace(entity.getUniqueId(), QuestPlayer.getByUUID(entity.getUniqueId()));
		
		entity.sendMessage("Stats wiped!");
	}
	
	/*private void replaceQuest(Player entity, int id)
	{		
		QuestPlayer player = players.get(entity.getUniqueId());
		PlayerQuest q = player.getQuest(false, false).PlayerQuest;
		player.assignQuestId(q.Id, id);
		
		players.replace(entity.getUniqueId(), QuestPlayer.getByUUID(entity.getUniqueId()));
		entity.sendMessage("Quest replaced!");
	}*/
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void onPlayerJoin(PlayerJoinEvent event)
	{
		// TODO: take care of requirements like player rank, ontime or whatever?
		
		if(!event.getPlayer().hasPermission("quests.quests"))
		{
			return;
		}
		
		final Player entity = event.getPlayer();
		
		QuestPlayer player = QuestPlayer.getByUUID(entity.getUniqueId());
		
		// Player doesn't exist in the db yet, so create him now.
		if(player.getModel() == null)
		{
			player = QuestPlayer.create(entity.getUniqueId(), entity.getName());
			
			if(player.getModel() == null)
			{
				getLogger().warning("Could not create player model for '" + entity.getName() + "'.");
				return;
			}
			
			// Give player a random quest.
			player.giveRandomQuest();
		}
		
		players.put(entity.getUniqueId(), player);
		
		// Let the player know if his last (current) quest wasn't completed before it expired.
		if(player.getCurrentQuest().getPlayerQuestModel().Status == QuestStatus.Incomplete)
		{
			getLogger().info(entity.getName() + " didn't complete their last quest in time.");
			
			getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
			{
				public void run()
				{
					String msg = " " + ChatColor.AQUA + "Aaawww! You didn't manage to complete your last quest in time.";
					msg += "\n A new quest will be created for you shortly..";
			
					entity.sendMessage(msg);
				}
			}, 60);
			
			// And give him a new quest.
			player.giveRandomQuest();
		}
		
		notifyPlayerOfQuest(entity, player.getCurrentQuest().getPlayerQuestModel().Status, 160);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void onPlayerQuit(PlayerQuitEvent event)
	{
		if(!event.getPlayer().hasPermission("quests.quests"))
		{
			return;
		}
		
		players.remove(event.getPlayer().getUniqueId());
	}
	
	private String getStreakResult(int streak, Player entity)
	{
		String msg = "";

		if(PluginConfig.MILESTONE_REWARDS.containsKey("streak_of_" + streak))
		{
			List<QuestReward> rewards = PluginConfig.MILESTONE_REWARDS.get("streak_of_" + streak);
			
			if(!rewards.isEmpty())
			{
				for(QuestReward reward : rewards)
				{
					getServer().dispatchCommand(getServer().getConsoleSender(), reward.Command.replaceAll("%player%", entity.getName()));
					msg += " " + ChatColor.GREEN + "✦ " + ChatColor.DARK_AQUA + reward.Text.replaceAll("%happy%", "ツ") + "\n";
				}
			}
		}
		
		return msg;
	}
	
	void updateProgress(QuestPlayer player, Player entity, double amount)
	{
		player.getCurrentQuest().getPlayerQuestModel().Progress += amount;
		
		entity.playSound(entity.getLocation(), Sound.ORB_PICKUP, 1, 1);
		
		if(player.getCurrentQuest().updateProgress())
		{
			if(player.getCurrentQuest().getPlayerQuestModel().Progress >= player.getCurrentQuest().getQuest().getModel().Amount)
				completeQuest(entity);
		}
		else
		{
			entity.sendMessage(ChatColor.RED + " Sorry, something went wrong. Please make a ticket and refer to this error code: 3");
			return;
		}
	}
	
	void notifyPlayerOfQuest(final Player player, final int status, int delay)
	{
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
		{
			public void run()
			{
				String msg = "[{color: \"green\", text: \" There is a new quest for you.\n\"},{color: \"dark_aqua\", text: \" [Click here] to get started.\", clickEvent: {action: \"run_command\", value: \"/quest\" }, hoverEvent: {action: \"show_text\", value: \"Clicking will show the\nquest details.\"}}]";
				
				if(status == QuestStatus.Accepted)
					msg = "[{color: \"green\", text: \" Reminder: You have a quest that's not completed yet.\n\"},{color: \"dark_aqua\", text: \" [Click here] for details.\", clickEvent: {action: \"run_command\", value: \"/quest\" }, hoverEvent: {action: \"show_text\", value: \"Clicking will show the\nquest details.\"}}]";
				
				if(status != QuestStatus.Cancelled)
					sendJsonMessage(player, msg);
			}
		}, delay);
	}
	
	// This is called from quest events if a PlayerQuest can't be found.
	// If that happens, it's almost without a doubt because the database was manipulated by hand or because the automatic tier and/or cycle rank-up didn't work.
	void handleNullQuest(Player player)
	{
		player.sendMessage(ChatColor.RED + " Sorry, something went wrong. Please make a ticket and refer to this error code: 2");
		getLogger().warning("PlayerQuest for " + player.getName() + " (UUID: " + player.getUniqueId() + ") was null.");
	}
		
	private boolean sendJsonMessage(Player player, String json)
	{
        if(player == null)
        	return false;
        
        final CommandSender console = getServer().getConsoleSender();
        final String command = "minecraft:tellraw " + player.getName() + " " + json;

        getServer().dispatchCommand(console, command);
        
        return true;
    }
	
	@SuppressWarnings("deprecation")
	private boolean broadcastJsonMessage(String json)
	{
		final CommandSender console = getServer().getConsoleSender();
        
		for(Player player : getServer().getOnlinePlayers())
		{
			final String command = "minecraft:tellraw " + player.getName() + " " + json;

			getServer().dispatchCommand(console, command);
		}
        
        return true;
    }
	
	private boolean giveMoney(QuestPlayer player, double amount)
	{
		if(amount < 0.0)
			throw new IllegalArgumentException("Amount must be positive");
 
		OfflinePlayer entity = getServer().getOfflinePlayer(player.getModel().UUID);
 
		getLogger().info(String.format("Gave %s to %s.", economy.format(amount), entity.getName()));
	 
		return economy.depositPlayer(entity, amount).transactionSuccess();
	}
	
	private void summonRocket(Player entity)
	{
		FireworkEffect.Builder builder = FireworkEffect.builder();
        builder.with(FireworkEffect.Type.STAR);
        builder.withColor(Color.AQUA);
        builder.withFlicker();
        builder.withTrail();
		
		Location loc = entity.getEyeLocation();
        Firework firework = (Firework)loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffects(builder.build());
        int power = entity.isSneaking() ? 0 : 1;
        meta.setPower(power);
        firework.setFireworkMeta(meta);
	}
	
	@SuppressWarnings("unused")
	private void debug(Object o)
	{
		System.out.println(o);
	}
}