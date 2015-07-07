package io.github.feydk.Quests;

import io.github.feydk.Quests.Db.PlayerQuestModel;
import io.github.feydk.Quests.Db.QuestModel;

public class PlayerQuest
{
	private PlayerQuestModel model;
	private Quest quest;
	private QuestPlayer player;
	
	public PlayerQuest(PlayerQuestModel model)
	{
		this.model = model;
	}
	
	public PlayerQuestModel getPlayerQuestModel()
	{
		return model;
	}
	
	public Quest getQuest()
	{
		if(quest == null)
		{
			quest = Quest.getById(model.QuestId);
		}
		
		return quest;
	}
		
	public QuestModel getQuestModel()
	{
		return getQuest().getModel();
	}
	
	public QuestPlayer getPlayer()
	{
		if(player == null)
		{
			player = QuestPlayer.getById(model.PlayerId);
		}
		
		return player;
	}
	
	public static PlayerQuest getById(int id)
	{
		PlayerQuestModel model = PlayerQuestModel.loadById(id);
		
		return new PlayerQuest(model);
	}
	
	// Accept the quest.
	public boolean accept()
	{
		model.Status = QuestStatus.Accepted;
		
		return model.update();
	}
	
	// Cancel the quest.
	public boolean cancel()
	{
		model.Status = QuestStatus.Cancelled;
		
		return model.update();
	}
	
	// Complete the quest.
	public boolean complete()
	{
		model.Status = QuestStatus.Complete;
		
		return model.update();
	}
	
	public boolean updateProgress()
	{
		return model.update();
	}
	
	public boolean setIncomplete()
	{
		model.Status = QuestStatus.Incomplete;
		
		return model.update();
	}
	
	public boolean setProcessed()
	{
		model.Processed = 1;
		
		return model.update();
	}
	
	public String getStatus()
	{
		switch(model.Status)
		{
			case -1:
				return "Nonexistent";
			case 0:
				return "Created";
			case 1:
				return "Accepted";
			case 2:
				return "Complete";
			case 3:
				return "Cancelled";
			case 4:
				return "Incomplete";
			default:
				return "";
		}
	}
}
