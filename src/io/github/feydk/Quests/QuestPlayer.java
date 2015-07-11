package io.github.feydk.Quests;

import io.github.feydk.Quests.Db.PlayerModel;
import io.github.feydk.Quests.Db.PlayerQuestModel;
import io.github.feydk.Quests.Db.QuestModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class QuestPlayer
{
	private PlayerModel model;
	private PlayerQuest quest;
	
	public QuestPlayer(PlayerModel model)
	{
		this.model = model;
	}
	
	public static QuestPlayer getByUUID(UUID uuid)
	{
		PlayerModel model = PlayerModel.loadByUUID(uuid);
		
		return new QuestPlayer(model);
	}
	
	public static QuestPlayer getById(int id)
	{
		PlayerModel model = PlayerModel.loadById(id);
		
		return new QuestPlayer(model);
	}
	
	public static QuestPlayer create(UUID uuid, String name)
	{
		PlayerModel model = PlayerModel.create(uuid, name);
		
		return new QuestPlayer(model);
	}
	
	public PlayerModel getModel()
	{
		return model;
	}
	
	public PlayerQuest getCurrentQuest()
	{
		if(quest == null)
		{
			quest = PlayerQuest.getById(getModel().CurrentQuestId);
		}
		
		return quest;
	}
	
	public void giveRandomQuest()
	{
		QuestModel model = QuestModel.loadRandom(this);
		
		quest = new PlayerQuest(PlayerQuestModel.create(this.model, model));
		
		this.model.CurrentQuestId = quest.getPlayerQuestModel().Id;
		this.model.update();
	}
	
	public void giveSpecificQuest(int id)
	{
		QuestModel model = QuestModel.loadById(id);
		
		quest = new PlayerQuest(PlayerQuestModel.create(this.model, model));
		
		this.model.CurrentQuestId = quest.getPlayerQuestModel().Id;
		this.model.update();
	}
	
	public static List<QuestPlayer> getPlayersWithExpiredQuests()
	{
		List<PlayerModel> models = PlayerModel.loadPlayersWithExpiredQuests();
		List<QuestPlayer> list = new ArrayList<QuestPlayer>();
		
		if(models != null && models.size() > 0)
		{
			for(PlayerModel m : models)
			{
				QuestPlayer obj = new QuestPlayer(m);
				list.add(obj);
			}
		}
		
		return list;
	}
	
	// Increment player streak.
	public boolean incrementStreak()
	{
		model.Streak++;
		
		return model.update();
	}
	
	// Increment player tier.
	private boolean incrementTier()
	{
		model.Tier++;
		
		return model.update();
	}
	
	// Increment player tier if player qualifies.
	public boolean maybeIncrementTier()
	{		
		int total_count = Quests.getNumberOfQuestsInTier(model.Tier);
		
		String query = "select count(id) from quest_player_quests where player_id = ? and quest_id in (select id from quest_quests where active = 1 and series_id = ? and tier = ?) and status = ? and cycle = ?";
		
		HashMap<Integer, Object> params = new HashMap<Integer, Object>();
		params = new HashMap<Integer, Object>();
		params.put(1, model.Id);
		params.put(2, model.SeriesId);
		params.put(3, model.Tier);
		params.put(4, QuestStatus.Complete);
		params.put(5, model.Cycle);
		
		int complete_count = Quests.db.getInt(query, params);	// Number of completed quests in current tier and cycle.

		if(total_count > 0 && complete_count > -1 && total_count <= complete_count)
		{
			return incrementTier();
		}
		
		return false;
	}
	
	// Increment player cycle if player qualifies.
	public boolean maybeIncrementCycle()
	{
		int total_count = Quests.getMaxNumberOfTiers();
		
		if(model.Tier > total_count)
		{
			model.Tier = 1;
			model.Cycle++;
			
			return model.update();
		}
		
		return false;
	}
	
	public double calcStreakBonus()
	{
		if(model.Streak == 0)
			return 0;
		
		double multiplier = 0;
		int streak = model.Streak + 1;
		
		if(streak >= PluginConfig.STREAK_BONUS_CAP)
			multiplier = PluginConfig.STREAK_BONUS_CAP;
		else
			multiplier = PluginConfig.STREAK_BONUS_INCREMENT * streak;
		
		return (multiplier / 100.0) * getCurrentQuest().getQuestModel().Reward;
	}
	
	public double calcCycleBonus()
	{
		if(model.Cycle == 1)
			return 0;
		
		return (double)getCurrentQuest().getQuestModel().Reward * (model.Cycle * PluginConfig.CYCLE_MULTIPLIER);
	}
	
	// Reset everything!
	public void reset()
	{
		String query = "delete from quest_player_quests where series_id = ? and player_id = ?";
		
		HashMap<Integer, Object> params = new HashMap<Integer, Object>();
		params.put(1, model.SeriesId);
		params.put(2, model.Id);
		
		Quests.db.update(query, params);
		
		query = "update quest_players set tier = 1, streak = 0, cycle = 1, current_quest_id = 0 where series_id = ? and id = ?";
		
		Quests.db.update(query, params);
	}
	
	public boolean resetStreak()
	{
		model.Streak = 0;
		
		return model.update();
	}
	
	// For stats.
	public int getTotalQuests(int status)
	{
		String query = "select count(id) from quest_player_quests where player_id = ? and series_id = ?";
		
		if(status > -1)
			query += " and status = ?";
		
		HashMap<Integer, Object> params = new HashMap<Integer, Object>();
		params.put(1, model.Id);
		params.put(2, model.SeriesId);
		
		if(status > -1)
			params.put(3, status);
		
		int count = Quests.db.getInt(query, params);
		
		return count;
	}
	
	// For stats.
	public int getTotalQuests(int status, int cycle)
	{
		String query = "select count(id) from quest_player_quests where player_id = ? and series_id = ? and cycle = ?";
		
		if(status > -1)
			query += " and status = ?";
		
		HashMap<Integer, Object> params = new HashMap<Integer, Object>();
		params.put(1, model.Id);
		params.put(2, model.SeriesId);
		params.put(3, cycle);
		
		if(status > -1)
			params.put(4, status);
		
		int count = Quests.db.getInt(query, params);
		
		return count;
	}
	
	// For stats.
	public double getTotalRewards(String type)
	{
		String query = "select sum(" + type + ") from quest_player_quests where player_id = ? and series_id = ? and status = 2";
				
		HashMap<Integer, Object> params = new HashMap<Integer, Object>();
		params.put(1, model.Id);
		params.put(2, model.SeriesId);
		
		double sum = Quests.db.getDouble(query, params);
		
		return sum;
	}
}