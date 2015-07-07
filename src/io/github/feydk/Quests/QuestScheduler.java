package io.github.feydk.Quests;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
		List<QuestPlayer> players = QuestPlayer.getPlayersWithExpiredQuests();
		
		if(players.size() > 0)
		{
			for(QuestPlayer p : players)
			{
				OfflinePlayer entity = plugin.getServer().getOfflinePlayer(p.getModel().UUID);
				
				if(p.getCurrentQuest().getPlayerQuestModel().Status == QuestStatus.Accepted)
				{
					int old_streak = p.getModel().Streak;
					
					p.resetStreak();
					p.getCurrentQuest().setIncomplete();
					p.getCurrentQuest().setProcessed();
					
					if(entity.isOnline())
					{
						String msg = " " + ChatColor.AQUA + "Aaawww! You didn't manage to complete your quest in time.";
						
						if(old_streak > 1)
							msg += " Your previous streak of " + ChatColor.YELLOW + old_streak + ChatColor.AQUA + " has come to an end.";
					
						msg += "\n A new quest will be created for you shortly..";
						
						((Player)entity).sendMessage(msg);
						
						p.giveRandomQuest();
						
						plugin.players.replace(p.getModel().UUID, p);
						
						plugin.notifyPlayerOfQuest((Player)entity, p.getCurrentQuest().getPlayerQuestModel().Status, 50);
					}
				}
				else
				{
					p.getCurrentQuest().setProcessed();
					
					if(entity.isOnline())
					{
						p.giveRandomQuest();
						
						plugin.players.replace(p.getModel().UUID, p);
						
						plugin.notifyPlayerOfQuest((Player)entity, p.getCurrentQuest().getPlayerQuestModel().Status, 50);
					}
				}
			}
		}
	}
}