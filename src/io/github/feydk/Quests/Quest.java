package io.github.feydk.Quests;

import io.github.feydk.Quests.Config.*;
import io.github.feydk.Quests.Db.QuestModel;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Quest
{
	private QuestModel model;
	private BaseConfig config;
	private List<QuestReward> rewards;
	
	public Quest(QuestModel model)
	{
		this.model = model;
	}
	
	public QuestModel getModel()
	{
		return model;
	}
	
	public BaseConfig getConfig()
	{
		if(config == null)
		{
			BaseConfig c = null;
			
			switch(getQuestType())
			{
				case Craft:
					c = new CraftConfig();
					break;
				case TakeDamage:
					c = new DamageConfig();
					break;
				case Eat:
					c = new EatConfig();
					break;
				case Enchant:
					c = new EnchantConfig();
					break;
				case Fish:
					c = new FishConfig();
					break;
				case Grow:
					c = new GrowConfig();
					break;
				case Kill:
					c = new KillConfig();
					break;
				case Mine:
					c = new MineConfig();
					break;
				case Smelt:
					c = new SmeltConfig();
					break;
				case Tame:
					c = new TameConfig();
					break;
				// Trading has no special config.
				default:
					c = new BaseConfig();
					break;
			}
			
			config = c;
			config.parse(model.Config);
		}
		
		return config;
	}
	
	public List<QuestReward> getRewards()
	{
		if(rewards == null)
		{
			if(model.Commands != null && !model.Commands.isEmpty())
			{
				JSONObject o = (JSONObject)JSONValue.parse(model.Commands);
				
				if(o != null)
				{	
					rewards = new ArrayList<QuestReward>();
					JSONArray list = (JSONArray)o.get("rewards");
					
					if(list != null)
					{
						for(int i = 0; i < list.size(); i++)
						{
							JSONObject r = (JSONObject)list.get(i);
							
							QuestReward reward = new QuestReward();
							reward.Text = r.get("text").toString();
							reward.Command = r.get("command").toString();
							rewards.add(reward);
						}
					}
				}
			}
		}
		
		return rewards;
	}
	
	public QuestType getQuestType()
	{
		switch(model.Type)
		{
			case "craft":
				return QuestType.Craft;
			case "take_damage":
				return QuestType.TakeDamage;
			case "eat":
				return QuestType.Eat;
			case "enchant":
				return QuestType.Enchant;
			case "fish":
				return QuestType.Fish;
			case "grow":
				return QuestType.Grow;
			case "kill":
				return QuestType.Kill;
			case "mine":
				return QuestType.Mine;
			case "smelt":
				return QuestType.Smelt;
			case "tame":
				return QuestType.Tame;
			case "trade":
				return QuestType.Trade;
		}
		
		return QuestType.Unknown;
	}
	
	public static Quest getById(int id)
	{
		QuestModel model = QuestModel.loadById(id);
		
		return new Quest(model);
	}
}