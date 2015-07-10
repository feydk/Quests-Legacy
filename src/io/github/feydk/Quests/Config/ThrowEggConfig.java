package io.github.feydk.Quests.Config;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import io.github.feydk.Quests.BaseConfig;

public class ThrowEggConfig extends BaseConfig
{
	public boolean Throw;
	public boolean Hatch;
	
	public void parse(String json)
	{
		super.parse(json);
		
		JSONObject o = (JSONObject)JSONValue.parse(json);
		
		if(o.get("hatch") != null)
			Hatch = Boolean.parseBoolean(o.get("hatch").toString());
		else if(o.get("throw") != null)
			Throw = Boolean.parseBoolean(o.get("throw").toString());
	}
}