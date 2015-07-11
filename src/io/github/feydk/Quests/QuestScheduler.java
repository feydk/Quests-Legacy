package io.github.feydk.Quests;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

// The scheduler simply checks the db for expired quests and handles them. See comments in code.
public class QuestScheduler extends BukkitRunnable
{
	private QuestsPlugin plugin;

	public QuestScheduler(QuestsPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	public void start()
	{
		// Every ~ 1 minute should be fine. Don't need it to be super precise.
		runTaskTimer(plugin, 1, 1200);
	}
	
	public void stop()
	{
	}
	
	@Override
	public void run()
	{
		// Get a list of players with expired quests.
		List<QuestPlayer> players = QuestPlayer.getPlayersWithExpiredQuests();
		
		if(players.size() > 0)
		{
			for(QuestPlayer p : players)
			{
				OfflinePlayer entity = plugin.getServer().getOfflinePlayer(p.getModel().UUID);
				
				// If the quest status is Accepted, it means the player didn't complete it in time.
				if(p.getCurrentQuest().getPlayerQuestModel().Status == QuestStatus.Accepted)
				{
					int old_streak = p.getModel().Streak;
					
					// So therefore, reset the players streak.
					p.resetStreak();
					
					// Set the quest as incomplete.
					p.getCurrentQuest().setIncomplete();
					
					// Set the quest as processed.
					p.getCurrentQuest().setProcessed();
					
					// And if player is online, send him a message, create a new quest and send him a notification about that new quest.
					if(entity.isOnline())
					{
						String msg = " " + ChatColor.AQUA + "Aaawww! You didn't manage to complete your quest in time.";
						
						if(old_streak > 1)
							msg += " Your previous streak of " + ChatColor.YELLOW + old_streak + ChatColor.AQUA + " has come to an end.";
					
						msg += "\n A new quest will be created for you shortly..";
						
						if(!PluginConfig.SOFT_LAUNCH)
							((Player)entity).sendMessage(msg);
						
						p.giveRandomQuest();
						
						plugin.players.replace(p.getModel().UUID, p);
						
						if(!PluginConfig.SOFT_LAUNCH)
							plugin.notifyPlayerOfQuest((Player)entity, p.getCurrentQuest().getPlayerQuestModel().Status, 50);
					}
				}
				// If the quest has any other status..
				else
				{
					// Just set as processed.
					p.getCurrentQuest().setProcessed();
					
					// And if player is online, create a new quest and send him a notification about it.
					if(entity.isOnline())
					{
						p.giveRandomQuest();
						
						plugin.players.replace(p.getModel().UUID, p);
						
						if(!PluginConfig.SOFT_LAUNCH)
							plugin.notifyPlayerOfQuest((Player)entity, p.getCurrentQuest().getPlayerQuestModel().Status, 50);
					}
				}
			}
		}
	}
}