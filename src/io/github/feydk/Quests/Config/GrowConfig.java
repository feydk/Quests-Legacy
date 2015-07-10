package io.github.feydk.Quests.Config;

import io.github.feydk.Quests.BaseConfig;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.TreeType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class GrowConfig extends BaseConfig
{
	public List<TreeType> TreeTypes;
	
	public void parse(String json)
	{
		super.parse(json);
		
		JSONObject o = (JSONObject)JSONValue.parse(json);
		JSONArray treetypes = (JSONArray)o.get("treetypes");

		if(treetypes != null)
		{
			TreeTypes = new ArrayList<TreeType>();
			
			for(int i = 0; i < treetypes.size(); i++)
			{
				TreeTypes.add(TreeType.valueOf(((JSONObject)treetypes.get(i)).get("type").toString()));
			}
		}
	}
}