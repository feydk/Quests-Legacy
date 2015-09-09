package io.github.feydk.Quests.Db;

import io.github.feydk.Quests.PluginConfig;
import io.github.feydk.Quests.Quests;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Highscore
{
	public String name;
	public int count;
	public int streak;
	
	public static List<Highscore> getHighscore(int count)
	{
		String query = 
			"select p.name, p.streak, " +
			"(select count(id) from quest_player_quests q where q.player_id = p.id and q.series_id = ? and q.status = 2) as count " + 
			"from quest_players p " +
			"where series_id = ? " +
			"order by count desc, streak desc " +
			"limit 0, " + count;
		
		HashMap<Integer, Object> params = new HashMap<Integer, Object>();
		params.put(1, PluginConfig.SERIES_ID);
		params.put(2, PluginConfig.SERIES_ID);
		
		ResultSet rs = Quests.db.select(query, params);
		
		List<Highscore> list = new ArrayList<Highscore>();
		
		try
		{
			if(rs != null)
			{
				while(rs.next())
				{
					Highscore model = new Highscore();
					model.name = rs.getString("name");
					model.count = rs.getInt("count");
					model.streak = rs.getInt("streak");
					
					list.add(model);
				}
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
}